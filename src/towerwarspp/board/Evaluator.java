package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.ChangeArt.*;
import java.util.Vector;
import java.util.Stack;
import java.util.ListIterator;
import java.lang.Exception;

public class Evaluator extends SimpleBoard{
	/*protected int size;
        protected PlayerColor turn = RED;
        protected Status status = OK;
        protected Vector<Entity> listRed = new Vector<Entity>();
        protected Vector<Entity> listBlue = new Vector<Entity>();
        protected Entity[][] board;
        protected Position redBase;
        protected Position blueBase;
	private Move curMove;*/
	private Stack<Change> stack = new Stack<Change>();
	private int order;

	public Evaluator(int n) {
		super(n);
	}
	public Evaluator (int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		super(size, turn, lRed, lBlue, board, redB, blueB);
	}
	public Status evaluate(Move move, int order) {
		this.order = order;
		makeMove(move, turn);
		int res = 0;
		if (status == RED_WIN || status == BLUE_WIN) {
			res = Integer.MAX_VALUE;
		}
		else if(order > 1) {
			//Vector Moves = ;
		}
		undoChanges();
		return status;
	}

	private void undoChanges() {

	}
	/**
	* Puts the specified figure on the specified position on the board.
	* @param ent the figure in question.
	* @param pos position for the speciifiied figure to be placed on.
	*/
	private void setElement(Entity ent, Position pos) {
		int x = pos.getLetter();
		int y = pos.getNumber();
		stack.push(new Change(board[x][y], pos, ELEMENT_REPLACED, order));
		board[x][y] = ent;
	}
	
	private void block(Entity tower) {
		stack.push(new Change(tower, TOWER_BLOCKED, order));
		tower.setBlocked(true);
	}
	private void unblock(Entity tower) {
		stack.push(new Change(tower, TOWER_UNBLOCKED, order));
		tower.setBlocked(false);
	}
	private void addMove(Entity ent, Position pos, int range) {
		stack.push(new Change(ent, pos, range, MOVE_ADDED, order));
		ent.addMove(pos, range);
	}
	private void addMoveNotSave(Entity ent, Position pos, int range) {
		ent.addMove(pos, range);
	}
	private void removeMove(Entity ent, Position pos, int range) {
		stack.push(new Change(ent, pos, range, MOVE_REMOVED, order));
		ent.removeMove(pos, range);
	}
	private void removeAllMoves(Entity ent) {
		stack.push(new Change(ent, ent.getMoves(), ent.getReachable(), order));
		ent.removeAllMoves();
	}
	/**
	* Increases the step width of the specified figure (stone) by n and adds 
	* newly available positions in the new range to its list of possible moves.
	* @param stone the figure (stone) whose step width has to be increased.
	* @param n amount of steps that has to be added.
	*/
	private void addSteps(Entity stone, int n) {
		for(int i = 0; i < n; ++i) {
			incRange(stone);
			Vector<Position> opponents = findPositionsInRange(stone.getPosition(), stone.getRange());
			ListIterator<Position> it = opponents.listIterator();
			while(it.hasNext()) {
				Position opponentPos = it.next(); 
				if(checkMoveForStone(opponentPos, stone.getColor(), stone.getRange() )) {
					addMoveNotSave(stone, opponentPos, stone.getRange());
				}
			}
		}
	}
	private void incRange(Entity ent) {
		stack.push(new Change(ent, RANGE_INC, order));
		ent.incRange();
	}
	private void decRange(Entity ent) {
		int range = ent.getRange();
		stack.push(new Change(ent, ent.getRangeMoves(range), ent.getRangeReachable(range), range, order));
		ent.decRange();
	}
	private void incHeight(Entity tower) {
		stack.push(new Change(tower, HEIGHT_INCREASED, order));
		tower.incHeight();
	}
	private void decHeight(Entity tower) {
		stack.push(new Change(tower, HEIGHT_DECREASED, order));
		tower.decHeight();
	}
	private void setPosition(Entity ent, Position pos) {
		stack.push(new Change(ent, ent.getPosition(), POSITION_CHANGED, order));
		ent.setPosition(pos);
	}
	/**
	* Adds the specified figure to the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	private void addToList(Entity ent) {
		stack.push(new Change(ent, ENTITY_ADDED, order));
		if(ent.getColor() == RED) {
			listRed.add(ent);
		}
		else {
			listBlue.add(ent);
		}
	}
	/**
	* Removes the specified figure from the the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	private void removeFromList(Entity ent) {
		stack.push(new Change(ent, ENTITY_REMOVED, order));
		if(ent.getColor() == RED) listRed.remove(ent);
		else listBlue.remove(ent);
	}
}

