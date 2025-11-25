package Player;

import Deck.Deck;
import Deck.ExtraDeck;
import Hand.Hand;
import Board.Board;
import Card.MonsterCard;
import Card.SpellCard;
import Card.TrapCard;
import Card.Card;
import Card.Position;

/**
 * Represents a player in a Yu-Gi-Oh! duel.
 * <p>
 * The {@code Player} class manages a player's identity (name), life points,
 * and serves as the central point for their game zones (Deck, Extra Deck, Hand, Board).
 * It is also responsible for actions a player can perform during different turn phases
 * (drawing, summoning, activating cards, etc.).
 * </p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Store the player's name and life points</li>
 *   <li>Initialize life points to 8000 at the start of the game</li>
 *   <li>Provide access methods for name and life points</li>
 *   <li>Manage game zones (Deck, ExtraDeck, Hand, Board)</li>
 *   <li>Handle gameplay actions (summons, attacks, effect activations)</li>
 * </ul>
 *
 * <h2>Planned Relationships</h2>
 * <ul>
 *   <li>Composition with {@code Deck}, {@code ExtraDeck}, {@code Hand}, {@code Board}</li>
 *   <li>Interaction with {@code Graveyard} and {@code BanishedZone} through gameplay actions</li>
 *   <li>Coordination with {@code Game} to manage phases and win conditions</li>
 * </ul>
 *
 * @author Nicolas
 * @version 1.0
 * @since 2025
 */
public class Player {

    /** The player's name */
    private String name;

    /** The player's current life points */
    private int lifePoints;

    /** The player's main deck */
    private Deck deck;

    /** The player's extra deck (for Fusion, Synchro, XYZ, Link monsters) */
    private ExtraDeck extraDeck;

    /** The player's hand */
    private Hand hand;

    /** The player's game board/field */
    private Board board;

    /** Flag indicating whether the player has already performed their Normal Summon this turn */
    private boolean hasPlayerNormalSummon;

    /**
     * Creates a new player with specified attributes and game zones.
     * <p>
     * This constructor initializes the player with a pre-configured deck and
     * extra deck, creates an empty hand, and sets up their board. The Normal
     * Summon flag is set to false, allowing the player to perform one Normal
     * Summon on their first turn.
     * </p>
     *
     * @param name the player's name
     * @param lifePoints the player's starting life points (typically 8000)
     * @param deck the player's main deck
     * @param extraDeck the player's extra deck, or null to create an empty one
     */
    public Player(String name, int lifePoints, Deck deck, ExtraDeck extraDeck) {
        this.name = name;
        this.lifePoints = lifePoints;
        this.deck = deck;
        this.extraDeck = extraDeck != null ? extraDeck : new ExtraDeck();
        this.hand = new Hand();
        this.board = new Board(this, this.deck, this.extraDeck);
        this.hasPlayerNormalSummon = false;
    }

    /**
     * Creates a new player with only a name and default values.
     * <p>
     * This simplified constructor sets life points to 8000 and creates
     * empty hand and extra deck. The main deck and board must be initialized
     * later using {@link #setDeck(Deck)} before the player can draw cards
     * or play properly.
     * </p>
     * <p>
     * This constructor is useful for quick player setup where decks are
     * assigned later during game initialization.
     * </p>
     *
     * @param name the player's name
     */
    public Player(String name) {
        this.name = name;
        this.lifePoints = 8000;
        this.hand = new Hand();
        this.extraDeck = new ExtraDeck();
        // Board cannot be properly initialized without a deck
        // It will be initialized when a deck is assigned via setDeck()
    }

    // --- Basic Getters ---

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getName() { 
        return name; 
    }

    /**
     * Gets the player's current life points.
     *
     * @return the current life points value
     */
    public int getLifePoints() { 
        return lifePoints; 
    }

    /**
     * Gets the player's game board.
     *
     * @return the Board object representing the player's field
     */
    public Board getBoard() { 
        return board; 
    }

    /**
     * Checks if the player has already performed their Normal Summon this turn.
     * <p>
     * In Yu-Gi-Oh!, each player can only perform one Normal Summon or Set per turn
     * (unless a card effect allows additional summons). This flag tracks whether
     * the player has used their Normal Summon for the current turn.
     * </p>
     *
     * @return true if the player has Normal Summoned this turn, false otherwise
     */
    public boolean hasNormalSummoned() { 
        return hasPlayerNormalSummon; 
    }

    /**
     * Gets the player's hand.
     *
     * @return the Hand object containing the player's cards
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Gets the player's main deck.
     *
     * @return the Deck object containing the player's remaining cards
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Gets the player's extra deck.
     *
     * @return the ExtraDeck object containing special summon monsters
     */
    public ExtraDeck getExtraDeck() {
        return extraDeck;
    }

    // --- Life Points Management ---

