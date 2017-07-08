package towerwarspp.player;

import towerwarspp.io.View;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Requestable;

/**
 * Class that implements a Human Player. Is a subclass of BasePlayer. The only difference is the implementation of
 * deliverMove(), which uses a Requestable object passed at Object construction to get user input in the form of move
 * objects. If the move is not allowed on the players board, requestable.deliver() will be called again.
 * @author Robin Hundt
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
     * Constructor for the HumanPlayer with the {@link #requestable} instance and an optional {@link View} object, that can be
     * used to display the current state of the game (required if the Player is offered on the network but the user wishes
     * to see output).
     * @param requestable {@link Requestable} object to get user input from
     * @param view {@link View} object to update output of the Game after each request and update
     */
    public HumanPlayer(Requestable requestable, View view) {
        this(requestable);
        setView(view);
    }


    /**
     * deliverMove() is called in super class {@link BasePlayer} during {@link BasePlayer#request()}. deliverMove()
     * will call deliver() on instance {@link #requestable} object as long as no valid move per {@link towerwarspp.board.Board#moveAllowed(Move, PlayerColor)}
     * is returned.
     * @return move validated by {@link towerwarspp.board.Board#moveAllowed(Move, PlayerColor)}.
     * @throws Exception
     */
    @Override
    Move deliverMove() throws Exception {
        Move move;
        do {
            move = requestable.deliver();
        } while (!board.moveAllowed(move, color));
        return move;
    }

}
