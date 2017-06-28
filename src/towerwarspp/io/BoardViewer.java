package towerwarspp.io;

import towerwarspp.board.Entity;
import towerwarspp.preset.*;
import towerwarspp.board.Board;


/**
 * Class {@link BoardViewer} create the interface to Board
 * @version 0.3 23th june 2017
 * @author Kai Kuhlmann
 */
public class BoardViewer implements Viewer {

    /**
     * Private Board
     */
    private Board board;
    private Entity[][] entities;

    /**
     * Constructor to Initialize Viewer
     * @param board the Board of the game
     */
    public BoardViewer(Board board, Entity[][] entities) {
        this.board = board;
        this.entities = entities;
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

    @Override
    public boolean isBlocked(int row, int col) {
        return entities[row][col].isBlocked();
    }

    @Override
    public boolean isStone(int row, int col) {
        return !entities[row][col].isTower();
    }

    @Override
    public boolean isTower(int row, int col) {
        return entities[row][col].isTower();
    }

    @Override
    public PlayerColor getPlayerColor(int row, int col) {
        return entities[row][col].getColor();
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
