package towerwarspp.io;
import java.awt.*;
import towerwarspp.main.Debug;
import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.debug.DebugSource;
import towerwarspp.preset.Position;
/**
 * Class {@link HexagonGrid} creates the Grid of the Hexagons
 *
 * It calculates the Center coordinates of each {@link Hexagon}
 *
 * The Class creates the Arrays of {@link Polygon} which can be drawn in the {@link GraphicIO}
 *
 * @version 0.7 July 14th 2017
 * @author Kai Kuhlmann
 */
public class HexagonGrid {
    /**
     * Array of {@link Hexagon}
     */
    private Hexagon[][] hexagons;
    /**
     * Array of drawable {@link Polygon} which are the {@link HexagonGrid}
     */
    private Polygon[][] polygon;
    /**
     * Array of drawable {@link Polygon} which are the {@link Hexagon} on which are the
     * Tokens of the Player
     */
    private Polygon[][] markedPolygon;
    /**
     * The Size of each {@link Polygon} and {@link Hexagon}
     */
    private int polygonSize;
    /**
     * Debug Instance of {@link Debug}
     */
    private Debug debugInstance;
    /**
     * The Constructor of the {@link HexagonGrid}
     *
     * Set the Size of each {@link Polygon} and {@link Hexagon}
     *
     * Initialize the Arrays for the {@link Polygon} and {@link Hexagon}
     *
     * Starts the calculation of the coordinates of the {@link Hexagon.Center}
     *
     * Creates each {@link Hexagon} and saves them in the Array of {@link Hexagon}
     *
     * @param boardSize The Size of the Board
     * @param polySize The Size of each {@link Polygon} or {@link Hexagon}
     */
    HexagonGrid(int boardSize, int polySize) {
        this.debugInstance = Debug.getInstance();
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
        this.debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "HexagonGrid created.");
    }
    /**
     * Setter for {@link Polygon}
     *
     * Creates a {@link Polygon} with six Edges based on the Corners of a {@link Hexagon}
     * and creates the {@link Polygon} which are the {@link Hexagon} on which are Tokens
     *
     * @param x X-Coordinate of the {@link HexagonGrid}
     * @param y Y-Coordinate of the {@link HexagonGrid}
     */
    private void setPolygon(int x, int y) {
        Hexagon.Corner[] corners = this.hexagons[x][y].getCorners();
        int xPolygon[] = new int[6];
        int yPolygon[] = new int[6];
        int xMarkedPolygon[] = new int[6];
        int yMarkedPolygon[] = new int[6];
        /*
        Remove 4 pixel from each Corner to get an inner Polygon
         */
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
        this.debugInstance.send(DebugLevel.LEVEL_6, DebugSource.IO, "Polygon created. Coordinates (" + x + ", " + y + ")");
    }
    /**
     * Getter for the Array of {@link Polygon} for drawing
     * @return Array of {@link Polygon}
     */
    Polygon[][] getPolygon() {
        return this.polygon;
    }
    /**
     * Getter for the Array of {@link Polygon} which are the marked Polygons on which are Tokens
     * @return Array of {@link Polygon}
     */
    Polygon[][] getMarkedPolygon() {
        return this.markedPolygon;
    }
    void updatePolygonSize(int polySize) {
        this.polygonSize = polySize;
        for(int y = 1; y < this.hexagons.length; ++y) {
            for (int x = 1; x < this.hexagons.length; ++x) {
                int [] coordinates = calculateCenterCoordinates(x, y);
                this.hexagons[x][y].updateHexagon(coordinates[0], coordinates[1], this.polygonSize);
                setPolygon(x, y);
                this.debugInstance.send(DebugLevel.LEVEL_7, DebugSource.IO, "Polygon (" + x + ", " + y + ") updated.");
            }
        }
        this.debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "All Polygon updated.");
    }
    /**
     * Get an {@link Hexagon.Center} on the specific coordinates
     * @param x X-Coordinate of the {@link HexagonGrid}
     * @param y Y-Coordinate of the {@link HexagonGrid}
     * @return the {@link Hexagon.Center}
     */
    Hexagon.Center getCenter(int x, int y) {
        return this.hexagons[x][y].getCenter();
    }
    /**
     * Calculates the Coordinates in Pixel of the {@link Hexagon.Center} based on the Coordinates on the {@link HexagonGrid}
     * @param x X-Coordinate of the {@link HexagonGrid}
     * @param y Y-Coordinate of the {@link HexagonGrid}
     * @return Array of the X and Y coordinates of the Center in Pixel
     */
    private int[] calculateCenterCoordinates(int x, int y) {
        int [] coordinates = new int[2];
        int distance = (int) (Math.cos(Math.toRadians(30.0)) * this.polygonSize);
        coordinates[0] = x * (2 * distance) + (y - 1) * distance + (distance / 2);
        coordinates[1] = y * this.polygonSize + (y - 1) * (this.polygonSize / 2) + distance;
        this.debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "Center coordinate claculated. (" + x + ", " + y +")");
        return coordinates;
    }
}