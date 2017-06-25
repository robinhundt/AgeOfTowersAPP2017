package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception; 

public class Board implements Viewable {
	private int size;
	private PlayerColor turn = PlayerColor.RED;
	private Status status = Status.OK;
	private Vector<Entity> listRed = new Vector<Entity>();
	private Vector<Entity> listBlue = new Vector<Entity>();
	private Entity[][] board; 
	private int[][] boardInt;
	private Position redBase;
	private Position blueBase;

	/**
	* Initialises a new object of the Board class.
	* @param n - size of the new board. 
	*/	
	public Board (int n) {
		board = new Entity[n][n];
		boardInt = new int[n][n];
		setBoards();
		size = n;
	}
	private void setBoards() {
		;
	}
	/**
	* Returns a viewer for this board.
	* @return
	*	a viewer for this board. 
	*/
	public Viewer viewer() {
		return new BoardViewer(this);
	}
	/**
	* Returns the size of the board.
	* @return
	*	the size of the board. 
	*/
	public int getSize() {
		return size;
	}
	/**
	* Returns the current turn.
	* @return
	*	the curren turn. 
	*/
	public int getTurn() {
		return (turn == PlayerColor.RED? 1: 2);
	}
	/**
	* Returns the status of the board.
	* @return
	*	the status of the board. 
	*/
	public Status getStatus() {
		return status;
	}
	/**
	* Returns a representation of the board as a 2-dimensional integer array.
	* @return
	*	a representation of the board as a 2-dimensional integer array. 
	*/
	public int[][] getBoard() { 
		return boardInt;
	}
	private int distance (Position a, Position b) {
		int x = a.getLetter() - b.getLetter();
		int y = a.getNumber() - b.getNumber();
		int z = x - y;
		return (Math.abs(x) + Math.abs(y) + Math.abs(z))/2;
	
	}
	private void setTurn(PlayerColor t) {
		turn = t;
	}
	/**
	* Evaluates the specified move.
	* @param move - the move which has to be evaluated.
	* @return
	*	a score of the move. 
	*/
	public int scoreMove(Move move) {
		return 0;
	}
	/**
	* Checks if the specified move is possible and if so, updates the board: executes the move. 
	* Illegal moves will not be executed. 
	* Changes the board status accordingly and returns it.
	* @param move - new move to be executed.
	* @param col - color of the figure which has to be moved.
	* @return
	*	a status of the board after checking or checking and executing the move. 
	*/
	public Status update(Move move, PlayerColor col) {
		Position start = move.getStart();
		Position end = move.getEnd();
		Entity ent = board[start.getLetter()][start.getNumber()];
		if (ent == null || ent.getColor() != col || !ent.moveIsPossible(end)) {
			status = Status.ILLEGAL;
			return status;
		}
		boolean color = (col == PlayerColor.RED);
		ent = changeStart(ent, start.getLetter(), start.getNumber());
		ent.setPosition(end);
		changeEnd(ent, end.getLetter(), end.getNumber(), color);
		checkWin();
		return status;
	}
	private Entity changeStart(Entity ent, int oldX, int oldY) {
		if (ent.isTower()) {
			ent.removeStone();
			actualiseTowerStoneRemoved(ent);
			boardInt[oldX][oldY]--;	
			Entity newStone = new Entity(null, new Vector<Position>(), ent.getColor());
			addToList(newStone, ent.getColor() == PlayerColor.RED);
			return newStone;
		}
		setElement(null, 0, oldX, oldY);
		return ent;	
	}
	private void changeEnd(Entity ent, int newX, int newY, boolean col) {
		Entity opponent = board[newX][newY];
		if(opponent.isBase()){
			setElement(ent, ent.getIntRepr(), newX, newY); 
			status = (col? Status.RED_WIN : Status.BLUE_WIN);
			return;
		}
		if(opponent == null) {
			setElement(ent, ent.getIntRepr(), newX, newY);
			findMoves(ent);
			return;
		}
		if (opponent.getColor() != ent.getColor()) {
			if(opponent.isTower()) {
				if(distance(opponent.getPosition(), ent.getPosition()) > 1) {
					opponent.setBlocked(true);
					actualiseTowerBlocked(opponent, !col);
					removeFromList(ent, col);
				}
				else {
					setElement(ent, ent.getIntRepr(), newX, newY);
					removeFromList(opponent, !col);
					actualiseTowerRemoved(opponent, !col);
				}
			}
			return;					
		} 
		removeFromList(ent, col);
		if(opponent.isBlocked()) {
			opponent.setBlocked(false);
			actualiseTowerUnblocked(opponent, col);
			return;
		}
		opponent.addStone();
		if (opponent.maxHigh()) {
			actualiseTowerMaxHigh(opponent.getPosition(), col);
		}
		else actualiseTowerStoneAdded(opponent, col);
	}
	private void positionClosed(Position closedPos, boolean col) {
		Vector<Entity> list = (col? listRed: listBlue);
		ListIterator<Entity> it = list.listIterator();
		while(it.hasNext()) {
			it.next().removeMove(closedPos);
		}
	}
	private void positionOpened(Position openedPos, boolean col) {
		Vector<Entity> list = (col? listRed: listBlue);
		ListIterator<Entity> it = list.listIterator();
		while(it.hasNext()) {
			it.next().addMove(openedPos);
		}
	}
	private void actualiseTowerBlocked(Entity tower, boolean col) {
		int high = tower.getHigh();
		Vector<Position> neighbours = tower.getMoves();
		ListIterator<Position> it = neighbours.listIterator();
		Position p;	
		while(it.hasNext()) {
			p = it.next();
			Entity ent = board[p.getLetter()][p.getNumber()];
			if(ent == null) continue;
			ent.stepDecrease(high);
			findMoves(ent);
		}
		positionClosed(tower.getPosition(), !col);
	}
	private void actualiseTowerUnblocked(Entity tower, boolean col) {

	}
	private void actualiseTowerRemoved(Entity tower, boolean col) {

	}
	private void actualiseTowerStoneAdded(Entity tower, boolean col) {

	}	
	private void actualiseTowerStoneRemoved(Entity tower) {

	}
	private void actualiseTowerMaxHigh(Position p, boolean col) {

	}
	private void findMoves(Entity ent) {

	}
	private void checkWin() {

	}
	private void setElement(Entity ent, int repr, int x, int y) {
		board[x][y] = ent;
		boardInt[x][y] = repr;
	}
	private void addToList(Entity ent, boolean col) {
		if(col)
			listRed.add(ent);
		else 
			listBlue.add(ent);
	}
	private void removeFromList(Entity ent, boolean col) {
		if(col) {
			listRed.remove(ent);
		}
		else {
			listBlue.remove(ent);
		}
	}
	/**
	* Returns all possible moves which a player with the color col has.
	* @param col - player's color.
	* @return
	*	a vector with all possible moves of the player or an empty vector if there are no such moves.
	*/
	/*public Vector<Move> allPossibleMoves(PlayerColor col) throws Exception {
		Vector<Position> list = (col == PlayerColor.RED? listRed: listBlue);
		Vector<Move> moves = new Vector<Move>();
		ListIterator<Position> it = list.listIterator();
		Position pos;
		Entity ent;
		while(it.hasNext()) {
			pos = it.next();
			ent = board[pos.getLetter()][pos.getNumber()];
			if (ent == null) throw new Exception ("Mistake: PossibleMoves-search");
			moves.addAll(ent.getMoves());
		}
		return moves;
	}*/
	public Vector<Move> allPossibleMoves(PlayerColor col) throws Exception {
		return null;
	}
	/**
	* Returns all possible moves which a figure (stone, tower, base) on the position pos has.
	* @param p - position of the figure.
	* @return
	*	a vector with all possible moves which the specified figure has.
	*/
	/*public Move[] stoneMoves(Position p) throws Exception {
		if (p == null) throw new Exception("IllegalArgumentException");
		Entity ent = board[p.getLetter()][p.getNumber()];
		return ent.getMoves().toArray(new Move[ent.getMovesNumber()]);
	}*/
	public Move[] stoneMoves(Position p) throws Exception {
		return null;
	}

	/**
	* Returns all possible moves which a player with the color {@link col} has.
	* @param col - player's color.
	* @return
	*	a vector with all possible moves of the player packed in {@link MoveList} objects or an empty vector if there are no such moves.
	*/
	public Vector<MoveList> getAllPossibleMoves(PlayerColor col) throws Exception {
		Vector<MoveList> moves = new Vector<MoveList>();
 		Vector<Entity> list = (col == PlayerColor.RED? listRed: listBlue);
		ListIterator<Entity> it = list.listIterator();
		while(it.hasNext()) {
			Entity ent = it.next();
			if(ent.hasMove()) {
				moves.add(new MoveList(ent));
			}
		}
		return moves;
	}
	/**
	* Creates and returns a new {@link MoveList} object with all possible moves
	* which a figure (stone, tower, base) on the position {@link p} has
	* @param p - position of the figure.
	* @return
	*	a {@link MoveList} with all possible moves which the specified figure has.
	*/
	public MoveList getStoneMoves(Position p) throws Exception {
		Entity ent = board[p.getLetter()][p.getNumber()];
		if (ent == null) throw new Exception ("Position is empty");
		return new MoveList(ent);
	}
}
