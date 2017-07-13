package towerwarspp.io;

import towerwarspp.board.Entity;
import towerwarspp.preset.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Class {@link GraphicIO} creates the graphic in- output
 *
 * @version 0.6 July 07th 2017
 * @author Kai Kuhlmann
 */
public class GraphicIO extends JFrame implements IO {
    /**
     * String for Home-Directory
     */
    private String home;
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
     * JPanel-Object for right infooutput
     */
    private JPanel infoPanel;
    /**
     * Textarea for output of turn, player
     */
    private JTextField info;

    private JTextArea area;

    private JDialog dialog;

    private boolean clicked;

    private JDialog saveDialog;

    private boolean save;

    private String saveGameName;

    /**
     * Constructor
     */
    public GraphicIO() {
        this.home = System.getProperty("user.dir");
        this.jFrame = new JFrame("Age of Towers");
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.jFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.jFrame.addComponentListener(getFrameResize());
        jPanel = getJPanel();
        jPanel.addMouseListener(getMouseListener());
        jPanel.addMouseMotionListener(getMouseListener());
        jPanel.setPreferredSize(new Dimension(this.jFrame.getWidth() - 136, this.jFrame.getHeight()));
        this.infoPanel = getInfoPanel();
        this.info = getDebugLine();
        this.dialog = getResultDialog();
        this.saveDialog = getSaveDialog();
        this.clicked = false;
        this.save = false;
    }

    /**
     * Creates the InfoPanel with the two Buttons surrender and save
     * Surrender Button sends a null Move for surrendering
     * save button opens a JDialog
     * @return returns the InfoPanel
     */
    private JPanel getInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.setSize(new Dimension(135, 100));
        JButton surrenderButton = getSurrenderButton();
        surrenderButton.setSize(135,100);
        infoPanel.add(surrenderButton);
        JButton save = new JButton("Save and Exit");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showSaveDialog();
            }
        });
        save.setSize(135,100);
        infoPanel.add(save);
        return infoPanel;
    }

    /**
     * Creates a TextField at the buttom of the JFrame
     * and shows actual messages
     * @return returns the DebugLine
     */
    private JTextField getDebugLine() {
        JTextField debugLine = new JTextField();
        debugLine.setPreferredSize(new Dimension(this.jPanel.getWidth(), 25));
        debugLine.setVisible(true);
        return debugLine;
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
        return saveDialog;
    }

    /**
     * Set the saveDialog visible
     */
    private void showSaveDialog() {
        this.saveDialog.setVisible(true);
        this.save = true;
    }

    /**
     * Creates the ResultDialog after a Tournament with a Close Button which closes the Application
     * @return returns ResultDialog
     */
    private JDialog getResultDialog() {
        JDialog resultDialog = new JDialog(this.jFrame, "Result");
        this.area = new JTextArea();
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        this.area.setEditable(false);
        resultDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        resultDialog.add(this.area, BorderLayout.LINE_START);
        resultDialog.add(close, BorderLayout.AFTER_LAST_LINE);
        resultDialog.setResizable(false);
        return resultDialog;
    }

    /**
     * Set the textarea of InfoPanel to display e.g turn and player
     * @param string Information which should be displayed
     */
    @Override
    public void display(String string) {
        info.setText(string);
    }

    /**
     * Sets the Title of the JFrame
     * @param string String who should be in the Title
     */
    public void setTitle(String string) {
        this.jFrame.setTitle("Age of Towers - " + string);
    }

    /**
     * Listener on Resize of Frame
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
            }
        };
    }

    /**
     * Setter of the size of a single Polygon
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
     * Get the coordinates of the clicked Entity
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
                    try {
                        positionStart = new Position(position[0], position[1]);
                        Entity entity = viewer.getEntity(positionStart);
                        /*-- not Empty & right player & not endofgame --*/
                        if (entity != null && viewer.getTurn() == entity.getColor() && viewer.getStatus() == Status.OK) {
                            possibleMoves = viewer.getPossibleMoves(entity);
                            clicked = true;
                            jPanel.repaint();
                        } else {
                            possibleMoves = null;
                            clicked = false;
                            jPanel.repaint();
                        }
                    } catch (Exception ex) {
                        System.out.println("mouseClick" + ex);
                        System.exit(1);
                    }
                } else if (e.getButton() == 3 && position != null) { /*-- right mouse click --*/
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
        }
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
     * @param viewer Viewer-Object
     */
    @Override
    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
        if(this.hexagonGrid == null) {
            setPolygonSize();
            this.hexagonGrid = new HexagonGrid(this.viewer.getSize(), polySize);
            this.polygon = this.hexagonGrid.getPolygon();
            this.markedPolygon = this.hexagonGrid.getMarkedPolygon();
            jFrame.add(infoPanel, BorderLayout.EAST);
            jFrame.add(jPanel, BorderLayout.WEST);
            jFrame.add(info, BorderLayout.SOUTH);
            jFrame.pack();
            if (!jFrame.isVisible()) {
                jFrame.setVisible(true);
            }
        }
    }

    /**
     * Dialog
     */
    @Override
    public void dialog(String string) {
        this.area.setText(string);
        this.dialog.pack();
        this.dialog.setVisible(true);
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
                deliverMove = null;
                wakeUp();
            }
        });
        return button;
    }

    /**
     * Updates the JPanel
     */
    @Override
    public void visualize() {
        if(this.viewer.getStatus() != Status.ILLEGAL) {
            jPanel.repaint();
        }
    }

    /**
     * Wakes the dormant Main-Thread
     */
    synchronized private void wakeUp() {
        notify();
    }

    /**
     * Waits for Player Input and set the Main-Thread to wait()
     * @return Move
     * @throws Exception Illegal move
     */
    @Override
    synchronized public Move deliver() throws Exception {
        wait();
        return deliverMove;
    }
}
