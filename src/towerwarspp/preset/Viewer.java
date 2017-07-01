package towerwarspp.preset;

import towerwarspp.board.MoveList;


public interface Viewer {
   	int getSize();
	int getTurn();
	Status getStatus();
	Move[] getPossibleMoves(Position position);
	MoveList possibleMoves(Position p) throws Exception;
	boolean isTower(Position pos);
	boolean isStone(Position pos);
	boolean isBlocked(Position pos);	
	int getHeight(Position pos);
	PlayerColor getPlayerColor(Position pos);
}
