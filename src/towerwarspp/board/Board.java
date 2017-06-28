package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Board implements Viewable {
        private int size;
        private PlayerColor turn = RED;
        private Status status = Status.OK;
        private Vector<Entity> listRed = new Vector<Entity>();
        private Vector<Entity> listBlue = new Vector<Entity>();
        private Entity[][] board;
        private Position redBase;
        private Position blueBase;

        /**
        * Initialises a new object of the Board class.
        * @param n - size of the new board.
        */
        public Board (int n) {
                board = new Entity[n+1][n+1];
                initializeBoard();
                size = n;
        }
        private void initializeBoard() {
                redBase = new Position(1, 1);
                board[1][1] = new Entity (redBase, RED, size);
                blueBase = new Position(size, size);
                board[size][size] = new Entity (blueBase, BLUE, size);
                int d = size/2;
                int y = 1;
                int lastX = 1 + d;
                int lastY = 1 + d;
	 	for(int x = 2; x <= lastX; ++x) {
                        Entity ent = new Entity(new Position(x, y), RED, size);
                        board[x][y] = ent;
                        initialiseEntityMoves(ent, x, y);
                        listRed.add(ent);
                }
                for (y = 2; y <= lastY; ++y) {
                         --lastX;
                        for (int x = 1; x <= lastX; ++x) {
                                Entity ent = new Entity(new Position(x, y), RED, size);
                                board[x][y] = ent;
                                initialiseEntityMoves(ent, x, y);
                                listRed.add(ent);
                        }
                }
                y = size;
		lastX = size - 1 - d;
		lastY = size - 1 - d;
		for(int x = size-1; x >= lastX; --x) {
			Entity ent = new Entity(new Position(x, y), RED, size);
			board[x][y] = ent;
			initialiseEntityMoves(ent, x, y);
			listRed.add(ent);
		}
		for (y = size - 1; y >= lastY; --y) {
			++lastX;
			for (int x = size; x >= lastX; --x) {
				Entity ent = new Entity(new Position(x, y), RED, size);
				board[x][y] = ent;
				initialiseEntityMoves(ent, x, y);
				listRed.add(ent);
			}
		}
	}
	private void initialiseEntityMoves(Entity ent, int x, int y) {
		if (y > 1) {
			ent.addMove(new Position(x, y-1));
			if (x < size) {
				ent.addMove(new Position(x+1, y-1));
			}
		}
		if (x > 1) {
			ent.addMove(new Position(x-1, y));
			if (y < size) {
				ent.addMove(new Position(x-1, y+1));
			}
		}
		if (x < size) {
			ent.addMove(new Position(x+1, y));
		}
		if (y < size) {
			ent.addMove(new Position(x, y+1));
		}
	}
	/**
	* Returns a viewer for this board.
	* @return
	*	a viewer for this board.
	*/
	public Viewer viewer() {
		return new BoardViewer(this, board);
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
		return new int[size][size];
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
		if(col != turn) {
			status = Status.ILLEGAL;
			return status;
		}
		Position start = move.getStart();
		Position end = move.getEnd();
		Entity ent = board[start.getLetter()][start.getNumber()];
		if (ent == null || ent.getColor() != col || !ent.hasMove(end)) {
			status = Status.ILLEGAL;
			return status;
		}
		boolean color = (col == PlayerColor.RED);
		ent = changeStart(ent, start, color);
		changeEnd(ent, start, end, color);
		checkWin();
		return status;
	}
	private Entity changeStart(Entity ent, Position start, boolean col) {
		if (ent.isTower()) {
			actualiseTowerRemoveStone(ent, col);
			Entity newStone = new Entity(start, ent.getColor(), size);
			addToList(newStone, col);
			return newStone;
		}
		int x = start.getLetter();
		int y = start.getNumber();
		setElement(null, x, y);
		checkOpponentTower(ent, x, y-1);
		checkOpponentTower(ent, x+1, y-1);
		checkOpponentTower(ent, x+1, y);
		checkOpponentTower(ent, x, y+1);
		checkOpponentTower(ent, x-1, y+1);
		checkOpponentTower(ent, x-1, y);
		return ent;
	}
	private void checkOpponentTower(Entity ent, int x, int y) {
		if(x < 1 || y < 1 || x > size || y > size) return;
		Entity opponent = board[x][y];
		if(opponent.getColor() != ent.getColor() && opponent.isTower() && !opponent.isBlocked()) {
			opponent.addMove(ent.getPosition(), 1);
		}
	}
	private void changeEnd(Entity ent, Position start, Position end, boolean col) {
		ent.setPosition(end);
		int endX = end.getLetter();
		int endY = end.getNumber();
		Entity opponent = board[endX][endY];
		if(opponent.isBase()){
			setElement(ent, endX, endY);
			status = (col? Status.RED_WIN : Status.BLUE_WIN);
			return;
		}
		if(opponent == null) {
			setElement(ent, endX, endY);
			findMoves(ent);
			return;
		}
		if (opponent.getColor() != ent.getColor()) {
			if(opponent.isTower()) {
				if(distance(start, end) > 1) {
					opponent.setBlocked(true);
					actualiseTowerBlockOrDecrease(opponent, !col, opponent.getHigh());
					removeFromList(ent, col);
					remotePositionClosed(end, col);
				}
				else {
					setElement(ent, endX, endY);
					actualiseTowerRemoved(opponent, !col);
					findMoves(ent);
				}
			}
			return;
		}
		removeFromList(ent, col);
		if(opponent.isBlocked()) {
			opponent.setBlocked(false);
			actualiseTowerUnblockOrNew(opponent, col);
			positionOpened(end, !col);
			return;
		}
		actualiseTowerAddStone(opponent, col);
	}
	private void positionClosed(Position closedPos, boolean col) {
		ListIterator<Entity> it = (col? listRed.listIterator(): listBlue.listIterator());
		while(it.hasNext()) {
			Entity ent = it.next();
			if(ent.hasMove(closedPos)) {
				ent.removeMove(closedPos);
			}
		}
	}
	private void remotePositionClosed(Position closedPos, boolean col) {
		ListIterator<Entity> it = (col? listRed.listIterator(): listBlue.listIterator());
		while(it.hasNext()) {
			Entity ent = it.next();
			if(ent.hasRemoteMove(closedPos)) {
				ent.removeMove(closedPos);
			}
		}
	}
	private void positionOpened(Position openedPos, boolean col) {
		ListIterator<Entity> it = (col? listRed.listIterator(): listBlue.listIterator());
		while(it.hasNext()) {
			Entity ent = it.next();
			if(ent.canReach(openedPos)) {
				ent.addMove(openedPos);
			}
		}
	}
	private void actualiseTowerBlockOrDecrease(Entity tower, boolean col, int change) {
		ListIterator<Position> it = tower.getMoves().listIterator();
		Position p;
		while(it.hasNext()) {
			p = it.next();
			Entity ent = board[p.getLetter()][p.getNumber()];
			if(ent == null) continue;
			ent.stepDecrease(change);
		}
		if(tower.maxHigh()) {
			positionOpened(tower.getPosition(), col);
		}
	}
	private void actualiseTowerIncrease(Entity tower, boolean col) {
		ListIterator<Position> it = tower.getMoves().listIterator();
		Position p;
		while(it.hasNext()) {
			p = it.next();
			Entity ent = board[p.getLetter()][p.getNumber()];
			if(ent == null) continue;
			ent.stepIncrease(1);
		}
		if(tower.maxHigh()) {
			positionClosed(tower.getPosition(), col);
		}
	}
	private void actualiseTowerUnblockOrNew(Entity tower, boolean col) {
		Position p = tower.getPosition();
		if(tower.maxHigh()) {
			positionClosed(tower.getPosition(), col);
		}
		int x = p.getLetter();
		int y = p.getNumber();
		tower.removeAllMoves();
		towerCheckNeighbour(tower, x, y-1);
		towerCheckNeighbour(tower, x+1, y-1);
		towerCheckNeighbour(tower, x+1, y);
		towerCheckNeighbour(tower, x, y+1);
		towerCheckNeighbour(tower, x-1, y+1);
		towerCheckNeighbour(tower, x-1, y);
	}
	private void actualiseTowerRemoved(Entity tower, boolean col) {
		removeFromList(tower, col);
		if(!tower.isBlocked()) {
			actualiseTowerBlockOrDecrease(tower, col, tower.getHigh());
		}
		else positionOpened(tower.getPosition(), !col);
	}
	private void actualiseTowerAddStone(Entity tower, boolean col) {
		tower.addStone();
		if(tower.getHigh() == 1) {
			actualiseTowerUnblockOrNew(tower, col);
		}
		else {
			actualiseTowerIncrease(tower, col);
		}
	}
	private void actualiseTowerRemoveStone(Entity tower, boolean col) {
		actualiseTowerBlockOrDecrease(tower, col, 1);
		tower.removeStone();
		if(tower.getHigh() < 1) {
			findMoves(tower);
		}
	}
	private void towerCheckNeighbour(Entity tower, int x, int y) {
		if(x < 1 || y < 1 || x > size || y > size) return;
		Entity opponent = board[x][y];
		if(opponent == null) {
			tower.addMove(new Position(x, y), 1);
		}
		if(opponent.isBase()) return;
		if(opponent.getColor() == tower.getColor()) {
			if(opponent.isBlocked()) {
				tower.addMove(new Position(x, y), 1);
				return;
			}
			if(!opponent.maxHigh()) {
				tower.addMove(new Position(x, y), 1);
			}
			if(!opponent.isTower()) {
				addMoves(opponent, tower.getHigh());
			}
		}
	}
	private int checkCloseNeighbour(Entity ent,  int x, int y) {
		if(x < 1 || y < 1 || x > size || y > size) return 0;
		Entity opponent = board[x][y];
		int res = 0;
		if(opponent == null) {
			ent.addMove(new Position(x, y), 1);
		}
		if(opponent.isBase()) {
			if (opponent.getColor() == ent.getColor()) return 0;
			ent.addMove(new Position(x, y), 1);
			return 0;
		}
		if(opponent.isBlocked()) {
			ent.addMove(new Position(x, y), 1);
			return res;
		}
		if(opponent.isTower()) {
			if(opponent.getColor() == ent.getColor()) {
				res += opponent.getHigh();
				if(opponent.maxHigh()) {
					ent.setReach(x, y, 1);
				}
				else {
					ent.addMove(new Position(x, y), 1);
				}
			}
			else {
				opponent.removeMove(new Position(x, y));
				ent.addMove(new Position(x, y), 1);
			}

		}
		else ent.addMove(new Position(x, y), 1);
		return res;
	}
	private int findCloseMoves (Entity ent) {
		int addStep = 0;
		Position p = ent.getPosition();
		int x = p.getLetter();
		int y = p.getNumber();
		addStep += checkCloseNeighbour(ent, x, y-1);
		addStep += checkCloseNeighbour(ent, x+1, y-1);
		addStep += checkCloseNeighbour(ent, x+1, y);
		addStep += checkCloseNeighbour(ent, x, y+1);
		addStep += checkCloseNeighbour(ent, x-1, y+1);
		addStep += checkCloseNeighbour(ent, x-1, y);
		return addStep;
	}
	private void findMoves(Entity ent) {
		ent.removeAllMoves();
		int addStep = findCloseMoves(ent);
		if (addStep < 1) return;
		addMoves(ent, addStep);
	}
	private void checkOpponent (Entity ent,  int x, int y, int step) {
		if(x < 1 || y < 1 || x > size || y > size) return;
		Entity opponent = board[x][y];
		if(opponent == null) {
			ent.addMove(new Position(x, y), step);
		}
		if(opponent.isBase()) {
			if (opponent.getColor() == ent.getColor()) return;
			ent.addMove(new Position(x, y), step);
			return;
		}
		if(opponent.getColor() == ent.getColor())
			if(opponent.isBlocked() || !opponent.maxHigh()) {
				ent.addMove(new Position(x, y), step);
				return;
			}
			if(opponent.maxHigh()) {
				ent.setReach(x, y, step);
			}
		else {
			if(opponent.isBlocked() && step > 1) {
				ent.setReach(x, y, step);
				return;
			}
			ent.addMove(new Position(x, y), step);
		}
	}
	private void addMoves (Entity ent, int change) {
		int curStep = ent.getStep() + 1;
		int newStep = ent.stepIncrease(change);
		Position pos = ent.getPosition();
		int entX = pos.getLetter();
		int entY = pos.getNumber();
		for(; curStep <= newStep; ++curStep) {
			int firstX = entX;
			int lastX = entX + curStep;
			int y = entY - curStep;
			for(int x = firstX; x <= lastX && y >= 1; ++x) {
				checkOpponent(ent, x, y, curStep);
			}
			for (int d = 1; d <= curStep; ++d) {
				checkOpponent(ent, --firstX, ++y, curStep);
				checkOpponent(ent, lastX, y, curStep);
			}
			for (int d = 1; d < curStep; ++d) {
				checkOpponent(ent, firstX, ++y, curStep);
				checkOpponent(ent, --lastX, y, curStep);
			}
			++y;
			--lastX;
			for(int x = firstX; x <= lastX && y <= size; ++x) {
				checkOpponent(ent, x, y, curStep);
			}
		}
	}
	private void checkWin() {
		if (status == Status.RED_WIN || status == Status.BLUE_WIN) {
			return;
		}
		if (turn == PlayerColor.BLUE) {
			if (!hasMoves (listRed.listIterator())) {
				status = Status.BLUE_WIN;
				return;
			}
		}
		else if (!hasMoves (listBlue.listIterator())) {
			status = Status.RED_WIN;
			return;
		}
		status = Status.OK;
	}
	private void setElement(Entity ent, int x, int y) {
		board[x][y] = ent;
	}
	private void addToList(Entity ent, boolean col) {
		if(col) listRed.add(ent);
		else listBlue.add(ent);
	}
	private void removeFromList(Entity ent, boolean col) {
		if(col) listRed.remove(ent);
		else listBlue.remove(ent);
	}
	private boolean hasMoves (ListIterator<Entity> it) {
		while(it.hasNext()) {
			if(it.next().canMove())
				return true;
		}
		return false;
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
		ListIterator<Entity> it = (col == PlayerColor.RED? listRed.listIterator(): listBlue.listIterator());
		while(it.hasNext()) {
			Entity ent = it.next();
			if(ent.canMove()) {
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
