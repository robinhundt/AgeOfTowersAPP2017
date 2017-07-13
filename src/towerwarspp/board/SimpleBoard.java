package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.main.WinType.*;
import towerwarspp.main.WinType;
import java.util.Vector;
import java.util.HashSet;
import java.util.ListIterator;
import java.lang.Exception;

public class SimpleBoard implements Viewable {
        protected int size;
        protected PlayerColor turn = RED;
        protected Status status = OK;
        protected Vector<Entity> listRed = new Vector<Entity>();
        protected Vector<Entity> listBlue = new Vector<Entity>();
        protected Entity[][] board;
        protected Position redBase;
        protected Position blueBase;
	protected WinType winType = null;

        /**
        * Initialises a new object of the Board class.
        * @param n - size of the new board.
        */
        public SimpleBoard (int n) {
                board = new Entity[n+1][n+1];
				size = n;
                initialiseBoard();
        }
	public SimpleBoard(int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		this.size = size;
		this.turn = turn;
		this.listRed = lRed;
		this.listBlue = lBlue;
		this.board = board;
		this.redBase = redB;
		this.blueBase = blueB; 
	}

	public Entity[][] getBoard(){
		return board;
	}
        private void initialiseBoard() {
                redBase = new Position(1, 1);
                board[1][1] = new Entity (redBase, RED, size, true);
                blueBase = new Position(size, size);
                board[size][size] = new Entity (blueBase, BLUE, size, true);
		int d = size/2;
		initialiseEntities(redBase, d, RED, listRed);
		initialiseEntities(blueBase, d, BLUE,listBlue);
	}
	private void initialiseEntities(Position base, int dist, PlayerColor col, Vector<Entity> list) {
		for(int i = 1; i <= dist; ++i) {
			Vector<Position> positions = findPositionsInRange(base, i);
			for(Position pos: positions) {
 				Entity ent = new Entity(pos, col, size);
                        	board[pos.getLetter()][pos.getNumber()] = ent;
				findStoneMoves(ent);
                       		list.add(ent);
			}
		}
	}
	/**
	* Returns a viewer for this board.
	* @return
	*	a viewer for this board.
	*/
	public BViewer viewer() {
		return new BViewer(this, board, size);
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
	public PlayerColor getTurn() {
		return turn;
	}
	/**
	* Returns the status of the board.
	* @return
	*	the status of the board.
	*/
	public Status getStatus() {
		return status;
	}
	
	public WinType getWinType() {
		return winType;
	}
	
	/**
	* Calculates the distance between two positions on the board.
	* @param a the first position.
	* @param b the second position.
	* @return the distance between the positions a and b.
	*/
	protected int distance (Position a, Position b) {
		int x = a.getLetter() - b.getLetter();
		int y = a.getNumber() - b.getNumber();
		int z = x + y;
		return (Math.abs(x) + Math.abs(y) + Math.abs(z))/2;
	}

	/**
	* Finds all positions on the board which have the specified distance to the position cent.
	* @param cent the position for which the distant positions have to be calculated.
	* @range the distance between cent and the positions in question.
	* @return a vector of all positions on the board with the distance range to the position cent. 
	*/
	public Vector<Position> findPositionsInRange(Position cent, int range) {
		Vector<Position> res = new Vector<Position>();
		int entX = cent.getLetter();
		int entY = cent.getNumber();
		int firstX = entX; 		//most left x in the row
		int lastX = entX + range;	//the most riht left x in the row
		int y = entY - range;		//current row
		for(int x = firstX; x <= lastX && y >= 1; ++x) {
			addPositionToResult(x, y, res);
		}
		--firstX; 
		++y;
		for (; y <= entY; ++y, --firstX) {
			addPositionToResult(firstX, y, res);
			addPositionToResult(lastX, y, res);
		}
		++firstX;
		--lastX;
		for (int d = 1; d < range; ++d, ++y, --lastX) {
			addPositionToResult(firstX, y, res);
			addPositionToResult(lastX, y, res);
		}
		for(int x = firstX; x <= lastX && y <= size; ++x) {
			addPositionToResult(x, y, res);
		}
		return res;
	}
	/**
	* Proves if the coordinates x and y in the argument lay on the board. If so, 
	* a new Position object with these coordinates will be added to the specified vector of positions. 
	* @x x-coordinate in question.
	* @y y-coordinate in question.
	* @res vector of positions to which the new position has to be added if it is located on the board.
	*/	
	private void addPositionToResult(int x, int y, Vector<Position> res) {
		if (x < 1 || y < 1 || x > size || y > size)
			return;
		Position pos = new Position(x, y);
		res.add(pos);
	}
	
	/**
	* Returns true, if the player of the specified color can make the specified move.
	* @param move move in question
	* @param col color of the player in question
	* @return true, if the player of the color col can make the specified move.
	*/
	public boolean moveAllowed(Move move, PlayerColor col) {
		if(move == null) return true;
		Position start = move.getStart();
		Position end = move.getEnd();
		Entity ent = getElement(start);
		int dist = distance(start, end);
		if (ent == null || ent.getColor() != col || !ent.hasMove(end, dist)) {
			return false;
		}
		return true;
	}
	public Status makeMove(Move move, PlayerColor col) {
		return update(move, col);
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
		if (move == null) {
			turn = (turn == RED? BLUE: RED);
			winType = SURRENDER;
			return status = col == RED ? BLUE_WIN : RED_WIN;
		}
		Position start = move.getStart();
		Position end = move.getEnd();
		Entity ent = getElement(start);
		if(col != turn) {
			status = ILLEGAL;
			winType = ILLEGAL_MOVE;
			throw new IllegalArgumentException("Board: turn is incorrect");
			//return status;
		}
		if(!moveAllowed(move, col)) {
			status = ILLEGAL;
			if(ent == null) {
				throw new IllegalArgumentException("Board: Entity not on the board " + move);
			}
			if(ent.getColor() != col) {
				throw new IllegalArgumentException("Board: false color " + move + " entity color: "+ ent.getColor());
			}
			throw new IllegalArgumentException("Board: move does not ex. " + move);
			//return status;
		}
		ent = changeStart(ent, start);
		changeEnd(ent, start, end);
		status = checkWin(end);
		turn = (turn == RED? BLUE: RED);
		return status;
	}
	/**
	* Conducts changes caused by the current move, which is specified for the figure ent, in respect to its start position.
	* If the moving figure is a tower;
	*	1) its height will be decreased;
	*	2) the step range of all neighbouring stones with the same color will be decreased as well;
	*	3) a new Entity, representing the stone taken from the top of the tower, will be created and returned.
	* If the moving figure is a stone:
	*	1) the position will be marked as an empty one;
	*	2) all neighbouring towers of the other color will get a new move to this newly emppty position;
	*	3) the same stone will be returned.
	* @param ent the figure which has to be moved (if it is a simple stone) or dismantled (if it is a tower).
	* @return the stone which is to be placed on the end position of the current move.
	*/
	private Entity changeStart(Entity ent, Position start) {
		if (ent.isTower()) {
			actualiseTowerRemoveStone(ent);
			Entity newStone = new Entity(start, ent.getColor(), size);
			addToList(newStone);
			return newStone;
		}
		setElement(null, start);
		Vector<Position> neighbours = findPositionsInRange(start, 1);
		ListIterator<Position> it = neighbours.listIterator();
		while(it.hasNext()) {
			actualiseTowerNeighbourIsEmpty(start, it.next(), ent.getColor());
		}
		return ent;
	}
	/**
	* Conducts changes caused by the current move, which is specified for the figure ent, in respect to its end position.
	* Case 1: the end position is empty:
	*	1) the figure in question will be placed on this position on the board;
	*	2) all corresponding changes will be commited;
	* Case 2: the end position is the opponent's base:
	*	1) the figure in question will replace the base on the desk;
	*	3) no other changes will be commited.
	* Case 3: there is the opponent's figure (stone or tower) on the end position:
	*	1.1) if the opponent's figure is a tower,
	*		it will be replaced or blocked by the moving stone, depending on the art of the move;
	*	1.2) if the opponent's figure is a simple stone,
	*		it will be replaced by the moving stone;
	*	2) all corresponding changes will be commited.
	* Case 4: there is a figure of the same color on the end position:
	* 	1.1) if the other figure is a stone:
	*		a new tower with the height 1 will be builded;
	*	1.2) if the other figure is a tower which is not blocked (and has not reached the maximal height),
	*		the height of the tower will be inncreased;
	*	1.3) if the other figure is a blocked tower,
	*		this  tower will be unblocked:
	*	2) the moving stone will be removed from the list of movable figures;
	*	3) all other corresponding channges will be commited.		
	* @param ent the figure in question which has to be moved to the specified end position.
	* @param end the position to which the figure in question has to be moved. 
	*/
	private void changeEnd(Entity ent, Position start, Position end) {
		setPosition(ent, end);
		Entity opponent = getElement(end);
		if(opponent == null || opponent.isBase()) {
			setElement(ent, end);
			if(opponent == null) {
				findStoneMoves(ent);
			}
			return;
		}
		if (opponent.getColor() != ent.getColor()) {
			if(opponent.isTower()) {
				int dist = distance(start, end);
				if( dist > 1) {
					blockTower(opponent, ent);
				}
				else {
					removeTower(opponent, ent);
				}
			}
			else {
				removeFigure(opponent, ent);
			}
			return;
		}
		if(opponent.isBlocked()) {
			unblockTower(opponent, ent);
		}
		else {
			buildTower(opponent, ent);
		}
	}
	/**
	* Blocks the specified tower and commits all the necessary changes. 
	* @param tower the tower to be blocked.
	* @param blockingStone the stone which is going to block the tower.
	*/
	private void blockTower(Entity tower, Entity blockingStone) {
		block(tower);
		actualiseOwnTowerBlockedOrDecreased(tower, tower.getHeight());
		removeFromList(blockingStone);
		positionClosed(tower.getPosition(), blockingStone.getColor(), false);
	}
	/**
	* Removes the specified tower from the board replacing it with the stone removingStone.
	* Commits all the necessary changes. 
	* @param tower the tower to be removed.
	* @param removingStone the stone which is going to beat the tower and take its place. 
	*/
	private void removeTower(Entity tower, Entity removingStone) {
		actualiseTowerRemoved(tower);
		removeFigure(tower, removingStone);
	}
	/**
	* Removes the specified figure from the board replacing it with the stone removingStone.
	* Commits all the necessary changes. 
	* @param ent the figure to be removed.
	* @param removingStone the stone which is going to beat the specified figure and take its place. 
	*/
	private void removeFigure(Entity ent, Entity removingStone) {
		setElement(removingStone, ent.getPosition());
		removeFromList(ent);
		findStoneMoves(removingStone);
	}
	/**
	* Unblocks the specified tower and commits all the necessary changes. 
	* @param tower the tower to be unblocked.
	* @param unblockingStone the stone which is going to unblock the tower.
	*/
	private void unblockTower(Entity tower, Entity unblockingStone) {
			unblock(tower);
			removeFromList(unblockingStone);
			findTowerMoves(tower);
			actualiseOwnTowerUnblockedOrIncreased(tower, tower.getHeight());
			positionOpenedStonesOnly(tower.getPosition(), (tower.getColor() == RED? BLUE : RED));
	}
	/**
	* Puts the stone newStone on the top of the other stone or tower of the same color specified by the parameter tower.
	* Commits all the necessary changes. 
	* @param tower the figure (a tower or a stone) whose height has to be increased as a result of the current move.
	* @param newStone the stone which is going to be put on the top of the figure in question. 
	*/
	private void buildTower(Entity tower, Entity newStone) {
			removeFromList(newStone);
			actualiseTowerAddStone(tower);
	}
	/**
	* Proves if there is a tower on the position pos which is not blocked and has color different from the col.
	* If so, adds a new move - to the position emptyPos - to the tower's possible moves.
	* @param emptyPos the newly empty position.
	* @param pos some other position on the board neigbouring to emptyPos.
	* @col the color of the player whose figure has just left the position emptyPos.
	*/
	private void actualiseTowerNeighbourIsEmpty(Position emptyPos, Position pos, PlayerColor col) {
		Entity ent = getElement(pos);
		if(ent != null && ent.getColor() != col && ent.isTower() && !ent.isBlocked()) {
			addMove(ent, emptyPos, 1);
		}
	}
	/**
	* Removes the specified position from the list of possible moves for every figure of the specified color,
	* if this figure can reach the position. The parameter onlyRemote indicates if the position in question
	* has to be closed for all figures of the specified color or only for those which can reach the position with a remote move.
	* @param closedPos the position in question.
	* @param col the of the figures for which the position in question has to be closed.
	* @param forAll indicates if the position in question has to be closed for all figures
	*	of the specified color (forAll == true) or only for those which can reach the position with a remote move (forAll == false).
	*/
	private void positionClosed(Position closedPos, PlayerColor col, boolean forAll) {
		/*ListIterator<Entity> it = (col == RED? listRed.listIterator(): listBlue.listIterator());
		while(it.hasNext()) {
			Entity ent = it.next();
			int dist = distance(closedPos, ent.getPosition());
			if (!forAll && dist == 1) 
				continue;
			if(ent.hasMove(closedPos, dist)) {
				removeMove(ent, closedPos, dist);
			}
		}*/
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			int dist = distance(closedPos, ent.getPosition());
			if (!forAll && dist == 1) 
				continue;
			if(ent.hasMove(closedPos, dist)) {
				removeMove(ent, closedPos, dist);
			}
		}
	}
	/**
	* Adds the specified position to the list of possible moves for every figure of the specified color,
	* if this figure can reach the position.
	* @param openedPos the position in question.
	* @param col the of the figures for which the position in question has to be opened.
	*/
	private void positionOpened(Position openedPos, PlayerColor col) {
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			if(!ent.isBlocked()) {
				int dist = distance(openedPos, ent.getPosition());
				if(ent.getRange() >= dist) {
					addMove(ent, openedPos, dist);
				}
			}
		}
	}
	/**
	* Adds the specified position to the list of possible moves for every stone (and for the towers),
	* if this stone can reach the position.
	* @param openedPos the position in question.
	* @param col the of the stone for which the position in question has to be opened.
	*/
	private void positionOpenedStonesOnly(Position openedPos, PlayerColor col) {
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			if(!ent.isTower()) {
				int dist = distance(openedPos, ent.getPosition());
				if(ent.getRange() >= dist) {
					addMove(ent, openedPos, dist);
				}
			}
		}
	}
	
	/**
	* Commits changes caused by blocking or dismantling of a tower in respect to its neighbours of the same color.
	* 1) For each neighbouring stone of the same color: its step width will be decreased and all corresponding
	*	remote moves will be removed from the list of possible moves.
	* 2) For each neigbouring tower of the same color: no changes are needed.
	* 3) If the tower in question had the maximum height, a move to its position will be added 
	* to the lists of possible moves for all figures (stones and towers) of the same color if they can reach this position.
	* @param tower the tower in question.
	* @change the number of steps that the neighbouring stones of the same color
	*	lose as a result of blocking or dismantling of the tower in question.
	*/
	private void actualiseOwnTowerBlockedOrDecreased(Entity tower, int change) {
		HashSet<Move> moves = tower.getMoves().get(1);
		if (tower.isBlocked()) {
			removeAllMoves(tower);
		}
		Position pos;
		for(Move move: moves) {
			pos = move.getEnd();
			Entity ent = getElement(pos);
			if(ent != null && !ent.isTower()) {
				removeRanges(ent, change);
			}
		}
		if(tower.isMaxHeight()) {
			positionOpened(tower.getPosition(), tower.getColor());
		}
	}
	/**
	* Commits changes caused by creating, increasing or unblocking of a tower in respect to its neighbours of the same color.
	* 1) For each neighbouring stone of the same color: its step width will be increased and all newly available
	*	remote moves will be added to its list of possible moves.
	* 2) For each neigbouring tower of the same color: no changes are needed if the height of the increased/unblocked one is not maximum.
	* 3) If the tower in question has the maximum height, all moves to its position will be removed 
	* from the lists of possible moves for all figures (stones and towers) of the same color in case they had such moves.
	* @param tower the tower whose height has ben increased.
	* @change the number of steps that the neighbouring stones of the same color
	*	gain as a result of creating, increasing or unblocking of the tower in question.
	*/
	private void actualiseOwnTowerUnblockedOrIncreased(Entity tower, int change) {
		HashSet<Move> moves = tower.getMoves().get(1);
		Position pos;
		for(Move move: moves) {
			pos = move.getEnd();
			Entity ent = getElement(pos);
			if(ent != null && !ent.isTower()) { 
				addRanges(ent, change);
			}
		}
		if(tower.isMaxHeight()) {
			positionClosed(tower.getPosition(), tower.getColor(), true);
		}
	}
	/**
	* Conducts all necessary changes caused by removing a tower.
	* @param tower the tower that has been removed.
	*/
	private void actualiseTowerRemoved(Entity tower) {
		removeFromList(tower);
		if(!tower.isBlocked()) {
			actualiseOwnTowerBlockedOrDecreased(tower, tower.getHeight());
		}
		positionOpened(tower.getPosition(), (tower.getColor() == RED? BLUE: RED));
		if(tower.isMaxHeight()) {
			positionOpenedStonesOnly(tower.getPosition(), tower.getColor());
		}
	}
	/**
	* Conducts all necessary changes caused by putting a stone on a top of a tower.
	* @param tower the tower whose height has been increased with a new stone.
	*/
	private void actualiseTowerAddStone(Entity tower) {
		incHeight(tower);
		if(tower.getHeight() == 1) {
			findTowerMoves(tower);
		}
		actualiseOwnTowerUnblockedOrIncreased(tower, 1);
	}
	/**
	* Conducts all necessary changes caused by removing a stone from a tower.
	* @param tower the tower whose top stone has been removed.
	*/
	private void actualiseTowerRemoveStone(Entity tower) {
		actualiseOwnTowerBlockedOrDecreased(tower, 1);
		if(tower.isMaxHeight()) {
			positionOpened(tower.getPosition(), tower.getColor());
		}
		decHeight(tower);
		if(tower.getHeight() < 1) {
			findStoneMoves(tower);
		}
	}
	/**
	* Finds all possible moves for the specified tower.
	* @param tower the tower whose possible moves have to be found.
	*/
	private void findTowerMoves(Entity tower) {
		removeAllMoves(tower);
		Vector<Position> positions = findPositionsInRange(tower.getPosition(), 1);
		for(Position endPos: positions) {
			if(checkMoveForTower(endPos, tower.getColor())) {
				addMove(tower, endPos, 1);
			}
		}
	}
	/**
	* Returns true if a tower of the color col neighbouring to the position pos can be dismantled on this position. 
	* @param pos the position in question.
	* @param col the color of the tower in question.
	* @return true if a tower of the color col neighbouring to the position pos can be dismantled on this position.  
	*/
	private boolean checkMoveForTower(Position pos, PlayerColor col) {
		Entity opponent = getElement(pos);
		if(opponent == null || (!opponent.isBase() && opponent.getColor() == col && 
				(!opponent.isMaxHeight() || opponent.isBlocked() ) ) ){
				return true;
		}
		return false;
	}
	/**
	* Finds all possible moves for the specified stone and conducts changes relevant for its close neighbours.
	* @param stone the stone whose possible moves have to be found.
	*/
	private void findStoneMoves(Entity stone) {
		removeAllMoves(stone);
		int addRanges = 0;	//additional steps
		Vector<Position> closeNeighbours = findPositionsInRange(stone.getPosition(), 1);
		ListIterator<Position> it = closeNeighbours.listIterator();
		while(it.hasNext()) {
			Position neighbourPos = it.next();
			Entity neighbour = getElement(neighbourPos);
			if(checkMoveForStone(neighbourPos, stone.getColor(), 1) ) {
				addMove(stone, neighbourPos, 1);
			}
			if(neighbour != null && neighbour.isTower() && !neighbour.isBlocked()) {
				if(neighbour.getColor() == stone.getColor()) {
					addRanges += neighbour.getHeight();
					addMove(neighbour, stone.getPosition(), 1);
				} 
				else {
					removeMove(neighbour, stone.getPosition(), 1);
				}
			}
		}
		addRanges(stone, addRanges);
	}
	/**
	* Returns true if a stone of the color col can go to the position pos taking into account the art of the move in question (close or remote one). 
	* @param pos the position in question.
	* @param col the color of the stone in question.
	* @param dist distance from the stone position to the position in question. If dist == 1, it is a close move, else - a remote one.
	* @return true if the stone in question can go to the specified position.  
	*/
	protected boolean checkMoveForStone(Position pos, PlayerColor col, int dist) { // 	boolean checkMoveForStone(opponentPos, PlayerColor col)) { checkOpponent
		Entity opponent = getElement(pos);
		if(opponent == null
			|| (opponent.getColor() != col && (opponent.isBase() || !opponent.isBlocked() || dist == 1) )
			|| (opponent.getColor() == col && !opponent.isBase() && (!opponent.isMaxHeight() || opponent.isBlocked() ) ) ) {
			return true;
		}
		return false;
	}
	/**
	* Proves if the last move to the position lastMove was a winning one.
	* @param lastMove end position of the last move.
	* @return 
	*	RED_WIN if the red player has won;
	*	BLUE_WIN if the blue player has won;
	*	OK the move was not winning.
	*/
	private Status checkWin(Position lastMove) {
		if (lastMove.equals((turn == RED? blueBase: redBase))) {
			winType = BASE_DESTROYED;
			return (turn == RED? RED_WIN: BLUE_WIN);
		}
		if (!hasMoves ((turn == RED? listBlue: listRed))) {
			winType = NO_POSSIBLE_MOVES;
			return (turn == RED? RED_WIN: BLUE_WIN);
		}
		return OK;
	}
	/**
	* Puts the specified figure on the specified position on the board.
	* @param ent the figure in question.
	* @param pos position for the speciifiied figure to be placed on.
	*/
	protected void setElement(Entity ent, Position pos) {
		board[pos.getLetter()][pos.getNumber()] = ent;
	}
	/**
	* Returns an element located on the specified position on the board.
	* @param pos the position of the element that has to be returned.
	* @return an element located on the specified position on the board.
	*/
	protected Entity getElement(Position pos) {
		return board[pos.getLetter()][pos.getNumber()];
	}
	protected void block(Entity tower) {
		tower.setBlocked(true);
	}
	protected void unblock(Entity tower) {
		tower.setBlocked(false);
	}
	protected void addMove(Entity ent, Position pos, int range) {
		ent.addMove(pos, range);
	}
	protected void removeMove(Entity ent, Position pos, int range) {
		ent.removeMove(pos, range);
	}
	protected void removeAllMoves(Entity ent) {
		ent.removeAllMoves();
	}
	/**
	* Increases the step width of the specified figure (stone) by n and adds 
	* newly available positions in the new range to its list of possible moves.
	* @param stone the figure (stone) whose step width has to be increased.
	* @param n amount of steps that has to be added.
	*/
	protected void addRanges(Entity stone, int n) {
		for(int i = 0; i < n; ++i) {
			incRange(stone);
			Vector<Position> opponents = findPositionsInRange(stone.getPosition(), stone.getRange());
			for(Position opponentPos: opponents){
				if(checkMoveForStone(opponentPos, stone.getColor(), stone.getRange())) {
					addMove(stone, opponentPos, stone.getRange());
				}
			}
		}
	}
	/**
	* Removes the specified number of steps together with all corresponding moves
	* from the specified figure's list of possible moves.
	* @param ent the figure in question.
	* @param n number of steps to be removed. 
	*/
	private void removeRanges(Entity ent, int n) {
		for(int i = 0; i < n; ++i) {
			decRange(ent);
		}
	}
	protected void incRange(Entity ent) {
		ent.incRange();
	}
	protected void decRange(Entity ent) {
		ent.decRange();
	}
	protected void incHeight(Entity tower) {
		tower.incHeight();
	}
	protected void decHeight(Entity tower) {
		tower.decHeight();
	}
	protected void setPosition(Entity ent, Position pos) {
		ent.setPosition(pos);
	}
	/**
	* Adds the specified figure to the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	protected void addToList(Entity ent) {
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.add(ent);
	}
	/**
	* Removes the specified figure from the the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	protected void removeFromList(Entity ent) {
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		/*if(!list.contains(ent) ) {
			System.out.println("Entity ist nicht in List");
		}*/
		list.remove(ent);

	}
	/*public void printList(Vector<Entity> list) {
		for(Entity ent: list) {
			System.out.print(ent.getPosition() + " ");
		}
		System.out.println();
	}*/
	/**
	* Returns true if at least one of the entities returned by the iterator in the argument can be moved.
	* @param it iterator over a list of Entities (listRed or listBlue) for which the move possibility has to be proved.
	* @return true if at least one of the entities returned by the iterator it can be moved.
	*/
	private boolean hasMoves (Vector<Entity> list) {
		for(Entity ent: list) {
			if(ent.movable()) {
				return true;
			}
		}
		return false;
	}
	
	protected Vector<Entity> getEntityList(PlayerColor col) {
		return (col == RED? listRed: listBlue);
	}
	
}
