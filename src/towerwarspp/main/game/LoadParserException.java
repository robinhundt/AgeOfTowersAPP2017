package towerwarspp.main.game;

import java.io.IOException;

/**
 * This Exception is thrown, if an error with the loading occurs
 * @author Alexander Wähling
 */
public class LoadParserException extends IOException {
    public LoadParserException(String msg) {
        super(msg);
    }

    public LoadParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}