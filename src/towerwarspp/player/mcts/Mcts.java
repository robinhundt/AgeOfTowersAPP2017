package towerwarspp.player.mcts;

import towerwarspp.board.Board;
//import towerwarspp.io.TextIO;
import towerwarspp.io.TextIO;
import towerwarspp.main.Debug;
import towerwarspp.player.PlayStrategy;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;


import static towerwarspp.main.debug.DebugLevel.*;
import static towerwarspp.main.debug.DebugSource.PLAYER;

import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;
import static towerwarspp.preset.Status.BLUE_WIN;
import static towerwarspp.preset.Status.OK;

/**
 * Created by robin on 09.07.17.
 */
public class Mcts implements Runnable{
    private Debug debug;
    static final int DEF_SCORE = 1;
    private static Random random = new Random();
    private Node root;
    private Board board;
    private Board newBoard;
    private PlayerColor player;
    private PlayerColor newPlayer;
    private Move currentBestMove;
    private Move enemyMove;
    private boolean moveRequested;
    private boolean moveReceived;
    private boolean initFlag;
    private long timePerMove;
    private long startTime;


    private PlayStrategy strategy = PlayStrategy.HEAVY;

    //DEBUG STUFF
    private TextIO io;

    public Mcts(long timePerMove) {
        this.timePerMove = timePerMove;
        debug = Debug.getInstance();
        debug.send(LEVEL_1, PLAYER, "Mcts: Created new mcts object.");

        // DEBUG STUFF

    }

    private void init() {
        board = newBoard;
        player = newPlayer;
        io = new TextIO();
        io.setViewer(board.viewer());
        io.visualize();
        root = new Node(board, board.getTurn());
        for(Node child : root.getChildren()) {
            Board boardClone = board.clone();
            boardClone.update(child.getMove(), child.getPlayer());
            child.backPropagateScore(DEF_SCORE, playout(boardClone));
        }
        debug.send(LEVEL_1, PLAYER, "Mcts: Initialized adv2 player and expanded root.");
        initFlag = false;
    }

    public void setInit(Board board, PlayerColor playerColor) {
        newBoard = board;
        newPlayer = playerColor;
        initFlag = true;
    }


    @Override
    public void run() {

//        io.visualize();

        while (true) {
//            io.setViewer(board.viewer());
            if(initFlag) {
                init();
            } else if(!initFlag && moveReceived) {
                moveReceived = false;
//                System.out.println("before updateRoot");
//                io.visualize();
                updateRoot(enemyMove);
//                System.out.println("After update root");
//                io.visualize();
            } else if(!initFlag && moveRequested && System.currentTimeMillis() > startTime + timePerMove) {
                moveRequested = false;
//                System.out.println("Before current bestmove");
//                io.visualize();
                currentBestMove = bestMove();
//                System.out.println("after currentbestmove");
//                io.visualize();

                wakeUp();
            } else if(!initFlag && moveRequested && root.hasTerminalChild()) {
                moveRequested = false;
                currentBestMove = root.getTerminalChild().getMove();
                wakeUp();
            }

            select();
        }
    }

    private synchronized void wakeUp() {
        notify();
    }

    private Move bestMove() {
        debug.send(LEVEL_2, PLAYER, "Mcts: Completed tree iterations, calculating best move...");
        Node maxChild = root.maxChild();
        debug.send(LEVEL_2, PLAYER, "Mcts: Returning move " + maxChild + " of max child, with weight: "
                + maxChild.getWeight());
        updateRoot(maxChild);
        return maxChild.getMove();
    }

    public synchronized Move getMove() {
        startTime = System.currentTimeMillis();
        moveRequested = true;
        try {
            wait();
        } catch (InterruptedException e) {
            debug.send(LEVEL_1, PLAYER, "Mcts: Thread interrupted during Mcts.bestMove()");
        }
        return currentBestMove;
    }

    public void feedEnemyMove(Move move) {
        enemyMove = move;
        moveReceived = true;
    }


    private void updateRoot(Move move) {
        for(Node node : root.getChildren()) {
            debug.send(LEVEL_3, PLAYER, "MCTS: Root has child " + node);
            if(node.getMove().equals(move)) {
                root = node;
                freeAncestorMemory(root);
                debug.send(LEVEL_2, PLAYER, "Mcts: New root " + root);
                System.out.println("Before root update");
                io.visualize();
                board.update(root.getMove(), root.getPlayer());
                System.out.println("After root update");
                io.visualize();
                if(board.getStatus() != OK)
                    root.setTerminalTrue();
                else
                    rootPlayout();
                break;
            }
        }
    }

    private void updateRoot(Node node) {
        root = node;
        freeAncestorMemory(root);
        board.update(root.getMove(), root.getPlayer());
        if(board.getStatus() != OK)
            root.setTerminalTrue();
        else
            rootPlayout();
    }



