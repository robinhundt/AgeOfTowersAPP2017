package towerwarspp.player;

import towerwarspp.preset.Move;
import towerwarspp.preset.Requestable;

import java.rmi.RemoteException;

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
        Move move = moveDeliver.deliver();
        board.update(move, color);
        return move;
    }
}
