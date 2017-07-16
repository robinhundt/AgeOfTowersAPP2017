package towerwarspp.player;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Requestable;

/**
 * Class that implements a Human Player. Is a subclass of BasePlayer. The only difference is the implementation of
 * deliverMove(), which uses a Requestable object passed at Object construction to get user input in the form of move
 * objects. If the move is not allowed on the players board, requestable.deliver() will be called again.
 * @author Alexander Wähling
 * @version 08-07-17
 */

public class HumanPlayer extends BasePlayer {
    /**
     * Object implementing the {@link Requestable} Interface that is used to get Human input via the {@link Requestable#deliver()}
     * method.
     */
    private Requestable requestable;

    /**
     * Constructor for a HumanPlayer with the specified {@link Requestable} object.
     * @param requestable
     */
    public HumanPlayer(Requestable requestable) {
        this.requestable = requestable;
    }



    /**
     * deliverMove() is called in super class {@link BasePlayer} during {@link BasePlayer#request()}. deliverMove()
     * will call deliver() on instance {@link #requestable} object as long as no valid move per {@link towerwarspp.board.Board#moveAllowed(Move, PlayerColor)}
     * is returned.
     * @return move validated by {@link towerwarspp.board.Board#moveAllowed(Move, PlayerColor)}.
     * @throws Exception in case {@link Requestable#deliver()} causes any Exceptio, the Exceptions message is wrapped into a normal
     * Exception and passed up.
     */
    @Override
    Move deliverMove() throws Exception{
        Move move;
        try {
            do {
                move = requestable.deliver();
            } while (!board.moveAllowed(move, color));
            return move;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

}
