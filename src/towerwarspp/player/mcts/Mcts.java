package towerwarspp.player.mcts;

import static towerwarspp.main.debug.DebugLevel.*;
import static towerwarspp.main.debug.DebugSource.PLAYER;
import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;
import static towerwarspp.preset.Status.BLUE_WIN;
import static towerwarspp.preset.Status.OK;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.concurrent.Future;

import towerwarspp.board.Board;
import towerwarspp.main.Debug;
import towerwarspp.player.PlayStrategy;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;


/**
 * This class implements a concurrent version of the Monte Carlo tree search algorithm. In order to balance between
 * exploitation and exploration the UCT (Upper Confidence Bound 1 applied to trees) is used (see wikipedia Monte Carlo
 * tree search for detailed information  <a href="https://en.wikipedia.org/wiki/Monte_Carlo_tree_search">Monte Carlo
 * tree search</a>.
 *
 * The main algorithm is divided into 4 steps: Selection - Expansion - Simulation - Backpropagation
 *
 * Selection: From the root we descend into the tree always choosing the Node for which the UCB1 formulae is highest.
 * The algorithm descends as far as the Node he is at is expanded, meaning for all child Nodes at least one Simulation has
 * been run or until hea reaches a terminal Node (representing the end of the game).
 *
 * Expansion: If a Node is reached that is not fully expanded one of the next possible game states is added as a child
 * and a simulation is run for this new child. If it was the last unvisited child, the Nodes expanded status is set to
 * true.
 *
 * Simulation: After expansion of a Node for the new created Node a game is played through until a terminal state.
 * The playout is done depending on the set {@link PlayStrategy}. If {@link PlayStrategy#LIGHT} is set, the games will be
 * played to the end as if both players are choosing random moves. If {@link PlayStrategy#HEAVY} is set, the games will be
 * played as if both players were playing by choosing the move that has the best score returned by {@link Board#altScore(Move, PlayerColor)}.
 * {@link PlayStrategy#heavyPlay(Board)} is used to get this move.
 *
 * Backpropagation: After each Simulation the result of the playout is propagated up through the tree by calling the
 * {@link Node#backPropagateScore(double, PlayerColor)} method on the Node where the simulation was run.
 *
 * By concurrent it's meant that the algorithm (the select - expand - simulate - backpropagate loop) is executed all the
 * time, even if it's the opponents turn. By specifying the {@link #parallelizationFactor} variable during object
 * construction, the maximal amount of concurrent loop iterations can be controlled. On computers that have a high number of possible
 * Threads setting this variable to a value up to twice the amount of possible Threads will cause the algorithm to converge
 * against optimal play a lot faster. This means, that especially for opponents that need a long time to decide on their next
 * move (e.g. a human player or another ai that takes considerable time to choose a move) valuable information on the search
 * tree can be gathered by continuing the main algorithm loop. When the opponent decides on one of his possible moves,
 * the root of the tree is set to the Node representing this action und the resulting subtree is then used for the algorithm.
 * Likewise if a move is decided on, the root of the tree is set to it's child Node representing this move.
 * Through this concurrency and reuse of viable subtree the algorithm achieves better result than by building up a new
 * search tree for each move request.
 * @author Robin Hundt
 * @version 13-07-17
 */
