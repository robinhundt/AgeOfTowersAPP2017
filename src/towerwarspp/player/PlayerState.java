package towerwarspp.player;

import towerwarspp.preset.Move;
import towerwarspp.preset.Player;
import towerwarspp.preset.Status;

/**
 * Enum class representing the different states a {@link towerwarspp.preset.Player} can be in.
 * Players PlayState should correspond to the method that is next in line to be called. E.g. if request() has been called
 * on a player, its PlayState should be {@link #CONFIRM} because the confirm() method has to be called next.
 * @author Alexander Wähling
 * @version 08-07-17
 */
public enum PlayerState {
    /**
     * Corresponds to the Player being in a state where it's {@link Player#request()} method must be called next.
     */
    REQUEST,
    /**
     * Corresponds to the Player being in a state where it's {@link Player#confirm(Status)} ()} method must be called next.
     */
    CONFIRM,
    /**
     * Corresponds to the Player being in a state where it's {@link Player#update(Move, Status)} ()} method must be called next.
     */
    UPDATE;

    /**
     * next() method always returns the next PlayerState depending on PlayState instance it is called on.
     * For example if next() is called on {@link #REQUEST} it will return {@link #CONFIRM} because confirm() always has to
     * be called after request().
     * @return next PlayState
     */
    public PlayerState next() {
        if(this.equals(REQUEST))
            return CONFIRM;
        else if(this.equals(CONFIRM))
            return UPDATE;
        else
            return REQUEST;
    }
}
