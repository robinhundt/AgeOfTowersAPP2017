package towerwarspp.player;

// TODO change imports and variables after board commit
import towerwarspp.board.Board;
import towerwarspp.preset.*;

/**
 * Implementation of Player Interface that represents a human player.
 */
public class BasePlayer implements Player {
    private Requestable move;
    private Board board;
    private PlayerState state;

    private BasePlayer(Requestable move) {
        this.move = move;
    }

    /*
    * request a move from move object (the human player)
    * */
    @Override
    public Move request() throws Exception {
        if(state != PlayerState.INIT && state != PlayerState.UPDATED)
            throw new Exception("Illegal PlayerState. request can only be called after init or update.");
        state = PlayerState.REQUESTED;
        return move.deliver();
    }

    @Override
    public void confirm(Status boardStatus) throws Exception {
        if(state != PlayerState.REQUESTED)
            throw new Exception("Illegal PlayerState. confirm can only be called after request");
        if(!board.getStatus().equals(boardStatus))
            throw new Exception("Illegal PlayerState. Confirmation unsuccessful. Non matching status of player board and passed status");
        state = PlayerState.CONFIRMED;
    }

    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception {
        if(state != PlayerState.CONFIRMED)
            throw new Exception("Illegal PlayerState. update can only be called after confirm");
        state = PlayerState.UPDATED;
    }

    @Override
    public void init(int size, PlayerColor color) throws Exception {
        board = new Board(size);
        state = PlayerState.INIT;
    }
}
