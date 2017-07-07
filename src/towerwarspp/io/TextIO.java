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
        StringBuilder output = new StringBuilder();

        int size = this.viewer.getSize();
        char headChar = 'A';
        String tap = "  ";

        /*own stringbuilder for the letters*/
        StringBuilder letters = new StringBuilder();
        letters.append("    ");
        /*fill stringbuilder with letters*/
        for (int j=1; j<=size; j++) {
            letters.append(headChar++ + "  ");
        }

        for (int i=0; i<=size+1; i++) {
            /*first line, only letters*/
            if (i==0) {
                output.append(letters.toString() + "\n");
            }
            /*last line, only letters*/
            else if (i == size+1) {
                output.append(tap);
                output.append(letters.toString() + "\n");
            }
            /*every other line, number, board and number*/
            else if (i<=size) {
                output.append(tap);
                output.append(i);
                output.append("  ");
                for (int j=1; j<=size; j++) {
                    output.append(entityToString(new Position(j, i)));
                }
                output.append("  ");
                output.append(i + "\n");
                tap += "  ";
            }
        }
        System.out.println(output.toString());
    }

    /**
     * Defines which Entity is at the position
     * @param position position of the Entity
     * @return Returns the Type of the Entity as String
     */
    private String entityToString(Position position) {
        if(!this.viewer.isEmpty(position)) {
            PlayerColor color = this.viewer.getPlayerColor(position);
            boolean blocked = this.viewer.isBlocked(position);
            int height = this.viewer.getHeight(position);
            int maxHeight = this.viewer.getSize() / 3;
            String col;
            String s;
            if (color == PlayerColor.RED) {
                if (blocked) {
                    col = ANSI_WHITE;
                } else if (height == maxHeight) {
                    col = ANSI_YELLOW;
                } else {
                    col = ANSI_RED;
                }
            } else {
                if (blocked) {
                    col = ANSI_CYAN;
                } else if (height == maxHeight) {
                    col = ANSI_PURPLE;
                } else {
                    col = ANSI_BLUE;
                }

            }
            if (viewer.isBase(position)) {
                s = col + " B " + ANSI_RESET;
            } else {
                s = col + (height != 0 ? " T" + height : " S ") + ANSI_RESET;
            }
            return s;
        }
        return " o ";
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
        System.out.println("Please enter move: ");
        nextMove = this.scanner.next();
        try {
            move = Move.parseMove(nextMove);
        } catch (Exception e) {
            System.out.println("Couldn't interpret move. Move needs to be like 'A2->A3'");
        }
        return move;
    }
}
