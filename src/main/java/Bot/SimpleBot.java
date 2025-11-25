package Bot;

import Card.Card;
import Card.MonsterCard;
import Deck.Deck;
import Game.Game;
import Game.Phase;
import Player.Player;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ui.DuelBoard;

/**
 * Simple AI bot for playing Yu-Gi-Oh! duels against the player.
 * <p>
 * This bot implements basic Yu-Gi-Oh! gameplay logic, automatically executing turns
 * by progressing through all phases, drawing cards, summoning monsters, and declaring
 * attacks. It follows official Yu-Gi-Oh! turn structure and implements fundamental
 * strategies for single-player gameplay.
 
 * <p>
 * The bot's behavior is designed to be predictable and fair, providing a reasonable
 * opponent for players learning the game or testing decks. It makes decisions based
 * on simple heuristics rather than advanced AI algorithms.
 
 * <p>
 * <strong>Bot Capabilities:</strong>
 * <ul>
 * <li>Automatically progresses through all turn phases with timed delays</li>
 * <li>Draws cards during the Draw Phase</li>
 * <li>Summons monsters from hand (Normal Summon in Main Phase)</li>
 * <li>Declares attacks with monsters during Battle Phase</li>
 * <li>Manages its own board, hand, and deck</li>
 * <li>Respects game rules (one Normal Summon per turn, Battle Phase restrictions, etc.)</li>
 * </ul>
 
 * <p>
 * <strong>Turn Execution:</strong>
 * The bot uses a JavaFX Timeline to execute phases sequentially with delays between
 * each phase, creating a more natural and observable gameplay experience:
 * <ul>
 * <li>Draw Phase: 0ms (immediate)</li>
 * <li>Standby Phase: 800ms</li>
 * <li>Main Phase 1: 1600ms</li>
 * <li>Battle Phase: 2400ms</li>
 * <li>Main Phase 2: 3200ms</li>
 * <li>End Phase: 4000ms</li>
 * </ul>
 * Total turn duration: approximately 4 seconds.
 
 * <p>
 * <strong>Card Visibility:</strong>
 * The bot's cards are displayed as card backs (rotated 180°) to hide them from the
 * player, maintaining the hidden information aspect of Yu-Gi-Oh! gameplay. When cards
 * are played to the field, they become visible.
 
 * <p>
 * <strong>Integration:</strong>
 * The bot requires a reference to the {@link DuelBoard} to interact with the UI and
 * resolve battles with the player's monsters. It manages its own {@link Player} object
 * and associated game zones (board, hand, deck).
 
 * <p>
 * <strong>Future Enhancements:</strong>
 * The bot can be extended to:
 * <ul>
 * <li>Play spell and trap cards</li>
 * <li>Change monster positions strategically</li>
 * <li>Implement card effect logic</li>
 * <li>Use more advanced decision-making algorithms</li>
 * <li>Adjust difficulty levels</li>
 * </ul>
 
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class SimpleBot {

  /** The game controller managing the duel state */
  private Game game;

  /** The Player object representing the bot */
  private Player botPlayer;

  /** The GridPane representing the bot's game board */
  private GridPane botBoard;

  /** The HBox containing the bot's hand (displayed as card backs) */
  private HBox botHand;

  /** Flag tracking whether the bot has used its Normal Summon this turn */
  private boolean hasNormalSummonedThisTurn = false;

  /** Reference to the main DuelBoard for UI interactions and battle resolution */
  private DuelBoard duelBoard;

  /**
   * Creates a new SimpleBot with the necessary game components.
   * <p>
   * The bot is initialized with references to the game state, its player object,
   * its visual zones (board and hand), and the main duel board for interactions.
   
   *
   * @param game the Game controller managing the duel
   * @param botPlayer the Player object representing the bot
   * @param botBoard the GridPane where the bot places its cards
   * @param botHand the HBox displaying the bot's hand (as card backs)
   * @param duelBoard the main DuelBoard for UI updates and battle resolution
   */
  public SimpleBot(
    Game game,
    Player botPlayer,
    GridPane botBoard,
    HBox botHand,
    DuelBoard duelBoard
  ) {
    this.game = game;
    this.botPlayer = botPlayer;
    this.botBoard = botBoard;
    this.botHand = botHand;
    this.duelBoard = duelBoard;
  }

  /**
   * Executes the bot's complete turn through all phases.
   * <p>
   * This method orchestrates the bot's turn by creating a Timeline with KeyFrames
   * for each phase. Each phase is executed at a specific time delay, allowing the
   * player to observe the bot's actions in real-time rather than seeing everything
   * happen instantly.
   
   * <p>
   * <strong>Phase Sequence:</strong>
   * <ol>
   * <li><strong>Draw Phase (0ms):</strong> Bot draws a card from its deck</li>
   * <li><strong>Standby Phase (800ms):</strong> Trigger effects, currently no actions</li>
   * <li><strong>Main Phase 1 (1600ms):</strong> Bot summons monsters and sets cards</li>
   * <li><strong>Battle Phase (2400ms):</strong> Bot declares attacks with its monsters</li>
   * <li><strong>Main Phase 2 (3200ms):</strong> Additional summons/sets after battle</li>
   * <li><strong>End Phase (4000ms):</strong> End turn cleanup, turn passes back</li>
   * </ol>
   
   * <p>
   * The Normal Summon flag is reset at the start of the turn, allowing the bot to
   * summon one monster during this turn.
   
   * <p>
   * Debug output is printed to the console at the start and end of the turn for
   * tracking bot behavior.
   
   */
  public void playTurn() {
    System.out.println("=== Bot's Turn ===");

    // Reset Normal Summon flag
    hasNormalSummonedThisTurn = false;

    // Create action sequence with delays
    javafx.animation.Timeline timeline = new javafx.animation.Timeline();

    // Draw Phase at t=0
    timeline
      .getKeyFrames()
      .add(
        new javafx.animation.KeyFrame(javafx.util.Duration.millis(0), event ->
          drawPhase()
        )
      );

    // Standby Phase at t=800ms
    timeline
      .getKeyFrames()
      .add(
        new javafx.animation.KeyFrame(javafx.util.Duration.millis(800), event ->
          stanbyPhase()
        )
      );

    // Main Phase at t=1600ms
    timeline
      .getKeyFrames()
      .add(
        new javafx.animation.KeyFrame(
          javafx.util.Duration.millis(1600),
          event -> mainPhase1()
        )
      );

    // Battle Phase at t=2400ms
    timeline
      .getKeyFrames()
      .add(
        new javafx.animation.KeyFrame(
          javafx.util.Duration.millis(2400),
          event -> battlePhase()
        )
      );

    // Main Phase 2 at t=3200ms
    timeline
      .getKeyFrames()
      .add(
        new javafx.animation.KeyFrame(
          javafx.util.Duration.millis(3200),
          event -> mainPhase2()
        )
      );

    // End Phase at t=4000ms
    timeline
      .getKeyFrames()
      .add(
        new javafx.animation.KeyFrame(
          javafx.util.Duration.millis(4000),
          event -> {
            endPhase();
            System.out.println("=== Bot's Turn Ended ===");
          }
        )
      );

    timeline.play();
  }

  /**
   * Pauses execution for the specified duration.
   * <p>
   * This utility method is used to introduce delays between bot actions for a more
   * natural gameplay experience. It uses Thread.sleep() internally.
   
   * <p>
   * <strong>Note:</strong> This method is currently unused in favor of JavaFX Timeline
   * for better UI thread safety. Using Thread.sleep() on the JavaFX Application Thread
   * would freeze the UI, so Timeline with KeyFrames is preferred.
   
   *
   * @param milliseconds the duration to pause in milliseconds
   * @deprecated Use JavaFX Timeline with KeyFrames instead for better UI responsiveness
   */
  @Deprecated
  public void pause(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Executes the bot's Draw Phase.
   * <p>
   * During the Draw Phase, the bot:
   * <ol>
   * <li>Sets the game phase to Draw Phase (DP)</li>
   * <li>Draws one card from its deck via {@link Player#getDeck()}</li>
   * <li>Creates a card back ImageView (rotated 180°) to hide the card</li>
   * <li>Adds the card to the bot's hand display</li>
   * <li>Updates the deck counter on the board</li>
   * <li>Logs the drawn card name and hand/deck sizes to console</li>
   * </ol>
   
   * <p>
   * The card is stored with the following properties:
   * <ul>
   * <li>"card": The actual Card object</li>
   * <li>"faceDown": true (hidden from player)</li>
   * <li>"isOpponent": true (identifies as opponent's card)</li>
   * <li>"position": "ATK" (default position)</li>
   * </ul>
   
   * <p>
   * If the card back image cannot be loaded or the deck is empty, appropriate error
   * messages are printed to the console.
   
   * <p>
   * <strong>Note:</strong> In official Yu-Gi-Oh! rules, the first turn player does
   * not draw during their first Draw Phase. This implementation does not enforce
   * that restriction.
   
   */
  public void drawPhase() {
    System.out.println("Bot: Draw Phase");
    game.setPhase(Phase.DP);

    Card drawn = botPlayer.getDeck().draw();
    if (drawn != null) {
      try {
        Image backImg = new Image(
          getClass().getResource("/ui/card_back.png").toExternalForm()
        );
        ImageView back = new ImageView(backImg);
        back.setFitWidth(88);
        back.setFitHeight(132);
        back.setRotate(180);

        back.getProperties().put("card", drawn);
        back.getProperties().put("faceDown", true);
        back.getProperties().put("isOpponent", true);
        back.getProperties().put("position", "ATK");

        // Direct addition, without Platform.runLater
        botHand.getChildren().add(back);
        updateDeckCount(botBoard, botPlayer.getDeck());

        System.out.println("Bot drew: " + drawn.getName());
        System.out.println(
          "Hand: " +
          botHand.getChildren().size() +
          ", Deck: " +
          botPlayer.getDeck().size()
        );
      } catch (Exception ex) {
        System.err.println("Bot draw error: " + ex.getMessage());
        ex.printStackTrace();
      }
    }
  }

  /**
   * Updates the deck card counter display on the bot's board.
   * <p>
   * This helper method finds the deck zone on the bot's board and updates its
   * counter label to reflect the current number of cards remaining in the deck.
   * This provides visual feedback to the player about the bot's deck size.
   
   * <p>
   * The method searches for a StackPane with the "deck-zone" CSS class, then
   * locates a child Label with the "deck-count" CSS class and updates its text.
   
   * <p>
   * If either the deck zone or counter label cannot be found, the update fails
   * silently without throwing exceptions.
   
   *
   * @param board the GridPane representing the bot's game board
   * @param deck the Deck whose size should be displayed
   */
  public void updateDeckCount(GridPane board, Deck deck) {
    StackPane deckZone = (StackPane) board
      .getChildren()
      .stream()
      .filter(node -> node.getStyleClass().contains("deck-zone"))
      .findFirst()
      .orElse(null);

    if (deckZone != null) {
      Label deckCount = (Label) deckZone
        .getChildren()
        .stream()
        .filter(node -> node.getStyleClass().contains("deck-count"))
        .findFirst()
        .orElse(null);

      if (deckCount != null) {
        deckCount.setText("Cards: " + deck.size());
      }
    }
  }

  /**
   * Executes the bot's Standby Phase.
   * <p>
   * During the Standby Phase, certain card effects trigger or resolve. Currently,
   * this implementation only sets the game phase to Standby Phase without performing
   * any additional actions.
   
   * <p>
   * <strong>Future Enhancements:</strong>
   * This phase could be extended to handle:
   * <ul>
   * <li>Continuous card effects that trigger during Standby Phase</li>
   * <li>Maintenance costs for certain cards</li>
   * <li>Special card effects that activate at this timing</li>
   * </ul>
   
   */
  public void stanbyPhase() {
    System.out.println("Bot: Standby Phase");
    game.setPhase(Phase.SP);
    // Drawing will be handled manually in DuelBoard for now
  }

  /**
   * Executes the bot's Main Phase 1.
   * <p>
   * During Main Phase 1, the bot attempts to summon monsters from its hand using
   * intelligent decision-making based on monster levels, tribute requirements, and
   * the current board state. The bot can perform Normal Summons (including tribute
   * summons) and set monsters face-down in Defense Position.
   
   * <p>
   * <strong>Summoning Logic:</strong>
   * <ol>
   * <li>Checks if the bot has already Normal Summoned this turn</li>
   * <li>Finds the best summonable monster from hand via {@link #findBestSummonableMonster()}</li>
   * <li>Determines if the monster should be set defensively via {@link #shouldPlayDefensive(MonsterCard)}</li>
   * <li>Handles summoning based on monster level:
   *     <ul>
   *     <li><strong>Level 1-4:</strong> Normal Summon without tributes (or set defensively)</li>
   *     <li><strong>Level 5-6:</strong> Tribute Summon with 1 tribute if available</li>
   *     <li><strong>Level 7+:</strong> Tribute Summon with 2 tributes if available</li>
   *     </ul>
   * </li>
   * <li>Sets the Normal Summon flag to prevent multiple summons</li>
   * </ol>
   
   * <p>
   * <strong>Defensive Play:</strong>
   * If {@link #shouldPlayDefensive} returns true (when the player has strong monsters),
   * the bot will set weak monsters face-down in Defense Position instead of summoning
   * them in Attack Position, protecting its life points.
   
   * <p>
   * All summoning attempts are logged to the console with details about level,
   * tributes used, and success/failure reasons.
   
   */
  public void mainPhase1() {
    System.out.println("Bot: Main Phase 1");
    game.setPhase(Phase.MP1);

    if (hasNormalSummonedThisTurn) {
      System.out.println("Bot has already Normal Summoned this turn");
      return;
    }

    MonsterCard monsterToSummon = findBestSummonableMonster();

    if (monsterToSummon != null) {
      int level = monsterToSummon.getLevel();

      // Check if we should summon defensively
      boolean shouldSetInDefense = shouldPlayDefensive(monsterToSummon);

      if (level <= 4) {
        if (shouldSetInDefense) {
          System.out.println(
            "Bot sets in defense (level " +
            level +
            "): " +
            monsterToSummon.getName()
          );
          setMonsterInDefense(monsterToSummon);
        } else {
          System.out.println(
            "Bot summons (level " + level + "): " + monsterToSummon.getName()
          );
          summonMonster(monsterToSummon);
        }
        hasNormalSummonedThisTurn = true;
      } else if (level <= 6) {
        if (canTribute(1)) {
          System.out.println(
            "Bot tribute summons with 1 tribute (level " +
            level +
            "): " +
            monsterToSummon.getName()
          );
          tributeMonsters(1);
          summonMonster(monsterToSummon);
          hasNormalSummonedThisTurn = true;
        } else {
          System.out.println(
            "Bot cannot summon " +
            monsterToSummon.getName() +
            " (not enough monsters)"
          );
        }
      } else {
        if (canTribute(2)) {
          System.out.println(
            "Bot tribute summons with 2 tributes (level " +
            level +
            "): " +
            monsterToSummon.getName()
          );
          tributeMonsters(2);
          summonMonster(monsterToSummon);
          hasNormalSummonedThisTurn = true;
        } else {
          System.out.println(
            "Bot cannot summon " +
            monsterToSummon.getName() +
            " (not enough monsters)"
          );
        }
      }
    } else {
      System.out.println("Bot has no summonable monsters");
    }
  }

  /**
   * Determines whether the bot should play defensively.
   * <p>
   * This method evaluates the current board state to decide if the bot should set
   * a monster face-down in Defense Position rather than summoning it face-up in
   * Attack Position. The decision is based on:
   * <ul>
   * <li>The monster's ATK value (considers monsters with less than 1500 ATK as weak)</li>
   * <li>The strength of the player's monsters on the field</li>
   * </ul>
   
   * <p>
   * <strong>Defensive Strategy:</strong>
   * The bot will play defensively if:
   * <ul>
   * <li>The monster to summon has ATK less than 1500, AND</li>
   * <li>The player has at least one monster with ATK of 1500 or higher on the field</li>
   * </ul>
   * This prevents the bot from losing monsters unnecessarily to stronger opponents.
   
   *
   * @param monster the MonsterCard being considered for summoning
   * @return true if the monster should be set defensively, false if it should be summoned in Attack Position
   */
  public boolean shouldPlayDefensive(MonsterCard monster) {
    // If the monster has less than 1500 ATK
    if (monster.getAtk() < 1500) {
      // Check if the player has strong monsters
      List<MonsterInfo> playerMonsters = getPlayerMonstersOnField();

      for (MonsterInfo playerMonster : playerMonsters) {
        if (playerMonster.monster.getAtk() >= 1500) {
          // Player has a dangerous monster
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Sets a monster face-down in Defense Position on the bot's field.
   * <p>
   * This method performs the following operations:
   * <ol>
   * <li>Locates the monster's ImageView in the bot's hand</li>
   * <li>Finds an empty monster zone on the bot's board (row 1, columns 1-5)</li>
   * <li>Creates a new card back ImageView rotated 270° (horizontal face-down defense)</li>
   * <li>Stores card properties (card reference, faceDown=true, position=DEF, isOpponent=true)</li>
   * <li>Sets up a click handler via {@link DuelBoard#handleBotMonsterClick}</li>
   * <li>Places the card on the board and removes it from hand</li>
   * </ol>
   
   * <p>
   * <strong>Visual Representation:</strong>
   * Face-down defense monsters are displayed as card backs rotated 270° to indicate
   * horizontal position. The rotation differentiates them from face-down spell/trap
   * cards (which are upright) and face-up monsters (which are at 0° or 180°).
   
   * <p>
   * <strong>Click Interaction:</strong>
   * When the player clicks on a face-down bot monster, the click is handled by
   * {@link DuelBoard#handleBotMonsterClick}, which can flip the card face-up if
   * attacked or flip summoned.
   
   * <p>
   * If the monster cannot be found in hand, no empty zones are available, or an
   * error occurs loading the card back image, the operation fails with appropriate
   * error messages logged to the console.
   
   *
   * @param monster the MonsterCard to set face-down in Defense Position
   */
  public void setMonsterInDefense(MonsterCard monster) {
    ImageView monsterView = null;

    for (Node node : new ArrayList<>(botHand.getChildren())) {
      if (node instanceof ImageView) {
        ImageView view = (ImageView) node;
        Object cardObj = view.getProperties().get("card");
        if (cardObj == monster) {
          monsterView = view;
          break;
        }
      }
    }

    if (monsterView == null) return;

    // Find a free zone
    for (int col = 1; col <= 5; col++) {
      final int currentCol = col;
      Node cell = botBoard
        .getChildren()
        .stream()
        .filter(n -> {
          Integer rowIdx = GridPane.getRowIndex(n);
          Integer colIdx = GridPane.getColumnIndex(n);
          int row = (rowIdx == null) ? 0 : rowIdx;
          int column = (colIdx == null) ? 0 : colIdx;
          return row == 1 && column == currentCol;
        })
        .findFirst()
        .orElse(null);

      if (cell instanceof StackPane) {
        StackPane zone = (StackPane) cell;
        if (zone.getChildren().isEmpty()) {
          // Create face-down defense card
          try {
            Image backImg = new Image(
              getClass().getResource("/ui/card_back.png").toExternalForm()
            );
            ImageView placedView = new ImageView(backImg);
            placedView.setFitWidth(88);
            placedView.setFitHeight(132);
            placedView.setRotate(270); // 270° rotation = face-down defense

            placedView.getProperties().put("card", monster);
            placedView.getProperties().put("faceDown", true);
            placedView.getProperties().put("position", "DEF");
            placedView.getProperties().put("isOpponent", true);
            placedView.getProperties().put("placedCell", zone);

            // Click handler
            placedView.setOnMouseClicked(e -> {
              duelBoard.handleBotMonsterClick(
                placedView,
                monster,
                botBoard,
                botHand
              );
            });

            zone.getChildren().add(placedView);
            zone.getProperties().put("card", monster);

            botHand.getChildren().remove(monsterView);

            System.out.println(
              "Bot set " + monster.getName() + " face-down in DEF"
            );
          } catch (Exception ex) {
            System.err.println(
              "Error setting monster in defense: " + ex.getMessage()
            );
          }
          break;
        }
      }
    }
  }

  /**
   * Executes the bot's Battle Phase.
   * <p>
   * During the Battle Phase, the bot declares attacks with all available monsters
   * on its field. The bot uses strategic decision-making to choose targets or perform
   * direct attacks based on the current board state.
   
   * <p>
   * <strong>Battle Strategy:</strong>
   * <ol>
   * <li>Retrieves all bot monsters via {@link #getBotMonstersOnField()}</li>
   * <li>Retrieves all player monsters via {@link #getPlayerMonstersOnField()}</li>
   * <li><strong>If player has no monsters:</strong> Declares direct attacks with all
   *     bot monsters, dealing damage equal to their ATK directly to player's life points</li>
   * <li><strong>If player has monsters:</strong> For each bot monster:
   *     <ul>
   *     <li>Checks if the monster has already attacked this turn (skip if true)</li>
   *     <li>Finds the best target via {@link #findBestTarget(MonsterInfo, List)}</li>
   *     <li>Resolves battle via {@link DuelBoard#resolveBattlePublic}</li>
   *     <li>Marks the monster as having attacked to prevent multiple attacks</li>
   *     <li>Removes destroyed targets from the list</li>
   *     </ul>
   * </li>
   * </ol>
   
   * <p>
   * <strong>Attack Restrictions:</strong>
   * Monsters that have already attacked this turn (marked with "hasAttacked" = true)
   * are skipped. This enforces the Yu-Gi-Oh! rule that each monster can only attack
   * once per turn.
   
   * <p>
   * <strong>Direct Attack Conditions:</strong>
   * Direct attacks occur only when the player has no monsters on the field. All bot
   * monsters that haven't attacked yet will attack directly in this scenario.
   
   * <p>
   * All battle actions are logged to the console with details about attackers, targets,
   * and damage calculations.
   
   */
  public void battlePhase() {
    System.out.println("Bot: Battle Phase");
    game.setPhase(Phase.BP);

    List<MonsterInfo> botMonsters = getBotMonstersOnField();
    System.out.println("Number of bot's monsters: " + botMonsters.size());

    if (botMonsters.isEmpty()) {
      System.out.println("Bot has no monsters to attack with");
      return;
    }

    List<MonsterInfo> playerMonsters = getPlayerMonstersOnField();
    System.out.println(
      "Number of player's monsters detected: " + playerMonsters.size()
    );

    if (playerMonsters.isEmpty()) {
      System.out.println("Bot attacks Life Points directly!");
      for (MonsterInfo botMonster : botMonsters) {
        // Check if monster has already attacked
        boolean hasAttacked = (boolean) botMonster.view
          .getProperties()
          .getOrDefault("hasAttacked", false);

        if (!hasAttacked) {
          duelBoard.directAttack(botMonster.monster.getAtk());
          botMonster.view.getProperties().put("hasAttacked", true); // Mark as attacked
          System.out.println(
            "  - " +
            botMonster.monster.getName() +
            " attacks directly for " +
            botMonster.monster.getAtk() +
            " damage"
          );
        }
      }
    } else {
      for (MonsterInfo attacker : botMonsters) {
        // Check if monster has already attacked
        boolean hasAttacked = (boolean) attacker.view
          .getProperties()
          .getOrDefault("hasAttacked", false);

        if (hasAttacked) {
          System.out.println(
            "Bot: " +
            attacker.monster.getName() +
            " has already attacked this turn"
          );
          continue;
        }

        MonsterInfo bestTarget = findBestTarget(attacker, playerMonsters);

        if (bestTarget != null) {
          System.out.println(
            "Bot: " +
            attacker.monster.getName() +
            " (ATK:" +
            attacker.monster.getAtk() +
            ") attacks " +
            bestTarget.monster.getName()
          );

          duelBoard.resolveBattlePublic(
            attacker.monster,
            attacker.view,
            bestTarget.monster,
            bestTarget.view,
            false
          );

          attacker.view.getProperties().put("hasAttacked", true); // Mark as attacked
          playerMonsters.remove(bestTarget);
        } else {
          System.out.println(
            "Bot: " +
            attacker.monster.getName() +
            " cannot attack advantageously"
          );
        }
      }
    }
  }

  /**
   * Retrieves all monsters the bot currently has on the field.
   * <p>
   * This method scans all 5 monster zones (row 1, columns 1-5) on the bot's board,
   * identifies ImageViews representing monsters, and collects them into a list of
   * MonsterInfo objects containing the card, view, and zone references.
   
   * <p>
   * The method checks for:
   * <ul>
   * <li>StackPane zones at the correct grid positions</li>
   * <li>ImageView children within those zones</li>
   * <li>Card objects stored in the "card" property that are instances of MonsterCard</li>
   * </ul>
   
   *
   * @return a List of MonsterInfo objects representing all monsters on the bot's field
   */
  public List<MonsterInfo> getBotMonstersOnField() {
    List<MonsterInfo> monsters = new ArrayList<>();

    for (int col = 1; col <= 5; col++) {
      final int currentCol = col;
      Node cell = botBoard
        .getChildren()
        .stream()
        .filter(n -> {
          Integer rowIdx = GridPane.getRowIndex(n);
          Integer colIdx = GridPane.getColumnIndex(n);
          int row = (rowIdx == null) ? 0 : rowIdx;
          int column = (colIdx == null) ? 0 : colIdx;
          return row == 1 && column == currentCol;
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
              monsters.add(new MonsterInfo((MonsterCard) cardObj, view, zone));
            }
          }
        }
      }
    }

    return monsters;
  }

  /**
   * Retrieves all monsters the player currently has on the field.
   * <p>
   * This method scans all 5 monster zones (row 0, columns 1-5) on the player's board,
   * identifies ImageViews representing monsters, and collects them into a list of
   * MonsterInfo objects. This is used by the bot to identify potential attack targets.
   
   * <p>
   * <strong>Debug Output:</strong>
   * This method includes extensive debug logging that prints:
   * <ul>
   * <li>Whether the player board reference is null</li>
   * <li>Total number of children in the GridPane</li>
   * <li>Search results for each column (nodes found, types, card names)</li>
   * <li>Total number of monsters detected</li>
   * </ul>
   * This debug information helps troubleshoot issues with monster detection.
   
   * <p>
   * The method carefully handles null grid indices (defaulting to 0) to ensure robust
   * zone identification even if GridPane.setRowIndex() or GridPane.setColumnIndex()
   * were not called on certain nodes.
   
   *
   * @return a List of MonsterInfo objects representing all monsters on the player's field
   */
  public List<MonsterInfo> getPlayerMonstersOnField() {
    List<MonsterInfo> monsters = new ArrayList<>();
    GridPane playerBoard = duelBoard.getBoardJoueur1();

    System.out.println("=== DEBUG: Searching for player monsters ===");
    System.out.println("PlayerBoard null? " + (playerBoard == null));

    if (playerBoard != null) {
      System.out.println(
        "Total children count: " + playerBoard.getChildren().size()
      );

      for (int col = 1; col <= 5; col++) {
        final int currentCol = col;
        System.out.println("\nSearching column " + col + " row 0:");

        Node cell = playerBoard
          .getChildren()
          .stream()
          .filter(n -> {
            Integer rowIdx = GridPane.getRowIndex(n);
            Integer colIdx = GridPane.getColumnIndex(n);
            int row = (rowIdx == null) ? 0 : rowIdx;
            int column = (colIdx == null) ? 0 : colIdx;

            boolean match = row == 0 && column == currentCol;
            if (match) {
              System.out.println(
                "  -> Found node: " +
                n.getClass().getSimpleName() +
                " (row=" +
                row +
                ", col=" +
                column +
                ")"
              );
            }
            return match;
          })
          .findFirst()
          .orElse(null);

        if (cell instanceof StackPane) {
          StackPane zone = (StackPane) cell;
          System.out.println(
            "  StackPane with " + zone.getChildren().size() + " children"
          );

          for (Node child : zone.getChildren()) {
            System.out.println(
              "    Child: " + child.getClass().getSimpleName()
            );
            if (child instanceof ImageView) {
              ImageView view = (ImageView) child;
              Object cardObj = view.getProperties().get("card");
              System.out.println(
                "      Card: " +
                (cardObj != null ? ((Card) cardObj).getName() : "null")
              );

              if (cardObj instanceof MonsterCard) {
                monsters.add(
                  new MonsterInfo((MonsterCard) cardObj, view, zone)
                );
                System.out.println("      >>> MONSTER ADDED");
              }
            }
          }
        } else {
          System.out.println("  No StackPane found");
        }
      }
    }

    System.out.println("=== Total: " + monsters.size() + " monster(s) ===\n");
    return monsters;
  }

  /**
   * Finds the best target to attack from a list of potential targets.
   * <p>
   * This method implements the bot's battle strategy for target selection. The bot
   * prioritizes destroying the strongest monster it can defeat, maximizing field
   * advantage while minimizing risk.
   
   * <p>
   * <strong>Target Selection Strategy:</strong>
   * <ul>
   * <li><strong>Against Attack Position monsters:</strong>
   *     <ul>
   *     <li>Only attack if attacker's ATK > target's ATK (guaranteed destruction)</li>
   *     <li>Among valid targets, choose the one with the highest ATK</li>
   *     <li>This removes the strongest possible threat</li>
   *     </ul>
   * </li>
   * <li><strong>Against Defense Position monsters:</strong>
   *     <ul>
   *     <li>Only attack if attacker's ATK > target's DEF (guaranteed destruction)</li>
   *     <li>Among valid targets, choose the one with the highest DEF</li>
   *     <li>This removes defensive walls efficiently</li>
   *     </ul>
   * </li>
   * </ul>
   
   * <p>
   * The bot will not attack if it cannot guarantee destruction, avoiding unnecessary
   * damage to its own life points. If no favorable targets exist, returns null,
   * causing the attacking monster to not attack this turn.
   
   *
   * @param attacker the MonsterInfo of the bot's attacking monster
   * @param targets the List of potential target MonsterInfo objects on the player's field
   * @return the best MonsterInfo target to attack, or null if no favorable target exists
   */
  public MonsterInfo findBestTarget(
    MonsterInfo attacker,
    List<MonsterInfo> targets
  ) {
    MonsterInfo bestTarget = null;
    int attackerAtk = attacker.monster.getAtk();

    // Strategy: attack the weakest monster we can destroy
    for (MonsterInfo target : targets) {
      String position = (String) target.view
        .getProperties()
        .getOrDefault("position", "ATK");

      if ("ATK".equals(position)) {
        // In ATK: attack if we're stronger
        if (attackerAtk > target.monster.getAtk()) {
          if (
            bestTarget == null ||
            target.monster.getAtk() > bestTarget.monster.getAtk()
          ) {
            bestTarget = target;
          }
        }
      } else {
        // In DEF: attack if we can destroy
        if (attackerAtk > target.monster.getDef()) {
          if (
            bestTarget == null ||
            target.monster.getDef() > bestTarget.monster.getDef()
          ) {
            bestTarget = target;
          }
        }
      }
    }

    return bestTarget;
  }

  /**
   * Executes the bot's Main Phase 2.
   * <p>
   * Main Phase 2 occurs after the Battle Phase and allows the bot to perform
   * additional summons or set cards. The logic is identical to Main Phase 1:
   * the bot attempts to Normal Summon if it hasn't already done so this turn.
   
   * <p>
   * This phase is useful for:
   * <ul>
   * <li>Summoning defensive monsters after attacking</li>
   * <li>Setting monsters in Defense Position to protect life points</li>
   * <li>Summoning monsters that couldn't be summoned in Main Phase 1 due to
   *     tribute requirements (if tributes became available during Battle Phase)</li>
   * </ul>
   
   * <p>
   * The method follows the same summoning logic as {@link #mainPhase1()},
   * including level-based tribute requirements and defensive play decisions.
   
   * <p>
   * <strong>Note:</strong> The phase is set to MP1 instead of MP2 due to a bug
   * in the Phase enum or game logic. This should be corrected to Phase.MP2.
   
   */
  public void mainPhase2() {
    System.out.println("Bot: Main Phase 2");
    game.setPhase(Phase.MP1); // TODO: Should be Phase.MP2

    if (hasNormalSummonedThisTurn) {
      System.out.println("Bot has already Normal Summoned this turn");
      return;
    }

    MonsterCard monsterToSummon = findBestSummonableMonster();

    if (monsterToSummon != null) {
      int level = monsterToSummon.getLevel();

      // Check if we should summon defensively
      boolean shouldSetInDefense = shouldPlayDefensive(monsterToSummon);

      if (level <= 4) {
        if (shouldSetInDefense) {
          System.out.println(
            "Bot sets in defense (level " +
            level +
            "): " +
            monsterToSummon.getName()
          );
          setMonsterInDefense(monsterToSummon);
        } else {
          System.out.println(
            "Bot summons (level " + level + "): " + monsterToSummon.getName()
          );
          summonMonster(monsterToSummon);
        }
        hasNormalSummonedThisTurn = true;
      } else if (level <= 6) {
        if (canTribute(1)) {
          System.out.println(
            "Bot tribute summons with 1 tribute (level " +
            level +
            "): " +
            monsterToSummon.getName()
          );
          tributeMonsters(1);
          summonMonster(monsterToSummon);
          hasNormalSummonedThisTurn = true;
        } else {
          System.out.println(
            "Bot cannot summon " +
            monsterToSummon.getName() +
            " (not enough monsters)"
          );
        }
      } else {
        if (canTribute(2)) {
          System.out.println(
            "Bot tribute summons with 2 tributes (level " +
            level +
            "): " +
            monsterToSummon.getName()
          );
          tributeMonsters(2);
          summonMonster(monsterToSummon);
          hasNormalSummonedThisTurn = true;
        } else {
          System.out.println(
            "Bot cannot summon " +
            monsterToSummon.getName() +
            " (not enough monsters)"
          );
        }
      }
    } else {
      System.out.println("Bot has no summonable monsters");
    }
  }

  /**
   * Executes the bot's End Phase and passes the turn.
   * <p>
   * During the End Phase:
   * <ol>
   * <li>Sets the game phase to End Phase (EP)</li>
   * <li>Calls {@link Game#endTurn()} to pass the turn back to the player</li>
   * </ol>
   
   * <p>
   * This marks the completion of the bot's turn. The game controller will then
   * reset the turn phase to Draw Phase and switch the active player back to the
   * human player.
   
   * <p>
   * <strong>Future Enhancements:</strong>
   * This phase could be extended to handle:
   * <ul>
   * <li>End Phase trigger effects</li>
   * <li>Hand size limit enforcement (discard down to 6 cards)</li>
   * <li>Turn counter updates</li>
   * </ul>
   
   */
  public void endPhase() {
    System.out.println("Bot: End Phase");
    game.setPhase(Phase.EP);
    game.endTurn();
  }

  /**
   * Finds the best summonable monster according to the rules.
   * <p>
   * Rules:
   * <ul>
   *   <li>Level ≤ 4: always summonable without tribute</li>
   *   <li>Level 5–6: summonable if at least 1 tribute is available</li>
   *   <li>Level ≥ 7: summonable if at least 2 tributes are available</li>
   * </ul>
   * The monster with the highest ATK among summonable candidates is chosen.
   *
   * @return the {@link MonsterCard} with the highest ATK that can be summoned,
   *         or {@code null} if none are summonable
   */
  public MonsterCard findBestSummonableMonster() {
    MonsterCard bestMonster = null;
    int maxAtk = -1;

    for (Node node : botHand.getChildren()) {
      if (node instanceof ImageView) {
        ImageView view = (ImageView) node;
        Object cardObj = view.getProperties().get("card");

        if (cardObj instanceof MonsterCard) {
          MonsterCard monster = (MonsterCard) cardObj;
          int level = monster.getLevel();

          // Check if the monster can be summoned
          boolean canSummon = false;

          if (level <= 4) {
            canSummon = true; // Always summonable
          } else if (level <= 6 && canTribute(1)) {
            canSummon = true; // Summonable with 1 tribute
          } else if (level >= 7 && canTribute(2)) {
            canSummon = true; // Summonable with 2 tributes
          }

          if (canSummon && monster.getAtk() > maxAtk) {
            maxAtk = monster.getAtk();
            bestMonster = monster;
          }
        }
      }
    }

    return bestMonster;
  }

  /**
   * Checks if the bot has enough monsters on the field to perform the required tributes.
   * <p>
   * Iterates over the 5 monster zones (row 1, columns 1–5) and counts occupied cells.
   *
   * @param requiredTributes number of tributes required (usually 1 or 2)
   * @return {@code true} if the bot has enough monsters on the field, otherwise {@code false}
   */
  public boolean canTribute(int requiredTributes) {
    int monstersOnField = 0;

    for (int col = 1; col <= 5; col++) {
      final int currentCol = col; // must be final for lambda usage
      Node cell = botBoard
        .getChildren()
        .stream()
        .filter(
          n ->
            GridPane.getRowIndex(n) == 1 &&
            GridPane.getColumnIndex(n) == currentCol
        )
        .findFirst()
        .orElse(null);

      if (cell instanceof StackPane) {
        StackPane zone = (StackPane) cell;
        if (!zone.getChildren().isEmpty()) {
          monstersOnField++;
        }
      }
    }

    return monstersOnField >= requiredTributes;
  }

  /**
   * Performs tributes by removing the weakest monsters from the bot's field.
   * <p>
   * Steps:
   * <ul>
   *   <li>Collect all monsters currently on the field</li>
   *   <li>Sort them by ATK ascending</li>
   *   <li>Remove the required number of weakest monsters</li>
   *   <li>Send them to the graveyard</li>
   * </ul>
   *
   * @param count number of monsters to tribute (usually 1 or 2)
   */
  public void tributeMonsters(int count) {
    System.out.println("Bot tributes " + count + " monster(s)");

    List<MonsterInfo> monsters = new ArrayList<>();

    for (int col = 1; col <= 5; col++) {
      final int currentCol = col;
      Node cell = botBoard
        .getChildren()
        .stream()
        .filter(
          n ->
            GridPane.getRowIndex(n) == 1 &&
            GridPane.getColumnIndex(n) == currentCol
        )
        .findFirst()
        .orElse(null);

      if (cell instanceof StackPane) {
        StackPane zone = (StackPane) cell;
        if (!zone.getChildren().isEmpty()) {
          for (Node child : zone.getChildren()) {
            if (child instanceof ImageView) {
              ImageView view = (ImageView) child;
              Object cardObj = view.getProperties().get("card");
              if (cardObj instanceof MonsterCard) {
                monsters.add(
                  new MonsterInfo((MonsterCard) cardObj, view, zone)
                );
              }
            }
          }
        }
      }
    }

    // Sort by ATK ascending (tribute weakest monsters first)
    monsters.sort((a, b) ->
      Integer.compare(a.monster.getAtk(), b.monster.getAtk())
    );

    for (int i = 0; i < Math.min(count, monsters.size()); i++) {
      MonsterInfo info = monsters.get(i);
      System.out.println(
        "  - Tribute " +
        info.monster.getName() +
        " (ATK: " +
        info.monster.getAtk() +
        ")"
      );

      // Remove from field and send to graveyard
      info.zone.getChildren().remove(info.view);
      sendToGraveyard(info.monster, info.view, botBoard);
    }
  }

  /**
   * Helper class to store information about a monster currently on the bot's field.
   * <p>
   * Contains:
   * <ul>
   *   <li>The monster card</li>
   *   <li>The ImageView representing it</li>
   *   <li>The StackPane zone where it is placed</li>
   * </ul>
   */
  public class MonsterInfo {

    /** The monster card. */
    MonsterCard monster;
    /** The ImageView representing the monster on the board. */
    ImageView view;
    /** The StackPane zone containing the monster. */
    StackPane zone;

    /**
     * Constructs a MonsterInfo object.
     *
     * @param monster the monster card
     * @param view the ImageView representing the monster
     * @param zone the StackPane zone containing the monster
     */
    MonsterInfo(MonsterCard monster, ImageView view, StackPane zone) {
      this.monster = monster;
      this.view = view;
      this.zone = zone;
    }
  }

  /**
   * Sends a card to the graveyard zone of the specified board.
   * <p>
   * Searches for the graveyard zone (either directly in the GridPane or inside an HBox),
   * then adds a reduced-size ImageView of the card to that zone.
   *
   * @param card the card to send to the graveyard
   * @param view the ImageView representing the card on the field
   * @param boardTarget the board containing the graveyard zone
   */
  public void sendToGraveyard(
    Card card,
    ImageView view,
    GridPane boardTarget
  ) {
    Node graveyardCell = null;

    // Search for the graveyard zone (may be inside an HBox for opponent)
    for (Node node : boardTarget.getChildren()) {
      if (
        node.getStyleClass().contains("graveyard-zone") &&
        node instanceof StackPane
      ) {
        graveyardCell = node;
        break;
      }
      if (node instanceof HBox) {
        for (Node child : ((HBox) node).getChildren()) {
          if (
            child.getStyleClass().contains("graveyard-zone") &&
            child instanceof StackPane
          ) {
            graveyardCell = child;
            break;
          }
        }
      }
      if (graveyardCell != null) break;
    }

    if (graveyardCell instanceof StackPane) {
      StackPane graveyardZone = (StackPane) graveyardCell;
      ImageView graveyardView = new ImageView(card.getImage());
      graveyardView.setFitWidth(60);
      graveyardView.setFitHeight(90);
      graveyardView.setRotate(0);
      graveyardView.getProperties().put("card", card);
      graveyardZone.getChildren().add(graveyardView);
      System.out.println("  → " + card.getName() + " sent to graveyard");
    }
  }

  /**
   * Summons a monster from the bot's hand onto its field.
   * <p>
   * Steps performed:
   * <ul>
   *   <li>Searches the bot's hand for the specified monster card.</li>
   *   <li>If found, looks for the first empty monster zone on the bot's board.</li>
   *   <li>Creates a new {@link ImageView} for the monster and places it in the zone.</li>
   *   <li>Sets properties such as card reference, position (ATK), ownership (opponent), and zone reference.</li>
   *   <li>Attaches a click handler via {@link ui.DuelBoard#handleBotMonsterClick(ImageView, MonsterCard, GridPane, HBox)}
   *       so the monster can be targeted by the player during attacks.</li>
   *   <li>Removes the monster from the bot's hand once successfully placed.</li>
   * </ul>
   *
   * @param monster the {@link MonsterCard} to summon from the bot's hand
   */
  public void summonMonster(MonsterCard monster) {
    System.out.println("=== START SUMMON ===");
    System.out.println("Attempting to summon: " + monster.getName());
    System.out.println(
      "Hand size BEFORE search: " + botHand.getChildren().size()
    );

    ImageView monsterView = null;

    // Search for the monster in the bot's hand
    for (Node node : new ArrayList<>(botHand.getChildren())) {
      if (node instanceof ImageView) {
        ImageView view = (ImageView) node;
        Object cardObj = view.getProperties().get("card");
        System.out.println(
          "  Checking card: " +
          (cardObj != null ? ((Card) cardObj).getName() : "null")
        );
        if (cardObj == monster) {
          monsterView = view;
          System.out.println("  >>> CARD FOUND!");
          break;
        }
      }
    }

    if (monsterView == null) {
      System.out.println("ERROR: Card not found in bot's hand");
      return;
    }

    System.out.println("Card found in hand, searching for an empty zone...");

    // Iterate over the 5 monster zones
    for (int col = 1; col <= 5; col++) {
      final int currentCol = col;
      Node cell = botBoard
        .getChildren()
        .stream()
        .filter(
          n ->
            GridPane.getRowIndex(n) == 1 &&
            GridPane.getColumnIndex(n) == currentCol
        )
        .findFirst()
        .orElse(null);

      if (cell instanceof StackPane) {
        StackPane zone = (StackPane) cell;
        if (zone.getChildren().isEmpty()) {
          System.out.println("Empty zone found at column " + col);

          // Create ImageView for the summoned monster
          ImageView placedView = new ImageView(monster.getImage());
          placedView.setFitWidth(88);
          placedView.setFitHeight(132);
          placedView.setRotate(180);

          // Set properties for game logic
          placedView.getProperties().put("card", monster);
          placedView.getProperties().put("faceDown", false);
          placedView.getProperties().put("position", "ATK");
          placedView.getProperties().put("isOpponent", true);
          placedView.getProperties().put("placedCell", zone);

          // Attach click handler so the monster can be targeted in battle
          placedView.setOnMouseClicked(e -> {
            duelBoard.handleBotMonsterClick(
              placedView,
              monster,
              botBoard,
              botHand
            );
          });

          // Place monster on the board
          zone.getChildren().add(placedView);
          zone.getProperties().put("card", monster);

          System.out.println(
            "Hand size BEFORE removal: " + botHand.getChildren().size()
          );

          boolean removed = botHand.getChildren().remove(monsterView);

          System.out.println("Removal successful? " + removed);
          System.out.println(
            "Hand size AFTER removal: " + botHand.getChildren().size()
          );
          System.out.println(
            "Bot summoned " + monster.getName() + " in ATK position"
          );
          System.out.println("=== END SUMMON ===");
          break;
        }
      }
    }
  }
}
