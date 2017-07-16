package towerwarspp.board;

import towerwarspp.preset.Position;

/**
 * This class represents a hexagon field on a hexagon board.
 * It helps to calculate the coordinates of the hexagon's neighbours as well as to find positions located
 * on some particular distance in a given direction from the current hexagon.
 *
 * @author Anastasiia Kysliak
 * @version 15-07-17
 */
public class Hexagon {
    /**
     * The current coordinates.
     */
    private int x, y;

    /**
     * Creates a new instance of {@link Hexagon} class with the given x- and y-coordinates.
     *
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public Hexagon(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new instance of {@link Hexagon} class with the same coordinates as the given {@link Hexagon} object.
     *
     * @param coordinates another haxagon whose x- and y-coordinates will be copied for this object.
     */
    public Hexagon(Hexagon coordinates) {
        this(coordinates.x, coordinates.y);
    }

    /**
     * Creates a new instance of {@link Hexagon} class using the coordinates stored in the given {@link Position} object.
     *
     * @param pos position whose letter and number values will be stored as x- and y-coordinates in this object.
     */
    public Hexagon(Position pos) {
        this(pos.getLetter(), pos.getNumber());
    }

    /**
     * Returns the current x-coordinate.
     *
     * @return the current x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current y-coordinate.
     *
     * @return the current y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Adds the coordinates of the other haxagon to the x- and y-coordinates stored in this object.
     *
     * @param other the other {@link Hexagon} object whose coordinates have to be added to the actual object's coordinates.
     */
    public void add(Hexagon other) {
        this.x += other.x;
        this.y += other.y;
    }

    /**
     * Multiplies the coordinates of this haxagon with the given scale.
     *
     * @param scale the value which the actual haxagon's coordinates have to be multiplied with.
     */
    public void scale(int scale) {
        this.x *= scale;
        this.y *= scale;
    }
}
