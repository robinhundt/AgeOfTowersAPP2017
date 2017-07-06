package towerwarspp.player;

import towerwarspp.io.View;
import towerwarspp.preset.Move;
import towerwarspp.preset.Player;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by robin on 23.06.17.
 */
public class NetPlayer extends UnicastRemoteObject implements Player {

    private Player player;

    public NetPlayer(Player player) throws Exception{
        if(player == null)
            throw new Exception("Cannot bind NetPlayer to null");
        else
            this.player = player;
    }


    @Override
    public Move request() throws Exception{
        Move move = player.request();
        return move;
    }

    @Override
    public void confirm(Status boardStatus) throws Exception{
        player.confirm(boardStatus);
    }

    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception {
        player.update(opponentMove, boardStatus);
    }

    @Override
    public void init(int size, PlayerColor color) throws Exception{
        player.init(size, color);
    }
}
