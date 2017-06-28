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

    /**
     * Constructor to Initialize the TextIO
     * @param viewer Object of Type Viewer
     */
    public TextIO(BoardViewer viewer) {
        this.viewer = viewer;
    }

    /**
     * just for testing
     */
    public TextIO() {}

    /**
     * Checks if is Stone
     * @return  True if Token is stone, otherwise false
     */
    private boolean isStone() {
        return false;
    }

    /**
     * Checks if Stone is Tower
     * @return True if Stone is Tower, otherwise false
     */
    private boolean isTower() {
        return false;
    }

    /**
     * Checks if Stone is blocked
     * @return True if Stone is vlocked, otherwise false
     */
    private boolean isBlocked() {
        return false;
    }

    /**
     * Get the size of the Board
     * @return Size of Board
     */
    private int getSize() {
        //return viewer.getSize();
        return 10;
    }

    /**
     * Output of the Board
     * @return Board as String
     */
    @Override
    public String toString() {
        int size = getSize();
        char headChar = 'A';
        System.out.print("\t");
        for(int top = 0; top < size; ++top) {
            System.out.print(headChar + "\t");
            ++headChar;
        }
        System.out.print("\n");
        String tap = "  ";
        for(int row = 0; row < size; ++row) {
            System.out.print(tap + row + "  ");
            for(int col = 0; col < size; ++col) {
                if(isTower()) {
                    System.out.print(" T ");
                } else if(row == 0 && col == 0) {
                    System.out.print(RED + " B " + RESET);
                } else if(row == size - 1 && col == size - 1) {
                    System.out.print(BLUE + " B " + RESET);
                } else if(true) {
                    if(false) {
                        System.out.print(RED);
                    } else {
                        System.out.print(BLUE);
                    }
                    System.out.print(" S " + RESET);
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
        return null;
    }

    /**
     * Request an Move from the player and converts to Move
     * @return Move of the Player
     * @throws Exception if Move has wrong Format
     */
    @Override
    public Move deliver() throws Exception {
        Move move = null;
        Scanner scanner = new Scanner(System.in);
        String nextMove = null;
        System.out.println("Bitte geben Sie Ihren Zug an: ");
        nextMove = scanner.next();
        try {
            move = move.parseMove(nextMove);
        } catch (Exception e) {
            System.out.println(e);
        }
        return move;
    }
}
