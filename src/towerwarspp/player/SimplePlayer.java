package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;

import java.util.Random;
import java.util.Vector;


/**
 * Class that implements a simple AI opponent as specified in the project description. The AI chooses which move to place
 * by using the {@link Board#getBestMoves(PlayerColor)} method, that uses the following formulae to
 * calculate the score:
 * First a base distance-bonus is calculated: dBonus = d1 - d2 with d1 being the distance to the
 * enemy base before the move and d2 after the move. Then a second bonus bBonus is calculated: bBonus = 0 if no enemy
 * Entity is defeated, bBonus = 1 if an enemy stone is defeated and bBonus = 2 if an enemy tower is defeated.
 * The score is the calculated as: score = dBonus + bBonus.
 * The simple AI randomly chooses one of the moves with the highest score. A move that leads to victory is always choosen
 * before the others. Moves that lead to a position in wich the opponent could win in one move will only be executed
 * if no other moves are available.
 *
 * @author Alexander Wähling
 * @version 08-07-17
 */
public class SimplePlayer extends BasePlayer {
    /**
     * Random instance that is used to generate pseudo-rnd Integers that are used to pick a rnd move from all
     * possible moves for ths player.
     */
    private final Random rnd;

    /**
     * Construct a new Random Player. For the initialization of the {@link #rnd} object no seed is used, which causes
     * per specification the seed to be set "a value very likely to be distinct from any other invocation of this constructor."
     */
    public SimplePlayer() {
        rnd = new Random();
    }

    /**
     * Uses the {@link Board#scoreMove(Move, PlayerColor)} method to evaluate all possible moves for this players {@link #color}
     * and randomly chooses one of the highest rated moves. If the score of a Move is equal to {@link Board#WIN} (leads to
     * instant victory), that move is automatically returned.
     *
     * @return random highest scored move
     */
    @Override
    Move deliverMove() {
        Vector<Move> bestMoves = board.getBestMoves(color);
        return bestMoves.get(rnd.nextInt(bestMoves.size()));
    }
}
