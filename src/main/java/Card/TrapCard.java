package Card;

import Player.Player;
import javafx.scene.image.Image;

/**
 * Represents a Trap Card in Yu-Gi-Oh!
 * <p>
 * Trap cards are reactive cards that must be set face-down on the field before
 * they can be activated. They are typically used to disrupt the opponent's strategy
 * or protect the player's own cards and life points.
 * </p>
 * <p>
 * Unlike Spell cards, Trap cards cannot be activated on the same turn they are set
 * (with few exceptions). They come in different types (Normal, Counter, Continuous)
 * which determine their activation timing and whether they remain on the field.
 * </p>
 * <p>
 * Counter Trap cards have special properties: they can only be negated by other
 * Counter Traps and have the highest spell speed in the game.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class TrapCard extends Card {

    /** The type of trap card (e.g., Normal, Counter, Continuous) */
    private String trapType;

    /** The effect object associated with this trap card */
    private Effect effect;

    /** Whether the trap card remains on the field after activation (true for Continuous Traps) */
    private boolean isContinuous;

    /** URL pointing to the official card image (typically from YGOPRODeck API) */
    private String imageUrl;

    /**
     * Creates a new Trap Card with full specifications including image URL.
     * <p>
     * This is the primary constructor that allows complete customization of
     * all trap card attributes, including the image source.
     * </p>
     *
     * @param name the name of the trap card
     * @param description the card's effect text
     * @param cardId the unique card ID (password)
     * @param owner the player who owns this trap card
     * @param trapType the type of trap (e.g., "Normal", "Counter", "Continuous")
     * @param effect the Effect object containing the card's special ability
     * @param isContinuous true if the card remains on the field after activation, false otherwise
     * @param imageUrl the URL to the card's image (local file or web URL)
     */
    public TrapCard(String name, String description, int cardId, Player owner,
                    String trapType, Effect effect, boolean isContinuous,
                    String imageUrl) {
        super(name, description, cardId, owner);
        this.trapType = trapType;
        this.effect = effect;
        this.isContinuous = isContinuous;
        this.imageUrl = imageUrl;
    }

    /**
     * Creates a new Trap Card without specifying an image URL.
     * <p>
     * This constructor uses a default image ("default.png") for the trap card.
     * Useful when the image is not yet available or when testing.
     * </p>
     *
     * @param name the name of the trap card
     * @param description the card's effect text
     * @param cardId the unique card ID (password)
     * @param owner the player who owns this trap card
     * @param trapType the type of trap (e.g., "Normal", "Counter", "Continuous")
     * @param effect the Effect object containing the card's special ability
     * @param isContinuous true if the card remains on the field after activation, false otherwise
     */
    public TrapCard(String name, String description, int cardId, Player owner,
                    String trapType, Effect effect, boolean isContinuous) {
        this(name, description, cardId, owner, trapType, effect, isContinuous, "default.png");
    }

    // --- Getters ---
    
    /**
     * Gets the type of this trap card.
     * <p>
     * Trap types include:
     * <ul>
     * <li>Normal: Standard trap cards activated in response to game events</li>
     * <li>Counter: Special traps with spell speed 3 that can negate activations</li>
     * <li>Continuous: Remain on the field and provide ongoing effects</li>
     * </ul>
     * </p>
     *
     * @return the trap type as a String
     */
    public String getTrapType() { 
        return trapType; 
    }
    
    /**
     * Checks if this trap card is continuous.
     * <p>
     * Continuous trap cards remain on the field after activation and provide
     * ongoing effects or can be activated multiple times. Non-continuous traps
     * are sent to the graveyard after their effect resolves.
     * </p>
     *
     * @return true if the card remains on the field after activation, false otherwise
     */
    public boolean isContinuous() { 
        return isContinuous; 
    }
    
    /**
     * Gets the effect object associated with this trap card.
     *
     * @return the Effect object, or null if no effect is assigned
     */
    public Effect getEffect() { 
        return effect; 
    }
    
    /**
     * Gets the URL of the trap card's image.
     *
     * @return the image URL as a String
     */
    public String getImageUrl() { 
        return imageUrl; 
    }

    /**
     * Gets the JavaFX Image representation of this trap card.
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
     * Associates a new effect with this trap card.
     * <p>
     * This method allows dynamic modification of the trap card's effect,
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
     * Activates this trap card's effect.
     * <p>
     * If the card has an associated effect, it is activated with the following
     * parameters: this card as the source, no target (null), and the owner's
     * board as the game context. The effect description is printed to the console
     * for logging purposes.
     * </p>
     * <p>
     * If the card has no effect, a message is printed indicating this.
     * After activation, non-continuous trap cards should be sent to the
     * graveyard by the game logic (not handled by this method).
     * </p>
     * <p>
     * Note: This method does not enforce the rule that traps cannot be activated
     * on the turn they are set. That validation should be handled by the game
     * controller.
     * </p>
     */
    @Override
    public void activateEffect() {
        if (effect != null) {
            System.out.println("Activation de l'effet de Piège: " + effect.getDescription());
            effect.activate(this, null, getOwner().getBoard());
        } else {
            System.out.println(getName() + " n'a pas d'effet à activer.");
        }
    }

    /**
     * Sets whether this trap card is continuous.
     * <p>
     * This method allows dynamic modification of the continuous status,
     * which might be useful for special game mechanics or card effects
     * that change a trap's behavior.
     * </p>
     *
     * @param isContinuous true if the card should remain on the field after activation, false otherwise
     */
    public void setContinuous(boolean isContinuous) {
        this.isContinuous = isContinuous;
    }

    /**
     * Returns a string representation of this trap card.
     * <p>
     * The output includes the card's name, trap type, effect description,
     * and password (ID).
     * </p>
     *
     * @return a multi-line string with trap card information
     */
    @Override
    public String toString() {
        return getName() + "\n (" + trapType + " Trap)\n" + getDescription() + 
               "\nPassword: " + getCardId();
    }
}