package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.ChangeArt.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Change {
	ChangeArt art;
	Position position;
	Entity ent = null;
	Move move = null;
	Vector<Move> moves = null;
	int step = -1;
//ENTITY_ADDED, ENTITY_REMOVED, TOWER_INCRESE, TOWER_DECREASE, TOWER_BLOCKED, TOWER_UNBLOCKED
	public Change(Entity ent, ChangeArt art) {
		this.ent = ent;
		this.art = art;
	}
//MOVE_ADDED, MOVE_REMOVED
	public Change(Entity ent, Move move, int step, ChangeArt art) {
		this.ent = ent;
		this.move = move;
		this.step = step;
		this.art = art; 
	}
// STEP_REMOVED	
	public Change(Entity ent, Vector<Move> moves, int step) {
		this.ent = ent;
		this.moves = moves;
		this.step = step;
		this.art = STEP_REMOVED;
	}
// STEP_ADDED
	public Change(Entity ent, int step) {
		this.ent = ent;
		this.step = step;
		this.art = STEP_ADDED;
	}
	public ChangeArt getArt() {
		return art;
	}
	public Entity getEntity() {
		return ent;
	}
	/**
	*
	*
	* @throws Exception if this change's art is not MOVE_ADDED or MOVE_REMOVED
	*/
	public Move getMove() throws Exception {
		if (move == null)
			throw new Exception("Illegal operation in Change: move == null");
		return move;
	}
	/**
	*
	*
	* @throws Exception if this change's art is not MOVE_ADDED or MOVE_REMOVED
	*/
	public Vector<Move> getMoves() throws Exception {
		if (moves == null)
			throw new Exception("Illegal operation in Change: moves == null");
		return moves;
	}
	/**
	*
	*
	* @throws Exception if this change's art is not MOVE_ADDED or MOVE_REMOVED
	*/
	public int getStep() throws Exception {
		if (step < 0)
			throw new Exception("Illegal operation in Change: step < 0");
		return step;
	}
}