    /**
     * Increases the player's life points by the specified amount.
     * <p>
     * This method is called when the player gains life points through card
     * effects or game mechanics. The increase is logged to the console.
     * </p>
     *
     * @param amount the amount of life points to add (must be positive)
     */
    public void increaseLifePoints(int amount) {
        lifePoints += amount;
        System.out.println(name + " gains " + amount + " LP. Total: " + lifePoints);
    }

    /**
     * Decreases the player's life points by the specified amount.
     * <p>
     * This method is called when the player takes damage from battles or
     * card effects. Life points cannot go below 0; if the reduction would
     * result in negative life points, they are set to exactly 0.
     * </p>
     * <p>
     * The decrease is logged to the console. After calling this method,
     * the game should check win conditions via {@link #isAlive()}.
     * </p>
     *
     * @param amount the amount of life points to subtract (must be positive)
     */
    public void decreaseLifePoints(int amount) {
        lifePoints -= amount;
        if (lifePoints < 0) lifePoints = 0;
        System.out.println(name + " loses " + amount + " LP. Total: " + lifePoints);
    }

    /**
     * Checks if the player is still in the game.
     * <p>
     * A player is considered alive if their life points are greater than 0.
     * When a player's life points reach 0 or below, they lose the duel.
     * This method is typically called after any action that could reduce
     * life points to determine if the game should end.
     * </p>
     *
     * @return true if life points are greater than 0, false if 0 or below
     */
    public boolean isAlive() {
        return lifePoints > 0;
    }

    /**
     * Assigns a deck to the player and initializes their board.
     * <p>
     * This method sets the player's main deck and creates a new Board
     * using the provided deck and the player's extra deck. This is typically
     * called during game initialization before the duel begins.
     * </p>
     * <p>
     * If the player was created with the simplified constructor, this method
     * must be called before the player can draw cards or play properly.
     * </p>
     *
     * @param deck the Deck to assign to this player
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
        this.board = new Board(this, this.deck, this.extraDeck);
    }

    // --- Basic Actions (to be expanded later) ---

    /**
     * Draws one card from the player's deck and adds it to their hand.
     * <p>
     * This method is typically called during the Draw Phase of the player's turn.
     * If the deck is empty when this method is called, the player loses the duel
     * (deck out condition), though this is handled by the {@link Deck#draw()} method.
     * </p>
     * <p>
     * The drawn card is logged to the console for visibility.
     * </p>
     */
    public void drawCard() {
        Card drawn = deck.draw();
        if (drawn != null) {
            hand.addCard(drawn);
            System.out.println(name + " draws: " + drawn.getName());
        }
    }

    /**
     * Attempts to Normal Summon a monster card to the field.
     * <p>
     * A Normal Summon places a monster from the hand onto the field in Attack
     * Position. Each player can only perform one Normal Summon per turn unless
     * a card effect allows additional summons.
     * </p>
     * <p>
     * If the player has already Normal Summoned this turn, the request is
     * rejected and a message is printed. The monster is placed in zone 0
     * by default (this may be enhanced to allow zone selection).
     * </p>
     * <p>
     * Note: This method does not currently check tribute requirements for
     * high-level monsters. That logic should be added for levels 5+.
     * </p>
     *
     * @param card the MonsterCard to summon from the hand
     */
    public void summonMonster(MonsterCard card) {
        if (!hasPlayerNormalSummon) {
            board.placeMonster(card, 0, Position.ATTACK); // example: zone 0
            hasPlayerNormalSummon = true;
            System.out.println(name + " summons " + card.getName());
        } else {
            System.out.println(name + " has already Normal Summoned this turn.");
        }
    }

    /**
     * Activates a Spell Card from the player's hand.
     * <p>
     * This method delegates the activation to the player's board, which
     * handles placing the spell card in the appropriate zone and resolving
     * its effect. Spell cards can typically be activated during the player's
     * Main Phase (or during either player's turn for Quick-Play spells).
     * </p>
     *
     * @param card the SpellCard to activate
     */
    public void playSpell(SpellCard card) {
        board.activateSpell(card);
    }

    /**
     * Activates a Trap Card that was previously set on the field.
     * <p>
     * This method delegates the activation to the player's board, which
     * handles flipping the trap card face-up and resolving its effect.
     * Trap cards must be set for at least one turn before they can be
     * activated (with few exceptions).
     * </p>
     *
     * @param card the TrapCard to activate
     */
    public void activateTrap(TrapCard card) {
        board.activateTrap(card);
    }

    /**
     * Ends the player's turn and resets turn-based flags.
     * <p>
     * This method should be called when the player finishes all their actions
     * and is ready to pass priority to the opponent. It resets the Normal
     * Summon flag, allowing the player to Normal Summon again on their next turn.
     * </p>
     * <p>
     * Additional end-of-turn logic (such as resetting monster attack flags)
     * should be handled by the Board or Game controller.
     * </p>
     */
    public void endTurn() {
        hasPlayerNormalSummon = false;
        System.out.println(name + " ends their turn.");
    }
}