package towerwarspp.player.mcts;

/**
 * Simple Data container class that contains pairs of Nodes and their score. Implements {@link Comparable<ScoredNode>}
 * to impose a total ordering. Scored Nodes are order by the natural ordering of their scores.
 */
class ScoredNode implements Comparable<ScoredNode>{
    /**
     * Contained Node in pair.
     */
    final Node node;
    /**
     * Contained UCB1 score in Pair.
     */
    final double ucbScore;

    /**
     * Construct a new pair that holds a reference to the passed Node and it's  score.
     * @param node
     * @param ucbScore
     */
    ScoredNode(Node node, double ucbScore) {
        this.node = node;
        this.ucbScore = ucbScore;
    }

    /**
     * Imposes a total ordering on scored Nodes equivalent to the ordering of their double scores.
     * @param scoredNode
     * @return
     */
    @Override
    public int compareTo(ScoredNode scoredNode) {
        if(ucbScore < scoredNode.ucbScore)
            return -1;
        else if(ucbScore > scoredNode.ucbScore)
            return 1;
        else
            return 0;
    }
}
