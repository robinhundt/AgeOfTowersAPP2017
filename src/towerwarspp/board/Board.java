package towerwarspp.board;

import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;
import static towerwarspp.board.MoveResult.*;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Vector;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
/**
 * This class represents an extended board which, on the one hand, has the same functionality as {@link SimpleBoard}
 * and on the other hand, gives the possibility to score moves according to different score strategies, including the simple one.
 * On request, the Board provides a list of all possible moves which the specified player has. 
 * It also determines and delivers the best possible moves for the specified player evaluated according to the simple score strategy.
 *
 * @author Anastasiia Kysliak
 * @version 15-07-17
 */
public class Board extends SimpleBoard {
	public static final int WIN = Integer.MAX_VALUE;
	public static final int LOSE = Integer.MIN_VALUE;
	private static final int DEFENCE = -5000;
	private static final int DEFEND = 5000;

 	/**
         * Initialises a new object of the class Board.
         * @param n - size of the new board.
         */
	public Board(int n) {
		super(n);
	}
	/**
         * Initialises a new object of the class Board with the specified parameters.
         * @param size size of the new board.
         * @param turn current turn.
         * @param lRed list of all red entities except the red base.
         * @param lBlue list of all blue entities except the blue base.
         * @param board representation of the board as a 2-dimensional array of entities.
         * @param redB position of the red base.
         * @param blueB position of the blue base.
         */
	public Board(int size, PlayerColor turn, Vector<Entity> lRed, Vector<Entity> lBlue, Entity[][] board, Position redB, Position blueB) {
		super(size, turn, lRed, lBlue, board, redB, blueB); 
	}
	/**
	 * Clones the current {@link Board} object and returns the clone.
	 * @return the clone oof this {@link Board} object.
	 */
	public Board clone() {
		Entity[][] newBoard = new Entity[size+1][size+1];
		newBoard[1][1] = getElement(new Position(1, 1)).clone();
		newBoard[size][size] = getElement(new Position(size, size)).clone();
		Vector<Entity> newListRed = cloneAndPutOnBoard(listRed, newBoard);
		Vector<Entity> newListBlue = cloneAndPutOnBoard(listBlue, newBoard);
		return new Board(size, turn, newListRed, newListBlue, newBoard, redBase, blueBase);
	}
	/**
	 * Returns all possible moves which a player of the color col has.
	 * @param col the color of the player whose possible moves have to be returned.
	 * @return all possible moves which a player of the color col has.
	 */
	public Vector<Move> allPossibleMoves(PlayerColor col) {
		Vector<Move> allMoves = new Vector<Move>();
		Vector<Entity> list = getEntityList(col);
		for(Entity ent: list) {
			if(ent.isMovable()) {
				int range = ent.getRange();
				Vector<HashSet<Move>> entMoves = ent.getMoves();
				for(int i = 1; i <= range; ++i) {
					allMoves.addAll(entMoves.get(i));
				}
			}
		}
		return allMoves;
	}

