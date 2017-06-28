package towerwarspp.io;

import javax.swing.*;

/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * @version 0.1 June 28th 2017
 * @author Kai Kuhlmann
 */
public class HexagonGrid extends JFrame {
    private Hexagon[][] hexagons;

    private HexagonGrid(int size) {
        for(int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                this.hexagons[row][col] = this.hexagons[row][col].getHexagon(row, col, 20);
            }
        }
    }
}
