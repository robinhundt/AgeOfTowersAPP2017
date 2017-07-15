package towerwarspp.board;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
import towerwarspp.preset.Viewer;

public class BViewer implements Viewer {
	/**
	* The {@link SimpleBoard} object which has be observed by this {@link BViewer}.
	*/
	private SimpleBoard board;
	/**
	* Creates a new instance of the class {@link BViewer}.
	* @param board the viewable board object that has be observed by this {@link BViwer}. 
	*/
	public BViewer(SimpleBoard board) {
		this.board = board;
	}
	/**
	* Returns the size the observed board.
	* @return the size the observed board. 
	*/
	public int getSize() {
		return board.getSize();
	}
	/**
	* Returns the current status of the observed board.
	* @return the current status of the observed board.
	*/
	public Status getStatus() {
		return board.getStatus();
	}
	/**
	* Returns the color of the player whose turn it is to make move.
	* @return the color of the player whose turn it is to make move.
	*/
	public PlayerColor getTurn() {
		return board.getTurn();
	}
	/**
	* Returns a clone of the token located on the specified position on the board.
	* @param position the position of the token whose clone has to be returned.
	* @return a clone of the token located on the specified position on the board or null if the position is empty.
	*/
	public Entity getEntity(Position position) {
		Entity entity = board.getElement(position);
		if(entity != null) {
			return entity.clone();
		}
		return null;
	}
}
