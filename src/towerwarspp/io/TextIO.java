package towerwarspp.io;

import java.util.Scanner;

import towerwarspp.board.BViewer;
import towerwarspp.preset.*;

/**
 * Class {@link TextIO} control the Input over the Command Line Interface
 *
 * @version 0.6 July 03th 2017
 * @author Kai Kuhlmann
 */
public class TextIO implements IO {
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
    private BViewer viewer;

    /**
     * Private Scanner-Object
     */
    private Scanner scanner;

    /**
     * Constructor to Initialize the TextIO
     * @param viewer Object of Type Viewer
     */
    public TextIO(BViewer viewer) {
        this.viewer = viewer;
        this.scanner = new Scanner(System.in);
    }


    /**
     * Output of the Board
     */
    public void visualize() {
        int size = this.viewer.getSize();
        char headChar = 'A';
        System.out.print("    ");
        for(int top = 0; top < size; ++top) {
            System.out.print(headChar + "  ");
            ++headChar;
        }
        System.out.print("\n");
        String tap = "  ";

        for (int y=1; y <= size; y++) {
            System.out.print(tap + y + ((y>=10) ? " " : "  "));
            for (int x=1; x <=size; x++) {
                System.out.print(this.viewer.positionToString(new Position(x, y)));
            }
            System.out.print("  " + y);
            System.out.println();
            tap += "  ";
        }
        headChar = 'A';
        tap += "   ";
        System.out.print(tap);
        for(int top = 0; top < size; ++top) {
            System.out.print(headChar + "  ");
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
