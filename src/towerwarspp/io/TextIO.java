package towerwarspp.io;
import static towerwarspp.preset.PlayerColor.RED;
import java.util.NoSuchElementException;
import java.util.Scanner;
import towerwarspp.board.Entity;
import towerwarspp.main.Debug;
import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.debug.DebugSource;
import towerwarspp.preset.Position;
import towerwarspp.preset.Viewer;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Move;
/**
 * Class {@link TextIO} interacting with to user over the command line
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
     * Debug Instance of {@link Debug}
     */
    private Debug debugInstance;
    /**
     * name of the Save-game
     */
    private String saveGameName;

    /**
     * shows, if the user wants to save the game
     */
    private boolean save = false;
    /**
     * Constructor to Initialize the TextIO
     */
    public TextIO() {
        this.debugInstance = Debug.getInstance();
        this.scanner = new Scanner(System.in);
        this.debugInstance.send(DebugLevel.LEVEL_2, DebugSource.IO, "TextIO initialized.");
    }
    /**
     * Overridden method setViewer() to set own {@link Viewer}
     *
     * @param viewer {@link Viewer} object to be set as {@link Viewer}
     */
    @Override
    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
        this.debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Viewer is set.");
    }
    @Override
    public void setTitle(String string) {
    }
    /**
     * Output of the Board
     */
    public void visualize() {
        StringBuilder output = new StringBuilder();
        int size = this.viewer.getSize();
        char headChar = 'A';
        StringBuilder tap = new StringBuilder("  ");
        /*own StringBuilder for the letters to be displayed above and beneath the board*/
        StringBuilder letters = new StringBuilder();
        letters.append("    ");
        /*fill StringBuilder with letters*/
        for (int j=1; j<=size; j++) {
            letters.append(headChar++).append("  ");
        }
        /*output representation of board*/
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
     * Overriden method display to get possibility for other classes to inform user
     *
     * @param string containing information for user
     */
    @Override
    public void display(String string) {
        System.out.println(string);
    }
    @Override
    public void dialog(String string) {
    }
    /**
     * Method positionToString() showing if there is an {@link towerwarspp.board.Entity} at given {@link Position} and what kind
     *
     * @param position {@link Position} of the {@link towerwarspp.board.Entity}
     * @return String containing representation of the {@link Position}
     */
    private String positionToString(Position position) {
        Entity entity = this.viewer.getEntity(position);
        /*check if there is an entity on that position*/
        if(entity != null) {
            /*get all information about entity at that position*/
            PlayerColor color = entity.getColor();
            boolean blocked = entity.isBlocked();
            int height = entity.getHeight();
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
            if (entity.isBase()) {
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
     * Overridden method deliver() parsing a textual input from the standard-input into a {@link Move}
     *
     * @return {@link Move} user wants to make
     * @throws Exception if input has wrong format
     */
    @Override
    public Move deliver() throws Exception {
        Move move;
        /*tell user to enter a move*/
        System.out.println("Please enter move: ");
        String nextMove;
        try {
            /*read in string in one line from standard input*/
            nextMove = this.scanner.nextLine();
            /*if user wants to surrender*/
            if (nextMove.equals("surrender")) {
                this.debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Game surrendered. Input: " + nextMove);
                return null;
            }
            /*another option to surrender, but user will be asked twice*/
            else if (nextMove.equals("")) {
                System.out.println("Do you really want to surrender? yes or no");
                String answer = this.scanner.nextLine();
                if (answer.equals("yes") || answer.equals("y")) {
                    this.debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Game surrendered. Input: " + nextMove + " and " + answer);
                    return null;
                }
                /*if something else then "yes" or "y" will be inputted, return illegal move, so user get's another chance*/
                this.debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Move illegal. (" + nextMove + ")");
                return new Move(new Position(1, 1), new Position(1, 1));
            }

            else if (nextMove.equals("save")) {
                System.out.println("Please name your Savefile");
                String saveGameName = this.scanner.nextLine();
                save = true;
                return null;
            }
        }
        /*if no line was found*/
        catch (NoSuchElementException e) {
            return null;
        }
        try {
            /*if something else but "surrender" or nothing has been inputted*/
            move = Move.parseMove(nextMove);
            this.debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Move parsed. (" + move + ")");
        }
        catch (Exception e) {
            /*if input did not have the correct format, give user another chance*/
            System.out.println("Couldn't interpret move. Move needs to be like 'A2->A3'");
            this.debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Wrong move format.");
            return new Move(new Position(1, 1), new Position(1, 1));
        }
        /*if everything went right, return the parsed move*/
        this.debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Returned move.");
        return move;
    }
    
    @Override
    public String getSaveGameName() {
        return saveGameName;
    }

    @Override
    public boolean getSave() {
        return save;
    }
}