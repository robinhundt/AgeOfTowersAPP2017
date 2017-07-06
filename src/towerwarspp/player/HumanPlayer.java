package towerwarspp.player;

import towerwarspp.io.View;
import towerwarspp.preset.Move;
import towerwarspp.preset.Requestable;

/**
 *
 */

public class HumanPlayer extends BasePlayer {
    private Requestable moveDeliver;

    public HumanPlayer(Requestable moveDeliver, View view) {
        this.moveDeliver = moveDeliver;
        this.view = view;
    }

    public HumanPlayer(Requestable moveDeliver) {
        this(moveDeliver, null);
    }

    @Override
    Move deliverMove() throws Exception {
        Move move;
        do {
            move = moveDeliver.deliver();
        } while (!board.moveAllowed(move, color));
        return move;
    }

}
