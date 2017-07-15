package towerwarspp.preset;

//import towerwarspp.board.MoveList;

import towerwarspp.board.Entity;

import java.util.Vector;


public interface Viewer {
   	int getSize();
	PlayerColor getTurn();
	Status getStatus();
	Entity getEntity(Position position);
}