public class Mcts implements Runnable{
    /**
     * Bias that is used as a constant in the calculation of the UCB1 in formulae during {@link Node#bestUCBChild()}.
     */
    static double BIAS = 1.5;
    /**
     * The default score that is backpropagated through the tree at the end of a simulation or when a terminal node is
     * reached.
     */
    static final int DEF_SCORE = 1;
    /**
     * Reference to Debug object that is used to send debug messages.
     */
    private Debug debug;
    /**
     * ExecutorService that is used to provide parallelization of the main select - expand - simulate - backpropagate
     * loop of the algorithm. Is used to run instance of the Runnable {@link UpdateTree}.
     */
    private ExecutorService updatePool;
    /**
     * Array of Futures that is used to store the Futures returned by the {@link ExecutorService#submit(Runnable)} method.
     */
    private Future[] futures;
    /**
     * Specifies the number of Threads that is at most used to concurrently execute {@link UpdateTree} instances.
     */
    private int parallelizationFactor = 1;
    /**
     * The current root of the search tree.
     */
    private Node root;
    /**
     * The current board that is always at the state represented by the root of the tree.
     *
     */
    private Board board;
    /**
     * This variable can be set from outside this Thread. If {@link #initFlag} is also set to true, the main loop in
     * {@link #run()} will register the change and reinitialize the tree.
     */
    private Board newBoard;
    /**
     * Holds the current best move chosen dependent on the {@link TreeSelectionStrategy}. Will either contain the
     * Move corresponding to the max child (highest win / games ratio) or the robust child (most games) of the root.
     */
    private Move currentBestMove;
    /**
     * Holds the last Move the enemy made.
     */
    private Move enemyMove;
    /**
     * If fairPlay is set to true, the algorithm will spend as much time calculating the next move as the enemy player
     * took to decide on his last move.
     */
    private boolean fairPlay;
    /**
     * Flag that is set to true when {@link #getMove()} is called.
     */
    private boolean moveRequested;
    /**
     * Flag that is set to true when {@link #feedEnemyMove(Move)} is called.
     */
    private boolean moveReceived;
    /**
     * Flag that is set to true when {@link #setInit(Board)} is called.
     */
    private boolean initFlag;
    /**
     * The time the algorithm will spend expanding the tree once {@link #getMove()} has been called.
     */
    private long timePerMove;
    /**
     * The time at which a move was requested by a call to {@link #getMove()}.
     */
    private long startTime;
    /**
     * The time at which the algorithm decided on his next move.
     */
    private long endTime;
    /**
     * The {@link PlayStrategy} used for this instance of the MCTS algorithm. If set to {@link PlayStrategy#LIGHT} random
     * games will be played in the simulation phase. If set to {@link PlayStrategy#HEAVY} the evaluation function
     * provided by {@link Board#altScore(Move, PlayerColor)} is used to carry out the simulated games.
     */
    private PlayStrategy playStrategy = PlayStrategy.HEAVY;
    /**
     * The  {@link TreeSelectionStrategy} used  for this instance of the MCTS algorithm. If set to
     * {@link TreeSelectionStrategy#MAX} upon request of a move, the move of the child  that has the highest win / games ratio
     * returned by {@link Node#getWeight()} will be returned.
     * If set to {@link TreeSelectionStrategy#ROBUST} the move of the child with most played out games will be returned.
     */
    private TreeSelectionStrategy treeSelectionStrategy = TreeSelectionStrategy.ROBUST;


    /**
     * Construct a new Mcts object that approximately takes the specified time per move in milliseconds to decide on a move after
     * one has been requested.
     * @param timePerMove time in milliseconds that the algorithm will spend on deciding which move to make after the request.
     * @param parallelizationFactor Specifies the number of Threads that is at most used to concurrently execute {@link UpdateTree} instances.
     */
    public Mcts(long timePerMove, int parallelizationFactor) {
        this.timePerMove = timePerMove;
        this.parallelizationFactor = parallelizationFactor;
        debug = Debug.getInstance();
        updatePool = Executors.newWorkStealingPool();
        futures = new Future[parallelizationFactor];
        debug.send(LEVEL_1, PLAYER, "Mcts: Created new mcts object.");
    }

