package towerwarspp.player.mcts;

import static towerwarspp.main.debug.DebugLevel.*;
import static towerwarspp.main.debug.DebugSource.PLAYER;
import static towerwarspp.preset.PlayerColor.*;


import java.util.*;

import towerwarspp.board.Board;
import towerwarspp.main.Debug;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;


/**
 * Node class that represent Node's in a game tree. Are used as the objects the Monte Carlo tree search algorithm implemented
 * in {@link Mcts} operates with.
 * A Node holds information about the Move it represents, the color of the player that has this move, the enemy's color,
 * the parent of this Node, whether it has a terminal child (representing a terminal game state), a list of explored
 * children as well es a list of unexplored children.
 */

public class Node {
    /**
     * {@link Debug} instance that is used to send Debug messages.
     */
    private static Debug debug;
    /**
     * Static Random instance used generate pseudo random ints. No seed is used, since: "This constructor sets the
     * seed of the random number generator to a value very likely to be distinct from any other invocation of this constructor."
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#Random--">JavaDoc Random</a>
     */
    private static Random random = new Random();
    /**
     * Number of wins in the subtree containing this Node.
     */
    private double wins;
    /**
     * Number of games played in the subtree containing this Node.
     */
    private double games;
    /**
     * A Node is expanded if a simulation has been run for all it's {@link #children} and no {@link #unvisitedChildren}
     * are left.
     */
    private boolean expanded;
    /**
     * A Node is terminal if the Move it represents leads to the end of the game.
     */
    private boolean terminal;
    /**
     * The move (game state) represented by this Node.
     */
    private Move move;
    /**
     * The player whose possible {@link #move} is saved in the Node.
     */
    private PlayerColor player;
    /**
     * The players enemy.
     */
    private PlayerColor enemy;
    /**
     * The parent Node of this Node.
     */
    private Node parent;
    /**
     * If a Node has Child that represents a terminal game state, it's reference will be saved here.
     */
    private Node terminalChild;
    /**
     * Explored (meaning a playout has been done from their represented game state) children of this Node.
     */
    private ArrayList<Node> children;
    /**
     * Children of this Node for which no information has been gathered.
     */
    private ArrayList<Node> unvisitedChildren;

    /**
     * Root constructor of the Monte Carlo tree search. Should only be called once per game. Initializes {@link #children}
     * of the root with all possible  next game states.
     * @param board board with initial state to build up tree from
     */
    Node(Board board) {
        debug = Debug.getInstance();
        this.player = board.getTurn() == RED ? BLUE : RED;
        this.enemy = player == RED ? BLUE : RED;

        children = new ArrayList<>();

        for(Move move : board.allPossibleMoves(board.getTurn())) {
            children.add(new Node(move, this));
        }
    }

    /**
     * Private Node constructor that is used to add a new Node to a prior leaf Node.
     * @param move move that should be represented by this Node
     * @param parent direct ancestor of this Node
     */
    private Node(Move move, Node parent) {
        debug = Debug.getInstance();
        this.move = move;
        this.parent = parent;
        this.player = parent.enemy;
        this.enemy = parent.player;
        this.children = new ArrayList<>();
    }
    /**
     * Returns whether this Node has a terminal child (representing a terminal game state).
     * @return
     */
    boolean hasTerminalChild() {
        return terminalChild != null;
    }
    /**
     * Check first with {@link #hasTerminalChild()} if a Node has a terminal Child.
     * If that is the case, this method will return it, otherwise null will be returned.
     * @return
     */
    Node getTerminalChild() {
        return terminalChild;
    }
    /**
     * Returns the weight ({@link #wins} / {@link #games} ratio) of this Node.
     * @return
     */
    double getWeight() {
        return wins / games;
    }

    /**
     * Returns the {@link #move} represented by this Node.
     * @return
     */
    Move getMove() {
        return move;
    }

    /**
     * Returns the player associated with the stored {@link #move}.
     * @return
     */
    PlayerColor getPlayer() {
        return player;
    }

    /**
     * Returns the {@link #player}'s enemy.
     * @return
     */
    PlayerColor getEnemy() {
        return enemy;
    }

    /**
     * Wins for this player in this Nodes subtree.
     * @return
     */
    double getWins() {
        return wins;
    }

    /**
     * Games played in this Nodes subtree.
     * @return
     */
    double getGames() {
        return games;
    }

    /**
     * Will return true if this Node is fully expanded, meaning for all possible children at least one playout has been done.
     * @return
     */
    boolean isExpanded() {
        return expanded;
    }

    /**
     * Returns the direct ancestor of this Node. Null in case of root Node.
     * @return
     */
    Node getParent() {
        return parent;
    }
    /**
     * Use to set the parent value of this Node. Is used in {@link Mcts#freeAncestorMemory(Node)} to set parent references
     * to null.
     */
    void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Returns the ArrayList containing this Nodes explored children.
     * @return
     */
    ArrayList<Node> getChildren() {
        return children;
    }

    /**
     * Returns the number of explored children.
     * @return
     */
    int childCount() {
        return children.size();
    }

