package towerwarspp.player.mcts;


import static towerwarspp.util.debug.DebugLevel.LEVEL_3;
import static towerwarspp.util.debug.DebugLevel.LEVEL_4;
import static towerwarspp.util.debug.DebugLevel.LEVEL_5;
import static towerwarspp.util.debug.DebugSource.PLAYER;
import static towerwarspp.player.mcts.Mcts.DEF_SCORE;
import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;
import static towerwarspp.preset.Status.BLUE_WIN;
import static towerwarspp.preset.Status.OK;

import towerwarspp.board.Board;
import towerwarspp.util.debug.Debug;
import towerwarspp.player.PlayStrategy;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;

/**
 * Runnable class that provides the option of parallelization of the Monte Carlo tree search algorithm.
 */
class UpdateTree implements Runnable {
    /**
     * {@link Debug} object that is used to send relevant debug messages.
     */
    private Debug debug;
    /**
     * Board that the algorithm is executed on.
     */
    private Board board;
    /**
     * Root of the search tree.
     */
    private Node root;
    /**
     * The play strategy employed in the simulation phase of the algorithm
     */
    private PlayStrategy playStrategy;
    /**
     * Variable specifying which subtree of the current root to explore. For example if set to 3, in the first selection
     * step the Node with the third highest UCB1 score is selected. From there on only Nodes that have a maximal
     * UCB1 score are selected.
     */
    private int nthSubtree;

    /**
     * Constructor to construct a new UpdateTree object. As {@link Board} only copies of the actual board the game is
     * played on should be passed to this object.
     * @param board the {@link Board} object used to to run simulation of the game on. Only copies of Boards should be passsed
     *              since they'll be changed by the {@link #playStrategy} and {@link #run()} methods.
     * @param root  Node representing the game state from which the algorithm should be executed.
     * @param playStrategy {@link PlayStrategy} to employ during the simulation phase
     * @param nthSubtree which subtree from the root to continue the selection phase on
     */
    UpdateTree(Board board, Node root, PlayStrategy playStrategy, int nthSubtree) {
        this.debug = Debug.getInstance();
        this.board = board;
        this.root = root;
        this.playStrategy = playStrategy;
        this.nthSubtree = nthSubtree;
    }

    static PlayerColor playout(Board board, PlayStrategy playStrategy) {
        while (board.getStatus() == OK) {
            Move move;
            if(playStrategy == PlayStrategy.LIGHT)
                move = PlayStrategy.lightPlay(board);
            else
                move = PlayStrategy.heavyPlay(board);
            board.makeMove(move);
        }

        switch (board.getStatus()) {
            case BLUE_WIN: return BLUE;
            case RED_WIN: return RED;
            default: throw new IllegalStateException("Mcts.playout. Board status can not be illegal.");
        }
    }

    /**
     *  Running this method in a parallel Thread by passing on instance of this class to a Thread and executing it will
     *  will enable the {@link Mcts} algorithm to do multiple iterations of the select - expand - simulate - backpropagate
     *  loop concurrently.
     *
     *  In this method first from the root the Child that has the {@link #nthSubtree} highest USB1 score is selected.
     *  From there on at each depth the child with the highest UCB score is selected
     *  until a child is reached whose {@link Node#isExpanded()} returns false. Then this Child is expanded and
     *  a simulation according to the specified {@link #playStrategy} is executed. The result of that simulation is
     *  backpropagated up the tree.
     */
    @Override
    public void run() {
        Node selectedChild = root;
        debug.send(LEVEL_3, PLAYER, "Mcts: Select on " + root);
        debug.send(LEVEL_4, PLAYER, "Roots children: " + root.getChildren().toString());

        /* Selection phase of the algorithm:
        * From the root first select the Child that has the nth highest UCB1 score specified during object construction.
        */
        if(selectedChild.isExpanded() && !selectedChild.isTerminal()) {
           selectedChild = selectedChild.nthBestUCBChild(nthSubtree % selectedChild.childCount());
           board.makeMove(selectedChild.getMove());
            if(board.getStatus() != OK) {
                selectedChild.setTerminalTrue();
                debug.send(LEVEL_5, PLAYER, "Mcts: Found terminal Node " + selectedChild);
            }
        }
        /* From there on at each depth select the child with the highest UCB1 score as long as the selected Child
        * is expanded and not terminal*/
        Node expNode;
            while (selectedChild.isExpanded() && !selectedChild.isTerminal()) {
                selectedChild = selectedChild.bestUCBChild();
                debug.send(LEVEL_5, PLAYER, "Mcts: selected child " + selectedChild);

                board.makeMove(selectedChild.getMove());
                if(board.getStatus() != OK) {
                    selectedChild.setTerminalTrue();
                    debug.send(LEVEL_5, PLAYER, "Mcts: Found terminal Node " + selectedChild);
                }
            }

            debug.send(LEVEL_3, PLAYER, "Mcts: Found best child " + selectedChild);

            if(selectedChild.isTerminal()) {
                selectedChild.backPropagateScore(DEF_SCORE, board.getStatus() == BLUE_WIN ? BLUE : RED);
            } else if(!selectedChild.isExpanded()) {
                /* if the selected Child is neither terminal nor expanded expand the Node and do a playout, then backpropagate
                * the result */
                expNode = selectedChild.expand(board);
                if(expNode != null) {

                    board.makeMove(expNode.getMove());

                    if(board.getStatus() != OK) {
//                        System.out.println("Setting terminal");
                        expNode.setTerminalTrue();
                    } else {
                        expNode.backPropagateScore(DEF_SCORE, playout(board, playStrategy));
                    }
                }
            }
    }
}
