package towerwarspp.player;

import towerwarspp.main.Debug;
import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.debug.DebugSource;
import towerwarspp.player.mcts.Mcts;
import towerwarspp.player.mcts.TreeSelectionStrategy;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

/**
 * Created by robin on 23.06.17.
 */
public class Adv2Player extends BasePlayer {
    public static final int DEF_PARALLELIZATION = 8;
    public static final long DEF_TIME_PER_MOVE = 2000;
    private Debug debug;
    private Mcts mcts;
    private Thread ai;



    public Adv2Player(long timePerMove, int parallelizationFactor, TreeSelectionStrategy selectionStrategy,
                      PlayStrategy playStrategy, boolean fairPlay, double bias) {
        debug = Debug.getInstance();
        mcts = new Mcts(timePerMove, parallelizationFactor, playStrategy, selectionStrategy, fairPlay, bias);
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
        if(!ai.isAlive()){
            ai.start();
            debug.send(DebugLevel.LEVEL_2, DebugSource.PLAYER, "Started Thread " + ai.toString());
        }
    }

}
