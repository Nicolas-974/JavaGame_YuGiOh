package Card;

import Player.Player;
import javafx.scene.image.Image;

/**
 * Represents a Monster Card in Yu-Gi-Oh!
 * <p>
 * Monster cards are the primary cards used for attacking and defending in the game.
 * Each monster has attack and defense points, a level, and various attributes that
 * define its characteristics and battle capabilities.
 * </p>
 * <p>
 * Monsters can be placed on the field in different positions (Attack or Defense),
 * and have turn-based restrictions on position changes and attacks to prevent
 * multiple actions in a single turn.
 * </p>
 * <p>
 * This class provides multiple constructors to accommodate different levels of
 * detail when creating monster cards, from minimal information to full specifications
 * including effects and images.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class MonsterCard extends Card {

    /** The monster's attack points (ATK) */
    private int atk;
    
    /** The monster's defense points (DEF) */
    private int def;
    
    /** The monster's level (number of stars, from 1 to 12) */
    private int level;
    
    /** The monster's attribute (DARK, LIGHT, WATER, FIRE, EARTH, WIND, DIVINE) */
    private String attribute;
    
    /** The monster's type/race (Dragon, Warrior, Spellcaster, etc.) */
    private String type;
    
    /** The monster's sub-type (Normal, Effect, Fusion, etc.) */
    private String subType;
    
    /** The monster's specific type category (Normal Monster, Effect Monster, etc.) */
    private String monsterType;
    
    /** The special effect object associated with this monster, if any */
    private Effect effect;
    
    /** The type/category of the effect (Trigger, Ignition, Quick, etc.) */
    private String effectType;
    
    /** The current battle position of the monster on the field */
    private Position position;
    
    /** Flag indicating if the monster has already attacked this turn */
    private boolean hasAttackedThisTurn;
    
    /** Flag indicating if the monster has already changed position this turn */
    private boolean hasChangedPositionThisTurn;

    /** URL pointing to the official card image (typically from YGOPRODeck API) */
    private String imageUrl;

    /**
     * Creates a new Monster Card with full specifications including image URL.
     * <p>
     * This is the primary constructor that all other constructors delegate to.
     * The monster is initially placed in Attack Position with no turn restrictions.
     * </p>
     *
     * @param name the name of the monster
     * @param description the monster's effect text or flavor text
     * @param cardId the unique card ID (password)
     * @param owner the player who owns this monster
     * @param atk the attack points (ATK) of the monster
     * @param def the defense points (DEF) of the monster
     * @param level the level of the monster (1-12 stars)
     * @param attribute the monster's attribute (e.g., "DARK", "LIGHT")
     * @param type the monster's type/race (e.g., "Dragon", "Warrior")
     * @param subType the monster's sub-type (e.g., "Normal", "Effect")
     * @param monsterType the specific monster category
     * @param imageUrl the URL to the monster's image (local file or web URL)
     */
    public MonsterCard(String name, String description, int cardId, Player owner,
                       int atk, int def, int level,
                       String attribute, String type, String subType, String monsterType,
                       String imageUrl) {
        super(name, description, cardId, owner);
        this.atk = atk;
        this.def = def;
        this.level = level;
        this.attribute = attribute;
        this.type = type;
        this.subType = subType;
        this.monsterType = monsterType;
        this.position = Position.ATTACK;
        this.hasAttackedThisTurn = false;
        this.hasChangedPositionThisTurn = false;
        this.imageUrl = imageUrl;
    }

    /**
     * Creates a new Monster Card without specifying an image URL.
     * <p>
     * This constructor uses a default image ("default.png") for the monster.
     * Useful when the image is not yet available or when testing.
     * </p>
     *
     * @param name the name of the monster
     * @param desc the monster's effect text or flavor text
     * @param id the unique card ID (password)
     * @param owner the player who owns this monster
     * @param atk the attack points (ATK) of the monster
     * @param def the defense points (DEF) of the monster
     * @param level the level of the monster (1-12 stars)
     * @param attribute the monster's attribute
     * @param race the monster's type/race
     * @param subType the monster's sub-type
     * @param monsterType the specific monster category
     */
    public MonsterCard(String name, String desc, int id, Player owner,
                       int atk, int def, int level,
                       String attribute, String race,
                       String subType, String monsterType) {
        this(name, desc, id, owner, atk, def, level, attribute, race, subType, monsterType, "default.png");
    }

    /**
     * Creates a new Monster Card with minimal information.
     * <p>
     * This constructor assumes a "Normal" sub-type and uses a default image.
     * Ideal for creating simple Normal Monsters quickly.
     * </p>
     *
     * @param name the name of the monster
     * @param desc the monster's flavor text
     * @param id the unique card ID (password)
     * @param owner the player who owns this monster
     * @param atk the attack points (ATK) of the monster
     * @param def the defense points (DEF) of the monster
     * @param level the level of the monster (1-12 stars)
     * @param attribute the monster's attribute
     * @param race the monster's type/race
     * @param monsterType the specific monster category
     */
    public MonsterCard(String name, String desc, int id, Player owner,
                       int atk, int def, int level,
                       String attribute, String race,
                       String monsterType) {
        this(name, desc, id, owner, atk, def, level, attribute, race, "Normal", monsterType, "default.png");
    }

    /**
     * Creates a new Monster Card with an associated effect.
     * <p>
     * This constructor is used for Effect Monsters that have special abilities.
     * The effect object encapsulates the monster's special power, and the
     * effectType categorizes when and how it can be activated.
     * </p>
     *
     * @param name the name of the monster
     * @param desc the monster's effect text
     * @param id the unique card ID (password)
     * @param owner the player who owns this monster
     * @param atk the attack points (ATK) of the monster
     * @param def the defense points (DEF) of the monster
     * @param level the level of the monster (1-12 stars)
     * @param attribute the monster's attribute
     * @param race the monster's type/race
     * @param subType the monster's sub-type
     * @param monsterType the specific monster category
     * @param effect the Effect object containing the monster's special ability
     * @param effectType the classification of the effect (e.g., "Trigger", "Ignition")
     */
    public MonsterCard(String name, String desc, int id, Player owner,
                       int atk, int def, int level,
                       String attribute, String race,
                       String subType, String monsterType,
                       Effect effect, String effectType) {
        this(name, desc, id, owner, atk, def, level, attribute, race, subType, monsterType, "default.png");
        this.effect = effect;
        this.effectType = effectType;
    }

    // --- Getters ---
    
    /**
     * Gets the attack points (ATK) of this monster.
     *
     * @return the monster's attack value
     */
    public int getAtk() { 
        return atk; 
    }
    
    /**
     * Gets the defense points (DEF) of this monster.
     *
     * @return the monster's defense value
     */
    public int getDef() { 
        return def; 
    }
    
    /**
     * Gets the level of this monster.
     * <p>
     * Level determines how many tributes are required to summon the monster:
     * <ul>
     * <li>Levels 1-4: No tributes required</li>
     * <li>Levels 5-6: 1 tribute required</li>
     * <li>Levels 7+: 2 tributes required</li>
     * </ul>
     * </p>
     *
     * @return the monster's level (1-12)
     */
    public int getLevel() { 
        return level; 
    }
    
    /**
     * Gets the attribute of this monster.
     *
     * @return the monster's attribute (e.g., "DARK", "LIGHT", "WATER")
     */
    public String getAttribute() { 
        return attribute; 
    }
    
    /**
     * Gets the type/race of this monster.
     *
     * @return the monster's type (e.g., "Dragon", "Warrior", "Spellcaster")
     */
    public String getType() { 
        return type; 
    }
    
    /**
     * Gets the sub-type of this monster.
     *
     * @return the monster's sub-type (e.g., "Normal", "Effect", "Fusion")
     */
    public String getSubType() { 
        return subType; 
    }
    
    /**
     * Gets the specific monster type category.
     *
     * @return the monster's category (e.g., "Normal Monster", "Effect Monster")
     */
    public String getMonsterType() { 
        return monsterType; 
    }
    
    /**
     * Gets the current battle position of this monster.
     *
     * @return the current position (ATTACK or DEFENSE)
     */
    public Position getPosition() { 
        return this.position; 
    }
    
    /**
     * Gets the URL of the monster's image.
     *
     * @return the image URL as a String
     */
    public String getImageUrl() { 
        return imageUrl; 
    }

    /**
     * Gets the JavaFX Image representation of this monster card.
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

    // --- Gestion du tour ---
    
    /**
     * Attempts to change the monster's battle position.
     * <p>
     * A monster can only change its position once per turn. If the monster
     * has already changed position this turn, the request is rejected and
     * a message is printed to the console.
     * </p>
     * <p>
     * This restriction prevents position-switching exploitation and follows
     * official Yu-Gi-Oh! rules.
     * </p>
     *
     * @param pos the new position (ATTACK or DEFENSE)
     */
    public void setPosition(Position pos) {
        if (!hasChangedPositionThisTurn) {
            this.position = pos;
            System.out.println(getName() + " change sa position en " + pos);
            this.hasChangedPositionThisTurn = true;
        } else {
            System.out.println(getName() + " a déjà changé de position ce tour !");
        }
    }

    /**
     * Attempts to declare an attack with this monster.
     * <p>
     * A monster can only attack once per turn. If the monster has already
     * attacked this turn, the request is rejected and a message is printed
     * to the console.
     * </p>
     * <p>
     * This method only marks the monster as having attacked; the actual
     * battle calculation and damage are handled elsewhere in the game logic.
     * </p>
     */
    public void setAttack() {
        if (!hasAttackedThisTurn) {
            System.out.println(getName() + " attaque !");
            this.hasAttackedThisTurn = true;
        } else {
            System.out.println(getName() + " a déjà attaqué ce tour !");
        }
    }

    /**
     * Resets the turn-based action flags for this monster.
     * <p>
     * This method should be called at the start of each player's turn to
     * allow the monster to attack and change position again. Both
     * {@code hasAttackedThisTurn} and {@code hasChangedPositionThisTurn}
     * are set back to false.
     * </p>
     */
    public void resetTurnFlags() {
        hasAttackedThisTurn = false;
        hasChangedPositionThisTurn = false;
    }

    /**
     * Activates this monster's special effect.
     * <p>
     * If the monster has an effect (i.e., it's an Effect Monster), prints
     * the effect description to the console. If the monster has no effect
     * (i.e., it's a Normal Monster), prints a message indicating this.
     * </p>
     * <p>
     * Note: The actual implementation of the effect logic is handled by
     * the {@link Effect} object and should be executed through the game's
     * effect resolution system.
     * </p>
     */
    @Override
    public void activateEffect() {
        if (effect != null) {
            System.out.println("Activation de l'effet: " + getDescription());
        } else {
            System.out.println(getName() + " n'a pas d'effet.");
        }
    }

    /**
     * Returns a detailed string representation of this monster card.
     * <p>
     * The output includes all relevant monster information: name, type,
     * description, stats (ATK/DEF), level, attribute, type categories,
     * and the card's password (ID).
     * </p>
     *
     * @return a multi-line string with complete monster information
     */
    @Override
    public String toString() {
        return getName() + "\n (" + monsterType + ") \n " + getDescription() + 
               " \n ATK: " + atk + " \n DEF: " + def +
               " \n Level: " + level + " \n Attribute: " + attribute +
               " \n Type: " + type + " \n SubType: " + subType + 
               "\nPassword: " + getCardId();
    }
}