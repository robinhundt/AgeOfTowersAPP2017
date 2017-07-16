package towerwarspp.main.tournament;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;

import towerwarspp.main.game.*;
import towerwarspp.preset.*;

/**
 * This class extends the {@link Save} -Class, so that it works for {@link Tournament}
 * @author Alexander Wähling
 * @version 0.1 
 */
public class TSave extends Save {


     /**
     * shows, if this is a saveGame for a {@link Tournament} or not
     */
    private boolean tournament = false;

    /**
     * empty TResult-Object. Filled, if {@link Tournament} is true
     */
    private TResult result;

    /**
     * Constructor for TSave
     * @param size the size of the board
     */
    public TSave (int size) {
        super(size);
        this.result = new TResult();
        this.tournament = true;
    }

    /**
     * sets the {@link TResult}-Object of the Class
     * @param result the new result
     */
    public void setResult(TResult result) {
        this.result = result;
    }

    /**
     * exports a TSave - Object to a file
     * 1. Line : size of the {@link Board}
     * 2. - n. Line Moves of the Game
     * n. - (n+16).Line the Results of the {@link Tournament}
     * @param file the name of the File
     */
    public void exportT(String file) {
        try {
            super.export(file);
            BufferedWriter bw = new BufferedWriter(new FileWriter(super.getSavePath() + file + ".aot", true));
            PrintWriter writer = new PrintWriter(bw);
            writeTournatment(writer);
        } catch (Exception e) {
            System.out.println("Error Saving File");
        }
    }

    /**
     * This Method writes the {@link Tournament}-Result with agiven writer to a textfile
     * @param writer the PrintWriter ith the file
     */
    private void writeTournatment(PrintWriter writer) {
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

    /**
     * This Method Loads a TSave-Object from an .aot-Savefile using the parser of the super-class and 
     * {@link parseTournament}
     */
    @Override
    public  TSave load(String file) throws LoadParserException, IOException {
         try(BufferedReader br = new BufferedReader(new FileReader(super.getSavePath() + file))){
            TSave a = new TSave(Integer.parseInt(br.readLine()));
            String line = br.readLine();
            while(line != null) {
                a.add(super.parseLoad(line));    
                line = br.readLine();            
            }
            return a;
        } 
    }

    /**
     * this method parses a TResult-Object from a file
     * @param br the reader with the ".aot" -file
     */
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