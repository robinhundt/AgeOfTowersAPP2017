package towerwarspp.io;

import towerwarspp.preset.Move;
import towerwarspp.preset.Requestable;
import towerwarspp.preset.Viewer;

/**
 * Class {@link GraphicIO} creates the graphic in- output
 *
 * @version 0.2 20th June 2017
 * @author Kai Kuhlmann
 */
public class GraphicIO implements Requestable {

    private BoardViewer viewer;

    public GraphicIO(BoardViewer viewer) {
        this.viewer = viewer;
    }

    public GraphicIO() {}

    /**
     * Checks if is Stone
     * @return  True if Token is stone, otherwise false
     */
    private boolean isStone() {
        return false;
    }

    /**
     * Checks if Stone is Tower
     * @return True if Stone is Tower, otherwise false
     */
    private boolean isTower() {
        return false;
    }

    /**
     * Checks if Stone is blocked
     * @return True if Stone is vlocked, otherwise false
     */
    private boolean isBlocked() {
        return false;
    }

    /**
     * Get the size of the Board
     * @return Size of Board
     */
    private int getSize() {
        //return viewer.getSize();
        return 10;
    }

    @Override
    public Move deliver() throws Exception {
        return null;
    }
}
