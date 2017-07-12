package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;

import java.util.Random;
import java.util.Vector;

/**
 * Created by robin on 12.07.17.
 */
public enum PlayStrategy {
    LIGHT,
    HEAVY;

    private static Random random = new Random();

    public static Move rndPlay(Board board) {
        Vector<Move> moves = board.allPossibleMoves(board.getTurn());
        return moves.get(random.nextInt(moves.size()));
    }

    public static Move heavyPlay(Board board) {
        // get all possible moves that this player has available
        Vector<Move> moves = board.allPossibleMoves(board.getTurn());
        int maxScore = Board.LOSE;
        // create new Vector of moves that will always hold the Move objects that have the highest score
        Vector<Move> maxMoves = new Vector<>();

        for(Move move : moves) {
            // iterate over all possible moves and calculate their scores
            int score = board.altScore(move, board.getTurn());
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
        return maxMoves.get(random.nextInt(maxMoves.size()));
    }
}
