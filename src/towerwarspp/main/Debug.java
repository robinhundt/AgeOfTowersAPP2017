package towerwarspp.main;


import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.debug.DebugSource;

import static towerwarspp.main.debug.DebugLevel.*;
import static towerwarspp.main.debug.DebugSource.NO_SOURCE_DEBUG;

/**
 * Created by robin on 11.07.17.
 */
public class Debug {

    private static Debug debug;

    private boolean debugging;

    private DebugLevel debugLevel = NO_LEVEL_DEBUG;
    private DebugSource debugSource = NO_SOURCE_DEBUG;

    private StringBuffer debugMessages;

    private Debug() {
     debugMessages = new StringBuffer();
    }

    public synchronized static Debug getInstance() {
        if(debug == null) {
            debug = new Debug();
            return debug;
        } else {
            return debug;
        }
    }

    public boolean isCollecting() {
        return debugging;
    }

    public void send(DebugLevel level, DebugSource source, String msg) {
        if(debugging && debugSource != NO_SOURCE_DEBUG && level.compareTo(debugLevel) <= 0 && debugSource == source) {
            debugMessages.append(source).append(' ').append(level).append(' ').append(msg).append('\n');
        } else if(debugging && debugSource == NO_SOURCE_DEBUG && level.compareTo(debugLevel) <= 0) {
            debugMessages.append(source).append(' ').append(level).append(' ').append(msg).append('\n');
        }
    }


    String getDebugOutput() {
        String debug = "";
        if(debugMessages.length() > 1) {
            debug =  debugMessages.deleteCharAt(debugMessages.length()-1).toString();
            debugMessages = new StringBuffer();
        }
        return debug;
    }

    DebugLevel getDebugLevel() {
        return debugLevel;
    }

    void setDebugLevel(DebugLevel debugLevel) {
        this.debugLevel = debugLevel;
        debugging = true;

    }

    DebugSource getSource() {
        return debugSource;
    }

    void setSource(DebugSource source) {
        debugSource = source;
        debugging = true;
    }

    @Override
    public String toString() {
        return getDebugOutput();
    }
}
