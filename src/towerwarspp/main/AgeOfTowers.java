package towerwarspp.main;

import towerwarspp.board.BViewer;
import towerwarspp.board.Board;
import towerwarspp.io.GraphicIO;
import towerwarspp.io.IO;
import towerwarspp.io.TextIO;
import towerwarspp.io.View;
import towerwarspp.network.Remote;
import towerwarspp.player.HumanPlayer;
import towerwarspp.player.NetPlayer;
import towerwarspp.player.RndPlayer;
import towerwarspp.preset.*;

import static towerwarspp.preset.PlayerColor.*;

/**
 * Class AgeOfTower - main class to start a new game of TowerWarsPP
 *
 * @author Niklas Mueller
 * @version 07-05-2017
 */
public class AgeOfTowers {
    private ArgumentParser ap;
    private Player[] players;
    private Board board;
    private IO io;
    private Requestable requestable;


    private int gameTimeOut = 0;

    /**
     * Constructor
     * @param args command line parameters, providing settings for the game
     */
    private AgeOfTowers(String[] args) {
        try {
            ap = new ArgumentParser(args);

            /*check if no parameters have been given, or flag --help is activated*/
            if (args.length == 0 || ap.isHelp()) {
                System.out.println(helpOutput());
                System.exit(0);
            }

            /*check outputType*/
            if(ap.isSet("output")) {
                setUpIO(ap.getOutputType());
            } else {
                setUpIO(OutputType.TEXTUAL);
            }

            if(ap.isSet("timeout")) {
                gameTimeOut = ap.getTimeOut();
            }

            /*check with way of game needs to be started, network or local*/
            if(ap.isSet("offer")) {
                findRemotePlay();
            } else if(ap.isSet("blue") && ap.isSet("red") && ap.isSet("size")) {
                /*check if board size is valid*/
                if(ap.getSize() >= 4 && ap.getSize() <= 26) {
                    initBoard(ap.getSize());
                } else {
                    System.out.println("-size of Board must be between 4 and 26");
                    System.exit(1);
                }
                players = createPlayers();
                /*check if tournament mode is enabled*/
                if(ap.isSet("games") && ap.getGameCount() > 1) {
                    startTournament(players);
                } else {
                    startGame(players[0], players[1]);
                }

            } else {
                System.out.println("Invalid combination of Arguments. See following --help");
                System.out.println(helpOutput());
                System.exit(1);
            }

        } catch (ArgumentParserException e) {
            System.out.println(e.getMessage());
            System.out.println(helpOutput());
            System.exit(1);
        }
    }

    /**
     * Method initBoard to initialized the board variable with a new {@link Board}
     * @param boardSize
     */
    private void initBoard(int boardSize) {
        board = new Board(boardSize);
    }


    private void findRemotePlay() {

    }

    private Player getRemotePlayer() {
        //TODO find remote player depending on arguments
        return null;
    }

    private void setUpIO(OutputType outputType) {
        switch (outputType) {
            case NONE: requestable = new TextIO(); break;
            case TEXTUAL:
                io = new TextIO();
                requestable = io; break;
            case GRAPHIC:
                io = new GraphicIO();
                requestable = io; break;
        }
    }

    /**
     * Method createPlayers to get an array of two {@link Player}, first one of {@link PlayerColor} RED, second BLUE
     *
     * @return array of {@link Player} with {@link PlayerColor} RED and BLUE
     */
    private Player[] createPlayers() {
        Player[] players = new Player[2];
        players[0] = createPlayer(RED);
        players[1] = createPlayer(BLUE);
        return players;
    }

