package Card;

/**
 * Enumeration representing the possible battle positions for monster cards in Yu-Gi-Oh!
 * <p>
 * Monster cards can be placed on the field in different positions, which affects
 * how they interact in battle and which of their stats (ATK or DEF) are used.
 * </p>
 * <p>
 * <strong>Position Mechanics:</strong>
 * <ul>
 * <li><strong>Attack Position:</strong> The monster is vertical on the field. Uses ATK
 *     for battle calculations. Can attack opponent's monsters or directly attack the
 *     opponent if their field is empty. Takes battle damage if destroyed in battle.</li>
 * <li><strong>Defense Position:</strong> The monster is horizontal on the field. Uses DEF
 *     for battle calculations. Cannot attack but can protect the player from direct
 *     attacks. If destroyed in battle while face-up, no battle damage is inflicted
 *     to the controller.</li>
 * <li><strong>Face-Down:</strong> The monster is set face-down in Defense Position.
 *     Its identity and stats are hidden from the opponent. When attacked or manually
 *     flipped, it becomes face-up. Some monsters have Flip Effects that activate when
 *     flipped face-up.</li>
 * </ul>
 * </p>
 * <p>
 * In official Yu-Gi-Oh! rules, monsters can change their battle position once per turn
 * (except on the turn they were summoned, and except for monsters that attacked that turn).
 * Changing from Attack to Defense or vice versa is called "changing battle position."
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public enum Position {
    /**
     * Attack Position - Monster is vertical and can attack.
     * <p>
     * Uses ATK stat for battle. Can declare attacks on opponent's monsters
     * or directly on the opponent. Takes battle damage when losing a battle.
     * </p>
     */
    ATTACK,
    
    /**
     * Defense Position - Monster is horizontal and cannot attack.
     * <p>
     * Uses DEF stat for battle. Protects the player from taking battle damage
     * when destroyed in battle (while face-up). Cannot declare attacks.
     * </p>
     */
    DEFENSE,
    
    /**
     * Face-Down Position - Monster is set face-down in Defense Position.
     * <p>
     * The card's identity and stats are hidden from the opponent. Uses DEF stat
     * when attacked. Flips face-up when attacked or manually flipped. May have
     * Flip Effects that activate when flipped face-up. Face-down monsters in
     * Defense Position do not inflict battle damage to either player when destroyed.
     * </p>
     */
    FACE_DOWN
}