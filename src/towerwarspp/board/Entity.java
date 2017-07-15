package towerwarspp.board;

import static towerwarspp.main.debug.DebugLevel.*;
import static towerwarspp.main.debug.DebugSource.*;

import java.util.Vector;
import java.util.HashSet;

import towerwarspp.main.debug.*;
import towerwarspp.main.Debug;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
/**
 * This Class provides several Functions, used for TowerwarsPP. It's instances are the Stones and
 * Towers of the Game
 *
 * @author Alexander Wähling - 28.07.2017
 * @version 0.2.2.8
 */
public class Entity {

	/**
	 * The Position of the Entity on the board.
	 */
	private Position pos;

	/**
	 * The color of the Entity.
	 */
	private final PlayerColor color;

	/**
	 * The height of the Entity. If height == 0, this entity represents a normal stone. If height > 0, it is a tower.
	 */
	private int height;

	/**
	 * The maximum allowed height.
	 */
	private final int maxHeight;

	/**
	 * Shows whether the Entity is blocked or not.
	 */
	private boolean blocked = false;

	/**
	 * Represents the current step range of the Entity.
	 */
	private int range = 1;

	/**
	 * this array of vectors shows, which moves are available for every range
	 */
	private Vector<HashSet<Move>> rangeMoves;

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
	 * @param original Entity to be copied
	 */
	public Entity(Entity original) {
		this.pos = original.pos;
		this.color = original.color;
		this.height = original.height;
		this.size = original.size;
		this.range = original.range;
		this.blocked = original.blocked;
		this.base = original.base;
		this.moveCounter = original.moveCounter;
		this.maxHeight = original.maxHeight;
		this.maxRange = original.maxRange;
		this.rangeMoves = original.copyRangeMoves();
	}



	/**
	 * Returns all Moves, which are possible for a specific range
	 * @return the Vector of positions
	 */
	public HashSet<Move> getRangeMoves(int range) {
		return rangeMoves.elementAt(range);
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
	synchronized private Vector<HashSet<Move>> copyRangeMoves() {
		Vector<HashSet<Move>> out = new Vector<HashSet<Move>>();
		for(HashSet<Move> vector : rangeMoves) {
			out.add(new HashSet<Move>(vector));
		}

		return out;
	}

	/**
	 * Returns the Position.
	 * @return the Position.
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
	public Vector<HashSet<Move>> getMoves() {
		return rangeMoves;
	}

	public Vector<Move> getMovesAsVector() {
		Vector<Move> moves = new Vector<Move>();
		if(movable()) {
			for(int i = 1; i <= range; ++i) {
				moves.addAll(rangeMoves.get(i));
			}
		}
		return moves;
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
	synchronized public void decRange() {
		moveCounter-= rangeMoves.elementAt(range).size();
		rangeMoves.set(range, new HashSet<Move>());
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
	 */
	synchronized public void incRange(HashSet<Move> rangeVector) {
		range++;
		rangeMoves.set(range, rangeVector);
		moveCounter += rangeVector.size();
	}

	/**
	 * This Method adds a possible move to the Entity.
	 * It is added in rangeMoves and rangeMoves
	 * @param end endposition of the move
	 * @param range distance of the endposition
	 */
	synchronized public void addMove(Position end, int range) {
		if(rangeMoves.get(range).add(new Move(pos, end))) {
			++moveCounter;
		}
	}

	/**
	 * This Method removes a move from the Entity.
	 * It is removed in and rangeMoves
	 * @param end endposition of the move
	 * @param range distance of the endposition
	 */
	synchronized public void removeMove(Position end, int range) {
		if(rangeMoves.get(range).remove(new Move(pos, end))) {
			--moveCounter;
		}
	}

	/**
	 * Checks, if a move is possible or not
	 * @param end the Move to be checked
	 * @param range the range of the move
	 */
	synchronized public boolean hasMove(Position end, int range) {
		return rangeMoves.get(range).contains(new Move(pos, end));
	}

	/**
	 * checks, if the Entity is movable
	 * @return true, if movable, false if not
	 */
	public boolean movable() {
		return !(blocked || moveCounter == 0);
	}
	private boolean hasPossibleMove() {
		for(int i = 1; i <= range; ++i) {
			if(!rangeMoves.get(range).isEmpty()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * intitalises the rangeMoves-Vector
	 */
	synchronized private void initialiseMoves() {
		rangeMoves = new Vector<HashSet<Move>>(maxRange);
		for(int i = 0; i < maxRange; ++i) {
			rangeMoves.add(i, new HashSet<Move>(i * 6 + 1));
		}
	}
	/**
	 * removes all moves of the entity and sets the range to 1
	 */
	public void removeAllMoves() {
		initialiseMoves();
		moveCounter = 0;
		range = 1;
	}

	/**
	 * This method gives an Entity all moves
	 * @param rangeMoves the moves in the vector of vectors
	 */
	public void setAllMoves(Vector<HashSet<Move>> rangeMoves, int range, int moveCounter) {
		this.rangeMoves = rangeMoves;
		this.range = range;
		this.moveCounter = moveCounter;
	}

	/**
	 * This method clones this instance of the entity with the copy-constructor
	 * @return the clones entityt
	 */
	@Override
	synchronized public Entity clone() {
		return new Entity(this);
	}
}
