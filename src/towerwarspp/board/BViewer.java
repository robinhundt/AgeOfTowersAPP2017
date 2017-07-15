package towerwarspp.board;

import java.util.HashSet;
import java.util.Vector;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
import towerwarspp.preset.Viewer;

public class BViewer implements Viewer {
	private SimpleBoard board;
	private int size;

	public BViewer(SimpleBoard board, int size) {
		this.board = board;
	}
	public int getSize() {
		return board.getSize();
	}
	public Status getStatus() {
		return board.getStatus();
	}
	public PlayerColor getTurn() {
		return board.getTurn();
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
	/**
	 * Returns an element located on the specified position on the board.
	 * @param pos the position of the element that has to be returned.
	 * @return an element located on the specified position on the board.
	 */
	private Entity getElement(Position pos) {
		return board.getElement(pos);
	}

	public Entity getEntity(Position position) {
		Entity entity = getElement(position);
		if(entity != null) {
			return entity.clone();
		}
		return null;
	}
}
