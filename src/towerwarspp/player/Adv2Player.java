package towerwarspp.player;

import towerwarspp.main.Debug;
import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.debug.DebugSource;
import towerwarspp.player.mcts.Mcts;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

/**
 * Created by robin on 23.06.17.
 */
public class Adv2Player extends BasePlayer {
    private Debug debug;
    private Mcts mcts;
    private Thread ai;



    public Adv2Player(long timePerMove, int parallelizationFactor) {
        debug = Debug.getInstance();
        mcts = new Mcts(timePerMove, parallelizationFactor);
        ai = new Thread(mcts);
        ai.setDaemon(true);
    }

    @Override
    Move deliverMove() throws Exception {
        Move move = mcts.getMove();
        debug.send(DebugLevel.LEVEL_1, DebugSource.PLAYER, "Adv2Player " + color + " moving " + move);
        return move;
    }

    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception{
        super.update(opponentMove, boardStatus);
        debug.send(DebugLevel.LEVEL_1, DebugSource.PLAYER, "Adv2Player " + color + " received " + opponentMove);
        mcts.feedEnemyMove(opponentMove);
    }

    @Override
    public void init(int boardSize, PlayerColor playerColor) {
        super.init(boardSize, playerColor);
        mcts.setInit(board.clone());
        if(!ai.isAlive())
            ai.start();
    }

}
