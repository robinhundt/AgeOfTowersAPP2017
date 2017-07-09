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
        this.polygonSize = polySize;
        this.hexagons = new Hexagon[boardSize + 1][boardSize + 1];
        this.polygon = new Polygon[boardSize + 1][boardSize + 1];
        for(int y = 1; y <= boardSize; ++y) {
            for(int x = 1; x <= boardSize; ++x) {
                int [] coordinates = calculateCenterCoordinates(x, y);
                this.hexagons[x][y] = new Hexagon(coordinates[0], coordinates[1], this.polygonSize, new Position(x, y));
                setPolygon(x, y);
            }

        }
        System.out.println(this.hexagons.length);
    }

    public int getPolygonSize() {
        return this.polygonSize;
    }

    public void updatePolygonSize(int polySize) {
        this.polygonSize = polySize;
        for(int y = 1; y < this.hexagons.length; ++y) {
            for (int x = 1; x < this.hexagons.length; ++x) {
                int [] coordinates = calculateCenterCoordinates(x, y);
                this.hexagons[x][y].updateHexagon(coordinates[0], coordinates[1], this.polygonSize);
                setPolygon(x, y);
            }
        }
    }

    private int[] calculateCenterCoordinates(int x, int y) {
        int distance = (int) (Math.cos(Math.toRadians(30.0)) * this.polygonSize);
        int [] coordinates = {x * (2 * distance) + (y - 1) * distance + (distance / 2), y * this.polygonSize + (y - 1) * (this.polygonSize / 2) + distance};
        return coordinates;
    }

    private void setPolygon(int x, int y) {
        Corner[] corners = this.hexagons[x][y].getCorners();

        int xPolygon[] = new int[6];
        int yPolygon[] = new int[6];

        for (int i = 0; i < 6; ++i) {
            xPolygon[i] = corners[i].getX();
            yPolygon[i] = corners[i].getY();
        }

        this.polygon[x][y] = new Polygon(xPolygon, yPolygon, 6);
    }

    public Center getCenter(int x, int y) {
        return this.hexagons[x][y].getCenter();
    }

    public Polygon[][] getPolygon() {
        return this.polygon;
    }
}
