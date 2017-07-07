package towerwarspp.player;

import towerwarspp.preset.Move;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.Vector;

/**
 * Created by robin on 23.06.17.
 */
public class SimplePlayer extends BasePlayer {
    Random random;

    public SimplePlayer() {
        random = new Random(42);
    }

    @Override
    Move deliverMove() throws Exception {
        Vector<Move> moves = board.allPossibleMoves(color);
        int maxScore = 0;
        Vector<Move> maxMoves = new Vector<>();

        for(Move move : moves) {
            int score = board.scoreMove(move, color);
            if(score == maxScore) {
                maxMoves.add(move);
            } else if(score > maxScore) {
                maxMoves.clear();
                maxMoves.add(move);
                maxScore = score;
                if(score == Integer.MAX_VALUE) {
                    break;
                }
            }

        }

        return maxMoves.get(random.nextInt(maxMoves.size()));
    }
}
