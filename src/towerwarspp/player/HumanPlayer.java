package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;
import towerwarspp.preset.Requestable;

import java.rmi.RemoteException;

/**
 * Created by robin on 23.06.17.
 */
class HumanPlayer extends BasePlayer {
    private Requestable moveDeliver;

    HumanPlayer(Requestable moveDeliver) {
        this.moveDeliver = moveDeliver;
    }

    @Override
    public Move request() throws Exception, RemoteException {
        Move move = moveDeliver.deliver();
        board.makeMove(move);
        return move;
    }
}
