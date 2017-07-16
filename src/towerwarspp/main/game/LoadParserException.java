package towerwarspp.main.game;

import java.io.IOException;

/**
 * This Exception is thrown, if an error with the loading occurs
 * @author Alexander Wähling
 */
public class LoadParserException extends IOException {

    /**
     * the Constructor for th exception
     * @param msg the message to be called
     */
    public LoadParserException(String msg) {
        super(msg);
    }
}