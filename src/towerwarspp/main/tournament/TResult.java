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

    final short RED = 0;
    final short BLUE = 1;

    boolean swappedPlayers = false;

    int wins[];
    int baseDestroyed[];
    int noPosMoves[];
    int illegalMove[];
    int surrender[];
    double avgMoves[];
    int totalMoves[];


    /**
     * Constructor, just creating a new object with already initialized variables
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
     * Method addResult to include a {@link Result} in the statistic
     *
     * @param result {@link Result} information to be added
     */
    public void addResult(Result result) {
        int winnerIndex = 0;

        if(result.winner == PlayerColor.RED)
            winnerIndex = swappedPlayers ? BLUE : RED;
        else if(result.winner == PlayerColor.BLUE)
            winnerIndex = swappedPlayers ? RED : BLUE;

        wins[winnerIndex]++;
        switch (result.winType) {
            case BASE_DESTROYED: baseDestroyed[winnerIndex]++; break;
            case NO_POSSIBLE_MOVES: noPosMoves[winnerIndex]++; break;
            case ILLEGAL_MOVE: illegalMove[winnerIndex]++;  break;
            case SURRENDER: surrender[winnerIndex]++; break;
        }

        totalMoves[winnerIndex] += result.winnerMoves;
        avgMoves[winnerIndex] = (avgMoves[winnerIndex] * (wins[winnerIndex] -1) + result.winnerMoves) / wins[winnerIndex];
        swappedPlayers = !swappedPlayers;

    }

    @Override
    /**
     * Method toString returning a string with all information about this {@link TResult}
     *
     * @return String
     */
    public String toString() {

        return "Red:\n" +
                "-total wins: " + wins[RED] + "\n" +
                "-wins per base destruction:\t " + baseDestroyed[RED] + "\n" +
                "-wins per surrender of blue:\t " + surrender[RED] + "\n" +
                "-wins per illegal move of blue:\t " + illegalMove[RED] + "\n" +
                "-wins per immobility of blue:\t " + noPosMoves[RED] + "\n" +
                "-total moves: \t " + totalMoves[RED] + "\n" +
                "-average amount of moves per win:\t " + String.format("%.3f" ,avgMoves[RED]) + "\n" +

                "\n" +
                "Blue:\n" +
                "-total wins: " + wins[BLUE] + "\n" +
                "-wins per base destruction:\t " + baseDestroyed[BLUE] + "\n" +
                "-wins per surrender of red:\t " + surrender[BLUE] + "\n" +
                "-wins per illegal move of red:\t " + illegalMove[BLUE] + "\n" +
                "-wins per immobility of red:\t " + noPosMoves[BLUE] + "\n" +
                "-total moves: \t " + totalMoves[BLUE] + "\n" +
                "-average amount of moves per win:\t " + String.format("%.3f" ,avgMoves[BLUE]) + "\n";


    }
}
