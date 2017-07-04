package towerwarspp.player;

import towerwarspp.preset.Move;
import towerwarspp.preset.Requestable;

/**
 *
 */

class HumanPlayer extends BasePlayer {
    private Requestable moveDeliver;

    HumanPlayer(Requestable moveDeliver) {
        this.moveDeliver = moveDeliver;
    }

    @Override
    Move deliverMove() throws Exception {
        Move move;
        do {
            move = moveDeliver.deliver();
        } while (!board.moveAllowed(move, playerColor));
        return move;
    }

}
