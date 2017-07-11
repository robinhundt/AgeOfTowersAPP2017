package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;

import java.util.Random;
import java.util.Vector;

/**
 * Created by robin on 10.07.17.
 */
public class Adv1Player  extends BasePlayer{
    Random random;

    public Adv1Player() {
        random = new Random();
    }

    @Override
    Move deliverMove() {
        // get all possible moves that this player has available
        Vector<Move> moves = board.allPossibleMoves(color);
        int maxScore = Board.LOSE;
        // create new Vector of moves that will always hold the Move objects that have the highest score
        Vector<Move> maxMoves = new Vector<>();

        for(Move move : moves) {
            // iterate over all possible moves and calculate their scores
            int score = board.altScore(move, color);
//            System.out.println(score + " " + move);
//            System.out.println("score " + score + "Move " + move);
            if(score == maxScore) {
                maxMoves.add(move);
            } else if(score > maxScore) {
                /*
                * If the score of the last evaluated move is higher than the current maximum score, clear the maxMoves
                * vector containing the prior best moves. Then add the new best move to the vector and update
                * the maxScore variable to new Score
                * */
                maxMoves.clear();
                maxMoves.add(move);
                maxScore = score;
                // if the score indicates that this move is a winning move, break out of the loop and return it
                if(score == Board.WIN) {
                    break;
                }
            }

        }
        Move move = maxMoves.get(random.nextInt(maxMoves.size()));
        if(board.moveAllowed(move, color))
            return move;
        else {
            System.out.println("Passed illegal move: " + move + " " + color);
            try{
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
