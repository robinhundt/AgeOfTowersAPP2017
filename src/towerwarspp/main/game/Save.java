package towerwarspp.main.game;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.io.IOException;
import java.util.regex.*;

import towerwarspp.main.game.LoadParserException;
import towerwarspp.main.tournament.*;

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
     * shows, if this is a saveGame for a tournament or not
     */
    private boolean tournament = false;

    /**
     * empty TResult-Object. Filled, if tournament is true
     */
    private TResult result;

   

    /**
     * Constructor for Save-Object
     * @param size the size of the board
     */
    public Save (int size) {
        this.size = size;
        this.historySize = 0;
    }

    /**
     * Tournament-Constructor. Used to construct a Tournament-Save-Game
     */
    public Save (int size, TResult result) {
        this.size = size;
        this.historySize = 0;
        this.result = result;
        this.tournament = true;
    }

    /**
     * getter for the boardsize
     * @return the the size of the saved board
     */
    public int getSize() {
        return size;
    }

    /**
     * returns the size of the ArrayDeque
     * @return the ArrayDeque with the moves
     */
    public int getHistorySize() {
        return historySize;
    }

    /**
     * polls the first move from the queue, implemented by ArrayDeque
     * @return the polled move
     */
    public Move getNextMove() {
        return moveHistory.poll();
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
            if(tournament == true) {
                exportTournatment(writer);
            }
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
    public static Save load(String file) throws LoadParserException, IOException {

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
    private static Move parseLoad(String str) throws LoadParserException  {
        try {
            Matcher m = movePattern.matcher(str);
            m.find();
            return Move.parseMove(m.group(0));
        } catch (Exception e){
            throw new LoadParserException("illegal Savefile!");
        } 
    }

    /**
     * writes the values of the TResult
     * @param writer writes the Lines to the file
     */
    private void exportTournatment(PrintWriter writer) {
        int red = 0, blue = 1;
        for(int i = red; i < blue; i++) {
            writer.println(result.getWins()[i]);
            writer.println(result.getBaseDestroyed()[i]);
            writer.println(result.getNoPosMoves()[i]);
            writer.println(result.getIllegalMove()[i]);
            writer.println(result.getSurrender()[i]);
            writer.println(result.getAvgMoves()[i]);
            writer.println(result.getTotalMoves()[i]);
        }
        writer.println(result.getTimeoutGames());
        writer.println(result.getTotalGames());
    }
    private static TResult parseTournament(BufferedReader br) throws LoadParserException {
        int red = 0, blue = 1;
        TResult resultp = new TResult();
        int[] wins = new int[2];       
        int[] baseDestroyed = new int[2];
        int[] noPosMoves = new int[2];
        int[] illegalMove = new int[2];
        int[] surrender = new int[2];
        double[] avgMoves = new double[2];
        try {
            for(int i = red; i < blue; i++) {
                wins[i] = Integer.parseInt(br.readLine());
                baseDestroyed[i] = Integer.parseInt(br.readLine());
                noPosMoves[i] = Integer.parseInt(br.readLine());
                illegalMove[i] = Integer.parseInt(br.readLine());
                surrender[i] = Integer.parseInt(br.readLine());
                avgMoves[i] = Double.parseDouble(br.readLine());
            }
            resultp.setTimeoutGames(Integer.parseInt(br.readLine()));
            resultp.setTotalGames(Integer.parseInt(br.readLine()));
            resultp.setWins(wins);
            resultp.setBaseDestroyed(baseDestroyed);
            resultp.setNoPosMoves(noPosMoves);
            resultp.setIllegalMove(illegalMove);
            resultp.setSurrender(surrender);
            resultp.setAvgMoves(avgMoves);
            return resultp;
        } catch (Exception e) {
            throw new LoadParserException("illegal tournamentsave");
        }        
    }
}