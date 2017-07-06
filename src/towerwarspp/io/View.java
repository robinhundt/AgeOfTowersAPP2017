package towerwarspp.io;

import towerwarspp.preset.Viewer;

/**
 * Interface {@link View}
 *
 * @author Robin Hundt
 * @version 0.2 July 06th 2017
 */
public interface View {
    void visualize();
    void setViewer(Viewer viewer);
}
