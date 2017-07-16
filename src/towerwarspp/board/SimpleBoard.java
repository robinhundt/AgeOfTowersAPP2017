package towerwarspp.board;

import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.util.debug.DebugLevel.*;
import static towerwarspp.util.debug.DebugSource.*;
import static towerwarspp.main.WinType.*;

import java.util.HashSet;
import java.util.Vector;

import towerwarspp.util.debug.Debug;
import towerwarspp.main.WinType;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
import towerwarspp.preset.Viewable;

/**
* This class represents the board on which all players' moves have to be executed.
* It contains information about all tokens which are currently on the board as well as about the current turn and game status.
* It executes new moves conducting all necessary changes respectively and recognises if the new move is a legal one
* and if it leads to the end of the game. Illegal moves will not be executed. Instead, the SimpleBoard's status will be set to ILLEGAL.
* If the game is finished, the SimpleBoard saves the information on the win type.
*  
* @author Anastasiia Kysliak
* @version 15-07-17 
*/
public class SimpleBoard implements Viewable {
	/**
	* A list of {@link Hexagon} objects representing all possible directions on the board.
	*/
	private final Hexagon[] directions = { new Hexagon(1, 0), new Hexagon(0, 1), new Hexagon(-1, 1),
			new Hexagon(-1, 0), new Hexagon(0, -1), new Hexagon(1, -1), };
	/**
	* The size of the board.
	*/
	protected int size;

	/**
	* The current turn.
	*/
	protected PlayerColor turn = RED;

	/**
	* A list of all potentially movable tokens belonging to the red player.
	* This list contais only stones and towers (blocked and not blocked), but not the base.
	*/
	protected Vector<Entity> listRed = new Vector<Entity>();

	/**
	* A list of all potentially movable tokens belonging to the blue player.
	* This list contais only stones and towers (blocked and not blocked), but not the base.
	*/
	protected Vector<Entity> listBlue = new Vector<Entity>();

	/**
	* The position of the red base.
	*/
	protected Position redBase;

	/**
	* The position of the blue base.
	*/
	protected Position blueBase;

	/**
	* Instance of the class {@link Debug}.
	*/ 
	protected Debug debug;

	/**
	* The current board satus.
	*/
	private Status status = OK;

	/**
	* The representation of the board as a 2-dimensional array of Entities.
	*/
	private Entity[][] board;

	/**
	* Stores the information on the win type if the game is finished. This variable has value null as long as the game is not finished.
	*/
	private WinType winType = null;

