package ui;

import Game.Game;
import Game.Phase;
import Player.Player;
import Card.Card;
import Card.MonsterCard;
import Card.SpellCard;
import Card.TrapCard;
import Deck.Deck;
import loader.CardLoader;
import util.MusicPlayer;
import Bot.SimpleBot;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Bot.SimpleBot;
import javafx.stage.Modality;
import javafx.animation.PauseTransition;

/**
 * Main game board UI for Yu-Gi-Oh! duels.
 * <p>
 * This class manages the complete duel interface, including both players' boards,
 * hands, life points, and all game interactions. It serves as the main game screen
 * where players conduct their duels, following the official Yu-Gi-Oh! field layout
 * and game mechanics.
 * </p>
 * <p>
 * The DuelBoard extends {@link Application} to integrate with the JavaFX application
 * lifecycle and provides a comprehensive visual representation of the game state,
 * including:
 * <ul>
 * <li>Monster zones (5 per player)</li>
 * <li>Spell/Trap zones (5 per player)</li>
 * <li>Field spell zones</li>
 * <li>Graveyard and Banished zones</li>
 * <li>Deck and Extra Deck indicators</li>
 * <li>Player hands with card visuals</li>
 * <li>Life point displays and tracking</li>
 * <li>Phase indicator and control buttons</li>
 * <li>Card information display area</li>
 * <li>Action zone for gameplay controls</li>
 * </ul>
 * </p>
 * <p>
 * The board supports various game actions such as:
 * <ul>
 * <li>Summoning monsters in Attack or Defense Position</li>
 * <li>Setting cards face-down</li>
 * <li>Activating Spell and Trap cards</li>
 * <li>Declaring attacks and calculating battle damage</li>
 * <li>Changing monster positions</li>
 * <li>Phase progression and turn management</li>
 * <li>Bot opponent AI integration (optional)</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Card Selection Mechanics:</strong>
 * Players interact with cards by clicking on them. The selected card is highlighted
 * and stored in {@code selectedCard} and {@code selectedView}. Actions are then
 * performed on the selected card through the action zone buttons.
 * </p>
 * <p>
 * <strong>Battle System:</strong>
 * When "Attack Mode" is activated, players can select an attacking monster, then
 * click on an opponent's monster or attack directly. Battle calculations follow
 * official Yu-Gi-Oh! rules with proper damage calculation.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class DuelBoard extends Application {

    /** The main game controller managing turns, phases, and win conditions */
    private Game game;
    
    /** Player 1's main deck */
    private Deck deckJ1;
    
    /** Player 2's main deck (or bot's deck) */
    private Deck deckJ2;
    
    /** Visual container for Player 1's hand cards */
    private HBox handJ1;
    
    /** Visual container for Player 2's hand cards (hidden from Player 1 in PvP) */
    private HBox handJ2;
    
    /** The currently selected card's visual representation */
    private ImageView selectedView = null;
    
    /** The currently selected Card object for actions */
    private Card selectedCard = null;
    
    /** Text area displaying detailed information about the selected card */
    private TextArea cardInfoArea;
    
    /** Container for action buttons (Summon, Set, Activate, etc.) */
    private VBox actionZone;
    
    /** Label displaying the current turn phase */
    private Label phaseLabel;
    
    /** Flag indicating whether attack mode is currently active */
    private boolean attackMode = false;
    
    /** The monster card selected to perform an attack */
    private MonsterCard attackerCard = null;
    
    /** Visual representation of the attacking monster */
    private ImageView attackerView = null;
    
    /** GridPane representing Player 1's field (bottom of screen) */
    private GridPane boardJoueur1;
    
    /** GridPane representing the opponent's field (top of screen) */
    private GridPane opponentBoard;

    // Life Points Management
    
    /** Array holding Player 1's current life points (wrapped in array for lambda access) */
    private int[] lp1 = {8000};
    
    /** Array holding Player 2's current life points (wrapped in array for lambda access) */
    private int[] lp2 = {8000};
    
    /** Label displaying Player 1's current life points */
    private Label lpJoueur1;
    
    /** Label displaying Player 2's current life points */
    private Label lpJoueur2;
    
    /** Player 1 object managing their game state */
    private Player joueur1;
    
    /** Player 2 object managing their game state */
    private Player joueur2;

    // Bot Integration
    
    /** AI bot controller for single-player mode */
    private SimpleBot bot;
    
    /** Flag to enable/disable bot opponent (true for PvE, false for PvP) */
    private boolean isBotEnabled = false;

    /**
     * Constructs and returns a GridPane representing a player's game board.
     * <p>
     * This method builds the visual layout for either the player's board (bottom)
     * or the opponent's board (top). The layout follows the official Yu-Gi-Oh!
     * field structure with proper zone placement and styling.
     * </p>
     * <p>
     * <strong>Board Layout:</strong>
     * <ul>
     * <li><strong>Player Board (isOpponent=false):</strong>
     *     <ul>
     *     <li>Row 0: 5 Monster Zones (center), Field Zone (left), Graveyard (right)</li>
     *     <li>Row 1: 5 Spell/Trap Zones (center), Extra Deck (left), Deck (right)</li>
     *     </ul>
     * </li>
     * <li><strong>Opponent Board (isOpponent=true):</strong>
     *     <ul>
     *     <li>Row 0: 5 Spell/Trap Zones (center), Extra Deck (left), Deck (right)</li>
     *     <li>Row 1: 5 Monster Zones (center), Field Zone (right), Graveyard+Banished (left)</li>
     *     </ul>
     * </li>
     * </ul>
     * </p>
     * <p>
     * Each zone is created as a StackPane with specific dimensions (90x138 pixels)
     * and CSS styling classes for visual customization. The deck zone includes a
     * card back image and a counter label showing remaining cards.
     * </p>
     * <p>
     * <strong>Zone Types:</strong>
     * <ul>
     * <li>Monster Zones: Where monster cards are summoned</li>
     * <li>Spell/Trap Zones: Where spell and trap cards are set or activated</li>
     * <li>Field Zone: For field spell cards (one per player)</li>
     * <li>Graveyard: Displays discarded/destroyed cards</li>
     * <li>Banished Zone: Displays removed from play cards</li>
     * <li>Deck Zone: Shows deck back and card count</li>
     * <li>Extra Deck: For Fusion, Synchro, Xyz, and Link monsters</li>
     * </ul>
     * </p>
     * <p>
     * If the deck back image ({@code /ui/card_back.png}) cannot be loaded, a
     * fallback text label is displayed instead.
     * </p>
     *
     * @param prefix a string identifier for the player (e.g., "J1", "J2") used in zone labels
     * @param isOpponent true if building the opponent's board (top), false for player's board (bottom)
     * @return a configured GridPane representing the complete game board for one player
     */
    public GridPane buildPlayerBoard(String prefix, boolean isOpponent) {
        GridPane board = new GridPane();
        board.setHgap(8);
        board.setVgap(12);
        board.setAlignment(Pos.CENTER);

        // Field Spell Zone
        StackPane terrain = new StackPane();
        terrain.setPrefSize(90, 138);
        terrain.getStyleClass().add("terrain-zone");

        // Graveyard Zone
        StackPane graveyard = new StackPane();
        graveyard.setPrefSize(90, 138);
        graveyard.getStyleClass().add("graveyard-zone");

        // Banished Zone
        Label banished = new Label("Banished " + prefix);
        banished.setPrefSize(90, 138);
        banished.getStyleClass().add("banished-zone");

        // Extra Deck Zone
        Label extraDeck = new Label("Extra Deck " + prefix);
        extraDeck.setPrefSize(90, 138);
        extraDeck.getStyleClass().add("extra-zone");

        // Deck Zone with card back image and counter
        StackPane deckZone = new StackPane();
        deckZone.setPrefSize(90, 138);
        deckZone.getStyleClass().add("deck-zone");

        try {
            // Load deck back image
            ImageView deckBack = new ImageView(
                new Image(getClass().getResource("/ui/card_back.png").toExternalForm())
            );
            deckBack.setFitWidth(88);
            deckBack.setFitHeight(132);
            deckZone.getChildren().add(deckBack);
        } catch (Exception ex) {
            // Fallback if image not found
            Label deckLabel = new Label("Deck " + prefix);
            deckLabel.getStyleClass().add("deck-fallback");
            deckZone.getChildren().add(deckLabel);
        }

        // Deck card counter (will be updated from start method)
        Label deckCount = new Label("Cards: 0");
        deckCount.getStyleClass().add("deck-count");
        StackPane.setAlignment(deckCount, Pos.BOTTOM_CENTER);
        deckZone.getChildren().add(deckCount);

        if (isOpponent) {
            // Opponent board layout (top of screen)
            
            // Row 0: Spell/Trap zones
            for (int i = 0; i < 5; i++) {
                StackPane spellTrapZone = new StackPane();
                spellTrapZone.setPrefSize(90, 138);
                spellTrapZone.getStyleClass().add("spelltrap-zone");
                board.add(spellTrapZone, i+1, 0);
            }
            
            // Row 1: Monster zones
            for (int i = 0; i < 5; i++) {
                StackPane monsterZone = new StackPane();
                monsterZone.setPrefSize(90, 138);
                monsterZone.getStyleClass().add("monster-zone");
                board.add(monsterZone, i+1, 1);
            }
            
            // Additional zones for opponent
            HBox graveyardZone = new HBox(8, banished, graveyard);
            board.add(extraDeck, 0, 0);
            board.add(deckZone, 6, 0);
            board.add(terrain, 6, 1);
            board.add(graveyardZone, 0, 1);

        } else {
            // Player board layout (bottom of screen)
            
            // Row 0: Monster zones
            for (int i = 0; i < 5; i++) {
                StackPane monsterZone = new StackPane();
                monsterZone.setPrefSize(90, 138);
                monsterZone.getStyleClass().add("monster-zone");
                board.add(monsterZone, i+1, 0);
            }
            
            // Row 1: Spell/Trap zones
            for (int i = 0; i < 5; i++) {
                StackPane spellTrapZone = new StackPane();
                spellTrapZone.setPrefSize(90, 138);
                spellTrapZone.getStyleClass().add("spelltrap-zone");
                board.add(spellTrapZone, i+1, 1);
            }

            // Additional zones for player
            board.add(terrain, 0, 0);
            board.add(extraDeck, 0, 1);
            board.add(graveyard, 6, 0);
            board.add(banished, 7, 0);
            board.add(deckZone, 6, 1);
        }

        return board;
    }
    
        /**
     * Builds a visual representation of a player's hand from a list of cards.
     * <p>
     * This method creates an HBox container displaying all cards in the player's hand.
     * For the current player, cards are shown face-up with their actual images. For the
     * opponent, cards are displayed as card backs (rotated 180°) to hide their identity.
     * </p>
     * <p>
     * Each card in the hand is clickable and performs two actions when clicked:
     * <ul>
     * <li>Selects the card (highlights it and displays its information)</li>
     * <li>Generates appropriate action buttons based on the card type</li>
     * </ul>
     * </p>
     * <p>
     * The card images are scaled to 88x132 pixels for consistent display. If a card
     * image fails to load, an error message is logged but the hand continues to build
     * with the remaining cards.
     * </p>
     * <p>
     * <strong>Card Selection Behavior:</strong>
     * When a card is clicked, it becomes the {@code selectedCard} and action buttons
     * appear in the action zone (Summon, Set, Activate, etc., depending on card type
     * and game phase).
     * </p>
     *
     * @param cards the list of Card objects to display in the hand
     * @param boardTarget the GridPane representing the target board for placing cards
     * @param isOpponent true if this is the opponent's hand (shows card backs), false for player's hand
     * @param cardInfoArea the TextArea where card information will be displayed when selected
     * @param actionZone the VBox where action buttons will be generated for the selected card
     * @return an HBox containing ImageViews of all cards in the hand
     */
    public HBox buildPlayerHandFromCards(List<Card> cards,
                                        GridPane boardTarget,
                                        boolean isOpponent,
                                        TextArea cardInfoArea,
                                        VBox actionZone) 
    {
        HBox handBox = new HBox(10);
        handBox.setAlignment(Pos.CENTER);
        handBox.getStyleClass().add("hand-zone");

        for (Card card : cards) {
            try {
                ImageView view;

                if (isOpponent) {
                    // Display card back for opponent's cards
                    Image backImage = new Image(getClass().getResource("/ui/card_back.png").toExternalForm());
                    view = new ImageView(backImage);
                    view.setRotate(180);
                    view.getProperties().put("card", card); // Store card reference
                } else {
                    // Display actual card for the player
                    view = new ImageView(card.getImage());
                    view.getProperties().put("card", card); // Store card reference
                }

                view.setFitWidth(88);
                view.setFitHeight(132);

                // Card click handler: select card and create action buttons
                view.setOnMouseClicked(e -> {
                    // Update selection (yellow border + card info)
                    selectCard(view, boardTarget, handBox, cardInfoArea, card);

                    // Build appropriate action buttons based on card type
                    createActionButtonsForCard(card, view, boardTarget, handBox, cardInfoArea, actionZone, isOpponent);
                });

                handBox.getChildren().add(view);

            } catch (Exception ex) {
                System.err.println("Error displaying card: " + card.getName() + " → " + ex.getMessage());
            }
        }

        return handBox;
    }

    /**
     * Places a card from the hand onto the game board in the appropriate zone.
     * <p>
     * This method handles the placement logic for all card types, finding an available
     * zone on the target board and moving the card from the hand to that zone. It
     * supports special handling for Field Spell cards and normal placement for monsters
     * and regular spell/trap cards.
     * </p>
     * <p>
     * <strong>Placement Logic:</strong>
     * <ul>
     * <li><strong>Field Spell Cards:</strong> Placed in the dedicated field zone. Only one
     *     field spell can be active at a time, so any existing field spell is replaced.</li>
     * <li><strong>Monster Cards:</strong> Placed in the first available monster zone (columns 1-5,
     *     row 0 for player, row 1 for opponent). Rotation depends on position (ATK/DEF) and
     *     face-up/face-down status.</li>
     * <li><strong>Spell/Trap Cards:</strong> Placed in the first available spell/trap zone
     *     (columns 1-5, row 1 for player, row 0 for opponent).</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Card Rotation:</strong>
     * <ul>
     * <li>Face-up Attack Position: 0° (player) or 180° (opponent)</li>
     * <li>Face-down Defense Position (Monster): 90° (player) or 270° (opponent)</li>
     * <li>Face-down Spell/Trap: 0° (upright)</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Click Behavior on Placed Cards:</strong>
     * When a placed card is clicked, two modes are possible:
     * <ul>
     * <li><strong>Attack Mode:</strong> If attack mode is active and an attacker is selected,
     *     clicking an opponent's monster resolves battle between them.</li>
     * <li><strong>Normal Mode:</strong> Selects the card and generates action buttons
     *     (Change Position, Activate Effect, etc.).</li>
     * </ul>
     * </p>
     * <p>
     * The card is removed from the hand after successful placement, and all relevant
     * properties (faceDown status, position, card reference) are stored in both the
     * zone and the ImageView for future reference.
     * </p>
     *
     * @param boardTarget the GridPane representing the game board where the card will be placed
     * @param handBox the HBox containing the player's hand (card will be removed from here)
     * @param view the ImageView representing the card to place
     * @param card the Card object being placed
     * @param targetRow the row index where the card should be placed (0 for monsters, 1 for spell/traps)
     * @param isOpponent true if placing on opponent's side (affects rotation), false for player's side
     * @param cardInfoArea the TextArea for displaying placement messages and card info
     * @param actionZone the VBox where action buttons will be generated when the placed card is clicked
     */
    public void placeCardInZone(GridPane boardTarget,
                                HBox handBox,
                                ImageView view,
                                Card card,
                                int targetRow,
                                boolean isOpponent,
                                TextArea cardInfoArea,
                                VBox actionZone)
    {
        // === Special Case: Field Spell Cards ===
        if (card instanceof SpellCard) {
            SpellCard spell = (SpellCard) card;
            if ("Field".equals(spell.getSpellType())) {
                Node fieldCell = boardTarget.getChildren().stream()
                    .filter(node -> node.getStyleClass().contains("terrain-zone"))
                    .findFirst()
                    .orElse(null);

                if (fieldCell instanceof StackPane) {
                    StackPane fieldZone = (StackPane) fieldCell;

                    ImageView placed = view;
                    placed.setFitWidth(88);
                    placed.setFitHeight(132);
                    placed.setRotate(isOpponent ? 180 : 0);

                    // Click handler for placed field spell → specific field actions
                    placed.setOnMouseClicked(e -> {
                        selectCard(placed, boardTarget, handBox, cardInfoArea, card);
                        createActionButtonsForPlacedCard(card, placed, boardTarget, handBox, cardInfoArea, actionZone, isOpponent);
                    });

                    // Only one Field Spell at a time
                    fieldZone.getChildren().clear();
                    fieldZone.getChildren().add(placed);

                    // Remove card from hand
                    handBox.getChildren().remove(view);

                    // Store state properties
                    fieldZone.getProperties().put("card", card);
                    placed.getProperties().put("placedCell", fieldZone);
                    placed.getProperties().put("faceDown", false);
                    placed.getProperties().put("position", "ATK");
                    placed.getProperties().put("isOpponent", isOpponent);

                    cardInfoArea.setText("Field Spell placed: " + card.getName());
                    return; // Exit, no need to check columns
                }
            }
        }

        // === Normal Case: Monsters and Regular Spell/Trap Cards ===
        for (int col = 1; col <= 5; col++) {
            final int currentCol = col;

            Node cell = boardTarget.getChildren().stream()
                .filter(node -> GridPane.getRowIndex(node) == targetRow &&
                                GridPane.getColumnIndex(node) == currentCol)
                .findFirst()
                .orElse(null);

            if (cell instanceof StackPane) {
                StackPane zone = (StackPane) cell;
                if (zone.getChildren().isEmpty()) {
                    ImageView placed = view;
                    placed.setFitWidth(88);
                    placed.setFitHeight(132);

                    // Retrieve placement properties
                    boolean faceDown = (boolean) placed.getProperties().getOrDefault("faceDown", false);
                    String position = (String) placed.getProperties().getOrDefault("position", faceDown ? "DEF" : "ATK");

                    // Apply rotation based on face-down status and position
                    if (faceDown) {
                        if (card instanceof MonsterCard) {
                            placed.setRotate(isOpponent ? 270 : 90); // Horizontal for face-down defense
                        } else {
                            placed.setRotate(0); // Upright for face-down spell/trap
                        }
                    } else {
                        placed.setRotate(isOpponent ? 180 : 0); // Normal orientation
                    }

                    // Click handler with attack mode detection
                    placed.setOnMouseClicked(e -> {
                        // Check if in attack mode first
                        if (attackMode && attackerCard != null && attackerView != null) {
                            // Clicked card is an opponent's monster = attack target
                            if (card instanceof MonsterCard && isOpponent) {
                                resolveBattle(attackerCard, attackerView, (MonsterCard) card, placed, isOpponent);
                                
                                // Reset attack mode
                                attackMode = false;
                                attackerCard = null;
                                attackerView = null;
                                
                                // Clear action zone
                                actionZone.getChildren().clear();
                                cardInfoArea.setText("Battle resolved!");
                            } else {
                                cardInfoArea.setText("You can only attack an opponent's monster.");
                            }
                        } else {
                            // Normal mode: selection + action display
                            selectCard(placed, boardTarget, handBox, cardInfoArea, card);
                            createActionButtonsForPlacedCard(card, placed, boardTarget, handBox, cardInfoArea, actionZone, isOpponent);
                        }
                    });

                    // Store card reference (CRITICAL)
                    placed.getProperties().put("card", card);

                    zone.getChildren().add(placed);
                    handBox.getChildren().remove(view);

                    // Store properties in zone
                    zone.getProperties().put("card", card);
                    zone.getProperties().put("faceDown", faceDown);
                    zone.getProperties().put("position", position);

                    // Store properties in placed view
                    placed.getProperties().put("placedCell", zone);
                    placed.getProperties().put("faceDown", faceDown);
                    placed.getProperties().put("position", position);
                    placed.getProperties().put("isOpponent", isOpponent);

                    break;
                }
            }
        }
    }

    /**
     * Selects a card and updates the UI to reflect the selection.
     * <p>
     * This method manages the visual feedback and state updates when a player
     * selects a card either from their hand or from the field. It performs four
     * key operations:
     * </p>
     * <ol>
     * <li><strong>Remove previous selection:</strong> Clears the "card-selected"
     *     CSS class from all cards in the hand and on the board</li>
     * <li><strong>Add new selection:</strong> Applies the "card-selected" CSS class
     *     to the clicked card (typically adds a yellow border)</li>
     * <li><strong>Update card info area:</strong> Displays the card's details in the
     *     information panel, with special handling for face-down cards</li>
     * <li><strong>Update global state:</strong> Sets {@code selectedView} and
     *     {@code selectedCard} for use by action buttons</li>
     * </ol>
     * <p>
     * <strong>Face-Down Card Handling:</strong>
     * <ul>
     * <li><strong>Opponent's face-down cards:</strong> Shows only "Opponent's card (face-down)"
     *     to prevent information leaking</li>
     * <li><strong>Player's face-down cards:</strong> Shows full card information with
     *     "[Face-down]" indicator, allowing the player to see their own set cards</li>
     * <li><strong>Face-up cards:</strong> Shows complete card information normally</li>
     * </ul>
     * </p>
     *
     * @param selectedView the ImageView of the card being selected
     * @param boardTarget the GridPane representing the game board (for clearing selections)
     * @param handBox the HBox containing the player's hand (for clearing selections)
     * @param cardInfoArea the TextArea where card information will be displayed
     * @param card the Card object being selected
     */
    public void selectCard(ImageView selectedView,
                            GridPane boardTarget,
                            HBox handBox,
                            TextArea cardInfoArea,
                            Card card) 
    {
        // 1. Remove previous selection highlights
        handBox.getChildren().forEach(node -> node.getStyleClass().remove("card-selected"));
        boardTarget.getChildren().forEach(node -> {
            if (node instanceof StackPane) {
                ((StackPane) node).getChildren().forEach(child -> child.getStyleClass().remove("card-selected"));
            }
        });

        // 2. Add selection highlight to clicked card
        selectedView.getStyleClass().add("card-selected");

        // 3. Update card information area with appropriate visibility rules
        boolean faceDown = (boolean) selectedView.getProperties().getOrDefault("faceDown", false);
        boolean isOpponent = (boolean) selectedView.getProperties().getOrDefault("isOpponent", false);
        
        if (isOpponent && faceDown) {
            // Opponent's face-down card: NEVER show information
            cardInfoArea.setText("Opponent's card (face-down)");
        } else if (faceDown) {
            // Player's face-down card: SHOW information with indicator
            cardInfoArea.setText(card.toString() + "\n[Face-down]");
        } else {
            // Face-up card: display information normally
            cardInfoArea.setText(card.toString());
        }

        // 4. Update global selection state
        this.selectedView = selectedView;
        this.selectedCard = card;
    }

        /**
     * Creates and displays action buttons for a card selected from the player's hand.
     * <p>
     * This method dynamically generates appropriate action buttons based on the type
     * of card selected. The buttons appear in the action zone and allow the player to
     * perform legal actions with the selected card according to Yu-Gi-Oh! rules.
     * </p>
     * <p>
     * <strong>Monster Card Actions:</strong>
     * <ul>
     * <li><strong>Normal Summon:</strong> Places the monster face-up in Attack Position.
     *     The card is displayed with its actual image and properly rotated for opponent's side.
     *     After summoning, the card is removed from hand and placed in the first available
     *     monster zone.</li>
     * <li><strong>Set:</strong> Places the monster face-down in Defense Position (horizontal).
     *     Uses the card back image rotated 90° to indicate face-down defense. The actual
     *     card data is stored in properties for later flip effects.</li>
     * <li><strong>Send to Graveyard:</strong> Discards the monster directly to the graveyard
     *     without playing it. This can be used for card costs or voluntary discards.</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Spell/Trap Card Actions:</strong>
     * <ul>
     * <li><strong>Set:</strong> Places the spell or trap card face-down in a spell/trap zone.
     *     The card can be activated later (traps must wait at least one turn). Uses the
     *     card back image in upright position.</li>
     * <li><strong>Activate:</strong> Immediately activates the spell/trap card face-up.
     *     The card is placed in a spell/trap zone and its effect is applied via
     *     {@link #applyCardEffect(Card, GridPane, HBox, boolean)}. Spell cards can be
     *     activated from hand, but this doesn't enforce timing restrictions.</li>
     * <li><strong>Send to Graveyard:</strong> Discards the spell/trap directly to the
     *     graveyard without activation.</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Common Behavior:</strong>
     * <ul>
     * <li>All actions automatically remove the card from the hand after execution</li>
     * <li>The action zone is cleared after each action completes</li>
     * <li>Card properties (faceDown, position, card reference) are stored for later interactions</li>
     * <li>Cards sent to graveyard are displayed as smaller images (60x90) in the graveyard zone</li>
     * <li>Proper rotation is applied based on which player owns the card (180° for opponent)</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Note on Game Rules:</strong>
     * This method does not enforce all official Yu-Gi-Oh! rules such as:
     * <ul>
     * <li>Tribute requirements for high-level monsters (5+ stars)</li>
     * <li>One Normal Summon per turn restriction</li>
     * <li>Main Phase timing for spell activation</li>
     * <li>Trap cards must be set for one turn before activation</li>
     * </ul>
     * These rules should be enforced by the game controller or higher-level logic.
     * </p>
     *
     * @param card the Card object for which to create action buttons
     * @param view the ImageView representing the card in the hand
     * @param boardTarget the GridPane where the card will be placed when an action is performed
     * @param handBox the HBox containing the player's hand (card will be removed from here)
     * @param cardInfoArea the TextArea for displaying action results and card information
     * @param actionZone the VBox where the action buttons will be displayed
     * @param isOpponent true if the card belongs to the opponent (affects rotation and zone targeting)
     */
    public void createActionButtonsForCard(Card card,
                                            ImageView view,
                                            GridPane boardTarget,
                                            HBox handBox,
                                            TextArea cardInfoArea,
                                            VBox actionZone,
                                            boolean isOpponent) 
    {
        // Clear existing action buttons
        actionZone.getChildren().clear();

        // Display selected card name
        Label selected = new Label("Selected card: " + card.getName());
        actionZone.getChildren().add(selected);

        // Differentiate by card type
        if (card instanceof MonsterCard) {
            Button summonBtn = new Button("Normal Summon");
            Button setBtn = new Button("Set");
            Button graveyardBtn = new Button("Send to Graveyard");

            // Normal Summon (face-up Attack Position)
            summonBtn.setOnAction(ev -> {
                int targetRow = isOpponent ? 1 : 0; // Monster row

                // Create new ImageView with actual card image
                ImageView visible = new ImageView(card.getImage());
                visible.setFitWidth(88);
                visible.setFitHeight(132);
                visible.getProperties().put("card", card);

                // Rotate if opponent's card (facing down the board)
                if (isOpponent) {
                    visible.setRotate(180);
                }

                // Remove from hand and place on field
                handBox.getChildren().remove(view);
                placeCardInZone(boardTarget, handBox, visible, card, targetRow, isOpponent, cardInfoArea, actionZone);

                // Clear action zone after playing the card
                actionZone.getChildren().clear();
            });

            // Set (face-down Defense Position)
            setBtn.setOnAction(ev -> {
                int targetRow = isOpponent ? 1 : 0; // Monster row
                try {
                    Image backImage = new Image(getClass().getResource("/ui/card_back.png").toExternalForm());
                    ImageView hidden = new ImageView(backImage);
                    hidden.setFitWidth(88);
                    hidden.setFitHeight(132);
                    hidden.setRotate(90); // Horizontal for face-down defense
                    hidden.getProperties().put("faceDown", true);
                    hidden.getProperties().put("position", "DEF");

                    handBox.getChildren().remove(view);
                    placeCardInZone(boardTarget, handBox, hidden, card, targetRow, isOpponent, cardInfoArea, actionZone);

                    // Clear action zone after playing the card
                    actionZone.getChildren().clear();

                } catch (Exception ex) {
                    System.err.println("Error loading card back: " + ex.getMessage());
                }
            });

            // Send to Graveyard
            graveyardBtn.setOnAction(ev -> {
                handBox.getChildren().remove(view);
                Object origin = view.getProperties().get("placedCell");
                if (origin instanceof StackPane) {
                    ((StackPane) origin).getChildren().remove(view);
                }
                Node graveyardCell = boardTarget.getChildren().stream()
                    .filter(node -> node.getStyleClass().contains("graveyard-zone"))
                    .findFirst()
                    .orElse(null);
                if (graveyardCell instanceof StackPane) {
                    StackPane graveyardZone = (StackPane) graveyardCell;
                    // Create smaller image for graveyard display
                    ImageView graveyardView = new ImageView(card.getImage());
                    graveyardView.setFitWidth(60);
                    graveyardView.setFitHeight(90);
                    graveyardView.getProperties().put("card", card); // Store card reference
                    graveyardZone.getChildren().add(graveyardView);
                }
                cardInfoArea.setText(card.getName() + " sent to the Graveyard.");
            });

            actionZone.getChildren().addAll(summonBtn, setBtn, graveyardBtn);

        } else if (card instanceof SpellCard || card instanceof TrapCard) {
            Button setBtn = new Button("Set");
            Button activateBtn = new Button("Activate");
            Button graveyardBtn = new Button("Send to Graveyard");

            // Set (face-down)
            setBtn.setOnAction(ev -> {
                int targetRow = isOpponent ? 0 : 1; // Spell/Trap row
                try {
                    Image backImage = new Image(getClass().getResource("/ui/card_back.png").toExternalForm());
                    ImageView hidden = new ImageView(backImage);
                    hidden.setFitWidth(88);
                    hidden.setFitHeight(132);
                    hidden.getProperties().put("faceDown", true);

                    handBox.getChildren().remove(view);
                    placeCardInZone(boardTarget, handBox, hidden, card, targetRow, isOpponent, cardInfoArea, actionZone);

                    // Clear action zone after playing the card
                    actionZone.getChildren().clear();

                } catch (Exception ex) {
                    System.err.println("Error loading S/T back: " + ex.getMessage());
                }
            });

            // Activate (face-up)
            activateBtn.setOnAction(ev -> {
                int targetRow = isOpponent ? 0 : 1;
                ImageView visible = new ImageView(card.getImage());
                visible.setFitWidth(88);
                visible.setFitHeight(132);
                if (isOpponent) visible.setRotate(180);

                handBox.getChildren().remove(view);
                placeCardInZone(boardTarget, handBox, visible, card, targetRow, isOpponent, cardInfoArea, actionZone);

                cardInfoArea.setText("Activating " + card.getName());
                
                // NEW: Apply the card's effect
                applyCardEffect(card, boardTarget, handBox, isOpponent);

                actionZone.getChildren().clear();
            });

            // Send to Graveyard
            graveyardBtn.setOnAction(ev -> {
                handBox.getChildren().remove(view);
                Object origin = view.getProperties().get("placedCell");
                if (origin instanceof StackPane) {
                    ((StackPane) origin).getChildren().remove(view);
                }
                Node graveyardCell = boardTarget.getChildren().stream()
                    .filter(node -> node.getStyleClass().contains("graveyard-zone"))
                    .findFirst()
                    .orElse(null);
                if (graveyardCell instanceof StackPane) {
                    StackPane graveyardZone = (StackPane) graveyardCell;
                    // Create smaller image for graveyard display
                    ImageView graveyardView = new ImageView(card.getImage());
                    graveyardView.setFitWidth(60);
                    graveyardView.setFitHeight(90);
                    graveyardView.getProperties().put("card", card); // Store card reference
                    graveyardZone.getChildren().add(graveyardView);
                }
                cardInfoArea.setText(card.getName() + " sent to the Graveyard.");

                // Clear action zone after playing the card
                actionZone.getChildren().clear();
            });
            
            actionZone.getChildren().addAll(setBtn, activateBtn, graveyardBtn);
        }
    }

        /**
     * Creates and displays action buttons for a card already placed on the field.
     * <p>
     * This method dynamically generates appropriate action buttons for cards that are
     * currently on the game board (as opposed to cards in hand). The available actions
     * depend on the card type, its current state (face-up/face-down, Attack/Defense),
     * and the current game phase.
     * </p>
     * <p>
     * <strong>Monster Card Actions:</strong>
     * <ul>
     * <li><strong>Send to Graveyard:</strong> Removes the monster from the field and
     *     places it in the graveyard. Can be used for tributes, manual removal, or
     *     destruction effects.</li>
     * <li><strong>Flip:</strong> Flips a face-down monster face-up, revealing its identity
     *     and stats. The monster is automatically set to Attack Position upon flipping.
     *     If already face-up, displays a message indicating this.</li>
     * <li><strong>Change Position:</strong> Switches the monster between Attack Position
     *     (vertical, 0°/180°) and Defense Position (horizontal, 90°/270°). This follows
     *     the Yu-Gi-Oh! rule that allows one position change per monster per turn (though
     *     this restriction is not enforced by this method).</li>
     * <li><strong>Attack (Battle Phase only):</strong> Available only during the Battle Phase
     *     and only if the monster hasn't attacked yet this turn. Activates attack mode,
     *     allowing the player to select an opponent's monster as a target.</li>
     * <li><strong>Direct Attack (Battle Phase only):</strong> Allows the monster to attack
     *     the opponent's life points directly. This button is styled in red (#ff6b6b) for
     *     visibility. Direct attacks are only allowed if the opponent has no monsters on
     *     the field; otherwise, an error message is displayed. After a successful direct
     *     attack, the monster is marked as having attacked to prevent multiple attacks.</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Field Spell Card Actions:</strong>
     * <ul>
     * <li><strong>Activate:</strong> Activates a face-down field spell, revealing its image
     *     and moving it to the field zone if not already there. Only one field spell can
     *     be active at a time per player, so activating a new one clears the previous.</li>
     * <li><strong>Send to Graveyard:</strong> Removes the field spell from the field zone
     *     and sends it to the graveyard.</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Regular Spell/Trap Card Actions:</strong>
     * <ul>
     * <li><strong>Activate:</strong> Flips a face-down spell/trap card face-up, revealing
     *     its effect. The card image is displayed with proper rotation. If already face-up,
     *     displays a message indicating this.</li>
     * <li><strong>Send to Graveyard:</strong> Removes the spell/trap from its zone and
     *     sends it to the graveyard. Used after activation for non-continuous cards or
     *     for manual removal.</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Attack System Details:</strong>
     * The attack functionality works in two stages:
     * <ol>
     * <li>Player clicks "Attack" button → enters attack mode, stores attacking monster</li>
     * <li>Player clicks on opponent's monster → resolves battle via
     *     {@link #resolveBattle(MonsterCard, ImageView, MonsterCard, ImageView, boolean)}</li>
     * </ol>
     * Direct attacks check for opponent monsters using {@link #hasOpponentMonsters(boolean)}
     * and apply damage directly to life points via {@link #directAttack(int)} or manual
     * LP reduction.
     * </p>
     * <p>
     * <strong>Visual Feedback:</strong>
     * <ul>
     * <li>Position changes update rotation: 0°/180° for Attack, 90°/270° for Defense</li>
     * <li>Face-down to face-up flipping reveals the actual card image</li>
     * <li>Cards sent to graveyard are displayed as smaller images (60x90) in graveyard zone</li>
     * <li>Opponent's cards are rotated 180° to face away from the player</li>
     * <li>Monsters that have attacked show a red warning label instead of attack buttons</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Note on Game Rules:</strong>
     * This method does not enforce several official Yu-Gi-Oh! rules:
     * <ul>
     * <li>One position change per turn per monster</li>
     * <li>Cannot change position on the turn a monster is summoned</li>
     * <li>Cannot attack on the turn a monster is summoned</li>
     * <li>Trap cards cannot be activated the turn they are set</li>
     * </ul>
     * Higher-level game logic should enforce these restrictions.
     * </p>
     *
     * @param card the Card object that is placed on the field
     * @param placed the ImageView representing the card's visual appearance on the field
     * @param boardTarget the GridPane containing the game board zones
     * @param handBox the HBox containing the player's hand (not used for placed cards but kept for consistency)
     * @param cardInfoArea the TextArea for displaying action results and card information
     * @param actionZone the VBox where the action buttons will be displayed
     * @param isOpponent true if the card belongs to the opponent (affects rotation and damage calculations)
     */
    public void createActionButtonsForPlacedCard(Card card,
                                                ImageView placed,
                                                GridPane boardTarget,
                                                HBox handBox,
                                                TextArea cardInfoArea,
                                                VBox actionZone,
                                                boolean isOpponent) 
    {
        // Clear existing action buttons
        actionZone.getChildren().clear();

        Label selected = new Label("Card on field: " + card.getName());
        actionZone.getChildren().add(selected);

        if (card instanceof MonsterCard) {
            MonsterCard monster = (MonsterCard) card;

            // === Monster Card Buttons ===
            Button graveyardBtn = new Button("Send to Graveyard");
            graveyardBtn.setOnAction(ev -> {
                Object origin = placed.getProperties().get("placedCell");
                if (origin instanceof StackPane) {
                    ((StackPane) origin).getChildren().remove(placed);
                }
                Node graveyardCell = boardTarget.getChildren().stream()
                    .filter(node -> node.getStyleClass().contains("graveyard-zone"))
                    .findFirst()
                    .orElse(null);
                if (graveyardCell instanceof StackPane) {
                    StackPane graveyardZone = (StackPane) graveyardCell;
                    // Create smaller image for graveyard display
                    ImageView graveyardView = new ImageView(card.getImage());
                    graveyardView.setFitWidth(60);
                    graveyardView.setFitHeight(90);
                    graveyardView.getProperties().put("card", card); // Store card reference
                    graveyardZone.getChildren().add(graveyardView);
                }
                cardInfoArea.setText(card.getName() + " sent to the Graveyard.");
            });

            Button flipBtn = new Button("Flip");
            flipBtn.setOnAction(ev -> {
                boolean faceDown = (boolean) placed.getProperties().getOrDefault("faceDown", false);
                if (faceDown) {
                    placed.setImage(card.getImage());
                    boolean opponent = (boolean) placed.getProperties().getOrDefault("isOpponent", false);
                    placed.setRotate(opponent ? 180 : 0);
                    placed.getProperties().put("faceDown", false);
                    placed.getProperties().put("position", "ATK");
                    cardInfoArea.setText(card.getName() + " is flipped face-up in ATK.");
                } else {
                    cardInfoArea.setText(card.getName() + " is already face-up.");
                }
            });

            Button changePosBtn = new Button("Change Position");
            changePosBtn.setOnAction(ev -> {
                String pos = (String) placed.getProperties().getOrDefault("position", "ATK");
                boolean opponent = (boolean) placed.getProperties().getOrDefault("isOpponent", false);

                if ("ATK".equals(pos)) {
                    placed.setRotate(opponent ? 270 : 90);
                    placed.getProperties().put("position", "DEF");
                    cardInfoArea.setText(card.getName() + " switched to DEF Position.");
                } else {
                    placed.setRotate(opponent ? 180 : 0);
                    placed.getProperties().put("position", "ATK");
                    cardInfoArea.setText(card.getName() + " switched to ATK Position.");
                }
            });

            // Attack button only available during Battle Phase
            if (game != null && game.getCurrentPhase() == Phase.BP) {
                boolean hasAttacked = (boolean) placed.getProperties().getOrDefault("hasAttacked", false);
                
                if (!hasAttacked) {
                    Button attackBtn = new Button("Attack");
                    attackBtn.setOnAction(ev -> {
                        attackMode = true;
                        attackerCard = monster;
                        attackerView = placed;
                        cardInfoArea.setText(monster.getName() + " declares an attack. Select an opponent's monster.");
                    });
                    actionZone.getChildren().add(attackBtn);
                    
                    // NEW: Direct Attack Button
                    Button directAttackBtn = new Button("Direct Attack");
                    directAttackBtn.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white;");
                    directAttackBtn.setOnAction(ev -> {
                        // Check if opponent has any monsters
                        boolean opponentHasMonsters = hasOpponentMonsters(isOpponent);
                        
                        if (opponentHasMonsters) {
                            cardInfoArea.setText("Cannot attack directly: opponent has monsters! You must attack them.");
                        } else {
                            // Direct attack
                            if (isOpponent) {
                                // Opponent's monster attacks the player
                                directAttack(monster.getAtk());
                            } else {
                                // Player's monster attacks the opponent
                                lp2[0] -= monster.getAtk();
                                lpJoueur2.setText(joueur2.getName() + " LP: " + lp2[0]);
                                cardInfoArea.setText(monster.getName() + " attacks directly for " + monster.getAtk() + " damage!");
                                checkVictoryConditions();
                            }
                            
                            // Mark monster as having attacked
                            placed.getProperties().put("hasAttacked", true);
                            
                            // Clear action zone
                            actionZone.getChildren().clear();
                        }
                    });
                    actionZone.getChildren().add(directAttackBtn);
                    
                } else {
                    Label alreadyAttacked = new Label("This monster has already attacked");
                    alreadyAttacked.setStyle("-fx-text-fill: #ff6b6b;");
                    actionZone.getChildren().add(alreadyAttacked);
                }
            }

            actionZone.getChildren().addAll(graveyardBtn, flipBtn, changePosBtn);
            
        } else if (card instanceof SpellCard) {
            SpellCard spell = (SpellCard) card;

            if ("Field".equals(spell.getSpellType())) {
                // === Field Spell Card Buttons ===
                Button activateBtn = new Button("Activate");
                activateBtn.setOnAction(ev -> {
                    placed.setImage(card.getImage());
                    placed.setRotate(isOpponent ? 180 : 0);
                    placed.getProperties().put("faceDown", false);

                    // Place in field zone
                    Node fieldCell = boardTarget.getChildren().stream()
                        .filter(node -> node.getStyleClass().contains("terrain-zone"))
                        .findFirst()
                        .orElse(null);

                    if (fieldCell instanceof StackPane) {
                        StackPane fieldZone = (StackPane) fieldCell;
                        fieldZone.getChildren().clear(); // Only one Field Spell at a time
                        fieldZone.getChildren().add(placed);
                    }

                    cardInfoArea.setText("Activating Field Spell: " + card.getName());
                });

                Button graveyardBtn = new Button("Send to Graveyard");
                graveyardBtn.setOnAction(ev -> {
                    Object origin = placed.getProperties().get("placedCell");
                    if (origin instanceof StackPane) {
                        ((StackPane) origin).getChildren().remove(placed);
                    }
                    Node graveyardCell = boardTarget.getChildren().stream()
                        .filter(node -> node.getStyleClass().contains("graveyard-zone"))
                        .findFirst()
                        .orElse(null);
                    if (graveyardCell instanceof StackPane) {
                        StackPane graveyardZone = (StackPane) graveyardCell;
                        ImageView graveyardView = new ImageView(card.getImage());
                        graveyardView.setFitWidth(60);
                        graveyardView.setFitHeight(90);
                        graveyardView.getProperties().put("card", card);
                        graveyardZone.getChildren().add(graveyardView);
                    }
                    cardInfoArea.setText(card.getName() + " sent to the Graveyard.");
                });

                actionZone.getChildren().addAll(activateBtn, graveyardBtn);

            } else {
                // === Regular Spell Card Buttons ===
                Button activateBtn = new Button("Activate");
                activateBtn.setOnAction(ev -> {
                    boolean faceDown = (boolean) placed.getProperties().getOrDefault("faceDown", true);
                    if (faceDown) {
                        placed.setImage(card.getImage());
                        placed.setRotate(isOpponent ? 180 : 0);
                        placed.getProperties().put("faceDown", false);
                        cardInfoArea.setText("Activating " + card.getName());
                    } else {
                        cardInfoArea.setText(card.getName() + " is already activated.");
                    }
                });

                Button graveyardBtn = new Button("Send to Graveyard");
                graveyardBtn.setOnAction(ev -> {
                    Object origin = placed.getProperties().get("placedCell");
                    if (origin instanceof StackPane) {
                        ((StackPane) origin).getChildren().remove(placed);
                    }
                    Node graveyardCell = boardTarget.getChildren().stream()
                        .filter(node -> node.getStyleClass().contains("graveyard-zone"))
                        .findFirst()
                        .orElse(null);
                    if (graveyardCell instanceof StackPane) {
                        StackPane graveyardZone = (StackPane) graveyardCell;
                        ImageView graveyardView = new ImageView(card.getImage());
                        graveyardView.setFitWidth(60);
                        graveyardView.setFitHeight(90);
                        graveyardView.getProperties().put("card", card);
                        graveyardZone.getChildren().add(graveyardView);
                    }
                    cardInfoArea.setText(card.getName() + " sent to the Graveyard.");
                });

                actionZone.getChildren().addAll(activateBtn, graveyardBtn);
            }
        } else if (card instanceof TrapCard) {
            // === Trap Card Buttons (NEW) ===
            Button activateBtn = new Button("Activate");
            activateBtn.setOnAction(ev -> {
                boolean faceDown = (boolean) placed.getProperties().getOrDefault("faceDown", true);
                if (faceDown) {
                    placed.setImage(card.getImage());
                    placed.setRotate(isOpponent ? 180 : 0);
                    placed.getProperties().put("faceDown", false);
                    cardInfoArea.setText("Activating Trap: " + card.getName());
                    // Optionnel: Appeler un effet ici si vous en ajoutez un jour
                } else {
                    cardInfoArea.setText(card.getName() + " is already activated.");
                }
            });

            Button graveyardBtn = new Button("Send to Graveyard");
            graveyardBtn.setOnAction(ev -> {
                Object origin = placed.getProperties().get("placedCell");
                if (origin instanceof StackPane) {
                    ((StackPane) origin).getChildren().remove(placed);
                }
                Node graveyardCell = boardTarget.getChildren().stream()
                    .filter(node -> node.getStyleClass().contains("graveyard-zone"))
                    .findFirst()
                    .orElse(null);
                if (graveyardCell instanceof StackPane) {
                    StackPane graveyardZone = (StackPane) graveyardCell;
                    ImageView graveyardView = new ImageView(card.getImage());
                    graveyardView.setFitWidth(60);
                    graveyardView.setFitHeight(90);
                    graveyardView.getProperties().put("card", card);
                    graveyardZone.getChildren().add(graveyardView);
                }
                cardInfoArea.setText(card.getName() + " sent to the Graveyard.");
            });

            actionZone.getChildren().addAll(activateBtn, graveyardBtn);
        }
    }

        /**
     * Checks if the opponent has any monsters on their field.
     * <p>
     * This method is used to determine whether a direct attack is allowed. In Yu-Gi-Oh!,
     * players can only attack the opponent's life points directly if the opponent has
     * no monsters on the field. If any monsters are present, they must be attacked first.
     * </p>
     * <p>
     * The method checks all 5 monster zones (columns 1-5) on the appropriate board:
     * <ul>
     * <li>If checking from an opponent's monster perspective (isOpponent=true), checks
     *     the player's board (boardJoueur1, row 0)</li>
     * <li>If checking from the player's monster perspective (isOpponent=false), checks
     *     the opponent's board (opponentBoard, row 1)</li>
     * </ul>
     * </p>
     * <p>
     * A zone is considered to contain a monster if it has an ImageView child whose
     * stored "card" property is an instance of MonsterCard.
     * </p>
     *
     * @param isOpponent true if checking from an opponent's monster perspective (checks player's field),
     *                   false if checking from player's monster perspective (checks opponent's field)
     * @return true if at least one monster is found on the target field, false if no monsters exist
     */
    public boolean hasOpponentMonsters(boolean isOpponent) {
        // If it's an opponent's monster wanting to attack, check the player's field
        // If it's the player's monster wanting to attack, check the opponent's field
        GridPane targetBoard = isOpponent ? boardJoueur1 : opponentBoard;
        int targetRow = isOpponent ? 0 : 1;
        
        for (int col = 1; col <= 5; col++) {
            final int currentCol = col;
            Node cell = targetBoard.getChildren().stream()
                .filter(n -> {
                    Integer rowIdx = GridPane.getRowIndex(n);
                    Integer colIdx = GridPane.getColumnIndex(n);
                    int row = (rowIdx == null) ? 0 : rowIdx;
                    int column = (colIdx == null) ? 0 : colIdx;
                    return row == targetRow && column == currentCol;
                })
                .findFirst()
                .orElse(null);
            
            if (cell instanceof StackPane) {
                StackPane zone = (StackPane) cell;
                // Check if the zone contains a monster
                for (Node child : zone.getChildren()) {
                    if (child instanceof ImageView) {
                        Object cardObj = ((ImageView) child).getProperties().get("card");
                        if (cardObj instanceof MonsterCard) {
                            return true; // At least one monster found
                        }
                    }
                }
            }
        }
        
        return false; // No monsters found
    }

    /**
     * Refreshes the action zone for the currently selected card.
     * <p>
     * This utility method regenerates the action buttons for the currently selected
     * card and updates the action zone display. It's useful when the game state changes
     * and the available actions for a card need to be recalculated (such as after a
     * phase change or after a card effect resolves).
     * </p>
     * <p>
     * If no card is currently selected ({@code selectedCard} or {@code selectedView} is null),
     * this method does nothing.
     * </p>
     * <p>
     * <strong>Note:</strong> This implementation passes null for boardTarget and handBox
     * parameters, which may cause issues if those parameters are needed by
     * {@link #createActionButtonsForPlacedCard}. Consider passing actual values if needed.
     * </p>
     */
    public void refreshActionZone() {
        if (selectedCard != null && selectedView != null) {
            createActionButtonsForPlacedCard(
                selectedCard,
                selectedView,
                null,   // TODO: Pass actual boardTarget if needed
                null,   // TODO: Pass actual handBox if needed
                cardInfoArea,
                actionZone,
                false   // TODO: Determine if card is opponent's
            );
        }
    }

    /**
     * Resolves a battle between two monsters following Yu-Gi-Oh! battle mechanics.
     * <p>
     * This method calculates battle outcomes based on the positions and stats of both
     * monsters, applies battle damage to the appropriate player, and sends destroyed
     * monsters to the graveyard. It follows official Yu-Gi-Oh! battle rules.
     * </p>
     * <p>
     * <strong>Battle Calculation Rules:</strong>
     * </p>
     * <p>
     * <strong>Attack vs Defense Position:</strong>
     * <ul>
     * <li><strong>ATK > DEF:</strong> Defender is destroyed. No damage to either player.</li>
     * <li><strong>ATK < DEF:</strong> Attacker takes damage equal to (DEF - ATK).
     *     Defender remains on field.</li>
     * <li><strong>ATK = DEF:</strong> Neither monster is destroyed, no damage dealt.</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Attack vs Attack Position:</strong>
     * <ul>
     * <li><strong>Attacker ATK > Defender ATK:</strong> Defender is destroyed.
     *     Defender's controller takes damage equal to (Attacker ATK - Defender ATK).</li>
     * <li><strong>Attacker ATK < Defender ATK:</strong> Attacker is destroyed.
     *     Attacker's controller takes damage equal to (Defender ATK - Attacker ATK).</li>
     * <li><strong>Equal ATK:</strong> Both monsters are destroyed. No damage to either player.</li>
     * </ul>
     * </p>
     * <p>
     * After battle resolution, the method automatically checks victory conditions via
     * {@link #checkVictoryConditions()} to determine if any player's life points have
     * reached 0 or below.
     * </p>
     * <p>
     * The attacking monster is marked as having attacked this turn by setting the
     * "hasAttacked" property to true, preventing multiple attacks per turn.
     * </p>
     *
     * @param attacker the MonsterCard performing the attack
     * @param attackerView the ImageView representing the attacker on the field
     * @param defender the MonsterCard being attacked
     * @param defenderView the ImageView representing the defender on the field
     * @param defenderIsOpponent true if the defender belongs to the opponent, false if it belongs to the player
     */
    public void resolveBattle(MonsterCard attacker, ImageView attackerView,
                            MonsterCard defender, ImageView defenderView,
                            boolean defenderIsOpponent) 
    {
        // Mark attacker as having attacked this turn
        attackerView.getProperties().put("hasAttacked", true);
        
        String defenderPos = (String) defenderView.getProperties().getOrDefault("position", "ATK");
        int atkAttacker = attacker.getAtk();
        int atkDefender = defender.getAtk();
        int defDefender = defender.getDef();

        if ("DEF".equals(defenderPos)) {
            // Battle against Defense Position
            if (atkAttacker > defDefender) {
                sendToGraveyard(defender, defenderView, defenderIsOpponent ? opponentBoard : boardJoueur1);
                cardInfoArea.setText(attacker.getName() + " destroys " + defender.getName() + " in DEF.");
            } else if (atkAttacker < defDefender) {
                int damage = defDefender - atkAttacker;
                
                // Apply damage to attacker's controller
                if (defenderIsOpponent) {
                    lp1[0] -= damage;
                    lpJoueur1.setText(joueur1.getName() + " LP: " + lp1[0]);
                } else {
                    lp2[0] -= damage;
                    lpJoueur2.setText(joueur2.getName() + " LP: " + lp2[0]);
                }
                
                cardInfoArea.setText(attacker.getName() + " attacks " + defender.getName() + 
                                " in DEF but fails. Damage: " + damage);
            } else {
                cardInfoArea.setText("Draw: ATK = DEF, no monsters destroyed.");
            }
        } else {
            // Battle against Attack Position
            if (atkAttacker > atkDefender) {
                int damage = atkAttacker - atkDefender;
                sendToGraveyard(defender, defenderView, defenderIsOpponent ? opponentBoard : boardJoueur1);
                
                // Apply damage to defender's controller
                if (defenderIsOpponent) {
                    lp2[0] -= damage;
                    lpJoueur2.setText(joueur2.getName() + " LP: " + lp2[0]);
                } else {
                    lp1[0] -= damage;
                    lpJoueur1.setText(joueur1.getName() + " LP: " + lp1[0]);
                }
                
                cardInfoArea.setText(attacker.getName() + " destroys " + defender.getName() + 
                                " in ATK. Damage: " + damage);
            } else if (atkAttacker < atkDefender) {
                int damage = atkDefender - atkAttacker;
                sendToGraveyard(attacker, attackerView, defenderIsOpponent ? boardJoueur1 : opponentBoard);
                
                // Apply damage to attacker's controller
                if (defenderIsOpponent) {
                    lp1[0] -= damage;
                    lpJoueur1.setText(joueur1.getName() + " LP: " + lp1[0]);
                } else {
                    lp2[0] -= damage;
                    lpJoueur2.setText(joueur2.getName() + " LP: " + lp2[0]);
                }
                
                cardInfoArea.setText(attacker.getName() + " is destroyed by " + defender.getName() + 
                                " in ATK. Damage: " + damage);
            } else {
                // Equal ATK: both destroyed
                sendToGraveyard(attacker, attackerView, defenderIsOpponent ? boardJoueur1 : opponentBoard);
                sendToGraveyard(defender, defenderView, defenderIsOpponent ? opponentBoard : boardJoueur1);
                cardInfoArea.setText("Draw: both monsters are destroyed.");
            }
        }
        
        // Check victory conditions after battle
        checkVictoryConditions();
    }

    /**
     * Sends a card to the graveyard, handling all necessary UI updates.
     * <p>
     * This method performs three critical operations when a card is sent to the graveyard:
     * <ol>
     * <li>Removes the card's ImageView from its current zone on the field</li>
     * <li>Locates the appropriate graveyard zone on the target board</li>
     * <li>Creates a new smaller ImageView (60x90) of the card and adds it to the graveyard</li>
     * </ol>
     * </p>
     * <p>
     * The method handles two possible graveyard zone locations:
     * <ul>
     * <li>Direct child of the GridPane (typical for player's graveyard)</li>
     * <li>Child of an HBox within the GridPane (typical for opponent's graveyard,
     *     which may be grouped with the banished zone)</li>
     * </ul>
     * </p>
     * <p>
     * The card reference is stored in the graveyard ImageView's properties for potential
     * future interactions (such as revival effects or graveyard examination).
     * </p>
     * <p>
     * <strong>Debugging Output:</strong>
     * The method prints debug information to the console, showing:
     * <ul>
     * <li>The name of the card being sent</li>
     * <li>Which board's graveyard is being targeted (Player or Opponent)</li>
     * <li>Whether the graveyard zone was successfully found</li>
     * </ul>
     * </p>
     * <p>
     * If the graveyard zone cannot be located, an error message is printed to the console
     * and no visual update occurs (though the card is still removed from its original zone).
     * </p>
     *
     * @param card the Card object being sent to the graveyard
     * @param view the ImageView representing the card on the field (to be removed)
     * @param boardTarget the GridPane containing the graveyard zone where the card should be sent
     */
    public void sendToGraveyard(Card card, ImageView view, GridPane boardTarget) {
        System.out.println("Sending to graveyard: " + card.getName());
        System.out.println("Target board: " + (boardTarget == boardJoueur1 ? "Player" : "Opponent"));
        
        if (boardTarget == null || view == null) return;

        // 1. Remove the card from its current zone
        Object placedCell = view.getProperties().get("placedCell");
        if (placedCell instanceof StackPane) {
            ((StackPane) placedCell).getChildren().remove(view);
        } else if (view.getParent() instanceof StackPane) {
            ((StackPane) view.getParent()).getChildren().remove(view);
        }

        // 2. Find the graveyard zone (may be in an HBox for opponent)
        StackPane graveyardZone = null;

        // Search directly in the GridPane
        for (Node node : boardTarget.getChildren()) {
            if (node.getStyleClass().contains("graveyard-zone") && node instanceof StackPane) {
                graveyardZone = (StackPane) node;
                break;
            }
            // Search in HBoxes (opponent case)
            if (node instanceof HBox) {
                for (Node child : ((HBox) node).getChildren()) {
                    if (child.getStyleClass().contains("graveyard-zone") && child instanceof StackPane) {
                        graveyardZone = (StackPane) child;
                        break;
                    }
                }
            }
            if (graveyardZone != null) break;
        }

        if (graveyardZone != null) {
            System.out.println("Graveyard found!");
            
            // 3. Create a new ImageView for the graveyard
            ImageView graveyardView = new ImageView(card.getImage());
            graveyardView.setFitWidth(60);
            graveyardView.setFitHeight(90);
            graveyardView.setRotate(0); // Always upright in graveyard
            graveyardView.getProperties().put("card", card); // Store card reference

            // 4. Add to graveyard
            graveyardZone.getChildren().add(graveyardView);
            
            cardInfoArea.appendText("\n" + card.getName() + " has been sent to the graveyard.");
        } else {
            System.out.println("Graveyard not found!");
        }
    }

        /**
     * Displays a popup window showing all cards in a player's graveyard.
     * <p>
     * This method creates a modal dialog that allows players to view and interact with
     * cards in the graveyard. The popup displays all cards in a scrollable grid, shows
     * detailed information when a card is selected, and provides action buttons for
     * applicable cards (such as Special Summoning monsters).
     * </p>
     * <p>
     * <strong>Popup Components:</strong>
     * <ul>
     * <li><strong>Card Grid:</strong> A FlowPane displaying all graveyard cards as 120x180
     *     pixel thumbnails in a scrollable view</li>
     * <li><strong>Card Info Area:</strong> A TextArea showing detailed information about
     *     the selected card (stats, description, etc.)</li>
     * <li><strong>Actions Zone:</strong> A VBox containing context-specific action buttons
     *     based on the selected card type</li>
     * <li><strong>Close Button:</strong> Closes the popup and returns to the game</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Monster Card Actions:</strong>
     * When a MonsterCard is selected, a "Special Summon" button appears (styled in green).
     * Clicking it attempts to Special Summon the monster from the graveyard to the player's
     * field:
     * <ul>
     * <li>Removes the card from the graveyard zone</li>
     * <li>Creates a new ImageView (88x132) for the field</li>
     * <li>Places it in the first available monster zone in Attack Position</li>
     * <li>Sets up click handlers for further interactions</li>
     * <li>Closes the popup on successful summon</li>
     * <li>Returns the card to graveyard if no zones are available</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Card Selection:</strong>
     * Clicking a card highlights it with the "card-selected" CSS class and displays its
     * information. The card object is retrieved from the ImageView's stored properties,
     * ensuring accurate data display even after visual transformations.
     * </p>
     * <p>
     * <strong>Empty Graveyard Handling:</strong>
     * If the graveyard contains no cards, displays a simple message "The graveyard is empty"
     * in a smaller window.
     * </p>
     * <p>
     * <strong>Note:</strong> Currently, Special Summoning always places monsters on the
     * player's field (boardJoueur1) with rotation 0 and isOpponent=false. This is correct
     * for the current player viewing their own or opponent's graveyard to summon to their
     * own field.
     * </p>
     *
     * @param graveyardZone the StackPane containing the graveyard cards to display
     * @param playerName the name of the player whose graveyard is being viewed (for title)
     * @param isOpponent true if viewing the opponent's graveyard, false for the player's own
     */
    public void showGraveyardPopup(StackPane graveyardZone, String playerName, boolean isOpponent) {
        Stage popup = new Stage();
        popup.setTitle(playerName + "'s Graveyard");
        
        // Retrieve all cards from the graveyard with their Card objects
        List<Node> graveyardNodes = new ArrayList<>(graveyardZone.getChildren());
        
        if (graveyardNodes.isEmpty()) {
            Label empty = new Label("The graveyard is empty");
            empty.setStyle("-fx-font-size: 16px; -fx-padding: 20px;");
            Scene scene = new Scene(new VBox(empty), 450, 300);
            popup.setScene(scene);
            popup.show();
            return;
        }
        
        // Selected card info area
        TextArea selectedCardInfo = new TextArea("Select a card to view its information");
        selectedCardInfo.setEditable(false);
        selectedCardInfo.setPrefHeight(150);
        selectedCardInfo.setWrapText(true);
        selectedCardInfo.setStyle("-fx-font-size: 12px; -fx-control-inner-background: #3c3c3c; -fx-text-fill: white;");
        
        // Actions zone
        VBox actionsZone = new VBox(10);
        actionsZone.setAlignment(Pos.CENTER);
        actionsZone.setPrefHeight(80);
        
        // Card grid
        FlowPane cardGrid = new FlowPane();
        cardGrid.setHgap(10);
        cardGrid.setVgap(10);
        cardGrid.setPadding(new Insets(15));
        cardGrid.setStyle("-fx-background-color: #2c2c2c;");
        
        // Variables for selected card
        final ImageView[] selectedView = {null};
        final Card[] selectedCard = {null};
        
        for (Node node : graveyardNodes) {
            if (node instanceof ImageView) {
                ImageView originalView = (ImageView) node;
                
                // Retrieve associated card (stored in properties)
                Card card = (Card) originalView.getProperties().get("card");
                
                // Create a copy for display
                ImageView cardView = new ImageView(originalView.getImage());
                cardView.setFitWidth(120);
                cardView.setFitHeight(180);
                cardView.setPreserveRatio(true);
                cardView.getStyleClass().add("graveyard-card-view");
                
                // Click on card → show info + actions
                cardView.setOnMouseClicked(e -> {
                    System.out.println("Graveyard card clicked");
                    System.out.println("Card retrieved: " + card);
                    
                    // Remove previous selection
                    cardGrid.getChildren().forEach(n -> n.getStyleClass().remove("card-selected"));
                    cardView.getStyleClass().add("card-selected");
                    
                    selectedView[0] = originalView;
                    selectedCard[0] = card;
                    
                    // Display card info
                    if (card != null) {
                        System.out.println("Displaying info: " + card.toString());
                        selectedCardInfo.setText(card.toString());
                    } else {
                        System.out.println("Card is null!");
                        selectedCardInfo.setText("Card information unavailable");
                    }
                    
                    // Create action buttons
                    actionsZone.getChildren().clear();
                    System.out.println("Card type: " + (card != null ? card.getClass().getSimpleName() : "null"));
                    
                    if (card instanceof MonsterCard) {
                        System.out.println("It's a monster, adding button");
                        Button specialSummonBtn = new Button("Special Summon");
                        specialSummonBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
                        
                        specialSummonBtn.setOnAction(ev -> {
                            // CORRECTION: Always summon to current player's field (player 1)
                            GridPane targetBoard = boardJoueur1;
                            
                            // Remove card from graveyard
                            graveyardZone.getChildren().remove(originalView);
                            
                            // Create new ImageView for the field
                            ImageView summonedView = new ImageView(card.getImage());
                            summonedView.setFitWidth(88);
                            summonedView.setFitHeight(132);
                            summonedView.setRotate(0); // Always rotation 0 for player
                            summonedView.getProperties().put("faceDown", false);
                            summonedView.getProperties().put("position", "ATK");
                            summonedView.getProperties().put("isOpponent", false); // Always false as it's our field
                            
                            // Find a free monster zone on player's field
                            int targetRow = 0; // Player's monster row
                            boolean placed = false;
                            
                            for (int col = 1; col <= 5; col++) {
                                final int currentCol = col;
                                Node cell = targetBoard.getChildren().stream()
                                    .filter(n -> GridPane.getRowIndex(n) == targetRow &&
                                                GridPane.getColumnIndex(n) == currentCol)
                                    .findFirst()
                                    .orElse(null);
                                
                                if (cell instanceof StackPane) {
                                    StackPane zone = (StackPane) cell;
                                    if (zone.getChildren().isEmpty()) {
                                        // Place the monster
                                        summonedView.setOnMouseClicked(clickEv -> {
                                            selectCard(summonedView, targetBoard, handJ1, cardInfoArea, card);
                                            createActionButtonsForPlacedCard(card, summonedView, targetBoard,
                                                handJ1, cardInfoArea, actionZone, false);
                                        });
                                        
                                        zone.getChildren().add(summonedView);
                                        zone.getProperties().put("card", card);
                                        summonedView.getProperties().put("placedCell", zone);
                                        
                                        cardInfoArea.setText(card.getName() + " was Special Summoned from the graveyard!");
                                        placed = true;
                                        popup.close();
                                        break;
                                    }
                                }
                            }
                            
                            if (!placed) {
                                // Return to graveyard if no space available
                                graveyardZone.getChildren().add(originalView);
                                selectedCardInfo.setText("No monster zones available!");
                            }
                        });
                        
                        actionsZone.getChildren().add(specialSummonBtn);
                    } else {
                        System.out.println("Not a monster or card is null");
                        Label noAction = new Label("Only monsters can be summoned");
                        noAction.setStyle("-fx-text-fill: #999;");
                        actionsZone.getChildren().add(noAction);
                    }
                });
                
                cardGrid.getChildren().add(cardView);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(cardGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: #1a1a1a; -fx-background-color: #1a1a1a;");
        
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 30px;");
        closeBtn.setOnAction(e -> popup.close());
        
        VBox layout = new VBox(15, scrollPane, selectedCardInfo, actionsZone, closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #1a1a1a;");
        layout.setPadding(new Insets(10));
        
        Scene scene = new Scene(layout, 700, 650);
        scene.getStylesheets().add(getClass().getResource("/ui/board.css").toExternalForm());
        popup.setScene(scene);
        popup.show();
    }

    /**
     * Displays a popup window showing all cards in a player's deck.
     * <p>
     * This method creates a modal dialog that allows players to view and interact with
     * cards in their deck. This is typically used for card effects that allow searching
     * the deck (such as "Sangan" or "Reinforcement of the Army") or for testing purposes.
     * The popup displays all cards in a scrollable grid and provides Special Summon
     * functionality for monsters.
     * </p>
     * <p>
     * <strong>Popup Components:</strong>
     * <ul>
     * <li><strong>Card Grid:</strong> A FlowPane displaying all deck cards as 120x180
     *     pixel thumbnails in a scrollable view</li>
     * <li><strong>Card Info Area:</strong> A TextArea showing detailed information about
     *     the selected card</li>
     * <li><strong>Actions Zone:</strong> A VBox containing action buttons for applicable
     *     card types</li>
     * <li><strong>Close Button:</strong> Closes the popup and returns to the game</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Monster Card Actions:</strong>
     * When a MonsterCard is selected, a "Special Summon" button appears. Clicking it:
     * <ul>
     * <li>Removes the card from the deck via {@link Deck#removeCard(Card)}</li>
     * <li>Creates a new ImageView for the field (88x132, face-up Attack Position)</li>
     * <li>Places it in the first available monster zone</li>
     * <li>Updates the deck counter display via {@link #updateDeckCount(GridPane, Deck)}</li>
     * <li>Closes the popup on successful summon</li>
     * <li>Returns the card to deck if no zones are available</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Empty Deck Handling:</strong>
     * If the deck contains no cards, displays a simple message "The deck is empty" in
     * a smaller window.
     * </p>
     * <p>
     * <strong>Use Cases:</strong>
     * <ul>
     * <li>Card effects that search the deck for specific cards</li>
     * <li>Special Summon effects that summon from deck (e.g., "Cyber Dragon")</li>
     * <li>Testing and debugging deck contents</li>
     * <li>Deck verification before the duel starts</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Note:</strong> This method directly modifies the deck by removing summoned
     * cards. In official Yu-Gi-Oh! rules, the deck should be shuffled after searching it,
     * which should be handled by the calling code if needed.
     * </p>
     *
     * @param deck the Deck object containing the cards to display
     * @param playerName the name of the player whose deck is being viewed (for title)
     */
    public void showDeckPopup(Deck deck, String playerName) {
        Stage popup = new Stage();
        popup.setTitle(playerName + "'s Deck");
        
        // Retrieve all cards from the deck
        List<Card> deckCards = deck.getCards(); // Assumes a getCards() getter exists in Deck
        
        if (deckCards.isEmpty()) {
            Label empty = new Label("The deck is empty");
            empty.setStyle("-fx-font-size: 16px; -fx-padding: 20px;");
            Scene scene = new Scene(new VBox(empty), 300, 150);
            popup.setScene(scene);
            popup.show();
            return;
        }
        
        // Selected card info area
        TextArea selectedCardInfo = new TextArea("Select a card to view its information");
        selectedCardInfo.setEditable(false);
        selectedCardInfo.setPrefHeight(150);
        selectedCardInfo.setWrapText(true);
        selectedCardInfo.setStyle("-fx-font-size: 12px; -fx-control-inner-background: #3c3c3c; -fx-text-fill: white;");
        
        // Actions zone
        VBox actionsZone = new VBox(10);
        actionsZone.setAlignment(Pos.CENTER);
        actionsZone.setPrefHeight(80);
        
        // Card grid
        FlowPane cardGrid = new FlowPane();
        cardGrid.setHgap(10);
        cardGrid.setVgap(10);
        cardGrid.setPadding(new Insets(15));
        cardGrid.setStyle("-fx-background-color: #2c2c2c;");
        
        // Variable for selected card
        final Card[] selectedCard = {null};
        
        for (Card card : deckCards) {
            // Create ImageView for each card
            ImageView cardView = new ImageView(card.getImage());
            cardView.setFitWidth(120);
            cardView.setFitHeight(180);
            cardView.setPreserveRatio(true);
            cardView.getStyleClass().add("graveyard-card-view");
            
            // Click on card
            cardView.setOnMouseClicked(e -> {
                // Remove previous selection
                cardGrid.getChildren().forEach(n -> n.getStyleClass().remove("card-selected"));
                cardView.getStyleClass().add("card-selected");
                
                selectedCard[0] = card;
                
                // Display card info
                selectedCardInfo.setText(card.toString());
                
                // Create action buttons
                actionsZone.getChildren().clear();
                
                if (card instanceof MonsterCard) {
                    Button specialSummonBtn = new Button("Special Summon");
                    specialSummonBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
                    
                    specialSummonBtn.setOnAction(ev -> {
                        // Remove card from deck
                        deck.removeCard(card); // Assumes a removeCard() method exists in Deck
                        
                        // Create ImageView for the field
                        ImageView summonedView = new ImageView(card.getImage());
                        summonedView.setFitWidth(88);
                        summonedView.setFitHeight(132);
                        summonedView.setRotate(0);
                        summonedView.getProperties().put("faceDown", false);
                        summonedView.getProperties().put("position", "ATK");
                        summonedView.getProperties().put("isOpponent", false);
                        
                        // Find a free monster zone
                        int targetRow = 0;
                        boolean placed = false;
                        
                        for (int col = 1; col <= 5; col++) {
                            final int currentCol = col;
                            Node cell = boardJoueur1.getChildren().stream()
                                .filter(n -> GridPane.getRowIndex(n) == targetRow &&
                                            GridPane.getColumnIndex(n) == currentCol)
                                .findFirst()
                                .orElse(null);
                            
                            if (cell instanceof StackPane) {
                                StackPane zone = (StackPane) cell;
                                if (zone.getChildren().isEmpty()) {
                                    summonedView.setOnMouseClicked(clickEv -> {
                                        selectCard(summonedView, boardJoueur1, handJ1, cardInfoArea, card);
                                        createActionButtonsForPlacedCard(card, summonedView, boardJoueur1,
                                            handJ1, cardInfoArea, actionZone, false);
                                    });
                                    
                                    zone.getChildren().add(summonedView);
                                    zone.getProperties().put("card", card);
                                    summonedView.getProperties().put("placedCell", zone);
                                    
                                    cardInfoArea.setText(card.getName() + " was Special Summoned from the deck!");
                                    placed = true;
                                    
                                    // Update deck counter
                                    updateDeckCount(boardJoueur1, deck);
                                    
                                    popup.close();
                                    break;
                                }
                            }
                        }
                        
                        if (!placed) {
                            // Return to deck if no space available
                            deck.addCard(card);
                            selectedCardInfo.setText("No monster zones available!");
                        }
                    });
                    
                    actionsZone.getChildren().add(specialSummonBtn);
                } else {
                    Label noAction = new Label("Only monsters can be summoned");
                    noAction.setStyle("-fx-text-fill: #999;");
                    actionsZone.getChildren().add(noAction);
                }
            });
            
            cardGrid.getChildren().add(cardView);
        }
        
        ScrollPane scrollPane = new ScrollPane(cardGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: #1a1a1a; -fx-background-color: #1a1a1a;");
        
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px 30px;");
        closeBtn.setOnAction(e -> popup.close());
        
        VBox layout = new VBox(15, scrollPane, selectedCardInfo, actionsZone, closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #1a1a1a;");
        layout.setPadding(new Insets(10));
        
        Scene scene = new Scene(layout, 700, 650);
        scene.getStylesheets().add(getClass().getResource("/ui/board.css").toExternalForm());
        popup.setScene(scene);
        popup.show();
    }

        /**
     * Updates the deck card counter display on the game board.
     * <p>
     * This helper method finds the deck zone on the specified board and updates
     * its counter label to show the current number of cards remaining in the deck.
     * This provides visual feedback to players about deck depletion and helps track
     * the risk of deck-out loss conditions.
     * </p>
     * <p>
     * The method searches for a node with the "deck-zone" CSS class, then locates
     * a child Label with the "deck-count" CSS class and updates its text to display
     * "Cards: X" where X is the current deck size.
     * </p>
     * <p>
     * If either the deck zone or counter label cannot be found, the method fails
     * silently without updating anything.
     * </p>
     *
     * @param board the GridPane containing the deck zone to update
     * @param deck the Deck object whose size will be displayed
     */
    public void updateDeckCount(GridPane board, Deck deck) {
        StackPane deckZone = (StackPane) board.getChildren().stream()
            .filter(node -> node.getStyleClass().contains("deck-zone"))
            .findFirst()
            .orElse(null);
        
        if (deckZone != null) {
            Label deckCount = (Label) deckZone.getChildren().stream()
                .filter(node -> node.getStyleClass().contains("deck-count"))
                .findFirst()
                .orElse(null);
            
            if (deckCount != null) {
                deckCount.setText("Cards: " + deck.size());
            }
        }
    }

    /**
     * Handles click events on the bot's monster cards.
     * <p>
     * This public method allows the bot AI to properly handle interactions with its
     * monsters on the field. It supports two modes of interaction:
     * </p>
     * <ul>
     * <li><strong>Attack Mode:</strong> If attack mode is active (player is attacking),
     *     the clicked bot monster becomes the target of the attack. Battle is resolved
     *     via {@link #resolveBattle} and attack mode is reset.</li>
     * <li><strong>Normal Mode:</strong> The monster is selected normally, displaying its
     *     information and generating appropriate action buttons.</li>
     * </ul>
     * <p>
     * This method ensures that bot monsters can be attacked by the player and also
     * allows viewing bot monster information for gameplay decisions.
     * </p>
     *
     * @param view the ImageView representing the bot's monster on the field
     * @param card the Card object (must be a MonsterCard) being clicked
     * @param board the GridPane containing the bot's game board
     * @param hand the HBox containing the bot's hand (typically hidden)
     */
    public void handleBotMonsterClick(ImageView view, Card card, GridPane board, HBox hand) {
        // If in attack mode, this is a target
        if (attackMode && attackerCard != null && attackerView != null && card instanceof MonsterCard) {
            resolveBattle(attackerCard, attackerView, (MonsterCard) card, view, true);
            attackMode = false;
            attackerCard = null;
            attackerView = null;
        } else {
            // Normal selection
            selectCard(view, board, hand, cardInfoArea, card);
            createActionButtonsForPlacedCard(card, view, board, hand, cardInfoArea, actionZone, true);
        }
    }

    /**
     * Gets the player's game board.
     * <p>
     * This getter provides access to player 1's board (bottom of screen) for external
     * classes such as the bot AI, which needs to interact with the player's field when
     * making decisions or executing actions.
     * </p>
     *
     * @return the GridPane representing player 1's game board
     */
    public GridPane getBoardJoueur1() {
        return boardJoueur1;
    }

    /**
     * Public wrapper for resolving battles, accessible to external classes.
     * <p>
     * This method delegates to the {@link #resolveBattle} method, allowing
     * external classes (such as the bot AI) to trigger battle resolution while
     * maintaining encapsulation of the internal battle logic.
     * </p>
     * <p>
     * This is used when the bot needs to declare attacks and resolve battles
     * programmatically rather than through UI interactions.
     * </p>
     *
     * @param attacker the MonsterCard performing the attack
     * @param attackerView the ImageView representing the attacker on the field
     * @param defender the MonsterCard being attacked
     * @param defenderView the ImageView representing the defender on the field
     * @param defenderIsOpponent true if the defender belongs to the opponent
     */
    public void resolveBattlePublic(MonsterCard attacker, ImageView attackerView,
                                    MonsterCard defender, ImageView defenderView,
                                    boolean defenderIsOpponent) {
        resolveBattle(attacker, attackerView, defender, defenderView, defenderIsOpponent);
    }

    /**
     * Applies damage from a direct attack to the player's life points.
     * <p>
     * A direct attack occurs when a monster attacks the opponent directly because
     * they have no monsters on the field to defend. The full ATK value of the
     * attacking monster is dealt as damage to the player's life points.
     * </p>
     * <p>
     * After applying damage, this method automatically checks victory conditions
     * via {@link #checkVictoryConditions()} to determine if the player has lost
     * due to reaching 0 life points.
     * </p>
     *
     * @param damage the amount of damage to inflict (typically the attacking monster's ATK)
     */
    public void directAttack(int damage) {
        lp1[0] -= damage;
        lpJoueur1.setText(joueur1.getName() + " LP: " + lp1[0]);
        cardInfoArea.setText("Direct attack! " + damage + " damage dealt!");

        // Check victory conditions
        checkVictoryConditions();
    }

    /**
     * Resets the "hasAttacked" flag for all monsters on the specified board.
     * <p>
     * This method should be called at the start of each turn to allow all monsters
     * to attack again. It iterates through all zones on the board, finds ImageViews
     * (representing cards), and sets their "hasAttacked" property to false.
     * </p>
     * <p>
     * This is part of the turn management system that enforces the Yu-Gi-Oh! rule
     * that each monster can only attack once per turn.
     * </p>
     *
     * @param board the GridPane whose monsters should have their attack flags reset
     */
    public void resetAttackFlags(GridPane board) {
        for (Node node : board.getChildren()) {
            if (node instanceof StackPane) {
                StackPane zone = (StackPane) node;
                for (Node child : zone.getChildren()) {
                    if (child instanceof ImageView) {
                        child.getProperties().put("hasAttacked", false);
                    }
                }
            }
        }
    }

    /**
     * Checks all victory and loss conditions and ends the game if any are met.
     * <p>
     * This method evaluates the standard Yu-Gi-Oh! win/loss conditions:
     * <ul>
     * <li><strong>Life Points:</strong> If either player's life points reach 0 or below,
     *     their opponent wins</li>
     * <li><strong>Deck Out:</strong> If either player's deck is empty when they need to
     *     draw, they lose (handled by checking {@link Deck#isEmpty()})</li>
     * </ul>
     * </p>
     * <p>
     * When a victory condition is met:
     * <ul>
     * <li>The victory screen is displayed via {@link #showVictoryScreen}</li>
     * <li>The bot is disabled if active (isBotEnabled = false)</li>
     * <li>Further gameplay is prevented</li>
     * </ul>
     * </p>
     * <p>
     * This method should be called after any action that could result in a win:
     * <ul>
     * <li>After battle damage is applied</li>
     * <li>After direct attacks</li>
     * <li>After card effects that deal damage or force draws</li>
     * <li>At the start of the Draw Phase (to check deck-out)</li>
     * </ul>
     * </p>
     */
    public void checkVictoryConditions() {
        if (lp1[0] <= 0) {
            showVictoryScreen(joueur2, joueur1.getName() + " has no Life Points left!");
            isBotEnabled = false; // Disable bot
        } else if (lp2[0] <= 0) {
            showVictoryScreen(joueur1, joueur2.getName() + " has no Life Points left!");
            isBotEnabled = false;
        }
        
        // Bonus: Check if a deck is empty (deck-out loss)
        if (deckJ1.isEmpty()) {
            showVictoryScreen(joueur2, joueur1.getName() + " cannot draw anymore!");
            isBotEnabled = false;
        } else if (deckJ2.isEmpty()) {
            showVictoryScreen(joueur1, joueur2.getName() + " cannot draw anymore!");
            isBotEnabled = false;
        }
    }

    /**
     * Displays the victory screen when the game ends.
     * <p>
     * This method creates a modal dialog that announces the winner and the reason
     * for victory. The popup features:
     * <ul>
     * <li>A gradient background (dark blue theme)</li>
     * <li>The winner's name in large gold text</li>
     * <li>The reason for victory in white text</li>
     * <li>A close button to dismiss the dialog</li>
     * </ul>
     * </p>
     * <p>
     * The victory screen is modal, meaning it blocks interaction with the main
     * game window until closed. This prevents further gameplay after the duel
     * has concluded.
     * </p>
     *
     * @param winner the Player who won the duel
     * @param reason a string describing why the victory occurred (e.g., "has no Life Points left!")
     */
    public void showVictoryScreen(Player winner, String reason) {
        // Create victory popup
        Stage victoryStage = new Stage();
        victoryStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        victoryStage.setTitle("Victory!");
        
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e); -fx-padding: 40px;");
        
        Label winnerLabel = new Label("Winner: " + winner.getName() + " has won!");
        winnerLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: gold;");
        
        Label reasonLabel = new Label(reason);
        reasonLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10px 30px;");
        closeBtn.setOnAction(e -> victoryStage.close());
        
        layout.getChildren().addAll(winnerLabel, reasonLabel, closeBtn);
        
        Scene scene = new Scene(layout, 500, 300);
        victoryStage.setScene(scene);
        victoryStage.show();
    }

    /**
     * Applies the effect of a card based on its name.
     * <p>
     * This method implements specific card effects by matching the card's name and
     * executing the corresponding game logic. It serves as a centralized effect
     * handler for spell and trap cards that have been activated.
     * </p>
     * <p>
     * <strong>Implemented Effects:</strong>
     * <ul>
     * <li><strong>Dark Hole:</strong> Destroys all monsters on both players' fields.
     *     Calls {@link #destroyAllMonsters} for each board.</li>
     * <li><strong>Fissure:</strong> Destroys the opponent's monster with the lowest ATK.
     *     Calls {@link #destroyWeakestMonster} on the opponent's board.</li>
     * </ul>
     * </p>
     * <p>
     * For cards without implemented effects, a message is logged to the console and
     * appended to the card info area indicating "(Effect not implemented)".
     * </p>
     * <p>
     * <strong>Future Enhancements:</strong>
     * Additional card effects should be added here using the same pattern:
     * check card name, execute effect logic, update card info area with result.
     * </p>
     *
     * @param card the Card whose effect is being activated
     * @param boardTarget the GridPane of the player activating the card
     * @param handBox the HBox containing the activating player's hand
     * @param isOpponent true if the card is being activated by the opponent
     */
    public void applyCardEffect(Card card, GridPane boardTarget, HBox handBox, boolean isOpponent) {
        String cardName = card.getName();
        
        System.out.println("Applying effect of: " + cardName);
        
        // Dark Hole: Destroy all monsters
        if (cardName.equalsIgnoreCase("Dark Hole")) {
            destroyAllMonsters(boardJoueur1);
            destroyAllMonsters(opponentBoard);
            cardInfoArea.setText("Dark Hole activated! All monsters are destroyed.");
        }
        
        // Fissure: Destroy opponent's weakest monster
        else if (cardName.equalsIgnoreCase("Fissure")) {
            GridPane opponentBoardTarget = isOpponent ? boardJoueur1 : opponentBoard;
            destroyWeakestMonster(opponentBoardTarget);
            cardInfoArea.setText("Fissure activated! The opponent's weakest monster is destroyed.");
        }
        
        // Add other effects here...
        
        else {
            System.out.println("Effect not implemented for: " + cardName);
            cardInfoArea.appendText("\n(Effect not implemented)");
        }
    }

    /**
     * Destroys all monsters on the specified game board.
     * <p>
     * This method iterates through all monster zones (columns 1-5) on the given board,
     * identifies any MonsterCards present, removes them from their zones, and sends
     * them to the graveyard via {@link #sendToGraveyard}.
     * </p>
     * <p>
     * The monster row is determined based on which board is being cleared:
     * <ul>
     * <li>Player's board (boardJoueur1): row 0</li>
     * <li>Opponent's board: row 1</li>
     * </ul>
     * </p>
     * <p>
     * This method is used by board-clearing effects like "Dark Hole", "Raigeki",
     * "Mirror Force", etc.
     * </p>
     *
     * @param board the GridPane whose monsters should be destroyed
     */
    public void destroyAllMonsters(GridPane board) {
        int monsterRow = (board == boardJoueur1) ? 0 : 1;
        
        for (int col = 1; col <= 5; col++) {
            final int currentCol = col;
            Node cell = board.getChildren().stream()
                .filter(n -> {
                    Integer rowIdx = GridPane.getRowIndex(n);
                    Integer colIdx = GridPane.getColumnIndex(n);
                    int row = (rowIdx == null) ? 0 : rowIdx;
                    int column = (colIdx == null) ? 0 : colIdx;
                    return row == monsterRow && column == currentCol;
                })
                .findFirst()
                .orElse(null);
            
            if (cell instanceof StackPane) {
                StackPane zone = (StackPane) cell;
                List<Node> toRemove = new ArrayList<>();
                
                for (Node child : zone.getChildren()) {
                    if (child instanceof ImageView) {
                        ImageView view = (ImageView) child;
                        Object cardObj = view.getProperties().get("card");
                        if (cardObj instanceof MonsterCard) {
                            toRemove.add(child);
                            sendToGraveyard((MonsterCard)cardObj, view, board);
                        }
                    }
                }
                
                toRemove.forEach(node -> zone.getChildren().remove(node));
            }
        }
    }

    /**
     * Destroys the monster with the lowest ATK on the specified board.
     * <p>
     * This method scans all monster zones to find the monster with the minimum ATK
     * value, then removes it from the field and sends it to the graveyard. This
     * implements effects like "Fissure" and "Smashing Ground".
     * </p>
     * <p>
     * The search process:
     * <ol>
     * <li>Iterates through all 5 monster zones</li>
     * <li>Identifies MonsterCards in each zone</li>
     * <li>Tracks the monster with the lowest ATK value</li>
     * <li>Destroys that monster if one is found</li>
     * </ol>
     * </p>
     * <p>
     * If multiple monsters have the same lowest ATK, the first one encountered is
     * destroyed. If no monsters are on the field, the method does nothing.
     * </p>
     * <p>
     * Debug information about the destroyed monster (name and ATK) is printed to
     * the console.
     * </p>
     *
     * @param board the GridPane whose weakest monster should be destroyed
     */
    public void destroyWeakestMonster(GridPane board) {
        int monsterRow = (board == boardJoueur1) ? 0 : 1;
        MonsterCard weakest = null;
        ImageView weakestView = null;
        StackPane weakestZone = null;
        int minAtk = Integer.MAX_VALUE;
        
        for (int col = 1; col <= 5; col++) {
            final int currentCol = col;
            Node cell = board.getChildren().stream()
                .filter(n -> {
                    Integer rowIdx = GridPane.getRowIndex(n);
                    Integer colIdx = GridPane.getColumnIndex(n);
                    int row = (rowIdx == null) ? 0 : rowIdx;
                    int column = (colIdx == null) ? 0 : colIdx;
                    return row == monsterRow && column == currentCol;
                })
                .findFirst()
                .orElse(null);
            
            if (cell instanceof StackPane) {
                StackPane zone = (StackPane) cell;
                for (Node child : zone.getChildren()) {
                    if (child instanceof ImageView) {
                        ImageView view = (ImageView) child;
                        Object cardObj = view.getProperties().get("card");
                        if (cardObj instanceof MonsterCard) {
                            MonsterCard monster = (MonsterCard) cardObj;
                            if (monster.getAtk() < minAtk) {
                                minAtk = monster.getAtk();
                                weakest = monster;
                                weakestView = view;
                                weakestZone = zone;
                            }
                        }
                    }
                }
            }
        }
        
        if (weakest != null && weakestView != null && weakestZone != null) {
            weakestZone.getChildren().remove(weakestView);
            sendToGraveyard(weakest, weakestView, board);
            System.out.println("Monster destroyed: " + weakest.getName() + " (ATK: " + weakest.getAtk() + ")");
        }
    }

    
        /**
     * Initializes and displays the complete game UI for a Yu-Gi-Oh! duel.
     * <p>
     * This is the main entry point of the JavaFX application. It performs extensive
     * setup to create a fully functional duel board with all necessary components,
     * game logic, and user interactions. The method builds the entire UI hierarchy
     * and configures all event handlers for gameplay.
     * </p>
     * <p>
     * <strong>Initialization Steps:</strong>
     * <ol>
     * <li><strong>Players and Game:</strong> Creates Player objects for Yugi and Kaiba,
     *     initializes the Game controller</li>
     * <li><strong>Life Points System:</strong> Sets up LP labels, input fields, and +/-
     *     toggle buttons for manual LP adjustment during the duel</li>
     * <li><strong>Phase Management:</strong> Creates phase indicator, turn counter, and
     *     buttons for each phase (DP, SP, MP1, BP, MP2, EP) with "End Turn" button</li>
     * <li><strong>Game Boards:</strong> Builds both player and opponent boards with all
     *     zones using {@link #buildPlayerBoard}</li>
     * <li><strong>Graveyard Interaction:</strong> Sets up click handlers on graveyard zones
     *     to open popup windows via {@link #showGraveyardPopup}</li>
     * <li><strong>Deck Loading:</strong> Loads Starter Deck: Yugi and Starter Deck: Kaiba
     *     from YGOPRODeck API, shuffles them, and draws initial 5-card hands</li>
     * <li><strong>Deck Interaction:</strong> Configures deck zones with:
     *     <ul>
     *     <li>Left-click to draw a card</li>
     *     <li>Right-click to view entire deck (via {@link #showDeckPopup})</li>
     *     <li>Card counter display showing remaining cards</li>
     *     </ul>
     * </li>
     * <li><strong>Card Info and Actions:</strong> Creates info display area and action
     *     button zone on the right side</li>
     * <li><strong>Bot Integration:</strong> Initializes SimpleBot with joueur2 and enables
     *     automatic bot play when it's their turn</li>
     * <li><strong>Music:</strong> Starts background duel music</li>
     * <li><strong>Styling:</strong> Applies CSS stylesheet for visual theming</li>
     * </ol>
     * </p>
     * <p>
     * <strong>Layout Structure:</strong>
     * <pre>
     * BorderPane root
     * ├── Top: Life Points bar (LP labels + inputs + toggle buttons)
     * ├── Center: VBox
     * │   ├── Opponent's hand (card backs, hidden)
     * │   ├── Opponent's board (ScrollPane)
     * │   ├── Phase zone (player indicator + phase label + turn counter + phase buttons)
     * │   └── Player's board (ScrollPane)
     * ├── Right: Action Zone (dynamically generated buttons)
     * └── Bottom: VBox
     *     ├── Player's hand (visible cards)
     *     └── Card info area (TextArea)
     * </pre>
     * </p>
     * <p>
     * <strong>Life Points System:</strong>
     * Players can manually adjust life points using text fields and +/- toggle buttons.
     * This is useful for:
     * <ul>
     * <li>Correcting life points after mistakes</li>
     * <li>Applying card effect damage/healing that isn't automated</li>
     * <li>Testing specific game scenarios</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Phase Buttons:</strong>
     * Each phase has a dedicated button. The current phase is highlighted in light blue.
     * Clicking a phase button:
     * <ul>
     * <li>Changes the game phase immediately</li>
     * <li>Updates the phase label</li>
     * <li>Refreshes action buttons for the selected card (if any)</li>
     * <li>May enable/disable certain actions (e.g., Attack only in Battle Phase)</li>
     * </ul>
     * </p>
     * <p>
     * <strong>End Turn Behavior:</strong>
     * When "End Turn" is clicked:
     * <ul>
     * <li>Resets attack flags for all monsters on both fields</li>
     * <li>Calls {@link Game#endTurn()} to switch players</li>
     * <li>Updates turn counter and player indicator</li>
     * <li>Resets to Draw Phase with button highlighting</li>
     * <li>If bot is enabled and it's joueur2's turn, bot automatically plays with
     *     a 4.1-second delay using PauseTransition for UI updates</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Deck Clicking Behavior:</strong>
     * <ul>
     * <li><strong>Player's deck (left-click):</strong> Draws a card, shows it face-up,
     *     adds to hand with full interaction</li>
     * <li><strong>Player's deck (right-click):</strong> Opens deck viewer popup</li>
     * <li><strong>Opponent's deck (click):</strong> Draws a card as card back (hidden),
     *     adds to opponent's hand. Player cannot see what was drawn.</li>
     * </ul>
     * </p>
     * <p>
     * <strong>Card Loading:</strong>
     * Decks are loaded from YGOPRODeck API using specific starter deck URLs. If loading
     * fails, empty decks and hands are created to prevent crashes. Error messages are
     * printed to stderr for debugging.
     * </p>
     * <p>
     * <strong>Graveyard and Deck Zones:</strong>
     * Special handling for graveyard zones which may be in an HBox (opponent's side).
     * Debug messages are printed to console to verify proper detection and event handler
     * attachment.
     * </p>
     * <p>
     * <strong>Bot Automatic Play:</strong>
     * When bot is enabled and joueur2's turn starts, the bot automatically executes its
     * turn via {@link SimpleBot#playTurn()}. A PauseTransition ensures the UI updates
     * correctly after the bot finishes (4.1 seconds to account for bot action delays).
     * </p>
     *
     * @param stage the primary Stage provided by JavaFX for displaying the game window
     */
    @Override
    public void start(Stage stage) {
        // === Players and Game ===
        joueur1 = new Player("Yugi");
        joueur2 = new Player("Kaiba");

        game = new Game(joueur1, joueur2);

        BorderPane root = new BorderPane();

        // === Life Points ===
        lpJoueur1 = new Label(joueur1.getName() + " LP: " + lp1[0]);
        lpJoueur1.getStyleClass().addAll("lp-label", "lp-j1");

        lpJoueur2 = new Label(joueur2.getName() + " LP: " + lp2[0]);
        lpJoueur2.getStyleClass().addAll("lp-label", "lp-j2");

        TextField input1 = new TextField();
        input1.setPromptText("Amount " + joueur1.getName());
        input1.getStyleClass().add("lp-input");

        TextField input2 = new TextField();
        input2.setPromptText("Amount " + joueur2.getName());
        input2.getStyleClass().add("lp-input");

        Button toggle1 = new Button("+");
        Button toggle2 = new Button("+");
        toggle1.getStyleClass().add("lp-toggle");
        toggle2.getStyleClass().add("lp-toggle");

        toggle1.setOnAction(e -> toggle1.setText(toggle1.getText().equals("+") ? "-" : "+"));
        toggle2.setOnAction(e -> toggle2.setText(toggle2.getText().equals("+") ? "-" : "+"));

        input1.setOnAction(e -> {
            try {
                int val = Integer.parseInt(input1.getText());
                lp1[0] += toggle1.getText().equals("+") ? val : -val;
                lpJoueur1.setText(joueur1.getName() + " LP: " + lp1[0]);
                input1.clear();
            } catch (NumberFormatException ex) {
                input1.setText("Error");
            }
        });

        input2.setOnAction(e -> {
            try {
                int val = Integer.parseInt(input2.getText());
                lp2[0] += toggle2.getText().equals("+") ? val : -val;
                lpJoueur2.setText(joueur2.getName() + " LP: " + lp2[0]);
                input2.clear();
            } catch (NumberFormatException ex) {
                input2.setText("Error");
            }
        });

        HBox lpBox = new HBox(50, lpJoueur1, input1, toggle1, lpJoueur2, input2, toggle2);
        lpBox.setAlignment(Pos.CENTER);
        lpBox.getStyleClass().add("lp-bar");

        // === Phases / Turn ===
        phaseLabel = new Label("Current Phase: " + game.getCurrentPhase().getLabel());
        phaseLabel.getStyleClass().add("phase-label");

        Label joueurActif = new Label(game.getCurrentPlayer().getName() + "'s Turn");
        joueurActif.getStyleClass().add("active-player");

        Label tourLabel = new Label("Turn: " + game.getTurnCount());
        tourLabel.getStyleClass().add("tour-label");

        HBox phaseBox = new HBox(20);
        phaseBox.setAlignment(Pos.CENTER);

        for (Phase phase : Phase.values()) {
            Button phaseButton = new Button(phase.name());
            phaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            if (phase == game.getCurrentPhase()) {
                phaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: lightblue;");
            }

            phaseButton.setOnAction(e -> {
                game.setPhase(phase);
                phaseLabel.setText("Current Phase: " + game.getCurrentPhase().getLabel());
                phaseBox.getChildren().forEach(node -> node.setStyle("-fx-font-size: 14px; -fx-padding: 10;"));
                phaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: lightblue;");

                // Rebuild action zone if a card is selected
                if (selectedCard != null && selectedView != null) {
                    createActionButtonsForPlacedCard(
                        selectedCard, selectedView, null, null, cardInfoArea, actionZone,
                        Boolean.TRUE.equals(selectedView.getProperties().get("isOpponent"))
                    );
                }
            });

            phaseBox.getChildren().add(phaseButton);
        }

        Button finTour = new Button("End Turn");
        finTour.getStyleClass().add("button-fin-tour");

        finTour.setOnAction(e -> {
            // Reset attack flags for all monsters on both fields
            resetAttackFlags(boardJoueur1);
            resetAttackFlags(opponentBoard);

            game.endTurn();
            joueurActif.setText(game.getCurrentPlayer().getName() + "'s Turn");
            phaseLabel.setText("Current Phase: " + game.getCurrentPhase().getLabel());
            tourLabel.setText("Turn: " + game.getTurnCount());
            
            phaseBox.getChildren().forEach(node -> node.setStyle("-fx-font-size: 14px; -fx-padding: 10;"));
            phaseBox.getChildren().stream()
                .filter(n -> n instanceof Button && ((Button) n).getText().equals(Phase.DP.name()))
                .findFirst()
                .ifPresent(n -> n.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: lightblue;"));
            
            if (isBotEnabled && game.getCurrentPlayer() == joueur2) {
                bot.playTurn();
                
                // Update UI AFTER bot's turn completes
                // Wait for bot's Timeline to finish
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(4100));
                pause.setOnFinished(event -> {
                    joueurActif.setText(game.getCurrentPlayer().getName() + "'s Turn");
                    phaseLabel.setText("Current Phase: " + game.getCurrentPhase().getLabel());
                    tourLabel.setText("Turn: " + game.getTurnCount());
                    
                    phaseBox.getChildren().forEach(node -> node.setStyle("-fx-font-size: 14px; -fx-padding: 10;"));
                    phaseBox.getChildren().stream()
                        .filter(n -> n instanceof Button && ((Button) n).getText().equals(Phase.DP.name()))
                        .findFirst()
                        .ifPresent(n -> n.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: lightblue;"));
                });
                pause.play();
            }
        });

        VBox phaseZone = new VBox(5, joueurActif, phaseLabel, tourLabel, phaseBox, finTour);
        phaseZone.setAlignment(Pos.CENTER);

        // === Boards (global fields) ===
        boardJoueur1 = buildPlayerBoard("Player", false);
        opponentBoard = buildPlayerBoard("Opponent", true);

        // === Connect clicks on graveyards ===

        // Player 1's graveyard
        StackPane graveyardJ1 = (StackPane) boardJoueur1.getChildren().stream()
            .filter(node -> node.getStyleClass().contains("graveyard-zone"))
            .findFirst()
            .orElse(null);

        if (graveyardJ1 != null) {
            graveyardJ1.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    showGraveyardPopup(graveyardJ1, joueur1.getName(), false);
                }
            });
            graveyardJ1.setStyle(graveyardJ1.getStyle() + "; -fx-cursor: hand;");
        }

        // Player 2's graveyard (in HBox for opponent)
        StackPane graveyardJ2 = null;
        for (Node node : opponentBoard.getChildren()) {
            if (node instanceof HBox) {
                for (Node child : ((HBox) node).getChildren()) {
                    if (child.getStyleClass().contains("graveyard-zone") && child instanceof StackPane) {
                        graveyardJ2 = (StackPane) child;
                        System.out.println("Opponent's graveyard found!");
                        break;
                    }
                }
            }
            if (graveyardJ2 != null) break;
        }

        if (graveyardJ2 != null) {
            StackPane finalGraveyardJ2 = graveyardJ2;
            
            // Add style to show zone is clickable
            graveyardJ2.setStyle(graveyardJ2.getStyle() + "; -fx-cursor: hand; -fx-border-color: red; -fx-border-width: 2px;");
            
            graveyardJ2.setOnMouseClicked(e -> {
                System.out.println("Click detected on opponent's graveyard!");
                if (e.getButton() == MouseButton.PRIMARY) {
                    showGraveyardPopup(finalGraveyardJ2, joueur2.getName(), true);
                }
            });
            
            System.out.println("Click handler added to opponent's graveyard");
        } else {
            System.out.println("Opponent's graveyard not found!");
        }

        // === Card info display area ===
        cardInfoArea = new TextArea();
        cardInfoArea.setEditable(false);
        cardInfoArea.setPrefHeight(120);
        cardInfoArea.setWrapText(true);
        cardInfoArea.getStyleClass().add("card-info");

        // === Action zone on right ===
        actionZone = new VBox(10);
        actionZone.setAlignment(Pos.TOP_CENTER);
        actionZone.getStyleClass().add("action-zone");
        root.setRight(actionZone);

        // === Decks and hands ===
        try {
            // Yugi's Deck
            List<Card> deckCardsJ1 = CardLoader.loadCardsFromSet(
                "https://db.ygoprodeck.com/api/v7/cardinfo.php?cardset=starter%20deck:%20yugi",
                joueur1
            );
            deckJ1 = new Deck(deckCardsJ1);
            deckJ1.shuffle();

            // Assign deck to joueur1
            joueur1.setDeck(deckJ1);

            List<Card> mainJoueur1 = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Card c = deckJ1.draw();
                if (c != null) mainJoueur1.add(c);
            }
            handJ1 = buildPlayerHandFromCards(mainJoueur1, boardJoueur1, false, cardInfoArea, actionZone);
            handJ1.setPrefHeight(150);

            List<Card> deckCardsJ2 = CardLoader.loadCardsFromSet(
                "https://db.ygoprodeck.com/api/v7/cardinfo.php?cardset=starter%20deck:%20kaiba",
                joueur2
            );
            deckJ2 = new Deck(deckCardsJ2);
            deckJ2.shuffle();

            // Assign deck to joueur2
            joueur2.setDeck(deckJ2);

            List<Card> mainJoueur2 = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Card c = deckJ2.draw();
                if (c != null) mainJoueur2.add(c);
            }
            handJ2 = buildPlayerHandFromCards(mainJoueur2, opponentBoard, true, cardInfoArea, actionZone);
            handJ2.setPrefHeight(150);

        } catch (Exception e) {
            System.err.println("Error loading decks: " + e.getMessage());
            deckJ1 = new Deck();
            deckJ2 = new Deck();
            handJ1 = new HBox();
            handJ2 = new HBox();
        }

        // Declare variable before using it
        Label deckCountJ1 = null;

        // === Connect Player 1's Deck zone (counter + draw) ===
        StackPane deckZoneJ1 = (StackPane) boardJoueur1.getChildren().stream()
            .filter(node -> node.getStyleClass().contains("deck-zone"))
            .findFirst()
            .orElse(null);

        // Right-click on deck to view entire deck
        if (deckZoneJ1 != null) {
            deckCountJ1 = (Label) deckZoneJ1.getChildren().stream()
                .filter(node -> node.getStyleClass().contains("deck-count"))
                .findFirst()
                .orElse(null);

            if (deckCountJ1 != null) {
                deckCountJ1.setText("Cards: " + deckJ1.size());
            }

            // Create final variable for use in lambda
            final Label finalDeckCountJ1 = deckCountJ1;

            deckZoneJ1.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    Card drawn = deckJ1.draw();
                    if (drawn != null) {
                        ImageView view = new ImageView(drawn.getImage());
                        view.setFitWidth(88);
                        view.setFitHeight(132);
                        handJ1.getChildren().add(view);

                        view.setOnMouseClicked(ev -> {
                            selectCard(view, boardJoueur1, handJ1, cardInfoArea, drawn);
                            createActionButtonsForCard(drawn, view, boardJoueur1, handJ1, cardInfoArea, actionZone, false);
                        });

                        cardInfoArea.setText(drawn.toString());
                        if (finalDeckCountJ1 != null) {
                            finalDeckCountJ1.setText("Cards: " + deckJ1.size());
                        }
                    }
                } else if (e.getButton() == MouseButton.SECONDARY) {
                    showDeckPopup(deckJ1, joueur1.getName());
                }
            });
        }

        // === Connect Opponent's Deck zone (counter + draw with card back) ===
        StackPane deckZoneJ2 = (StackPane) opponentBoard.getChildren().stream()
            .filter(node -> node.getStyleClass().contains("deck-zone"))
            .findFirst()
            .orElse(null);

        if (deckZoneJ2 != null) {
            Label deckCountJ2 = (Label) deckZoneJ2.getChildren().stream()
                .filter(node -> node.getStyleClass().contains("deck-count"))
                .findFirst()
                .orElse(null);

            if (deckCountJ2 != null) deckCountJ2.setText("Cards: " + deckJ2.size());

            deckZoneJ2.setOnMouseClicked(e -> {
                Card drawn = deckJ2.draw();
                if (drawn != null) {
                    try {
                        Image backImg = new Image(getClass().getResource("/ui/card_back.png").toExternalForm());
                        ImageView back = new ImageView(backImg);
                        back.setFitWidth(88);
                        back.setFitHeight(132);
                        back.setRotate(180);

                        // Properties
                        back.getProperties().put("faceDown", true);
                        back.getProperties().put("isOpponent", true);
                        back.getProperties().put("position", "ATK"); // default

                        // Click on opponent's card in hand (back visible)
                        back.setOnMouseClicked(ev -> {
                            if (attackMode && attackerCard != null && attackerView != null && drawn instanceof MonsterCard) {
                                // Clicked card is the target
                                resolveBattle(attackerCard, attackerView, (MonsterCard) drawn, back, true);
                                // Reset attack mode
                                attackMode = false;
                                attackerCard = null;
                                attackerView = null;
                            } else {
                                // Normal selection
                                selectCard(back, opponentBoard, handJ2, cardInfoArea, drawn);
                                createActionButtonsForCard(drawn, back, opponentBoard, handJ2, cardInfoArea, actionZone, true);
                            }
                        });

                        handJ2.getChildren().add(back);

                    } catch (Exception ex) {
                        // Fallback: face-up if card back image unavailable
                        ImageView view = new ImageView(drawn.getImage());
                        view.setFitWidth(88);
                        view.setFitHeight(132);
                        view.setRotate(180);

                        view.getProperties().put("faceDown", false);
                        view.getProperties().put("isOpponent", true);
                        view.getProperties().put("position", "ATK");

                        // Click on opponent's card in hand (face visible)
                        view.setOnMouseClicked(ev -> {
                            if (attackMode && attackerCard != null && attackerView != null && drawn instanceof MonsterCard) {
                                resolveBattle(attackerCard, attackerView, (MonsterCard) drawn, view, true);
                                attackMode = false;
                                attackerCard = null;
                                attackerView = null;
                            } else {
                                selectCard(view, opponentBoard, handJ2, cardInfoArea, drawn);
                                createActionButtonsForCard(drawn, view, opponentBoard, handJ2, cardInfoArea, actionZone, true);
                            }
                        });

                        handJ2.getChildren().add(view);
                    }

                    if (deckCountJ2 != null) deckCountJ2.setText("Cards: " + deckJ2.size());
                }
            });
        }

        // === Scroll around boards ===
        ScrollPane scrollBoardJ1 = new ScrollPane(boardJoueur1);
        scrollBoardJ1.setFitToWidth(true);
        scrollBoardJ1.setFitToHeight(true);
        scrollBoardJ1.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollBoardJ1.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollBoardJ1.getStyleClass().add("dark-scroll");

        ScrollPane scrollBoardJ2 = new ScrollPane(opponentBoard);
        scrollBoardJ2.setFitToWidth(true);
        scrollBoardJ2.setFitToHeight(true);
        scrollBoardJ2.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollBoardJ2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollBoardJ2.getStyleClass().add("dark-scroll");

        // === Assembly ===
        VBox boards = new VBox(20, handJ2, scrollBoardJ2, phaseZone, scrollBoardJ1);
        boards.setAlignment(Pos.CENTER);

        root.setTop(lpBox);       // Life points
        root.setCenter(boards);   // Boards + phases
        root.setRight(actionZone);
        root.setBottom(new VBox(10, handJ1, cardInfoArea)); // J1 hand + info

        // After creating joueur1, joueur2, game, etc.
        bot = new SimpleBot(game, joueur2, opponentBoard, handJ2, this);
        isBotEnabled = true; // Enable bot

        Scene scene = new Scene(root, 1200, 950);
        scene.getStylesheets().add(getClass().getResource("/ui/board.css").toExternalForm());

        // Start duel music
        MusicPlayer.playMusic("/audio/Main_Song_2.mp3");

        stage.setScene(scene);
        stage.setTitle("Yu-Gi-Oh! Duel Board");
        stage.show();
    }

    /**
     * Application entry point for the Yu-Gi-Oh! duel board.
     * <p>
     * This method launches the JavaFX application, which invokes {@link #start(Stage)}
     * to build and display the game interface.
     * </p>
     * <p>
     * <strong>Usage:</strong>
     * <pre>
     * java ui.DuelBoard
     * </pre>
     * </p>
     *
     * @param args command-line arguments (not currently used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}