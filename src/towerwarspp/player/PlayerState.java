package towerwarspp.player;

/**
 * Created by robin on 21.06.17.
 */
public enum PlayerState {
    INIT, REQUEST, CONFIRM, UPDATE;

    public PlayerState next() {
        if(this.equals(INIT))
            return REQUEST;
        else if(this.equals(REQUEST))
            return CONFIRM;
        else if(this.equals(CONFIRM))
            return UPDATE;
        else
            return REQUEST;
    }
}