        /**
        * Initialises a new object of the class SimpleBoard.
        * @param n size of the new board.
        */
        public SimpleBoard (int n) {
		if(n < 4 || n > 26) {
			throw new IllegalArgumentException("The size " + n + " is not allowed");
		}
                board = new Entity[n+1][n+1];
		size = n;
                initialiseBoard();
		this.debug = Debug.getInstance();
        }
 	/**
        * Initialises a new object of the class SimpleBoard with the specified parameters.
        * @param size size of the new board.
        * @param turn current turn.
        * @param lRed list of all red entities except the red base.
        * @param lBlue list of all blue entities except the blue base.
        * @param board representation of the board as a 2-dimensional array of entities.
        * @param redB position of the red base.
        * @param blueB position of the blue base.
        */
	public SimpleBoard(int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		this.size = size;
		this.turn = turn;
		this.listRed = lRed;
		this.listBlue = lBlue;
		this.board = board;
		this.redBase = redB;
		this.blueBase = blueB;
		this.debug = Debug.getInstance();
	}
	/**
	* Returns the size of the board.
	* @return the size of the board.
	*/
	public int getSize() {
		return size;
	}
	/**
	* Returns the current turn.
	* @return the curren turn.
	*/
	public PlayerColor getTurn() {
		return turn;
	}
	/**
	* Returns the status of the board.
	* @return the status of the board.
	*/
	public Status getStatus() {
		return status;
	}
	/**
	* Returns the WinType or null if the game is not finished.
	* @return the WinType or null if the game is not finished.
	*/
	public WinType getWinType() {
		return winType;
	}
	/**
	* Returns a viewer for this board.
	* @return a viewer for this board.
	*/
	public BViewer viewer() {
		return new BViewer(this);
	}
	/**
	* Returns the list of movable tokens of the specified color.
	* @param col the color of the tokens in question.
	* @return the list of movable tokens of the specified color.
	*/
	protected Vector<Entity> getEntityList(PlayerColor col) {
		return (col == RED? listRed: listBlue);
	}
	/**
	* Returns the token located on the specified position on the board.
	* @param pos the position of the token that has to be returned.
	* @return the token located on the specified position on the board.
	*/
	protected Entity getElement(Position pos) {
		return board[pos.getLetter()][pos.getNumber()];
	}
	/**
	* Puts the specified token on the specified position on the board.
	* @param ent the token in question.
	* @param pos position for the specified token to be placed on.
	*/
	private void setElement(Entity ent, Position pos) {
		board[pos.getLetter()][pos.getNumber()] = ent;
	}
	/**
	* Blocks or unblocks the specified tower.
	* @param tower the tower which has to be blocked.
	* @param block specifies if the tower in question has to be blocked or unblocked (if block == true, the tower has to be blocked).
	*/
	private void setBlocked(Entity tower, boolean block) {
		tower.setBlocked(block);
	}
	/**
	* Adds a move to the specified end position to the list of all possible moves of the specified token.
	* @param ent the token which has to have a move to the specified position.
	* @param pos the end position of the move which has to be possible for the specified token.
	* @param range distance to the position pos from the token's current position.
	*/
	private void addMove(Entity ent, Position pos, int range) {
		ent.addMove(pos, range);
	}
	/**
	* Removes the move to the specified end position from the list of all possible moves of the specified token.
	* @param ent the token whose move to the specified position has to be removed from its list of possible moves.
	* @param pos the end position of the move which has to be removed from the token's possible moves list.
	* @param range distance to the position pos from the token's current position.
	*/
	private void removeMove(Entity ent, Position pos, int range) {
		ent.removeMove(pos, range);
	}
	/**
	* Removes all moves of the specified token.
	* @param ent the token whose moves have to be removed.
	*/
	private void removeAllMoves(Entity ent) {
		ent.removeAllMoves();
	}
	/**
	* Increases the step range of the specified token (stone) by n and adds 
	* newly available positions in the new range to its list of possible moves.
	* @param stone the token (stone) whose step range has to be increased.
	* @param n the required change of the step range.
	*/
	private void addRanges(Entity stone, int n) {
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
	* Decreases the step range of the specified token and removes all corresponding moves
	* from the specified token's list of possible moves.
	* @param ent the token in question.
	* @param n the required change of the step range. 
	*/
	private void removeRanges(Entity ent, int n) {
		for(int i = 0; i < n; ++i) {
			decRange(ent);
		}
	}
	/**
	* Increases the step range of the specified token (stone) by one without
	* adding any new moves to its list of possible moves.
	* @param ent the token whose step range has to be increased.
	*/
	private void incRange(Entity ent) {
		ent.incRange();
	}
	/**
	* Decreases the step range of the specified token (stone) by one.
	* All moves which are no more possible will be removed from the token's list of possible moves.
	* @param ent the token whose step range has to be decreased.
	*/
	private void decRange(Entity ent) {
		ent.decRange();
	}
	/**
	* Increases the height of the specified token.
	* @param tower the token whose height has to be increased.
	*/
	private void incHeight(Entity tower) {
		tower.incHeight();
	}
	/**
	* Decreases the height of the specified token.
	* @param tower the token whose height has to be decreased.
	*/
	private void decHeight(Entity tower) {
		tower.decHeight();
	}
	/**
	* Sets a new position for the specified token.
	* @param ent the token whose position has to be changed.
	* @param pos the new position.
	*/
	private void setPosition(Entity ent, Position pos) {
		ent.setPosition(pos);
	}
	/**
	* Adds the specified token to the list of movable tokens of the corresponding color.
	* @param ent the token in question.
	*/
	private void addToList(Entity ent) {
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.add(ent);
	}
	/**
	* Removes the specified token from the the list of movable tokens of the corresponding color.
	* @param ent the token in question.
	*/
	private void removeFromList(Entity ent) {
		Vector<Entity> list = (ent.getColor() == RED? listRed: listBlue);
		list.remove(ent);

	}
	/**
	* Calculates the distance between two positions on the board.
	* @param a the first position.
	* @param b the second position.
	* @return the distance between the positions a and b.
	*/
	public static int distance (Position a, Position b) {
		int x = a.getLetter() - b.getLetter();
		int y = a.getNumber() - b.getNumber();
		int z = x + y;
		return (Math.abs(x) + Math.abs(y) + Math.abs(z))/2;
	}
	/**
	* Returns true if the player of the specified color can make the specified move.
	* @param move the move in question.
	* @param col the color of the player in question.
	* @return true if the player of the color col can make the specified move.
	*/
	public boolean moveAllowed(Move move, PlayerColor col) {
		if(move == null) return true;
		Entity ent = getElement(move.getStart());
		int dist = distance(move.getStart(), move.getEnd());
		if (ent == null || ent.getColor() != col || !ent.hasMove(move.getEnd(), dist)) {
			return false;
		}
		return true;
	}
	/**
	* Executes the specified move if this move is legal. Changes the board status and the turn accordingly.
	* If the move is illegal, the turn will not be changed.
	* Returns the current status of the board.
	* @param move new move to be executed.
	* @return actualised status of the board.
	*/
	public Status makeMove(Move move) {
		if (move == null) {
			winType = SURRENDER;
			status = (turn == RED ? BLUE_WIN : RED_WIN);
			return status;
		}
		Position start = move.getStart();
		Position end = move.getEnd();
		Entity ent = getElement(start);
		if(!moveAllowed(move, turn)) {
			status = ILLEGAL;
			if(ent == null) {
				debug.send(LEVEL_1, BOARD, "Entity is not on the board " + move.toString());
			}
			if(ent.getColor() != turn) {
				debug.send(LEVEL_1, BOARD, "Wrong color: entity color = " + ent.getColor() + ", move = " + move.toString());
			}
			debug.send(LEVEL_1, BOARD, "Move does not exist: turn = " + turn + ", move = " + move.toString());
			return status;
		}
		ent = changeStart(ent, start);
		changeEnd(ent, start, end);
		status = checkWin(end);
		turn = (turn == RED? BLUE: RED);
		return status;
	}
	/**
	* Proves if the last move to the position lastMove was a winning one and returns the corresponding status.
	* @param lastMove end position of the last move.
	* @return RED_WIN if the red player has won;
	*	BLUE_WIN if the blue player has won;
	*	OK if the move was not winning.
	*/
	private Status checkWin(Position lastMove) {
		if (lastMove.equals((turn == RED? blueBase: redBase))) {
			winType = BASE_DESTROYED;
			return (turn == RED? RED_WIN: BLUE_WIN);
		}
		if (!hasMoves ((turn == RED? BLUE: RED))) {
			winType = NO_POSSIBLE_MOVES;
			return (turn == RED? RED_WIN: BLUE_WIN);
		}
		return OK;
	}
	/**
	* Returns true if the player of the color col has at least one move. 
	* @param col the color of the player in question.
	* @return true if the player of the color col has at least one move.
	*/
	private boolean hasMoves (PlayerColor col) {
		Vector<Entity> list = getEntityList(col);
		for(Entity ent: list) {
			if(ent.isMovable()) {
				return true;
			}
		}
		return false;
	}
	/**
	* Returns all positions on the board which lay on the ring with the specified center and the specified radius.
	* @param center position of the ring's center.
	* @param radius the ring's radius.
	* @return all positions on the board which lay on the ring with the specified center and the specified radius.
	*/
	private Vector<Position> findPositionsInRange (Position center, int radius) {
		Vector<Position> result = new Vector<Position>(radius*6);
    		Hexagon curHex = new Hexagon(center);
		Hexagon direction = new Hexagon(directions[4]);
		direction.scale(radius);
		curHex.add(direction);
    		for (int i = 0; i < 6; ++i) {
        		for(int j = 0; j < radius; ++j) {
				if(isOnBoard(curHex.getX(), curHex.getY())) {
            				result.add(new Position(curHex.getX(), curHex.getY()));
				}
            			curHex.add(directions[i]);
			}
		}
    		return result;
	}
	/**
	* Returns true if the position with the specified coordinates lays on the board. 
	* @param x x-coordinate of the position in question.
	* @param y y-coordinate of the position in question.
	* @return true if the position (x, y) lays on the board.
	*/
	private boolean isOnBoard(int x, int y) {
			return 	x >= 1 && y >= 1 && x <= size && y <= size;
	}
	
	/**
	* Conducts changes caused by the current move, which is specified for the token ent, in respect to its start position.
	* If the moving token is a tower;
	*	1. its height will be decreased;
	*	2. the step range of all neighbouring stones of the same color will be decreased as well;
	*	3. a new Entity, representing the stone taken from the top of the tower, will be created and returned.
	* If the moving token is a stone:
	*	1. the position will be marked as an empty one;
	*	2. all neighbouring towers of the other color will get a new move to this newly empty position;
	*	3. the same token will be returned.
	* @param ent the token which has to be moved (if it is a simple stone) or dismantled (if it is a tower).
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
		for(Position neighbour : neighbours) {
			actualiseTowerNeighbourIsEmpty(start, neighbour, ent.getColor());
		}
		return ent;
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
	* Returns true if a stone of the color col can go to the position pos taking into account the art of the move in question (close or remote one). 
	* @param pos the position in question.
	* @param col the color of the stone in question.
	* @param dist distance from the current stone position to the position in question.
	*		If dist == 1, the potential move is a close one, else - a remote one.
	* @return true if the stone in question can go to the specified position.  
	*/
	private boolean checkMoveForStone(Position pos, PlayerColor col, int dist) {
		Entity opponent = getElement(pos);
		if(opponent == null
			|| (opponent.getColor() != col && (opponent.isBase() || !opponent.isBlocked() || dist == 1) )
			|| (opponent.getColor() == col && !opponent.isBase() && (!opponent.isMaxHeight() || opponent.isBlocked() ) ) ) {
			return true;
		}
		return false;
	}
	/**
	* Conducts changes caused by the current move, which is specified for the token ent, in respect to its end position.
	* Case 1: the end position is empty:
	*	1. the token in question will be placed on this position on the board;
	*	2. all corresponding changes will be commited.
	* Case 2: the end position is the opponent's base:
	*	1. the token in question will replace the base on the board;
	*	2. no other changes will be commited.
	* Case 3: there is the opponent's token (stone or tower) on the end position:
	*	1.1. if the opponent's token is a tower,
	*		it will be replaced or blocked by the moving stone, depending on the art of the move (close or remote one);
	*	1.2. if the opponent's token is a simple stone,
	*		it will be replaced by the moving stone;
	*	2. all corresponding changes will be commited.
	* Case 4: there is a token of the same color on the end position:
	* 	1.1. if the other token is a stone:
	*		a new tower with the height 1 will be builded;
	*	1.2. if the other token is a tower which is not blocked (and has not reached the maximal height),
	*		the height of the tower will be inncreased;
	*	1.3. if the other token is a blocked tower,
	*		this  tower will be unblocked;
	*	2. the moving stone will be removed from the list of movable tokens;
	*	3. all other corresponding changes will be commited.		
	* @param ent the token in question which has to be moved to the specified end position.
	* @param end the position to which the token in question has to be moved. 
	*/
	private void changeEnd(Entity ent, Position start, Position end) {
		setPosition(ent, end);
		Entity opponent = getElement(end);
		if(opponent == null || opponent.isBase()) {
			setElement(ent, end);
			if(opponent == null) {
				findStoneMoves(ent);
				positionClosedAndOpenedForTowers(end, ent.getColor() == RED? BLUE : RED, false); 
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
					actualiseTowerRemoved(opponent);
					removeToken(opponent, ent);
				}
			}
			else {
				removeToken(opponent, ent);
			}
			return;
		}
		if(opponent.isBlocked()) {
			unblockTower(opponent, ent);
		}
		else {
			removeFromList(ent);
			actualiseTowerAddStone(opponent);
		}
	}
	/**
	* Blocks the specified tower and commits all the necessary changes:
	* 	1. the range of neighbouring stones of the same color will be decreased;
	*	2. if the tower in question was of the maximal height,
	*		towers and stones of the same color which can reach the tower's position will get a new possible move to this position.
	*	3. the blocking stone will be removed from the list of movable tokens.
	*	4. for all opponent's stones which have a remote move to the tower's position: 
	*			this move will be removed from their lists of possible moves.
	* @param tower the tower to be blocked.
	* @param blockingStone the stone which is going to block the tower.
	*/
	private void blockTower(Entity tower, Entity blockingStone) {
		actualiseTowerNeighboursRemoveRanges(tower, tower.getHeight());
		setBlocked(tower, true);
		removeAllMoves(tower);
		if(tower.isMaxHeight()) {
			positionOpened(tower.getPosition(), tower.getColor());
		}
		removeFromList(blockingStone);
		positionClosed(tower.getPosition(), blockingStone.getColor(), false);
	}
	/**
	* Unblocks the specified tower, removes the and commits all the necessary changes. 
	* 	1. the range of neighbouring stones of the same color will be inreased;
	*	2. the unblocking stone will be removed from the list of movable tokens.
	*	3. if the unblocked tower is of the maximal height,
	*		for all tokens of the same color which have a possible move to the tower's position: 
	*		this move will be removed from their lists of possible moves.
	*	4. for all opponent's stones which could reach the tower's position with a remote move: 
	*		this move will be added to their lists of possible moves.
	* @param tower the tower to be unblocked.
	* @param unblockingStone the stone which is going to unblock the tower.
	*/
	private void unblockTower(Entity tower, Entity unblockingStone) {
			setBlocked(tower, false);
			removeFromList(unblockingStone);
			findTowerMoves(tower);
			actualiseOwnTowerUnblockedOrIncreased(tower, tower.getHeight());
			positionOpenedStonesOnly(tower.getPosition(), (tower.getColor() == RED? BLUE : RED));
	}
	
