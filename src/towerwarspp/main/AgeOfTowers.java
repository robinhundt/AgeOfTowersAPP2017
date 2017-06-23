package towerwarspp.main;

import towerwarspp.preset.ArgumentParser;
import towerwarspp.preset.ArgumentParserException;
import towerwarspp.io.*;
import towerwarspp.preset.Move;
import towerwarspp.preset.Status;
import towerwarspp.preset.Player;

/**
 * Class {@link AgeOfTowers} initializing a new game of TowerWarsPP with given settings <br>
 *     from the commandline using the {@link ArgumentParser}.
 *
 * @version 0.1 20th June 2017
 * @author Niklas Mueller
 */
public class AgeOfTowers {

    /**
     * Constructor to initialize a new game
     * @param args list with settings to put into the ArgumentParser
     */
    public AgeOfTowers(String[] args) {

        boolean debugMode;
        int delayTime;
        //<Board> board

        try {
            ArgumentParser ap = new ArgumentParser(args);

            //System.out.println("local: " + ap.isLocal());     //will probably be removed
            System.out.println("network: " + ap.isNetwork());
            System.out.println("size: " + ap.getSize());

            //System.out.println("graphic: " + ap.isGraphic());


            switch (ap.getRed()) {
                case HUMAN:
                    System.out.println("Red chose human player");
                    /*
                    <HumanPlayer> red = new <HumanPlayer>();
                     */
                    break;
                case RANDOM_AI:
                    System.out.println("Red chose random player");
                    /*
                    <AIPlayer> red = new <AIPlayer>();
                     */
                    break;
                case SIMPLE_AI:
                    System.out.println("Red chose simple player");
                    /*
                    <SimplePlayer red = new <SimplePlayer>();
                     */
                    break;
                case ADVANCED_AI_1:
                    System.out.println("Red chose advanced player");
                    /*
                    <AdvancedPlayer> red = new <AdvancedPlayer>();
                     */
                    break;
                case REMOTE:
                    System.out.println("Red chose remote player");
                    /*
                    <RemotePlayer> red = new <RemotePlayer>();
                    */
                    break;
            }

            switch (ap.getBlue()) {
                case HUMAN:
                    System.out.println("Blue chose human player");
                    /*
                    <HumanPlayer> blue = new <HumanPlayer>();
                     */
                    break;
                case RANDOM_AI:
                    System.out.println("Blue chose random player");
                    /*
                    <AIPlayer> blue = new <AIPlayer>();
                     */
                    break;
                case SIMPLE_AI:
                    System.out.println("Blue chose simple player");
                    /*
                    <SimplePlayer blue = new <SimplePlayer>();
                     */
                    break;
                case ADVANCED_AI_1:
                    System.out.println("Blue chose advanced player");
                    /*
                    <AdvancedPlayer> blue = new <AdvancedPlayer>();
                     */
                case REMOTE:
                    System.out.println("Blue chose remote player");
                    /*
                    <RemotePlayer> blue = new <RemotePlayer>();
                    */
                    break;
            }
            
            /*set wether it's should be debug mode or not */
            debugMode = ap.isDebug();

            /*set delay-time*/
            delayTime = ap.getDelay();
            //
        }
        catch (ArgumentParserException e) {
            e.printStackTrace();
        }

        /*Object from wich implements requestable-interface*/
        //textInput input = new textInput();

        red.init(ap.getSize(), PlayerColor.RED);
        blue.init(ap.getSize(), PlayerColor.BLUE);

        Player redPlayer = red;
        Player bluePlayer = blue;

        /*initialize board object with given size */
        //board = new <Board>(ap.getSize());

        /*initialize viewer-object with method viewer() from board*/
        //<Viewer> viewer = board.viewer();


        /*
        initialize two player-objects with given settings from interface towerwarspp.preset.Player;
            both players are using the same object from a class implementing Requestable-Interface

          request alternately moves from both players, check validity and inform other player

          do move on board

          output current gamestatus (graphic/textual)

          tell output if move is leading to end of game

          possibility to play with a network player

         */


        Status status = Status.OK;
        Move redMove = null;
        Move blueMove = null;

        while (true) {
            /*red's move*/
            try {
                /*request move*/
                redMove = redPlayer.request();

                /*confirm move */
                redPlayer.confirm(board.getStatus());

                /*update move*/
                redPlayer.update(blueMove, board.getStatus());

                if (board.getStatus() == Status.RED_WIN) {
                    status = RED_WIN;
                    break;
                }

                /*make move on board*/
                //board.makeMove(redMove);

                /*if debug-mode is enabled*/
                if (debugMode) {
                    System.out.println("Red's move: " + redMove.toString());
                    //output board
                    System.out.println("Status: " + redMove.getStatus());
                }

                /*if delay-mode is enabled*/
                if (delayTime != 0) {
                    Thread.sleep(delayTime);
                }
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            /*blue's move*/
            try {
                /*update move*/
                bluePlayer.update(redMove, board.getStatus());

                /*request move*/
                blueMove = bluePlayer.request();

                /*confirm move */
                bluePlayer.confirm(board.getStatus());

                if (board.getStatus() == Status.BLUE_WIN) {
                    status = BLUE_WIN;
                    break;
                }

                /*make move on board*/
                //board.makeMove(blueMove);
                
                /*if debug-mode is enabled*/
                if (debugMode) {
                    System.out.println("Blue's move: " + blueMove.toString());
                    //output board
                    System.out.println("Status: " + blueMove.getStatus());
                }

                /*if delay-mode is enabled*/
                if (delayTime != 0) {
                    Thread.sleep(delayTime);
                }
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*end of game */
    }

    /**
     * Method getMove calling deliver from input-class to get a move from user input
     * @return move parsed from user input
     */
    public Move getMove() {
        //return inputClass.deliver();
    }

    /**
     * Main method initializing a new object of AgeOfTowers parsing the input String
     * @param args input specifying all settings
     */
    public static void main(String[] args) {
        AgeOfTowers game = new AgeOfTowers(args);
    }

}