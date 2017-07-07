package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Board extends SimpleBoard {

 	/**
        * Initialises a new object of the class PlayerBoard.
        * @param n - size of the new board.
        */
	public Board(int n) {
		super(n);
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
		Position endPos = move.getEnd();
		if (col == RED && endPos.equals(blueBase)) {
			return Integer.MAX_VALUE;
		}
		if (col == BLUE && endPos.equals(blueBase)) {
			return Integer.MAX_VALUE;
		}
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
