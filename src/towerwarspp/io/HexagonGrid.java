package towerwarspp.io;

import towerwarspp.preset.Position;
import javax.swing.*;
import java.awt.*;

/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * @version 0.3 July 03th 2017
 * @author Kai Kuhlmann
 */
public class HexagonGrid extends JFrame {
    private Hexagon[][] hexagons;


    public HexagonGrid(int size) {
        this.hexagons = new Hexagon[size][size];
        this.hexagons[1][1] = new Hexagon(1, 1, 20, new Position(1, 1));


        this.setTitle("Age of Towers");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        paintGrid(size);
        this.setVisible(true);
    }

    private void paintGrid(int size) {
        Polygon polygon = new Polygon();
        Corner[] corners = this.hexagons[1][1].getCorners();
        for(int i = 0; i < 6; ++i) {
            polygon.addPoint(corners[i].getX(), corners[i].getY());
        }
    }
}
