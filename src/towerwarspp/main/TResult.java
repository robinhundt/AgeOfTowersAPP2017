package towerwarspp.main;

import static towerwarspp.preset.PlayerColor.*;

/**
 * Class TResult to provide a statistic about a {@link Tournament}.
 * Information are: number of wins, number of wins per {@link WinType}, average number of moves per win
 *
 * @author Niklas Mueller
 * @version 07-04-2017
 */
public class TResult {

    /*variable to store information in, initialized with zero*/
    int redWins = 0;
    int blueWins = 0;
    int redBaseDestroyed = 0;
    int blueBaseDestroyed = 0;
    int redNoPosMoves = 0;
    int blueNoPosMoves = 0;
    int redIllegalMove = 0;
    int blueIllegalMove = 0;
    int redSurrender = 0;
    int blueSurrender = 0;
    int redAverageMoves = 0;
    int blueAverageMoves = 0;


    /**
     * Constructor, just creating a new object with already initialized variables
     */
    public TResult() {

    }

    /**
     * Method addResult to include a {@link Result} in the statistic
     *
     * @param result {@link Result} information to be added
     */
    public void addResult(Result result) {
        if (result.winner == RED) {
            redWins++;
            switch (result.winType) {
                case BASE_DESTROYED: redBaseDestroyed++;
                case ILLEGAL_MOVE: redIllegalMove++;
                case NO_POSSIBLE_MOVES: redNoPosMoves++;
                case SURRENDER: redSurrender++;
            }
            redAverageMoves = (redAverageMoves * (redWins-1) + (result.winnerMoves))/redWins;
        }
        else {
            blueWins++;
            switch (result.winType) {
                case BASE_DESTROYED: blueBaseDestroyed++;
                case ILLEGAL_MOVE: blueIllegalMove++;
                case NO_POSSIBLE_MOVES: blueNoPosMoves++;
                case SURRENDER: blueSurrender++;
            }
            blueAverageMoves = (blueAverageMoves *(blueWins-1) + (result.winnerMoves))/blueWins;
        }
    }

    @Override
    /**
     * Method toString returning a string with all information about this {@link TResult}
     *
     * @return String
     */
    public String toString() {
        return "Red:\n" +
                "-total wins: " + redWins + "\n" +
                "-wins per base destruction: " + redBaseDestroyed + "\n" +
                "-wins per surrender of blue: " + redSurrender + "\n" +
                "-wins per illegal move of blue: " + redIllegalMove + "\n" +
                "-wins per immobility of blue: " + redNoPosMoves + "\n" +
                "-average amount of moves per win: " + redAverageMoves + "\n" +
                "\n" +
                "Blue:\n" +
                "-total wins: " + blueWins + "\n" +
                "-wins per base destruction: " + blueBaseDestroyed + "\n" +
                "-wins per surrender of red: " + blueSurrender + "\n" +
                "-wins per illegal move of red: " + blueIllegalMove + "\n" +
                "-wins per immobility of red: " + blueNoPosMoves + "\n" +
                "-average amount of moves per win: " + blueAverageMoves + "\n";
    }
}
