package towerwarspp.board;

import towerwarspp.preset.*;

public class BoardViewer implements Viewer{
	private Board board;
	
	public BoardViewer (Board b) {
		board = b;
	}
	
	public int getSize() {
		return board.getSize();
	}

	public int getTurn() {
		return board.getTurn();
	}

	public Status getStatus() {
		return board.getStatus();
	}
}
