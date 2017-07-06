package towerwarspp.main;

import towerwarspp.io.View;
import towerwarspp.preset.Player;

/**
 * Class {@link Tournament} to start a given number of {@link Game}s
 *
 * @author Niklas Mueller
 * @version 07-05-2017
 */
public class Tournament {

    Player[] players;
    View view;
    boolean debug;
    int delayTime, boardSize, rounds, timeOut;
    TResult tResult;

    /**
     * Constructor
     *
     * @param players array of {@link Player}s
     * @param outputType {@link OutputType}
     * @param debug
     * @param delayTime
     * @param boardSize
     * @param rounds number of {@link Game}s
     */
    public Tournament(Player[] players, View view, boolean debug,
                      int delayTime, int boardSize, int rounds, int timeOut) {
        this.players = players;
        this.view = view;
        this.debug = debug;
        this.delayTime = delayTime;
        this.boardSize = boardSize;
        this.rounds = rounds;
        this.timeOut = timeOut;
    }
    // TODO fix tournament cuz this won't work in case one of the players is human (probably)

    /**
     * Method play to start the {@link Tournament}
     *
     * @return {@link TResult} providing a statistic about this {@link Tournament}
     */
    public TResult play() {
        tResult = new TResult();
        for (int i=1; i<=rounds; i++) {
            System.out.println("Round No.: " + i);

            /*switch players to get a fair tournament*/
            int red = 0, blue = 1;
            if (i>1) {
                int tmp = red;
                red = blue;
                blue = tmp;
            }

            Game game = new Game(players[red], players[blue], boardSize, view, debug, delayTime);

            try {
                Result result = game.play(timeOut);
                System.out.println(result);
                tResult.addResult(result);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tResult;
    }
}
