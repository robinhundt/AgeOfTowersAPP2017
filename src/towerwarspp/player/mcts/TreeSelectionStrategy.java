package towerwarspp.player.mcts;

/**
 * Enum class to differentiate between different TreeSelectionStrategies.
 * @author Robin Hundt
 */
public enum TreeSelectionStrategy {
    /**
     * Robust strategy means, that when deciding on a move to return, the MCTS algorithm will return the move of the child
     * that has the highest {@link Node#getGames()} value.
     */
    ROBUST,
    /**
     * Max strategy means, that when deciding on a move to return, the MCTS algorithm will return the move of the child
     * that has the highest {@link Node#getWeight()} value (wins / games ratio)
     */
    MAX
}