	/**
	* Removes the specified token ent from the board replacing it with the stone removingStone.
	* Commits some corresponding changes:
	* 	1. the position in question will be closed for all neighbouring towers of the same color as the token ent.
	* 	2. the position in question will be opened for all neighbouring towers of the same color as the token removingStone.
	*	3. the list of all possible moves of the token removingStone will be actualised with respect to its new position.
	* @param ent the token to be removed.
	* @param removingStone the stone which is going to beat the specified opponent's token and take its place on the board. 
	*/
	private void removeToken(Entity ent, Entity removingStone) {
		setElement(removingStone, ent.getPosition());
		removeFromList(ent);
		findStoneMoves(removingStone);
		positionClosedAndOpenedForTowers(ent.getPosition(), ent.getColor(), true);
	}
	
	/**
	* Proves if there is a tower on the position pos which is not blocked and has color different from col.
	* If so, adds a new move - to the position emptyPos - to the tower's possible moves.
	* @param emptyPos the newly empty position.
	* @param pos some other position on the board neigbouring to emptyPos.
	* @param col the color of the player whose token has just left the position emptyPos.
	*/
	private void actualiseTowerNeighbourIsEmpty(Position emptyPos, Position pos, PlayerColor col) {
		Entity ent = getElement(pos);
		if(ent != null && ent.getColor() != col && ent.isTower() && !ent.isBlocked()) {
			addMove(ent, emptyPos, 1);
		}
	}
	/**
	* Removes the specified position from the list of possible moves for every token of the specified color
	* if this token can reach the position and satisfies the specified requirement (if available).
	* The parameter forAll indicates if the position in question has to be closed for all tokens 
	* of the specified color or only for those which can reach the position with a remote move.
	* @param closedPos the position in question.
	* @param col the color of the tokens which are no more allowed to go to the position closedPos.
	* @param forAll indicates if the position in question has to be closed for all tokens
	*	of the specified color (forAll == true) or only for those which can reach the position with a remote move (forAll == false).
	*/
	private void positionClosed(Position closedPos, PlayerColor col, boolean forAll) {
		Vector<Entity> list = getEntityList(col);
		for(Entity ent: list) {
			int dist = distance(closedPos, ent.getPosition());
			if (!forAll && dist == 1) {
				continue;
			}
			removeMove(ent, closedPos, dist);
		}
	}
	/**
	* Adds the specified position to the list of possible moves for every token of the specified color
	* if this token can reach the position.
	* @param openedPos the position in question.
	* @param col the color of the tokens which are now allowed to go to the position openedPos if they can reach it.
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
	* Adds the specified position to the list of possible moves for every stone (and not for the towers)
	* if this stone can reach the position.
	* @param openedPos the position in question.
	* @param col the color of the stones which are now allowed to go to the position openedPos if they can reach it.
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
	* Commits changes caused by blocking, removing or dismantling of a tower in respect to its neighbours of the same color.
	* 	1. For all neighbouring stones of the same color: their step ranges will be decreased and all corresponding
	*		remote moves will be removed from their lists of possible moves.
	* 	2. For all neighbouring towers of the same color: no changes are needed.
	* @param tower the tower in question.
	* @param change the necessary change in the step range: will be subtracted from the current range values of the neighbouring stones.
	*/
	private void actualiseTowerNeighboursRemoveRanges(Entity tower, int change) {
		HashSet<Move> moves = tower.getMoves().get(1);
		for(Move move: moves) {
			Entity ent = getElement(move.getEnd());
			if(ent != null && !ent.isTower()) {
				removeRanges(ent, change);
			}
		}
	}
	/**
	* Commits changes caused by creating, increasing or unblocking of a tower in respect to its neighbours of the same color.
	* 	1. For all neighbouring stones of the same color: their step ranges will be increased and all newly available
	*		remote moves will be added to their lists of possible moves.
	* 	2. For all neighbouring towers of the same color:
	*		no changes are needed if the height of the increased/unblocked tower is not maximum.
	* 	3. If the tower in question has reached the maximum height, all moves to its position will be removed 
	* 		from the lists of possible moves for all tokens (stones and towers) of the same color in case they had such moves.
	* @param tower the tower whose height has been increased.
	* @param change the necessary change in the step range: will be added to the current range values of the neighbouring stones.
	*/
	private void actualiseOwnTowerUnblockedOrIncreased(Entity tower, int change) {
		HashSet<Move> moves = tower.getMoves().get(1);
		for(Move move: moves) {
			Entity ent = getElement(move.getEnd());
			if(ent != null && !ent.isTower()) { 
				addRanges(ent, change);
			}
		}
		if(tower.isMaxHeight()) {
			positionClosed(tower.getPosition(), tower.getColor(), true);
		}
	}
	/**
	* Commits some necessary changes caused by removing a tower.
	* Case 1: the removed tower was blocked:
	*	its position will be opened for all opponent's tokens (stones and towers);
	* Case 2: the removed tower was not blocked:
	*	1. the step ranges of the neighbouring stones of the same color will be decreased
	*		and the corresponding remote moves will be removed from their lists of possible moves
	*	2. If the tower had the maximal height, its position will be opened for all stones of the same color.
	* @param tower the tower that has been removed.
	*/
	private void actualiseTowerRemoved(Entity tower) {
		removeFromList(tower);
		if(tower.isBlocked()) {
			positionOpened(tower.getPosition(), (tower.getColor() == RED? BLUE: RED));
			return;
		}
		actualiseTowerNeighboursRemoveRanges(tower, tower.getHeight());
		if(tower.isMaxHeight()) {
			positionOpenedStonesOnly(tower.getPosition(), tower.getColor());
		}
	}
	/**
	* Removes the specified position from the list of possible moves for all neighbouring towers of the specified color.
	* If the parameter openForOpponent has value true, the same position will be added to the possible moves
	* list of the neighbouring towers of the other color.
	* @param pos the position in question.
	* @param col the color of the towers which are no more allowed to be dismantled on the position closedPos.
	* @param openForOpponent this parameter defines, if the same position has to be added to the list of possible moves
	*	for the towers of th other color (if so, openForOpponent == true).
	*/
	private void positionClosedAndOpenedForTowers(Position pos, PlayerColor col, boolean openForOpponent) {
		Vector<Position> neighbours = findPositionsInRange(pos, 1);
		for(Position neighbour : neighbours) {
			Entity ent = getElement(neighbour);
			if(ent != null && ent.isTower() && !ent.isBlocked()) {
				if (ent.getColor() == col) {
					removeMove(ent, pos, 1); 
				}
				else if(openForOpponent) {
					addMove(ent, pos, 1);
				}			
			} 
		}
	}
	/**
	* Commits all necessary changes caused by putting a stone on the top of the specified token (stone or tower):
	*	1. The token's height will be increased.
	*	2. The tower moves will be found if originally the token was not a tower.
	*	3. For all neighbouring stones of the same color: their step ranges will be increased respectively and all newly available
	*		remote moves will be added to their lists of possible moves.
	*	4. If the increased tower has reached the maximum height, all moves to its position will be removed 
	* 		from the lists of possible moves for all tokens (stones and towers) of the same color in case they had such moves. 
	* @param tower the token whose height has been increased by a new stone.
	*/
	private void actualiseTowerAddStone(Entity tower) {
		incHeight(tower);
		if(tower.getHeight() == 1) {
			findTowerMoves(tower);
		}
		actualiseOwnTowerUnblockedOrIncreased(tower, 1);
	}
	/**
	* Commits all necessary changes caused by removing a stone from the top of the specified tower:
	*	1. The tower's height will be decreased.
	* 	2. For all neighbouring stones of the same color: their step ranges will be decreased respectively and all corresponding
	*		remote moves will be removed from their lists of possible moves.
	*	3. If the tower had the maximal height, its position will be opened for all tokens (stones and towers) of the same color.
	*	4. If there is only a simple stone left from the tower, 
	*		its possible moves will be calculated and added to its list of possible moves.
	* @param tower the tower whose top stone has to be removed.
	*/
	private void actualiseTowerRemoveStone(Entity tower) {
		actualiseTowerNeighboursRemoveRanges(tower, 1);
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
	* Finds all possible moves for the specified stone.
	* @param stone the stone whose possible moves have to be found.
	*/
	private void findStoneMoves(Entity stone) {
		removeAllMoves(stone);
		int addRanges = 0;	//additional step range
		Vector<Position> closeNeighbours = findPositionsInRange(stone.getPosition(), 1);
		for(Position neighbourPos: closeNeighbours) {
			Entity neighbour = getElement(neighbourPos);
			if(checkMoveForStone(neighbourPos, stone.getColor(), 1) ) {
				addMove(stone, neighbourPos, 1);
			}
			if(neighbour != null && neighbour.isTower() && !neighbour.isBlocked()) {
				if(neighbour.getColor() == stone.getColor()) {
					addRanges += neighbour.getHeight();
				} 
			}
		}
		addRanges(stone, addRanges);
	}
	/**
	* Initialises the board before the first move:
	* 	1. creates and sets all necessary entities on their start positions;
	*	2. adds all entities except the bases to the corresponding red and blue lists of entities (lists of movable tokens).
	*/
        private void initialiseBoard() {
                redBase = new Position(1, 1);
                setElement(new Entity (redBase, RED, size, true), redBase);
                blueBase = new Position(size, size);
                setElement(new Entity (blueBase, BLUE, size, true), blueBase);
		int d = size/2;
		initialiseEntities(redBase, d, RED, listRed);
		initialiseEntities(blueBase, d, BLUE,listBlue);
	}
	/**
	* Initialises all entities of the specified color before the first move. 
	* For all positions on the board whose distance to the specified base position is not greater than the specified distance:
	*	1. creates new entity of the specified color, 
	*	2. sets this entity on the position,
	*	3. determines the entity's possible moves,
	*	4. adds the new entity to the corresponding entity list.
	* @param base position of the base which determines positions of the new entities.
	* @param dist the maximal allowed distance to the base.
	* @param col the color of the player whose entities have to be created and set on the board.
	* @param list the list where all newly created entities have to be stored.
	*/
	private void initialiseEntities(Position base, int dist, PlayerColor col, Vector<Entity> list) {
		for(int i = 1; i <= dist; ++i) {
			Vector<Position> positions = findPositionsInRange(base, i);
			for(Position pos: positions) {
 				Entity ent = new Entity(pos, col, size);
                        	setElement(ent, pos);
				findStoneMoves(ent);
                       		list.add(ent);
			}
		}
	}
}
