package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.io.View;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import java.util.Random;
import java.util.Vector;

/**
 * Class that implements a simple AI opponent as specified in the project description. The AI chooses which move to place
 * by using the {@link towerwarspp.board.Board#scoreMove(Move, PlayerColor)} method, that uses the following formulae to
 * calculate the score:
 * First a base distance-bonus is calculated: dBonus = d1 - d2 with d1 being the distance to the
 * enemy base before the move and d2 after the move. Then a second bonus bBonus is calculated: bBonus = 0 if no enemy
 * Entity is defeated, bBonus = 1 if an enemy stone is defeated and bBonus = 2 if an enemy tower is defeated.
 * The score is the calculated as: score = dBonus + bBonus.
 * The simple AI randomly chooses one of the moves with the highest score. A move that leads to victory is always choosen
 * before the others. Moves that lead to a position in wich the opponent could win in one move will only be executed
 * if no other moves are available.
 * @author Robin Hundt
 * @version 08-07-17
 */
public class SimplePlayer extends BasePlayer {
    /**
     * Random instance that is used to generate pseudo-rnd Integers that are used to pick a rnd move from all
     * possible moves for ths player.
     */
    private Random rnd;

    /**
     * Construct a new Random Player. For the initialization of the {@link #rnd} object no seed is used, which causes
     * per specification the seed to be set "a value very likely to be distinct from any other invocation of this constructor."
     */
    public SimplePlayer() {
        rnd = new Random();
    }

    /**
     * Construct a new Random Player. For the initialization of the {@link #rnd} object no seed is used, which causes
     * per specification the seed to be set "a value very likely to be distinct from any other invocation of this constructor."
     * @param view optional {@link View} instance that can be used to update the visual state of the game after each
     *             {@link #request()} and {@link #update(Move, Status)}
     */
    public SimplePlayer(View view) {
        this();
        setView(view);
    }

    /**
     * Uses the {@link Board#scoreMove(Move, PlayerColor)} method to evaluate all possible moves for this players {@link #color}
     * and randomly chooses one of the highest rated moves. If the score of a Move is equal to {@link Board#WIN} (leads to
     * instant victory), that move is automatically returned.
     * @return random highest scored move
     */
    @Override
    Move deliverMove() {
        // get all possible moves that this player has available
        Vector<Move> moves = board.allPossibleMoves(color);
        int maxScore = Board.LOSE;
        // create new Vector of moves that will always hold the Move objects that have the highest score
        Vector<Move> maxMoves = new Vector<>();

        for(Move move : moves) {
            // iterate over all possible moves and calculate their scores
            int score = board.simpleScoreMove(move, color);
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
        Move move = maxMoves.get(rnd.nextInt(maxMoves.size()));
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
