package towerwarspp.main;

import towerwarspp.preset.ArgumentParser;
import towerwarspp.preset.ArgumentParserException;

/**
 * Class {@link ageoftowers} initializing a new game of TowerWarsPP with given settings <br>
 *     from the commandline using the ArgumentParser.
 *
 * @version 0.1 20th June 2017
 * @author Niklas Mueller
 */
public class AgeOfTowers {

    /**
     * Constructor to initialize a new game
     * @param args list with settings to put into the ArgumentParser
     */
    public AgeOfTowers(String[] args) {
        try {
            ArgumentParser ap = new ArgumentParser(args);

            //System.out.println("local: " + ap.isLocal());     //will probably be removed
            System.out.println("network: " + ap.isNetwork());
            System.out.println("size: " + ap.getSize());

            //System.out.println("graphic: " + ap.isGraphic());


            switch (ap.getRed()) {
                case HUMAN:
                    System.out.println("Red chose human player");
                    /*
                    <HumanPlayer> red = new <HumanPlayer>();
                     */
                    break;
                case RANDOM_AI:
                    System.out.println("Red chose random player");
                    /*
                    <AIPlayer> red = new <AIPlayer>();
                     */
                    break;
                case SIMPLE_AI:
                    System.out.println("Red chose simple player");
                    /*
                    <SimplePlayer red = new <SimplePlayer>();
                     */
                    break;
                case ADVANCED_AI_1:
                    System.out.println("Red chose advanced player");
                    /*
                    <AdvancedPlayer> red = new <AdvancedPlayer>();
                     */
            }

            switch (ap.getBlue()) {
                case HUMAN:
                    System.out.println("Blue chose human player");
                    /*
                    <HumanPlayer> blue = new <HumanPlayer>();
                     */
                    break;
                case RANDOM_AI:
                    System.out.println("Blue chose random player");
                    /*
                    <AIPlayer> blue = new <AIPlayer>();
                     */
                    break;
                case SIMPLE_AI:
                    System.out.println("Blue chose simple player");
                    /*
                    <SimplePlayer blue = new <SimplePlayer>();
                     */
                    break;
                case ADVANCED_AI_1:
                    System.out.println("Blue chose advanced player");
                    /*
                    <AdvancedPlayer> blue = new <AdvancedPlayer>();
                     */
            }

            //
        }
        catch (ArgumentParserException e) {
            e.printStackTrace();
        }

        red.init(ap.getSize(), PlayerColor.RED);
        blue.init(ap.getSize(), PlayerColor.BLUE);


        /*initialize board object with given size */


        /*
        initialize two player-objects with given settings from interface towerwarspp.preset.Player;
            both players are using the same object from a class implementing Requestable-Interface

          request alternately moves from both players, check validity and inform other player

          do move on board

          output current gamestatus (graphic/textual)

          tell output if move is leading to end the game

          possibility to play with a network player

         */
    }

    public static void main(String[] args) {
        AgeOfTowers game = new AgeOfTowers(args);
    }

}