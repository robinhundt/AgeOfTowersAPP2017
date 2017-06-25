package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Vector;
//import java.util.ListIterator;

public class Entity {
	private Position myPosition;
	private Vector<Position> moves;
	private PlayerColor color;
	private int high = 0; 
	private int intRepr;
	private boolean blocked = false;
	private int step = 0;
	public Entity(Position p, Vector<Position> possibleMoves, PlayerColor col) {
		myPosition = p;
		moves = possibleMoves;
		color = col;
		intRepr = 1;
	}
	public void addMove(Position moveEnd) {
		moves.add(moveEnd);
	}
	public boolean removeMove(Position moveEnd) {
		return moves.remove(moveEnd);
	}
	public Vector<Position> getMoves() {
		return moves;
	}
	public boolean hasMove() {
		return (!blocked && !moves.isEmpty());
	}
	public int getMovesNumber() {
		return moves.size();
	}
	public PlayerColor getColor() {
		return color;
	}
	public boolean moveIsPossible(Position endPos) {
		return moves.contains(endPos);
	}
	public void setPosition(Position p) {
		myPosition = p;
	}
	public Position getPosition() {
		return myPosition;
	}
	public boolean isTower() {
		return high > 0;
	}
	public void addStone() {
		high++;
	}
	public void removeStone() {
		high--;
	}
	public int getHigh() {
		return high;
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
	public void stepDecrease(int change) {
		step -= change;
	}
	public void stepInrease(int change) {
		step += change;
	}
}
