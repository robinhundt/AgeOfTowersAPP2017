package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Vector;

/**
 * This Class provides several Functions, used for TowerwarsPP. It's instances are the Stones and
 * Towers of the Game
 * 
 * @author Alexander Wähling - 28.07.2017
 * @version 0.1.1.1
 */
public class EntityNew {

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
	private Vector<Vector<Position>> stepMoves = new Vector<Vector<Position>>(60);
	
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
	 * Constructor for the Entity-Object 
	 * @param pos startposition of the object
	 * @param color color of the stone
	 * @param size size of the field (size*size)
	 */
	public EntityNew (Position pos, PlayerColor color, int size) {
		this.pos = pos;
		this.color = color;
		this.size = size;
		reachable = new int[size*2][size];
		maxHeight = size/3;
	}
	/**
	 * Constructor for Bases
	 * @param pos startposition of the object
	 * @param color color of the stone
	 * @param size size of the field (size*size)
	 * @param base bool for showing if base or not
	 */
	public EntityNew (Position pos, PlayerColor color, int size, boolean base) {
		this(pos, color, size);
		this.base = base;
	}

	/**
	 * Returns all Moves, which are possible for a specific range
	 * @return the Vector of positions
	 */
	public Vector<Position> getRangeMoves(int range) {
		return stepMoves.elementAt(range);
	}

	/**
	 * returns the reachable fields for a specific range
	 * @return the int[] of reachable fields
	 */
	public int[] getRangeReachable(int range) {	
		int [] out = reachable[range].clone();
		return out;
	}

	/**
	 * returns the Position
	 * @return the Position
	 */
	public Position getPos() {
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
	 * checks if this entity is a base or not
	 * @return true if base, false if not
	 */
	public boolean isBase () {
		return base;
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
		moveCounter-= stepMoves.elementAt(range).size();
		stepMoves.set(range, new Vector<Position>());
		for(int i = 0; i < size; i++) {
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
	 * This Method adds a possible move to the Entity.
	 * It is added in reachable, stepMoves and stepMoves
	 * @param end endposition of the move
	 * @param range distance of the endposition
	 */
	public void addMove(Position end, int range) {
		int temp = 1 << end.getLetter();
		if(hasMove(end, range)){
			reachable[range][end.getNumber()] |=  temp;
			stepMoves.elementAt(range).add(end);
			moveCounter++;
		}		
	}

	/**
	 * This Method removes a move from the Entity.
	 * It is removed in reachable and stepMoves
	 * @param end endposition of the move
	 * @param range distance of the endposition
	 */
	public void removeMove(Position end, int range) {
		int temp = 1 << end.getLetter();
		if(!hasMove(end, range)) {
			reachable[range][end.getNumber()] = reachable[range][end.getNumber()] ^ temp;
			stepMoves.elementAt(range).remove(end);
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
			return true;
		}
		return false;
	}
}