	/**
	 * Computes an alternative score for the passed move representing an aggressive play strategy with some focus on
	 * defending the base by assigning a very low score to moves that cause an entity to leave an area around the base
	 * dependant on the board size.
	 * @param move move to evaluate
	 * @param playerColor player making that move
	 * @return score for this move
	 */
	public int altScore(Move move, PlayerColor playerColor) {
		Position ownBase = playerColor == RED ? redBase : blueBase;
        Position opponentBase = playerColor == RED ? blueBase : redBase;
        Position endPos = move.getEnd();
        if(endPos.equals(opponentBase))
            return WIN;
        Entity opponent = getElement(endPos);
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
	/**
	 * Returns a {@link MoveScore} object representing the information on the specified move: its score made according to the simple strategy
	 * and its possible consequence like immediate win or lose, or unknown result.
	 *
	 */
	public MoveScore scoreMove(Move move, PlayerColor ownColor) {
		return scoreMove(move, ownColor, true);
	}
	/**
	 * Returns the best moves available for the player of the color ownColor which have been rated 
	 * according to the simple score strategy and with respect to each available move's possible consequence.
	 * @param ownColor the color of the player whose best possible moves have to be returned.
	 * @return the best possible moves which a player of the color ownColor has.
	 */
	public Vector<Move> getBestMoves(PlayerColor ownColor) {
		PlayerColor opponentColor = (ownColor == RED? BLUE: RED);
		Vector<Entity> ownEntityList = getEntityList(ownColor);
		Vector<Entity> opponentEntityList = getEntityList(opponentColor);
		Position ownBase = (ownColor == RED? redBase: blueBase);
		boolean opponentCanDestroyBase = canBeDestroyed(ownBase, opponentEntityList);
		Vector<Move> bestMoves = new Vector<Move>();
		PriorityQueue<MoveScore> scoredMoves = new PriorityQueue<MoveScore>();
		for(Entity ent : ownEntityList) {
			Vector<HashSet<Move>> allMoves = ent.getMoves();
			for(HashSet<Move> rangeMoves : allMoves) {
				for(Move move : rangeMoves) {
					scoredMoves.add(scoreMove(move, ownColor, opponentCanDestroyBase));
				}
			}		
		}
		/*if(opponentCanDestroyBase) {
			System.out.println("My moves " + scoredMoves.size());
			System.out.println(scoredMoves.peek().getResult() + " " + scoredMoves.peek().getScore());
			for(MoveScore sc : scoredMoves) {
				System.out.print(sc.getResult() + " " + sc.getScore() + ", ");
			}
			System.out.println();
		}*/
		if(!scoredMoves.isEmpty()) {
			/*System.out.println("All scores");
			PriorityQueue test = new PriorityQueue<MoveScore>();
			while(!scoredMoves.isEmpty()) {
				MoveScore sc = scoredMoves.poll();
				System.out.print(sc.getMove() + " " + sc.getScore() + " " + sc.getResult() + ", ");
				test.add(sc);
			}
			scoredMoves = test;*/
			int curScore = scoredMoves.peek().getScore();
			MoveResult curResult = scoredMoves.peek().getResult();
			while(!scoredMoves.isEmpty() && scoredMoves.peek().getScore() == curScore && scoredMoves.peek().getResult() == curResult) {
				bestMoves.add(scoredMoves.poll().getMove());
			}
			/*System.out.println("Best");
			for(Move move : bestMoves) {
				System.out.print(move +  ", ");
			}
			System.out.println();*/
		}
		return bestMoves;		
	}
	/**
	 * Evaluates the specified move.
	 * @param move the move which has to be evaluated.
	 * @param ownColor the color of the player whose turn it is to make a move and whose move has to be evaluated.
	 * @param ownBaseCanBeDestroyed indicates if the opponent has a move which can destroy the current player's base.
	 * 	If the value is true, it will be proved if the opponent still can destroy the base after the move in question.
	 *	If the value is false, no further examination concerning the current player's base is needed.  
	 * @return evaluation result as a MoveScore object containing the score and information on the possible consequence of the move.
	 */
	private MoveScore scoreMove(Move move, PlayerColor ownColor, boolean ownBaseCanBeDestroyed) {
		PlayerColor opponentColor = (ownColor == RED? BLUE: RED);
		Status ownWin = (ownColor == RED? RED_WIN: BLUE_WIN);
		Position ownBase = (ownColor == RED? redBase: blueBase);
		Position opponentBase = (ownColor == RED? blueBase: redBase);
		int score = computeScore(move, opponentBase, ownColor);
		if(move.getEnd().equals(opponentBase)) {
			return new MoveScore(move, score, WILL_WIN);
		}
		Board copy = this.clone();
		copy.makeMove(move);
		if(copy.getStatus() == ownWin) {
			return new MoveScore(move, score, WILL_WIN);
		}
		if(ownBaseCanBeDestroyed && canBeDestroyed(ownBase, copy.getEntityList(opponentColor))) {
			return new MoveScore(move, score, CAN_LOSE);
		}
		if (haveManyMoves(copy.getEntityList(ownColor))) {
			return new MoveScore(move, score, UNKNOWN);
		} 
		if(playerCanWin(copy, opponentColor)) {
			return new MoveScore(move, score, CAN_LOSE);
		}
		return new MoveScore(move, score, UNKNOWN);
	}
	/**
 	 * Computes a score for the given move using the simple strategy.
 	 * @param move the move in question.
	 * @param opponentBase the position of the opponent's base.
	 * @param ownColor the color of the player who can make the specified move.
	 * @return the score of the move made according to the simple score strategy.
	 */
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
	/**
	 * This function helps to prove if a player has enough tokens and possible moves so that the opponent cannot leave him/her 	
	 * without any moves by only one move. It returns true if at least two tokens in the list ownEntities have 2 or more moves
	 * or there is at least one stone and one other token with 2 or more possible moves in this list.
	 * @param ownEntities the list of the tokens belonging to the player whose lose resistance has to be proved.
	 * @return true if the player in question has enough moves and tokens not to lose as a result of the opponnt's move
	 *		because of the absence of possible moves.
	 */
	private boolean haveManyMoves(Vector<Entity> ownEntities) {
		int nHasManyMoves = 0;
		int nStones = 0;
		for(Entity ent : ownEntities) {
			if(ent.isMovable()) {
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
	/**
	 * Returns true if at least one of the moves available for the player of the color col
	 * leads to the win.
	 * @param board the board with the current game situation.
	 * @param col the color of the player in question. It is this player's turn to make a move.
	 * @return true at least one of the moves available for the player of the color col
	 * 	leads to the win.
	 */
	private boolean playerCanWin(Board board, PlayerColor col) {
		Status win = (col == RED? RED_WIN : BLUE_WIN);
		Vector<Move> moves = board.allPossibleMoves(col);
		for(Move move: moves) {
			Board copy = board.clone();
			Status prediction = copy.makeMove(move);
			if (prediction == win) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Proves if a token located at the specified position can be destroyed (or blocked) by the opponent's stone.
	 * Returns true if this is possible which means if at least one opponent's token has a possible move to the specified position.
	 * @param pos the position of the token for which it has to be proved if it can be destroyed or blocked.
	 * @param entities all movable tokens belonging to the opponent.
	 * @return true if at least one token from entities has a possible move to the position pos.
	 */
	private boolean canBeDestroyed(Position pos, Vector<Entity> entities) {
		for(Entity ent: entities) {
			int dist = distance(ent.getPosition(), pos);
			if(ent.hasMove(pos, dist)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Clones all entities in the given entity list and saves the clones on the correct positions
	 * in the specified array representation of the board.
	 * @param list the list of entities which has to be cloned.
	 * @param cloneBoard the 2-dimensional array representation of the board 
	 *	where all the cloned entities have to be saved on the correct positions.
	 */
	private Vector<Entity> cloneAndPutOnBoard(Vector<Entity> list, Entity[][] cloneBoard) {
		Vector<Entity> cloneList = new Vector<Entity>(list.size());
		for(Entity ent: list) {
			Entity cloneEnt = ent.clone();
			Position pos = cloneEnt.getPosition();
			cloneBoard[pos.getLetter()][pos.getNumber()] = cloneEnt;
			cloneList.add(cloneEnt);
		}
		return cloneList;
	}
}
