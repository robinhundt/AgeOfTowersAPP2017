package towerwarspp.player.mcts;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import java.util.Random;
import java.util.Vector;

import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;
import static towerwarspp.preset.Status.OK;

/**
 * Created by robin on 09.07.17.
 */
public class Mcts {
    static final int DEF_SCORE = 1;
    static Random random = new Random();
    Node root;
    Board initialBoard;
    PlayerColor player;

    public Mcts(Board initialBoard, PlayerColor player) {
        this.initialBoard = initialBoard;
        this.player = player;
        root = new Node(initialBoard, player);
        for(Node child : root.getChildren()) {
            child.backPropagateScore(DEF_SCORE, playout(initialBoard.clone(), child.getPlayer()));
        }
        System.out.println("Expanded root");
    }

    public Move bestMove(long thinkingTime) {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < startTime + thinkingTime) {
            select(root);
        }
        System.out.println("Done thinking");
        Move move = root.maxChild().getMove();
        System.out.println("returning " + move);
        return move;
    }

    void select(Node root) {
//        System.out.println("selecting...");
        Board tempBoard = initialBoard.clone();
        Node bestChild = root;

        while (bestChild.isExpanded() && !bestChild.isTerminal()) {
            bestChild = bestChild.bestChild();
//            System.out.println("best child " + bestChild.getMove() + bestChild.getPlayer());
            tempBoard.update(bestChild.getMove(), bestChild.getPlayer());
            if(tempBoard.getStatus() != OK)
                bestChild.setTerminal(true);
        }



        if(!bestChild.isTerminal() && !bestChild.isExpanded()) {
            Node expNode = bestChild.expand(tempBoard);
//            System.out.println("Expanded bestChild");
//            System.out.println("Making move " + expNode.getMove() + expNode.getPlayer());
            if(expNode != null) {
                tempBoard.update(expNode.getMove(), expNode.getPlayer());
                if(tempBoard.getStatus() != OK)
                    expNode.setTerminal(true);
                expNode.backPropagateScore(DEF_SCORE, playout(tempBoard, expNode.getEnemy()));
            }
        }
    }

    PlayerColor playout(Board board, PlayerColor player) {
        PlayerColor turn = player;
        while (board.getStatus() == OK) {
            Vector<Move> moves = board.allPossibleMoves(turn);
            Move move = moves.get(random.nextInt(moves.size()));
//            System.out.println(move + " " + turn);
            if(!board.moveAllowed(move, turn)) {
                System.out.println(move + "not allowed");
                for(Move m : moves)
                    System.out.println(m);
            }

            board.update(move, turn);
//            System.out.println(board.getStatus());
            if(board.getStatus() == OK)
                turn = turn == RED ? BLUE : RED;
        }

        switch (board.getStatus()) {
            case BLUE_WIN: return BLUE;
            case RED_WIN: return RED;
            default: throw new IllegalStateException("Mcts.playout. Board status can not be illegal.");
        }
    }

    private static Move rndMove(Vector<Move> moves) {
        return moves.get(random.nextInt(moves.size()));
    }
}
