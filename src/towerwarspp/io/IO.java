package towerwarspp.io;

import towerwarspp.preset.Requestable;

/**
 * Created by robin on 03.07.17.
 */
public interface IO extends Requestable {
    public void visualize();
    public void setDebug(boolean isDebug);
}
