package towerwarspp.io;

import java.util.Scanner;

import towerwarspp.preset.*;

/**
 * Class {@link TextIO} control the Input over the Command Line Interface
 *
 * @version 0.5 June 29th 2017
 * @author Kai Kuhlmann
 */
public class TextIO implements Requestable {
    /**
     * Normal Command Line Color
     */
    private static final String RESET = "\u001B[0m";
    /**
     * Command Line Color = Red
     */
    private static final String RED = "\u001B[31m";
    /**
     * Command Line Color = Blue
     */
    private static final String BLUE = "\u001B[34m";
    /**
     * Viewer-Object
     */
    private BoardViewer viewer;

    /**
     * Private Scanner-Object
     */
    private Scanner scanner;

    /**
     * Constructor to Initialize the TextIO
     * @param viewer Object of Type Viewer
     */
    public TextIO(BoardViewer viewer) {
        this.viewer = viewer;
        this.scanner = new Scanner(System.in);
    }

    /**
     * just for testing
     */
    public TextIO() {}

    /**
     * Checks if is Stone
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return  True if Token is stone, otherwise false
     */
    private boolean isStone(int x, int y) {
        return this.viewer.isStone(x, y);
    }

    /**
     * Checks if Stone is Tower
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return True if Stone is Tower, otherwise false
     */
    private boolean isTower(int x, int y) {
        return this.viewer.isTower(x, y);
    }

    /**
     * Checks if Stone is blocked
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return True if Stone is vlocked, otherwise false
     */
    private boolean isBlocked(int x, int y) {
        return this.viewer.isBlocked(x, y);
    }

    /**
     * Get the height of a Tower
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return height of Tower
     */
    private int getTowerHeight(int x, int y) {
        return this.viewer.getHeight(x, y);
    }

    /**
     * Get owner of Tower
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return Playercolor
     */
    private PlayerColor getPlayerColor(int x, int y) {
        return this.viewer.getPlayerColor(x, y);
    }

    /**
     * Get the size of the Board
     * @return Size of Board
     */
    private int getSize() {
        return this.viewer.getSize();
    }

    /**
     * Output of the Board
     */
    public void visualize() {
        int size = getSize();
        char headChar = 'A';
        System.out.print("    ");
        for(int top = 0; top < size; ++top) {
            System.out.print(headChar + "   ");
            ++headChar;
        }
        System.out.print("\n");
        String tap = "  ";
        for(int x = 1; x <= size; ++x) {
            System.out.print(tap + x + ((x>=10) ? " " : "  "));
            for(int y = 1; y <= size; ++y) {
                if(isTower(x, y)) {
                    if(getPlayerColor(x, y) == PlayerColor.RED) {
                        System.out.print(RED);
                    } else {
                        System.out.print(BLUE);
                    }
                    System.out.print(" T" + getTowerHeight(x, y) + RESET);
                } else if(x == 1 && y == 1) {
                    System.out.print(RED + " B " + RESET);
                } else if(x == size && y == size) {
                    System.out.print(BLUE + " B " + RESET);
                } else if(isStone(x, y)) {
                    if(getPlayerColor(x, y) == PlayerColor.RED) {
                        System.out.print(RED);
                    } else {
                        System.out.print(BLUE);
                    }
                    System.out.print(" S " + RESET);
                } else if(isBlocked(x, y)) {
                    if(getPlayerColor(x, y) == PlayerColor.RED) {
                        System.out.print(RED);
                    } else {
                        System.out.print(BLUE);
                    }
                    System.out.print(" X " + RESET);
                } else {
                    System.out.print(" o ");
                }
                System.out.print(" ");
            }
            tap += "  ";
            System.out.print("  " + x + "\n");
        }
        headChar = 'A';
        tap += "   ";
        System.out.print(tap);
        for(int top = 0; top < size; ++top) {
            System.out.print(headChar + "   ");
            ++headChar;
        }
        System.out.println("");
    }

    /**
     * Request an Move from the player and converts to Move
     * @return Move of the Player
     * @throws Exception if Move has wrong Format
     */
    @Override
    public Move deliver() throws Exception {
        Move move = null;
        String nextMove = null;
        System.out.println("Bitte geben Sie Ihren Zug an: ");
        nextMove = this.scanner.next();
        try {
            move = Move.parseMove(nextMove);
        } catch (Exception e) {
            System.out.println(e);
        }
        return move;
    }
}
