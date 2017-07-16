package towerwarspp.player.mcts;

import towerwarspp.preset.Move;

/**
 * Created by robin on 16.07.17.
 */
public class MoveAction extends Move implements Action {

    public MoveAction(MoveAction action) {
        super(action);
    }
}
