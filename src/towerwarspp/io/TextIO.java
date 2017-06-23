package towerwarspp.io;

import java.util.Scanner;

import towerwarspp.preset.*;

/**
 * Class {@link TextIO} control the Input over the Command Line Interface
 *
 * @version 0.1 20th june 2017
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
    private Viewer viewer;

    /**
     * Constructor to Initialize the TextIO
     * @param viewer Object of Type Viewer
     */
    public TextIO(Viewer viewer) {
        this.viewer = viewer;
    }

    /**
     * just for testing
     */
    public TextIO() {}

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
        String stone = "     ";
        System.out.print("\t    ");
        for(int head = 0; head < size; ++head) {
            System.out.print(headChar + "\t\t\t    ");
            ++headChar;
        }
        System.out.print("\n");
        for(int row = 0; row < size; ++row) {
            for(int top = 0; top < size; ++top) {
                System.out.print("\t    -\t\t");
            }
            System.out.print("\n");
            for(int next = 0; next < size; ++next) {
                System.out.print("\t /     \\\t");
            }
            System.out.print("\n");
            System.out.print(row);
            for(int col = 0; col < size; ++col) {
                if(row == 2) {
                    stone = RED + "  t  " + RESET;
                } else if(row == 0) {
                    stone = BLUE + "  t  " + RESET;
                } else {
                    stone = "     ";
                }
                System.out.print("\t  " + stone + "  \t");
            }
            System.out.print("\n");
            for (int next = 0; next < size; ++next) {
                System.out.print("\t \\     /\t");
            }
            System.out.print("\n");
            for (int next = 0; next < size; ++next) {
                System.out.print("\t    -\t\t");
            }
            System.out.print("\n");
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
