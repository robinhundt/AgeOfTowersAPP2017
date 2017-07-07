package towerwarspp.io;

import towerwarspp.preset.Position;

import java.awt.*;

/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * @version 0.6 July 07th 2017
 * @author Kai Kuhlmann
 */
public class HexagonGrid {
    private Hexagon[][] hexagons;
    private Polygon[][] polygon;
    private int polygonSize;

    public HexagonGrid(int boardSize, int polySize) {
        if(polySize < 20) {
            this.polygonSize = 20;
        } else {
            this.polygonSize = polySize;
        }
        this.hexagons = new Hexagon[boardSize + 1][boardSize + 1];
        this.polygon = new Polygon[boardSize + 1][boardSize + 1];
        int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
        for(int y = 1; y <= boardSize; ++y) {
            for(int x = 1; x <= boardSize; ++x) {
                this.hexagons[x][y] = new Hexagon(x * (2 * distance) + (y - 1) * distance, y * this.polygonSize + (y - 1) * (this.polygonSize / 2), this.polygonSize, new Position(x, y));

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
    }

    public int getPolygonSize() {
        return this.polygonSize;
    }

    public Polygon[][] getPolygon() {
        return this.polygon;
    }
}
