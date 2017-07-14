package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.MoveResult.*;
import java.util.Vector;
import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.lang.IllegalStateException;

public class Board extends SimpleBoard {
	public static final int WIN = Integer.MAX_VALUE;
	public static final int LOSE = Integer.MIN_VALUE;
	private static final int DEFENCE = -5000;
	private static final int DEFEND = 5000;

 	/**
        * Initialises a new object of the class PlayerBoard.
        * @param n - size of the new board.
        */
	public Board(int n) {
		super(n);
	}
	public Board(int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		super(size, turn, lRed, lBlue, board, redB, blueB); 
	}
	public Board clone() {
		Entity[][] newBoard = new Entity[size+1][size+1];
		newBoard[1][1] = board[1][1].clone();
		newBoard[size][size] = board[size][size].clone();
		Vector<Entity> newListRed = cloneEntityList(listRed, newBoard);
		Vector<Entity> newListBlue = cloneEntityList(listBlue, newBoard);
		return new Board(size, turn, newListRed, newListBlue, newBoard, redBase, blueBase);
	}

	private Vector<Entity> cloneEntityList(Vector<Entity> list, Entity[][] board2) {
		Vector<Entity> newList = new Vector<Entity>(list.size());
		for(Entity ent: list) {
			Entity ent2 = ent.clone();
			Position pos = ent2.getPosition();
			board2[pos.getLetter()][pos.getNumber()] = ent2;
			newList.add(ent2);
		}
		return newList;
	}
	public ScoreMove scoreMove(Move move, PlayerColor ownColor) {
		return scoreMove(move, ownColor, true);
	}

