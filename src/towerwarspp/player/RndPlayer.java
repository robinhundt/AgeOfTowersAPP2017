package towerwarspp.player;

import towerwarspp.io.View;
import towerwarspp.preset.Move;
import towerwarspp.preset.Status;

import java.util.Random;
import java.util.Vector;

/**
 * Class that implements a Random Player. Is a subclass of {@link BasePlayer}. The only difference is the implementation of
 * deliverMove(), which picks a random move from all possible moves that are available for the players {@link #color}.
 * @author Robin Hundt
 * @version 08-07-17
 */
public class RndPlayer extends BasePlayer {
    /**
     * Random instance that is used to generate pseudo-random Integers that are used to pick a random move from all
     * possible moves for ths player.
     */
    private Random rnd;

    /**
     * Construct a new Random Player. For the initialization of the {@link #rnd} object no seed is used, which causes
     * per specification the seed to be set "a value very likely to be distinct from any other invocation of this constructor."
     */
    public RndPlayer() {
        rnd = new Random();
    }

    /**
     * Construct a new Random Player. For the initialization of the {@link #rnd} object no seed is used, which causes
     * per specification the seed to be set "a value very likely to be distinct from any other invocation of this constructor."
     * @param view optional {@link View} instance to update state of the game after each {@link #request()} and {@link #update(Move, Status)}
     */
    public RndPlayer(View view) {
        this();
        setView(view);
    }

    /**
     * Implementation of the {@link BasePlayer#deliverMove()} method. Randomly selects one move from all the possible
     * moves that are available for this player {@link #color} and returns it.
     * @return randomly selected move out of all possible moves.
     */
    @Override
    Move deliverMove() {
        Vector<Move> moves = board.allPossibleMoves(color);
        return moves.get(rnd.nextInt(moves.size()));
    }
}
