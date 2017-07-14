package towerwarspp.player;

import towerwarspp.preset.Move;

import java.util.Random;

/**
 * Created by robin on 10.07.17.
 */
public class Adv1Player  extends BasePlayer{
    Random random;

    public Adv1Player() {
        random = new Random();
    }

    @Override
    Move deliverMove() {
        return PlayStrategy.heavyPlay(board);
    }
}
