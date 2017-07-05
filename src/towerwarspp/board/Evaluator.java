package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.ChangeArt.*;
import java.util.Vector;
import java.util.Stack;
import java.util.ListIterator;
import java.lang.Exception;

public class Evaluator extends SimpleBoard{
	/*protected int size;
        protected PlayerColor turn = RED;
        protected Status status = OK;
        protected Vector<Entity> listRed = new Vector<Entity>();
        protected Vector<Entity> listBlue = new Vector<Entity>();
        protected Entity[][] board;
        protected Position redBase;
        protected Position blueBase;
	private Move curMove;*/
	private Stack<Change> stack = new Stack<Change>();

	public Evaluator(int n) {
		super(n);
	}
	public Evaluator (int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		super(size, turn, lRed, lBlue, board, redB, blueB);
	}
	public int evaluate(Move move, int order) {
		makeMove(move, turn);
		return 0;
	}
	/**
	* Puts the specified figure on the specified position on the board.
	* @param ent the figure in question.
	* @param pos position for the speciifiied figure to be placed on.
	*/
	private void setElement(Entity ent, Position pos) {
		board[pos.getLetter()][pos.getNumber()] = ent;
	}
	/**
	* Returns an element located on the specified position on the board.
	* @param pos the position of the element that has to be returned.
	* @return an element located on the specified position on the board.
	*/
	private Entity getElement(Position pos) {
		return board[pos.getLetter()][pos.getNumber()];
	}
	private void block(Entity tower) {
		tower.setBlocked(true);
	}
	private void unblock(Entity tower) {
		tower.setBlocked(false);
	}
	private void addMove(Entity ent, Position pos, int range) {
		ent.addMove(pos, range);
	}
	private void removeMove(Entity ent, Position pos, int range) {
		ent.removeMove(pos, range);
	}
	/**
	* Increases the step width of the specified figure (stone) by n and adds 
	* newly available positions in the new range to its list of possible moves.
	* @param stone the figure (stone) whose step width has to be increased.
	* @param n amount of steps that has to be added.
	*/
	private void addSteps(Entity stone, int n) {
		for(int i = 0; i < n; ++i) {
			addStep(stone);
			Vector<Position> opponents = findPositionsInRange(stone.getPosition(), stone.getStep());
			ListIterator<Position> it = opponents.listIterator();
			while(it.hasNext()) {
				Position opponentPos = it.next(); 
				if(checkMoveForStone(opponentPos, stone.getColor(), stone.getStep() )) {
					addMove(stone, opponentPos, stone.getStep());
				}
			}
		}
	}
	private void addStep(Entity ent) {
		ent.removeStep();
	}
	private void removeStep(Entity ent) {
		ent.removeStep();
	}
	private void addStone(Entity tower) {
		tower.addStone();
	}
	private void removeStone(Entity tower) {
		tower.removeStone();
	}
	private void setPosition(Entity ent, Position pos) {
		ent.setPosition(pos);
	}
	/**
	* Adds the specified figure to the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	private void addToList(Entity ent) {
		if(ent.getColor() == RED) listRed.add(ent);
		else listBlue.add(ent);
	}
	/**
	* Removes the specified figure from the the list of movable figures of the corresponding color.
	* @param ent the figure in question.
	*/
	private void removeFromList(Entity ent) {
		if(ent.getColor() == RED) listRed.remove(ent);
		else listBlue.remove(ent);
	}
}

