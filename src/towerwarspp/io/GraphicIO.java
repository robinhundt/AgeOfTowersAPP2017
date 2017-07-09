package towerwarspp.io;

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
     * Dimension-Object for screenresolution
     */
    private Dimension screenResolution;
    /**
     * JPanel-Object for right infooutput
     */
    private JPanel infoPanel;
    /**
     * Textarea for output of turn, player
     */
    private JTextArea info;

    /**
     * JButton-Object for surrender Button
     */
    private JButton surrenderButton;

    /**
     * Constructor
     */
    public GraphicIO() {
        this.jFrame = new JFrame();
        this.jFrame.setLayout(new BorderLayout());
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
        this.jFrame.setTitle("Age of Towers");
        this.jFrame.setSize(this.screenResolution);
        this.jFrame.addComponentListener(getFrameResize());
        this.surrenderButton = getSurrenderButton();
        this.info = new JTextArea();
        info.setEditable(false);
        info.setLineWrap(true);
    }

    /**
     * Set the textarea of InfoPanel to display e.g turn and player
     * @param string Information which should be displayed
     */
    @Override
    public void display(String string) {
        info.setText(info.getText() + "\n" + string);
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
                jPanel.setPreferredSize(new Dimension(jFrame.getWidth() - 100, jFrame.getHeight()));
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
     * Creates the MouseListener for the JPanel for the clicks on stones and the moves
     * @return MouseListener
     */
    private MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            Position positionStart = null;
            @Override
            public void mouseClicked(MouseEvent e) {
                /*-- left mouse click --*/
                if (e.getButton() == 1) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    for (int y = 1; y <= viewer.getSize(); ++y) {
                        for (int x = 1; x <= viewer.getSize(); ++x) {
                            /*-- the right polygon --*/
                            if (polygon[x][y].contains(mouseX, mouseY)) {
                                try {
                                    positionStart = new Position(x, y);
                                    /*-- not Empty & right player & not endofgame --*/
                                    if (!viewer.isEmpty(positionStart) && viewer.getTurn() == viewer.getPlayerColor(positionStart) && viewer.getStatus() == Status.OK) {
                                        possibleMoves = viewer.possibleMoves(positionStart);
                                        visualize();
                                    } else {
                                        possibleMoves = null;
                                        visualize();
                                    }
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                    System.exit(1);
                                }
                            }
                        }
                    }
                } else if (e.getButton() == 3) { /*-- right mouse click --*/
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    for (int y = 1; y <= viewer.getSize(); ++y) {
                        for (int x = 1; x <= viewer.getSize(); ++x) {
                            /*-- right polygon --*/
                            if (polygon[x][y].contains(mouseX, mouseY)) {
                                deliverMove = new Move(positionStart, new Position(x, y));
                                possibleMoves = null;
                                wakeUp();
                            }
                        }
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
            private char[] height;
            @Override
            protected void paintComponent(Graphics g) {
                int distance = (int)((Math.cos(Math.toRadians(30.0)) * polySize) * 2.0);
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.setFont(new Font("TimesRoman", Font.BOLD, distance / 2));
                char[] topLetter = new char[1];
                topLetter[0] = 'A';
                String leftNumber = "";
                int topLetterX = 0;
                int topLetterY = 0;
                /*-- draw the top Chars --*/
                for(int i = 0; i < viewer.getSize(); ++i) {
                    g.drawChars(topLetter, 0, topLetter.length, (int)(topLetterX + (distance * i) + (0.5 * distance)), topLetterY + (distance / 2));
                    ++topLetter[0];
                }
                g.setFont(new Font("TimesRoman", Font.BOLD, distance / 4));
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

                for(int y = 1; y <= viewer.getSize(); ++y) {
                    /*-- draw the left Numbers --*/
                    g.setFont(new Font("TimesRoman", Font.BOLD, distance / 2));
                    leftNumber = String.valueOf(y);
                    g.setColor(Color.BLACK);
                    g.drawString(leftNumber, (y - 1) * (distance / 2), hexagonGrid.getCenter(1, y).getY() + (polySize / 4));
                    for(int x = 1; x <= viewer.getSize(); ++x) {
                        g.setFont(new Font("TimesRoman", Font.BOLD, distance / 4));

                        /*-- draw Grid --*/
                        g.setColor(Color.BLACK);
                        g.drawPolygon(polygon[x][y]);
                        /*-----*/

                        Position position = new Position(x, y);
                        /*draw entities*/
                        if(!viewer.isEmpty(position)) {
                            /*set color*/
                            if(viewer.getPlayerColor(position) == PlayerColor.RED) {
                                if(viewer.getHeight(position) == (viewer.getSize() / 3)) {
                                    g.setColor(Color.PINK);
                                } else {
                                    g.setColor(Color.RED);
                                }
                            } else if(viewer.getPlayerColor(position) == PlayerColor.BLUE) {
                                if(viewer.getHeight(position) == (viewer.getSize() / 3)) {
                                    g.setColor(Color.CYAN);
                                } else {
                                    g.setColor(Color.BLUE);
                                }
                            }

                            int i = hexagonGrid.getCenter(x, y).getX() - (polySize / 2);
                            int i1 = hexagonGrid.getCenter(x, y).getY() - (polySize / 2);
                            int i2 = polySize - (polySize / 32);
                            int i3 = i2;

                            g.fillOval(i, i1, i2, i3);

                            g.setColor(Color.BLACK);


                            g.drawOval(i, i1, i2, i3);
                            if(!viewer.isEmpty(position) && viewer.getHeight(position) >= 0) {
                                g.setColor(Color.WHITE);
                                int intHeight = viewer.getHeight(position);
                                char zwischen = Character.forDigit(intHeight, 10);
                                if(viewer.isBase(position)) {
                                    height = new char[1];
                                    height[0] = 'B';
                                } else if(viewer.isStone(position)) {
                                    height = new char[1];
                                    height[0] = 'S';
                                } else if(viewer.isTower(position)) {
                                    height = new char[2];
                                    height[1] = zwischen;
                                    if(viewer.isBlocked(position)) {
                                        height[0] = 'X';
                                    } else {
                                        height[0] = 'T';
                                    }
                                }
                                g.drawChars(height, 0, height.length, i + (polySize - (polySize * 2 / 3)), i1 + (polySize * 3 / 4));
                            }
                        }
                    }
                }
            }
        };
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
        jPanel = getJPanel();
        jPanel.addMouseListener(getMouseListener());
        jPanel.setPreferredSize(new Dimension(this.jFrame.getWidth() - 150, this.jFrame.getHeight()));
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(135, 100));
        infoPanel.add(surrenderButton, BorderLayout.NORTH);
        infoPanel.add(info, BorderLayout.CENTER);
        jFrame.add(infoPanel, BorderLayout.EAST);
        jFrame.add(jPanel, BorderLayout.WEST);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    /**
     * Creates a Button with the action of surrender
     * @return The surrender Button
     */
    public JButton getSurrenderButton() {
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
        if(this.viewer.getStatus() == Status.OK) {
            jPanel.setPreferredSize(new Dimension(this.jFrame.getWidth() - 150, this.jFrame.getHeight()));
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
     * @throws Exception
     */
    @Override
    synchronized public Move deliver() throws Exception {
        wait();
        return deliverMove;
    }
}
