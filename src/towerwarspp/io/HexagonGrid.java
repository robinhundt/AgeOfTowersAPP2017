package towerwarspp.io;

import towerwarspp.preset.Position;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import java.awt.*;

/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * @version 0.3 July 03th 2017
 * @author Kai Kuhlmann
 */
public class HexagonGrid {
    private Hexagon[][] hexagons;
    private Polygon[][] polygon;
    private JFrame jFrame;
    private JPanel jPanel;

    public HexagonGrid(int boardSize, int polySize) {
        this.hexagons = new Hexagon[boardSize + 1][boardSize + 1];
        this.polygon = new Polygon[boardSize + 1][boardSize + 1];
        jFrame = new JFrame();
        jFrame.setResizable(false);
        jFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
        for(int y = 1; y <= boardSize; ++y) {
            for(int x = 1; x <= boardSize; ++x) {
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                System.out.println("x: " + x + " size: " + polySize);
                this.hexagons[x][y] = new Hexagon(y * (2 * distance) + (x - 1) * distance, x * polySize + (x - 1) * (polySize / 2), polySize, new Position(1, y));

                Corner[] corners = this.hexagons[x][y].getCorners();

                int xPolygon[] = new int[6];
                int yPolygon[] = new int[6];

                for (int i = 0; i < 6; ++i) {
                    xPolygon[i] = corners[i].getX();
                    yPolygon[i] = corners[i].getY();
                }

                this.polygon[x][y] = new Polygon(xPolygon, yPolygon, 6);
            }

        }
        jPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLUE);
                for(int y = 1; y <= boardSize; ++y) {
                    for(int x = 1; x <= boardSize; ++x){
                        g.drawPolygon(polygon[x][y]);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                return new Dimension((boardSize - 1) / 2 * (2 * distance), 2 * polySize * boardSize);
            }
        };
        jFrame.add(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