    /**
     * Constructs a new Mcts object with the specified time per move, {@link PlayStrategy} and {@link TreeSelectionStrategy}
     * @param timePerMove time to decide on a move after a request
     * @param parallelizationFactor Specifies the number of Threads that is at most used to concurrently execute {@link UpdateTree} instances.
     * @param playStrategy {@link PlayStrategy} to use in the simulation phase of the algorithm.
     * @param treeSelectionStrategy {@link TreeSelectionStrategy} to decide on the child representing a move when a move
     *                              is requested.
     * @param fairPlay if set to true, the algorithm will spend approximately as much time deciding on it's next move,
     *                 as the enemy took for his last move, the passed timPerMove will only be considered at his first move
     *                 if the player is red.
     */
    public Mcts(long timePerMove, int parallelizationFactor, PlayStrategy playStrategy, TreeSelectionStrategy treeSelectionStrategy, boolean fairPlay) {
        this(timePerMove, parallelizationFactor);
        this.playStrategy = playStrategy;
        this.treeSelectionStrategy = treeSelectionStrategy;
        this.fairPlay = fairPlay;
    }



    /**
     * Reinitialize this Mcts object by setting the board to the new Board passed via {@link #newBoard}.
     * A new {@link #root} is constructed and {@link #rootPlayout()} is called to fully expand the root and do a
     * playout on all the new children.
     */
    private void init() {
        board = newBoard;
        root = new Node(board, board.getTurn());
        rootPlayout();
        debug.send(LEVEL_1, PLAYER, "Mcts: Initialized adv2 player and expanded root.");
        initFlag = false;
    }

    /**
     * Method is called from other Thread.
     * Sets the {@link #newBoard} variable to the passed Board and set {@link #initFlag} to true to signal Thread containing
     * the {@link #run()} method that a new board is available and a new tree should be constructed.
     * @param board that will be used as new initial game state
     */
    public void setInit(Board board) {
        newBoard = board;
        initFlag = true;
    }

