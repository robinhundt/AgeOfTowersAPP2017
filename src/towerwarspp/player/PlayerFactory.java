package towerwarspp.player;

import towerwarspp.preset.Player;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.PlayerType;
import towerwarspp.preset.Requestable;

/**
 * Created by robin on 23.06.17.
 */
public class PlayerFactory {
    private PlayerFactory(){}

//    public static Player makeHumanPlayer(int boardSize, PlayerColor playerColor, Requestable moveDeliver) throws Exception {
//        return new HumanPlayer(moveDeliver);
//    }

    public static Player makePlayer(int boardSize, PlayerColor playerColor, PlayerType playerType, Requestable moveDeliver) throws Exception {
        Player player;
        switch (playerType) {
            case HUMAN: player = new HumanPlayer(moveDeliver);
            // case REMOTE: player = new NetPlayer() /*TODO implement Remote*/ break;
            case RANDOM_AI: player = new RndPlayer();       break;
            case SIMPLE_AI: player =  new SimplePlayer();   break;
            case ADVANCED_AI_1: player = new AdvPlayer();   break;
            default: throw new IllegalArgumentException("Unsupported PlayerType");
        }
        return player;
    }



}
