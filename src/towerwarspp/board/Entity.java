package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Vector;

/**
 * This Class provides several Functions, used for TowerwarsPP. It's instances are the Stones and
 * Towers of the Game
 *
 * @author Alexander Wähling - 28.07.2017
 * @version 0.2.2.8
 */
public class Entity {

	/**
	 * This is the Position of the Entity at the Board
	 */
	private Position pos;

	/**
	 * Color of the Entity
	 */
	private final PlayerColor color;

	/**
	 * height of the Entity. 0 == normal stone. >0 == Tower
	 */
	private int height;

	/**
	 * max height of the Game
	 */
	private final int maxHeight;

	/**
	 * shows, weather the Entity is blocked or not
	 */
	private boolean blocked = false;

	/**
	 * this variable contains the maximum range of the entity
	 */
	private int range = 1;

	/**
	 * this Array contains as many booleans, as the field has positions. For every position there is a bool,
	 * which shows, if the position is reachable or not.
	 */
	private int[][] reachable;

	/**
	 * this array of vectors shows, which moves are available for every range
	 */
	private Vector<Vector<Move>> rangeMoves;

	/**
	 * size of the gamefield
	 */
	private int size;

	/**
	 * shows, weather this entity is the base or not
	 */
	private boolean base = false;

	/**
	 * counts, how many moves this entity has
	 */
	private int moveCounter = 0;

	/**
	 * The maximum range, which is possible with this boardsize
	 */
	private final int maxRange;

	/**
	 * Constructor for the Entity-Object
	 * @param pos startposition of the object
	 * @param color color of the stone
	 * @param size size of the field (size*size)
	 */
	public Entity (Position pos, PlayerColor color, int size) {
		this.pos = pos;
		this.color = color;
		this.size = size;
		maxHeight = size/3;
		maxRange = 6*maxHeight+2;
		initialiseMoves();
		/*reachable = new int[6*maxHeight+2][size+1];
		rangeMoves = new Vector<Vector<Position>>(6*maxHeight+2);
		for(int i = 0; i <= 6*maxHeight+1; ++i) {
			rangeMoves.add(i, new Vector<Position>(i * 6 + 1));
		}*/
	}
	/**
	 * Constructor for Bases
	 * @param pos startposition of the object
	 * @param color color of the stone
	 * @param size size of the field (size*size)
	 * @param base bool for showing if base or not
	 */
	public Entity (Position pos, PlayerColor color, int size, boolean base) {
		this(pos, color, size);
		this.base = base;
	}

	/**
	 * Copy-Constructor
	 * @param copy Entity to b copied 
	 */
	public Entity(Entity copy) {
		this.pos = copy.getPosition();
		this.color = copy.getColor();
		this.size = copy.getSize();
		maxHeight = size/3;
		reachable = copy.getReachable();
		rangeMoves = copy.getCloneMoves();
		this.range = copy.getRange();
		this.blocked = copy.isBlocked();
		this.base = copy.isBase();
		maxRange = 6*maxHeight+2;
	}

	/**
	 * Returns all Moves, which are possible for a specific range
	 * @return the Vector of positions
	 */
	public Vector<Move> getRangeMoves(int range) {
		return rangeMoves.elementAt(range);
	}

	/**
	 * returns the reachable fields for a specific range
	 * @return the int[] of reachable fields
	 */
	public int[] getRangeReachable(int range) {
		int[] out = reachable[range].clone();
		return out;
	}

	/**
	 * returns the  boardsize for copy-constructor
	 */
	public int getSize() {
		return size;
	}

	/**
	 * returns a clone of the rangeMoves
	 */
	public Vector<Vector<Move>> getCloneMoves() {
		Vector<Vector<Move>> out = new Vector<Vector<Move>>();
		for(int i = 0; i < rangeMoves.size(); i++) {
			out.add((Vector<Move>)rangeMoves.elementAt(i).clone());
		}
		return out;
	}

	/**
	 * returns the Position
	 * @return the Position
	 */
	public Position getPosition() {
		return pos;
	}

	/**
	 * returns the player-Color, of the Entity
	 * @return the color
	 */
	public PlayerColor getColor(){
		return color;
	}

	/**
	 * returns the height of the entity
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * checks, if the entity is blocked or not
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/**
	 * returns the move-range of the entity
	 */
	public int getRange() {
		return range;
	}

