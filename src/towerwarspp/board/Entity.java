package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Vector;
import java.util.ListIterator;

public class Entity {
	private Position position;
	private PlayerColor color;
	private Vector<Position> moves;
	boolean base;
/**
* Includes information about possible moves
*/
	private int[] canGo;
/**
* Includes information about amount of step which this Entity needs to rich other positions:
* canReach[i][j] == 0 -> this Entity can NOT reach the position (i, j);
* canReach[i][j] = x -> this Entity can reach the position (i,j) in x soteps
*/
	private int[][] nSteps;
	//private PlayerColor color;	// color of this Entity
	int size;
	private int high = 0;	// 0 - the high of a single stone
	private int maxHigh; 	// max possible high for a tower
	private boolean blocked = false;	// shows if the Entity is blocked (relevant only for towers)
	private int step = 1;			// current step size of this Entity
/**
* Construktor
*/
	public Entity(Position p, PlayerColor col, int size) {
		position = p;
		color = col;
		base = false;
		this.size = size;
		moves = new Vector<Position>();
		maxHigh = size/3;
		nSteps = new int[size+1][size+1];
	}
	public Entity(Position p, PlayerColor col, int size, boolean isBase) {
		position = p;
		color = col;
		this.base = isBase;
		this.size = size;
		moves = new Vector<Position>();
		maxHigh = size/3;
		nSteps = new int[size+1][size+1];
	}
/**
* Ensures that the move with end position {@link moveEnd} is in the list of possible moves.
* @param moveEnd end position of the move in question.
* @param stps amount of steps needed to reach {@link moveEnd} from {@link myPosition}.
* 
* HOW IT WORKS:
* 1. prooves, if the move is already in the list;
* 	if so - return, else - DO:
* 2. add the move to the list of possible moves {@link moves}
* 3. add the move to {@link canGo}
* 4. add stps to {@link nSteps}
*/
	public void addMove(Position moveEnd, int stps) {
		if(!moves.contains(moveEnd)) {
			moves.add(moveEnd);
		}
	}
	/*public void addMove(Position moveEnd) {
		if(!moves.contain(moveEnd)) {
			moves.add(moveEnd);
		}
	}*/
/**
* removes the move from the possible moves list.
*/
	public boolean removeMove(Position moveEnd, int stps) {
		return moves.remove(moveEnd);
	}
	/**
	* Calculates the distance between two positions on the board.
	* @param a the first position.
	* @param b the second position.
	* @return the distance between the positions a and b.
	*/
	private int distance (Position a, Position b) {
		int x = a.getLetter() - b.getLetter();
		int y = a.getNumber() - b.getNumber();
		int z = x - y;
		return (Math.abs(x) + Math.abs(y) + Math.abs(z))/2;

	}
	public boolean isBase() {
		return base;
	}
	public Position getPosition() {
		return position;
	}
	public PlayerColor getColor() {
		return color;
	}	
/**
* For example: creates new {@link moves},  {@link canGo}, {@link nSteps}.
*/
	public void removeAllMoves() {
		moves = new Vector<Position>();
	}
/**
* Returns the list of all possible moves (their end positions) for this Entity.
*/
	public Vector<Position> getMoves() {
		return moves;
	}
/**
* Prooves if this Entity is not blocked and has moves.
*/ 
	public boolean canMove() {
		return (!blocked && !moves.isEmpty());
	}
/**
* Returns the number of all possible moves for this Entity.
*/
	public int getMovesNumber() {
		return moves.size();
	}
/**
* Returns true, if there is a possible move to the position {@link endPos}.
* @param endPos - end position of the move in question.
*/
	public boolean hasMove(Position endPos, int dist) {
		return moves.contains(endPos);
	}

	public void setPosition(Position p) {
		position = p;
		step = 1;
		removeAllMoves();
	}
	public int getStep() {
		return step;
	}
	public void setMinimalStep() {
		step = 1;
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
		return high == maxHigh;
	}
	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean bl) {
		blocked = bl;
	}
/**
* Removes all positions which can be reached in {@link step} steps from the list of possible moves.
* Decreases current {@link step} by 1.
*/
	public void removeStep() {
		ListIterator<Position> it = moves.listIterator();
		while(it.hasNext()) {
			if(distance(position, it.next()) == step) {
				it.remove();
			}
		}
		--step;
	}
/**
* Adds all positions which can be reached in {@link step}+1 steps to the list of possible moves.
* Increases current {@link step} by 1.
*/

	public void addStep() {
		++step;
	}
}
