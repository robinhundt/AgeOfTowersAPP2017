package towerwarspp.main.tournament;

import towerwarspp.io.View;
import towerwarspp.main.game.Game;
import towerwarspp.main.game.Result;
import towerwarspp.preset.Player;

/**
 * Class {@link Tournament} to start a given number of {@link Game}s
 *
 * @author Niklas Mueller
 * @version 0.7 July 05th 2017
 */
public class Tournament {

    /**
     * Array of {@link Player}s, to  be able to switch the {@link towerwarspp.preset.PlayerColor} in every other {@link Game}
     */
    private Player[] players;
    /**
     * {@link View} to start the {@link Game} with
     */
    private View view;
    /**
     * boolean that is set to true if a View object is passed to the constructor, false if null is passed
     */
    private boolean hasView;
    /**
     * Boolean debug indicating if debug mode is activated and additional information should be given
     */
    private boolean debug;
    /**
     * Integer delayTime setting time to wait after every {@link towerwarspp.preset.Move} has been made.
     * boardSize setting size of the {@link towerwarspp.board.Board} to start the {@link Game} with.
     * games setting number of {@link Game}s to be played.
     * timeOut setting maximum time for a {@link Game} to last, stops if gametime exceeds this
     */
    private int delayTime, boardSize, games, timeOut;


    /**
     * Constructor setting every parameter as given.
     *
     * @param players array of {@link Player}s
     * @param view {@link View} object to be able to visualize the {@link Game}
     * @param debug boolean activating debug-mode
     * @param delayTime integer setting time to wait after every {@link Game}
     * @param boardSize integer setting board size
     * @param games number of {@link Game}s
     */
    public Tournament(Player[] players, View view, boolean debug,
                      int delayTime, int boardSize, int games, int timeOut) {
        this.players = players;
        if(view != null) {
            this.view = view;
            hasView = true;
        }
        this.debug = debug;
        this.delayTime = delayTime;
        this.boardSize = boardSize;
        this.games = games;
        this.timeOut = timeOut;
    }

    /**
     * Method play to start the {@link Tournament}. Starting as many {@link Game}s as class variable games is indicating.
     * {@link towerwarspp.preset.PlayerColor} of {@link Player}, and so the starting {@link Player}, is swapping after every {@link Game}.
     * {@link Result} of every {@link Game} will be written on standard output and a statistic of the whole {@link Tournament} as well.
     * If a {@link Game} exceeds a given number of {@link towerwarspp.preset.Move}s, this {@link Game} will be stopped.
     *
     * @return {@link TResult} providing a statistic about this {@link Tournament}
     */
    public TResult play() {
        /*create new TResult object to collect information for every game and create statistic*/
        TResult tResult = new TResult();
        int red = 0, blue = 1;

        /*start as many games as wanted*/
        for (int i=1; i<=games; i++) {
            /*if output is wanted, output current game number*/
            if(hasView)
                view.setTitle("Game No.: " + i);

            /*switch players to get a fair tournament*/
            if (i>1) {
                int tmp = red;
                red = blue;
                blue = tmp;
            }

            /*create game with given settings*/
            Game game = new Game(players[red], players[blue], boardSize, view, debug, delayTime);

            /*start game, output result of this game and include result in the statistic about this tournament*/
            try {
                /*start game and store result*/
                Result result = game.play(timeOut);
                if(hasView)
                    view.display(result.toString());
                tResult.addResult(result);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return tResult;
    }
}
