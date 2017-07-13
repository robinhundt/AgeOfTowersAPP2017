package towerwarspp.main.game;

import java.io.IOException;

public class LoadParserException extends IOException {
    public LoadParserException(String msg) {
        super(msg);
    }

    public LoadParserException(String msg, Throwable cause) {
        super(msg, cause);
    }
}