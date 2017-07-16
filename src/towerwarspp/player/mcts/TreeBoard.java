package towerwarspp.player.mcts;

import towerwarspp.board.Board;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by robin on 16.07.17.
 */
public class TreeBoard extends Board implements MctsBoard<MoveAction> {

    public TreeBoard(int size) {
        super(size);
    }

    @Override
    public Status execute(MoveAction moveAction) {
        makeMove(moveAction);
        return null;
    }

    @Override
    public Collection<MoveAction> possibleActions(PlayerColor playerColor) {
        return null;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @Override
    public TreeBoard clone() {
        return null;
    }

    @Override
    public PlayerColor getTurn() {
        return null;
    }


    @Override
    public Iterator<MoveAction> iterator() {
        return null;
    }
}