    /**
     * Sets both this Nodes {@link #terminal} and {@link #expanded} status to true and calls {@link #backPropagateScore(double, PlayerColor)}
     * with arguments {@link Mcts#DEF_SCORE} and {@link #player} (a terminal Node can only represent a winning move).
     */
    void setTerminalTrue() {
        terminal = true;
        expanded = true;
        if(parent != null)
            parent.terminalChild = this;
        backPropagateScore(Mcts.DEF_SCORE, player);
    }

    /**
     * Returns the status of {@link #terminal}
     * @return
     */
    boolean isTerminal() {
        return terminal;
    }

    /**
     * Returns the Upper Confidence Bound 1 (UCB1) value for this Node. The value is calculated like this:
     * w / n + c * sqrt( ln(t) / n )
     * where:
     *  w: number of won games for this Node {@link #wins}
     *  n: number of games played for this Node {@link #games}
     *  c: bias parameter that can be set t
     * @param bias
     * @return
     */
    double upperConfBound(double bias) {
        return wins / games + bias * Math.sqrt( Math.log(parent.games) / games);
    }

    /**
     * Used to expand a Node by selecting one of it's unexplored children from {@link #unvisitedChildren} and adding it to
     * it's {@link #children} list.
     * Can return null in case there are no next possible game states. In this case {@link #setTerminalTrue()} is also
     * called on the Node.
     * @param board board representing a game state from which the current Node should be expanded (board will not be modified)
     * @return the newly created leaf Node
     */
    synchronized Node expand(Board board) {
        if(expanded) {
            debug.send(LEVEL_4, PLAYER, "Node: expand() called on already expanded Node from " + Thread.currentThread());
            throw new IllegalStateException("Node already expanded.");
        }
        if(terminal) {
            debug.send(LEVEL_1, PLAYER, "Node: expand() called on terminal Node.");
            throw new IllegalStateException("Cannot expand terminal node");
        }

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
        if(debug.isCollecting())
            debug.send(LEVEL_7, PLAYER, "Node: Expanded Node " + this + " by adding " + child + " to children.");
        return child;
    }

    /**
     * Use this function to fully expand a node. Should be used when updating the root
     * of the tree. Calling this function will cause {@link #expanded} to be set to true.
     * @param board board representing a game state from which the current Node should be expanded (board will not be modified)
     * @return an ArrayList of Node objects that holds all unvisited Nodes of the current one
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
        debug.send(LEVEL_4, PLAYER, "Node: Full expand on Node: " + this);
        return unexploredChildren;
    }

    /**
     * Method to calculate the UCB1 score via {@link #upperConfBound(double)} and return the Node that has the highest
     * value. Is used to determine the Node in the Selection phase of the MCTS algorithm from which the expansion should
     * start.
     * @return child that has the highest UCB1 value
     */
    Node bestUCBChild() {
        if(!expanded)
            throw new IllegalStateException("bestUCBChild can only be called on expanded Nodes");

        Node bestChild = null;
        double highestBound = Double.MIN_VALUE;
        for(Node child : children) {
            double bound = child.upperConfBound(Mcts.BIAS);
            if(debug.isCollecting())
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
    // TODO either comment or delete
    Node nthBestUCBChild(int nthHighest) {
        if(!expanded)
            throw new IllegalStateException("bestUCBChild can only be called on expanded Nodes");
        ArrayList<ScoredNode> scoredNodes = new ArrayList<>(children.size());
        for(Node node : children) {
            scoredNodes.add(new ScoredNode(node, node.upperConfBound(Mcts.BIAS)));
        }
        Collections.sort(scoredNodes);
        return scoredNodes.get(scoredNodes.size()  - nthHighest).node;

    }

    /**
     * Method to select the child Node which has the highest weight (win / games ratio) returned by calling {@link #getWeight()}
     * on all of the children this Node.
     * @return child Node that has the highest weight
     */
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

    /**
     * Method to select the child Node which has the highest {@link #games} count of all the children this Node has.
     * @return child Node that has the highest game count
     */
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


    /**
     * Method to backpropagate a win score (usually the {@link Mcts#DEF_SCORE} ) and a winner denoted by his {@link PlayerColor}
     * @param score score that is backpropagated
     * @param winner Player who won the game
     */
    void backPropagateScore(double score, PlayerColor winner) {
        games++;
        if(player == winner)
            this.wins += score;

        if(parent != null)
            parent.backPropagateScore(score, winner);
    }

    /**
     * Returns a string representation of this Node consisting of the {@link #move}, the {@link #player}, whether this
     * Node is {@link #expanded} and/or {@link #terminal}, the number of {@link #wins} and {@link #games}.
     * @return String representation of this Node
     */
    @Override
    public String toString() {
        if(move != null)
            return move.toString() + " " + player + " expanded: " + expanded + " terminal: " + terminal + " wins: "
                    + wins + " games " + games;
        else
            return "root of tree " + player + " expanded: " + expanded + " terminal: " + terminal + " wins: "
                    + wins + " games " + games;
    }
}



