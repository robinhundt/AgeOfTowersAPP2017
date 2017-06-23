package towerwarspp.io;

import towerwarspp.preset.Move;
import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
import towerwarspp.preset.Viewer;
import towerwarspp.board.Board;


/**
 * Class {@link BoardInfo} create the graphical Board of the Game
 *
 * @version 0.1 20th june 2017
 * @author Kai Kuhlmann
 */
public class BoardInfo implements Viewer {

    /**
     * Private Board
     */
    private Board board;

    /**
     * Constructor to Initialize Viewer
     * @param board the Board of the game
     */
    public BoardInfo(Board board) {
        this.board = board;
    }

    /**
     * Returns the size of the Board
     * @return size
     */
    @Override
    public int getSize() {
        return this.board.getSize();
    }

    /**
     * Returns the actual Turn
     * @return Turn
     */
    @Override
    public int getTurn() {
        return this.board.getTurn();
    }

    /**
     * Returns the actual Status
     * (OK, RED_WIN, BLUE_WIN, ILLEGAL)
     * @return Status
     */
    @Override
    public Status getStatus() {
        return this.board.getStatus();
    }

    /**
     * Retruns the set of Stones
     * @return set of Stones
     */
    @Override
    public int getStones() {
        return 0;
    }

    /**
     * Returns a set of possible Moves of a Stone
     * @param position Position of a Stone
     * @return the possible Moves of the Stone
     */
    @Override
    public Move[] getPossibleMoves(Position position) {
        try {
            return this.board.stoneMoves(position);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
