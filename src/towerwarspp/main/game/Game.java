package towerwarspp.main.game;

import towerwarspp.board.Board;
import towerwarspp.io.View;
import towerwarspp.main.WinType;
import towerwarspp.preset.*;

import java.rmi.RemoteException;

import static towerwarspp.preset.Status.*;
import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;

/**
 * Created by niklas on 03.07.17.
 */

public class Game {
    Player redPlayer;
    Player bluePlayer;
    Board board;
    View view;
    boolean debug;
    boolean hasView;
    int delayTime;

    /**
     *
     * @param redPlayer
     * @param bluePlayer
     * @param boardSize
     * @param view
     * @param debug
     * @param delayTime in millisecond
     */
    public Game(Player redPlayer, Player bluePlayer, int boardSize, View view, boolean debug, int delayTime) {
        if (redPlayer == null || bluePlayer == null) {
            throw new IllegalArgumentException("Player cannot be null!");
        }
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.debug = debug;
        this.delayTime = delayTime;
        this.view = view;

        board = new Board(boardSize);
        if(view != null) {
            view.setViewer(board.viewer());
            hasView = true;
        }

        try {
            this.redPlayer.init(board.getSize(), RED);
            this.bluePlayer.init(board.getSize(), BLUE);
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }


    }


    public Result play(int timeOut) throws RemoteException, Exception {
        Player currentPlayer = redPlayer;
        PlayerColor currentColor = RED;
        Move currentMove;
        int moveCounter = 0;

        if(hasView) {
            view.visualize();
        }

        while (board.getStatus() == OK) {
            moveCounter++;
            System.out.println(currentColor + "'s turn");

            currentMove = currentPlayer.request();

            board.update(currentMove, currentColor);

            if (debug) {
                System.out.println(currentColor + "'move :" + currentMove.toString());
                System.out.println("Status: " + board.getStatus());
            }

            currentPlayer.confirm(board.getStatus());
            currentPlayer = currentPlayer == redPlayer ? bluePlayer : redPlayer;
            currentColor = currentColor == RED ? BLUE : RED;

            if(hasView) {
                view.visualize();
            }

            currentPlayer.update(currentMove, board.getStatus());

            try {
                Thread.sleep(delayTime);
            }
            catch (InterruptedException e) {
                //TODO clarify when this exception is thrown
                e.printStackTrace();
                System.exit(1);
            }

            if(hasView) {
                view.visualize();
            }
        }

        if (board.getStatus() == RED_WIN) {
            return new Result(RED, winnerMoves(moveCounter), WinType.BASE_DESTROYED); //TODO WinType
        }
        else if (board.getStatus() == BLUE_WIN) {
            return new Result(BLUE, winnerMoves(moveCounter), WinType.BASE_DESTROYED); //TODO WinType
        }
        else {
            return new Result(currentColor, winnerMoves(moveCounter), WinType.ILLEGAL_MOVE);
        }

    }

    private int winnerMoves(int combinedMoveCounter) {
        return (int) (Math.ceil((double) combinedMoveCounter/2.0));
    }
}
