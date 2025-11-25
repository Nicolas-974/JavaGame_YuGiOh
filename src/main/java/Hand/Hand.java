package Hand;

import java.util.ArrayList;
import java.util.List;
import Card.Card;

/**
 * Represents a player's hand in Yu-Gi-Oh!
 * <p>
 * The hand contains cards that a player has drawn and can use during the duel.
 * Players can play cards from their hand by summoning monsters, activating spells,
 * or setting cards face-down on the field. Cards can also be discarded from the
 * hand as costs for certain card effects.
 * </p>
 * <p>
 * In official Yu-Gi-Oh! rules, there is no maximum hand size during the duel,
 * but during the End Phase, players must discard down to 6 cards if they have
 * more than that. This class does not enforce hand size limits, leaving that
 * responsibility to the game controller.
 * </p>
 * <p>
 * The hand manages card operations such as adding cards (from drawing), removing
 * cards (when played or discarded), and querying the current hand state.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class Hand {

    // --- Attributes ---
    
    /** The list of cards currently in the player's hand */
    private List<Card> cards;

    // --- Constructor ---
    
    /**
     * Creates a new empty hand.
     * <p>
     * A hand starts empty at the beginning of the game. Players then draw
     * their initial hand (typically 5 cards in official rules) before the
     * first turn begins.
     * </p>
     */
    public Hand() {
        this.cards = new ArrayList<>();
    }

    // --- Main Methods ---

    /**
     * Adds a card to the hand.
     * <p>
     * This method is typically called when a player draws a card from their
     * deck, retrieves a card from the graveyard, or searches their deck for
     * a card and adds it to hand. The card is appended to the end of the
     * hand's card list.
     * </p>
     *
     * @param c the Card to add to the hand
     */
    public void addCard(Card c) {
        cards.add(c);
    }

    /**
     * Removes a specific card from the hand.
     * <p>
     * This method is called when a card is played (summoned, activated, or set),
     * discarded as a cost, or sent to another location. The first occurrence
     * of the specified card is removed from the hand.
     * </p>
     * <p>
     * Note: This method only removes the card from the hand; additional logic
     * is needed to handle where the card goes next (field, graveyard, etc.).
     * </p>
     *
     * @param c the Card to remove from the hand
     * @return true if the card was found and removed, false if it wasn't in the hand
     */
    public boolean removeCard(Card c) {
        return cards.remove(c);
    }

    /**
     * Retrieves a card by its position in the hand.
     * <p>
     * This method allows accessing a specific card by its index, which is
     * useful for UI implementations where players select cards by position
     * rather than by reference. Cards are indexed from 0 to size()-1.
     * </p>
     * <p>
     * If the index is out of bounds, returns null instead of throwing an
     * exception for safer handling.
     * </p>
     *
     * @param index the position of the card in the hand (0-based)
     * @return the Card at the specified index, or null if the index is invalid
     */
    public Card getCard(int index) {
        if (index < 0 || index >= cards.size()) {
            return null;
        }
        return cards.get(index);
    }

    /**
     * Gets all cards currently in the hand.
     * <p>
     * This method returns the actual internal list of cards (not a copy).
     * This allows external code to iterate through the hand or perform
     * operations on all cards. Modifications to the returned list will
     * affect the hand's internal state.
     * </p>
     * <p>
     * <strong>Note:</strong> If you need to prevent external modifications,
     * consider returning a defensive copy: {@code return new ArrayList<>(cards);}
     * </p>
     *
     * @return the List of all cards in the hand
     */
    public List<Card> getAllCards() {
        return cards;
    }

    /**
     * Gets the number of cards currently in the hand.
     * <p>
     * This is useful for checking hand size limits, displaying hand information
     * to players, and implementing card effects that reference hand size.
     * </p>
     *
     * @return the number of cards in the hand
     */
    public int size() {
        return cards.size();
    }

    /**
     * Checks if the hand is empty.
     * <p>
     * An empty hand means the player has no cards to play or discard.
     * Some card effects may check for empty hands as part of their conditions.
     * </p>
     *
     * @return true if there are no cards in the hand, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Checks if a specific card is present in the hand.
     * <p>
     * This method searches for the exact card object (by reference) in the hand.
     * It's useful for validating whether a player actually has a card they're
     * trying to play or for implementing card effects that check hand contents.
     * </p>
     *
     * @param c the Card to search for
     * @return true if the card is in the hand, false otherwise
     */
    public boolean contains(Card c) {
        return cards.contains(c);
    }

    /**
     * Discards a card from the hand.
     * <p>
     * Discarding sends a card from the hand to the graveyard. This is often
     * used as a cost for activating card effects or as part of game rules
     * (such as discarding down to 6 cards during the End Phase).
     * </p>
     * <p>
     * Currently, this method only removes the card from the hand. The calling
     * code should handle sending the card to the graveyard separately. Consider
     * integrating with the Board's {@code sendToGraveyard()} method for complete
     * discard functionality.
     * </p>
     *
     * @param c the Card to discard
     * @return true if the card was successfully discarded, false if it wasn't in the hand
     */
    public boolean discard(Card c) {
        // For now, just simulate discarding by removing the card
        // TODO: Integrate with Board.sendToGraveyard() for complete discard logic
        return removeCard(c);
    }

    /**
     * Removes all cards from the hand.
     * <p>
     * This method empties the entire hand, which is useful when resetting
     * the game state, handling certain card effects that discard the entire
     * hand, or cleaning up after a duel ends.
     * </p>
     * <p>
     * Warning: This method does not send cards to the graveyard; it simply
     * removes them from the hand. Use with caution in game logic.
     * </p>
     */
    public void clear() {
        cards.clear();
    }
}