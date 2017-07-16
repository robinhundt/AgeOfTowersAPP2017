package towerwarspp.player;

import towerwarspp.preset.Move;

/**
 * Class implementing a simple Computer opponent that uses the moves returned by {@link PlayStrategy#HEAVY}.
 * @author Robin Hundt
 * @version 14-07-17
 */
public class Adv1Player  extends BasePlayer{
    /**
     * Chooses the {@link Move} returned by {@link PlayStrategy#HEAVY} to play.
     * @return move returned by {@link PlayStrategy#HEAVY}
     */
    @Override
    Move deliverMove() {
        return PlayStrategy.heavyPlay(board);
    }
}
