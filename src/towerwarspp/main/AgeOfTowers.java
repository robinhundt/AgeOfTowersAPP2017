package towerwarspp.main;

import static towerwarspp.main.debug.DebugLevel.*;
import static towerwarspp.main.debug.DebugSource.MAIN;

import towerwarspp.board.Board;
import towerwarspp.io.GraphicIO;
import towerwarspp.io.IO;
import towerwarspp.io.TextIO;
import towerwarspp.io.View;
import javax.swing.*;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.awt.*;

import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.game.*;
import towerwarspp.main.tournament.*;
import towerwarspp.network.Remote;
import towerwarspp.player.*;
import towerwarspp.player.mcts.Mcts;
import towerwarspp.player.mcts.TreeSelectionStrategy;
import towerwarspp.preset.*;





/**
 * Class AgeOfTower - main class to start a new game of TowerWarsPP.
 *
 * @author Niklas Mueller
 * @version 1.2 July 05th 2017
 */
public class AgeOfTowers {
    /**
     * Default {@link OutputType} that is used if nothing else is specified.
     */
    private static final OutputType DEF_OUTPUT = OutputType.GRAPHIC;

    /**
     * {@link ArgumentParser} to get settings and flags from the command line
     */
    private ArgumentParser ap;

    /**
     * {@link Debug} singleton that will be set to the debugging preferences entered by
     * the user via the command line arguments at program start
     */
    private Debug debug;

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
     * saveObject for save
     */
    private Save save;



    /**
     * Constructor parsing the given array of strings to the {@link ArgumentParser} and starting the specified type of game
     *
     * @param args command line parameters, providing settings for the game
     */
    private AgeOfTowers(String[] args) {
        try {
            if(args.length == 0) {
                try {
                    args = initArguments();
                } catch (Exception e) {

                }
            }
            /*create new ArgumentParser to get access to settings and flags*/
            ap = new ArgumentParser(args);

            /*check if no parameters have been given, or flag --help is activated*/
            if (ap.isHelp()) {
                System.out.println(helpOutput());
                System.exit(0);
            }

            /* Create single instance Debug object and initialize it with the optional values provided by the
            * user via the ArgumentParser */
            debug = Debug.getInstance();
            if(ap.isSet("debug")) {
                debug.setDebugLevel(ap.isSet("dlevel") ? ap.getDebugLevel() : DebugLevel.LEVEL_1);

                if(ap.isSet("dsource"))
                    debug.setSource(ap.getDebugSource());
            }

            if(ap.isSet("graphic")) {
                System.out.println("Flag --graphic is deprecated. Standard output type is graphic. If you wish to" +
                        "play with text or no output, set -output setting.");
            }

            /*check outputType, default is textual output*/
            if(ap.isSet("output")) {
                setUpIO(ap.getOutputType());
            } else {
                System.out.println("Starting game in standard graphic output mode. If text or no output is wished" +
                        " set -output {text, none}. Flag --graphic is deprecated and has no use.");
                setUpIO(DEF_OUTPUT);
            }

            /*check which way of game needs to be started: network (hosting or offering) or local*/
            if(ap.isSet("offer")) {
                findRemotePlay();
            } else if(ap.isSet("blue") && ap.isSet("red") && (ap.isSet("size") || ap.isSet("load"))) {
                if(ap.isSet("load")) {
                    try {
                        save = Save.load(ap.getLoadName());
                    } catch(Exception e) {
                        System.out.println("Loading Failed");
                    }
                } else {
                    /*check if board size is valid*/
                    if(ap.getSize() >= 4 && ap.getSize() <= 26) {
                        initBoard(ap.getSize());
                    } else {
                        System.out.println("-size of Board must be between 4 and 26");
                        System.exit(1);
                    }
                }
                /*create players with given PlayerTypes*/
                Player[] players = createPlayers();
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
     *
     */
    synchronized private String[] initArguments() throws Exception {
        ParameterInput parameterInput = new ParameterInput();
        return parameterInput.getString();
    }

    /**
     * Method initBoard to initialized the board variable with a new {@link Board} and given size
     * @param boardSize integer providing size of board
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
                    remote.offer(new NetPlayer(createPlayer(ap.getOfferedType()), io), ap.getName());
                else
                    /*create new netplayer without view object, no output will be provided*/
                    remote.offer(new NetPlayer(createPlayer(ap.getOfferedType())), ap.getName());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Couldn't offer ");
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
            e.printStackTrace();
            System.exit(1);
        }
        /*return found player*/
        return player;
    }

    /**
     * Method setUpIO to add a specified {@link OutputType} and/or a {@link Requestable} object  to the {@link View} object
     * @param outputType {@link OutputType} to be set
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
            e.printStackTrace();
            System.exit(1);
        }
        /*return created players, init needs to be called*/
        return players;
    }

