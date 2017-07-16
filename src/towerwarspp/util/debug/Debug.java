package towerwarspp.util.debug;


import static towerwarspp.util.debug.DebugLevel.*;
import static towerwarspp.util.debug.DebugSource.NO_SOURCE_DEBUG;

/**
 * Singleton class used to globally log debug messages. Via {@link Debug#getInstance()} a reference to the current {@link #debug}
 * can be obtained. The {@link DebugLevel} and {@link DebugSource} that should be logged can be specified via {@link #setDebugLevel(DebugLevel)}
 * and {@link #setSource(DebugSource)}.
 * If only the {@link #debugLevel} is set, all received messages that have the same level or lower will be logged.
 * If only the {@link #debugSource} is set, all received messages that originate from the set source will be logged.
 * If both the level and source is set, all messages that originate from this source <b>and</b> have a {@link DebugLevel}
 * equal to the set or lower, will be logged.
 *
 * Messages can be sent by calling {@link #send(DebugLevel, DebugSource, String)}.
 * The currently stored debug output can be retrieved by calling {@link #getDebugOutput()} or by calling the {@link Debug#toString()}
 * method. Retrieving the logged messages will delete them from the Debug object.
 *
 * Note: For debug messages that occur with high frequency {@link #isCollecting()} should first be checked before calling
 * the {@link #send(DebugLevel, DebugSource, String)} method to avoid unnecessary method calls and String creation.
 *
 * @author Robin Hundt
 * @version 16-07-17
 */
public class Debug {
    /**
     * Single instance of this class.
     */
    private static Debug debug;
    /**
     * Is set to true if either {@link #setSource(DebugSource)} or {@link #setDebugLevel(DebugLevel)} is called.
     */
    private boolean debugging;
    /**
     * The {@link DebugLevel} that is being logged.
     */
    private DebugLevel debugLevel = NO_LEVEL_DEBUG;
    /**
     * The {@link DebugSource} that is being logged.
     */
    private DebugSource debugSource = NO_SOURCE_DEBUG;
    /**
     * StringBuffer that is internally used to store all received debug messages.
     */
    private StringBuffer debugMessages;

    /**
     * Can only be called once via {@link #getInstance()}.
     */
    private Debug() {
     debugMessages = new StringBuffer();
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

    /**
     * Returns the current {@link #debug} instance. If {@link #debug} is null the {@link Debug} constructor is called.
     * This way only one instance can exist at any one time.
     * @return the single Debug instance
     */
    public synchronized static Debug getInstance() {
        if(debug == null) {
            debug = new Debug();
            return debug;
        } else {
            return debug;
        }
    }

    /**
     * Returns the currently stored debug messages inside the {@link #debugMessages} StringBuffer. by calling it's toString()
     * method. The Buffer is reset by assigning a new StringBuffer to {@link #debugMessages}.
     * @return the debug messages logged so far
     */
    public String getDebugOutput() {
        String debug = "";
        if(debugMessages.length() > 1) {
            debug =  debugMessages.toString();
            debugMessages = new StringBuffer();
        }
        return debug;
    }


    /**
     * Returns whether the Debug object is collecting.
     * @return true if {@link #debug} is collecting messages
     */
    public boolean isCollecting() {
        return debugging;
    }

    /**
     * Can be used to send debug messages with the specified {@link DebugLevel} and {@link DebugSource} to the {@link #debugMessages} Buffer
     * to await collection by {@link #getDebugOutput()}.
     * @param level of the debug message
     * @param source of the debug message
     * @param msg Message to be sent
     */
    public void send(DebugLevel level, DebugSource source, String msg) {
        if(debugging && debugSource != NO_SOURCE_DEBUG && level.compareTo(debugLevel) <= 0 && debugSource == source) {
            debugMessages.append(source).append(' ').append(level).append(' ').append(msg).append('\n');
        } else if(debugging && debugSource == NO_SOURCE_DEBUG && level.compareTo(debugLevel) <= 0) {
            debugMessages.append(source).append(' ').append(level).append(' ').append(msg).append('\n');
        }
    }

    /**
     * Calling this method will return the currently logged debug output inside {@link #debugMessages}.
     * @return
     */
    @Override
    public String toString() {
        return getDebugOutput();
    }
}
