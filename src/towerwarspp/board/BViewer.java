package towerwarspp.board;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
import towerwarspp.preset.Viewer;
/**
* This class implements the interface Viewer in the preset package and represents a board viewer
* which observes a board object. It offers the possibility for the classes from other packages 
* to get information from the board object without access to the board's private variables.
* {@link BViewer} provides the following information: the board's size, its current status, current turn, and 
* what token, if any, is located on some particular field. The token in question must be an instance of the {@link Entity} class.
* This {@link BViewer} clones the token located on the field in question if this field is not empty and delivers this clone to the caller.
*
* @author Anastasiia Kysliak
* @version 15-07-17
*/
public class BViewer implements Viewer {
	/**
	* The {@link SimpleBoard} object which has to be observed by this {@link BViewer}.
	*/
	private SimpleBoard board;
	/**
	* Creates a new instance of the class {@link BViewer}.
	* @param board the viewable board object that has to be observed by this {@link BViewer}.
	*/
	public BViewer(SimpleBoard board) {
		this.board = board;
	}
	/**
	* Returns the size of the observed board.
	* @return the size of the observed board. 
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
