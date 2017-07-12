package towerwarspp.io;

import towerwarspp.preset.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
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
    private JTextArea info;

    private JTextArea area;

    private JDialog dialog;

    private boolean repainting;

    private boolean clicked = false;

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
        jPanel.setPreferredSize(new Dimension(this.jFrame.getWidth() - 200, this.jFrame.getHeight()));
        JButton surrenderButton = getSurrenderButton();
        this.info = new JTextArea();
        this.info.setEditable(false);
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(135, 100));
        infoPanel.add(surrenderButton, BorderLayout.NORTH);
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("save");
            }
        });
        infoPanel.add(save, BorderLayout.SOUTH);
        infoPanel.add(this.info, BorderLayout.CENTER);
        this.dialog = new JDialog(this.jFrame, "Result");
        this.area = new JTextArea(200, 200);
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        this.area.setEditable(false);
        this.dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.dialog.add(this.area, BorderLayout.LINE_START);
        this.dialog.add(close, BorderLayout.AFTER_LAST_LINE);
        this.dialog.setPreferredSize(new Dimension(450, 400));
        this.dialog.setResizable(false);
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
                        /*-- not Empty & right player & not endofgame --*/
                        if (!viewer.isEmpty(positionStart) && viewer.getTurn() == viewer.getPlayerColor(positionStart) && viewer.getStatus() == Status.OK) {
                            possibleMoves = viewer.possibleMoves(positionStart);
                            clicked = true;
                            jPanel.repaint();
                        } else {
                            possibleMoves = null;
                            clicked = false;
                            jPanel.repaint();
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
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
                        /*-- not Empty & right player & not endofgame --*/
                        if (!viewer.isEmpty(positionStart) && viewer.getStatus() == Status.OK) {
                            possibleMoves = viewer.possibleMoves(positionStart);
                            jPanel.repaint();
                        } else {
                            possibleMoves = null;
                            jPanel.repaint();
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
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
                repainting = true;
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
                    char[] topLetter = new char[1];
                    topLetter[0] = 'A';
                    String leftNumber;
                    int topLetterX = 0;
                    int topLetterY = 0;
                /*-- draw the top Chars --*/
                    for (int i = 0; i < viewer.getSize(); ++i) {
                        g.drawChars(topLetter, 0, topLetter.length, (int) (topLetterX + (distance * i) + (0.5 * distance)), topLetterY + (distance / 2));
                        ++topLetter[0];
                    }
                    if (possibleMoves != null) {
                        for (Move move : possibleMoves) {
                            int letter = move.getEnd().getLetter();
                            int number = move.getEnd().getNumber();

                            g.setColor(Color.GREEN);
                            g.fillPolygon(polygon[letter][number]);
                            g.setColor(Color.BLACK);
                            g.drawPolygon(polygon[letter][number]);
                        }
                    }
                    for (int y = 1; y <= viewer.getSize(); ++y) {
                    /*-- draw the left Numbers --*/
                        g.setFont(letterFont);
                        leftNumber = String.valueOf(y);
                        g.setColor(Color.BLACK);
                        g.drawString(leftNumber, (y - 1) * (distance / 2), hexagonGrid.getCenter(1, y).getY() + (polySize / 4));
                        for (int x = 1; x <= viewer.getSize(); ++x) {


                            g.setFont(stoneFont);
                        /*-- draw Grid --*/
                            g.setColor(Color.BLACK);
                            g.drawPolygon(polygon[x][y]);
                        /*-----*/
                            Position position = new Position(x, y);
                        /*draw entities*/
                            if (!viewer.isEmpty(position)) {
                            /*set color*/
                                if(viewer.getTurn() == viewer.getPlayerColor(position)) {
                                    g.setColor((viewer.getPlayerColor(position) == PlayerColor.RED) ? Color.RED : Color.BLUE);
                                    g.drawPolygon(markedPolygon[x][y]);
                                }
                                g.setColor(getColor(position));
                                int i = hexagonGrid.getCenter(x, y).getX() - (polySize / 2);
                                int i1 = hexagonGrid.getCenter(x, y).getY() - (polySize / 2);
                                int i2 = polySize - (polySize / 32);
                                int i3 = i2;
                                g.fillOval(i, i1, i2, i3);
                                g.setColor(Color.BLACK);
                                g.drawOval(i, i1, i2, i3);
                                if (!viewer.isEmpty(position) && viewer.getHeight(position) >= 0) {
                                    g.setColor(Color.WHITE);
                                    char[] chars = getChar(position);
                                    g.drawChars(chars, 0, chars.length, i + (polySize - (polySize * 2 / 3)), i1 + (polySize - (distance / 5)));
                                }
                            }
                        }
                    }
                }
                repainting = false;
            }
        };
    }

    /**
     * Returns the Char for the Token-Type
     * @param position Position of the Token
     * @return the type of the Token as Char
     */
    private char[] getChar(Position position) {
        int intHeight = viewer.getHeight(position);
        char height = Character.forDigit(intHeight, 10);
        char[] chars = new char[2];
        if(viewer.isBase(position)) {
            chars[0] = 'B';
        } else if(viewer.isStone(position)) {
            chars[0] = 'S';
        } else if(viewer.isTower(position)) {
            chars[1] = height;
            if(viewer.isBlocked(position)) {
                chars[0] = 'X';
            } else {
                chars[0] = 'T';
            }
        }
        return chars;
    }

    /**
     * Returns the Color of the Token
     * @param position Position of the Token
     * @return the color of the Token
     */
    private Color getColor(Position position) {
        if(!viewer.isEmpty(position)) {
            if(viewer.getPlayerColor(position) == PlayerColor.RED) {
                if(viewer.getHeight(position) == (viewer.getSize() / 3)) {
                    return Color.PINK;
                } else {
                    return Color.RED;
                }
            } else if(viewer.getPlayerColor(position) == PlayerColor.BLUE) {
                if(viewer.getHeight(position) == (viewer.getSize() / 3)) {
                    return Color.CYAN;
                } else {
                    return Color.BLUE;
                }
            }
        }
        return null;
    }

    /**
     * Setter of Viewer and Initialize of JPanels
     * @param viewer Viewer-Object
     */
    @Override
    public void setViewer(Viewer viewer) {
        this.viewer = viewer;
        setPolygonSize();
        this.hexagonGrid = new HexagonGrid(this.viewer.getSize(), polySize);
        this.polygon = this.hexagonGrid.getPolygon();
        this.markedPolygon = this.hexagonGrid.getMarkedPolygon();
        jFrame.add(infoPanel, BorderLayout.EAST);
        jFrame.add(jPanel, BorderLayout.WEST);
        jFrame.pack();
        if(!jFrame.isVisible()) {
            jFrame.setVisible(true);
        }
    }

    /**
     * Dialog
     */
    @Override
    public void dialog(String string) {
        this.area.setText(string);
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
            while (repainting) {
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
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
