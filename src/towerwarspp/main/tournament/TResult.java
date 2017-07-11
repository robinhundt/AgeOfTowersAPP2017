package towerwarspp.main.tournament;

import towerwarspp.main.game.Result;
import towerwarspp.main.WinType;
import towerwarspp.preset.PlayerColor;


/**
 * Class TResult to provide a statistic about a {@link Tournament}.
 * Information are: number of wins, number of wins per {@link WinType}, average number of moves per win
 *
 * @author Niklas Mueller
 * @version 07-04-2017
 */
public class TResult {

    /**
     * Constant for array-index usage
     */
    private final short RED = 0;
    /**
     * Constant for array-index usage
     */
    private final short BLUE = 1;

    /**
     * Shows if the {@link towerwarspp.preset.Player}s have been swapped, so the {@link Result} will be included correctly
     */
    private boolean swappedPlayers = false;

    /**
     * Stores number of won games, for both {@link towerwarspp.preset.Player}s
     */
    private int wins[];
    /**
     * Stores number of games which ended with a base destruction, for both {@link towerwarspp.preset.Player}s
     */
    private int baseDestroyed[];
    /**
     * Stores number of games which ended with no possible moves, for both {@link towerwarspp.preset.Player}s
     */
    private int noPosMoves[];
    /**
     * Stores number of games which ended with a illegal move, for both {@link towerwarspp.preset.Player}s
     */
    private int illegalMove[];
    /**
     * Stores number of games which ended with a surrender, for both {@link towerwarspp.preset.Player}s
     */
    private int surrender[];
    /**
     * Store average number of moves per game, for both {@link towerwarspp.preset.Player}s
     */
    private double avgMoves[];
    /**
     * Stores total number of moves in the whole tournament, for both {@link towerwarspp.preset.Player}s
     */
    private int totalMoves[];

    /**
     * Stores number of game which ended with a timeout
     */
    private int timeoutGames = 0;
    /**
     * Stores total number of played games
     */
    private int totalGames = 0;


    /**
     * Constructor declaring the array and variables
     */
    TResult() {
        wins = new int[2];
        baseDestroyed = new int[2];
        noPosMoves = new int[2];
        illegalMove = new int[2];
        surrender = new int[2];
        avgMoves = new double[2];
        totalMoves = new int[2];
    }

    /**
     * Method addResult to include a {@link Result} in the statistic
     *
     * @param result {@link Result} information to be added
     */
    void addResult(Result result) {
        int winnerIndex = 0;

        /*check if the result contains a game which ended not with timeout*/
        if (result.winner != null) {
            /*identify winner and set index for arrays*/
            if (result.winner == PlayerColor.RED)
                winnerIndex = swappedPlayers ? BLUE : RED;
            else if (result.winner == PlayerColor.BLUE)
                winnerIndex = swappedPlayers ? RED : BLUE;

            /*increment number of wins for winner*/
            wins[winnerIndex]++;
            /*increment number of wintype for winner*/
            switch (result.winType) {
                case BASE_DESTROYED:
                    baseDestroyed[winnerIndex]++;
                    break;
                case NO_POSSIBLE_MOVES:
                    noPosMoves[winnerIndex]++;
                    break;
                case ILLEGAL_MOVE:
                    illegalMove[winnerIndex]++;
                    break;
                case SURRENDER:
                    surrender[winnerIndex]++;
                    break;
            }
            /*refresh total number of moves*/
            totalMoves[winnerIndex] += result.winnerMoves;
            /*refresh average number of moves per game for winner*/
            avgMoves[winnerIndex] = (double) totalMoves[winnerIndex] / wins[winnerIndex];
        }
        /*if a game ended with timeout*/
        else {
            /*just increment this counter*/
            timeoutGames++;
        }
        /*increment number of total played games and swap players*/
        totalGames++;
        swappedPlayers = !swappedPlayers;
    }


    /**
     * Overridden method toString returning a string with all information about this {@link TResult}
     *
     * @return String
     */
    @Override
    public String toString() {

        return  "total games: " + totalGames +
                "\nRed:\n" +
                "-total wins:\t \t \t" + wins[RED] + "\n" +
                "-wins per base destruction:\t \t" + baseDestroyed[RED] + "\n" +
                "-wins per surrender of blue:\t \t" + surrender[RED] + "\n" +
                "-wins per illegal move of blue:\t" + illegalMove[RED] + "\n" +
                "-wins per immobility of blue:\t \t" + noPosMoves[RED] + "\n" +
                "-total moves: \t \t \t" + totalMoves[RED] + "\n" +
                "-average amount of moves per win:\t" + String.format("%.1f" ,avgMoves[RED]) + "\n" +

                "\n" +
                "Blue:\n" +
                "-total wins:\t \t \t" + wins[BLUE] + "\n" +
                "-wins per base destruction:\t \t" + baseDestroyed[BLUE] + "\n" +
                "-wins per surrender of red:\t \t" + surrender[BLUE] + "\n" +
                "-wins per illegal move of red:\t" + illegalMove[BLUE] + "\n" +
                "-wins per immobility of red:\t \t" + noPosMoves[BLUE] + "\n" +
                "-total moves: \t \t \t" + totalMoves[BLUE] + "\n" +
                "-average amount of moves per win:\t" + String.format("%.1f" ,avgMoves[BLUE]) + "\n" +
                "\n-games ended with timeout: \t" + timeoutGames;


    }
}
