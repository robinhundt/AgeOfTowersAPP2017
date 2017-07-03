package towerwarspp.main;

import towerwarspp.board.BViewer;
import towerwarspp.board.Board;
import towerwarspp.io.GraphicIO;
import towerwarspp.io.IO;
import towerwarspp.io.TextIO;
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
    IO io;
    boolean debug;
    int delayTime;

    /**
     *
     * @param redPlayer
     * @param bluePlayer
     * @param outputType null is interpreted as {@link OutputType} NONE
     * @param debug
     * @param boardSize
     * @param delayTime in millisecond
     */
    Game(Player redPlayer, Player bluePlayer, OutputType outputType, boolean debug, int delayTime, int boardSize) {
        if (redPlayer == null || bluePlayer == null) {
            throw new IllegalArgumentException("Player cannot be null!");
        }
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.debug = debug;
        this.delayTime = delayTime;
        this.board = new Board(boardSize);
        switch (outputType) {
            //case GRAPHIC: io = (IO) new GraphicIO(board.viewer()); break;
            case TEXTUAL: io = (IO) new TextIO(board.viewer()); break;
        }

        try {
            this.redPlayer.init(boardSize, RED);
            this.bluePlayer.init(boardSize, BLUE);
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }


    }


    public Result play() throws RemoteException, Exception {
        Player currentPlayer = redPlayer;
        PlayerColor currentColor = RED;
        Move currentMove;
        int moveCounter = 0;

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
            currentPlayer.update(currentMove, board.getStatus());

            try {
                Thread.sleep(delayTime);
            }
            catch (InterruptedException e) {
                //TODO clarify when this exception is thrown
                e.printStackTrace();
                System.exit(1);
            }
            io.visualize();
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
