package towerwarspp.player.mcts;


import static towerwarspp.main.debug.DebugLevel.LEVEL_3;
import static towerwarspp.main.debug.DebugLevel.LEVEL_4;
import static towerwarspp.main.debug.DebugLevel.LEVEL_5;
import static towerwarspp.main.debug.DebugSource.PLAYER;
import static towerwarspp.player.mcts.Mcts.DEF_SCORE;
import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;
import static towerwarspp.preset.Status.BLUE_WIN;
import static towerwarspp.preset.Status.OK;

import towerwarspp.board.Board;
import towerwarspp.main.Debug;
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


    UpdateTree(Board board, Node root, PlayStrategy playStrategy) {
        this.debug = Debug.getInstance();
        this.board = board;
        this.root = root;
        this.playStrategy = playStrategy;
    }

    static PlayerColor playout(Board board, PlayStrategy playStrategy) {
        while (board.getStatus() == OK) {
            Move move;
            if(playStrategy == PlayStrategy.LIGHT)
                move = PlayStrategy.rndPlay(board);
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

    @Override
    public void run() {
        Node selectedChild = root;
        debug.send(LEVEL_3, PLAYER, "Mcts: Select on " + root);
        debug.send(LEVEL_4, PLAYER, "Roots children: " + root.getChildren().toString());
//        if(selectedChild.isExpanded() && !selectedChild.isTerminal()) {
//           selectedChild = selectedChild.nthBestUCBChild(nthSubtree % selectedChild.childCount());
//        }
//        Node expNode = null;
//        do {
//            while (selectedChild.isExpanded() && !selectedChild.isTerminal()) {
//                selectedChild = selectedChild.bestUCBChild();
//                debug.send(LEVEL_5, PLAYER, "Mcts: selected child " + selectedChild);
//
//                board.makeMove(selectedChild.getMove());
//                if(board.getStatus() != OK) {
//                    selectedChild.setTerminalTrue();
//                    debug.send(LEVEL_5, PLAYER, "Mcts: Found terminal Node " + selectedChild);
//                }
//            }
//
//            debug.send(LEVEL_3, PLAYER, "Mcts: Found best child " + selectedChild);
//
//            if(selectedChild.isTerminal()) {
//                selectedChild.backPropagateScore(DEF_SCORE, board.getStatus() == BLUE_WIN ? BLUE : RED);
//            }
//
//            if(!selectedChild.isTerminal() && !selectedChild.isExpanded()) {
//                expNode = selectedChild.expand(board);
//                if(expNode != null) {
//
//                    board.makeMove(expNode.getMove());
//
//                    if(board.getStatus() != OK) {
//                        expNode.setTerminalTrue();
//                    } else {
//                        expNode.backPropagateScore(DEF_SCORE, playout(board, playStrategy));
//                    }
//                }
//            }
//
//        } while (expNode == null && !selectedChild.isTerminal());




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
        }

        if(!selectedChild.isTerminal() && !selectedChild.isExpanded()) {
            Node expNode = selectedChild.expand(board);
            if(expNode != null) {

                board.makeMove(expNode.getMove());

                if(board.getStatus() != OK) {
                    expNode.setTerminalTrue();
                } else {
                    expNode.backPropagateScore(DEF_SCORE, playout(board, playStrategy));
                }
            }
        }
    }
}
