package towerwarspp.player.mcts;

import towerwarspp.board.Board;
import towerwarspp.player.Adv2Player;
import towerwarspp.player.PlayStrategy;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.util.debug.Debug;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static towerwarspp.player.mcts.Task.*;
import static towerwarspp.preset.Status.OK;
import static towerwarspp.util.debug.DebugLevel.*;
import static towerwarspp.util.debug.DebugSource.PLAYER;


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
 *
 * @author Robin Hundt
 * @version 13-07-17
 */
public class Mcts implements Runnable {
    /**
     * Default Bias that is used as a constant in the calculation of the UCB1 in formulae during {@link Node#bestUCBChild()}.
     */
    public static final double DEF_BIAS = 2;
    /**
     * The default score that is backpropagated through the tree at the end of a simulation or when a terminal node is
     * reached.
     */
    static final int DEF_SCORE = 1;
    /**
     * Reference to Debug object that is used to send debug messages.
     */
    private final Debug debug;
    /**
     * ExecutorService that is used to provide parallelization of the main select - expand - simulate - backpropagate
     * loop of the algorithm. Is used to run instance of the Runnable {@link UpdateTree}.
     */
    private final ExecutorService updatePool;
    private final ArrayDeque<Task> tasks;
    /**
     * Bias that is used as a constant in the calculation of the UCB1 in formulae during {@link Node#bestUCBChild()}.
     */
    private double bias = DEF_BIAS;
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
     */
    private Board board;
    /**
     * This variable can be set from outside this Thread. If a {@link Task#INIT} is at the front of the queue, the main loop in
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
    private TreeSelectionStrategy treeSelectionStrategy = TreeSelectionStrategy.MAX;


    /**
     * Construct a new Mcts object that approximately takes the specified time per move in milliseconds to decide on a move after
     * one has been requested.
     *
     * @param timePerMove           time in milliseconds that the algorithm will spend on deciding which move to make after the request.
     * @param parallelizationFactor Specifies the number of Threads that is at most used to concurrently execute {@link UpdateTree} instances.
     */
    public Mcts(long timePerMove, int parallelizationFactor) {
        this.timePerMove = timePerMove;
        this.parallelizationFactor = parallelizationFactor;
        debug = Debug.getInstance();
        updatePool = Executors.newFixedThreadPool(parallelizationFactor);
        futures = new Future[parallelizationFactor];
        tasks = new ArrayDeque<>();
        debug.send(LEVEL_1, PLAYER, "Mcts: Created new mcts object.");
    }

    /**
     * Constructs a new Mcts object with the specified time per move, {@link PlayStrategy} and {@link TreeSelectionStrategy}
     *
     * @param timePerMove           time to decide on a move after a request
     * @param parallelizationFactor Specifies the number of Threads that is at most used to concurrently execute {@link UpdateTree} instances.
     * @param playStrategy          {@link PlayStrategy} to use in the simulation phase of the algorithm.
     * @param treeSelectionStrategy {@link TreeSelectionStrategy} to decide on the child representing a move when a move
     *                              is requested.
     * @param fairPlay              if set to true, the algorithm will spend approximately as much time deciding on it's next move,
     *                              as the enemy took for his last move, the passed timPerMove will only be considered at his first move
     *                              if the player is red.
     */
    public Mcts(long timePerMove, int parallelizationFactor, PlayStrategy playStrategy, TreeSelectionStrategy treeSelectionStrategy,
                boolean fairPlay, double bias) {
        this(timePerMove, parallelizationFactor);
        this.playStrategy = playStrategy;
        this.treeSelectionStrategy = treeSelectionStrategy;
        this.fairPlay = fairPlay;
        this.bias = bias;
    }


    /**
     * Returns the {@link PlayStrategy} used by the algorithm at the moment.
     *
     * @return current {@link PlayStrategy}
     */
    public PlayStrategy getPlayStrategy() {
        return playStrategy;
    }

    /**
     * Sets the {@link PlayStrategy} to the passed one.
     * Note: The algorithm will dynamically change it's PlayStrategy to {@link PlayStrategy#LIGHT} if it's close to a
     * winning position to reduce the bias introduced by the Heavy PlayOut.
     *
     * @param playStrategy {@link PlayStrategy} to use from now on
     */
    public void setPlayStrategy(PlayStrategy playStrategy) {
        this.playStrategy = playStrategy;
    }

    /**
     * Return the current {@link TreeSelectionStrategy} employed by the algorithm when deciding on it's next move.
     *
     * @return current tree selection strategy
     */
    public TreeSelectionStrategy getTreeSelectionStrategy() {
        return treeSelectionStrategy;
    }

    /**
     * Sets the passed {@link TreeSelectionStrategy} to the passed one.
     *
     * @param treeSelectionStrategy strategy to use from now on.
     */
    public void setTreeSelectionStrategy(TreeSelectionStrategy treeSelectionStrategy) {
        this.treeSelectionStrategy = treeSelectionStrategy;
    }

