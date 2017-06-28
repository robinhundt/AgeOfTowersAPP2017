package towerwarspp.io;

import javax.swing.*;

/**
 * Class {@link Hexagon} creates a single Hexagon
 *
 * @version 0.1 June 28th 2017
 * @author Kai Kuhlmann
 */
public class Hexagon extends JPanel{
    private int x, y;
    private Corner[] coners;
    private Center center;

    private Hexagon (int x, int y, int size) {
        setCenter(x, y);
        setConers(this.center, size);
    }

    private void setCenter(int x, int y) {
        this.center = this.center.getCenter(x, y);
    }

    private void setConers(Center center, int size) {
        for (int i = 0; i < 6; ++i) {
            this.coners[i] = this.coners[i].getCorner(center, size, i);
        }
    }

    public Hexagon getHexagon(int x, int y, int size) {
        return new Hexagon(x, y, size);
    }
}

/**
 * Class {@link Corner} creates a single Corner
 */
class Corner {
    private double x, y;

    private Corner(double x, double y) {
        setX(x);
        setY(y);
    }

    private void setX(double x) {
        this.x = x;
    }

    private void setY(double y) {
        this.y = y;
    }

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
    private int x, y;

    private Center(int x, int y) {
        setX(x);
        setY(y);
    }

    private void setX(int x) {
        this.x = x;
    }

    private void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Center getCenter(int x, int y) {
        return new Center(x, y);
    }
}
