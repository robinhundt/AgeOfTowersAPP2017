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
import towerwarspp.player.SimplePlayer;
import towerwarspp.preset.*;

import static towerwarspp.preset.PlayerColor.*;

/**
 * Class AgeOfTower - main class to start a new game of TowerWarsPP.
 *
 * @author Niklas Mueller
 * @version 1.2 July 05th 2017
 */
public class AgeOfTowers {
    /**
     * Defaullt {@link OutputType} that is used if nothing else is specified.
     */
    private final OutputType DEF_OUTPUT = OutputType.GRAPHIC;

    /**
     * {@link ArgumentParser} to get settings and flags from the command line
     */
    private ArgumentParser ap;
    /**
     * Array of {@link Player}s
     */
    private Player[] players;
    /**
     * {@link Board} object
     */
    private Board board;
    /**
     * {@link IO} object to get possibility to request {@link Move}s from the {@link Player} and to visualize the {@link Board}
     */
    private IO io;
    /**
     * {@link Requestable} object to  get possibility to request {@link Move}s from the {@link Player} if no visualization is wanted
     */
    private Requestable requestable;

    /**
     * final integer RED with {@value}
     */
    private final int RED = 0;
    /**
     * final integer BLUE with {@value}
     */
    private final int BLUE = 1;