    /**
     * Main loop of the Monte Carlo tree search. As long as the Game is running the run method is executed.
     * Each iteration it is checked if any of the {@link #initFlag}, {@link #moveRequested} or {@link #moveReceived} is set
     * causing the board and tree to be reinitialized, a best Move to be chosen or the root to be reset respectively.
     *
     * If none of the flags is set and the board status is OK {@link #parallelizationFactor} many {@link UpdateTree}
     * instances will be created per iteration and submitted to the {@link #updatePool}. After submitting those
     * runnable's it's waited until all returned {@link #futures} are done. Then the loop is executed again.
     *
     * In case the status of the Game is != OK the Thread will sleep for some amount of time per loop iteration to not
     * waste processing power.
     */
    @Override
    public void run() {


        while (true) {
            if(initFlag) {
                init();
            } else if(!initFlag && moveReceived) {
                moveReceived = false;
                updateRoot(enemyMove);
            } else if(!initFlag && moveRequested && System.currentTimeMillis() > startTime + timePerMove) {
                moveRequested = false;
                currentBestMove = bestMove();
                debug.send(LEVEL_2, PLAYER, "Decided on move " + currentBestMove + " in "
                        + (System.currentTimeMillis() - startTime) + " ms.");
                endTime = System.currentTimeMillis();
                wakeUp();
            } else if(!initFlag && moveRequested && root.hasTerminalChild()) {
                /* if a move has been requested and one of the child nodes of the root will lead to direct victory
                * than return this move instantly */
                moveRequested = false;
                currentBestMove = root.getTerminalChild().getMove();
                board.update(currentBestMove, board.getTurn());
                endTime =System.currentTimeMillis();
                wakeUp();
            }
            if(board.getStatus() == OK) {
                for(int i=0; i < parallelizationFactor; i++) {
                    futures[i] = updatePool.submit(new UpdateTree(board.clone(), root, playStrategy));
                }
                boolean updating = true;
                while (updating) {
                    boolean threadWorking = false;
                    for(Future future : futures) {
                        if(future.isDone())
                            updating = false;
                        else
                            threadWorking = true;
                    }
                    if (threadWorking)
                        updating =true;
                }
            } else {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    debug.send(LEVEL_2, PLAYER, "Thread has been interrupted during sleep at end of game.");
                }
            }
        }
    }

    private synchronized void wakeUp() {
        notify();
    }

    private Move bestMove() {
        debug.send(LEVEL_2, PLAYER, "Mcts: Completed tree iterations, calculating best move...");
        Node selectedChild;
        if(treeSelectionStrategy == TreeSelectionStrategy.MAX)
            selectedChild = root.maxChild();
        else
            selectedChild = root.robustChild();
        debug.send(LEVEL_2, PLAYER, "Mcts: Returning move " + selectedChild + " of max child, with weight: "
                + selectedChild.getWeight());
        updateRoot(selectedChild);
        checkTreeSelectionPolicy(selectedChild.getWeight());
        return selectedChild.getMove();
    }

    private void checkTreeSelectionPolicy(double weight) {
        if(weight > 0.90) {
            playStrategy = PlayStrategy.LIGHT;
        } else {
            playStrategy = PlayStrategy.HEAVY;
        }
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
        if(fairPlay) {
            timePerMove = System.currentTimeMillis() - endTime;
        }
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
                board.update(root.getMove(), root.getPlayer());
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
        Node selectedChild = root;
        debug.send(LEVEL_3, PLAYER, "Mcts: Select on " + root);
        debug.send(LEVEL_4, PLAYER, "Roots children: " + root.getChildren().toString());
        while (selectedChild.isExpanded() && !selectedChild.isTerminal()) {
            selectedChild = selectedChild.bestUCBChild();
            debug.send(LEVEL_5, PLAYER, "Mcts: selected child " + selectedChild);

            tempBoard.update(selectedChild.getMove(), selectedChild.getPlayer());
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
            if(expNode != null) {

                tempBoard.update(expNode.getMove(), expNode.getPlayer());

                if(tempBoard.getStatus() != OK) {
                    expNode.setTerminalTrue();
                } else {
                    expNode.backPropagateScore(DEF_SCORE, playout(tempBoard, playStrategy));
                }
            }
        }
    }

    private void rootPlayout() {

        ArrayList<Node> unexploredNodes = root.fullExpand(board);
        for(Node unexplNode : unexploredNodes) {
            Board tempBoard = board.clone();
            tempBoard.update(unexplNode.getMove(), unexplNode.getPlayer());
            if(tempBoard.getStatus() != OK) {
                unexplNode.setTerminalTrue();
            } else {
                unexplNode.backPropagateScore(DEF_SCORE, playout(tempBoard, playStrategy));
            }
        }
    }

    static PlayerColor playout(Board board, PlayStrategy playStrategy) {
        while (board.getStatus() == OK) {
            Move move;
            if(playStrategy == PlayStrategy.LIGHT)
                move = PlayStrategy.rndPlay(board);
            else
                move = PlayStrategy.heavyPlay(board);
            board.update(move, board.getTurn());
        }

        switch (board.getStatus()) {
            case BLUE_WIN: return BLUE;
            case RED_WIN: return RED;
            default: throw new IllegalStateException("Mcts.playout. Board status can not be illegal.");
        }
    }

    private void printAncestor(Node node) {
        if(node.getParent() != null)
            printAncestor(node.getParent());
    }

    private void freeAncestorMemory(Node node) {
        if(node.getParent() != null) {
            Node parent = node.getParent();
            node.setParent(null);
            freeAncestorMemory(parent);
        }

    }

    public PlayStrategy getPlayStrategy() {
        return playStrategy;
    }

    public void setPlayStrategy(PlayStrategy playStrategy) {
        this.playStrategy = playStrategy;
    }

    public TreeSelectionStrategy getTreeSelectionStrategy() {
        return treeSelectionStrategy;
    }

    public void setTreeSelectionStrategy(TreeSelectionStrategy treeSelectionStrategy) {
        this.treeSelectionStrategy = treeSelectionStrategy;
    }
}