    /**
     * Method createPlayer to initialize a {@link Player} with given {@link PlayerType}
     *
     * @param playerColor {@link PlayerColor}
     * @return {@link Player} with needed {@link PlayerType}
     */
    private Player createPlayer(PlayerColor playerColor) {
        try {
            switch (playerColor == RED ? ap.getRed() : ap.getBlue()) {
                // TODO Split TextIO
                case HUMAN: return new HumanPlayer(requestable);
                case RANDOM_AI: return new RndPlayer();
                case REMOTE: return getRemotePlayer();
                default: System.out.println("Unsupported PlayerType."); System.exit(1);
            }

        } catch (ArgumentParserException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // TODO change this, looks hacky
        return null;
    }

    /**
     * Method startGame to create a new {@link Game} object, with given {@link Player}s.
     * Outputs information about this {@link Game}
     *
     * @param redPlayer {@link Player} set as red player
     * @param bluePlayer {@link Player} set as blue player
     */
    private void startGame(Player redPlayer, Player bluePlayer) {
        Result result = null;
        try {
            Game game = new Game(redPlayer, bluePlayer, ap.getSize(), io, ap.isDebug(),
                    ap.isSet("delay") ? ap.getDelay() : 0);
            result = game.play(ap.isSet("timeout") ? ap.getTimeOut() : 0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println(result);
    }

    /**
     * Method startTournament to start a tournament starting as many {@link Game}s as wished.
     * Outputs statistic about this {@link Tournament}.
     *
     * @param players array of {@link Player}
     */
    private void startTournament(Player[] players) {
        TResult tResult = new TResult();
        Tournament tournament = null;
        try {

            tournament = new Tournament(players, io, ap.isDebug(),
                    ap.isSet("delay") ? ap.getDelay() : 0, board.getSize(), ap.getGameCount(), gameTimeOut);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        tResult = tournament.play();
        System.out.println(tResult);
    }



    public static void main(String[] args) {

        new AgeOfTowers(args);
//
//
//
//
//        Player firstPlayer, secondPlayer;
//        Requestable requestable;
//        View view;
//        IO io;
//        OutputType outputType = OutputType.TEXTUAL;
//        try {
//            /*create new ArgumentParse object*/
//            ArgumentParser ap = new ArgumentParser(args);
//
//            /*check if no parameters have been given, or flag --help is activated*/
//            if (args.length == 0 || ap.isHelp()) {
//                System.out.println(helpOutput());
//                System.exit(0);
//            }
//
//            /*check if board size is valid*/
//            if (ap.getSize() < 4 || ap.getSize() > 26) {
//                System.out.println("Board size needs to be between 4 and 26!");
//                System.exit(1);
//            }
//
//            /*create new board and viewer object*/
//            Board board = new Board(ap.getSize());
//            BViewer viewer = board.viewer();
//
//            // Determine OutputType of this instance
//            if(ap.isSet("output"))
//                outputType = ap.getOutputType();
//
//            /*check if graphic output should be enabled*/
//            if (outputType == OutputType.GRAPHIC) {
//                // TODO change to graphicIO once completed
//                io = new TextIO(viewer);
//                requestable = io;
//                view = io;
//            }
//            /*otherwise do output on standard output*/
//            else if (outputType == OutputType.TEXTUAL){
//                io = new TextIO(viewer);
//                requestable = io;
//                view = io;
//            } else {
//                requestable =  new TextIO();
//            }
//
//            /*check if red and blue player types are given, then initialize them*/
//            if (ap.isSet("red") && ap.getRed() != PlayerType.REMOTE) {
//                firstPlayer = makePlayer(ap.getRed(), requestable);
//            }
//            else {
//                firstPlayer = null;
//            }
//            if (ap.isSet("blue") && ap.getBlue() != PlayerType.REMOTE) {
//                secondPlayer = makePlayer(ap.getBlue(), requestable);
//            }
//            else {
//                secondPlayer = null;
//            }
//
//            if(ap.isSet("blue") && ap.getBlue() == PlayerType.REMOTE) {
//                System.out.println("Looking for player " + ap.getName());
//                secondPlayer = Remote.find(ap.getHost(), ap.getName());
//            }
//
//            /*check if tournament mode is enabled*/
//
//            if(ap.isSet("offer")) {
//                Player player = makePlayer(ap.getOfferedType(), requestable);
//                player = new NetPlayer(player);
//                Remote.offer(player, ap.getName());
//            } else {
//
//                Game game = new Game(firstPlayer, secondPlayer, board, outputType, true, 0);
//                game.play();
//            }
//
//
////            if (ap.isSet("rounds")) {
////                /*create tournament object*/
////            }
////            else {
////                /*create a new game object with the given players and settings*/
////                Game game = new Game(firstPlayer, secondPlayer, OutputType.TEXTUAL, true,
////                        ap.isSet("delay") ? ap.getDelay() : 0, ap.getSize());
////
////                /*output game result*/
////                System.out.println(game.play().toString());
////            }
//        }
//        catch (ArgumentParserException e) {
//            System.out.println(e);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /**
     * Method helpOutput to get a {@link String} containing information about how to start a {@link AgeOfTowers} game
     * @return String containing information
     */
    private static String helpOutput() {
        return  "Starts a new game of TowerWarsPP with the given settings. \n" +
                "USAGE: \n --help \tshows this help message \n" +
                "Settings start with '-' and need a parameter and can be set as followed. \n" +
                "Flags start with '--' and do not need parameter. All flags are optional.\n" +

                "\nSETTINGS: \n" +
                "-offer \t chose player type, none other settings need to be set \n" +

                "if -offer is NOT set, obligatory settings are: \n" +
                "-red \t chose player type for red \n" +
                "-blue \t chose player type for blue \n" +
                "-size \t chose board size \n" +

                "optional settings are:\n" +
                "---- only together and if network game is wanted ---- \n" +
                "-host \n" +
                "-name \n" +
                "-port \n" +
                "------------ \n" +
                "-rounds \t activates tournament mode and sets number of rounds for the tournament \n" +
                "(beware that at every other game, the player colors are changing, so if you are starting \n" +
                "whit color red as a human, in the second round, you will be color blue, in terms of fairness) \n" +
                "-delay \t sets delay time in milliseconds to slow down the game \n" +
                "-output \t chose output type, default is textual \n" +
                "possible parameter:\n" +
                "player types: human, random, simple, adv1, remote \n" +
                "size: integer between 4 and 26 \n" +
                "output: textual, graphic, none \n" +
                "rounds: integer bigger than 1 \n" +
                "delay: integer bigger than 0 (in milliseconds) \n" +

                "\nFLAGS: \n" +
                "--graphic \t activates the graphic output, if not set output will be on the standard-output \n" +
                "--debug \t activates the debug-mode: shows additional information for every move \n";
    }
}