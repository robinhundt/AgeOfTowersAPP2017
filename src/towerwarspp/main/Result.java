package towerwarspp.main;

import towerwarspp.preset.PlayerColor;

/**
 * Class {@link Result} to store information about the winner of a {@link Game}.
 * Information are: {@link PlayerColor}, number of moves, {@link WinType}
 *
 * @author Niklas Mueller
 * @version 07-03-2017
 */
public class Result {
    final PlayerColor winner;
    final int winnerMoves;
    final WinType winType;

    /**
     * Contructor
     * @param winner {@link PlayerColor} of winning {@link towerwarspp.preset.Player}
     * @param winnerMoves integer containing number of moves winner needed to win
     * @param winType {@link WinType] of winning {@link towerwarspp.preset.Player}
     */
    Result(PlayerColor winner, int winnerMoves, WinType winType) {
        this.winner = winner;
        this.winnerMoves = winnerMoves;
        this.winType = winType;
    }

    @Override
    /**
     * Overriden method toString providing simple output of the {@link Result}}
     * @return String containing all informations of this object
     */
    public String toString() {
        return winner + " wins in " + winnerMoves + " moves by " + winType;
    }
}
