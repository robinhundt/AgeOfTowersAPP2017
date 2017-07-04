package towerwarspp.player;

import towerwarspp.board.MoveList;
import towerwarspp.preset.Move;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 *
 */
public class RndPlayer extends BasePlayer {
    private Random rnd;

    public RndPlayer() {
        // seed 42 for obvious reasons (error reproducibility)
        //TODO change seed to current system time
        rnd = new Random(42);
    }

    @Override
    Move deliverMove() throws Exception {
        Vector<MoveList> moveLists = board.getAllPossibleMoves(color);
        MoveList moveList = moveLists.get(rnd.nextInt(moveLists.size()));
        Move move = rndMove(moveList);
        while (move == null)
            move = rndMove(moveList);
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
