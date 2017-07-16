package towerwarspp.io;

import towerwarspp.preset.Requestable;

/**
 * Interface {@link IO} which extends from {@link Requestable} and {@link View}.
 *
 * This Interface is used by the Class {@link towerwarspp.main.game.Game} so that the Game
 * can use the Methods of the Interfaces {@link Requestable} and {@link View}.
 *
 * @author Robin Hundt, Kai Kuhlmann
 * @version 0.2 July 14th 2017
 */
public interface IO extends Requestable, View {

}