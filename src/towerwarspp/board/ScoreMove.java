package towerwarspp.board;

import towerwarspp.preset.*;

public class ScoreMove implements Comparable<ScoreMove> {
	private Move move;
	private int score;
	private MoveResult result;

	public ScoreMove(Move move, int score, MoveResult result) {
		this.move = move;
		this.score = score;
		this.result = result;
	}

	public Move getMove() {
		return move;
	}

	public int getScore() {
		return score;
	}
	public MoveResult getResult() {
		return result;
	}

	public int compareTo(ScoreMove other) {
		if (other == null) {
				throw new java.lang.NullPointerException();
		}
		int comp = this.result.compareTo(other.getResult());
		if(comp == 0) {
			return other.getScore() - this.score;
		}
		return comp;
	}
}
