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
		if(order > 1 && !(status == RED_WIN || status == BLUE_WIN)) {
			Vector<Move> moves = allPossibleMoves(turn);
			for(Move opponentMove: moves) {
				evaluate(opponentMove, order-1);
				if(status == RED_WIN || status == BLUE_WIN) {
					break;
				}	
			}
		}
		undoChanges();
		++order;
		return status;
	}

	private void undoChanges() {
		while(!stack.empty() && stack.peek().getOrder() == order) {
			Change change = stack.pop();
			switch (change.getArt()) {
				case MOVE_ADDED: undoMoveAdded(change); break;
				case MOVE_REMOVED: undoMoveRemoved(change); break;
				case ALL_MOVES_REMOVED: undoAllMovesRemoved(change); break;
				case RANGE_INC: undoRangeIncrease(change); break;
				case RANGE_DEC: undoRangeDecrease(change); break;
				case ENTITY_ADDED: undoEntityAdded(change); break;
				case ENTITY_REMOVED: undoEntityRemoved(change); break;
				case POSITION_CHANGED: undoPositionChanged(change); break;
				case ELEMENT_REPLACED: undoElementReplaced(change); break;
				case HEIGHT_INCREASED: undoHeightIncreased(change); break;
				case HEIGHT_DECREASED: undoHeightDecreased(change); break;
				case TOWER_BLOCKED: undoTowerBlocked(change); break;
				case TOWER_UNBLOCKED: undoTowerUnblocked(change);
			}	 
		}
	}
	private void undoMoveAdded(Change change) {
		Entity ent = change.getEntity();
		try{
			ent.removeMove(change.getMoveEndPos(), change.getRange());
		}
		catch(Exception e) {
			System.out.println("Mistake in Evaluator");
		}
	}
	private void undoMoveRemoved(Change change) {
		Entity ent = change.getEntity();
		try{
			ent.addMove(change.getMoveEndPos(), change.getRange());
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator");
		}
	}
	private void undoAllMovesRemoved(Change change) {
		Entity ent = change.getEntity();
		try{
			ent.setAllMoves(change.getAllMoves(), change.getReachable());
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator");
		}
	}
	private void undoRangeIncrease(Change change) {
		Entity ent = change.getEntity();
		ent.decRange();
	}
	private void undoRangeDecrease(Change change) {
		Entity ent = change.getEntity();
		try {
			ent.incRange(change.getRangeMoves(), change.getRangeReachable());
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator");
		}
	}
	private void undoEntityAdded(Change change) {
		Entity ent = change.getEntity();
		Vector<Entity> list= (ent.getColor() == RED? listRed: listBlue);
		list.remove(ent);
	}
	private void undoEntityRemoved(Change change) {
		Entity ent = change.getEntity();		
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.add(ent);
	}
	private void undoPositionChanged(Change change) {

	}
	private void undoElementReplaced(Change change) {
		try {
			Position pos = change.getPosition();
			board[pos.getLetter()][pos.getNumber()] = change.getEntity();
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator");
		}
	}
	private void undoHeightIncreased(Change change) {
		Entity tower = change.getEntity();
		tower.decHeight();
	}
	private void undoHeightDecreased(Change change) {
		Entity tower = change.getEntity();
		tower.incHeight();
	}
	private void undoTowerBlocked(Change change) {
		Entity tower = change.getEntity();
		tower.setBlocked(false);
	}
	private void undoTowerUnblocked(Change change) {
		Entity tower = change.getEntity();
		tower.setBlocked(true);
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
	private void addRanges(Entity stone, int n) {
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
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.add(ent);
	}
	/**
	* Removes the specified figure from the the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	private void removeFromList(Entity ent) {
		stack.push(new Change(ent, ENTITY_REMOVED, order));
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.remove(ent);
	}

	public Vector<Move> allPossibleMoves(PlayerColor col) {
		Vector<Move> allMoves = new Vector<Move>();
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			getEntityMoves(ent, allMoves);
		}
		return allMoves;
	}
}