    /**
     * Constructor parsing the given array of strings to the {@link ArgumentParser} and starting the specified type of game
     *
     * @param args command line parameters, providing settings for the game
     */
    private AgeOfTowers(String[] args) {
        try {
            /*create new ArgumentParser to get access to settings and flags*/
            ap = new ArgumentParser(args);

            /*check if no parameters have been given, or flag --help is activated*/
            if (args.length == 0 || ap.isHelp()) {
                System.out.println(helpOutput());
                System.exit(0);
            }

            /*check outputType, default is textual output*/
            if(ap.isSet("output")) {
                setUpIO(ap.getOutputType());
            } else {
                setUpIO(DEF_OUTPUT);
            }

            /*check which way of game needs to be started: network (hosting or offering) or local*/
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
                /*create players with given PlayerTypes*/
                players = createPlayers();
                /*check if tournament mode is enabled and game number is higher than 1, otherwise start just one game*/
                if(ap.isSet("games") && ap.getGameCount() > 1) {
                    startTournament(players);
                } else {
                    startGame(players[RED], players[BLUE]);
                }
            /*if no correct combination of settings has been provided, print help and exit*/
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


    /**
     * Method findRemotePlayer to offer a {@link NetPlayer} with given {@link PlayerType}
     */
    private void findRemotePlay() {
        try {
            /*check if playerType is not set as remote player*/
            if(ap.getOfferedType() == PlayerType.REMOTE) {
                System.out.println("Can't offer a remote player type as remote player.");
                System.exit(1);
            } else {
                /*set port, if none is set, port is default (1099)*/
                int port = ap.isSet("port") ? ap.getPort() : Remote.DEFAULT_PORT;
                /*create new remote object with given port*/
                Remote remote = new Remote(port);
                if(io != null)
                    /*create new netplayer with given type and view object*/
                    remote.offer(new NetPlayer(createPlayer(ap.getOfferedType(), io)), ap.getName());
                else
                    /*create new netplayer without view object, no output will to provided*/
                    remote.offer(new NetPlayer(createPlayer(ap.getOfferedType())), ap.getName());
            }

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

    }

    /**
     * Method getRemotePlayer to search for an offered {@link Player}, with given host, port and name
     *
     * @return {@link Player} found {@link Player} to start a {@link Game} with
     */
    private Player getRemotePlayer() {
        Player player = null;
        try {
            /*create remote object with given, or default, port and host*/
            String host = ap.isSet("host") ? ap.getHost() : "localhost";
            Remote remote = ap.isSet("port") ? new Remote(host, ap.getPort()) : new Remote(host);
            /*set player as given*/
            player = ap.isSet("name") ? remote.find(ap.getName()) : remote.find();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        /*return found player*/
        return player;
    }

    /**
     * Method setUpIO to add a specified {@link OutputType} and/or a {@link Requestable} object  to the {@link View} object
     * @param outputType
     */
    private void setUpIO(OutputType outputType) {
        /*check which output type is given and add correct one to the view*/
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
            /*create players with given PlayerTypes*/
            players[RED] = createPlayer(ap.getRed());
            players[BLUE] = createPlayer(ap.getBlue());
        } catch (ArgumentParserException e) {
            System.out.println(e);
            System.exit(1);
        }
        /*return created players, init needs to be called*/
        return players;
    }

    /**
     * Method createPlayer to initialize a {@link Player} with given {@link PlayerType} and {@link View} object
     *
     * @param playerType {@link PlayerType}
     * @param view {@link View} object to provide possibility to visualize the {@link Game} and {@link Board}
     *
     * @return {@link Player} with needed {@link PlayerType}
     */
    private Player createPlayer(PlayerType playerType, View view) {
        Player player = null;
        /*create concrete player, with given view object, of exit if wrong playertype is given*/
        switch (playerType) {
            // TODO Split TextIO
            case HUMAN: player = new HumanPlayer(requestable, view); break;
            case RANDOM_AI: player = new RndPlayer(view); break;
            case SIMPLE_AI: player = new SimplePlayer(); break;
            case REMOTE: player = getRemotePlayer(); break;
            default: System.out.println("Unsupported PlayerType."); System.exit(1);
        }
        return player;
    }

    /**
     * Method createPlayer to initialize a {@link Player} with given {@link PlayerType} but without {@link View} object
     * @param playerType {@link PlayerType}
     *
     * @return {@link Player}
     */
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
     * Outputs statistic about this {@link Tournament} on standard-output.
     *
     * @param players array of {@link Player}
     */
    private void startTournament(Player[] players) {
        Tournament tournament = null;
        try {

            tournament = new Tournament(players, io, ap.isDebug(),
                    ap.isSet("delay") ? ap.getDelay() : 0, board.getSize(), ap.getGameCount(), ap.isSet("timeout") ? ap.getTimeOut() : 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        TResult tResult = tournament.play();
        //TODO possibility for graphic output
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
                "-offer \t chose player type to offer . Can not be remote \n" +
                "-name  \t chose name for the player, none other settings need to be set" +

                "if -offer is NOT set, obligatory settings are: \n" +
                "-red \t chose player type for red \n" +
                "-blue \t chose player type for blue \n" +
                "-size \t chose board size \n" +

                "optional settings for finding players are:\n" +
                "---- only together and if network game is wanted ---- \n" +
                "-host \t ip-address or resolvable name (eg cip name) of device you want to connect with \n" +
                "-name \t name of concrete player you want to connect with, if none is given, all available players are displayed \n" +
                "-port \t port to either offer player on or look for player on host\n" +
                "------------ \n" +
                "-games  \t activates tournament mode and sets number of games for the tournament \n" +
                "        \t (beware that at every other game, the player colors are changing, so if you are starting \n" +
                "        \t with color red as a human, in the second round, you will be color blue, in terms of fairness) \n" +
                "-delay  \t sets delay time in milliseconds to slow down the game \n" +
                "-output \t chose output type, default is textual \n" +
                "possible parameter:\n" +
                "player types: human, random, simple, adv1, remote \n" +
                "size:   \t  integer between 4 and 26 \n" +
                "output: \t  text, graphic, none \n" +
                "games:  \t  integer bigger than 0 \n" +
                "delay:  \t  integer bigger than 0 (in milliseconds) \n" +

                "\nFLAGS: \n" +
                //TODO remove graphic flag
                "--graphic \t activates the graphic output, if not set output will be on the standard-output \n" +
                "--debug \t activates the debug-mode: shows additional information for every move \n";
    }
}