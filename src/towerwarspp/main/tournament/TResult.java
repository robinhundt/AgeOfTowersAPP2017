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

    private final short RED = 0;
    private final short BLUE = 1;

    private boolean swappedPlayers = false;

    private int wins[];
    private int baseDestroyed[];
    private int noPosMoves[];
    private int illegalMove[];
    private int surrender[];
    private double avgMoves[];
    private int totalMoves[];

    private int timeoutGames = 0;
    private int totalGames = 0;


    /**
     * Constructor, just creating a new object with already initialized variables
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

        if (result.winner != null) {
            if (result.winner == PlayerColor.RED)
                winnerIndex = swappedPlayers ? BLUE : RED;
            else if (result.winner == PlayerColor.BLUE)
                winnerIndex = swappedPlayers ? RED : BLUE;

            wins[winnerIndex]++;
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
            totalMoves[winnerIndex] += result.winnerMoves;
            avgMoves[winnerIndex] = (double) totalMoves[winnerIndex] / wins[winnerIndex];
        }
        else {
            timeoutGames++;
        }
        totalGames++;
        swappedPlayers = !swappedPlayers;
    }


    /**
     * Overriden method toString returning a string with all information about this {@link TResult}
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
