package towerwarspp.player;

import towerwarspp.board.MoveList;
import towerwarspp.preset.Move;

import java.rmi.RemoteException;
import java.util.Iterator;
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
        Vector<MoveList> moveLists = board.getAllPossibleMoves(color);
        MoveList moveList = moveLists.get(rnd.nextInt(moveLists.size()));
        Move move = rndMove(moveList);
        while (move == null)
            move = rndMove(moveList);
        board.update(move, color);
        return move;
    }

    private Move rndMove(MoveList moveList) {
        Move move = null;
        Iterator<Move> it = moveList.iterator();

        for(int i=0; i<moveList.size(); i++)
            move = it.next();

        return move;
    }
}