	/**
	* Evaluates the specified move.
	* @param move - the move which has to be evaluated.
	* @return
	*	a score of the move.
	*/
	private ScoreMove scoreMove(Move move, PlayerColor ownColor, boolean ownBaseCanBeDestroyed) {
		PlayerColor opponentColor = (ownColor == RED? BLUE: RED);
		Status ownWin = (ownColor == RED? RED_WIN: BLUE_WIN);
		Position ownBase = (ownColor == RED? redBase: blueBase);
		Position opponentBase = (ownColor == RED? blueBase: redBase);
		int score = computeScore(move, opponentBase, ownColor);
		// prove if I can beat the opponent's base: if so, return the Score with score and result = WIN
		if(move.getEnd().equals(opponentBase)) {
			return new ScoreMove(move, score, WIN2);
		}
		// prove if the opponet does not have moves after this move (if the opponent loses)
		Board copy = this.clone();
		copy.update(move, ownColor);
		if(copy.getStatus() == ownWin) {
			return new ScoreMove(move, score, WIN2);
		}
		// prove if the opponet can reach my base in his turn after this move: if so, return score with result LOSE
		if(ownBaseCanBeDestroyed && canBeDestroyed(ownBase, copy.getEntityList(opponentColor))) {
			return new ScoreMove(move, score, LOSE2);
		}
		// prove if the opponent can make a move so that I do not have possible moves after it (short varient). If so, return score with result = LOSE
		if (haveManyMoves(copy.getEntityList(ownColor))) {
			return new ScoreMove(move, score, UNKNOWN);
		}
		// prove if the opponent can make a move so that I do not have possible moves after it (longer varient: with executing the move). If so, return score with result = LOSE 
		if(canWin(copy, opponentColor)) {
			return new ScoreMove(move, score, LOSE2);
		}
		// return the score with the result UNKNOWN
		return new ScoreMove(move, score, UNKNOWN);
	}
	private boolean haveManyMoves(Vector<Entity> ownEntities) {
		int nHasManyMoves = 0;
		int nStones = 0;
		for(Entity ent : ownEntities) {
			if(ent.movable()) {
				if(ent.getMoveCounter() > 1) {
					++nHasManyMoves;
				}
				if(!ent.isTower()) {
					++nStones;
				}
				if(nHasManyMoves > 1 || nStones > 1) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean canWin(Board board, PlayerColor col) {
		Status win = (col == RED? RED_WIN : BLUE_WIN);
		Vector<Move> moves = board.allPossibleMoves(col);
		for(Move move: moves) {
			Board copy = board.clone();
			Status prediction = copy.update(move, col);
			if (prediction == win) {
				return true;
			}
		}
		return false;
	}
	private int computeScore(Move move, Position opponentBase, PlayerColor ownColor) {
		Position startPos = move.getStart();
		Position endPos = move.getEnd();
		Entity opponent = getElement(endPos);
		int res = distance(startPos, opponentBase) - distance(endPos, opponentBase);
		if(opponent != null && opponent.getColor() != ownColor) {
			if(!opponent.isTower()) {
				return (res + 1);
			}
			if (distance(startPos, endPos) == 1) {
				return (res + 2);
			}
		}
		return res;
		
	}
	private boolean canBeDestroyed(Position pos, Vector<Entity> entities) {
		for(Entity ent: entities) {
			int dist = distance(ent.getPosition(), pos);
			if(ent.hasMove(pos, dist)) {
				return true;
			}
		}
		return false;
	}
	/*private Move findDestroyMove(Position endPos, Vector<Entity> entities) {
		for(Entity ent: entities) {
			int dist = distance(ent.getPosition(), endPos);
			if(ent.hasMove(endPos, dist)) {
				return new Move(ent.getPosition(), endPos);
			}
		}
		return null;
	}*/
	public Vector<Move> getBestMoves(PlayerColor ownColor) {
		PlayerColor opponentColor = (ownColor == RED? BLUE: RED);
		Vector<Entity> ownEntityList = getEntityList(ownColor);
		Vector<Entity> opponentEntityList = getEntityList(opponentColor);
		Status ownWin = (ownColor == RED? RED_WIN: BLUE_WIN);
		Status ownLose = (ownColor == RED? BLUE_WIN: RED_WIN);
		Position ownBase = (ownColor == RED? redBase: blueBase);
		Position opponentBase = (ownColor == RED? blueBase: redBase);
		boolean opponentCanDestroyBase = canBeDestroyed(ownBase, opponentEntityList);
		Vector<Move> bestMoves = new Vector<Move>();
		PriorityQueue<ScoreMove> scoredMoves = new PriorityQueue<ScoreMove>();
		for(Entity ent : ownEntityList) {
			Vector<HashSet<Move>> allMoves = ent.getMoves();
			for(HashSet<Move> rangeMoves : allMoves) {
				for(Move move : rangeMoves) {
					scoredMoves.add(scoreMove(move, ownColor, opponentCanDestroyBase));
				}
			}		
		}
		if(!scoredMoves.isEmpty()) {
			int curScore = scoredMoves.peek().getScore();
			MoveResult curResult = scoredMoves.peek().getResult();
			while(!scoredMoves.isEmpty() && scoredMoves.peek().getScore() == curScore && scoredMoves.peek().getResult() == curResult) {
				bestMoves.add(scoredMoves.poll().getMove());
			}
		}
		return bestMoves;		
	}

	public int altScore(Move move, PlayerColor playerColor) {
		Position ownBase = playerColor == RED ? redBase : blueBase;
        Position opponentBase = playerColor == RED ? blueBase : redBase;
        Position endPos = move.getEnd();
        if(endPos.equals(opponentBase))
            return WIN;
        Entity opponent = board[endPos.getLetter()][endPos.getNumber()];
        int score = 3 * distance(move.getStart(), opponentBase) - distance(endPos, opponentBase);
        int disToOwnBase = distance(move.getStart(), ownBase);
        if(disToOwnBase < 4 && disToOwnBase < size / 4.0)
        	score = DEFENCE;
        if(opponent != null && score != DEFENCE) {
            if(opponent.getColor() != playerColor) {
                if(opponent.isTower()) {
                    if(distance(move.getStart(), endPos) == 1) {
                        score += 50 * opponent.getHeight();
                    } else {
                        score += 15;
                    }
                } else {
                    score += 45;
                }
            }
        } else if(opponent != null && opponent.getColor() != playerColor) {
        	score = DEFEND;
		}
        return score;
    }

	public Vector<Move> allPossibleMoves(PlayerColor col) {
		Vector<Move> allMoves = new Vector<Move>();
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			if(ent.movable()) {
				int range = ent.getRange();
				Vector<HashSet<Move>> entMoves = ent.getMoves();
				for(int i = 1; i <= range; ++i) {
					allMoves.addAll(entMoves.get(i));
				}
			}
		}
		return allMoves;
	}

}
