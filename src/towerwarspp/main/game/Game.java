package towerwarspp.main.game;

import towerwarspp.board.Board;
import towerwarspp.io.GraphicIO;
import towerwarspp.io.TextIO;
import towerwarspp.io.View;
import towerwarspp.main.WinType;
import towerwarspp.preset.*;

import java.rmi.RemoteException;

import static towerwarspp.preset.Status.*;
import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;

/**
 * Class {@link Game} doing management of players and output.
 *
 * @author Niklas Mueller
 * @version 1.5 July 03rd 2017
 */
public class Game {
    /**
     * {@link Player} with {@link PlayerColor} RED
     */
    Player redPlayer;
    /**
     * {@link Player} with {@link PlayerColor} BLUE
     */
    Player bluePlayer;
    /**
     * {@link Board} to play with
     */
    Board board;
    /**
     * {@link View} object to visualize the {@link Board} and {@link Game}
     */
    View view;
    /**
     * boolean debug activating debug-mode if true
     */
    boolean debug;
    /**
     * boolean hasView showing if {@link View} object is given
     */
    boolean hasView;
    /**
     * integer delayTime, time to wait after every turn
     */
    int delayTime;

    /**
     *Constructor setting {@link Player}s, {@link Board}, {@link View} and integer variables
     *
     * @param redPlayer {@link Player} with {@link PlayerColor} RED
     * @param bluePlayer {@link Player} with {@link PlayerColor} BLUE
     * @param boardSize integer size of {@link Board}
     * @param view {@link View} object providing possibility for visualization
     * @param debug integer debug activating debug mode if true
     * @param delayTime time to wait after every turn (in millisecond)
     */
    public Game(Player redPlayer, Player bluePlayer, int boardSize, View view, boolean debug, int delayTime) {
        /*if one of the players is null*/
        if (redPlayer == null || bluePlayer == null) {
            throw new IllegalArgumentException("Player cannot be null!");
        }
        /*set players , debug-mode, delay time and viewer*/
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.debug = debug;
        this.delayTime = delayTime;
        this.view = view;

        /*create new board and include viewer object in view*/
        board = new Board(boardSize);
        if(view != null) {
            view.setViewer(board.viewer());
            hasView = true;
        }

        /*try to initialized players*/
        try {
            this.redPlayer.init(board.getSize(), RED);
            this.bluePlayer.init(board.getSize(), BLUE);
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }


    }


    /**
     * Method play to start the {@link Game}, means requesting {@link Move}s from {@link Player}s, updating the {@link Board}
     * and visualizing the whole game
     *
     * @param timeOut integer timeOut, maximum number of {@link Move}s, after which the {@link Game} will be stopped
     * @return {@link Result} containing information about the winner of this {@link Game}
     * @throws RemoteException if an error occurs with the connection in a network game
     * @throws Exception
     */
    public Result play(int timeOut) throws RemoteException, Exception {
        /*set redPlayer as first player, red as first color, and counter of move*/
        Player currentPlayer = redPlayer;
        PlayerColor currentColor = RED;
        Move currentMove;
        int moveCounter = 0;

        /*if a view object exists, so visualization is wanted, do this*/
        if(hasView) {
            view.visualize();
        }

        /*as long as a valid move has been made and none of the players did not win, ask for next moves*/
        while (board.getStatus() == OK) {
            /*increment move count*/
            moveCounter++;
            /*output turn*/
            view.display(currentColor + "'s turn");

            /*get a move from current player*/
            currentMove = currentPlayer.request();

            /*make move on board*/
            board.update(currentMove, currentColor);

            /*if debug mode is enabled output information*/
            if (debug && currentMove != null) {
                view.display(currentColor + "'move :" + currentMove);
                view.display("Status: " + board.getStatus());
            }

            /*check if boardstatus of player is equal to own boardstatus*/
            currentPlayer.confirm(board.getStatus());
            /*switch current player and playercolor*/
            currentPlayer = currentPlayer == redPlayer ? bluePlayer : redPlayer;
            currentColor = currentColor == RED ? BLUE : RED;

            /*inform player about last move of opponent player*/
            currentPlayer.update(currentMove, board.getStatus());

            /*if delay time is set, wait*/
            try {
                Thread.sleep(delayTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*if view object has been added, visualized game*/
            if(hasView) {
                view.visualize();
            }
        }

        /*create result object with playercolor, number of moves and type of win*/
        if (board.getStatus() == RED_WIN) {
            return new Result(RED, winnerMoves(moveCounter), board.getWinType());
        }
        else if (board.getStatus() == BLUE_WIN) {
            return new Result(BLUE, winnerMoves(moveCounter), board.getWinType());
        }
        else {
            return new Result(currentColor, winnerMoves(moveCounter), board.getWinType());
        }

    }

    /**
     * Method winnerMoves to calculate number of moves of the winner
     *
     * @param combinedMoveCounter total number of move from both player
     * @return integer number of moves winner needed to win
     */
    private int winnerMoves(int combinedMoveCounter) {
        return (int) (Math.ceil((double) combinedMoveCounter/2.0));
    }
}
