package towerwarspp.io;

import towerwarspp.preset.Viewer;

/**
 * Interface {@link View}
 *
 * This Interface communicates between the Interface {@link IO} and the Class {@link GraphicIO} or {@link TextIO}
 * to control the output of the game.
 *
 * @author Robin Hundt, Kai Kuhlmann
 * @version 0.3 July 14th 2017
 */
public interface View {
    /**
     * Is Save Button clicked
     *
     * @return true if the Save Button is clicked
     */
    boolean getSave();

    /**
     * Get the String which the user has chosen for the Filename
     *
     * @return the Filename
     * @throws Exception if Thread is interrupted
     */
    String getSaveGameName() throws Exception;

    /**
     * Set the Title of the Game
     *
     * @param string additional Information which should be displayed
     */
    void setTitle(String string);

    /**
     * Set the Viewer in the IO
     *
     * @param viewer The Viewer Object which initialize the output
     */
    void setViewer(Viewer viewer);

    /**
     * Starts the Output of the complete Board
     */
    void visualize();

    /**
     * Displays information for each Turn
     *
     * @param string information which should be displayed every turn
     */
    void display(String string);

    /**
     * Displays information in a Dialog
     *
     * @param string information which should be displayed on that Dialog.
     */
    void dialog(String string);
}
