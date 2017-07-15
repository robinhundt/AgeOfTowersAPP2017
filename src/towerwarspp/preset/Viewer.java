package towerwarspp.preset;

//import towerwarspp.board.MoveList;

import towerwarspp.board.Entity;

import java.util.Vector;

/**
* This is an interface for a board viewer. It has to observe an object which represents a board and deliver
* the following information to the caller: 
*	the board's size, 
*	the board's current status, 
*	the current turn,
*	the clone of a token located on some particular field of the board if this field is not empty.
*
* @author Kai Kuhlmann
* @author Anastasiia Kysliak
* @version 15-07-17
*/
public interface Viewer {
	/**
	* Returns the size the observed board.
	* @return the size the observed board. 
	*/
   	int getSize();
	/**
	* Returns the current status of the observed board.
	* @return the current status of the observed board.
	*/
	Status getStatus();
	/**
	* Returns the color of the player whose turn it is to make move.
	* @return the color of the player whose turn it is to make move.
	*/
	PlayerColor getTurn();
	/**
	* Returns a clone of the token located on the specified position on the board.
	* @param position the position of the token whose clone has to be returned.
	* @return a clone of the token located on the specified position on the board or null if the position is empty.
	*/
	Entity getEntity(Position position);
}
