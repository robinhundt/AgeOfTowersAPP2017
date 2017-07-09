package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.ChangeType.*;
import java.util.Vector;
import java.util.Stack;
import java.util.ListIterator;
import java.lang.Exception;

public class Evaluator extends Board{
	
	private Stack<Change> stack = new Stack<Change>();

	private int order;

	public Evaluator(int n) {
		super(n);
	}
	public Evaluator (int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		super(size, turn, lRed, lBlue, board, redB, blueB);
	}
	public Status evaluate(Move move, int n) {
//System.out.println("turn " + turn + " order " + order + " move " + move);
//System.out.println("Stack size: " + stack.size());*/
		this.order = n;
		makeMove(move, turn);
/*System.out.println("Nach dem move");
Test.printBoard(new BViewer(this, board, size));*/
		if(order > 1 && !(status == RED_WIN || status == BLUE_WIN)) {
			Vector<Move> moves = allPossibleMoves(turn);
			PlayerColor curCol = turn;
			//int cnt = 0;
			for(Move opponentMove: moves) {
				turn = curCol;
				evaluate(opponentMove, order-1);
				order = n;
				//if(++cnt > 5) break;
				if(status == RED_WIN || status == BLUE_WIN) {
					break;
				}
			}
		}
		undoChanges();
/*System.out.println("Nach undoChanges()" + " move " + move + " order " + order);
System.out.println("Status: " + status);
Test.printBoard(new BViewer(this, board, size));
Test.showMoves(this, RED);*/
		return status;
	}

