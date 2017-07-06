package towerwarspp.main;

import towerwarspp.board.Board;
import towerwarspp.io.GraphicIO;
import towerwarspp.io.IO;
import towerwarspp.io.TextIO;
import towerwarspp.io.View;
import towerwarspp.main.game.Game;
import towerwarspp.main.game.Result;
import towerwarspp.main.tournament.TResult;
import towerwarspp.main.tournament.Tournament;
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

    private final int RED = 0;
    private final int BLUE = 1;



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
                    startGame(players[RED], players[BLUE]);
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
        try {
            if(ap.getOfferedType() == PlayerType.REMOTE) {
                System.out.println("Can't offer a remote player type as remote player.");
                System.exit(1);
            } else {
                int port = ap.isSet("port") ? ap.getPort() : Remote.DEFAULT_PORT;
                Remote remote = new Remote(port);
                if(io != null)
                    remote.offer(new NetPlayer(createPlayer(ap.getOfferedType(), io)), ap.getName());
                else
                    remote.offer(new NetPlayer(createPlayer(ap.getOfferedType())), ap.getName());
            }

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

    }

    private Player getRemotePlayer() {
        Player player = null;
        try {
            Remote remote = ap.isSet("port") ? new Remote(ap.getHost(), ap.getPort()) : new Remote(ap.getHost());
            player = ap.isSet("name") ? remote.find(ap.getName()) : remote.find();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        return player;
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
        try {
            players[RED] = createPlayer(ap.getRed());
            players[BLUE] = createPlayer(ap.getBlue());
        } catch (ArgumentParserException e) {
            System.out.println(e);
            System.exit(1);
        }
        return players;
    }

    /**
     * Method createPlayer to initialize a {@link Player} with given {@link PlayerType}
     *
     * @param playerType {@link PlayerType}
     * @return {@link Player} with needed {@link PlayerType}
     */
    private Player createPlayer(PlayerType playerType, View view) {
        Player player = null;
        switch (playerType) {
            // TODO Split TextIO
            case HUMAN: player = new HumanPlayer(requestable, view); break;
            case RANDOM_AI: player = new RndPlayer(view); break;
            case REMOTE: player = getRemotePlayer(); break;
            default: System.out.println("Unsupported PlayerType."); System.exit(1);
        }
        return player;
    }

    private Player createPlayer(PlayerType playerType) {
        return createPlayer(playerType, null);
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
                    ap.isSet("delay") ? ap.getDelay() : 0, board.getSize(), ap.getGameCount(), ap.isSet("timeout") ? ap.getTimeOut() : 0);
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
                "-games \t activates tournament mode and sets number of games for the tournament \n" +
                "(beware that at every other game, the player colors are changing, so if you are starting \n" +
                "whit color red as a human, in the second round, you will be color blue, in terms of fairness) \n" +
                "-delay \t sets delay time in milliseconds to slow down the game \n" +
                "-output \t chose output type, default is textual \n" +
                "possible parameter:\n" +
                "player types: human, random, simple, adv1, remote \n" +
                "size: integer between 4 and 26 \n" +
                "output: textual, graphic, none \n" +
                "games: integer bigger than 1 \n" +
                "delay: integer bigger than 0 (in milliseconds) \n" +

                "\nFLAGS: \n" +
                "--graphic \t activates the graphic output, if not set output will be on the standard-output \n" +
                "--debug \t activates the debug-mode: shows additional information for every move \n";
    }
}