package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;

import java.util.Random;
import java.util.Vector;

/**
 * Class offering enums representing different play strategies and methods that implement those strategies.
 * @author Alexander Wähling, Robin Hundt
 *
 */
public enum PlayStrategy {
    /**
     * Light PlayStrategy is equivalent to making moves at random. If an AI Player is employing a LIGHT strategy
     * he should use the offered {@link #lightPlay(Board)} method.
     */
    LIGHT,
    /**
     * Heavy PlayStrategy uses an evaluation function to score moves and randomly chose one of the highest scored ones.
     */
    HEAVY,
    /**
     * Dynamically change between {@link #LIGHT} and {@link #HEAVY}.
     */
    DYNAMIC;
    /**
     * Random instance to generate pseudo random numbers.
     */
    private static final Random random = new Random();

    /**
     * Uses the passed {@link Board} object to get all moves available to the Player returned by {@link Board#getTurn()},
     * then randomly selects one of them and returns it.
     * The Board instance is not changed in any way.
     * @param board board to get available moves from
     * @return randomly selected move
     */
    public static Move lightPlay(Board board) {
        Vector<Move> moves = board.allPossibleMoves(board.getTurn());
        return moves.get(random.nextInt(moves.size()));
    }

    /**
     * Uses the passed {@link Board} object to get all moves available to the Player returned by {@link Board#getTurn()},
     * then assigns a score to each of them by using the {@link Board#altScore(Move, PlayerColor)} evaluation function
     * and returns a random move out of the highest scored moves.
     * The {@link Board} instance is not changed in any way
     * @param board board to get moves from
     * @return randomly selected move out of the highest scored moves
     */
    public static Move heavyPlay(Board board) {
        // get all possible moves that this player has available
        Vector<Move> moves = board.allPossibleMoves(board.getTurn());
        int maxScore = Board.LOSE;
        // create new Vector of moves that will always hold the Move objects that have the highest score
        Vector<Move> maxMoves = new Vector<>();

        for(Move move : moves) {
            // iterate over all possible moves and calculate their scores
            int score = board.altScore(move, board.getTurn());
            if(score == maxScore) {
                maxMoves.add(move);
            } else if(score > maxScore) {
                /*
                * If the score of the last evaluated move is higher than the current maximum score, clear the maxMoves
                * vector containing the prior best moves. Then add the new best move to the vector and makeMove
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
