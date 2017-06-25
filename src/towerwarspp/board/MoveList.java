package towerwarspp.board;

import towerwarspp.preset.*;
import java.util.Collection;
import java.util.Iterator;
import java.lang.Exception; 

/**
* Class {@link MoveList} contains information about possible moves of a figure specified by its position on the board.
* This class makes it possible to traverse the list of possible moves holded by the corresponding {@link Entity}-object 
* whithout a risk of any unsanctioned changes in that list.
* @author Anastasiia Kysliak - 24.06.2017
* @version 1
*/
public class MoveList {
	/**
	* Class {@link MoveListIterator} represents an iterator over all moves saved in {@link MoveList}.
	* It implements methods {@link hasNext()} and {@link next()} and does not support the method remove() from the Iterator interface.
	* @author Anastasiia Kysliak - 24.06.2017
	* @version 1
	*/
	private class MoveListIterator implements Iterator<Move> {
		/**
		* Iterator over the collection of all positions which the specified figure can reach.
		*/
		private Iterator<Position> it;

		/**
		* Creates a new object of the class {@link MoveListIterator}. 
		*/
		public MoveListIterator() {
			it = moves.iterator();
		}

		/**
		* Returns true if the iteration has more elements. 
		* (In other words, returns true if {@link next()} would return an element rather than throwing an exception.)
		* @return 
		*	true if the iteration has more elements.
		*/
		public boolean hasNext() {
			return it.hasNext();
		}

		/**
		* Returns the next element in the iteration:
		* creates a new {@link Move] object with {@link start} as the start position
		* and the next element from {@link moves} returned by {@link it} (it.next()) as the end one.
		* @return
		*	the next {@link Move} element in the iteration.
		* @throws
		*	NoSuchElementException - if the iteration has no more elements.
		*/
		public Move next() {
			return new Move(start, it.next());
		}
	}
	/*
	* Start position of all moves saved in this MoveList.
	*/
	private Position start;

	/*
	* Collection of all positions which the specified figure can reach (end positions of the moves). 
	*/
	private Collection<Position> moves;

	/**
	* Creates a new object of the class {@link MoveList}.
	* @param ent - the taget figure.
	*/
	public MoveList(Entity ent) {
		start = ent.getPosition();
		moves = ent.getMoves();
	}
	
	/**
	* Returns an iterator over the elements in this MoveList.
	* @return 
	*	iterator over the elements in this MoveList.
	*/
	public MoveListIterator iterator(){
		return new MoveListIterator();
	}

	/**
	* Returns true if this MoveList contains no elements.
	* @return 
	*	true if this MoveList contains no elements.
	*/
	public boolean isEmpty() {
		return moves.isEmpty();
	}

	/**
	* Returns the number of elements in this MoveList.
	* @return
	*	the number of elements in this MoveList.
	*/
	public int size() {
		return moves.size();
	}
}
