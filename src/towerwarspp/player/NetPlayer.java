package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.io.View;
import towerwarspp.preset.Move;
import towerwarspp.preset.Player;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of the {@link Player} Interface that can be used to offer any of the other {@link towerwarspp.preset.PlayerType}
 * on a local RMI. During the  constructor call a {@link Player} object has to be passed. Calling the {@link #request()},
 * {@link #confirm(Status)}, {@link #update(Move, Status)} and {@link #init(int, PlayerColor)} methods on a NetPlayer will
 * call the equivalent methods on the NetPlayers {@link Player} instance.
 * As with sub classes of {@link BasePlayer} request() - confirm() and makeMove() must be called in the correct order.
 *
 * @author Alexander Wähling
 * @version 08-07-17
 */
public class NetPlayer extends UnicastRemoteObject implements Player {
    /**
     * {@link Player} instance to which this NetPlayer is connected. All method calls on this NetPlayer will invoke the
     * corresponding calls on this Player instance. E.g. if {@link #request()} is called on a NetPlayer, the netPlayer will
     * call {@link Player#request()}.
     */
    private Player player;
    private View view;
    private boolean hasView;

    /**
     * Construct a new NetPlayer object tat is linked to the passed {@link Player} object. Player reference can not be
     * null. If null is passed an exception is thrown.
     *
     * @param player {@link Player} instance to link NetPlayer with
     * @throws Exception if null is passed
     */
    public NetPlayer(Player player) throws Exception {
        this(player, null);
    }

    public NetPlayer(Player player, View view) throws Exception {
        if (player == null)
            throw new Exception("Cannot bind NetPlayer to null");
        else
            this.player = player;
        this.view = view;
        if (this.view != null)
            hasView = true;

    }

    /**
     * Calling the request() method will invoke a {@link Player#request()} call.
     *
     * @return Move returned by {@link Player#request()}
     * @throws Exception if request() is not called after {@link #init(int, PlayerColor)} or {@link #update(Move, Status)}
     */
    @Override
    public Move request() throws Exception {
        Move move = player.request();
        if (hasView)
            view.visualize();
        return move;
    }

    /**
     * Calling confirm() method will invoke a {@link Player#confirm(Status)} call.
     *
     * @param boardStatus {@link Status} to confirm Players own status with.
     * @throws Exception Throws an Exception in case the method is invoked in the wrong order, or {@link Player}'s board
     *                   status differs from passed one, or is {@link Status#ILLEGAL}.
     */
    @Override
    public void confirm(Status boardStatus) throws Exception {
        player.confirm(boardStatus);
    }

    /**
     * Calling makeMove() will invoke a {@link Player#update(Move, Status)} call.
     *
     * @param opponentMove opponents move to place on own Board
     * @param boardStatus  opponents board status after Move to validate against own status
     * @throws Exception is thrown if method is called in wrong order, or status is ILLEGAL or different from passed status
     */
    @Override
    public void update(Move opponentMove, Status boardStatus) throws Exception {
        player.update(opponentMove, boardStatus);
        if (hasView)
            view.visualize();
    }

    /**
     * Method to initialize current NetPlayer. calling it will invoke {@link Player#init(int, PlayerColor)}.
     * A new Board is created with specified board size and color is set to passed
     * playerColor. Also if view != null it's setViewer() method is called with the board.viewer() as argument and view.visualize()
     * is called.
     * {@link Player}'s state is set depending on passed playerColor. If {@link PlayerColor#BLUE} is passed state is set to UPDATE,
     * if {@link PlayerColor#RED} is set to REQUEST.
     *
     * @param size  size of {@link Board} to play on.
     * @param color {@link PlayerColor} of player
     */
    @Override
    public void init(int size, PlayerColor color) throws Exception {
        player.init(size, color);
        if (hasView) {
            view.setViewer(((BasePlayer) player).getViewer());
            view.visualize();
        }

    }
}
