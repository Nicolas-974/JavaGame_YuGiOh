package Game;

import Deck.Deck;
import Player.Player;

/**
 * Represents a Yu-Gi-Oh! duel between two players.
 * <p>
 * This class orchestrates the entire game flow, managing players, turn phases,
 * turn progression, and win conditions. It serves as the main game controller
 * that coordinates all gameplay elements and ensures rules are followed.
 * </p>
 * <p>
 * A typical game flow consists of:
 * <ol>
 * <li>Game initialization with two players</li>
 * <li>Starting the game and drawing initial hands</li>
 * <li>Progressing through turn phases (Draw, Standby, Main, Battle, End)</li>
 * <li>Switching between players after each turn</li>
 * <li>Checking win conditions after each action</li>
 * <li>Ending the game when a player wins</li>
 * </ol>
 * </p>
 * <p>
 * The game follows official Yu-Gi-Oh! turn structure and phase progression,
 * with each player alternating turns until one achieves a victory condition.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class Game {

    // --- Attributs ---
    
    /** The first player in the duel */
    private Player player1;
    
    /** The second player in the duel */
    private Player player2;
    
    /** The player whose turn is currently active */
    private Player currentPlayer;
    
    /** The current phase of the turn (Draw, Standby, Main, Battle, End) */
    private Phase currentPhase;
    
    /** The total number of turns that have passed since the game started */
    private int turnCount;
    
    /** Flag indicating whether the game has ended */
    private boolean isGameOver;

    /**
     * Creates a new Yu-Gi-Oh! game between two players.
     * <p>
     * The game is initialized with player1 as the starting player and
     * the Draw Phase as the initial phase. The game is not started until
     * {@link #startGame()} is called.
     * </p>
     *
     * @param player1 the first player who will take the first turn
     * @param player2 the second player who will take the second turn
     */
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1; // par défaut, player1 commence
        this.currentPhase = Phase.DP; // première phase du tour
        this.turnCount = 1;
        this.isGameOver = false;
    }

    // --- Méthodes principales ---
    
    /**
     * Starts the game and initializes the duel.
     * <p>
     * This method performs the following initialization steps:
     * <ul>
     * <li>Announces the start of the duel</li>
     * <li>Assigns decks to both players</li>
     * <li>Sets player1 as the current player</li>
     * <li>Each player draws their initial card</li>
     * <li>Resets turn count to 1</li>
     * <li>Sets game over flag to false</li>
     * </ul>
     * </p>
     * <p>
     * Note: In official Yu-Gi-Oh! rules, players draw 5 cards at the start,
     * but the first turn player cannot draw during their first Draw Phase.
     * This implementation may need adjustment to match official rules.
     * </p>
     */
    public void startGame() {
        System.out.println("La partie commence entre " + player1.getName() + " et " + player2.getName());
        player1.setDeck(new Deck(/* paramètres */));
        player2.setDeck(new Deck(/* paramètres */));
        currentPlayer = player1;
        player1.drawCard();
        player2.drawCard();
        turnCount = 1;
        isGameOver = false;
    }

    /**
     * Gets the current phase of the turn.
     *
     * @return the current Phase (DP, SP, M1, BP, M2, or EP)
     */
    public Phase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Advances to the next phase in the turn sequence.
     * <p>
     * The phase progression follows the official Yu-Gi-Oh! turn structure:
     * Draw Phase → Standby Phase → Main Phase 1 → Battle Phase → Main Phase 2 → End Phase
     * </p>
     * <p>
     * This method requires the {@link Phase} enum to implement a {@code next()}
     * method that returns the subsequent phase in the sequence.
     * </p>
     */
    public void nextPhase() {
        currentPhase = currentPhase.next(); // nécessite enum Phase avec méthode next()
        System.out.println("Phase suivante: " + currentPhase);
    }

    /**
     * Ends the current player's turn and starts the opponent's turn.
     * <p>
     * This method performs the following actions:
     * <ul>
     * <li>Calls the current player's end turn routine</li>
     * <li>Resets to the Draw Phase</li>
     * <li>Switches to the other player</li>
     * <li>Increments the turn counter</li>
     * <li>Announces the turn change</li>
     * </ul>
     * </p>
     */
    public void endTurn() {
        currentPlayer.endTurn();
        resetPhase();
        switchPlayer();
        incrementTurn();
        System.out.println("Fin du tour. C'est maintenant au tour de " + currentPlayer.getName());
    }

    /**
     * Gets the opponent of the current player.
     * <p>
     * This is a utility method to quickly access the non-active player,
     * which is useful for implementing card effects that target the opponent
     * or checking opponent's game state.
     * </p>
     *
     * @return the Player who is not currently taking their turn
     */
    public Player getOpponent() {
        return (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Gets the player whose turn is currently active.
     *
     * @return the current active Player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Checks if any player has met a win condition.
     * <p>
     * This method evaluates the following standard Yu-Gi-Oh! win conditions:
     * <ul>
     * <li>A player's Life Points reach 0 or below</li>
     * <li>A player cannot draw a card when required (deck out)</li>
     * <li>Special win conditions (handled by {@link Player#isAlive()})</li>
     * </ul>
     * </p>
     * <p>
     * If a win condition is met, {@link #endGame(Player)} is called with
     * the winning player. This method should be called after any action
     * that could result in a win.
     * </p>
     */
    public void checkWinCondition() {
        if (!player1.isAlive()) {
            endGame(player2);
        } else if (!player2.isAlive()) {
            endGame(player1);
        }
    }

    /**
     * Switches the active player between player1 and player2.
     * <p>
     * This method is called at the end of each turn to alternate control
     * between the two players. It does not trigger any phase changes or
     * turn-based effects; those are handled by {@link #endTurn()}.
     * </p>
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Gets the total number of turns that have elapsed.
     * <p>
     * Turn count starts at 1 when the game begins and increments after
     * each player completes their turn. This is useful for tracking
     * game duration and implementing turn-based card effects.
     * </p>
     *
     * @return the current turn number
     */
    public int getTurnCount() {
        return turnCount;
    }

    /**
     * Increments the turn counter by one.
     * <p>
     * This method is called automatically by {@link #endTurn()} and
     * should not typically be called manually. Each complete round
     * (both players taking a turn) increases the count by 2.
     * </p>
     */
    public void incrementTurn() {
        turnCount++;
    }

    /**
     * Manually sets the current phase to a specific phase.
     * <p>
     * This method allows forced phase changes, which may be needed for
     * special card effects or game mechanics that skip phases or jump
     * to specific phases out of sequence.
     * </p>
     *
     * @param phase the Phase to switch to
     */
    public void setPhase(Phase phase) {
        this.currentPhase = phase;
        System.out.println("Phase définie : " + currentPhase.getLabel());
    }

    /**
     * Resets the current phase to the Draw Phase.
     * <p>
     * This method is called at the start of each player's turn to ensure
     * the turn begins with the correct phase. It's automatically invoked
     * by {@link #endTurn()}.
     * </p>
     */
    public void resetPhase() {
        currentPhase = Phase.DP;
    }

    /**
     * Ends the game and declares a winner.
     * <p>
     * This method marks the game as over, preventing further actions,
     * and announces the winner to all players. Once called, {@link #isOver()}
     * will return true, and the game controller should stop accepting
     * player inputs.
     * </p>
     * <p>
     * This is typically called by {@link #checkWinCondition()} when a
     * player satisfies a win condition.
     * </p>
     *
     * @param winner the Player who has won the duel
     */
    public void endGame(Player winner) {
        isGameOver = true;
        System.out.println("La partie est terminée ! Le vainqueur est " + winner.getName());
    }

    /**
     * Checks if the game has ended.
     * <p>
     * This method should be checked before processing any game actions
     * to ensure no further moves are made after a winner is declared.
     * </p>
     *
     * @return true if the game is over, false if it's still ongoing
     */
    public boolean isOver() {
        return isGameOver;
    }
}