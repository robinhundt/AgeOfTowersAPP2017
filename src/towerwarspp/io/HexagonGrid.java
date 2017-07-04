package towerwarspp.io;

import towerwarspp.preset.Position;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.swing.*;
import java.awt.*;

/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * @version 0.4 July 04th 2017
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
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
        int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
        for(int y = 1; y <= boardSize; ++y) {
            for(int x = 1; x <= boardSize; ++x) {
                System.out.println("x: " + x + " size: " + polySize);
                this.hexagons[x][y] = new Hexagon(y * (2 * distance) + (x - 1) * distance, x * polySize + (x - 1) * (polySize / 2), polySize, new Position(y, x));

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
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                for(int y = 1; y <= boardSize; ++y) {
                    for(int x = 1; x <= boardSize; ++x){
                        g.setColor(Color.BLACK);
                        g.drawPolygon(polygon[x][y]);
                        g.setColor(Color.BLUE);
                        g.fillOval((y * (2 * distance) + (x - 1) * distance) - (polySize / 2), (x * polySize + (x - 1) * (polySize / 2)) - (polySize / 2), polySize - (polySize / 32), polySize - (polySize / 32));
                        g.setColor(Color.BLACK);
                        g.drawOval((y * (2 * distance) + (x - 1) * distance) - (polySize / 2), (x * polySize + (x - 1) * (polySize / 2)) - (polySize / 2), polySize - (polySize / 32), polySize - (polySize / 32));
                        g.setColor(Color.WHITE);
                        char[] height = {'1'};
                        g.drawChars(height, 0, height.length, y * (2 * distance) + (x - 1) * distance, x * polySize + (x - 1) * (polySize / 2));
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                return new Dimension((boardSize - 1) / 2 * (2 * distance) * boardSize, 2 * polySize * boardSize);
            }
        };
        jFrame.add(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