	private void undoChanges() {
		//System.out.println("Stack size: " + stack.size() + " stack order: " + stack.peek().getOrder() + " order " + order);
		while(!stack.empty() && stack.peek().getOrder() == order) {
			Change change = stack.pop();
			switch (change.getType()) {
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
			ent.removeMove(change.getPosition(), change.getRange());
		}
		catch(Exception e) {
			System.out.println("Mistake in Evaluator undoMoveAdded");
		}
	}
	private void undoMoveRemoved(Change change) {
		Entity ent = change.getEntity();
		try{
			ent.addMove(change.getPosition(), change.getRange());
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator undoMoveRemoved");
		}
	}
	private void undoAllMovesRemoved(Change change) {
		Entity ent = change.getEntity();
		//System.out.println("Entity: " + ent.getPosition());
		try{
			ent.setAllMoves(change.getAllMoves(), change.getReachable(), change.getRange(), change.getMoveCounter());
			/*System.out.println("\t\tSetze all moves zurueck");
			Position pos = ent.getPosition();
			
			System.out.println("Position: " + pos);
			System.out.println("Range: " + ent.getRange());
			printEntMoves(ent);*/
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator undoAllMovesRemoved" + e);
		}
	}
	private void undoRangeIncrease(Change change) {
		//System.out.println("\t\tundoRangeIncrease");
		Entity ent = change.getEntity();
		ent.decRange();
	}
	private void undoRangeDecrease(Change change) {
		//System.out.println("\tundoRangeDecrease");
		Entity ent = change.getEntity();
		try {
			ent.incRange(change.getRangeMoves(), change.getRangeReachable());
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator undoRangeIncrease");
		}
	}
	private void undoEntityAdded(Change change) {
		//System.out.println("\tundoEntityAdded");
		Entity ent = change.getEntity();
		Vector<Entity> list= (ent.getColor() == RED? listRed: listBlue);
		list.remove(ent);
	}
	private void undoEntityRemoved(Change change) {
		//System.out.println("\tundoEntityRemoved");
		Entity ent = change.getEntity();		
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.add(ent);
	}
	private void undoPositionChanged(Change change) {
		//System.out.println("\tundoPositionChanged");
		try {
			Position pos = change.getPosition();
			change.getEntity().setPosition(pos);
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator undoPositionChanged");
		}
	}
	private void undoElementReplaced(Change change) {
		//System.out.println("\tundoElementReplaced");
		try {
			Position pos = change.getPosition();
			board[pos.getLetter()][pos.getNumber()] = change.getEntity();
		} 
		catch(Exception e) {
			System.out.println("Mistake in Evaluator undoElementReplaced");
		}
	}
	private void undoHeightIncreased(Change change) {
		//System.out.println("\tundoHeightIncreased");
		Entity tower = change.getEntity();
		tower.decHeight();
	}
	private void undoHeightDecreased(Change change) {
		//System.out.println("\tundoHeightDecreased");
		Entity tower = change.getEntity();
		tower.incHeight();
	}
	private void undoTowerBlocked(Change change) {
		//System.out.println("\tundoTowerBlocked");
		Entity tower = change.getEntity();
		tower.setBlocked(false);
	}
	private void undoTowerUnblocked(Change change) {
		//System.out.println("\tundoTowerUnblocked");
		Entity tower = change.getEntity();
		tower.setBlocked(true);
	}
	/**
	* Puts the specified figure on the specified position on the board.
	* @param ent the figure in question.
	* @param pos position for the speciifiied figure to be placed on.
	*/
	@Override
	protected void setElement(Entity ent, Position pos) {
		int x = pos.getLetter();
		int y = pos.getNumber();
		stack.push(new Change(board[x][y], pos, ELEMENT_REPLACED, order));
		board[x][y] = ent;
	}
	@Override
	protected void block(Entity tower) {
		stack.push(new Change(tower, TOWER_BLOCKED, order));
		tower.setBlocked(true);
	}
	@Override
	protected void unblock(Entity tower) {
		stack.push(new Change(tower, TOWER_UNBLOCKED, order));
		tower.setBlocked(false);
	}
	@Override
	protected void addMove(Entity ent, Position pos, int range) {
		if(!ent.hasMove(pos, range)) {
			stack.push(new Change(ent, pos, range, MOVE_ADDED, order));
			ent.addMove(pos, range);
		}
	}
	private void addMoveNotSave(Entity ent, Position pos, int range) {
		ent.addMove(pos, range);
	}
	@Override
	protected void removeMove(Entity ent, Position pos, int range) {
		if(ent.hasMove(pos, range)) {
			stack.push(new Change(ent, pos, range, MOVE_REMOVED, order));
			ent.removeMove(pos, range);
		}
	}
	@Override
	protected void removeAllMoves(Entity ent) {
		stack.push(new Change(ent, ent.getMoves(), ent.getReachable(), ent.getRange(), ent.getMoveCounter(),order));
		ent.removeAllMoves();
	}
	/**
	* Increases the step width of the specified figure (stone) by n and adds 
	* newly available positions in the new range to its list of possible moves.
	* @param stone the figure (stone) whose step width has to be increased.
	* @param n amount of steps that has to be added.
	*/
	@Override
	protected void addRanges(Entity stone, int n) {
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
	@Override
	protected void incRange(Entity ent) {
		stack.push(new Change(ent, RANGE_INC, order));		
		ent.incRange();
	}
	@Override
	protected void decRange(Entity ent) {
		int range = ent.getRange();
		stack.push(new Change(ent, ent.getRangeMoves(range), ent.getRangeReachable(range), range, order));
		ent.decRange();
	}
	@Override
	protected void incHeight(Entity tower) {
		stack.push(new Change(tower, HEIGHT_INCREASED, order));
		tower.incHeight();
	}
	@Override
	protected void decHeight(Entity tower) {
		stack.push(new Change(tower, HEIGHT_DECREASED, order));
		tower.decHeight();
	}
	@Override
	protected void setPosition(Entity ent, Position pos) {
		stack.push(new Change(ent, ent.getPosition(), POSITION_CHANGED, order));
		ent.setPosition(pos);
	}
	/**
	* Adds the specified figure to the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	@Override
	protected void addToList(Entity ent) {
		stack.push(new Change(ent, ENTITY_ADDED, order));
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.add(ent);
	}
	/**
	* Removes the specified figure from the the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	@Override
	protected void removeFromList(Entity ent) {
		stack.push(new Change(ent, ENTITY_REMOVED, order));
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.remove(ent);
	}
	@Override
	public Vector<Move> allPossibleMoves(PlayerColor col) {
		Vector<Move> allMoves = new Vector<Move>();
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			Vector<Vector<Move>> entMoves = ent.getMoves();
			for(Vector<Move> rangeMoves: entMoves) {
				for(Move move: rangeMoves) {
					allMoves.add(move);
				}
			}
		}
		return allMoves;
	}
	private void printEntMoves(Entity ent) {
		Vector<Vector<Move>> moves = ent.getMoves();
		for(Vector<Move> rangeMoves: moves) {
			for(Move move: rangeMoves) {
				System.out.print(move + " ");
			}
		}
		System.out.println();
	}
}

