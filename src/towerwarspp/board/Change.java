package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.ChangeType.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Change {
	private ChangeType type;
	private Position position;
	private Entity ent = null;
	private Vector<Move> rangeMoves = null;
	private int[] rangeReachable = null;
	private Vector<Vector<Move>> allMoves = null;
	private int[][] reachable = null;
	private int range = -1;
	private int order;
//ENTITY_ADDED, ENTITY_REMOVED, HEIGHT_INCREASED, HEIGHT_DECREASED, TOWER_BLOCKED, TOWER_UNBLOCKED, RANGE_INC
	public Change(Entity ent, ChangeType type, int order) {
		this.ent = ent;
		this.type = type;
		this.order = order;
	}
//MOVE_ADDED, MOVE_REMOVED
	public Change(Entity ent, Position moveEndPos, int range, ChangeType type, int order) {
		this(ent, type,  order);
		this.position = moveEndPos;
		this.range = range;
	}
// RANGE_DEC	
	public Change(Entity ent, Vector<Move> rangeMoves, int[] rangeReachable, int range, int order) {
		this(ent, RANGE_DEC, order);
		this.rangeMoves = rangeMoves;
		this.rangeReachable = rangeReachable;
		this.range = range;
	}
// ALL_MOVES_REMOVED
	public Change(Entity ent, Vector<Vector<Move>> allMoves, int[][] reachable, int range, int order) {
		this(ent, ALL_MOVES_REMOVED, order);
		this.allMoves = allMoves;
		this.reachable = reachable;
		this.range = range;
	}
// ELEMENT_REPLACED, POSITION_CHANGED
	public Change(Entity ent, Position pos, ChangeType type, int order) {
		this(ent, type, order);
		this.position = pos;
	}
	public ChangeType getType() {
		return type;
	}
	public int getOrder() {
		return order;
	}
	public Entity getEntity() {
		return ent;
	}
	/**
	*
	*
	* @throws Exception if this change's type is not RANGE_DEC
	*/
	public Vector<Move> getRangeMoves() throws Exception {
		if (rangeMoves == null)
			throw new Exception("Illegal operation in Change: rangeMoves == null");
		return rangeMoves;
	}

	/**
	*
	*
	* @throws Exception if this change's type is not RANGE_DEC
	*/
	public int[] getRangeReachable() throws Exception {
		if (rangeReachable == null)
			throw new Exception("Illegal operation in Change: rangeReachable == null");
		return rangeReachable;
	}
	/**
	*
	*
	* @throws Exception if this change's type is not ALL_MOVES_REMOVED
	*/
	public Vector<Vector<Move>> getAllMoves() throws Exception{
		if (allMoves == null)
			throw new Exception("Illegal operation in Change: allMoves == null");
		return allMoves;
	}
	/**
	*
	*
	* @throws Exception if this change's type is not ALL_MOVES_REMOVED
	*/
	public int[][] getReachable() throws Exception{
		if (reachable == null)
			throw new Exception("Illegal operation in Change: reachable == null");
		return reachable;
	}
	/**
	*
	*
	* @throws Exception if this change's type is not MOVE_ADDED, MOVE_REMOVED, RANGE_DEC or ALL_MOVES_REMOVED
	*/
	public int getRange() throws Exception {
		if (range < 0)
			throw new Exception("Illegal operation in Change: range < 0");
		return range;
	}
	/**
	*
	*
	* @throws Exception if this change's type is not MOVE_ADDED, MOVE_REMOVED, ELEMENT_REPLACED or POSITION_CHANGED.
	*/
	public Position getPosition() throws Exception {
		if (position == null)
			throw new Exception("Illegal operation in Change: position == null");
		return position;
	}
}
