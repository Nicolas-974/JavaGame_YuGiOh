package Card;

import Player.Player;
import javafx.scene.image.Image;

/**
 * Abstract base class representing a Yu-Gi-Oh! card.
 * <p>
 * This class provides the common attributes and behaviors shared by all card types
 * (Monster, Spell, Trap). Each card has a name, description, unique ID, owner,
 * and can be placed face-up or face-down on the field.
 * </p>
 * <p>
 * The card's visual representation is managed through an image URL with caching
 * to improve performance when loading the same card multiple times.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public abstract class Card {
    
    /** The name of the card (e.g., "Blue-Eyes White Dragon") */
    private final String name;
    
    /** The card's effect or flavor text */
    private final String description;
    
    /** The unique 8-digit card ID (also called "password") */
    private final int cardId;
    
    /** The player who currently owns this card */
    private Player owner;
    
    /** Whether the card is currently face-down on the field */
    private boolean isFaceDown;

    /** URL pointing to the official card image */
    private String imageUrl;
    
    /** Cached JavaFX Image instance to avoid reloading from URL */
    private Image cachedImage = null;

    /**
     * Creates a new card with default image.
     * <p>
     * This constructor uses a default placeholder image located at
     * {@code file:resources/ui/default.png}.
     * </p>
     *
     * @param name the name of the card
     * @param description the card's effect text or flavor text
     * @param cardId the unique card ID (password)
     * @param owner the player who owns this card
     */
    public Card(String name, String description, int cardId, Player owner) {
        this(name, description, cardId, owner, "file:resources/ui/default.png");
    }

    /**
     * Creates a new card with a specified image URL.
     * <p>
     * The card is initially face-up by default.
     * </p>
     *
     * @param name the name of the card
     * @param description the card's effect text or flavor text
     * @param cardId the unique card ID (password)
     * @param owner the player who owns this card
     * @param imageUrl the URL to the card's image (local file or web URL)
     */
    public Card(String name, String description, int cardId, Player owner, String imageUrl) {
        this.name = name;
        this.description = description;
        this.cardId = cardId;
        this.owner = owner;
        this.isFaceDown = false;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the name of the card.
     *
     * @return the card's name
     */
    public String getName() { 
        return name; 
    }

    /**
     * Gets the card's description (effect text or flavor text).
     *
     * @return the card's description
     */
    public String getDescription() { 
        return description; 
    }

    /**
     * Gets the unique card ID (password).
     *
     * @return the 8-digit card ID
     */
    public int getCardId() { 
        return cardId; 
    }

    /**
     * Gets the player who owns this card.
     *
     * @return the owner of this card
     */
    public Player getOwner() { 
        return owner; 
    }

    /**
     * Gets the URL of the card's image.
     *
     * @return the image URL as a String
     */
    public String getImageUrl() { 
        return imageUrl; 
    }

    /**
     * Gets the JavaFX Image representation of this card.
     * <p>
     * This method implements lazy loading with caching. The image is only
     * loaded once from the URL, then cached for subsequent calls. Images
     * are loaded in the background to avoid blocking the UI thread.
     * </p>
     * <p>
     * If the image URL is null or empty, returns a default placeholder image.
     * </p>
     *
     * @return a JavaFX Image object representing the card's visual appearance
     */
    public Image getImage() {
        if (cachedImage == null) {
            if (imageUrl == null || imageUrl.isEmpty()) {
                cachedImage = new Image("file:resources/ui/default.png", 120, 180, true, true);
            } else {
                // Background loading (last parameter = true) to avoid UI blocking
                cachedImage = new Image(imageUrl, 120, 180, true, true, true);
            }
        }
        return cachedImage;
    }

    /**
     * Sets the owner of this card.
     * <p>
     * This is typically used when a card changes control between players.
     * </p>
     *
     * @param owner the new owner of this card
     */
    public void setOwner(Player owner) { 
        this.owner = owner; 
    }

    /**
     * Sets whether this card is face-down on the field.
     *
     * @param value true if the card should be face-down, false for face-up
     */
    public void setFaceDown(boolean value) { 
        this.isFaceDown = value; 
    }

    /**
     * Checks if this card is currently face-down.
     *
     * @return true if face-down, false if face-up
     */
    public boolean isFaceDown() { 
        return isFaceDown; 
    }

    /**
     * Flips this card between face-up and face-down positions.
     * <p>
     * If the card is face-up, it becomes face-down, and vice versa.
     * This is commonly used for flip effects or manual flipping during gameplay.
     * </p>
     */
    public void flip() { 
        this.isFaceDown = !this.isFaceDown; 
    }

    /**
     * Activates this card's effect.
     * <p>
     * This is an abstract method that must be implemented by all subclasses
     * (MonsterCard, SpellCard, TrapCard). The specific effect depends on
     * the card type and the game situation.
     * </p>
     */
    public abstract void activateEffect();

    /**
     * Returns a string representation of this card.
     * <p>
     * If the card is face-down, returns "Carte face cachée" (hidden card).
     * Otherwise, returns the card's name, description, and face-up status.
     * </p>
     *
     * @return a string describing the card's current state
     */
    @Override
    public String toString() {
        if (isFaceDown) return "Carte face cachée";
        return name + " - " + description + " (Face-Up)";
    }
}