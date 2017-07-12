package towerwarspp.main.game;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import towerwarspp.preset.*;

public class Save implements Serializable {
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
     * Constructor for Save-Object
     * @param save the size of the board
     */
    public Save (int size) {
        this.size = size;
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
    public void export() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataName + ".aot"))) {
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            System.out.println("Saving failed!");
        }
    }
}