package towerwarspp.player.mcts;

/**
 * Created by robin on 14.07.17.
 */
class ScoredNode implements Comparable<ScoredNode>{
    final Node node;
    final double ucbScore;

    ScoredNode(Node node, double ucbScore) {
        this.node = node;
        this.ucbScore = ucbScore;
    }

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
