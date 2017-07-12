package towerwarspp.preset;

//import towerwarspp.board.MoveList;

import towerwarspp.board.Entity;

import java.util.Vector;


public interface Viewer {
   	int getSize();
	PlayerColor getTurn();
	Status getStatus();
	//MoveList getPossibleMoves(Position p) throws Exception;
	Entity getEntity(Position position);
	Vector<Move> getPossibleMoves(Entity entity) throws Exception;
}
