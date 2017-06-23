package towerwarspp.player;

// TODO change imports and variables after board commit
import towerwarspp.board.Board;
import towerwarspp.preset.*;

/**
 * Partial omplementation of Player Interface that is used as Super-Class for specialized players (eg. Human, Random, etc.).
 */
abstract class BasePlayer implements Player {
    Board board;
    PlayerState state;
    PlayerColor color;

    /**
     * Method to validate the players boardStatus against the passed one. <b>Must always</b> be called after request and <b>before</b> confirm!
     * @param boardStatus boardStatus to validate
     * @throws Exception Throws an Exception in case the method is invodek in the wrong order.
     */
    @Override
    public void confirm(Status boardStatus) throws Exception {
        if(state != PlayerState.REQUESTED)
            throw new Exception("Illegal PlayerState. confirm can only be called after request");
        if(!board.getStatus().equals(boardStatus))
            throw new Exception("Illegal PlayerState. Confirmation unsuccessful. Non matching status of player board and passed status");
        state = PlayerState.CONFIRMED;
    }

    /**
     * Update own
     * @param opponentMove
     * @param boardStatus
     * @throws Exception
     */
    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception {
        if(state != PlayerState.CONFIRMED)
            throw new Exception("Illegal PlayerState. update can only be called after confirm");
        state = PlayerState.UPDATED;
    }

    @Override
    public void init(int size, PlayerColor color) throws Exception {
        board = new Board(size);
        this.color = color;
        state = PlayerState.INIT;
    }
}
