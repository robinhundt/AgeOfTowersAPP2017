package towerwarspp.main.game;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.io.IOException;
import java.util.regex.*;

import towerwarspp.main.game.LoadParserException;
import towerwarspp.preset.*;
import towerwarspp.main.game.*;


public class Save {
    /**
     * The History of the moves
     */
    ArrayDeque<Move> moveHistory = new ArrayDeque<Move>();

    /**
     * The Name of the File, which will be created
     */
    String dataName;

    /**
     * size of the board to be safed
     */
    int size;

    /**
     * variables for the PlayerColor
     */
    private static final boolean RED = true, BLUE = false;

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
     * Constructor for Save-Object
     * @param save the size of the board
     */
    public Save (int size) {
        this.size = size;
    }

    /**
     * getter for the boardsize
     */
    public int getSize() {
        return size;
    }

    /**
     * adds a move
     * @param move the move to be added
     */
    public void add(Move move) {
        moveHistory.add(move);
    }

    /**
     * The Method, which is called, if the user wants to save and export the game
     * to a file with the given name
     */
    public void export(String dateName) {
        try{
            File dir = new File(savePath);
            dir.mkdir();
            PrintWriter writer = new PrintWriter(savePath + dateName + ".aot", "UTF-8");
            writer.println(size);
            boolean currentPlayer = RED;
            for(Move i : moveHistory) {
                writer.println(i.toString() + (currentPlayer == RED ? ",Red" : ",Blue"));
                currentPlayer = (currentPlayer == RED ? BLUE : RED);
            }
            writer.close();
            System.out.println("Saving completed");
        } catch (IOException e) {
            System.out.println("Saving failed");
        }  
    }

    /**
     * This method loads a plain-text-file and converts it into a Save-Object
     * @param file the name of the file to be loaded
     * @return the parsed Save-Object
     */
    public static Save load(String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(savePath + file));
        Save a = new Save(Integer.parseInt(br.readLine()));
        String line = br.readLine();
        while(line != null) {
            a.add(parseLoad(line));    
            line = br.readLine();            
        }
        return a;
    }

    /**
     * parses a line of the savefile into a move-object
     */

    private static Move parseLoad(String str) throws LoadParserException  {
        try {
            Matcher m = movePattern.matcher(str);
            m.find();
            return Move.parseMove(m.group(0));
        } catch (Exception e){
            throw new LoadParserException("illegal Savefile!");
        } 
    }
}