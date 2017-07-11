package towerwarspp.player.mcts;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import static towerwarspp.preset.Status.*;
import static towerwarspp.preset.PlayerColor.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by robin on 09.07.17.
 */
public class OldNode implements Comparable<OldNode> {
    public static final double BIAS = 0.6;
    public static final int simCount = 1;
    public static int maxDepth = 0;


    private static Random random = new Random();
    private static Board board;

    private double wins;
    private double games;
    private boolean terminal;
    private boolean expanded;
    private PlayerColor playerColor;
    private PlayerColor enemyColor;
    private Move move;
    private OldNode parent;
    private ArrayList<OldNode> children;
    private ArrayList<OldNode> unvisitedChildren;
    int depth;


    /**
     * Create root OldNode
     * @param state
     */
    OldNode(Board state, PlayerColor playerColor) {
        this.playerColor = playerColor;
        this.enemyColor = this.playerColor == RED ? BLUE : RED;
        board = state;
        depth = 0;
        expandRoot();
    }

    /**
     * Create non-root OldNode
     * @param move
     * @param parent
     */
    OldNode(Move move, OldNode parent) {
        this.parent = parent;
        this.playerColor = parent.playerColor;
        this.enemyColor = parent.enemyColor;
        this.move = move;
        this.children = new ArrayList<>();

        this.depth = parent.depth +1;
        if(this.depth > maxDepth)
            maxDepth = depth;
//        System.out.println("Child OldNode constructed");
    }

    OldNode highestUCTChild() {
        if(!expanded)
            throw new IllegalStateException("Can not look for best child on unexpanded OldNode");
        OldNode bestChild = null;
        double highestBound = Double.MIN_VALUE;
        for(OldNode child : children) {
            double bound = child.upperConfBound(BIAS);
            if(bound > highestBound) {
                bestChild = child;
                highestBound = bound;
            }
        }
        return bestChild;
    }

    OldNode bestChild() {
        OldNode bestChild = null;
        double bestScore = 0;

        for(OldNode child : children) {
            System.out.println("Child " + child.getMove() + " has weight " + child.getWeight());
            if(child.getWeight() > bestScore) {
                bestChild = child;
                bestScore = child.getWins() / child.getGames();
            }
        }
        System.out.println("Best child had " + bestScore + " score. Games: " + bestChild.getGames());
        return bestChild;
    }

    ArrayDeque<Move> getHistory() {
        ArrayDeque<Move> moveHistory = new ArrayDeque<>();
        OldNode current = this;
        while (current.move != null) {
            moveHistory.addFirst(current.move);
            current = current.parent;
        }
        return moveHistory;
    }

    double getWeight() {
        return wins / games;
    }

    void expandRoot() {
        children = new ArrayList<>();
        for(Move move : board.allPossibleMoves(playerColor)) {
            OldNode child = new OldNode(move, this);
            children.add(child);
        }

        // get statistics on child Nodes
        for(OldNode child : children) {
            child.runSimulation();
        }
        expanded = true;
    }

    void expandNode() {
        if(unvisitedChildren == null) {
            unvisitedChildren = new ArrayList<>();
            for(Move move : board.allPossibleMoves(playerColor)) {
                unvisitedChildren.add(new OldNode(move, this));
            }
        } else {
            OldNode child = unvisitedChildren.get(random.nextInt(unvisitedChildren.size()));
            unvisitedChildren.remove(child);
            if(unvisitedChildren.size() == 0)
                expanded = true;
            children.add(child);
            child.runSimulation();
        }
    }

    void runSimulation() {
//        backPropagateScore(SimGame.simulate(board.clone(), this.getHistory(), playerColor));
    }


//    not working like intended
//    void feedEnemyMove(Move move) {
//        board.update(move, enemyColor);
//        for(OldNode child : children) {
//            try {
//                child.feedEnemyMove(move);
//            } catch (IllegalArgumentException e) {
//                children.remove(child);
//            }
//        }
//        if(unvisitedChildren != null) {
//            for(OldNode child : unvisitedChildren) {
//                try {
//                    child.feedEnemyMove(move);
//                } catch (IllegalArgumentException e) {
//                    children.remove(child);
//                }
//            }
//        }
//    }

    double upperConfBound(double bias) {
        return wins / games + bias * Math.sqrt( Math.log(parent.games) / games);
    }

    void backPropagateScore(double win) {
        games++;
        wins += win;

        if(parent != null)
            parent.backPropagateScore(win);
    }

    void checkWin() {
        Status status = board.getStatus();
        if(status != OK) {
//            backPropagateScore(SimGame.checkWin(status, playerColor));
            terminal = true;
        }
    }



    public double getWins() {
        return wins;
    }

    public double getGames() {
        return games;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public OldNode getParent() {
        return parent;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(OldNode oldNode) {
        return 0;
    }
}