    private void select() {
        Board tempBoard = board.clone();
//        io.setViewer(tempBoard.viewer());
        Node selectedChild = root;
//        System.out.println("Select on " + selectedChild);
////        io.visualize();
        debug.send(LEVEL_3, PLAYER, "Mcts: Select on " + root);
        debug.send(LEVEL_4, PLAYER, "Roots children: " + root.getChildren().toString());
        while (selectedChild.isExpanded() && !selectedChild.isTerminal()) {
            selectedChild = selectedChild.bestChild();
            debug.send(LEVEL_5, PLAYER, "Mcts: selected child " + selectedChild);
            Vector<Move> movesBlue = null;
            Vector<Move> movesRed = null;
            try {
                movesBlue = tempBoard.allPossibleMoves(BLUE);
                movesRed = tempBoard.allPossibleMoves(RED);
////                System.out.println("Inside selection");
////                io.visual ize();
                tempBoard.update(selectedChild.getMove(), selectedChild.getPlayer());
            } catch (Exception e) {
                e.printStackTrace();
                printAncestor(selectedChild);
//                System.out.println("blue" + movesBlue);
//                System.out.println("red" + movesRed);
//                io.visualize();
                try{
                    Thread.sleep(50000);
                } catch (InterruptedException i) {
//                    System.out.println(i);
                }
            }
            if(tempBoard.getStatus() != OK) {
                selectedChild.setTerminalTrue();
                debug.send(LEVEL_5, PLAYER, "Mcts: Found terminal Node " + selectedChild);
            }
        }

        debug.send(LEVEL_3, PLAYER, "Mcts: Found best child " + selectedChild);

        if(selectedChild.isTerminal()) {
            selectedChild.backPropagateScore(DEF_SCORE, tempBoard.getStatus() == BLUE_WIN ? BLUE : RED);
        }

        if(!selectedChild.isTerminal()) {
            Node expNode = selectedChild.expand(tempBoard);
////            System.out.println("Expanded selectedChild");
////            System.out.println("Making move " + expNode.moveRequested() + expNode.getPlayer());
            if(expNode != null) {
                Vector<Move> movesBlue = null;
                Vector<Move> movesRed = null;
                try {
                    movesBlue = tempBoard.allPossibleMoves(BLUE);
                    movesRed = tempBoard.allPossibleMoves(RED);
//                    System.out.println("Inside expansion");
//                    io.visualize();
                    tempBoard.update(expNode.getMove(), expNode.getPlayer());
//                    System.out.println("After expansion");
//                    io.visualize();
                } catch (Exception e) {
                    e.printStackTrace();
                    printAncestor(expNode);
//                    System.out.println("blue" + movesBlue);
//                    System.out.println("red" + movesRed);
//                    io.visualize();
                    try{
                        Thread.sleep(50000);
                    } catch (InterruptedException i) {
//                        System.out.println(i);
                    }

                }
                if(tempBoard.getStatus() != OK) {
                    expNode.setTerminalTrue();
                } else {
                    expNode.backPropagateScore(DEF_SCORE, playout(tempBoard));
                }
            }
        }
    }

    void rootPlayout() {

        ArrayList<Node> unexploredNodes = root.fullExpand(board);
        for(Node unexplNode : unexploredNodes) {
            Board tempBoard = board.clone();
//            io.setViewer(tempBoard.viewer());
//            System.out.println("Before update in rootplayout");
////            io.visualize();
            tempBoard.update(unexplNode.getMove(), unexplNode.getPlayer());
//            System.out.println("Before update in rootplayout");
//            io.visualize();
            if(tempBoard.getStatus() != OK) {
                unexplNode.setTerminalTrue();
            } else {
                unexplNode.backPropagateScore(DEF_SCORE, playout(tempBoard));
            }
        }
    }

    PlayerColor playout(Board board) {
        while (board.getStatus() == OK) {
            Vector<Move> moves = board.allPossibleMoves(board.getTurn());
            Move move;
            if(strategy == PlayStrategy.LIGHT)
                move = PlayStrategy.rndPlay(board);
            else
                move = PlayStrategy.heavyPlay(board);
////            System.out.println(move + " " + turn);
//            if(!board.moveAllowed(move, board.getTurn())) {
//                System.out.println(move + " not allowed");
//                for(Move m : moves)
//                    System.out.println(m);
//            }
//            System.out.println("Before play");
//            System.out.println(board.getStatus());
//            io.visualize();
            board.update(move, board.getTurn());
//            System.out.println("After play");
//            io.visualize();
        }

        switch (board.getStatus()) {
            case BLUE_WIN: return BLUE;
            case RED_WIN: return RED;
            default: throw new IllegalStateException("Mcts.playout. Board status can not be illegal.");
        }
    }

    void printAncestor(Node node) {
//        System.out.println(node);
        if(node.getParent() != null)
            printAncestor(node.getParent());
    }

    void freeAncestorMemory(Node node) {
        if(node.getParent() != null) {
            Node parent = node.getParent();
            node.setParent(null);
            freeAncestorMemory(parent);
        }

    }

}
