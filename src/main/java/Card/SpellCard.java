package Card;

import Player.Player;
import javafx.scene.image.Image;

/**
 * Represents a Spell Card in Yu-Gi-Oh!
 * <p>
 * Spell cards (also known as Magic cards) are cards that provide various effects
 * to support the player's strategy. They can be activated from the hand or set
 * face-down on the field to be activated later.
 * </p>
 * <p>
 * Spell cards come in different types (Normal, Quick-Play, Equip, Field, Ritual,
 * Continuous) which determine when and how they can be activated. Some spell cards
 * remain on the field after activation (Continuous, Equip, Field), while others
 * are sent to the graveyard immediately after resolving their effect.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class SpellCard extends Card {

    /** The type of spell card (e.g., Normal, Quick-Play, Equip, Field, Ritual, Continuous) */
    private String spellType;

    /** The effect object associated with this spell card */
    private Effect effect;

    /** Whether the spell card remains on the field after activation (true for Continuous, Equip, Field) */
    private boolean isContinuous;

    /** URL pointing to the official card image (typically from YGOPRODeck API) */
    private String imageUrl;

    /**
     * Creates a new Spell Card with full specifications including image URL.
     * <p>
     * This is the primary constructor that allows complete customization of
     * all spell card attributes, including the image source.
     * </p>
     *
     * @param name the name of the spell card
     * @param description the card's effect text
     * @param cardId the unique card ID (password)
     * @param owner the player who owns this spell card
     * @param spellType the type of spell (e.g., "Normal", "Quick-Play", "Equip")
     * @param effect the Effect object containing the card's special ability
     * @param isContinuous true if the card remains on the field after activation, false otherwise
     * @param imageUrl the URL to the card's image (local file or web URL)
     */
    public SpellCard(String name, String description, int cardId, Player owner,
                     String spellType, Effect effect, boolean isContinuous,
                     String imageUrl) {
        super(name, description, cardId, owner);
        this.spellType = spellType;
        this.effect = effect;
        this.isContinuous = isContinuous;
        this.imageUrl = imageUrl;
    }

    /**
     * Creates a new Spell Card without specifying an image URL.
     * <p>
     * This constructor uses a default image ("default.png") for the spell card.
     * Useful when the image is not yet available or when testing.
     * </p>
     *
     * @param name the name of the spell card
     * @param description the card's effect text
     * @param cardId the unique card ID (password)
     * @param owner the player who owns this spell card
     * @param spellType the type of spell (e.g., "Normal", "Quick-Play", "Equip")
     * @param effect the Effect object containing the card's special ability
     * @param isContinuous true if the card remains on the field after activation, false otherwise
     */
    public SpellCard(String name, String description, int cardId, Player owner,
                     String spellType, Effect effect, boolean isContinuous) {
        this(name, description, cardId, owner, spellType, effect, isContinuous, "default.png");
    }

    // --- Getters ---
    
    /**
     * Gets the type of this spell card.
     * <p>
     * Spell types include:
     * <ul>
     * <li>Normal: Can only be activated during your Main Phase</li>
     * <li>Quick-Play: Can be activated during either player's turn</li>
     * <li>Equip: Remains on the field and equips to a monster</li>
     * <li>Field: Remains on the field and affects the entire field</li>
     * <li>Ritual: Used to summon Ritual Monsters</li>
     * <li>Continuous: Remains on the field with a lasting effect</li>
     * </ul>
     * </p>
     *
     * @return the spell type as a String
     */
    public String getSpellType() { 
        return spellType; 
    }
    
    /**
     * Checks if this spell card is continuous.
     * <p>
     * Continuous spell cards remain on the field after activation and provide
     * ongoing effects. This includes Continuous, Equip, and Field spell cards.
     * Non-continuous spells are sent to the graveyard after their effect resolves.
     * </p>
     *
     * @return true if the card remains on the field after activation, false otherwise
     */
    public boolean isContinuous() { 
        return isContinuous; 
    }
    
    /**
     * Gets the effect object associated with this spell card.
     *
     * @return the Effect object, or null if no effect is assigned
     */
    public Effect getEffect() { 
        return effect; 
    }
    
    /**
     * Gets the URL of the spell card's image.
     *
     * @return the image URL as a String
     */
    public String getImageUrl() { 
        return imageUrl; 
    }

    /**
     * Gets the JavaFX Image representation of this spell card.
     * <p>
     * If the image URL is null or empty, returns a default placeholder image
     * located at {@code file:resources/ui/default.png}. Images are loaded
     * with smooth scaling enabled for better visual quality.
     * </p>
     *
     * @return a JavaFX Image object with dimensions 120x180 pixels
     */
    public Image getImage() {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return new Image("file:resources/ui/default.png", 120, 180, true, true);
        }
        return new Image(imageUrl, 120, 180, true, true);
    }

    /**
     * Associates a new effect with this spell card.
     * <p>
     * This method allows dynamic modification of the spell card's effect,
     * which can be useful for cards that gain different effects based on
     * game conditions or for implementing custom card effects.
     * </p>
     *
     * @param effect the new Effect object to assign to this card
     */
    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    /**
     * Activates this spell card's effect.
     * <p>
     * If the card has an associated effect, it is activated with the following
     * parameters: this card as the source, no target (null), and the owner's
     * board as the game context. The effect description is printed to the console
     * for logging purposes.
     * </p>
     * <p>
     * If the card has no effect, a message is printed indicating this.
     * After activation, non-continuous spell cards should be sent to the
     * graveyard by the game logic (not handled by this method).
     * </p>
     */
    @Override
    public void activateEffect() {
        if (effect != null) {
            System.out.println("Activation de l'effet de Magie: " + effect.getDescription());
            effect.activate(this, null, getOwner().getBoard());
        } else {
            System.out.println(getName() + " n'a pas d'effet à activer.");
        }
    }

    /**
     * Returns a string representation of this spell card.
     * <p>
     * The output includes the card's name, spell type, effect description,
     * and password (ID).
     * </p>
     *
     * @return a multi-line string with spell card information
     */
    @Override
    public String toString() {
        return getName() + "\n(" + spellType + " Spell)\n" + getDescription() + 
               "\nPassword: " + getCardId();
    }
}