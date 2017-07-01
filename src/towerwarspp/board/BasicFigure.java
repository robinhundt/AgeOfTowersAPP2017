package towerwarspp.board;

import towerwarspp.preset.*;

public class BasicFigure {
	private Position position;
	private PlayerColor color;	// color of this BasicFigur
	boolean base = true;
	
	public BasicFigure(Position pos, PlayerColor col) {
		position = pos;
		color = col;
	}
	
	public boolean isBase() {
		return base;
	}	
	public Position getPosition() {
		return position;
	}
	
	public PlayerColor getColor() {
		return color;
	}
}
