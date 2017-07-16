package towerwarspp.main.tournament;

import towerwarspp.main.WinType;
import towerwarspp.main.game.Result;
import towerwarspp.preset.PlayerColor;

import java.util.Arrays;


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
    public TResult() {
        wins = new int[2];
        baseDestroyed = new int[2];
        noPosMoves = new int[2];
        illegalMove = new int[2];
        surrender = new int[2];
        avgMoves = new double[2];
        totalMoves = new int[2];
    }

    /**
     * Returns the wins.
     *
     * @return the wins
     */
    int[] getWins() {
        return Arrays.copyOf(wins, wins.length);
    }

    /**
     * Sets the Wins.
     *
     * @param wins the new value of wins.
     */
    void setWins(int[] wins) {
        this.wins = wins;
    }

    /**
     * Returns the Int-Array of destroyed-base-wins.
     *
     * @return the Int-Array of destroyed-base-wins.
     */
    int[] getBaseDestroyed() {
        return Arrays.copyOf(baseDestroyed, baseDestroyed.length);
    }

    /**
     * Sets the int-array of destroyed bases
     *
     * @param baseDestroyed the new Value of baseDestroyed
     */
    void setBaseDestroyed(int[] baseDestroyed) {
        this.baseDestroyed = baseDestroyed;
    }

    /**
     * Returns the noPosMoves-Array.
     *
     * @return the noPosMoves-Array
     */
    int[] getNoPosMoves() {
        return Arrays.copyOf(noPosMoves, noPosMoves.length);
    }

    /**
     * Sets the value of noPosMoves.
     *
     * @param noPosMoves the new value of noPosMoves
     */
    void setNoPosMoves(int[] noPosMoves) {
        this.noPosMoves = noPosMoves;
    }

    /**
     * Returns the illegalMoves-array.
     *
     * @return illiegalMoves
     */
    int[] getIllegalMove() {
        return Arrays.copyOf(illegalMove, illegalMove.length);
    }

    /**
     * Sets the value of illegalMoves
     *
     * @param illegalMove the new value of illegal Moves
     */
    void setIllegalMove(int[] illegalMove) {
        this.illegalMove = illegalMove;
    }

    /**
     * Returns the Value of surrender.
     *
     * @return the Value of surrender
     */
    public int[] getSurrender() {
        return Arrays.copyOf(surrender, surrender.length);
    }

    /**
     * Sets the value of surrender.
     *
     * @param surrender the new value of surrender
     */
    public void setSurrender(int[] surrender) {
        this.surrender = surrender;
    }

    /**
     * Returns the Value of avgMoves.
     *
     * @return the Value of avgMoves
     */
    double[] getAvgMoves() {
        return Arrays.copyOf(avgMoves, avgMoves.length);
    }

    /**
     * Sets the value of avgMoves.
     *
     * @param avgMoves the new value of avgMoves
     */
    void setAvgMoves(double[] avgMoves) {
        this.avgMoves = avgMoves;
    }

    /**
     * Returns the Value of totalMoves.
     *
     * @return the Value of totalMoves
     */
    int[] getTotalMoves() {
        return Arrays.copyOf(totalMoves, totalMoves.length);
    }

    /**
     * Sets the value of totalMoves.
     *
     * @param totalMoves the new value of totalMoves
     */
    public void setTotalMoves(int[] totalMoves) {
        this.totalMoves = totalMoves;
    }

    /**
     * Returns the Value of timeoutGames.
     *
     * @return the Value of timeoutGames
     */
    int getTimeoutGames() {
        return timeoutGames;
    }

    /**
     * Sets the value of timeoutGames.
     *
     * @param timeoutGames the new value of timeoutGames
     */
    void setTimeoutGames(int timeoutGames) {
        this.timeoutGames = timeoutGames;
    }

    /**
     * Returns the Value of totalGames.
     *
     * @return the Value of totalGames
     */
    int getTotalGames() {
        return totalGames;
    }

    /**
     * Sets the value of totalGames.
     *
     * @param totalGames the new value of totalGames
     */
    void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    /**
     * Overridden method toString returning a string with all information about this {@link TResult}
     *
     * @return String
     */
    @Override
    public String toString() {

        return "total games: " + totalGames +
                "\nRed:\n" +
                "-total wins:\t \t \t" + wins[RED] + "\n" +
                "-wins per base destruction:\t \t" + baseDestroyed[RED] + "\n" +
                "-wins per surrender of blue:\t \t" + surrender[RED] + "\n" +
                "-wins per illegal move of blue:\t" + illegalMove[RED] + "\n" +
                "-wins per immobility of blue:\t \t" + noPosMoves[RED] + "\n" +
                "-total winning moves: \t \t" + totalMoves[RED] + "\n" +
                "-average amount of moves per win:\t" + String.format("%.1f", avgMoves[RED]) + "\n" +

                "\n" +
                "Blue:\n" +
                "-total wins:\t \t \t" + wins[BLUE] + "\n" +
                "-wins per base destruction:\t \t" + baseDestroyed[BLUE] + "\n" +
                "-wins per surrender of red:\t \t" + surrender[BLUE] + "\n" +
                "-wins per illegal move of red:\t" + illegalMove[BLUE] + "\n" +
                "-wins per immobility of red:\t \t" + noPosMoves[BLUE] + "\n" +
                "-total winning moves: \t \t" + totalMoves[BLUE] + "\n" +
                "-average amount of moves per win:\t" + String.format("%.1f", avgMoves[BLUE]) + "\n" +
                "\n-games ended with timeout: \t" + timeoutGames;


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
}
