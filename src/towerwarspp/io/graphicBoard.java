package towerwarspp.io;

import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
import towerwarspp.preset.Viewer;

import java.util.List;

/**
 *
 */
public class graphicBoard implements Viewer {
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

    @Override
    public int getStones() {
        return 0;
    }

    @Override
    public List getPossibleMoves(Position position) {
        return null;
    }
}
