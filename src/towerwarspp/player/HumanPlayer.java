package towerwarspp.player;

import towerwarspp.preset.Move;
import towerwarspp.preset.Requestable;

/**
 *
 */

public class HumanPlayer extends BasePlayer {
    private Requestable moveDeliver;

    public HumanPlayer(Requestable moveDeliver) {
        this.moveDeliver = moveDeliver;
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
