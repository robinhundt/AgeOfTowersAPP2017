package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class BViewer implements Viewer {
	private SimpleBoard boardO;
	private Entity[][] board;
	private int size;

	public BViewer(SimpleBoard b, Entity[][] ar, int sz) {
		boardO = b;
		board = ar;
		size = sz;
	}
	
    	public int getSize() {
		return size; 
	}
	public Status getStatus() {
		return boardO.getStatus();
	}
	public int getTurn() {
		return boardO.getTurn();
	}
    	public Move[] getPossibleMoves(Position position) {
		return new Move[2];
	}

	/**
	* Creates and returns a new {@link MoveList} object with all possible moves
	* which a figure (stone, tower, base) on the position {@link p} has
	* @param p - position of the figure.
	* @return
	*	a {@link MoveList} with all possible moves which the specified figure has.
	*/
	public MoveList possibleMoves(Position p) throws Exception {
		Entity ent = getElement(p);
		if (ent == null) throw new Exception ("Position is empty");
		return new MoveList(ent);
	}
	public boolean isTower(Position pos) {
		Entity ent = getElement(pos);
		return (ent != null && ent.isTower());
	}
	public boolean isStone(Position pos) {
		return !isBase(pos) && !getElement(pos).isTower();
	}
	public boolean isBlocked(Position pos) {
		Entity ent = getElement(pos);
		return (ent != null && !ent.isBlocked());
	}
	public int getHeight(Position pos) {
		Entity ent = getElement(pos);
		return ent.getHigh();
	}
    	public PlayerColor getPlayerColor(Position pos) {
		Entity ent = getElement(pos);
		return ent.getColor();
	}
	public boolean isEmpty(Position pos) {
		return getElement(pos) == null;
	}
	public boolean isBase(Position pos) {
		return !isEmpty(pos) && getElement(pos).isBase();
	}
	/**
	* Returns an element located on the specified position on the board.
	* @param pos the position of the element that has to be returned.
	* @return an element located on the specified position on the board.
	*/
	private Entity getElement(Position pos) {
		return board[pos.getLetter()][pos.getNumber()];
	}
}
