package towerwarspp.player.mcts;

/**
 * Enum class whose fields represent states the MCTS  algorithm can be in.
 * @author Robin Hundt
 */
public enum Task {
    /**
     * A new game has been started and the {@link Mcts} class has been notified to constrct a new tree.
     */
    INIT,
    /**
     * Represents the state that a move has been played and the algorithm should from now on only considered
     */
    MOVE_RECEIVED,
    /**
     * Represents the state that a move has been requested.
     */
    MOVE_REQUESTED
}
