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
		Evaluator evaluator = new Evaluator();
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
	/**
	* Returns all possible moves which a player with the color {@link col} has.
	* @param col - player's color.
	* @return
	*	a vector with all possible moves of the player packed in {@link MoveList} objects or an empty vector if there are no such moves.
	*/
	public Vector<MoveList> getAllPossibleMoves(PlayerColor col) throws Exception {
		Vector<MoveList> moves = new Vector<MoveList>();
		ListIterator<Entity> it = (col == RED? listRed.listIterator(): listBlue.listIterator());
		while(it.hasNext()) {
			Entity ent = it.next();
			if(ent.canMove()) {
				moves.add(new MoveList(ent));
			}
		}
		return moves;
	}
	/**
	* Returns all possible moves which a figure (stone, tower, base) on the position pos has.
	* @param p - position of the figure.
	* @return
	*	a vector with all possible moves which the specified figure has.
	*/
	/*public Move[] stoneMoves(Position p) throws Exception {
		if (p == null) throw new Exception("IllegalArgumentException");
		Entity ent = board[p.getLetter()][p.getNumber()];
		return ent.getMoves().toArray(new Move[ent.getMovesNumber()]);
	}*/
	public Move[] stoneMoves(Position p) throws Exception {
		return null;
	}
	public Vector<Move> allPossibleMoves(PlayerColor col) {
		Vector<Move> moves = new Vector<Move>();
		ListIterator<Entity> it = (col == RED? listRed.listIterator(): listBlue.listIterator());
		while(it.hasNext()) {
			Entity ent = it.next();
			if(ent.canMove()) {
				ListIterator<Position> itP = ent.getMoves().listIterator();
				while(itP.hasNext()) {
					moves.add(new Move(ent.getPosition(), itP.next()));
				}
			}
		}
		return moves;
	}

	/**
	* Creates and returns a new {@link MoveList} object with all possible moves
	* which a figure (stone, tower, base) on the position {@link p} has
	* @param p - position of the figure.
	* @return
	*	a {@link MoveList} with all possible moves which the specified figure has.
	*/
	public MoveList getStoneMoves(Position p) throws Exception {
		Entity ent = board[p.getLetter()][p.getNumber()];
		if (ent == null) throw new Exception ("Position is empty");
		return new MoveList(ent);
	}
}
