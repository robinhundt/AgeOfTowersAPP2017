package towerwarspp.io;

import towerwarspp.main.AgeOfTowers;

import javax.swing.*;
import java.awt.*;

/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * @version 0.2 June 29th 2017
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
        this.setTitle("Age of Towers");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void paintGrid(Graphics graphics) {
        Corner[] corner = null;
        Graphics2D graphics2D = (Graphics2D) graphics;
        corner = hexagons[0][0].getCorners();
        //graphics2D.drawLine(corner[0].getX(), corner[0].getY(), corner[1].getX(), corner[1].getY());
    }
}
