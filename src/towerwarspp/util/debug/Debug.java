package towerwarspp.util.debug;


import static towerwarspp.util.debug.DebugLevel.*;
import static towerwarspp.util.debug.DebugSource.NO_SOURCE_DEBUG;

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


    public String getDebugOutput() {
        String debug = "";
        if(debugMessages.length() > 1) {
            debug =  debugMessages.toString();
            debugMessages = new StringBuffer();
        }
        return debug;
    }

    public DebugLevel getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(DebugLevel debugLevel) {
        this.debugLevel = debugLevel;
        debugging = true;

    }

    public DebugSource getSource() {
        return debugSource;
    }

    public void setSource(DebugSource source) {
        debugSource = source;
        debugging = true;
    }

    @Override
    public String toString() {
        return getDebugOutput();
    }
}
