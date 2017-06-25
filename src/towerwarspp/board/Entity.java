package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Vector;
//import java.util.ListIterator;

public class Entity {
	private Position myPosition;
	private Vector<Position> moves;
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
	private PlayerColor color;	// color of this Entity
	int size;
	private int high = 0;	// 0 - the high of a single stone
	private int maxHigh; 	// max possible high for a tower
	private boolean blocked = false;	// shows if the Entity is blocked (relevant only for towers)
	private int step = 1;			// current step size of this Entity
	
/**
* Construktor
*/
	public Entity(Position p, Vector<Position> possibleMoves, PlayerColor col, int size) {
		myPosition = p;
		moves = possibleMoves;
		color = col;
		this.size = size;
		maxHigh = size/3;
		nSteps = new int[size][size];
	}
/**
* Ensures that the move with end position {@link moveEnd} is in the list of possible moves.
* @param moveEnd - end position of the move in question.
* @param stps - amount of steps needed to reach {@link moveEnd} from {@link myPosition}.
* 
* HOW IT WORKS:
* 1. prooves, if the move is already in the list;
* 	if so - return, else - DO:
* 2. add the move to the list of possible moves {@link moves}
* 3. add the move to {@link canGo}
* 4. add stps to {@link nSteps}
*/
	public void addMove(Position moveEnd, int stps) {
		// if(canGo includes moveEnd) return;
		// else do: 
		// add moveEnd to canGo
		moves.add(moveEnd);
		nSteps[moveEnd.getLetter()][moveEnd.getNumber()] = stps;
	}
	public void addMove(Position moveEnd) {
		// if nSteps[][] == 0 - fehler
		// else: add moveEnd to canGo
		moves.add(moveEnd);
	}
/**
* removes the move from {@link moves} and {@link canGo}. {@link nSteps} does not have to be changed.
*/
	public boolean removeMove(Position moveEnd) {
		return moves.remove(moveEnd);
	}

/**
* Removes all moves which need at least {@link stps} steps from {@link moves} and {@link canGo}. {@link nSteps} does not have to be changed.
* For ex.: traveres {@link moves} with an iterator an removes all positions (i, j) from this list if nSteps[i][j] >= stps.
* @param stps
*/
	public void removeMoves(int stps) {
		;
	}
/**
* For example: creates new {@link moves},  {@link canGo}, {@link nSteps}.
*/
	public void removeAllMoves() {
		;
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
	public boolean hasMove(Position endPos) {
		return moves.contains(endPos);
	}
/**
* Returns true, if there is a move to the position {@link endPos} and this move is a remote one.
* @param endPos - end position of the move in question.
*/
	public boolean hasRemoteMove(Position endPos) {
		// If the move is NOT in {@link canGo} return false. Else DO:
		return nSteps[endPos.getLetter()][endPos.getNumber()] > 1;
	}
/**
* Prooves if this Entity can reach the position {@link pos} with the current value of {@link step}.
* @param pos the position in question.
*/ 
	public boolean canReach(Position pos) {
		int st = nSteps[pos.getLetter()][pos.getNumber()];
		return (st > 0 && st <= step);
	}
/**
* Set informatian about field (x,y) to {@link nSteps}
*/
	public void setReach(int x, int y, int stps) {
		nSteps[x][y] = stps;
	}
/**
* Prooves if this Entity can reach the position {@link pos} in {@link stps} or less steps.
* @param pos the position in question.
* @param stps - the maximum allowed number of steps.
*/ 
	public boolean canReach(Position pos, int stps) {
		int st = nSteps[pos.getLetter()][pos.getNumber()];
		return (st > 0 && st <= stps);
	}
	public PlayerColor getColor() {
		return color;
	}
	public void setPosition(Position p) {
		myPosition = p;
		nSteps = new int[size][size];
		canGo = new int[size];
		step = 1;

	}
	public Position getPosition() {
		return myPosition;
	}
	public int getStep() {
		return step;
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
	public boolean isBase() {
		return high < 0;
	}

	public boolean isBlocked() {
		return blocked;
	}
	public void setBlocked(boolean bl) {
		blocked = bl;
	}
	public void stepDecrease(int change) {
		step -= change;
		removeMoves(step);
	}
	public int stepIncrease(int change) {
		step += change;
		return step;
	}
}
