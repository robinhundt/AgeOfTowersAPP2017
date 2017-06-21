package towerwarspp.io;

import towerwarspp.preset.Status;
import towerwarspp.preset.Viewer;

/**
 * 
 */
public class textOutput implements Viewer {
    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getTurn() {
        return 0;
    }

    @Override
    public Status getStatus() {
        return null;
    }
}
