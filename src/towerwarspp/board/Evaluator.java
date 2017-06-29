package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.ChangeArt.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Evaluator {
	int size;
	private PlayerColor turn;
	private Vector<Entity> listRed = new Vector<Entity>();
	private Vector<Entity> listBlue = new Vector<Entity>();
	private Entity[][] board;
	Move move;

	public Evaluator() {
	}
	public int evaluate(Entity[][] board, Vector<Entity> lRed, Vector<Entity> lBlue, int n, PlayerColor col, Move move) {
		size = n;
		this.board = board;
		listRed = lRed;
		listBlue = lBlue;
		turn = col;
		this.move = move;

		return 0;
	}
	
}

