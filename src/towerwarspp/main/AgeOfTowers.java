package towerwarspp.main;

import towerwarspp.board.BViewer;
import towerwarspp.board.Board;
import towerwarspp.io.GraphicIO;
import towerwarspp.io.IO;
import towerwarspp.io.TextIO;
import towerwarspp.preset.*;
import towerwarspp.player.PlayerFactory;

import static towerwarspp.player.PlayerFactory.makePlayer;
import static towerwarspp.preset.PlayerColor.*;

public class AgeOfTowers {
    public static void main(String[] args) {
        Player firstPlayer, secondPlayer;
        IO io;
        try {
            ArgumentParser ap = new ArgumentParser(args);

            if (args.length == 0 || ap.isHelp()) {
                System.out.println(helpOutput());
                System.exit(0);
            }

            Board board = new Board(ap.getSize());
            BViewer viewer = board.viewer();

//            if (ap.isGraphic()) {
//                //io = new GraphicIO(viewer);
//            }
//            else {
                io = new TextIO(viewer);
//            }

//            if (ap.isSet("red")) {
            //TODO player one and two instead of blue and red
                firstPlayer = makePlayer(ap.getSize(), RED, ap.getRed(), (Requestable) io);
//            }
//            if (ap.isSet("blue")) {
                secondPlayer = makePlayer(ap.getSize(), BLUE, ap.getBlue(), (Requestable) io);
//            }


            Game game = new Game(firstPlayer, secondPlayer, OutputType.TEXTUAL, true,
                    ap.isSet("delay") ? ap.getDelay() : 0, ap.getSize());

            Result result = game.play();

            System.out.println(result);
        }
        catch (ArgumentParserException e) {
            System.out.println(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String helpOutput() {
        return  "Starts a new game of TowerWarsPP with the given settings. \n" +
                "USAGE: \n --help \tshows this help message \n" +
                "Settings start with '-' and need a parameter and can be set as followed. \n" +
                "Flags start with '--' and do not need parameter. \n" +
                "\nsettings: \n" +
                "-red \t chose player type for red \n" +
                "-blue \t chose player type for blue \n" +
                "-size \t chose board size \n" +
                "-rounds \t set number of rounds for the tournament \n" +
                "-delay \t set delay time in milliseconds to slow down the game \n" +
                "\nflags: \n" +
                "--graphic \t activates the graphic output, if not set output will be on the standard-output \n" +
                "--debug \t activates the debug-mode: shows additional information for every move \n" +
                "--tournament \t activates the tournament-mode, in addition, '-rounds' needs to be set ";
    }
}