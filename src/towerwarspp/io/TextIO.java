package towerwarspp.io;

import java.util.NoSuchElementException;
import java.util.Scanner;

import towerwarspp.preset.*;

import static towerwarspp.preset.PlayerColor.RED;

/**
 * Class {@link TextIO} interacting with to user over the command line (standard-input)
 *
 * @version 1.5 July 06th 2017
 * @author Kai Kuhlmann, Niklas Mueller
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
    private static final String ANSI_PURPLE = "\u001B[35m";
    /**
     * Command Line Color = Yellow
     */
    private static final String ANSI_YELLOW = "\u001B[33m";
    /**
     * Command Line Color = Cyan
     */
    private static final String ANSI_CYAN = "\u001B[36m";
    /**
     * Command Line Color = White
     */
    private static final String ANSI_WHITE = "\u001B[37m";
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
        StringBuilder tap = new StringBuilder("  ");

        /*own stringbuilder for the letters*/
        StringBuilder letters = new StringBuilder();
        letters.append("    ");
        /*fill stringbuilder with letters*/
        for (int j=1; j<=size; j++) {
            letters.append(headChar++).append("  ");
        }

        for (int i=0; i<=size+1; i++) {
            /*first line, only letters*/
            if (i==0) {
                output.append(letters).append("\n");
            }
            /*last line, only letters*/
            else if (i == size+1) {
                output.append(tap);
                output.append(letters).append("\n");
            }
            /*every other line, number, board and number*/
            else if (i<=size) {
                output.append(tap);
                output.append(i);
                output.append(i>=10 ? " " : "  ");
                for (int j=1; j<=size; j++) {
                    output.append(positionToString(new Position(j, i)));
                }
                output.append("  ");
                output.append(i).append("\n");
                tap.append("  ");
            }
        }
        System.out.println(output);
    }

    /**
     * Method positionToString() showing if there is an {@link towerwarspp.board.Entity} at given {@link Position} and what kind
     *
     * @param position {@link Position} of the {@link towerwarspp.board.Entity}
     * @return String containing representation of the {@link Position}
     */
    private String positionToString(Position position) {
        if(!this.viewer.isEmpty(position)) {
            PlayerColor color = this.viewer.getPlayerColor(position);
            boolean blocked = this.viewer.isBlocked(position);
            int height = this.viewer.getHeight(position);
            int maxHeight = this.viewer.getSize() / 3;
            String col;
            String s;

            /*check if entity is blocked or has maximum height, otherwise it's a stone*/
            if (blocked) {
                /*colors for blocked towers*/
                col = color == RED ? ANSI_WHITE : ANSI_CYAN;
            }
            else if (height == maxHeight) {
                /*colors for towers with maximum height*/
                col = color == RED ? ANSI_YELLOW : ANSI_PURPLE;
            }
            else {
                /*normal stones*/
                col = color == RED ? ANSI_RED : ANSI_BLUE;
            }

            /*check if entity is base*/
            if (viewer.isBase(position)) {
                s = col + " B " + ANSI_RESET;
            } else {
                /*otherwise check if height is bigger than zero, so if there's a tower or a stone*/
                s = col + (height != 0 ? " T" + height : " S ") + ANSI_RESET;
            }
            /*return representation*/
            return s;
        }
        /*if position is empty*/
        return " o ";
    }

    /**
     * Override method setViewer() to set own {@link Viewer}
     *
     * @param viewer {@link Viewer} object to be set as {@link Viewer}
     */
    @Override
    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
    }

    /**
     * Overriden method deliver() parsing a textual input from the standard-input into a {@link Move}
     *
     * @return {@link Move} user wants to make
     * @throws Exception if input has wrong format
     */
    @Override
    public Move deliver() throws Exception {
        Move move;
        System.out.println("Please enter move: ");
        String nextMove;
        try {
            nextMove = this.scanner.nextLine();
        }
        catch (NoSuchElementException e) {
            return null;
        }
        try {
            move = Move.parseMove(nextMove);
        }
        catch (Exception e) {
            System.out.println("Couldn't interpret move. Move needs to be like 'A2->A3'");
            return new Move(new Position(1, 1), new Position(1, 1));
        }
        return move;
    }
}
