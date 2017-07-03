package towerwarspp.main;

import towerwarspp.preset.PlayerColor;

/**
 * Created by niklas on 03.07.17.
 */
public class Result {
    final PlayerColor winner;
    final int winnerMoves;
    final WinType winType;

    Result(PlayerColor winner, int winnerMoves, WinType winType) {
        this.winner = winner;
        this.winnerMoves = winnerMoves;
        this.winType = winType;
    }
}
