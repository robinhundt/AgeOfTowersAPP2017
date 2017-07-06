package towerwarspp.player;

import towerwarspp.board.MoveList;
import towerwarspp.io.View;
import towerwarspp.preset.Move;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 *
 */
public class RndPlayer extends BasePlayer {
    private Random rnd;

    public RndPlayer(View view) {
        // seed 42 for obvious reasons (error reproducibility)
        //TODO change seed to current system time

        this.view = view;
        rnd = new Random();
    }

    public RndPlayer() {
        this(null);
    }

    @Override
    Move deliverMove() throws Exception {
        Vector<Move> moves = board.allPossibleMoves(color);
        return moves.get(rnd.nextInt(moves.size()));
    }

//    private Move rndMove(MoveList moveList) {
//        Move move = null;
//        Iterator<Move> it = moveList.iterator();
//
//        for(int i=0; i<moveList.size(); i++)
//            move = it.next();
//
//        return move;
//    }
}
