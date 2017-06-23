package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Board implements Viewable {
	private int size;
	private PlayerColor turn = PlayerColor.RED;
	private Status status = Status.OK;
	private Vector<Position> listRed = new Vector<>();
	private Vector<Position> listBlue = new Vector<>();
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
	* Gives a viewer for this board.
	* @return
	*	a viewer for this board. 
	*/
	public Viewer viewer() {
		return new BoardViewer(this);
	}
	/**
	* Gives the size of the board.
	* @return
	*	the size of the board. 
	*/
	public int getSize() {
		return size;
	}
	/**
	* Gives the current turn.
	* @return
	*	the curren turn. 
	*/
	public int getTurn() {
		return (turn == PlayerColor.RED? 1: 2);
	}
	/**
	* Gives the status of the board.
	* @return
	*	the status of the board. 
	*/
	public Status getStatus() {
		return status;
	}
	/**
	* Gives a representation of the board as a 2-dimensional integer array.
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
	* Changes the board status accordingly and gives it out.
	* @param move - new move to be executed.
	* @param col - color of the figure which has to be moved.
	* @return
	*	a status of the board after checking or checking and executing the move. 
	*/
	public Status update(Move move, PlayerColor col) {
		Position start = move.getStart();
		Entity ent = board[start.getLetter()][start.getNumber()];
		if (ent == null || ent.getColor() != col || !ent.moveIsPossible(move)) {
			status = Status.ILLEGAL;
			return status;
		}
		boolean color = (col == PlayerColor.RED);
		Position end = move.getEnd();
		ent = changeStart(ent, start.getLetter(), start.getNumber());
		ent.setPosition(start);
		changeEnd(ent, end.getLetter(), end.getNumber(), color);
		checkWin();
		return status;
	}
	private Entity changeStart(Entity ent, int oldX, int oldY) {
		if (ent.isTurm()) {
			ent.removeStone();
			actualiseTurmStoneRemoved(ent);
			boardInt[oldX][oldY]--;	
			return new Entity(null, new Vector<Move>(), ent.getColor());
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
			addToList(ent.getPosition(), col);
		}
		else if	(opponent.getColor() != ent.getColor()) {
			if(opponent.isTurm()) {
				if(distance(opponent.getPosition(), ent.getPosition()) > 1) {
					opponent.setBlocked(true);
					actualiseTurmBlocked(opponent.getPosition(), !col);
				}
				else {
					setElement(ent, ent.getIntRepr(), newX, newY);
					addToList(ent.getPosition(), col);
					if(col) {
						listBlue.remove(opponent);
					}
					else {
						listRed.remove(opponent);
					}
					actualiseTurmRemoved(opponent, !col);
				}
			}					
		} 
		else if(opponent.isBlocked()) {
			opponent.setBlocked(false);
			actualiseTurmUnblocked(opponent.getPosition(), col);
		}
		else {
			opponent.addStone();
			if (opponent.maxHigh()) {
				actualiseTurmMaxHigh(opponent.getPosition(), col);
			}
			else actualiseTurmStoneAdded(opponent, col);	
		}
	}
	private void actualiseTurmBlocked(Position p, boolean col) {

	}
	private void actualiseTurmUnblocked(Position p, boolean col) {

	}
	private void actualiseTurmRemoved(Entity turm, boolean col) {

	}
	private void actualiseTurmStoneAdded(Entity turm, boolean col) {

	}	
	private void actualiseTurmStoneRemoved(Entity turm) {

	}
	private void actualiseTurmMaxHigh(Position p, boolean col) {

	}
	private void findMoves(Entity ent) {

	}
	private void checkWin() {

	}
	private void setElement(Entity ent, int repr, int x, int y) {
		board[x][y] = ent;
		boardInt[x][y] = repr;
	}
	private void addToList(Position p, boolean col) {
		if(col)
			listRed.add(p);
		else 
			listBlue.add(p);
	}

	/**
	* Gives all possible moves which a player with the color col has.
	* @param col - player's color.
	* @return
	*	a vector with all possible moves of the player or an empty vector if there are no such moves.
	*/
	public Vector<Move> allPossibleMoves(PlayerColor col) throws Exception {
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
	}
	
	/**
	* Gives all possible moves which a figure (stone, tower, base) on the position pos has.
	* @param p - position of the figure.
	* @return
	*	a vector with all possible moves which the specified figure has.
	*/
	public Move[] stoneMoves(Position p) throws Exception {
		if (p == null) throw new Exception("IllegalArgumentException");
		Entity ent = board[p.getLetter()][p.getNumber()];
		return ent.getMoves().toArray(new Move[ent.getMovesNumber()]);
	}
}
