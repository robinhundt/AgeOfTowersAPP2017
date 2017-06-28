package towerwarspp.io;

import javax.swing.*;

/**
 * Class {@link Hexagon} creates a single Hexagon
 *
 * @version 0.2 June 29th 2017
 * @author Kai Kuhlmann
 */
public class Hexagon {
    /**
     * Private x and y Coordinate(not pixels)
     */
    private int x, y;
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
     * @param size Size of Hexagon
     */
    private Hexagon (int x, int y, int size) {
        setCenter(x, y);
        setConers(this.center, size);
    }

    /**
     * Setter of the center of the Hexagon
     * @param x X-Coordinate of center
     * @param y Y-Coordinate of center
     */
    private void setCenter(int x, int y) {
        this.center = this.center.getCenter(x, y);
    }

    /**
     * Setter of the corners of the Hexagon
     * @param center The Center of the Hexagon
     * @param size Size of the Hexagon
     */
    private void setConers(Center center, int size) {
        for (int i = 0; i < 6; ++i) {
            this.corners[i] = this.corners[i].getCorner(center, size, i);
        }
    }

    /**
     * Getter of the Corners of the Hexagon
     * @return Retruns an Array of the corners of the Hexagon
     */
    public Corner[] getCorners() {
        return this.corners;
    }

    /**
     * Getter of the Hexagon which initialize the Hexagon
     * @param x X-Coordinate of the center of the Hexagon
     * @param y Y-Coordinate of the center of the Hexagon
     * @param size Size of the Hexagon
     * @return Retuns the Hexagon
     */
    public Hexagon getHexagon(int x, int y, int size) {
        return new Hexagon(x, y, size);
    }
}

/**
 * Class {@link Corner} creates a single Corner
 */
class Corner {
    /**
     * Private x and y Coordinate of the Corner
     */
    private double x, y;

    /**
     * Constructor
     * @param x X-Coordinate of the Corner
     * @param y Y-Coordinate of the Corner
     */
    private Corner(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * Setter of the X-Coordinate of the Corner
     * @param x X-Coordinate of the Corner
     */
    private void setX(double x) {
        this.x = x;
    }

    /**
     * Setter of the Y-Coordinate of the Corner
     * @param y Y-Coordinate of the Corner
     */
    private void setY(double y) {
        this.y = y;
    }

    /**
     * Getter of the X-Coordinate of the Corner
     * @return X-Coordinate of the Corner
     */
    public double getX() {
        return this.x;
    }

    /**
     * Getter of the Y-Coordinate of the Corner
     * @return Y-Coordinate of the Corner
     */
    public double getY() {
        return this.y;
    }

    /**
     * Getter of the Corner
     * @param center Center of the Hexagon
     * @param size Size of the Hexagon
     * @param i Number of the Corner (beginning at 0)
     * @return the Corner
     */
    public Corner getCorner(Center center, int size, int i) {
        int angle_degree = 60 * i + 30;
        double angle_radius = Math.PI / 180 * angle_degree;
        return new Corner(center.getX() + size * Math.cos(angle_radius),
                center.getY() + size * Math.sin(angle_radius));
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
    private Center(int x, int y) {
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

    /**
     * Getter of the Center
     * @param x X-Coordinate of the Center
     * @param y Y-Coordinate of the Center
     * @return the Center
     */
    public Center getCenter(int x, int y) {
        return new Center(x, y);
    }
}
