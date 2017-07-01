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

    boolean debugMode = false;
    int delayTime = 0;

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

            /*initialize board object with given size */
            board = new Board(ap.getSize());

            /*initialize viewer-object with method viewer() from board*/
            viewer = board.viewer();

            io = new TextIO((BoardViewer) viewer);

            //System.out.println("network: " + ap.isNetwork());
            System.out.println("size: " + ap.getSize());
            System.out.println("graphic: " + ap.isGraphic());

            /*initialize red player*/
            if (ap.getRed() == PlayerType.HUMAN) {
                System.out.println("Red chose human player");
                redPlayer = PlayerFactory.makeHumanPlayer(ap.getSize(), PlayerColor.RED, io);
            }
            else {
                redPlayer = PlayerFactory.makePlayer(ap.getSize(), PlayerColor.RED, ap.getRed());
            }

            /*initialize blue player*/
            if (ap.getBlue() == PlayerType.HUMAN) {
                System.out.println("Blue chose human player");
                bluePlayer = PlayerFactory.makeHumanPlayer(ap.getSize(), PlayerColor.BLUE, io);
            }
            else {
                bluePlayer = PlayerFactory.makePlayer(ap.getSize(), PlayerColor.BLUE, ap.getBlue());
            }
            
            /*set wether it should be debug mode or not */
            debugMode = ap.isDebug();

            /*set delay-time*/
            delayTime = ap.getDelay();
            //
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
        io.visualize();

        Player currentPlayer = redPlayer;
        Move currentMove = null;
        PlayerColor currentColor = PlayerColor.RED;

        while (status == Status.OK) {
            System.out.println(currentColor + "'s turn: ");
            //System.exit(0);
            try {
                /*request move*/
                if (currentPlayer != null) {
                    currentMove = currentPlayer.request();

                    /*make move on board*/
                    if (board != null)
                        status = board.update(currentMove, currentColor);

                    /*confirm move */
                    currentPlayer.confirm(board.getStatus());
                    
                    /*change players*/
                    currentPlayer = currentPlayer == redPlayer ? bluePlayer : redPlayer;

                    /*update move*/
                    if (currentMove != null) 
                        currentPlayer.update(currentMove, board.getStatus());
                }


                /*if debug-mode is enabled*/
                if (debugMode) {
                    System.out.println(currentColor + "'s move: " + currentMove.toString());
                    io.visualize();
                    System.out.println("Status: " + status);
                }

                /*if delay-mode is enabled*/
                if (delayTime != 0)
                    Thread.sleep(delayTime);
                
            }
            catch (RemoteException e) {
                System.out.println(e);
            }
            catch (Exception e) {
                System.out.println(e);
            }

            currentColor = currentColor == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
        }
        
        if (status == Status.RED_WIN || status == Status.BLUE_WIN) {
            System.out.println(status);
        }

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