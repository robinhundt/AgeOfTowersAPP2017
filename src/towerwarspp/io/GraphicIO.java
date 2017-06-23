package towerwarspp.io;

import towerwarspp.preset.Move;
import towerwarspp.preset.Requestable;

/**
 * Class {@link GraphicIO} control the in- and output over the graphical Interface
 *
 * @version 0.1 20th june 2017
 * @author Kai Kuhlmann
 */
public class GraphicIO implements Requestable {

    /**
     * Request an Move from the player and converts to Move
     * @return Move of the Player
     * @throws Exception if Move has wrong Format
     */
    @Override
    public Move deliver() throws Exception {
        return null;
    }
}
