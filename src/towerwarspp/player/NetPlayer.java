package towerwarspp.player;

import towerwarspp.preset.Move;
import towerwarspp.preset.Player;

import java.rmi.RemoteException;

/**
 * Created by robin on 23.06.17.
 */
class NetPlayer extends BasePlayer {

    Player player;

    @Override
    Move deliverMove() throws Exception {
        if(player == null)
            throw new Exception("NetPlayer has to be connected to a regular Player object");
        else
            return player.request();
    }

    public void connectPlayer(Player player) throws Exception {
        this.player = player;
    }
}
