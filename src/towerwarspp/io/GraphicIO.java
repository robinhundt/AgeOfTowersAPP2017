package towerwarspp.io;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import towerwarspp.board.Entity;
import towerwarspp.main.Debug;
import towerwarspp.main.debug.DebugLevel;
import towerwarspp.main.debug.DebugSource;
import towerwarspp.preset.Viewer;
import towerwarspp.preset.Move;
import towerwarspp.preset.Position;
import towerwarspp.preset.Status;
import towerwarspp.preset.PlayerColor;
/**
 * Class {@link GraphicIO} interacts with the User over a GUI
 *
 * @version 0.6 July 07th 2017
 * @author Kai Kuhlmann
 */
public class GraphicIO extends JFrame implements IO {
    /**
     * Viewer-Object
     */
    private Viewer viewer;
    /**
     * HexagonGrid-Object
     */
    private HexagonGrid hexagonGrid;
    /**
     * Polygon-Object
     */
    private Polygon[][] polygon;
    /**
     * Polygon-Object for the marks of which player has turn
     */
    private Polygon[][] markedPolygon;
    /**
     * JFrame-Object
     */
    private JFrame jFrame;
    /**
     * JPanel-Object
     */
    private JPanel jPanel;
    /**
     * Polygon Size
     */
    private int polySize;
    /**
     * MoveList-Object of possible Moves
     */
    private Vector<Move> possibleMoves;
    /**
     * deliveMove-Object
     */
    private Move deliverMove;
    /**
     * JPanel-Object for right Buttons
     */
    private JPanel buttonPanel;
    /**
     * Textarea for output of turn, player
     */
    private JTextField debugLine;
    /**
     * TextArea in ResultDialog
     */
    private JTextArea resultArea;
    /**
     * The ResultDialog on which is shown the result of the tournament or the game
     */
    private JDialog resultDialog;
    /**
     * Boolean if is clicked on a token
     */
    private boolean clicked;
    /**
     * The SaveDialog which names the savefile
     */
    private JDialog saveDialog;
    /**
     * Boolean if the saveButton is clicked
     */
    private boolean save;
    /**
     * The name of the savefile
     */
    private String saveGameName;
    /**
     * Debug Instance of {@link Debug}
     */
    private Debug debugInstance;
    /**
     * Constructor of the Class {@link GraphicIO}
     *
     * Creates a JFrame and add a ComponentListener which listens on the resize of the JFrame
     *
     * Adds to the JPanel an MouseMotionListener and an MouseMoveListener for the click Event and mousemouve Event
     *
     * Calculates the Size of the JPanel
     *
     */
    public GraphicIO() {
        this.debugInstance = Debug.getInstance();
        this.jFrame = new JFrame("Age of Towers");
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.jFrame.addComponentListener(getFrameResize());
        jPanel = getJPanel();
        jPanel.addMouseListener(getMouseListener());
        jPanel.addMouseMotionListener(getMouseListener());
        jPanel.setPreferredSize(new Dimension(this.jFrame.getWidth() - 136, this.jFrame.getHeight()));
        this.buttonPanel = getButtonPanel();
        this.debugLine = getDebugLine();
        this.resultDialog = getResultDialog();
        this.saveDialog = getSaveDialog();
        this.clicked = false;
        this.save = false;
        debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "GraphicIO initialized.");
    }
    /**
     * Get if Game should be saved
     * @return true if game should be saved
     */
    public boolean getSave() {
        return this.save;
    }
    /**
     * Get the Name of the SaveGame
     * @return name of the savegame
     */
    public String getSaveGameName() {
        return this.saveGameName;
    }
    /**
     * Setter of Viewer and Initialize of JPanels
     *
     * Creates the {@link HexagonGrid} when not initialized before
     *
     * Get the Arrays of {@link Polygon} which are
     *  1. All Hexagons
     *  2. All Hexagons a little smaller (for marking the tokens of the actual Player)
     *
     *  Add all JPanels to the JFrame
     *
     * @param viewer Viewer-Object
     */
    @Override
    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
        debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Viewer is set.");
        if(this.hexagonGrid == null) {
            setPolygonSize();
            this.hexagonGrid = new HexagonGrid(this.viewer.getSize(), polySize);
            this.polygon = this.hexagonGrid.getPolygon();
            this.markedPolygon = this.hexagonGrid.getMarkedPolygon();
            jFrame.add(buttonPanel, BorderLayout.EAST);
            jFrame.add(jPanel, BorderLayout.WEST);
            jFrame.add(debugLine, BorderLayout.SOUTH);
            jFrame.pack();
            if (!jFrame.isVisible()) {
                jFrame.setVisible(true);
            }
            debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "HexagonGrid, Polygon, markedPolygon and JFrame initialized/added");
        }
    }
    /**
     * Creates the ResultDialog after a Tournament with a Close Button which closes the Application
     * @return returns ResultDialog
     */
    private JDialog getResultDialog() {
        JDialog resultDialog = new JDialog(this.jFrame, "Result");
        this.resultArea = new JTextArea();
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Close Button in ResultDialog clicked.");
                System.exit(0);
            }
        });
        this.resultArea.setEditable(false);
        resultDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        resultDialog.add(this.resultArea, BorderLayout.LINE_START);
        resultDialog.add(close, BorderLayout.AFTER_LAST_LINE);
        resultDialog.setResizable(false);
        debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "ResultDialog created.");
        return resultDialog;
    }
    /**
     * Set the Text of the ResultDialog and set the Dialog on visible
     */
    @Override
    public void dialog(String string) {
        this.resultArea.setText(string);
        this.resultDialog.pack();
        this.resultDialog.setVisible(true);
        debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "ResultDialog shown.");
    }
    /**
     * Sets the Title of the JFrame
     * @param string String who should be in the Title
     */
    public void setTitle(String string) {
        this.jFrame.setTitle("Age of Towers - " + string);
        debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Title of JFrame set.");
    }
    /**
     * Creates the ButtonPanel with the two Buttons surrender and save
     * Surrender Button sends a null Move for surrendering
     * save button opens a JDialog
     * @return returns the ButtonPanel
     */
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.setSize(new Dimension(135, 100));
        JButton surrenderButton = getSurrenderButton();
        surrenderButton.setSize(135,100);
        buttonPanel.add(surrenderButton);
        JButton save = new JButton("Save and Exit");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "Savebutton clicked.");
                showSaveDialog();
            }
        });
        save.setSize(135,100);
        buttonPanel.add(save);
        debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "ButtonPanel initialized.");
        return buttonPanel;
    }
    /**
     * Creates a TextField at the buttom of the JFrame
     * and shows actual messages
     * @return returns the DebugLine
     */
    private JTextField getDebugLine() {
        JTextField debugLine = new JTextField();
        debugLine.setPreferredSize(new Dimension(this.jPanel.getWidth(), 25));
        debugLine.setEditable(false);
        debugLine.setVisible(true);
        debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "DebugLine initialized.");
        return debugLine;
    }
    /**
     * Set the textarea of ButtonPanel to display e.g turn and player
     * @param string Information which should be displayed
     */
    @Override
    public void display(String string) {
        debugLine.setText(string);
    }
    /**
     * Creates the SaveDialog where the humanPlayer can choose the savename
     * @return returns the savedialog
     */
    private JDialog getSaveDialog() {
        JDialog saveDialog = new JDialog();
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel saveText = new JLabel("Game Name:");
        JTextField saveFileName = new JTextField();
        saveFileName.setPreferredSize(new Dimension(150, 25));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(saveFileName.getText().equals("")) {
                    save = true;
                    saveGameName = saveFileName.getText();
                    debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "Save is true. saveFileName String is set.");
                }
            }
        });
        saveDialog.setTitle("Save Game");
        savePanel.add(saveText);
        savePanel.add(saveFileName);
        savePanel.add(saveButton);
        saveDialog.add(savePanel);
        saveDialog.setSize(200, 100);
        saveDialog.setResizable(false);
        debugInstance.send(DebugLevel.LEVEL_6, DebugSource.IO, "SaveDialog initialized.");
        return saveDialog;
    }
    /**
     * Set the saveDialog visible
     */
    private void showSaveDialog() {
        this.saveDialog.setVisible(true);
        debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "SaveDialog visible.");
    }
    /**
     * Listener on Resize of Frame
     *
     * Recalculate the {@link Polygon} size
     *
     * Update the {@link HexagonGrid}
     *
     * @return the Adapter
     */
    private ComponentAdapter getFrameResize() {
        return new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                jPanel.setPreferredSize(new Dimension(jFrame.getWidth() - 136, jFrame.getHeight()));
                setPolygonSize();
                hexagonGrid.updatePolygonSize(polySize);
                jPanel.repaint();
                debugInstance.send(DebugLevel.LEVEL_3, DebugSource.IO, "Resize: Panel repainted.");
            }
        };
    }
    /**
     * Setter of the size of a single Polygon
     *
     * Calculates the {@link Polygon} size based on the size of the Frame
     *
     */
    private void setPolygonSize() {
        double frameWidth = this.jFrame.getWidth() - 150;
        double frameHeight = this.jFrame.getHeight();
        double gridX = (frameWidth / (viewer.getSize() + ((1.0 / 2.0) * (viewer.getSize() - 1)))) / 2.0;
        double gridY = frameHeight / (viewer.getSize() * 2.0);
        gridX = gridX * Math.cos(Math.toRadians(30.0));
        this.polySize = gridX < gridY ? (int)gridX : (int)gridY;
    }
    /**
     * Get the coordinates of the clicked token
     * @param mouseX X-Coordinate of the Mouse
     * @param mouseY Y-Coordinate of the Mouse
     * @return Coordinates of the Entity
     */
    private int[] getCoordinatesOfPolygon(int mouseX, int mouseY) {
        int[] coordinates = new int[2];
        for (int y = 1; y <= viewer.getSize(); ++y) {
            for (int x = 1; x <= viewer.getSize(); ++x) {
                if(this.polygon[x][y].contains(mouseX, mouseY)) {
                    coordinates[0] = x;
                    coordinates[1] = y;
                    debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Return coordinate of Hexagon. (" + x + ", " + y + ")");
                    return coordinates;
                }
            }
        }
        return null;
    }
    /**
     * Creates the MouseListener for the JPanel for the clicks and hover on stones
     * When you hover over a token it shows the possible Moves of this token same for click,
     * but only possible on own Stones
     * @return MouseListener
     */
    private MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            Position positionStart = null;
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int[] position = getCoordinatesOfPolygon(mouseX, mouseY);
                /*-- left mouse click --*/
                if (e.getButton() == 1 && position != null) {
                    debugInstance.send(DebugLevel.LEVEL_6, DebugSource.IO, "Left Mouseclick.");
                    try {
                        positionStart = new Position(position[0], position[1]);
                        Entity entity = viewer.getEntity(positionStart);
                        /*-- not Empty & right player & not endofgame --*/
                        if (entity != null && viewer.getTurn() == entity.getColor() && viewer.getStatus() == Status.OK) {
                            debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Clicked on token.");
                            possibleMoves = viewer.getPossibleMoves(entity);
                            clicked = true;
                            jPanel.repaint();
                        } else {
                            debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Clicked on empty Hexagon.");
                            possibleMoves = null;
                            clicked = false;
                            jPanel.repaint();
                        }
                    } catch (Exception ex) {
                        System.out.println("mouseClick" + ex);
                        System.exit(1);
                    }
                } else if (e.getButton() == 3 && position != null) { /*-- right mouse click --*/
                    debugInstance.send(DebugLevel.LEVEL_6, DebugSource.IO, "Right mouseclick.");
                    deliverMove = new Move(positionStart, new Position(position[0], position[1]));
                    possibleMoves = null;
                    clicked = false;
                    wakeUp();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int[] position = getCoordinatesOfPolygon(mouseX, mouseY);
                if(position != null && !clicked) {
                    try {
                        positionStart = new Position(position[0], position[1]);
                        Entity entity = viewer.getEntity(positionStart);
                        /*-- not Empty & right player & not endofgame --*/
                        if (entity != null && viewer.getStatus() == Status.OK) {
                            debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Mouse on token.");
                            possibleMoves = viewer.getPossibleMoves(entity);
                            jPanel.repaint();
                        } else {
                            possibleMoves = null;
                            jPanel.repaint();
                        }
                    } catch (Exception ex) {
                        System.out.println("mouseMove" + ex);
                        System.exit(1);
                    }
                }
            }
        };
    }
    /**
     * Creates the JPanel where the HexagonGrid and the Entities are displayed
     * @return JPanel
     */
    private JPanel getJPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                if (viewer != null) {
                    Graphics2D g = (Graphics2D) graphics;
                    int distance = (int) ((Math.cos(Math.toRadians(30.0)) * polySize) * 2.0);
                    Font letterFont = new Font("TimesRoman", Font.BOLD, distance / 2);
                    Font stoneFont = new Font("TimesRoman", Font.BOLD, distance / 4);
                    super.paintComponent(g);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

                    g.setColor(Color.BLACK);
                    g.setFont(letterFont);
                    g.setStroke(new BasicStroke(5));

                    drawLetters(g, distance);
                    drawPossibleMoves(g);
                    for (int y = 1; y <= viewer.getSize(); ++y) {
                    /*-- draw the left Numbers --*/
                        g.setFont(letterFont);
                        drawNumbers(g, distance, y);
                        for (int x = 1; x <= viewer.getSize(); ++x) {
                            Position position = new Position(x, y);
                            Entity entity = viewer.getEntity(position);
                            g.setFont(stoneFont);
                        /*-- draw Grid --*/
                            g.setColor(Color.BLACK);
                            g.drawPolygon(polygon[x][y]);
                        /*-----*/
                            drawEntity(g, entity, distance, x, y);
                        }
                    }
                }
            }
        };
    }
    /**
     * Returns the Char for the Token-Type
     * @param entity Tower or Stone
     * @return the type of the Token as Char
     */
    private char[] getChar(Entity entity) {
        int intHeight = entity.getHeight();
        char height = Character.forDigit(intHeight, 10);
        char[] chars = new char[2];
        if(entity.isBase()) {
            chars[0] = 'B';
        } else if(entity.getHeight() == 0) {
            chars[0] = 'S';
        } else if(entity.isTower()) {
            chars[1] = height;
            if(entity.isBlocked()) {
                chars[0] = 'X';
            } else {
                chars[0] = 'T';
            }
        }
        return chars;
    }
    /**
     * Returns the Color of the Token
     * @param entity Tower or Stone
     * @return the color of the Token
     */
    private Color getColor(Entity entity) {
        if(entity != null) {
            if(entity.getColor() == PlayerColor.RED) {
                if(entity.isMaxHeight()) {
                    return Color.PINK;
                } else {
                    return Color.RED;
                }
            } else if(entity.getColor() == PlayerColor.BLUE) {
                if(entity.isMaxHeight()) {
                    return Color.CYAN;
                } else {
                    return Color.BLUE;
                }
            }
        }
        return null;
    }
    /**
     * Creates a Button with the action of surrender
     * @return The surrender Button
     */
    private JButton getSurrenderButton() {
        JButton button = new JButton("Surrender");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "Surrender Button clicked.");
                deliverMove = null;
                wakeUp();
            }
        });
        debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Surrender Button initialized.");
        return button;
    }
    /**
     * Updates the JPanel
     */
    @Override
    public void visualize() {
        if(this.viewer.getStatus() != Status.ILLEGAL) {
            jPanel.repaint();
            debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "visualize: JPanel repainted.");
        }
    }
    /**
     * Waits for Player Input and set the Main-Thread to wait()
     * @return Move
     * @throws Exception Illegal move
     */
    @Override
    synchronized public Move deliver() throws Exception {
        debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "Wait for Playerinput.");
        wait();
        debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "Player has done the move.");
        return deliverMove;
    }
    /**
     * Draws the LetterLine on top
     * @param g the graphics Object
     * @param distance the distance of an Hexagoncenter to the edge
     */
    private void drawLetters(Graphics2D g, int distance) {
        char[] topLetter = new char[1];
        topLetter[0] = 'A';
        int topLetterX = 0;
        int topLetterY = 0;
                /*-- draw the top Chars --*/
        for (int i = 0; i < this.viewer.getSize(); ++i) {
            g.drawChars(topLetter, 0, topLetter.length, (int) (topLetterX + (distance * i) + (0.5 * distance)), topLetterY + (distance / 2));
            ++topLetter[0];
        }
        debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "Letter draw complete.");
    }
    /**
     * fills the Hexagons which have the hovered or clicked token can reach with Green
     * @param g the graphics Object
     */
    private void drawPossibleMoves(Graphics2D g) {
        if (this.possibleMoves != null) {
            for (Move move : this.possibleMoves) {
                int letter = move.getEnd().getLetter();
                int number = move.getEnd().getNumber();

                g.setColor(Color.GREEN);
                g.fillPolygon(polygon[letter][number]);
                g.setColor(Color.BLACK);
                g.drawPolygon(polygon[letter][number]);
            }
            debugInstance.send(DebugLevel.LEVEL_5, DebugSource.IO, "possibleMoves draw complete.");
        }
    }
    /**
     * Draws the Numbers on the left side
     * @param g the graphics Object
     * @param distance the distance of an Hexagoncenter to the edge
     * @param y the row count
     */
    private void drawNumbers(Graphics2D g, int distance, int y) {
        String leftNumber;
        leftNumber = String.valueOf(y);
        g.setColor(Color.BLACK);
        g.drawString(leftNumber, (y - 1) * (distance / 2), hexagonGrid.getCenter(1, y).getY() + (polySize / 4));
    }
    /**
     * Draws the Entity
     * @param g the graphics Object
     * @param entity the entity on x and y
     * @param distance the distance of an Hexagoncenter to the edge
     * @param x the column count
     * @param y the row count
     */
    private void drawEntity(Graphics2D g, Entity entity, int distance, int x, int y) {
        if(entity != null) {
            if(viewer.getTurn() == entity.getColor()) {
                g.setColor((entity.getColor() == PlayerColor.RED ? Color.RED : Color.BLUE));
                g.drawPolygon(markedPolygon[x][y]);
                debugInstance.send(DebugLevel.LEVEL_6, DebugSource.IO, "Hexagon with Token. (" + entity.getColor() + ")");
            }
            g.setColor(getColor(entity));
            int i = hexagonGrid.getCenter(x, y).getX() - (polySize / 2);
            int i1 = hexagonGrid.getCenter(x, y).getY() - (polySize / 2);
            int i2 = polySize - (polySize / 32);
            g.fillOval(i, i1, i2, i2);
            g.setColor(Color.BLACK);
            g.drawOval(i, i1, i2, i2);
            if(entity.getHeight() >= 0) {
                g.setColor(Color.WHITE);
                char[] chars = getChar(entity);
                g.drawChars(chars, 0, chars.length, chars[0] == 'T' || chars[0] == 'X' ? i + (polySize - (distance / 2)) : i + (polySize - (polySize * 2 / 3)), i1 + (polySize - (distance / 5)));
            }
            debugInstance.send(DebugLevel.LEVEL_6, DebugSource.IO, "Entity on (" + x + ", " + y + ") drawn.");
        }
    }
    /**
     * Wakes the dormant Main-Thread
     */
    synchronized private void wakeUp() {
        debugInstance.send(DebugLevel.LEVEL_4, DebugSource.IO, "MainThread wakes up.");
        notify();
    }
}
