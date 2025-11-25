package BanishedZone;

import java.util.ArrayList;
import java.util.List;
import Card.Card;

/**
 * Represents a player's Banished Zone (also known as "Removed from Play") in Yu-Gi-Oh!
 * <p>
 * The Banished Zone is where cards are sent when they are removed from play by card
 * effects. Unlike the Graveyard, cards in the Banished Zone are much more difficult
 * to retrieve and interact with. Banishing is often used as a stronger form of removal
 * that prevents cards from being easily recovered.
 * </p>
 * <p>
 * In official Yu-Gi-Oh! rules, the Banished Zone is a public zone visible to both
 * players. Cards can be banished face-up or face-down (though this implementation
 * currently doesn't distinguish between the two). Face-down banished cards have
 * additional restrictions on how they can be interacted with.
 * </p>
 * <p>
 * Common ways cards are banished:
 * <ul>
 * <li>Card effects that specifically banish cards (e.g., "Dimensional Fissure")</li>
 * <li>Costs for activating certain card effects</li>
 * <li>Replacement effects that banish instead of sending to the Graveyard</li>
 * <li>Effects that temporarily banish cards and return them later</li>
 * </ul>
 * </p>
 * <p>
 * While banished cards are generally harder to recover than graveyard cards, certain
 * card effects can retrieve banished cards or interact with them in specific ways.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class BanishedZone {

    // --- Attributes ---
    
    /** The list of cards that have been banished (removed from play) */
    private List<Card> cards;

    // --- Constructor ---
    
    /**
     * Creates a new empty Banished Zone.
     * <p>
     * The Banished Zone starts empty at the beginning of the game and gradually
     * fills as cards are removed from play by various card effects.
     * </p>
     */
    public BanishedZone() {
        this.cards = new ArrayList<>();
    }

    // --- Main Methods ---

    /**
     * Adds a card to the Banished Zone.
     * <p>
     * This method is called when a card is removed from play by a card effect,
     * used as a cost that requires banishing, or replaced by an effect that
     * banishes instead of sending to the Graveyard (such as "Macro Cosmos").
     * </p>
     * <p>
     * Banished cards are much harder to retrieve than cards in the Graveyard,
     * making banishment a powerful form of removal. The operation is logged to
     * the console for game tracking purposes.
     * </p>
     *
     * @param c the Card to banish (remove from play)
     */
    public void addCard(Card c) {
        cards.add(c);
        System.out.println("Card banished: " + c.getName());
    }

    /**
     * Removes a specific card from the Banished Zone.
     * <p>
     * This method is called when a card effect retrieves a banished card and
     * returns it to another zone (hand, deck, field, or Graveyard). While less
     * common than retrieving from the Graveyard, some card effects specifically
     * interact with banished cards.
     * </p>
     * <p>
     * Examples include:
     * <ul>
     * <li>"Burial from a Different Dimension" - returns banished monsters to Graveyard</li>
     * <li>"Necroface" - shuffles banished cards back into the deck</li>
     * <li>"Leviair the Sea Dragon" - Special Summons banished Level 4 or lower monsters</li>
     * </ul>
     * </p>
     *
     * @param c the Card to remove from the Banished Zone
     * @return true if the card was found and removed, false if it wasn't banished
     */
    public boolean removeCard(Card c) {
        return cards.remove(c);
    }

    /**
     * Checks if a specific card is present in the Banished Zone.
     * <p>
     * This method searches for the exact card object (by reference) in the
     * Banished Zone. It's useful for verifying whether a card has been banished
     * or for implementing card effects that check for specific banished cards.
     * </p>
     *
     * @param c the Card to search for
     * @return true if the card is banished, false otherwise
     */
    public boolean contains(Card c) {
        return cards.contains(c);
    }

    /**
     * Gets the number of cards currently banished.
     * <p>
     * This is useful for card effects that reference the number of banished cards,
     * such as "Gren Maju Da Eiza" (gains ATK based on banished cards) or "Infernoid"
     * effects that require a certain number of banished cards as activation costs.
     * </p>
     *
     * @return the number of cards in the Banished Zone
     */
    public int size() {
        return cards.size();
    }

    /**
     * Checks if the Banished Zone is empty.
     * <p>
     * An empty Banished Zone means no cards have been removed from play yet,
     * or all banished cards have been retrieved by card effects. Some card
     * effects may have conditions or benefits based on whether any cards are
     * banished.
     * </p>
     *
     * @return true if there are no banished cards, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets all cards currently in the Banished Zone.
     * <p>
     * This method returns the actual internal list of cards (not a copy),
     * allowing external code to iterate through or examine all banished cards.
     * This is useful for implementing card effects that interact with multiple
     * banished cards or for displaying Banished Zone contents to players.
     * </p>
     * <p>
     * Since the Banished Zone is a public zone in Yu-Gi-Oh! (for face-up banished
     * cards), both players should be able to view its contents at any time.
     * </p>
     * <p>
     * <strong>Note:</strong> Modifications to the returned list will affect
     * the Banished Zone's internal state. Consider returning a defensive copy if
     * you need to prevent external modifications: {@code return new ArrayList<>(cards);}
     * </p>
     * <p>
     * <strong>Future Enhancement:</strong> Consider tracking whether cards are
     * banished face-up or face-down, as this affects which effects can interact
     * with them.
     * </p>
     *
     * @return the List of all cards in the Banished Zone
     */
    public List<Card> getAllCards() {
        return cards;
    }
}