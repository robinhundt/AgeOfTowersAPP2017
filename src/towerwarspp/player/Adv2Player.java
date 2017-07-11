package towerwarspp.player;

import towerwarspp.board.Board;
import towerwarspp.io.View;
import towerwarspp.player.mcts.Mcts;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Status;

import java.util.Random;
import java.util.Vector;

import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;
import static towerwarspp.preset.Status.OK;

/**
 * Created by robin on 23.06.17.
 */
public class Adv2Player extends BasePlayer {
    private final int simCount;
    Random random;

    public Adv2Player(int simCount) {
        this.simCount = simCount;
        random = new Random();
    }

    @Override
    Move deliverMove() throws Exception {
        Mcts mcts = new Mcts(board.clone(), color);
        Move move = mcts.bestMove(10000);
        System.out.println("Making move " + move);
        return move;
    }

//    @Override
//    public void update(Move enemyMove, Status boardStatus) throws Exception {
//        super.update(enemyMove, boardStatus);
//        mcts.feedEnemyMove(enemyMove);
//    }
//
//    @Override
//    public void init(int boardSize, PlayerColor playerColor) {
//        super.init(boardSize, playerColor);
//        mcts = new Mcts(board.clone(), playerColor);
//    }

//    @Override
//    Move deliverMove() throws Exception {
//        Move bestMove = null;
//        int moveCount = 0;
//        PlayerColor turn = color;
//        for(Move move : board.allPossibleMoves(color)) {
//            int count = 0;
//            for(int i=0; i < 1000; i++) {
//                Board cp = board.clone();
//                turn = color;
//                cp.update(move, turn);
////                System.out.println("here");
//                turn = turn == RED ? BLUE : RED;
//                while (cp.getStatus() == OK) {
////                    System.out.println(cp.getStatus());
//                    Vector<Move> moves = cp.allPossibleMoves(turn);
//                    Move rndMove = moves.get(random.nextInt(moves.size()));
//                    cp.update(rndMove, turn);
////                    System.out.println("There");
////                    System.out.println(cp.getStatus());
//                    if(cp.getStatus() == OK)
//                        turn = turn == RED ? BLUE : RED;
//                }
//                if(turn == color)
//                    count++;
//            }
//            if(count > moveCount) {
//                moveCount = count;
//                bestMove = move;
//            }
//        }
//
//        return bestMove;
//    }


//
//    }
//        Move bestMove = null;
//        int bestWinCounter = -1;
//
//        for(Move move : board.allPossibleMoves(color)) {
//            int winCount = 0;
//            for(int i=0; i < simCount; i++) {
////                System.out.println("sim: " + i);
//                Board copy = board.clone();
//                copy.update(move, color);
//                SimGame sim = new SimGame(copy, color == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED);
//                if(sim.simulate() == 1)
//                    winCount++;
//            }
//            if(winCount > bestWinCounter)
//                bestMove = move;
//
//        }
//        return bestMove;
//    }
}
