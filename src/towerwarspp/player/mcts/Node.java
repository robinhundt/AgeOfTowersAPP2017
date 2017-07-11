package towerwarspp.player.mcts;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;

import static towerwarspp.preset.PlayerColor.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by robin on 11.07.17.
 */
public class Node {
    private static double BIAS = 1.4;

    static Random random = new Random();
    Move move;
    PlayerColor player;
    PlayerColor enemy;
    double wins;
    double games;
    boolean expanded;
    boolean terminal;


    Node parent;

    ArrayList<Node> children;
    ArrayList<Node> unvisitedChildren;


    Node(Board board, PlayerColor player) {
        this.player = player == RED ? BLUE : RED;
        this.enemy = player;

        children = new ArrayList<>();

        for(Move move : board.allPossibleMoves(enemy)) {
            children.add(new Node(move, this));
        }
    }

    Node(Move move, Node parent) {
        this.move = move;
        this.parent = parent;
        this.player = parent.enemy;
        this.enemy = parent.player;
        this.children = new ArrayList<>();
    }

    Node expand(Board board) {
        if(expanded)
            throw new IllegalStateException("Node already expanded.");
        if(terminal)
            throw new IllegalStateException("Cannot expand terminal node");

        if(unvisitedChildren == null) {
            unvisitedChildren = new ArrayList<>();

            for(Move move : board.allPossibleMoves(enemy)) {
                unvisitedChildren.add(new Node(move, this));
            }
            if(unvisitedChildren.isEmpty()) {
                expanded = true;
                terminal = true;
                backPropagateScore(0, player);
                return null;
            }
        }

        Node child = unvisitedChildren.get(random.nextInt(unvisitedChildren.size()));
        unvisitedChildren.remove(child);
        if(unvisitedChildren.isEmpty())
            expanded = true;

        children.add(child);
        return child;
    }

    Node bestChild() {
        if(!expanded)
            throw new IllegalStateException("bestChild can only be called on expanded Nodes");

        Node bestChild = null;
        double highestBound = Double.MIN_VALUE;
        for(Node child : children) {
            double bound = child.upperConfBound(BIAS);
            if(bound > highestBound) {
                bestChild = child;
                highestBound = bound;
            }
        }
        return bestChild;
    }

    Node maxChild() {
        Node maxChild = null;
        double maxScore = 0;
        for(Node child : children) {
            double score = child.wins / child.games;
//            System.out.println("checking " + child.getMove() + " score " + score);
            if(score > maxScore) {
                maxChild = child;
                maxScore = score;
            }
        }
        return maxChild;
    }

    void backPropagateScore(double score, PlayerColor winner) {
        games++;
        if(player == winner)
            this.wins += score;

        if(parent != null)
            parent.backPropagateScore(score, winner);
    }


    double upperConfBound(double bias) {
        return wins / games + bias * Math.sqrt( Math.log(parent.games) / games);
    }

    public Move getMove() {
        return move;
    }

    public PlayerColor getPlayer() {
        return player;
    }

    public PlayerColor getEnemy() {
        return enemy;
    }

    public double getWins() {
        return wins;
    }

    public double getGames() {
        return games;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public Node getParent() {
        return parent;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }
}



