package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.*;

/**
 * Partial implementation of Player Interface that is used as Super-Class for specialized players (eg. Human, Random, etc.).
 */
abstract class BasePlayer implements Player {
    Board board;
    /**
     * State represents the point in the request - confirm - update cycle of the Player
     */
    PlayerState state;
    PlayerColor color;

    /**
     * Method to validate the players boardStatus against the passed one. <b>Must always</b> be called after request and <b>before</b> confirm!
     * @param boardStatus boardStatus to validate
     * @throws Exception Throws an Exception in case the method is invodek in the wrong order.
     */
    @Override
    public void confirm(Status boardStatus) throws Exception {
        if(state != PlayerState.CONFIRM)
            throw new Exception("Illegal PlayerState. confirm can only be called after request");
        if(!board.getStatus().equals(boardStatus))
            throw new Exception("Illegal PlayerState. Confirmation unsuccessful. Non matching status of player board and passed status");
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
        board.update(opponentMove, color == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE);
        if(!board.getStatus().equals(boardStatus))
            throw new Exception("Illegal PlayerState. Confirmation unsuccessful. Non matching status of player board and passed status");
        state = state.next();
    }

    @Override
    public void init(int size, PlayerColor color) throws Exception {
        board = new Board(size);
        this.color = color;
        if(color == PlayerColor.RED)
            state = PlayerState.REQUEST;
        else if(color == PlayerColor.BLUE)
            state = PlayerState.UPDATE;
    }

}
