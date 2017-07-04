package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.*;

import static towerwarspp.preset.Status.*;
import static towerwarspp.preset.PlayerColor.*;

import java.rmi.RemoteException;

/**
 * Partial implementation of Player Interface that is used as Super-Class for specialized players (eg. Human, Random, etc.).
 */
abstract class BasePlayer implements Player {
    Board board;
    /**
     * State represents the point in the request - confirm - update cycle of the Player
     */
    private PlayerState state;
    PlayerColor playerColor;

    abstract Move deliverMove() throws Exception;

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    @Override
    public Move request() throws Exception, RemoteException {
        if(state != PlayerState.REQUEST)
            throw new Exception("Illegal PlayerState. Request can only be called after after init or update");
        state = state.next();
        Move move = deliverMove();
        board.update(move, playerColor);
        return move;
    }

    /**
     * Method to validate the players boardStatus against the passed one. <b>Must always</b> be called after request and <b>before</b> confirm!
     * @param boardStatus boardStatus to validate
     * @throws Exception Throws an Exception in case the method is invodek in the wrong order.
     */


    @Override
    public void confirm(Status boardStatus) throws Exception {
        if(state != PlayerState.CONFIRM)
            throw new Exception("Illegal PlayerState. confirm can only be called after request");
        if(!board.getStatus().equals(boardStatus) || board.getStatus() == ILLEGAL)
            throw new Exception("Illegal PlayerState. Confirmation unsuccessful. Illegal or non matching status of player board and passed status");
        state = state.next();
    }

    /**
     * Update own
     * @param opponentMove
     * @param boardStatus
     * @throws Exception
     */
    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception {
        if(state != PlayerState.UPDATE)
            throw new Exception("Illegal PlayerState. update can only be called after confirm or at first if Player is Blue");
        board.update(opponentMove, playerColor == BLUE ? RED : BLUE);
        if(!board.getStatus().equals(boardStatus) || board.getStatus() == ILLEGAL)
            throw new Exception("Illegal PlayerState. Confirmation unsuccessful. Illegal or non matching status of player board and passed status");
        state = state.next();
    }

    @Override
    public void init(int size, PlayerColor playerColor) throws Exception {
        board = new Board(size);
        this.playerColor = playerColor;
        if(playerColor == RED)
            state = PlayerState.REQUEST;
        else if(playerColor == BLUE)
            state = PlayerState.UPDATE;
    }

}
