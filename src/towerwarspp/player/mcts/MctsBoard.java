package towerwarspp.player.mcts;

import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by robin on 16.07.17.
 */
public interface MctsBoard<E extends Action> extends Iterable<E>{

    public Status execute(E action);

    public Collection<E> possibleActions(PlayerColor playerColor);

    public Status getStatus();

    public MctsBoard<E> clone();

    public PlayerColor getTurn();
}