    /**
     * This method is always called fro outside this Thread. It will set {@link #startTime} to the current System time.
     * Then add a {@link Task#MOVE_REQUESTED} to the queue and set the callers Thread to wait(). If the Thread is woke up, it
     * will return the Move currently store in {@link #currentBestMove}.
     *
     * @return the {@link #currentBestMove} after the sspecified {@link #timePerMove}
     */
    public synchronized Move getMove() {
        startTime = System.currentTimeMillis();
        addTask(MOVE_REQUESTED);
        try {
            wait();
        } catch (InterruptedException e) {
            debug.send(LEVEL_1, PLAYER, "Mcts: Thread interrupted during Mcts.bestMove()");
        }
        return currentBestMove;
    }

    /**
     * Method is called from other Thread.
     * Sets the {@link #newBoard} variable to the passed Board and add a {@link Task#INIT} to the queue to signal Thread containing
     * the {@link #run()} method that a new board is available and a new tree should be constructed.
     *
     * @param board that will be used as new initial game state
     */
    public void setInit(Board board) {
        newBoard = board;
        addTask(INIT);
    }

    /**
     * Use to update the state of the tree after the enemy has made a move.
     * If {@link #fairPlay} is set to true, {@link #timePerMove} is set to the time the enemy spend deciding on his move.
     * {@link #enemyMove} is set to the passed Move and a {@link Task#MOVE_RECEIVED} is added to the queue, to notify the algorithm
     * Thread that the enemy has made a move.
     *
     * @param move that the enemy made.
     */
    public void feedEnemyMove(Move move) {
        if (fairPlay) {
            if (endTime != 0)
                timePerMove = System.currentTimeMillis() - endTime;
            else
                timePerMove = Adv2Player.DEF_TIME_PER_MOVE;
        }
        enemyMove = move;
        addTask(MOVE_RECEIVED);
    }

    /**
     * This method is called if the Thread has used it's available timePerMove to decide on the final Move that should
     * be played. Depending on the setting of {@link #treeSelectionStrategy} either the Move with the highest win ratio
     * represented by the Node returned by calling {@link Node#maxChild()} on the root, is selected or the move that has been
     * played the most often represented by the Node returned from {@link Node#robustChild()}.
     *
     * @return the Move of the best root child Node depending on the Tree Selection strategy
     */
    private Move bestMove() {
        debug.send(LEVEL_2, PLAYER, "Mcts: Completed tree iterations, calculating best move...");
        Node selectedChild;
        if (treeSelectionStrategy == TreeSelectionStrategy.MAX)
            selectedChild = root.maxChild();
        else
            selectedChild = root.robustChild();
        debug.send(LEVEL_2, PLAYER, "Mcts: Returning move " + selectedChild + " of max child, with weight: "
                + selectedChild.getWeight());
        updateRoot(selectedChild);
        checkPlayoutStrategy(selectedChild.getWeight());
        return selectedChild.getMove();
    }

    /**
     * Adds a {@link Task} the the task queue
     *
     * @param task task to add to end of queue
     */
    synchronized private void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a {@link Task} from the task queue
     */
    synchronized private void removeTask() {
        tasks.removeFirst();
    }

    /**
     * Returns and removes a {@link Task} from the Tsk queue
     *
     * @return task from head of queue
     */
    synchronized private Task pollTask() {
        return tasks.poll();
    }

    /**
     * Returns but does not remove the Task at the head of the queue
     *
     * @return head of the {@link #tasks} queue
     */
    private Task peekTask() {
        return tasks.peek();
    }

    /**
     * Reinitialize this Mcts object by setting the board to the new Board passed via {@link #newBoard}.
     * A new {@link #root} is constructed and {@link #rootPlayout()} is called to fully expand the root and do a
     * playout on all the new children.
     */
    private void init() {
        board = newBoard;
        root = new Node(board, bias);
        if (playStrategy == PlayStrategy.DYNAMIC)
            playStrategy = PlayStrategy.HEAVY;
        rootPlayout();
        debug.send(LEVEL_1, PLAYER, "Mcts: Initialized adv2 player and expanded root.");
    }

    /**
     * Wakes up the Thread that has requested a Move by calling notify().
     */
    private synchronized void wakeUp() {
        notify();
    }

    /**
     * Decides if to update the current {@link PlayStrategy} depending on the weight of the last played Move.
     * If the weight is above 0.98 the play strategy is changed to a {@link PlayStrategy#LIGHT} playout to reduce
     * the bias introduced by the heavy playout strategy.
     *
     * @param weight weight of last selected child Node
     */
    private void checkPlayoutStrategy(double weight) {
        if (playStrategy == PlayStrategy.DYNAMIC) {
            if (weight > 0.98) {
                playStrategy = PlayStrategy.LIGHT;
            }
        }
    }

