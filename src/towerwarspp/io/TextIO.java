package towerwarspp.io;

import java.util.Scanner;

import towerwarspp.preset.*;

/**
 * Class {@link TextIO} control the Input over the Command Line Interface
 *
 * @version 0.7 July 06th 2017
 * @author Kai Kuhlmann
 */
public class TextIO implements IO {
    /**
     * Normal Command Line Color
     */
    private static final String ANSI_RESET = "\u001B[0m";
    /**
     * Command Line Color = Red
     */
    private static final String ANSI_RED = "\u001B[31m";
    /**
     * Command Line Color = Blue
     */
    private static final String ANSI_BLUE = "\u001B[34m";
    /**
     * Command Line Color = Purple
     */
    public static final String ANSI_PURPLE = "\u001B[35m";
    /**
     * Command Line Color = Yellow
     */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /**
     * Command Line Color = Cyan
     */
    public static final String ANSI_CYAN = "\u001B[36m";
    /**
     * Command Line Color = White
     */
    public static final String ANSI_WHITE = "\u001B[37m";
    /**
     * Viewer-Object
     */
    private Viewer viewer;

    /**
     * Private Scanner-Object
     */
    private Scanner scanner;

    /**
     * Constructor to Initialize the TextIO
     */
    public TextIO() {
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
                System.out.print(entityToString(new Position(x, y)));
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
     * Defines which Entity is at the position
     * @param position position of the Entity
     * @return Returns the Type of the Entity as String
     */
    private String entityToString(Position position) {
        PlayerColor color = this.viewer.getPlayerColor(position);
        boolean blocked = this.viewer.isBlocked(position);
        int height = this.viewer.getHeight(position);
        int maxHeight = this.viewer.getSize()/3;
        String col;
        String s;
        if(color == PlayerColor.RED) {
            if(blocked) {
                col = ANSI_WHITE;
            }
            else if (height == maxHeight) {
                col = ANSI_YELLOW;
            }
            else {
                col = ANSI_RED;
            }
        }
        else {
            if(blocked) {
                col = ANSI_CYAN;
            }
            else if (height == maxHeight){
                col = ANSI_PURPLE;
            }
            else {
                col = ANSI_BLUE;
            }

        }
        if(viewer.isBase(position)) {
            s = col + " B " + ANSI_RESET;
        }
        else {
            s = col  + (height!=0 ? " T" + height : " S ") + ANSI_RESET;
        }
        return s;
    }

    /**
     * Setter of the Viewer
     * @param viewer Viewer-Object
     */
    @Override
    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
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
