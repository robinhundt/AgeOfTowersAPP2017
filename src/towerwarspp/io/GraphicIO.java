package towerwarspp.io;

import towerwarspp.board.BViewer;
import towerwarspp.preset.Move;

import javax.swing.*;

/**
 * Class {@link GraphicIO} creates the graphic in- output
 *
 * @version 0.3 July 03th 2017
 * @author Kai Kuhlmann
 */
public class GraphicIO extends JFrame implements IO {

    private BViewer viewer;
    private HexagonGrid hexagonGrid;

    public GraphicIO(BViewer viewer) {
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
    private int getBoardSize() {
        //return viewer.getSize();
        return 10;
    }

    @Override
    public void visualize() {
        this.hexagonGrid = new HexagonGrid(getBoardSize(), 20);
        this.setTitle("Age of Towers");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.add(this);
        this.setVisible(true);
    }

    @Override
    public Move deliver() throws Exception {
        return null;
    }
}
