package Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Card.Card;
import Card.MonsterCard;

/**
 * Represents a player's Extra Deck in Yu-Gi-Oh!
 * <p>
 * The Extra Deck is a separate deck that contains special summon monsters that
 * cannot be included in the main deck. These include Fusion, Synchro, Xyz, and
 * Link monsters. Unlike the main deck, the Extra Deck is face-up and visible to
 * both players (though in digital implementations, it may be hidden for practical
 * reasons).
 * </p>
 * <p>
 * In official Yu-Gi-Oh! rules, the Extra Deck can contain up to 15 cards, though
 * this class does not enforce that limit to allow for flexible testing and custom
 * game modes.
 * </p>
 * <p>
 * Extra Deck monsters are not drawn like normal cards; instead, they are Special
 * Summoned from the Extra Deck when their specific summoning conditions are met
 * (such as Fusion Summoning with Polymerization, or Synchro Summoning with a
 * Tuner monster).
 * </p>
 * <p>
 * When Extra Deck monsters are destroyed or leave the field, they typically return
 * to the Extra Deck rather than going to the Graveyard (with some exceptions based
 * on how they were summoned).
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class ExtraDeck {

    // --- Attributes ---
    
    /** The list of cards in the Extra Deck (Fusion, Synchro, Xyz, Link monsters) */
    private List<Card> cards;

    // --- Constructors ---
    
    /**
     * Creates a new empty Extra Deck.
     * <p>
     * An empty Extra Deck is useful for players who don't use Extra Deck
     * monsters or when building a deck programmatically. Cards can be added
     * later using {@link #addCard(Card)}.
     * </p>
     */
    public ExtraDeck() {
        this.cards = new ArrayList<>();
    }

    /**
     * Creates a new Extra Deck with a pre-defined list of cards.
     * <p>
     * This constructor creates a defensive copy of the provided card list
     * to prevent external modifications from affecting the Extra Deck's
     * internal state.
     * </p>
     *
     * @param initialCards the list of cards to initialize the Extra Deck with
     */
    public ExtraDeck(List<Card> initialCards) {
        this.cards = new ArrayList<>(initialCards);
    }

    // --- Main Methods ---

    /**
     * Adds a card to the Extra Deck.
     * <p>
     * This method is typically used when building a deck before the game starts,
     * or when an Extra Deck monster returns to the Extra Deck from the field or
     * Graveyard through a card effect.
     * </p>
     * <p>
     * The operation is logged to the console for game tracking purposes.
     * </p>
     *
     * @param c the Card (typically a MonsterCard with special summon type) to add
     */
    public void addCard(Card c) {
        cards.add(c);
        System.out.println("Card added to the Extra Deck: " + c.getName());
    }

    /**
     * Removes a specific card from the Extra Deck.
     * <p>
     * This method is called when an Extra Deck monster is Special Summoned
     * to the field. The card is removed from the Extra Deck and placed in the
     * appropriate monster zone on the board.
     * </p>
     * <p>
     * Note: When Extra Deck monsters are destroyed, they should return to the
     * Extra Deck in most cases (handled by game logic, not this method).
     * </p>
     *
     * @param c the Card to remove from the Extra Deck
     * @return true if the card was found and removed, false if it wasn't in the Extra Deck
     */
    public boolean removeCard(Card c) {
        return cards.remove(c);
    }

    /**
     * Selects a card from the Extra Deck by its index.
     * <p>
     * This method allows players to choose a specific Extra Deck monster for
     * Special Summoning. The index corresponds to the position in the Extra Deck,
     * typically displayed in a UI or menu during summoning procedures.
     * </p>
     * <p>
     * This method does NOT remove the card from the Extra Deck; it only returns
     * a reference to it. Use {@link #removeCard(Card)} to actually summon the card.
     * </p>
     * <p>
     * If the index is out of bounds, returns null and prints an error message.
     * </p>
     *
     * @param index the position of the card in the Extra Deck (0-based)
     * @return the Card at the specified index, or null if the index is invalid
     */
    public Card chooseCard(int index) {
        if (index < 0 || index >= cards.size()) {
            System.out.println("Invalid index in the Extra Deck.");
            return null;
        }
        return cards.get(index);
    }

    /**
     * Shuffles the cards in the Extra Deck randomly.
     * <p>
     * While Extra Decks are typically not shuffled in official Yu-Gi-Oh! rules
     * (since they're face-up and visible), this method may be useful for certain
     * custom game modes, testing scenarios, or card effects that randomize Extra
     * Deck order.
     * </p>
     * <p>
     * The shuffle operation is logged to the console.
     * </p>
     */
    public void shuffle() {
        Collections.shuffle(cards);
        System.out.println("The Extra Deck is shuffled.");
    }

    /**
     * Gets the number of cards currently in the Extra Deck.
     * <p>
     * This is useful for checking if the player has available Extra Deck
     * monsters to summon, or for implementing card effects that reference
     * Extra Deck size.
     * </p>
     *
     * @return the number of cards in the Extra Deck (maximum 15 in official rules)
     */
    public int size() {
        return cards.size();
    }

    /**
     * Checks if the Extra Deck is empty.
     * <p>
     * An empty Extra Deck means the player has no Extra Deck monsters available
     * to summon, or all have been summoned and not yet returned.
     * </p>
     *
     * @return true if there are no cards in the Extra Deck, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets all cards currently in the Extra Deck.
     * <p>
     * This method returns the actual internal list of cards (not a copy),
     * allowing external code to iterate through or examine all cards in the
     * Extra Deck. This is useful for displaying Extra Deck contents to players
     * or for implementing card effects that interact with the Extra Deck.
     * </p>
     * <p>
     * In official rules, the Extra Deck is face-up and both players can see
     * its contents, though in digital implementations it may be kept private
     * until summoning.
     * </p>
     * <p>
     * <strong>Note:</strong> Modifications to the returned list will affect
     * the Extra Deck's internal state. Consider returning a defensive copy if
     * needed: {@code return new ArrayList<>(cards);}
     * </p>
     *
     * @return the List of all cards in the Extra Deck
     */
    public List<Card> getAllCards() {
        return cards;
    }

    /**
     * Searches for Extra Deck monsters by their summoning type.
     * <p>
     * This method filters the Extra Deck to find all monsters of a specific
     * summoning type (Fusion, Synchro, Xyz, Link). This is useful for implementing
     * card effects that specifically search for or interact with certain types of
     * Extra Deck monsters.
     * </p>
     * <p>
     * The search is case-insensitive and matches against the monster's type.
     * Only MonsterCard instances are checked; other card types are ignored.
     * </p>
     * <p>
     * <strong>Example usage:</strong>
     * <pre>
     * List&lt;Card&gt; fusionMonsters = extraDeck.searchBySubType("Fusion Monster");
     * List&lt;Card&gt; xyzMonsters = extraDeck.searchBySubType("XYZ Monster");
     * </pre>
     * </p>
     *
     * @param subType the summoning type to search for (e.g., "Fusion Monster", "Synchro Monster")
     * @return a List of all matching Cards, or an empty list if none match
     */
    public List<Card> searchBySubType(String subType) {
        List<Card> result = new ArrayList<>();
        for (Card c : cards) {
            if (c instanceof MonsterCard) {
                MonsterCard m = (MonsterCard) c;
                String st = m.getMonsterType();
                if (st != null && st.equalsIgnoreCase(subType)) {
                    result.add(c);
                }
            }
        }
        return result;
    }
}