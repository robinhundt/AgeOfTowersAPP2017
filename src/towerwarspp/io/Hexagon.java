package towerwarspp.io;

import towerwarspp.preset.Position;
import towerwarspp.util.debug.Debug;
import towerwarspp.util.debug.DebugLevel;
import towerwarspp.util.debug.DebugSource;

/**
 * Class {@link Hexagon} creates a single Hexagon
 *
 * This Class creates an Object of {@link Center} in which are the Coordinates of the Center.
 * The Class creates also an Object of {@link Corner} which calculates the {@link Corner} based on the {@link Center}.
 *
 * @author Kai Kuhlmann
 * @version 0.4 July 14th 2017
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
     *
     * Set the {@link Center}, {@link Corner} and the {@link Position}.
     *
     * @param x        X-Coordinate of center
     * @param y        Y-Coordinate of center
     * @param size     the radius from center to corner
     * @param position Position of the Hexagon
     */
    Hexagon(int x, int y, int size, Position position) {
        Debug debugInstance = Debug.getInstance();
        setCenter(x, y);
        setCorners(this.center, size);
        setPosition(position);
        debugInstance.send(DebugLevel.LEVEL_7, DebugSource.IO, "Hexagon created on Coordinates (" + x + ", " + y + ")");
    }

    /**
     * Getter of the Position of the Hexagon.
     *
     * @return Returns the Position of the Hexagon
     */
    public Position getPosition() {
        return this.position;
    }

    /**
     * Setter of the position of the Hexagon.
     *
     * @param position Position of the Hexagon
     */
    private void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Getter for Center.
     *
     * @return the Center of the Hexagon
     */
    Center getCenter() {
        return this.center;
    }

    /**
     * Getter of the Corners of the Hexagon.
     *
     * @return Retruns an Array of the corners of the Hexagon
     */
    Corner[] getCorners() {
        return this.corners;
    }

    /**
     * Setter of the center of the Hexagon.
     *
     * @param x X-Coordinate of center
     * @param y Y-Coordinate of center
     */
    private void setCenter(int x, int y) {
        this.center = new Center(x, y);
    }

    /**
     * Setter of the corners of the Hexagon.
     *
     * @param center The Center of the Hexagon
     * @param size   the radius from center to corner
     */
    private void setCorners(Center center, int size) {
        this.corners = new Corner[6];
        for (int i = 0; i < 6; ++i) {
            this.corners[i] = new Corner(center, size, i);
        }
    }

    /**
     * Updates the Hexagon.
     *
     * @param x    X-Coordinate of the Center of the Hexagon
     * @param y    Y-Coordinate of the Center of the Hexagon
     * @param size the radius from center to corner
     */
    void updateHexagon(int x, int y, int size) {
        for (int i = 0; i < 6; ++i) {
            this.center.updateCenter(x, y);
            this.corners[i].updateCorner(this.center, size, i);
        }
    }

    /**
     * Class {@link Corner} creates a single Corner.
     */
    class Corner {
        /**
         * Private x and y Coordinate of the Corner
         */
        private int x, y;

        /**
         * Constructor set the X and Y Coordinate of the Corner of the {@link Hexagon}.
         */
        Corner(Center center, int size, int i) {
            int[] coordinates = calculateCorner(center, size, i);
            setX(coordinates[0]);
            setY(coordinates[1]);
        }

        /**
         * Getter of the X-Coordinate of the Corner.
         *
         * @return X-Coordinate of the Corner
         */
        int getX() {
            return this.x;
        }

        /**
         * Setter of the X-Coordinate of the Corner.
         *
         * @param x X-Coordinate of the Corner
         */
        private void setX(int x) {
            this.x = x;
        }

        /**
         * Getter of the Y-Coordinate of the Corner.
         *
         * @return Y-Coordinate of the Corner
         */
        int getY() {
            return this.y;
        }

        /**
         * Setter of the Y-Coordinate of the Corner.
         *
         * @param y Y-Coordinate of the Corner
         */
        private void setY(int y) {
            this.y = y;
        }

        /**
         * Calculates the Coordinates of the corner.
         *
         * @param center updated Center
         * @param size   the radius from center to corner
         * @param i      position
         * @return the Coordinates of the Corner
         */
        private int[] calculateCorner(Center center, int size, int i) {
            int coordinates[] = new int[2];
            /*-- 6 Corners (60) because of corner top + 30 --*/
            int angle_degree = 60 * i + 30;
            double angle_radius = Math.PI / 180 * angle_degree;
            Double x = center.getX() + size * Math.cos(angle_radius);
            Double y = center.getY() + size * Math.sin(angle_radius);
            coordinates[0] = x.intValue();
            coordinates[1] = y.intValue();
            return coordinates;
        }

        /**
         * Updates the Corner of the Hexagon.
         *
         * @param center updated Center
         * @param size   the radius from center to corner
         * @param i      position
         */
        void updateCorner(Center center, int size, int i) {
            int[] coordinates = calculateCorner(center, size, i);
            setX(coordinates[0]);
            setY(coordinates[1]);
        }
    }

    /**
     * Class {@link Center} creates a single Center.
     */
    class Center {
        /**
         * Private x and y Coordinate of the Center
         */
        private int x, y;

        /**
         * Constructor
         *
         * @param x X-Coordinate of the Center
         * @param y Y-Coordinate of the Center
         */
        Center(int x, int y) {
            setX(x);
            setY(y);
        }

        /**
         * Getter of the X-Coordinate of the Center.
         *
         * @return X-Coordinate of the Center
         */
        int getX() {
            return this.x;
        }

        /**
         * Setter of the X-Coordinate of the Center.
         *
         * @param x X-Coordinate of the Center
         */
        private void setX(int x) {
            this.x = x;
        }

        /**
         * Getter of the Y-Coordinate of the Center.
         *
         * @return Y-Coordinate of the Center
         */
        int getY() {
            return this.y;
        }

        /**
         * Setter of the Y-Coordinate of the Center.
         *
         * @param y Y-Coordinate of the Center
         */
        private void setY(int y) {
            this.y = y;
        }

        /**
         * Updates the Center of the Hexagon.
         *
         * @param x X-Coordinate of the Center
         * @param y Y-Coordinate of the Center
         */
        void updateCenter(int x, int y) {
            setX(x);
            setY(y);
        }
    }
}