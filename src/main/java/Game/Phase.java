package Game;

/**
 * Enumeration representing the different phases of a turn in Yu-Gi-Oh!
 * <p>
 * Each turn in Yu-Gi-Oh! is divided into six distinct phases that occur in a
 * specific order. Each phase has specific rules about what actions can be performed.
 * Players must complete each phase in sequence before moving to the next one.
 * </p>
 * <p>
 * The turn structure follows this order:
 * <ol>
 * <li><strong>Draw Phase (DP):</strong> The turn player draws one card from their deck
 *     (except on the first turn of the first player)</li>
 * <li><strong>Standby Phase (SP):</strong> Certain card effects activate or resolve
 *     during this phase</li>
 * <li><strong>Main Phase 1 (MP1):</strong> The turn player can Normal Summon/Set monsters,
 *     activate Spell cards, set Spell/Trap cards, and change monster positions</li>
 * <li><strong>Battle Phase (BP):</strong> The turn player can declare attacks with their
 *     monsters (if they have any in Attack Position)</li>
 * <li><strong>Main Phase 2 (MP2):</strong> Another Main Phase with the same actions as MP1,
 *     except monsters that attacked cannot change position</li>
 * <li><strong>End Phase (EP):</strong> The turn ends, certain effects activate, and the
 *     player discards down to 6 cards if they have more than that in hand</li>
 * </ol>
 * </p>
 * <p>
 * After the End Phase completes, the turn passes to the opponent and begins again
 * at the Draw Phase.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public enum Phase {
    /** Draw Phase - The turn player draws a card */
    DP("Draw Phase"),
    
    /** Standby Phase - Certain card effects trigger during this phase */
    SP("Standby Phase"),
    
    /** Main Phase 1 - First opportunity to summon monsters and play cards */
    MP1("Main Phase 1"),
    
    /** Battle Phase - The turn player can attack with their monsters */
    BP("Battle Phase"),
    
    /** Main Phase 2 - Second opportunity to play cards after battling */
    MP2("Main Phase 2"),
    
    /** End Phase - The turn concludes and effects resolve */
    EP("End Phase");

    /** The human-readable name of the phase */
    private final String label;

    /**
     * Constructs a Phase with its display label.
     *
     * @param label the human-readable name of the phase
     */
    Phase(String label) {
        this.label = label;
    }

    /**
     * Gets the human-readable label for this phase.
     * <p>
     * This is useful for displaying the current phase to players in the UI
     * or logging phase transitions to the console.
     * </p>
     *
     * @return the phase's display name (e.g., "Draw Phase", "Battle Phase")
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the next phase in the turn sequence.
     * <p>
     * This method implements the turn progression logic, automatically moving
     * from one phase to the next in the correct order. When called on the End
     * Phase, it loops back to the Draw Phase (which should trigger a turn change
     * in the game controller).
     * </p>
     * <p>
     * <strong>Phase Progression:</strong>
     * <pre>
     * DP → SP → MP1 → BP → MP2 → EP → (DP of next turn)
     * </pre>
     * </p>
     *
     * @return the Phase that follows this one in the turn sequence
     */
    public Phase next() {
        switch (this) {
            case DP: return SP;
            case SP: return MP1;
            case MP1: return BP;
            case BP: return MP2;
            case MP2: return EP;
            case EP: return DP;  // Loops back to Draw Phase (new turn)
            default: return DP;
        }
    }
}