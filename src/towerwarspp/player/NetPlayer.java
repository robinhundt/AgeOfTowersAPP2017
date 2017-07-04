package towerwarspp.player;

import towerwarspp.io.View;
import towerwarspp.preset.Move;
import towerwarspp.preset.Player;
import towerwarspp.preset.Status;

import java.rmi.RemoteException;

/**
 * Created by robin on 23.06.17.
 */
class NetPlayer extends BasePlayer {

    Player player;
    View view;

    @Override
    public Move request() throws Exception{
        Move move = super.request();
        if(view != null)
            view.visualize();
        return move;
    }

    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception {
        super.update(opponentMove, boardStatus);
        if(view != null)
            view.visualize();
    }

    @Override
    Move deliverMove() throws Exception {
        if(player == null)
            throw new Exception("NetPlayer has to be connected to a regular Player object");
        else
            return player.request();
    }

    public void connectPlayer(Player player) {
        this.player = player;
    }

    public void addView(View view) {
        this.view = view;
    }
}
