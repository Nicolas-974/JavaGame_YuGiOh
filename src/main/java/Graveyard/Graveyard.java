package Graveyard;

import java.util.ArrayList;
import java.util.List;
import Card.Card;

/**
 * Represents a player's Graveyard in Yu-Gi-Oh!
 * <p>
 * The Graveyard (also known as the Discard Pile) is where cards are sent when
 * they are destroyed, discarded, used as costs, or otherwise removed from play
 * through normal game mechanics. Unlike banished cards, cards in the Graveyard
 * are considered part of the game state and can be retrieved or interacted with
 * by various card effects.
 * </p>
 * <p>
 * In official Yu-Gi-Oh! rules, the Graveyard is a public zone that both players
 * can view at any time. Cards in the Graveyard maintain their identity and
 * properties, allowing effects to target or retrieve specific cards based on
 * their characteristics.
 * </p>
 * <p>
 * The Graveyard supports operations such as adding cards, removing cards (for
 * revival or banishment), searching for specific cards, and viewing the most
 * recently sent card (useful for "when this card is sent to the Graveyard" effects).
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class Graveyard {

    // --- Attributes ---
    
    /** The list of cards in the Graveyard, ordered by when they were sent */
    private List<Card> cards;

    // --- Constructor ---
    
    /**
     * Creates a new empty Graveyard.
     * <p>
     * A Graveyard starts empty at the beginning of the game and gradually
     * fills as cards are destroyed, discarded, or sent there by card effects.
     * </p>
     */
    public Graveyard() {
        this.cards = new ArrayList<>();
    }

    // --- Main Methods ---

    /**
     * Adds a card to the Graveyard.
     * <p>
     * This method is called when a card is destroyed in battle, destroyed by
     * a card effect, discarded from the hand, sent from the deck, or detached
     * as material. The card is appended to the end of the Graveyard, making it
     * the most recently added card.
     * </p>
     * <p>
     * The operation is logged to the console for game tracking purposes.
     * </p>
     *
     * @param c the Card to send to the Graveyard
     */
    public void addCard(Card c) {
        cards.add(c);
        System.out.println("Card sent to the Graveyard: " + c.getName());
    }

    /**
     * Removes a specific card from the Graveyard.
     * <p>
     * This method is called when a card is retrieved from the Graveyard
     * (returned to hand, Special Summoned, banished, etc.). The first occurrence
     * of the specified card is removed from the Graveyard.
     * </p>
     * <p>
     * Common scenarios include:
     * <ul>
     * <li>Monster Reborn effect: removing a monster to Special Summon it</li>
     * <li>Pot of Avarice effect: removing cards to shuffle back into the deck</li>
     * <li>Macro Cosmos effect: banishing instead of sending to Graveyard</li>
     * </ul>
     * </p>
     *
     * @param c the Card to remove from the Graveyard
     * @return true if the card was found and removed, false if it wasn't in the Graveyard
     */
    public boolean removeCard(Card c) {
        return cards.remove(c);
    }

    /**
     * Gets the most recently sent card in the Graveyard.
     * <p>
     * This method returns the last card that was added to the Graveyard, which
     * is useful for implementing card effects that trigger "when a card is sent
     * to the Graveyard" or effects that reference "the last card sent to the
     * Graveyard."
     * </p>
     * <p>
     * If the Graveyard is empty, returns null.
     * </p>
     *
     * @return the most recently added Card, or null if the Graveyard is empty
     */
    public Card getLastCard() {
        if (isEmpty()) return null;
        return cards.get(cards.size() - 1);
    }

    /**
     * Searches for a card in the Graveyard by its exact name.
     * <p>
     * This method performs a case-insensitive search through the Graveyard
     * to find a card matching the specified name. This is useful for implementing
     * card effects that retrieve specific named cards from the Graveyard.
     * </p>
     * <p>
     * If multiple cards with the same name exist in the Graveyard, only the
     * first occurrence is returned.
     * </p>
     *
     * @param name the exact name of the card to search for (case-insensitive)
     * @return the first Card with a matching name, or null if no match is found
     */
    public Card searchByName(String name) {
        for (Card c : cards) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Gets the number of cards currently in the Graveyard.
     * <p>
     * This is useful for card effects that reference Graveyard size, such as
     * "Lightsworn" effects that mill cards, or "Infernoid" effects that require
     * a certain number of banished cards.
     * </p>
     *
     * @return the number of cards in the Graveyard
     */
    public int size() {
        return cards.size();
    }

    /**
     * Checks if the Graveyard is empty.
     * <p>
     * An empty Graveyard means no cards have been destroyed or discarded yet,
     * or all cards have been removed by card effects. Some card effects may
     * have conditions based on whether the Graveyard is empty.
     * </p>
     *
     * @return true if there are no cards in the Graveyard, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets all cards currently in the Graveyard.
     * <p>
     * This method returns the actual internal list of cards (not a copy),
     * allowing external code to iterate through or examine all cards in the
     * Graveyard. This is useful for implementing card effects that interact
     * with multiple Graveyard cards or for displaying Graveyard contents to
     * players.
     * </p>
     * <p>
     * Since the Graveyard is a public zone in Yu-Gi-Oh!, both players should
     * be able to view its contents at any time.
     * </p>
     * <p>
     * <strong>Note:</strong> Modifications to the returned list will affect
     * the Graveyard's internal state. Consider returning a defensive copy if
     * you need to prevent external modifications: {@code return new ArrayList<>(cards);}
     * </p>
     *
     * @return the List of all cards in the Graveyard
     */
    public List<Card> getAllCards() {
        return cards;
    }
}