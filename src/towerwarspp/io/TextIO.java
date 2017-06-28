package towerwarspp.io;

import java.util.Scanner;

import towerwarspp.preset.*;

/**
 * Class {@link TextIO} control the Input over the Command Line Interface
 *
 * @version 0.3 2th june 2017
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
     * @return  True if Token is stone, otherwise false
     */
    private boolean isStone(int row, int col) {
        return this.viewer.isStone(row, col);
    }

    /**
     * Checks if Stone is Tower
     * @return True if Stone is Tower, otherwise false
     */
    private boolean isTower(int row, int col) {
        return this.viewer.isTower(row, col);
    }

    /**
     * Checks if Stone is blocked
     * @return True if Stone is vlocked, otherwise false
     */
    private boolean isBlocked(int row, int col) {
        return this.viewer.isBlocked(row, col);
    }

    /**
     *
     */
    private PlayerColor getPlayerColor(int row, int col) {
        return this.viewer.getPlayerColor(row, col);
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
        for(int row = 1; row <= size; ++row) {
            System.out.print(tap + row + "  ");
            for(int col = 1; col <= size; ++col) {
                if(isTower(row, col)) {
                    System.out.print(" T ");
                } else if(row == 1 && col == 1) {
                    System.out.print(RED + " B " + RESET);
                } else if(row == size && col == size) {
                    System.out.print(BLUE + " B " + RESET);
                } else if(isStone(row, col)) {
                    if(getPlayerColor(row, col) == PlayerColor.RED) {
                        System.out.print(RED);
                    } else {
                        System.out.print(BLUE);
                    }
                    System.out.print(" S " + RESET);
                } else if(isBlocked(row, col)) {
                    if(getPlayerColor(row, col) == PlayerColor.RED) {
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
            System.out.print("  " + row + "\n");
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
