package towerwarspp.main.game;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.regex.*;
import towerwarspp.preset.Move;
/**
 * This class provides the function of saving and loading Save-Games.
 * it saves the history of moves in an ArrayDeque of Moves and writes them into a
 * textfile with the ".aot"-ending
 * @author Alexander Wähling
 * @version 1.2
 */
public class Save implements Iterable<Move> {

     /**
     * the pattern for one move as string. used for parsing moves from savefile
     */
    private static final Pattern movePattern = 
    Pattern.compile("[A-Z]([1-9]|1[0-9]|2[0-6])->[A-Z]([1-9]|1[0-9]|2[0-6])");

    /**
     * path to the Folder of the Savegames
     */
    private static final String savePath = System.getProperty("user.home") +
    File.separator + "AOT_Saves" + File.separator;

    /**
     * The History of the moves
     */
    private ArrayDeque<Move> moveHistory = new ArrayDeque<Move>();

    /**
     * The Name of the File, which will be created
     */
    private String dataName;

    /**
     * The size of the arrayDeque
     */
    private int historySize;

    /**
     * size of the board to be safed
     */
    private int size;

    /**
     * variables for the PlayerColor
     */
    private static final boolean RED = true, BLUE = false;

   

    /**
     * Constructor for Save-Object
     * @param size the size of the board
     */
    public Save (int size) {
        this.size = size;
        this.historySize = 0;
    }

    /**
     * getter for the boardsize
     * @return the the size of the saved board
     */
    public int getSize() {
        return size;
    }

    /**
     * returns the ArrayDeque
     * @return the ArrayDeque with the moves
     */
    public ArrayDeque<Move> getMoveHistory() {
        return moveHistory;
    }

    /**
     * sets the ArrayDeque
     * @param moveHistory the new ArrayDeque
     */
    public void setMoveHistory(ArrayDeque<Move> moveHistory) {
        this.moveHistory = moveHistory;
    }

    /**
     * polls the first move from the queue, implemented by ArrayDeque
     * @return the polled move
     */
    public Move getNextMove() {
        return moveHistory.poll();
    }

    /**
     * returns the savePath
     * @return the savePath
     */
    public String getSavePath() {
        return savePath;
    }

    /**
     * returns iterator for the save-Class implemnted with an anonymous class,
     * so the user can't manipulate the ArrayDeque itself.
     * @return the iterator over moveHistory
     */
    @Override
    public Iterator<Move> iterator() {
        return new Iterator<Move>() {
            Iterator<Move> it = moveHistory.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Move next() {
                return it.next();
            }
        };
    }

    /**
     * adds a move
     * @param move the move to be added
     */
    public void add(Move move) {
        if(move != null) {
            moveHistory.add(move);
            historySize++;
        }        
    }

    /**
     * The Method, which is called, if the user wants to save and export the game
     * to a file with the given name.
     * @param fileName the Name for the new File. expanded by ".aot"
     */
    public void export(String fileName) {
        try{
            File dir = new File(savePath);
            dir.mkdir();
            PrintWriter writer = new PrintWriter(savePath + fileName + ".aot", "UTF-8");
            writer.println(size);
            boolean currentPlayer = RED;
            for(Move i : moveHistory) {
                writer.println(i.toString() + (currentPlayer == RED ? ",Red" : ",Blue"));
                currentPlayer = (currentPlayer == RED ? BLUE : RED);
            }
            writer.close();
            System.out.println("Saved Game in " + savePath + fileName + ".aot");
        } catch (IOException e) {
            System.out.println("Saving failed");
        }  
    }

    /**
     * This method loads a plain-text-file and converts it into a Save-Object.
     * @param file the name of the file to be loaded
     * @return the parsed Save-Object
     */
    public Save load(String file) throws LoadParserException, IOException {

        try(BufferedReader br = new BufferedReader(new FileReader(savePath + file))){
            Save a = new Save(Integer.parseInt(br.readLine()));
            String line = br.readLine();
            while(line != null) {
                a.add(parseLoad(line));    
                line = br.readLine();            
            }
            return a;
        } 
    }
    
    /**
     * This method parses a given string to a Move-Object with the help of the 
     * static movePattern-Variable, which is a regex.
     * @param str the string to be parsed
     * @return the Move-Object
     */
    public static Move parseLoad(String str) throws LoadParserException  {
        try {
            Matcher m = movePattern.matcher(str);
            m.find();
            return Move.parseMove(m.group(0));
        } catch (Exception e){
            throw new LoadParserException("illegal Savefile!");
        } 
    }
}