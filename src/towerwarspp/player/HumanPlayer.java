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
    public Move request() throws Exception, RemoteException {
        if(state != PlayerState.REQUEST)
            throw new Exception("Illegal PlayerState. Request can only be called after after init or update");
        Move move = moveDeliver.deliver();
        board.update(move, color);
        state = state.next();
        return move;
    }
}
