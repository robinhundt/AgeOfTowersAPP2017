package towerwarspp.board;

import towerwarspp.preset.*;

import java.util.HashSet;
import java.util.Vector;
import java.lang.Exception;

public class BViewer implements Viewer {
	private SimpleBoard boardO;
	private Entity[][] board;
	private int size;

	public BViewer(SimpleBoard b, Entity[][] ar, int sz) {
		boardO = b;
		board = ar;
		size = sz;
	}
	public Vector<Move> getPossibleMoves(Entity ent) throws Exception{
		if (ent == null) {
			throw new Exception ("Entity is null");
		}
		Vector<Move> moves = new Vector<Move>();
		if(ent.movable()) {
			int range = ent.getRange();
			Vector<HashSet<Move>> entMoves = ent.getMoves();
			for(int i = 1; i <= range; ++i) {
				moves.addAll(entMoves.get(i));
			}
		}
		return moves;
	}
	public int getSize() {
		return size;
	}
	public Status getStatus() {
		return boardO.getStatus();
	}
	public PlayerColor getTurn() {
		return boardO.getTurn();
	}
	/**
	 * Returns an element located on the specified position on the board.
	 * @param pos the position of the element that has to be returned.
	 * @return an element located on the specified position on the board.
	 */
	private Entity getElement(Position pos) {
		return board[pos.getLetter()][pos.getNumber()];
	}

	public Entity getEntity(Position position) {
		Entity entity = getElement(position);
		if(entity != null) {
			return entity.clone();
		}
		return null;
	}
}
