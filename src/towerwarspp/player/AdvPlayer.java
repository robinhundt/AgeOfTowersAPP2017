package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.io.View;
import towerwarspp.player.ai.SimRndGame;
import towerwarspp.preset.Move;

/**
 * Created by robin on 23.06.17.
 */
public class AdvPlayer extends BasePlayer {
    private final int simCount;

    public AdvPlayer(int simCount) {
        this.simCount = simCount;
    }

    public AdvPlayer(View view, int simCount) {
        this(simCount);
        setView(view);
    }

    @Override
    Move deliverMove() throws Exception {
        Move bestMove = null;
        int bestWinCounter = -1;

        for(Move move : board.allPossibleMoves(color)) {
            int winCount = 0;
            for(int i=0; i < simCount; i++) {
                SimRndGame sim = new SimRndGame(board.clone(), move, color);
                if(sim.simulate())
                    winCount++;
            }
            if(winCount > bestWinCounter)
                bestMove = move;

        }
        return bestMove;
    }

//        System.out.println("Before clone");
//        for (Move move : board.allPossibleMoves(color))
//            System.out.println(move);
//
//        System.out.println("After clone");
//        Board copy = board.clone();
//        for (Move move : copy.allPossibleMoves(color))
//            System.out.println(move);
//        return null;
}
