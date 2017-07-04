package towerwarspp.main;

import static towerwarspp.preset.PlayerColor.*;

/**
 * Created by niklas on 04.07.17.
 */
public class TResult {

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


    public TResult() {

    }

    public void addResult(Result result) {
        if (result.winner == RED) {
            redWins++;
            switch (result.winType) {
                case BASE_DESTROYED: redBaseDestroyed++;
                case ILLEGAL_MOVE: redIllegalMove++;
                case NO_POSSIBLE_MOVES: redNoPosMoves++;
                case SURRENDER: redSurrender++;
            }
            redAverageMoves = (redAverageMoves + result.winnerMoves)/2;
        }
        else {
            blueWins++;
            switch (result.winType) {
                case BASE_DESTROYED: blueBaseDestroyed++;
                case ILLEGAL_MOVE: blueIllegalMove++;
                case NO_POSSIBLE_MOVES: blueNoPosMoves++;
                case SURRENDER: blueSurrender++;
            }
            blueAverageMoves = (blueAverageMoves + result.winnerMoves)/2;
        }
    }
}
