package Board;

import Card.Card;
import Card.MonsterCard;
import Card.SpellCard;
import Card.TrapCard;
import Deck.Deck;
import Deck.ExtraDeck;
import Graveyard.Graveyard;
import BanishedZone.BanishedZone;
import Player.Player;
import Card.Position;

/**
 * Represents a player's game board/field in Yu-Gi-Oh!
 * <p>
 * The Board class manages all zones where cards can be placed during a duel,
 * including monster zones, spell/trap zones, field spell zone, graveyard, and
 * banished zone. It serves as the central hub for card placement, removal, and
 * zone-related operations.
 * </p>
 * <p>
 * The board follows the official Yu-Gi-Oh! field layout:
 * <ul>
 * <li>5 Monster Zones for summoning monsters</li>
 * <li>5 Spell/Trap Zones for spell and trap cards</li>
 * <li>1 Field Spell Zone for field spell cards</li>
 * <li>1 Graveyard for discarded/destroyed cards</li>
 * <li>1 Banished Zone for cards removed from play</li>
 * <li>References to the player's Deck and Extra Deck</li>
 * </ul>
 * </p>
 * <p>
 * The board also tracks important game state information such as the last
 * summoned monster and the last destroyed card, which is useful for implementing
 * card effects that reference recent actions.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class Board {

    // --- Attributes ---
    
    /** Array of 5 monster zones, indexed 0-4 */
    private MonsterCard[] monsterZones = new MonsterCard[5];
    
    /** Array of 5 spell/trap zones, indexed 0-4 */
    private Card[] spellTrapZones = new Card[5];
    
    /** The player's main deck */
    private Deck deck;
    
    /** The player's extra deck (for special summon monsters) */
    private ExtraDeck extraDeck;
    
    /** The player's graveyard where destroyed/discarded cards are sent */
    private Graveyard graveyard;
    
    /** The banished zone for cards removed from play */
    private BanishedZone banishedZone;
    
    /** The field spell zone (only one field spell can be active at a time) */
    private Card fieldZone;
    
    /** The player who owns this board */
    private Player owner;
    
    /** Reference to the most recently summoned monster (useful for card effects) */
    private MonsterCard lastSummonedMonster;
    
    /** Reference to the most recently destroyed card (useful for card effects) */
    private Card lastDestroyedCard;

    /**
     * Creates a new game board for a player.
     * <p>
     * Initializes all zones as empty, creates new Graveyard and BanishedZone,
     * and links the board to the player's deck and extra deck.
     * </p>
     *
     * @param owner the Player who owns this board
     * @param deck the player's main deck
     * @param extraDeck the player's extra deck
     */
    public Board(Player owner, Deck deck, ExtraDeck extraDeck) {
        this.owner = owner;
        this.deck = deck;
        this.extraDeck = extraDeck;
        this.graveyard = new Graveyard();
        this.banishedZone = new BanishedZone();
        this.fieldZone = null;
    }

    // --- Monster Management ---
    
    /**
     * Places a monster card in the specified monster zone.
     * <p>
     * The monster is placed in the specified position (Attack or Defense) and
     * becomes the last summoned monster. This method handles both Normal Summons
     * and Special Summons, though additional validation for summon conditions
     * should be performed before calling this method.
     * </p>
     * <p>
     * If the specified zone is invalid or already occupied, the operation fails
     * and an error message is printed.
     * </p>
     *
     * @param card the MonsterCard to place on the field
     * @param zoneIndex the monster zone index (0-4)
     * @param pos the Position to place the monster in (ATTACK or DEFENSE)
     */
    public void placeMonster(MonsterCard card, int zoneIndex, Position pos) {
        if (zoneIndex < 0 || zoneIndex >= monsterZones.length) {
            System.out.println("Invalid monster zone.");
            return;
        }
        if (monsterZones[zoneIndex] == null) {
            monsterZones[zoneIndex] = card;
            card.setPosition(pos);
            lastSummonedMonster = card;
            System.out.println(owner.getName() + " summons " + card.getName() + " in " + pos);
        } else {
            System.out.println("Monster zone " + zoneIndex + " is already occupied.");
        }
    }

    /**
     * Gets the most recently summoned monster.
     * <p>
     * This is useful for implementing card effects that trigger when a monster
     * is summoned or that target the last summoned monster.
     * </p>
     *
     * @return the last MonsterCard that was summoned, or null if no monster has been summoned yet
     */
    public MonsterCard getLastSummonedMonster() {
        return lastSummonedMonster;
    }

    /**
     * Removes a monster from the specified zone and sends it to the graveyard.
     * <p>
     * This method is typically called when a monster is destroyed in battle,
     * by a card effect, or when it's used as tribute. The removed monster is
     * automatically sent to the graveyard and becomes the last destroyed card.
     * </p>
     * <p>
     * If the zone index is invalid or the zone is empty, returns null.
     * </p>
     *
     * @param zoneIndex the monster zone index (0-4) to remove from
     * @return the removed MonsterCard, or null if the zone was empty or invalid
     */
    public MonsterCard removeMonster(int zoneIndex) {
        if (zoneIndex < 0 || zoneIndex >= monsterZones.length) return null;
        MonsterCard removed = monsterZones[zoneIndex];
        monsterZones[zoneIndex] = null;
        if (removed != null) {
            lastDestroyedCard = removed;
            sendToGraveyard(removed);
            System.out.println(removed.getName() + " is destroyed and sent to the Graveyard.");
        }
        return removed;
    }

    /**
     * Checks if the specified monster zone is available.
     * <p>
     * A zone is considered free if it's within valid range (0-4) and doesn't
     * currently contain a monster card.
     * </p>
     *
     * @param zoneIndex the monster zone index to check (0-4)
     * @return true if the zone is free, false if occupied or invalid
     */
    public boolean isMonsterZoneFree(int zoneIndex) {
        return zoneIndex >= 0 && zoneIndex < monsterZones.length && monsterZones[zoneIndex] == null;
    }

    /**
     * Gets the monster card in the specified zone.
     * <p>
     * This method provides direct access to monsters on the field for
     * battle calculations, effect targeting, and game state queries.
     * </p>
     *
     * @param zoneIndex the monster zone index (0-4)
     * @return the MonsterCard in that zone, or null if the zone is empty
     */
    public MonsterCard getMonster(int zoneIndex) { 
        return monsterZones[zoneIndex]; 
    }

    // --- Spell/Trap Management ---
    
    /**
     * Sets (places face-down) a spell or trap card in the specified zone.
     * <p>
     * Spell and trap cards can be set face-down to be activated later.
     * Spell cards can also be activated immediately from the hand, but this
     * method specifically handles the "set" action.
     * </p>
     * <p>
     * If the zone is invalid or already occupied, the operation fails with
     * an error message.
     * </p>
     *
     * @param card the spell or trap Card to set
     * @param zoneIndex the spell/trap zone index (0-4)
     */
    public void setSpellTrap(Card card, int zoneIndex) {
        if (zoneIndex < 0 || zoneIndex >= spellTrapZones.length) {
            System.out.println("Invalid Spell/Trap zone.");
            return;
        }
        if (spellTrapZones[zoneIndex] == null) {
            spellTrapZones[zoneIndex] = card;
            System.out.println(owner.getName() + " sets a Spell/Trap card: " + card.getName());
        } else {
            System.out.println("Spell/Trap zone " + zoneIndex + " is already occupied.");
        }
    }

    /**
     * Activates a spell card and sends it to the graveyard.
     * <p>
     * This method triggers the spell card's effect and, since most spell cards
     * (except Continuous, Equip, and Field spells) go to the graveyard after
     * activation, automatically sends the card there.
     * </p>
     * <p>
     * Note: For Continuous, Equip, and Field spells, additional logic should
     * be added to keep them on the field after activation.
     * </p>
     *
     * @param card the SpellCard to activate
     */
    public void activateSpell(SpellCard card) {
        if (card != null) {
            card.activateEffect();
            sendToGraveyard(card);
        }
    }

    /**
     * Activates a trap card and sends it to the graveyard if it's not continuous.
     * <p>
     * Trap cards must be set for at least one turn before activation (with few
     * exceptions). This method triggers the trap's effect and, if it's not a
     * Continuous Trap, sends it to the graveyard after resolution.
     * </p>
     * <p>
     * Continuous Traps remain on the field after activation and continue to
     * provide their effects.
     * </p>
     *
     * @param card the TrapCard to activate
     */
    public void activateTrap(TrapCard card) {
        if (card != null) {
            card.activateEffect();
            if (!card.isContinuous()) {
                sendToGraveyard(card);
            }
        }
    }

    /**
     * Removes a card from the specified spell/trap zone.
     * <p>
     * This method is used when a spell/trap card is destroyed, returned to hand,
     * or otherwise removed from the field. Unlike {@link #removeMonster(int)},
     * this method does not automatically send the card to the graveyard; that
     * must be handled separately based on the game context.
     * </p>
     *
     * @param zoneIndex the spell/trap zone index (0-4)
     * @return the removed Card, or null if the zone was empty or invalid
     */
    public Card removeSpellTrap(int zoneIndex) {
        if (zoneIndex < 0 || zoneIndex >= spellTrapZones.length) {
            System.out.println("Invalid Spell/Trap zone.");
            return null;
        }
        Card removed = spellTrapZones[zoneIndex];
        spellTrapZones[zoneIndex] = null;
        if (removed != null) {
            System.out.println(removed.getName() + " is removed from the Spell/Trap zone.");
        }
        return removed;
    }
    
    /**
     * Checks if the specified spell/trap zone is available.
     *
     * @param zoneIndex the spell/trap zone index to check (0-4)
     * @return true if the zone is free, false if occupied
     */
    public boolean isSpellTrapZoneFree(int zoneIndex) { 
        return spellTrapZones[zoneIndex] == null; 
    }

    // --- Graveyard and Banished Zone Management ---
    
    /**
     * Sends a card to the graveyard.
     * <p>
     * Cards are sent to the graveyard when they are destroyed, discarded, or
     * used as costs for other effects. Cards in the graveyard can potentially
     * be retrieved by certain card effects.
     * </p>
     *
     * @param card the Card to send to the graveyard
     */
    public void sendToGraveyard(Card card) {
        graveyard.addCard(card);
        System.out.println(card.getName() + " is sent to the Graveyard.");
    }

    /**
     * Banishes (removes from play) a card.
     * <p>
     * Banished cards are removed from the game and are much harder to retrieve
     * than cards in the graveyard. Some card effects specifically banish cards
     * as a cost or effect to prevent them from being easily recovered.
     * </p>
     *
     * @param card the Card to banish
     */
    public void banishCard(Card card) {
        banishedZone.addCard(card);
        System.out.println(card.getName() + " is removed from play (Banished).");
    }

    // --- Deck and Extra Deck Management ---
    
    /**
     * Draws a card from the player's deck.
     * <p>
     * This method is typically called during the Draw Phase or when a card
     * effect allows the player to draw. The drawn card should be added to
     * the player's hand by the calling code.
     * </p>
     *
     * @return the drawn Card, or null if the deck is empty (deck out condition)
     */
    public Card drawCard() { 
        return deck.draw(); 
    }
    
    /**
     * Shuffles the player's deck.
     * <p>
     * This method is called when the deck needs to be randomized, such as
     * at the start of the game or when a card effect requires it.
     * </p>
     */
    public void shuffleDeck() { 
        deck.shuffle(); 
    }
    
    /**
     * Gets the number of cards remaining in the main deck.
     *
     * @return the deck size
     */
    public int getDeckCount() { 
        return deck.size(); 
    }
    
    /**
     * Gets the number of cards remaining in the extra deck.
     *
     * @return the extra deck size
     */
    public int getExtraDeckCount() { 
        return extraDeck.size(); 
    }

    // --- Field Spell Zone Management ---
    
    /**
     * Places a field spell card in the field zone.
     * <p>
     * Only one field spell can be active at a time. If a new field spell is
     * activated while one is already present, the old one is destroyed and
     * sent to the graveyard (though this logic should be implemented by the
     * calling code before calling this method).
     * </p>
     *
     * @param card the field spell Card to place
     */
    public void setFieldSpell(Card card) { 
        this.fieldZone = card; 
    }
    
    /**
     * Removes the current field spell from the field zone.
     * <p>
     * This is typically called when the field spell is destroyed or replaced
     * by another field spell.
     * </p>
     */
    public void removeFieldSpell() { 
        this.fieldZone = null; 
    }
    
    /**
     * Checks if the field spell zone is available.
     *
     * @return true if no field spell is currently active, false otherwise
     */
    public boolean isFieldZoneFree() { 
        return fieldZone == null; 
    }

    // --- Utility Methods ---

    /**
     * Counts the number of monsters currently on the field.
     * <p>
     * This method iterates through all monster zones and counts non-null entries.
     * Useful for card effects that reference the number of monsters on the field.
     * </p>
     *
     * @return the number of monsters currently on the field (0-5)
     */
    public int getMonsterCount() {
        int count = 0;
        for (MonsterCard monster : monsterZones) {
            if (monster != null) count++;
        }
        return count;
    }

    /**
     * Counts the number of spell/trap cards currently on the field.
     * <p>
     * This includes both set cards and face-up continuous spells/traps.
     * </p>
     *
     * @return the number of spell/trap cards currently on the field (0-5)
     */
    public int getSpellTrapCount() {
        int count = 0;
        for (Card card : spellTrapZones) {
            if (card != null) count++;
        }
        return count;
    }

    /**
     * Counts the total number of cards on the entire field.
     * <p>
     * This includes all monsters, spell/trap cards, and the field spell if present.
     * Useful for card effects that reference the total number of cards on the field.
     * </p>
     *
     * @return the total number of cards on the field (0-11)
     */
    public int getTotalCardsOnField() {
        int total = getMonsterCount() + getSpellTrapCount();
        if (fieldZone != null) total++;
        return total;
    }

    /**
     * Clears all cards from the board.
     * <p>
     * This method removes all monsters, spell/trap cards, and the field spell
     * from the field, and resets tracking variables. The cards are not sent
     * to the graveyard; they simply disappear from the field.
     * </p>
     * <p>
     * This is typically used for board-wiping card effects or when resetting
     * the game state.
     * </p>
     */
    public void clearBoard() {
        for (int i = 0; i < monsterZones.length; i++) {
            monsterZones[i] = null;
        }
        for (int i = 0; i < spellTrapZones.length; i++) {
            spellTrapZones[i] = null;
        }
        fieldZone = null;
        lastSummonedMonster = null;
        lastDestroyedCard = null;
        System.out.println(owner.getName() + "'s field is cleared.");
    }

    /**
     * Completely resets the board to its initial state.
     * <p>
     * This method clears all cards from the field and also creates new
     * Graveyard and BanishedZone instances, effectively erasing all game history.
     * This is more thorough than {@link #clearBoard()} and is typically used
     * when starting a new game or duel.
     * </p>
     */
    public void resetBoard() {
        clearBoard();
        graveyard = new Graveyard();
        banishedZone = new BanishedZone();
        System.out.println(owner.getName() + "'s board is reset.");
    }

    /**
     * Gets the player's main deck.
     *
     * @return the Deck object
     */
    public Deck getDeck() { 
        return deck; 
    }
    
    /**
     * Gets the player's graveyard.
     *
     * @return the Graveyard object
     */
    public Graveyard getGraveyard() { 
        return graveyard; 
    }
    
    /**
     * Gets the banished zone.
     *
     * @return the BanishedZone object
     */
    public BanishedZone getBanished() { 
        return banishedZone; 
    }
}