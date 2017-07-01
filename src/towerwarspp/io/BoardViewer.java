package towerwarspp.io;

import towerwarspp.board.Entity;
import towerwarspp.preset.*;
import towerwarspp.board.Board;
import towerwarspp.board.MoveList;


/**
 * Class {@link BoardViewer} create the interface to Board
 * @version 0.5 June 29th 2017
 * @author Kai Kuhlmann
 */
public class BoardViewer implements Viewer{

    /**
     * Private Board
     */
    private Board board;
    /**
     * Private Array of Entities
     */
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

    /**
     * Checks if Stone is blocked
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return True if Stone is vlocked, otherwise false
     */
   // @Override
    public boolean isBlocked(int x, int y) {
        if(entities[x][y] != null) {
            return entities[x][y].isBlocked();
        }
        return false;
    }

    /**
     * Checks if is Stone
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return  True if Token is stone, otherwise false
     */
   // @Override
    public boolean isStone(int x, int y) {
        if(entities[x][y] != null) {
            return !entities[x][y].isTower();
        }
        return false;
    }

    /**
     * Checks if Stone is Tower
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return True if Stone is Tower, otherwise false
     */
   // @Override
    public boolean isTower(int x, int y) {
        if(entities[x][y] != null) {
            return entities[x][y].isTower();
        }
        return false;
    }

    /**
     * Get owner of Tower
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return Playercolor
     */
   // @Override
    public PlayerColor getPlayerColor(int x, int y) {
        return entities[x][y].getColor();
    }

    /**
     * Get the height of a Tower
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return height of Tower
     */
    // @Override
    public int getHeight(int x, int y) {
        if(entities[x][y] != null) {
            return entities[x][y].getHigh();
        }
        return 0;
    }

    /**
     * Returns a set of possible Moves of a Stone
     * @param position Position of a Stone
     * @return the possible Moves of the Stone
     */
  //  @Override
    public Move[] getPossibleMoves(Position position) {
        try {
            return this.board.stoneMoves(position);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


// Anasrtasiia wrote:
	public MoveList possibleMoves(Position p) throws Exception {
		return null;
	}
	public boolean isTower(Position pos) {
		return true;
	}
	public boolean isStone(Position pos) {
		return true;
	}
	public boolean isBlocked(Position pos) {
		return true;
	}	
	public int getHeight(Position pos) {
		return 1;
	}
	public PlayerColor getPlayerColor(Position pos) {
		return PlayerColor.RED;
	}
}
