package towerwarspp.player.mcts;

import towerwarspp.board.Board;
import towerwarspp.main.Debug;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;

import static towerwarspp.main.debug.DebugLevel.*;
import static towerwarspp.main.debug.DebugSource.PLAYER;
import static towerwarspp.preset.PlayerColor.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by robin on 11.07.17.
 */

public class Node {
    private static Debug debug;
    private static Random random = new Random();
    private Move move;
    private PlayerColor player;
    private PlayerColor enemy;
    private double wins;
    private double games;
    private boolean expanded;
    private boolean terminal;



    private Node parent;
    private Node terminalChild;

    private ArrayList<Node> children;
    private ArrayList<Node> unvisitedChildren;


    Node(Board board, PlayerColor player) {
        debug = Debug.getInstance();
        this.player = player == RED ? BLUE : RED;
        this.enemy = player;

        children = new ArrayList<>();

        for(Move move : board.allPossibleMoves(enemy)) {
            children.add(new Node(move, this));
        }
    }

    private Node(Move move, Node parent) {
        debug = Debug.getInstance();
        this.move = move;
        this.parent = parent;
        this.player = parent.enemy;
        this.enemy = parent.player;
        this.children = new ArrayList<>();
    }

    synchronized Node expand(Board board) {
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
                setTerminalTrue();
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

    /**
     * Board will not be modified. Use this function to fully expand a node. Should be used when updating the root
     * of the tree.
     */
    ArrayList<Node> fullExpand(Board board) {
        ArrayList<Node> unexploredChildren = new ArrayList<>();
        if(unvisitedChildren != null) {
            for(Iterator<Node> it = unvisitedChildren.iterator(); it.hasNext(); ) {
                Node child = it.next();
                children.add(child);
                unexploredChildren.add(child);
                it.remove();
            }
        } else {
            for(Move move : board.allPossibleMoves(enemy)) {
                Node child = new Node(move, this);
                children.add(child);
                unexploredChildren.add(child);
            }
        }
        expanded = true;
        return unexploredChildren;
    }

    Node bestUCBChild() {
        if(!expanded)
            throw new IllegalStateException("bestUCBChild can only be called on expanded Nodes");

        Node bestChild = null;
        double highestBound = Double.MIN_VALUE;
        for(Node child : children) {
            double bound = child.upperConfBound(Mcts.BIAS);
            debug.send(LEVEL_7, PLAYER, "Node: UCB of " + child + " : " + bound);
            if(Double.isNaN(bound))
                bound = Double.POSITIVE_INFINITY;

            if(bound > highestBound) {
                bestChild = child;
                highestBound = bound;
            }
        }
        return bestChild;
    }


    Node maxChild() {
        Node maxChild = null;
        double maxWeight = 0;
        debug.send(LEVEL_3, PLAYER, "Node: Selecting best Move from: ");
        for(Node child : children) {
            double weight = child.getWeight();
            debug.send(LEVEL_3, PLAYER, "Node: Child Node: " + child + " weight: " + child.getWeight());
            if(weight > maxWeight) {
                maxChild = child;
                maxWeight = weight;
            }
        }
        return maxChild;
    }

    Node robustChild() {
        Node robustChild = null;
        double maxGames = 0;
        debug.send(LEVEL_3, PLAYER, "Node: Selecting best Move from: ");
        for(Node child : children) {
            double games = child.getGames();
            debug.send(LEVEL_3, PLAYER, "Node: Child Node: " + child + " weight: " + child.getWeight());
            if(games > maxGames) {
                robustChild = child;
                maxGames = games;
            }
        }
        return robustChild;
    }

    boolean hasTerminalChild() {
        return terminalChild != null;
    }

    Node getTerminalChild() {
        return terminalChild;
    }



    void backPropagateScore(double score, PlayerColor winner) {
        games++;
        if(player == winner)
            this.wins += score;

        if(parent != null)
            parent.backPropagateScore(score, winner);
    }

    double getWeight() {
        return wins / games;
    }


    double upperConfBound(double bias) {
        return wins / games + bias * Math.sqrt( Math.log(parent.games) / games);
    }

    Move getMove() {
        return move;
    }

    PlayerColor getPlayer() {
        return player;
    }

    PlayerColor getEnemy() {
        return enemy;
    }

    double getWins() {
        return wins;
    }

    double getGames() {
        return games;
    }

    boolean isExpanded() {
        return expanded;
    }

    boolean isTerminal() {
        return terminal;
    }

    Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    ArrayList<Node> getChildren() {
        return children;
    }

    void setTerminalTrue() {
        terminal = true;
        expanded = true;
        if(parent != null)
            parent.terminalChild = this;
        backPropagateScore(Mcts.DEF_SCORE, player);
    }

    @Override
    public String toString() {
        if(move != null)
            return move.toString() + " " + player + " expanded: " + expanded + " terminal: " +terminal + " wins: "
                    + wins + " games " + games;
        else
            return "root of tree";
    }
}



