package towerwarspp.board;

import towerwarspp.preset.*;
import towerwarspp.io.*;
import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import java.util.Vector;
import java.util.ListIterator;
import java.lang.Exception;

public class Board extends SimpleBoard {
	public static final int WIN = Integer.MAX_VALUE;
	public static final int LOSE = Integer.MIN_VALUE;
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

	/**
	* Evaluates the specified move.
	* @param move - the move which has to be evaluated.
	* @return
	*	a score of the move.
	*/


	public int scoreMove(Move move) {
		return 0;
	}
	/**
	* Evaluates the specified move.
	* @param move - the move which has to be evaluated.
	* @return
	*	a score of the move.
	*/
	public int scoreMove(Move move, PlayerColor col) {
		Evaluator evaluator = new Evaluator(size, turn, listRed, listBlue, board, redBase, blueBase);
		Status prediction = evaluator.evaluate(move, 2);
		switch(prediction) {
			case RED_WIN: return (col == RED? WIN: LOSE);
			case BLUE_WIN: return (col == RED? LOSE: WIN);
		}
		Position endPos = move.getEnd();
		Entity opponent = board[endPos.getLetter()][endPos.getNumber()];
		Position base = (col == RED? blueBase: redBase);
		int res =  distance(move.getStart(), base) - distance(endPos, base);
		if (opponent == null) {
			return res;
		}
		if(opponent.getColor() != col) {
			if(!opponent.isTower()) {
				return (res + 1);
			}
			if (distance(move.getStart(), endPos) == 1)
				return (res + 2);
		}
		return res;
	}
	/**
	* Evaluates the specified move.
	* @param move - the move which has to be evaluated.
	* @return
	*	a score of the move.
	*/
	public int scoreMove2(Move move, PlayerColor ownColor) {
		PlayerColor opponentColor = (ownColor == RED? BLUE: RED);
		Status ownWin = (ownColor == RED? RED_WIN: BLUE_WIN);
		Status ownLose = (ownColor == RED? BLUE_WIN: RED_WIN);
		Position ownBase = (ownColor == RED? redBase: blueBase);
		Position opponentBase = (ownColor == RED? blueBase: redBase);
		if(move.getEnd().equals(opponentBase)) {
			return WIN;
		}
		Board copy = this.clone();
		copy.makeMove(move, ownColor);
		if(copy.getStatus() == ownWin) {
			return WIN;
		}
		Vector<Entity> opponentEntities = (ownColor == RED? copy.listBlue: copy.listRed);
		if(canBeDestroyed(ownBase, opponentEntities)) {
			return LOSE;
		}
		Vector<Entity> ownEntities = (ownColor == RED? copy.listRed: copy.listBlue);
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
					return computeScore(move, opponentBase, ownColor);
				}
			}
		}
		Board cloneCopy = copy.clone();
		Vector<Move> opponentMoves = copy.allPossibleMoves(opponentColor);
		for(Move opponentMove: opponentMoves) {
			Status prediction = cloneCopy.makeMove(opponentMove, opponentColor);
			if (prediction == ownLose) {
				return LOSE;
			}
		}
		return computeScore(move, opponentBase, ownColor);
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
			if (distance(startPos, endPos) == 1)
				return (res + 2);
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
	public int simpleScoreMove(Move move, PlayerColor playerColor) {
		Position opponentBase = playerColor == RED ? blueBase : redBase;
		Position ownBase = playerColor == RED ? redBase : blueBase;
		PlayerColor enemy = playerColor == RED ? BLUE : RED;
		Status enemyWin = playerColor == RED ? BLUE_WIN : RED_WIN;
		if(move.getEnd().equals(opponentBase))
			return WIN;
		else {
			Board copy = this.clone();
			copy.update(move, playerColor);
			Vector<Move> enemyMoves = copy.allPossibleMoves(enemy);
//			System.out.println(enemyMoves.size() + "size of enemy moves");
			boolean enemyHasWinMove = false;
			for(Move enemyMove : enemyMoves) {
//				System.out.println("enemy move + " + enemyMove + "after " + move);
				Board copyClone = copy.clone();
			    copyClone.update(enemyMove, enemy);
				if(copyClone.getStatus() == enemyWin){
//					System.out.println("Loosing move " + move + " enemy " + enemyMove);
					enemyHasWinMove = true;
					break;
				}
			}

			if(enemyHasWinMove)
				return LOSE;
			else {
				return verySimpleScoreMove(move, playerColor);
			}
		}
	}

	public int verySimpleScoreMove(Move move, PlayerColor playerColor) {
		Position opponentBase = playerColor == RED ? blueBase : redBase;
		Position endPos = move.getEnd();
		if(endPos.equals(opponentBase))
		    return WIN;
		Entity opponent = board[endPos.getLetter()][endPos.getNumber()];
		int score = distance(move.getStart(), opponentBase) - distance(endPos, opponentBase);
		if (opponent == null) {
			return score;
		}
		if (opponent.getColor() != playerColor) {
			if (!opponent.isTower()) {
				return (score + 1);
			}
			if (distance(move.getStart(), endPos) == 1)
				return (score + 2);
		}
		return score;
	}

	public int altScore(Move move, PlayerColor playerColor) {
        Position opponentBase = playerColor == RED ? blueBase : redBase;
        Position endPos = move.getEnd();
        if(endPos.equals(opponentBase))
            return WIN;
        Entity opponent = board[endPos.getLetter()][endPos.getNumber()];
        int score = distance(move.getStart(), opponentBase) - distance(endPos, opponentBase);
        if(opponent != null) {
            if(opponent.getColor() != playerColor) {
                if(opponent.isTower()) {
                    if(distance(move.getStart(), endPos) == 1) {
                        score += 25 * opponent.getHeight();
                    } else {
                        score += 15;
                    }
                } else {
                    score += 20;
                }
            } else {
                score += 1;
            }
        }
        return score;
    }

	public Vector<Move> allPossibleMoves(PlayerColor col) {
		Vector<Move> allMoves = new Vector<Move>();
		Vector<Entity> list = (col == RED? listRed: listBlue);
		for(Entity ent: list) {
			if(ent.movable()) {
			int range = ent.getRange();
			Vector<Vector<Move>> entMoves = ent.getMoves();
			for(int i = 1; i <= range; ++i) {
				allMoves.addAll(entMoves.get(i));
			}
		}
		}
		return allMoves;
	}

}
