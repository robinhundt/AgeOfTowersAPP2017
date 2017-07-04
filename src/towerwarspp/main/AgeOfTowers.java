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
            /*create new ArgumentParse object*/
            ArgumentParser ap = new ArgumentParser(args);

            /*check if no parameters have been given, or flag --help is activated*/
            if (args.length == 0 || ap.isHelp()) {
                System.out.println(helpOutput());
                System.exit(0);
            }

            /*check if board size is valid*/
            if (ap.getSize() < 4 || ap.getSize() > 26) {
                System.out.println("Board size needs to be between 4 and 26!");
                System.exit(1);
            }

            /*create new board and viewer object*/
            Board board = new Board(ap.getSize());
            BViewer viewer = board.viewer();

            /*check if graphic output should be enabled*/
            if (ap.isGraphic()) {
                io = new TextIO(viewer);
            }
            /*otherwise do output on standard output*/
            else {
                io = new TextIO(viewer);
            }

            /*check if red and blue player types are given, then initialize them*/
            if (ap.isSet("red")) {
                firstPlayer = makePlayer(ap.getSize(), RED, ap.getRed(), io);
            }
            else {
                firstPlayer = null;
            }
            if (ap.isSet("blue")) {
                secondPlayer = makePlayer(ap.getSize(), BLUE, ap.getBlue(), (Requestable) io);
            }
            else {
                secondPlayer = null;
            }

            /*check if tournament mode is enabled*/

            if (ap.isSet("rounds")) {
                /*create tournament object*/
            }
            else {
                /*create a new game object with the given players and settings*/
                Game game = new Game(firstPlayer, secondPlayer, OutputType.TEXTUAL, true,
                        ap.isSet("delay") ? ap.getDelay() : 0, ap.getSize());

                /*output game result*/
                System.out.println(game.play().toString());
            }
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
                "Flags start with '--' and do not need parameter. All flags are optional.\n" +
                "\nSETTINGS: \n" +
                "obligatory:\n" +
                "-red \t chose player type for red \n" +
                "-blue \t chose player type for blue \n" +
                "-size \t chose board size \n" +
                "optional:\n" +
                "-rounds \t activates tournament mode and sets number of rounds for the tournament \n" +
                "-delay \t sets delay time in milliseconds to slow down the game \n" +
                "possible parameter:\n" +
                "player types: human, random, simple, adv1, remote \n" +
                "size: integer between 4 and 26 \n" +
                "rounds: integer bigger than 1 \n" +
                "delay: integer bigger than 0 (in milliseconds) \n" +
                "\nFLAGS: \n" +
                "--graphic \t activates the graphic output, if not set output will be on the standard-output \n" +
                "--debug \t activates the debug-mode: shows additional information for every move \n";
    }
}