package Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Card.Card;
import Card.MonsterCard;
import Card.SpellCard;
import Card.TrapCard;
import Card.Effect;
import Player.Player;

/**
 * Represents a player's main deck in Yu-Gi-Oh!
 * <p>
 * The main deck contains the cards that a player draws from during the duel.
 * In official Yu-Gi-Oh! rules, a deck must contain between 40 and 60 cards,
 * though this class does not enforce those limits to allow for flexible testing
 * and custom game modes.
 * </p>
 * <p>
 * The deck supports standard operations such as shuffling, drawing, adding cards,
 * and checking its current state. Cards are drawn from the top of the deck (index 0),
 * and the deck can be reset with a new set of cards at any time.
 * </p>
 * <p>
 * When a player cannot draw a card because the deck is empty, they lose the duel
 * (deck out condition). This is handled by the {@link #draw()} method returning null.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class Deck {

    // --- Attributes ---
    
    /** The list of cards in the deck, with index 0 being the top */
    private List<Card> cards;

    // --- Constructors ---
    
    /**
     * Creates a new deck with a pre-defined list of cards.
     * <p>
     * This constructor creates a defensive copy of the provided card list
     * to prevent external modifications from affecting the deck's internal state.
     * The cards are added in the order provided and can be shuffled later using
     * {@link #shuffle()}.
     * </p>
     *
     * @param initialCards the list of cards to initialize the deck with
     */
    public Deck(List<Card> initialCards) {
        this.cards = new ArrayList<>(initialCards);
    }

    /**
     * Creates an empty deck.
     * <p>
     * This constructor initializes the deck with no cards. Cards must be added
     * manually using {@link #addCard(Card)} or by calling {@link #reset(List)}
     * with a card list. This is useful for building custom decks programmatically.
     * </p>
     */
    public Deck() {
        this.cards = new ArrayList<>();
    }

    /**
     * Creates a deck for testing purposes.
     * <p>
     * This constructor initializes an empty deck that can be populated with
     * test cards. The owner parameter allows cards to be assigned to the
     * correct player when they are added.
     * </p>
     * <p>
     * <strong>Note:</strong> This constructor currently creates an empty deck.
     * You can add default test cards here during development for quick testing.
     * </p>
     *
     * @param owner the player who owns this deck and the cards within it
     */
    public Deck(Player owner) {
        this.cards = new ArrayList<>();
        // TODO: Add default test cards if needed for development
    }

    // --- Main Methods ---
    
    /**
     * Shuffles the deck randomly.
     * <p>
     * This method randomizes the order of all cards in the deck using
     * {@link Collections#shuffle(List)}. In official Yu-Gi-Oh! rules, decks
     * must be shuffled at the start of the game and whenever a card effect
     * requires it (such as when searching the deck for a card).
     * </p>
     * <p>
     * The shuffle operation is logged to the console for game tracking purposes.
     * </p>
     */
    public void shuffle() {
        Collections.shuffle(cards);
        System.out.println("The Deck is shuffled.");
    }

    /**
     * Draws the top card from the deck.
     * <p>
     * This method removes and returns the card at index 0 (the top of the deck).
     * If the deck is empty when this method is called, it returns null and prints
     * a warning message. The calling code should handle the null return as a
     * deck-out loss condition.
     * </p>
     * <p>
     * In official Yu-Gi-Oh! rules, players draw one card during their Draw Phase
     * (except the first turn player on their first turn). Cards can also be drawn
     * by card effects.
     * </p>
     * <p>
     * The drawn card is logged to the console, showing its name for game tracking.
     * </p>
     *
     * @return the drawn Card object, or null if the deck is empty
     */
    public Card draw() {
        if (isEmpty()) {
            System.out.println("The Deck is empty, cannot draw.");
            return null;
        }
        Card top = cards.remove(0);
        System.out.println("Card drawn: " + top.getName());
        return top;
    }

    /**
     * Adds a card to the bottom of the deck.
     * <p>
     * This method appends a card to the end of the deck's card list. This is
     * typically used when a card effect returns a card to the deck or when
     * building a deck programmatically.
     * </p>
     * <p>
     * If the card should be added to a specific position (such as the top),
     * this method would need to be enhanced or a new method created.
     * </p>
     *
     * @param c the Card to add to the deck
     */
    public void addCard(Card c) {
        cards.add(c);
    }

    /**
     * Checks if the deck is empty.
     * <p>
     * A deck is considered empty when it contains no cards. When a player
     * must draw from an empty deck, they lose the duel (deck-out condition).
     * </p>
     *
     * @return true if the deck has no cards, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets the number of cards remaining in the deck.
     * <p>
     * This method is useful for tracking deck depletion, implementing card
     * effects that reference deck size, and displaying deck information to
     * players during the game.
     * </p>
     *
     * @return the number of cards currently in the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Views the top card of the deck without removing it.
     * <p>
     * This method allows "peeking" at the next card to be drawn without
     * actually drawing it. This is useful for implementing card effects
     * that reveal or manipulate the top card of the deck.
     * </p>
     * <p>
     * If the deck is empty, returns null.
     * </p>
     *
     * @return the Card at the top of the deck (index 0), or null if the deck is empty
     */
    public Card peek() {
        if (isEmpty()) return null;
        return cards.get(0);
    }

    /**
     * Removes a specific card from the deck.
     * <p>
     * This method searches for and removes the first occurrence of the specified
     * card from anywhere in the deck. This is typically used when a card effect
     * searches the deck for a specific card and removes it (to add to hand,
     * summon, etc.).
     * </p>
     * <p>
     * After removing a card from the deck, the deck should typically be shuffled
     * according to official Yu-Gi-Oh! rules.
     * </p>
     *
     * @param c the Card to remove from the deck
     * @return true if the card was found and removed, false if it wasn't in the deck
     */
    public boolean removeCard(Card c) {
        return cards.remove(c);
    }

    /**
     * Resets the deck with a new set of cards.
     * <p>
     * This method completely replaces the current deck contents with a new
     * card list. This is useful for starting a new game, implementing deck
     * reconstruction effects, or testing different deck configurations.
     * </p>
     * <p>
     * A defensive copy of the provided list is made to prevent external
     * modifications from affecting the deck's internal state. The reset
     * operation is logged to the console.
     * </p>
     *
     * @param newCards the new list of cards to populate the deck with
     */
    public void reset(List<Card> newCards) {
        this.cards = new ArrayList<>(newCards);
        System.out.println("The Deck has been reset.");
    }

    /**
     * Gets a copy of all cards currently in the deck.
     * <p>
     * This method returns a defensive copy of the card list to prevent
     * external code from modifying the deck's internal state directly.
     * This is useful for implementing card effects that need to examine
     * or iterate through deck contents without risking unintended modifications.
     * </p>
     * <p>
     * Note that while the list is a copy, the Card objects themselves are
     * not cloned, so modifications to card properties will affect the actual
     * cards in the deck.
     * </p>
     *
     * @return a new ArrayList containing all cards in the deck
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards); // Returns a copy of the list
    }
}