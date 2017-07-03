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
        if (args.length == 0) {
            //TODO help output
        }
        Player redPlayer, bluePlayer;
        IO io;
        try {
            ArgumentParser ap = new ArgumentParser(args);

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
                redPlayer = makePlayer(ap.getSize(), RED, ap.getRed(), (Requestable) io);
//            }
//            if (ap.isSet("blue")) {
                bluePlayer = makePlayer(ap.getSize(), BLUE, ap.getBlue(), (Requestable) io);
//            }


            Game game = new Game(redPlayer, bluePlayer, OutputType.TEXTUAL, true,
                    ap.isSet("delay") ? ap.getDelay() : 0, ap.getSize());

            Result result = game.play();

            System.out.println(result);
        }
        catch (ArgumentParserException e) {

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}