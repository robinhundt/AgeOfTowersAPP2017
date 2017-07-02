package towerwarspp.main;

import towerwarspp.preset.ArgumentParser;
import towerwarspp.preset.ArgumentParserException;
import towerwarspp.io.*;
import towerwarspp.player.*;
import towerwarspp.board.*;
import towerwarspp.preset.*;

import java.rmi.RemoteException;

/**
 * Class {@link AgeOfTowers} initializing a new game of TowerWarsPP with given settings <br>
 *     from the commandline using the {@link ArgumentParser}.
 *
 * @version 0.2 26th June 2017
 * @author Niklas Mueller
 */
public class AgeOfTowers {

    /*Object from class which implements requestable-interface*/
    TextIO io;

    int size = 0;
    boolean debugMode = false;
    int delayTime = 0;
    int tournamentRounds = 1;

    Board board = null;
    Viewer viewer = null;
    Player redPlayer = null;
    Player bluePlayer = null;
    Status status = Status.OK;
    Move redMove = null;
    Move blueMove = null;

    /**
     * Constructor to initialize a new game
     * @param args list with settings to put into the ArgumentParser
     */
    public AgeOfTowers(String[] args) {

        try {
            ArgumentParser ap = new ArgumentParser(args);

            size = ap.getSize();
            /*initialize board object with given size */
            board = new Board(size);

            /*initialize viewer-object with method viewer() from board*/
            viewer = board.viewer();

            io = new TextIO((BViewer) viewer);

            /*set number of rounds*/
            tournamentRounds = ap.getRounds();

            //System.out.println("network: " + ap.isNetwork());
            System.out.println("size: " + size);
            System.out.println("graphic: " + ap.isGraphic());

            /*initialize red player*/
            if (ap.getRed() == PlayerType.HUMAN) {
                System.out.println("Red chose human player");
                redPlayer = PlayerFactory.makeHumanPlayer(size, PlayerColor.RED, io);
            }
            else {
                redPlayer = PlayerFactory.makePlayer(size, PlayerColor.RED, ap.getRed());
            }

            /*initialize blue player*/
            if (ap.getBlue() == PlayerType.HUMAN) {
                System.out.println("Blue chose human player");
                bluePlayer = PlayerFactory.makeHumanPlayer(size, PlayerColor.BLUE, io);
            }
            else {
                bluePlayer = PlayerFactory.makePlayer(size, PlayerColor.BLUE, ap.getBlue());
            }

            
            /*set wether it should be debug mode or not */
            debugMode = ap.isDebug();

            /*set delay-time*/
            delayTime = ap.getDelay();

        }
        catch (ArgumentParserException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }



        /*
        initialize two player-objects with given settings from interface towerwarspp.preset.Player;
            both players are using the same object from a class implementing Requestable-Interface

          request alternately moves from both players, check validity and inform other player

          do move on board

          output current gamestatus (graphic/textual)

          tell output if move is leading to end of game

          possibility to play with a network player

         */
        int redWins = 0;
        int blueWins = 0;

        for (int i = 1; i<=tournamentRounds; i++) {
            try {
                if (i > 1) {
                    board = new Board(size);
                    bluePlayer.init(size, PlayerColor.BLUE);
                    redPlayer.init(size, PlayerColor.RED);
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }

            System.out.println("Round No.: " + i);
            if (io != null)
                io.visualize();

            Player currentPlayer = redPlayer;
            Move currentMove = null;
            PlayerColor currentColor = PlayerColor.RED;

            while (status == Status.OK) {
                System.out.println(currentColor + "'s turn: ");

                try {
                    /*request move*/
                    currentMove = currentPlayer.request();

                    if (board != null && currentMove != null) {
                        /*make move on board*/
                        status = board.update(currentMove, currentColor);

                        /*confirm move */
                        currentPlayer.confirm(board.getStatus());

                        /*change players*/
                        currentPlayer = currentPlayer == redPlayer ? bluePlayer : redPlayer;

                        /*update move*/
                        currentPlayer.update(currentMove, board.getStatus());
                    }


                    /*if debug-mode is enabled*/
                    if (debugMode && currentMove != null) {
                        System.out.println(currentColor + "'s move: " + currentMove.toString());
                        //io.visualize();
                        System.out.println("Status: " + status);
                    }

                    /*if delay-mode is enabled*/
                    if (delayTime != 0)
                        Thread.sleep(delayTime);

                } catch (RemoteException e) {
                    System.out.println(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    //System.exit(0);
                }

                currentColor = currentColor == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
            }

            if (status == Status.RED_WIN) {
                redWins++;
                System.out.println(status);
            }
            else if (status == Status.BLUE_WIN) {
                blueWins++;
                System.out.println(status);
            }
        }

        /*output statistic*/
        System.out.println("Red won " + redWins + " times!");
        System.out.println("Blue won " + blueWins + " times!");
        PlayerColor ultimateWinner = redWins > blueWins ? PlayerColor.RED : PlayerColor.BLUE;
        System.out.println("Final winner is " + ultimateWinner);
        


        /*end of game */
    }

    

    /**
     * Method getMove calling deliver from input-class to get a move from user input
     * @return move parsed from user input
     */
    public Move getMove() throws Exception {
        return io.deliver();
    }

    /**
     * Main method initializing a new object of AgeOfTowers parsing the input String
     * @param args input specifying all settings
     * @throws FileNotFoundException
     */
    public static void main(String[] args) {
        AgeOfTowers game = new AgeOfTowers(args);
    }

}