    /**
     * Used to update the {@link #root} of the tree after the enemy has made a move. First the passed Move is compared
     * against the moves of the child {@link Node}'s of the current root. If a match is found, the child is set as new root,
     * the move is played on the {@link #board} and a {@link #rootPlayout()} is executed.
     * Also memory is made available to be cleared up by the Garbage Collector by deleting references to the now unused
     * subtree.
     *
     * @param move the enemy move.
     */
    private void updateRoot(Move move) {
        for (Node node : root.getChildren()) {
            debug.send(LEVEL_3, PLAYER, "MCTS: Root has child " + node);
            if (node.getMove().equals(move)) {
                root = node;
                freeAncestorMemory(root);
                debug.send(LEVEL_2, PLAYER, "Mcts: New root " + root);
                board.makeMove(root.getMove());
                if (board.getStatus() != OK)
                    root.setTerminalTrue();
                else
                    rootPlayout();
                break;
            }
        }
    }

    /**
     * Used to update the root of the tree after the algorithm has decided on a move to play. First the {@link #root} is
     * set to the passed Node, the move represented by the new root is played on the board and depending on the state of
     * the game a {@link #rootPlayout()} is executed.
     *
     * @param node node to set as new root
     */
    private void updateRoot(Node node) {
        root = node;
        freeAncestorMemory(root);
        board.makeMove(root.getMove());
        if (board.getStatus() != OK)
            root.setTerminalTrue();
        else
            rootPlayout();
    }

    /**
     * This method should always be called after a new root has been set.
     * First {@link Node#fullExpand(Board)} is called on the root to fully expand it and get a List containing all
     * so far unexplored children of that Node. Then for every unexplored child a playout with the current {@link PlayStrategy}
     * is executed and the result backpropagated up the tree.
     */
    private void rootPlayout() {
        ArrayList<Node> unexploredNodes = root.fullExpand(board);
        for (Node unexplNode : unexploredNodes) {
            Board tempBoard = board.clone();
            tempBoard.makeMove(unexplNode.getMove());
            if (tempBoard.getStatus() != OK) {
                unexplNode.setTerminalTrue();
            } else {
                unexplNode.backPropagateScore(DEF_SCORE, UpdateTree.playout(tempBoard, playStrategy));
            }
        }
    }

    /**
     * Method to make a part of the search tree that is of no use anymore available to be cleared by the Garbage Collector
     * by iteratively setting the parent references of all the ancestors of the passed Node to null.
     *
     * @param node node to start freeing memory from
     */
    private void freeAncestorMemory(Node node) {
        if (node.getParent() != null) {
            Node parent = node.getParent();
            node.setParent(null);
            freeAncestorMemory(parent);
        }

    }

    /**
     * Main loop of the Monte Carlo tree search. As long as the Game is running the run method is executed.
     * Each iteration it is checked if the {@link #tasks} queue contains any submitted {@link Task}'s, if  that is the case
     * {@link #peekTask()} is called to get the task at the front of the queue. In the case of {@link Task#INIT} and
     * {@link Task#MOVE_RECEIVED} the tasks will be executed by calling the necessary functions and the completed task
     * is deleted from the queue by calling {@link #removeTask()}. If the Task at the front of the queue is
     * {@link Task#MOVE_REQUESTED} it will be ignored as long the algorithm {@link #timePerMove} hasn't run out or
     * the root of the tree has a terminal
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

            if (!tasks.isEmpty()) {
                Task task = peekTask();
                switch (task) {
                    case INIT:
                        init();
                        removeTask();
                        break;
                    case MOVE_RECEIVED:
                        updateRoot(enemyMove);
                        removeTask();
                        break;
                    case MOVE_REQUESTED:
                        if (System.currentTimeMillis() > startTime + timePerMove) {
                            if (root.childCount() > 0 && !root.isTerminal()) {
                                currentBestMove = bestMove();
                                debug.send(LEVEL_2, PLAYER, "Decided on move " + currentBestMove + " in "
                                        + (System.currentTimeMillis() - startTime) + " ms.");
                                endTime = System.currentTimeMillis();
                            /* wake up the Thread that requested the Move*/
                                removeTask();
                                wakeUp();
                            }
                        } else if (root.hasTerminalChild()) {
                            currentBestMove = root.getTerminalChild().getMove();
                            board.makeMove(currentBestMove);
                            endTime = System.currentTimeMillis();
                            removeTask();
                            wakeUp();
                        }
                        break;
                }
            }

            if (board.getStatus() == OK) {
                /* ExecutorService is used to compute iterations of the selecet - expand - simulate - backpropagate
                in parallel. It's waited until all futures returned by updatePool.submit() are done working.
                */
                for (int i = 0; i < parallelizationFactor; i++) {
                    futures[i] = updatePool.submit(new UpdateTree(board.clone(), root, playStrategy, i));
                }
                boolean updating = true;
                while (updating) {
                    boolean threadWorking = false;
                    for (Future future : futures) {
                        if (future.isDone())
                            updating = false;
                        else
                            threadWorking = true;
                    }
                    if (threadWorking)
                        updating = true;
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
}
