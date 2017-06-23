package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Vector;
//import java.util.ListIterator;

public class Entity {
	private Position position;
	private Vector<Move> moves;
	private PlayerColor color;
	private int high = 0; 
	private int intRepr;
	private boolean blocked = false;
	public Entity(Position p, Vector<Move> possibleMoves, PlayerColor col) {
		position = p;
		moves = possibleMoves;
		color = col;
		intRepr = 1;
	}
	public void addMove(Move move) {
		moves.add(move);
	}
	public Vector<Move> getMoves() {
		return moves;
	}
	public int getMovesNumber() {
		return moves.size();
	}
	public PlayerColor getColor() {
		return color;
	}
	public boolean moveIsPossible(Move move) {
		return moves.contains(move);
	}
	public boolean removeMove(Move move) {
		return moves.remove(move);
	}
	public void setPosition(Position p) {
		position = p;
	}
	public Position getPosition() {
		return position;
	}
	public boolean isTurm() {
		return high > 0;
	}
	public void addStone() {
		high++;
	}
	public void removeStone() {
		high--;
	}
	public boolean maxHigh() {
		return false;
	}
	public boolean isBase() {
		return high < 0;
	}
	public int getIntRepr() {
		return intRepr;
	}
	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean bl) {
		blocked = bl;
	} 
}
