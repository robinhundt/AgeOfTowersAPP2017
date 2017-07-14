package towerwarspp.player.mcts;

import towerwarspp.board.Board;
import towerwarspp.main.Debug;
import towerwarspp.player.PlayStrategy;

import static towerwarspp.main.debug.DebugLevel.LEVEL_3;
import static towerwarspp.main.debug.DebugLevel.LEVEL_4;
import static towerwarspp.main.debug.DebugLevel.LEVEL_5;
import static towerwarspp.main.debug.DebugSource.PLAYER;
import static towerwarspp.player.mcts.Mcts.DEF_SCORE;
import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;
import static towerwarspp.preset.Status.BLUE_WIN;
import static towerwarspp.preset.Status.OK;

/**
 * Created by robin on 14.07.17.
 */
class UpdateTree implements Runnable {
    private Debug debug;
    private Board board;
    private Node root;
    private PlayStrategy playStrategy;

    UpdateTree(Board board, Node root, PlayStrategy playStrategy) {
        this.board = board;
        this.root = root;
        this.playStrategy = playStrategy;
        this.debug = Debug.getInstance();
    }

    @Override
    public void run() {
        Node selectedChild = root;
        debug.send(LEVEL_3, PLAYER, "Mcts: Select on " + root);
        debug.send(LEVEL_4, PLAYER, "Roots children: " + root.getChildren().toString());
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

        if(!selectedChild.isTerminal()) {
            Node expNode = selectedChild.expand(board);
            if(expNode != null) {

                board.makeMove(expNode.getMove());

                if(board.getStatus() != OK) {
                    expNode.setTerminalTrue();
                } else {
                    expNode.backPropagateScore(DEF_SCORE, Mcts.playout(board, playStrategy));
                }
            }
        }
    }
}
