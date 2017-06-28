package towerwarspp.player;

import towerwarspp.preset.Move;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.Vector;

/**
 *
 */
class RndPlayer extends BasePlayer {
    Random rnd;

    RndPlayer() {
        // seed 42 for obvious reasons (error reproducibility)
        rnd = new Random(42);
    }

    @Override
    Move deliverMove() throws Exception {
        Vector<Move> moves = board.allPossibleMoves(color);
        Move move = moves.get(rnd.nextInt(moves.size()));
        board.update(move, color);
        return move;
    }
}
