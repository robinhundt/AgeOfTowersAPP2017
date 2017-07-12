package towerwarspp.io;

import towerwarspp.preset.Position;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * @version 0.6 July 07th 2017
 * @author Kai Kuhlmann
 */
public class HexagonGrid {
    private Hexagon[][] hexagons;
    private Polygon[][] polygon;
    private Polygon[][] markedPolygon;
    private int polygonSize;

    public HexagonGrid(int boardSize, int polySize) {
        this.polygonSize = polySize;
        this.hexagons = new Hexagon[boardSize + 1][boardSize + 1];
        this.polygon = new Polygon[boardSize + 1][boardSize + 1];
        this.markedPolygon = new Polygon[boardSize + 1][boardSize + 1];
        for(int y = 1; y <= boardSize; ++y) {
            for(int x = 1; x <= boardSize; ++x) {
                int[] coordinates = calculateCenterCoordinates(x, y);
                this.hexagons[x][y] = new Hexagon(coordinates[0], coordinates[1], this.polygonSize, new Position(x, y));
                setPolygon(x, y);
            }

        }
    }

    public Shape getPointedShape(int arrayX, int arrayY) {
        Corner[] corners = this.hexagons[arrayX][arrayY].getCorners();
        GeneralPath p = new GeneralPath();
        for (int i = 0; i < 6; i++) {
            int x = corners[i].getX();
            int y = corners[i].getY();
            if (i == 0) {
                p.moveTo(x, y);
            } else {
                p.lineTo(x, y);
            }
        }
        p.closePath();

        return p;
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
        int [] coordinates = new int[2];
        int distance = (int) (Math.cos(Math.toRadians(30.0)) * this.polygonSize);
        coordinates[0] = x * (2 * distance) + (y - 1) * distance + (distance / 2);
        coordinates[1] = y * this.polygonSize + (y - 1) * (this.polygonSize / 2) + distance;
        return coordinates;
    }

    private void setPolygon(int x, int y) {
        Corner[] corners = this.hexagons[x][y].getCorners();

        int xPolygon[] = new int[6];
        int yPolygon[] = new int[6];
        int xMarkedPolygon[] = new int[6];
        int yMarkedPolygon[] = new int[6];

        for (int i = 0; i < 6; ++i) {
            xPolygon[i] = corners[i].getX();
            yPolygon[i] = corners[i].getY();
            switch (i) {
                case 0:
                    xMarkedPolygon[i] = corners[i].getX() - 4;
                    yMarkedPolygon[i] = corners[i].getY() - 4;
                    break;
                case 1:
                    xMarkedPolygon[i] = corners[i].getX();
                    yMarkedPolygon[i] = corners[i].getY() - 4;
                    break;
                case 2:
                    xMarkedPolygon[i] = corners[i].getX() + 4;
                    yMarkedPolygon[i] = corners[i].getY() - 4;
                    break;
                case 3:
                    xMarkedPolygon[i] = corners[i].getX() + 4;
                    yMarkedPolygon[i] = corners[i].getY() + 4;
                    break;
                case 4:
                    xMarkedPolygon[i] = corners[i].getX();
                    yMarkedPolygon[i] = corners[i].getY() + 4;
                    break;
                case 5:
                    xMarkedPolygon[i] = corners[i].getX() - 4;
                    yMarkedPolygon[i] = corners[i].getY() + 4;
                    break;
            }
        }

        this.markedPolygon[x][y] = new Polygon(xMarkedPolygon, yMarkedPolygon, 6);
        this.polygon[x][y] = new Polygon(xPolygon, yPolygon, 6);
    }

    public Center getCenter(int x, int y) {
        return this.hexagons[x][y].getCenter();
    }

    public Polygon[][] getPolygon() {
        return this.polygon;
    }

    public Polygon[][] getMarkedPolygon() {
        return this.markedPolygon;
    }
}
