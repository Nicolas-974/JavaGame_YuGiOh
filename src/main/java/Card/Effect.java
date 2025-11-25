package Card;
import Board.Board;

/**
 * Represents a generic effect associated with a card in Yu-Gi-Oh!
 * <p>
 * Effects are the special abilities that cards can activate during gameplay.
 * This class encapsulates the description and type of an effect, and provides
 * the mechanism to activate it with specific parameters.
 * </p>
 * <p>
 * Effects can be categorized by type (Continuous, Trigger, Quick, Ignition, etc.)
 * which determines when and how they can be activated. The actual implementation
 * of effect logic should be added to the {@link #activate(Card, Card, Board)}
 * method for specific card effects.
 * </p>
 * <p>
 * This class serves as a foundation for the effect system and can be extended
 * or modified to implement complex card interactions and game mechanics.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class Effect {

    /** The text describing what the effect does */
    private String description;

    /** 
     * The type/category of the effect.
     * <p>
     * Common effect types include:
     * <ul>
     * <li>Continuous: Always active while the card is face-up on the field</li>
     * <li>Trigger: Activates automatically when a specific condition is met</li>
     * <li>Quick: Can be activated during either player's turn</li>
     * <li>Ignition: Can only be activated during the controller's Main Phase</li>
     * </ul>
     * </p>
     */
    private String effectType;

    /**
     * Creates a new Effect with specified description and type.
     * <p>
     * The description should clearly explain what the effect does, while
     * the effectType categorizes when and how the effect can be activated.
     * </p>
     *
     * @param description text describing what the effect does
     * @param effectType the category of effect (e.g., "Continuous", "Trigger", "Quick", "Ignition")
     */
    public Effect(String description, String effectType) {
        this.description = description;
        this.effectType = effectType;
    }

    /**
     * Gets the description of this effect.
     * <p>
     * The description provides the full text explaining what happens when
     * the effect is activated, including targeting requirements, costs,
     * and results.
     * </p>
     *
     * @return the effect's description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the type of this effect.
     * <p>
     * The effect type determines the timing and activation conditions for
     * this effect within the game's chain and priority system.
     * </p>
     *
     * @return the effect type (e.g., "Continuous", "Trigger", "Quick")
     */
    public String getEffectType() {
        return effectType;
    }

    /**
     * Activates this effect with the specified parameters.
     * <p>
     * This method is called when the effect is triggered or manually activated.
     * It receives the source card that generated the effect, an optional target
     * card, and the game board context.
     * </p>
     * <p>
     * Currently, this method prints activation information to the console.
     * The actual game logic for each specific effect should be implemented
     * here or in subclasses, based on the effectType and the card's requirements.
     * </p>
     * <p>
     * <strong>TODO:</strong> Implement specific logic based on effect type:
     * <ul>
     * <li>Validate activation conditions</li>
     * <li>Apply costs if any</li>
     * <li>Modify card stats or game state</li>
     * <li>Handle chaining and priority</li>
     * <li>Resolve effect and update board accordingly</li>
     * </ul>
     * </p>
     *
     * @param source the card that is activating this effect
     * @param target the card being targeted by this effect, or null if no specific target
     * @param board the game board where the effect takes place
     */
    public void activate(Card source, Card target, Board board) {
        System.out.println("Activation de l'effet: " + description +
                           " [Type: " + effectType + "]");
        // TODO: implémenter la logique spécifique selon le type d'effet
    }
}