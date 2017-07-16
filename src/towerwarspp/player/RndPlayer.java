package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;

/**
 * Class that implements a Random Player. Is a subclass of {@link BasePlayer}. The only difference is the implementation of
 * deliverMove(), which picks a random move from all possible moves that are available for the players {@link #color}.
 * @author Alexander Wähling
 * @version 08-07-17
 */
public class RndPlayer extends BasePlayer {
    /**
     * Implementation of the {@link BasePlayer#deliverMove()} method. Uses the method {@link PlayStrategy#lightPlay(Board)}
     * to get a random from all the possible moves of this player at the current game state. The PlayerColor does not
     * need to  be passed, because the board itself knows whose turn it is and this method should only be called if it's
     * also this players turn.
     * @return randomly selected move out of all possible moves for this player
     */
    @Override
    Move deliverMove() {
        return PlayStrategy.lightPlay(board);
    }
}
