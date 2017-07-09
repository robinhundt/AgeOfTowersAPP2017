package towerwarspp.io;

import towerwarspp.preset.Position;

/**
 * Class {@link Hexagon} creates a single Hexagon
 *
 * @version 0.3 July 03th 2017
 * @author Kai Kuhlmann
 */
public class Hexagon {
    /**
     * Private x and y Coordinate(not pixels)
     */
    private Position position;
    /**
     * Private Array of corners
     */
    private Corner[] corners;
    /**
     * Private center of the Hexagon
     */
    private Center center;

    /**
     * Constructor
     * @param x X-Coordinate of center
     * @param y Y-Coordinate of center
     * @param size the radius from center to corner
     * @param position Position of the Hexagon
     */
    public Hexagon (int x, int y, int size, Position position) {
        setCenter(x, y);
        setConers(this.center, size);
        setPosition(position);
    }

    /**
     * Setter of the center of the Hexagon
     * @param x X-Coordinate of center
     * @param y Y-Coordinate of center
     */
    private void setCenter(int x, int y) {
        this.center = new Center(x, y);
    }

    /**
     * Setter of the corners of the Hexagon
     * @param center The Center of the Hexagon
     * @param size the radius from center to corner
     */
    private void setConers(Center center, int size) {
        this.corners = new Corner[size];
        for (int i = 0; i < 6; ++i) {
            this.corners[i] = new Corner(center, size, i);
        }
    }

    /**
     * Updates the Hexagon
     * @param x X-Coordinate of the Center of the Hexagon
     * @param y Y-Coordinate of the Center of the Hexagon
     * @param size the radius from center to corner
     */
    public void updateHexagon(int x, int y, int size) {
        for(int i = 0; i < 6; ++i) {
            this.center.updateCenter(x, y);
            this.corners[i].updateCorner(this.center, size, i);
        }
    }

    /**
     * Setter of the position of the Hexagon
     * @param position Position of the Hexagon
     */
    private void setPosition(Position position){
        this.position = position;
    }

    /**
     * Getter for Center
     */
    public Center getCenter() {
        return this.center;
    }

    /**
     * Getter of the Corners of the Hexagon
     * @return Retruns an Array of the corners of the Hexagon
     */
    public Corner[] getCorners() {
        return this.corners;
    }

    /**
     * Getter of the Position of the Hexagon
     * @return Returns the Position of the Hexagon
     */
    public Position getPosition() {
        return this.position;
    }
}

/**
 * Class {@link Corner} creates a single Corner
 */
class Corner {
    /**
     * Private x and y Coordinate of the Corner
     */
    private int x, y;

    /**
     * Constructor
     */
    public Corner(Center center, int size, int i) {
        int[] coordinates = calculateCorner(center, size, i);
        setX(coordinates[0]);
        setY(coordinates[1]);
    }

    /**
     * Setter of the X-Coordinate of the Corner
     * @param x X-Coordinate of the Corner
     */
    private void setX(int x) {
        this.x = x;
    }

    /**
     * Setter of the Y-Coordinate of the Corner
     * @param y Y-Coordinate of the Corner
     */
    private void setY(int y) {
        this.y = y;
    }

    /**
     * Getter of the X-Coordinate of the Corner
     * @return X-Coordinate of the Corner
     */
    public int getX() {
        return this.x;
    }

    /**
     * Getter of the Y-Coordinate of the Corner
     * @return Y-Coordinate of the Corner
     */
    public int getY() {
        return this.y;
    }

    /**
     * Updates the Corner of the Hexagon
     * @param center updated Center
     * @param size the radius from center to corner
     * @param i position
     */
    public void updateCorner(Center center, int size, int i) {
        int[] coordinates = calculateCorner(center, size, i);
        setX(coordinates[0]);
        setY(coordinates[1]);
    }

    /**
     * Calculates the Coordinates of the corner
     * @param center updated Center
     * @param size the radius from center to corner
     * @param i position
     * @return the Coordinates of the Corner
     */
    private int[] calculateCorner(Center center, int size, int i) {
        /*-- 6 Corners (60) because of corner top + 30 --*/
        int angle_degree = 60 * i + 30;
        double angle_radius = Math.PI / 180 * angle_degree;
        Double x = center.getX() + size * Math.cos(angle_radius);
        Double y = center.getY() + size * Math.sin(angle_radius);
        int coordinates[] =  {x.intValue(), y.intValue()};
        return coordinates;
    }
}

/**
 * Class {@link Center} creates a single Centerpoint
 */
class Center {
    /**
     * Private x and y Coordinate of the Center
     */
    private int x, y;

    /**
     * Constructor
     * @param x X-Coordinate of the Center
     * @param y Y-Coordinate of the Center
     */
    public Center(int x, int y) {
        setX(x);
        setY(y);
    }

    /**
     * Updates the Center of the Hexagon
     * @param x X-Coordinate of the Center
     * @param y Y-Coordinate of the Center
     */
    public void updateCenter(int x, int y) {
        setX(x);
        setY(y);
    }

    /**
     * Setter of the X-Coordinate of the Center
     * @param x X-Coordinate of the Center
     */
    private void setX(int x) {
        this.x = x;
    }

    /**
     * Setter of the Y-Coordinate of the Center
     * @param y Y-Coordinate of the Center
     */
    private void setY(int y) {
        this.y = y;
    }

    /**
     * Getter of the X-Coordinate of the Center
     * @return X-Coordinate of the Center
     */
    public int getX() {
        return this.x;
    }

    /**
     * Getter of the Y-Coordinate of the Center
     * @return Y-Coordinate of the Center
     */
    public int getY() {
        return this.y;
    }
}
