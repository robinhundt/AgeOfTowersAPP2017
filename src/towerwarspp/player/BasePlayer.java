package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.main.Debug;
import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.debug.DebugSource;
import towerwarspp.preset.*;

import static towerwarspp.preset.Status.*;
import static towerwarspp.preset.PlayerColor.*;

/**
 * Partial implementation of Player Interface that is used as Super-Class for specialized players (eg. Human, Random, etc.).
 * The request(), confirm(), makeMove() and init() method are implemented and thus are the same for all subclasses. By using
 * the enum PlayerState it is guaranteed that the methods are always called in the correct order. All players have their own Board
 * instance on which they place their own and their opponent moves. If the State of the board is ILLEGAL or does'nt match
 * with the passed States in confirm() and makeMove() an exception is thrown.
 *
 * @author Robin Hundt
 * @version 07-07-17
 */
public abstract class BasePlayer implements Player {
    /**
     * Board instance on which the player places all his own and opponent moves. Is used to guarantee
     * that no moves are played that are considered Illegal by ones own Board implementation (necessary for remote play)
     */
    protected Board board;
    /**
     * State that represents the point in the request - confirm - makeMove cycle of the Player
     */
    private PlayerState state;
    /**
     * Color of this Player instance
     */
    protected PlayerColor color;

    private Debug debug = Debug.getInstance();

    /**
     * Only abstract method. Is called inside the request method. Subclasses of BasePlayer should put their logic into
     * their implementation of deliverMove(). This way it is guaranteed that all Player classes share the same
     * request - confirm - makeMove - init logic and their only difference is the specific Move returned by request()
     * depending on their type.
     * @return Move that the Player intends to make
     * @throws Exception
     */
    abstract Move deliverMove() throws Exception;

    /**
     * Returns the current {@link PlayerColor}
     * @return the color of the Player
     */
    public PlayerColor getColor() {
        return color;
    }

    Viewer getViewer() {
        return board.viewer();
    }

    /**
     * sets the {@link Board} of the Player to given Board and updates the {@link PlayerState}.
     * used for loading and replaying games
     * @param board the new board
     */
    public void setBoard(Board board) {
        this.board = board;
        this.state = board.getTurn() == color ? PlayerState.REQUEST : PlayerState.UPDATE;
    }


    /**
     * Request method as specified in the {@link Player} Interface. Can only be called after {@link #init(int, PlayerColor)} or
     * {@link #update(Move, Status)}. Makes a call to {@link #deliverMove()}, places the move on own {@link #board} and
     * optionally calls view.visualize() if view != null. Also sets {@link #state} to the next state by calling {@link PlayerState#next()}.
     * @return the Move the player wishes to make
     * @throws Exception if the method is called in an incorrect order
     */
    @Override
    public Move request() throws Exception {
        if(state != PlayerState.REQUEST)
            throw new Exception("Illegal PlayerState. Request can only be called after after init or makeMove");
        state = state.next();
        Move move = deliverMove();
        debug.send(DebugLevel.LEVEL_1, DebugSource.PLAYER, "BasePlayer: " + color + " playing move " + move);
        board.makeMove(move);
        return move;
    }

    /**
     * Method to validate the players boardStatus against the passed one. <b>Must always</b> be called after request and <b>before</b> confirm!
     * @param boardStatus boardStatus to validate
     * @throws Exception Throws an Exception in case the method is invoked in the wrong order, or own {@link Board#status}
     * diifers from passed one, or is {@link Status#ILLEGAL}.
     */


    @Override
    public void confirm(Status boardStatus) throws Exception {
        if(state != PlayerState.CONFIRM)
            throw new Exception("Illegal PlayerState. confirm can only be called after request");
        if(!board.getStatus().equals(boardStatus) || board.getStatus() == ILLEGAL)
            throw new Exception("Board staus:" + board.getStatus() + " Confirmation unsuccessful. Illegal or non matching status of player board and passed status");
        state = state.next();
    }

    /**
     * Update own board with opponentMove  and check if opponents board status is equal to own board status. If status are
     * unequal of own board status is ILLEGAL an exception is thrown.
     * @param opponentMove opponent move to place on own board
     * @param boardStatus opponent board status after move
     * @throws Exception is thrown if method is called in wrong order, or status is ILLEGAL or different from passed status
     */
    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception {
        if(state != PlayerState.UPDATE)
            throw new Exception("Illegal PlayerState. makeMove can only be called after confirm or at first if Player is Blue");

        board.makeMove(opponentMove);

        if(!board.getStatus().equals(boardStatus) || board.getStatus() == ILLEGAL) {
            throw new Exception("Illegal PlayerState. Confirmation unsuccessful. Illegal or non matching status of player board and passed status");
        }
        state = state.next();
    }


    /**
     * Method to initialize current Player. A new Board is created with specified board size and color is set to passed
     * color. Also if view != null it's setViewer() method is called with the board.viewer() as argument and view.visualize()
     * is called.
     * {@link #state} is set depending on passed color. If {@link PlayerColor#BLUE} is passed state is set to UPDATE,
     * if {@link PlayerColor#RED} is set to REQUEST.
     * @param size size of {@link Board} to play on.
     * @param color {@link PlayerColor} of player
     */
    @Override
    public void init(int size, PlayerColor color) {
        board = new Board(size);
        this.color = color;
        if(color == RED)
            state = PlayerState.REQUEST;
        else if(color == BLUE)
            state = PlayerState.UPDATE;
    }
}