	/**
	 * returns the Vector of Vector of the possible Moves
	 */
	public Vector<Vector<Move>> getMoves() {
		return rangeMoves;
	}

	public int[][] getReachable() {
		return reachable;
	}

	/**
	 * checks if this entity is a base or not
	 * @return true if base, false if not
	 */
	public boolean isBase () {
		return base;
	}

	/**
	 * returns the Movecounter
	 * @return moveCounter
	 */
	public int getMoveCounter() {
		return moveCounter;
	}

	/**
	 * checks if the entity is a Tower
	 * @return true if height != 0, false if height == 0
	 */
	public boolean isTower() {
		return height != 0;
	}

	/**
	 * checks if the tower has maxheight
	 * @return true if maxHeight
	 */
	public boolean isMaxHeight() {
		return height == maxHeight;
	}

	/**
	 * sets the Variable blocked
	 * @param blocked the new status of blocked
	 */
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	/**
	 * sets the position of entity, when it moves
	 * @param pos the new position
	 */
	public void setPosition (Position pos) {
		this.pos = pos;
	}

	/**
	 * increases the height by one
	 */
	public void incHeight() {
		height++;
	}

	/**
	 * decreases the height by one
	 */
	public void decHeight() {
		height--;
	}

	/**
	 * This method decreases the possible range and removes all moves, which aren't
	 * posssible anymore
	 */
	public void decRange() {
		moveCounter-= rangeMoves.elementAt(range).size();
		rangeMoves.set(range, new Vector<Move>());
		for(int i = 0; i <= size; i++) {
			reachable[range][i] = 0;
		}
		range--;

	}

	/**
	 * This Method increases the range. The now possible moves have to be added by
	 * the board
	 */
	public void incRange() {
		range++;
	}

	/**
	 * same as incRange but uses given vector and int-array
	 * @param rangeVector movevector for specific range
	 * @param reachableRange reachable-array for specific range
	 */
	public void incRange(Vector<Move> rangeVector, int[] reachableRange) {
		range++;
		rangeMoves.set(range, rangeVector);
		/*for(int i = 0; i < size; i++) {
			reachable[range][i] = reachableRange[i];
		}*/
		reachable[range] = reachableRange;
	}

	/**
	 * This Method adds a possible move to the Entity.
	 * It is added in reachable, rangeMoves and rangeMoves
	 * @param end endposition of the move
	 * @param range distance of the endposition
	 */
	public void addMove(Position end, int range) {
		int temp = 1 << end.getLetter();
		if(!hasMove(end, range)){
			reachable[range][end.getNumber()] |=  temp;
			rangeMoves.elementAt(range).add(new Move(pos, end));
			moveCounter++;
		}
	}

	/**
	 * This Method removes a move from the Entity.
	 * It is removed in reachable and rangeMoves
	 * @param end endposition of the move
	 * @param range distance of the endposition
	 */
	public void removeMove(Position end, int range) {
		int temp = 1 << end.getLetter();
		if(hasMove(end, range)) {
			reachable[range][end.getNumber()] ^=  temp;
			rangeMoves.elementAt(range).remove(new Move(pos, end));
			moveCounter--;
		}
	}

	/**
	 * Checks, if a move is possible or not
	 * @param end the Move to be checked
	 * @param range the range of the move
	 */
	public boolean hasMove(Position end, int range) {
		int temp = 1 << end.getLetter();
		if((reachable[range][end.getNumber()] & temp) == 0){
			return false;
		}
		return true;
	}

	/**
	 * checks, if the Entity is movable
	 * @return true, if movable, false if not
	 */
	public boolean movable() {
		return !(blocked || moveCounter == 0);
	}

	/**
	 * intitalises the rangeMoves-Vector
	 */
	private void initialiseMoves() {
		rangeMoves = new Vector<Vector<Move>>(maxRange);
		for(int i = 0; i < maxRange; ++i) {
			rangeMoves.add(i, new Vector<Move>(i * 6 + 1));
		}
		reachable = new int[maxRange][size+1];
	}
	/**
	 * removes all moves of the entity and sets the range to 1
	 */
	public void removeAllMoves() {
		initialiseMoves();
		range = 1;
	}
	public void setAllMoves(Vector<Vector<Move>> moves, int[][] reach) {
		rangeMoves = moves;
		reachable = reach;
	}
}