    /**
     * Method createPlayer to initialize a {@link Player} with given {@link PlayerType}.
     *
     * @param playerType {@link PlayerType}
     *
     * @return {@link Player} with needed {@link PlayerType}
     */
    private Player createPlayer(PlayerType playerType) {
        Player player = null;
        /*create concrete player, with given view object, of exit if wrong playertype is given*/
        switch (playerType) {
            case HUMAN: player = new HumanPlayer(requestable); break;
            case RANDOM_AI: player = new RndPlayer(); break;
            case SIMPLE_AI: player = new SimplePlayer(); break;
            case ADVANCED_AI_1: player = new Adv1Player(); break;
            case ADVANCED_AI_2: player = createAdv2Player(); break;
            case REMOTE: player = getRemotePlayer(); break;
            default: System.out.println("Unsupported PlayerType."); System.exit(1);
        }
        return player;
    }

    private Adv2Player createAdv2Player() {
        TreeSelectionStrategy treeSelectionStrategy = TreeSelectionStrategy.MAX;
        PlayStrategy playStrategy = PlayStrategy.DYNAMIC;
        long timePerMove = Adv2Player.DEF_TIME_PER_MOVE;
        int parallelFactor = Adv2Player.DEF_PARALLELIZATION;
        double bias = Mcts.DEF_BIAS;
        boolean fairPlay = false;
        try {

            if(ap.isSet("tstrategy"))
                    treeSelectionStrategy =  ap.getTreeSelectionStrategy();
            if(ap.isSet("pstrategy"))
                playStrategy = ap.getPlayStrategy();
            if(ap.isSet("thinktime"))
                timePerMove = ap.getThinkingTime();
            if(ap.isSet("prallel"))
                parallelFactor = ap.getParrallelFactor();
            if(ap.isSet("bias"))
                bias = ap.getBias();
            fairPlay = ap.isFairplay();

        } catch (ArgumentParserException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return new Adv2Player(timePerMove, parallelFactor, treeSelectionStrategy, playStrategy, fairPlay, bias);
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
            if(ap.isSet("load")) {
                Game game = new Game(redPlayer, bluePlayer, io, ap.isDebug(), 
                ap.isSet("delay") ? ap.getDelay() : 0, save);
                result = game.play(ap.isSet("timeout") ? ap.getTimeOut() : 0, game.turn());
            } else {
                Game game = new Game(redPlayer, bluePlayer, ap.getSize(), io, ap.isDebug(), 
                ap.isSet("delay") ? ap.getDelay() : 0);
                result = game.play(ap.isSet("timeout") ? ap.getTimeOut() : 0, game.turn());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (io != null ) {
            io.display(result != null ? result.toString() : "Game ended with timeout, because to many moves has been made!");
        }
    }

    /**
     * Method startTournament to start a tournament, starting as many {@link Game}s as wished.
     * Outputs statistic about this {@link Tournament} on standard-output.
     *
     * @param players array of {@link Player}
     */
    private void startTournament(Player[] players) {
        Tournament tournament;
        TResult tResult = null;
        try {

            tournament = new Tournament(players, io, ap.isDebug(),
                    ap.isSet("delay") ? ap.getDelay() : 0, board.getSize(), ap.getGameCount(), ap.isSet("timeout") ? ap.getTimeOut() : 0);
            tResult = tournament.play(ap.isStatistic());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (io != null) {
            io.dialog(tResult.toString());
        } else {
            System.out.println(tResult);
        }
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
                "-red  \t chose player type for red \n" +
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
                "--statistic \t activates the constant output of the tournament statistic, \n" +
                "            \t only takes effect if a tournament has been started" +
                "--debug     \t activates the debug-mode: shows additional information for every move \n" +
                "------------ \n" +
                "\n" +
                "Advanced settings for adv2 AI:\n" +
                "-thinktime \t set the time (in ms) adv2 AI will spend deciding on a move per round (default 2000ms)\n" +
                "-pstrategy \t set the playout strategy of adv2 AI to either light (l) or heavy (h).\n" +
                "           \t See  the documentation for more information.\n" +
                "-tstrategy \t set the tree selection policy to either max (m) or robust (r). See documentation for more\n" +
                "           \t information.\n" +
                "-bias      \t set the bias factor used in the UCB1 formulae of the Monte Carlo tree search.\n" +
                "-parallel  \t set a value for the amount of parrallelization to employ. Roughly corresponds to the \n" +
                "           \t  the maximum number of Thread running in parralel ( +-1).\n" +
                "--fair";

    }
}