package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.player.mcts.Mcts;
import towerwarspp.player.mcts.TreeSelectionStrategy;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;
import towerwarspp.util.debug.Debug;
import towerwarspp.util.debug.DebugLevel;
import towerwarspp.util.debug.DebugSource;

/**
 * Advanced AI enemy that uses a threading enabled implementation of the Monte Carlo tree search algorithm to decide
 * on his move after the specified amount of time. See {@link Mcts} for an explanation of the algorithm employed.
 */
public class Adv2Player extends BasePlayer {
    /**
     * Default parallelization value. The number of Threads the ai will use at most.
     */
    public static final int DEF_PARALLELIZATION = 8;
    /**
     * Default time in milliseconds the AI Player will decide on his move.
     */
    public static final long DEF_TIME_PER_MOVE = 2000;
    /**
     * Debug object to send debug messages.
     */
    private Debug debug;
    /**
     * Core piece of this player. This reference to the implementation of the Monte
     */
    private Mcts mcts;
    /**
     * Thread that the {@link #mcts} Runnable is executed in.
     */
    private Thread ai;


    /**
     * Creates a new Adv2Player object with the specified parameters.
     * @param timePerMove time the AI will spend deciding on a move
     * @param parallelizationFactor maximum number of Threads used by the AI concurrently
     * @param selectionStrategy employed {@link TreeSelectionStrategy}
     * @param playStrategy employed {@link PlayStrategy}
     * @param fairPlay if set to true, the AI will spend as much time deciding on it's move as the opponent took before it
     * @param bias bias used used for the UCB1 formulae in the {@link Mcts} implementation
     */
    public Adv2Player(long timePerMove, int parallelizationFactor, TreeSelectionStrategy selectionStrategy,
                      PlayStrategy playStrategy, boolean fairPlay, double bias) {
        debug = Debug.getInstance();
        /* Subtract one from the parallelizationFactor because the administration of the algorithm is running in it's
          own Thread at all times*/
        mcts = new Mcts(timePerMove, parallelizationFactor-1, playStrategy, selectionStrategy , fairPlay, bias);
        ai = new Thread(mcts);
        ai.setDaemon(true);
    }

    /**
     * Returns after the at Player construction specified time per move the best move found so far depending on the set
     * {@link TreeSelectionStrategy}.
     * @return best move so far
     */
    @Override
    Move deliverMove() {
        Move move = mcts.getMove();
        debug.send(DebugLevel.LEVEL_1, DebugSource.PLAYER, "Adv2Player " + color + " moving " + move);
        return move;
    }

    /**
     * Calls {@link BasePlayer#update(Move, Status)} and then {@link Mcts#feedEnemyMove(Move)} to update the state of the search
     * tree.
     * @param opponentMove opponent move to place on own board
     * @param boardStatus opponent board status after move
     * @throws Exception if Status is {@link Status#ILLEGAL} or board status are not matched
     */
    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception{
        super.update(opponentMove, boardStatus);
        debug.send(DebugLevel.LEVEL_2, DebugSource.PLAYER, "Adv2Player " + color + " received " + opponentMove);
        mcts.feedEnemyMove(opponentMove);
    }

    /**
     * Initializes this Player by first calling {@link BasePlayer#init(int, PlayerColor)} then {@link Mcts#setInit(Board)} to
     * notify Mcts algorithm about new game state.
     * If the Thread {@link #ai} is not alive, it's started.
     * @param boardSize size to initialize {@link BasePlayer}'s board with.
     * @param playerColor color of this Player
     * @throws Exception if boardSize is less than 4 or greater than 26
     */
    @Override
    public void init(int boardSize, PlayerColor playerColor) throws Exception {
        super.init(boardSize, playerColor);
        mcts.setInit(board.clone());
        if(!ai.isAlive()){
            ai.start();
            debug.send(DebugLevel.LEVEL_2, DebugSource.PLAYER, "Started Thread " + ai.toString());
        }
    }

}
