package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Board extends SimpleBoard {
	public static final int WIN = Integer.MAX_VALUE;
	public static final int LOSE = Integer.MIN_VALUE;
 	/**
        * Initialises a new object of the class PlayerBoard.
        * @param n - size of the new board.
        */
	public Board(int n) {
		super(n);
	}
	public Board(int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		super(size, turn, lRed, lBlue, board, redB, blueB); 
	}
	public Board clone() {
		Entity[][] newBoard = new Entity[size+1][size+1];
		newBoard[1][1] = board[1][1].clone();
		newBoard[size][size] = board[size][size].clone();
		Vector<Entity> newListRed = cloneEntityList(listRed, newBoard);
		Vector<Entity> newListBlue = cloneEntityList(listBlue, newBoard);
		return new Board(size, turn, newListRed, newListBlue, newBoard, redBase, blueBase);
	}

	private Vector<Entity> cloneEntityList(Vector<Entity> list, Entity[][] board2) {
		Vector<Entity> newList = new Vector<Entity>(list.size());
		for(Entity ent: list) {
			Entity ent2 = ent.clone();
			Position pos = ent2.getPosition();
			board2[pos.getLetter()][pos.getNumber()] = ent2;
			newList.add(ent2);
		}
		return newList;
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
	* Evaluates the specified move.
	* @param move - the move which has to be evaluated.
	* @return
	*	a score of the move.
	*/
	public int scoreMove(Move move, PlayerColor col) {
		Evaluator evaluator = new Evaluator(size, turn, listRed, listBlue, board, redBase, blueBase);
		Status prediction = evaluator.evaluate(move, 2);
		switch(prediction) {
			case RED_WIN: return (col == RED? WIN: LOSE);
			case BLUE_WIN: return (col == RED? LOSE: WIN);
		}
		Position endPos = move.getEnd();
		Entity opponent = board[endPos.getLetter()][endPos.getNumber()];
		Position base = (col == RED? blueBase: redBase);
		int res =  distance(move.getStart(), base) - distance(endPos, base);
		if (opponent == null) {
			return res;
		}
		if(opponent.getColor() != col) {
			if(!opponent.isTower()) {
				return (res + 1);
			}
			if (distance(move.getStart(), endPos) == 1)
				return (res + 2);
		}
		return res;
	}

	public Vector<Move> allPossibleMoves(PlayerColor col) {
		Vector<Move> allMoves = new Vector<Move>();
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			getEntityMoves(ent, allMoves);
		}
		return allMoves;
	}

}
