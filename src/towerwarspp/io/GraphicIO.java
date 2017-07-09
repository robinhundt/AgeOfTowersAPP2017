package towerwarspp.io;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.im.InputMethodRequests;
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

    private JPanel infoPanel;

    /**
     * JButton-Object for surrender Button
     */
    private JButton surrenderButton;

    /**
     * Constructor
     */
    public GraphicIO() {
        this.jFrame = new JFrame();
        this.jFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
        this.jFrame.setTitle("Age of Towers");
        this.jFrame.setSize(this.screenResolution);
        this.jFrame.addComponentListener(getFrameResize());
        this.surrenderButton = getSurrenderButton();
    }

    private ComponentListener getFrameResize() {
        return new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                setPolygonSize();
                hexagonGrid.updatePolygonSize(polySize);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        };
    }

    /**
     * Setter of the size of a single Polygon
     */
    private void setPolygonSize() {
        double frameWidth = this.jFrame.getWidth() - 50;
        double frameHeight = this.jFrame.getHeight();
        double gridX = (frameWidth / (viewer.getSize() + ((1.0 / 2.0) * (viewer.getSize() - 1)))) / 2.0;
        double gridY = frameHeight / (viewer.getSize() * 2.0);
        gridX = gridX * Math.cos(Math.toRadians(30.0));
        this.polySize = gridX < gridY ? (int)gridX : (int)gridY;
    }

    /**
     * Creates the MouseListener for the JPanel
     * @return MouseListener
     */
    private MouseListener getMouseListener() {
        return new MouseListener() {
            Position positionStart = null;
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == 1) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    for(int y = 1; y <= viewer.getSize(); ++y) {
                        for(int x = 1; x <= viewer.getSize(); ++x) {
                            if(polygon[x][y].contains(mouseX, mouseY)) {
                                try {
                                    positionStart = new Position(x, y);
                                    if (!viewer.isEmpty(positionStart) && viewer.getTurn() == viewer.getPlayerColor(positionStart)) {
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
                } else if(e.getButton() == 3) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    for(int y = 1; y <= viewer.getSize(); ++y) {
                        for(int x = 1; x <= viewer.getSize(); ++x) {
                            if(polygon[x][y].contains(mouseX, mouseY)) {
                                deliverMove = new Move(positionStart, new Position(x, y));
                                possibleMoves = null;
                                wakeUp();
                            }
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    /**
     * Creates the JPanel
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
                String leftNumber = "1";
                int topLetterX = 0;
                int topLetterY = 0;
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

//                for(int y = 1; y <= viewer.getSize(); ++y) {
//                    for(int x = 1; x <= viewer.getSize(); ++x){
//                        if(possibleMoves != null && !possibleMoves.isEmpty()) {
//                            Iterator<Move> moveListIterator = possibleMoves.iterator();
//
//                            Move move = null;
//                            Position endPosition = null;
//                            while(moveListIterator.hasNext()) {
//                                move = moveListIterator.next();
//                                //if(move.getStart().equals(new Position(x, y))) {
//                                    endPosition = move.getEnd();
//                                //}
//                                if(endPosition != null) {
//                                    if(viewer.getTurn() == 1 && !viewer.isEmpty(new Position(x, y)) ?
//                                            viewer.getPlayerColor(new Position(x, y)) == PlayerColor.RED :
//                                            viewer.getPlayerColor(new Position(x, y)) == PlayerColor.BLUE) {
//                                        g.setColor(Color.GREEN);
//                                        g.fillPolygon(polygon[endPosition.getLetter()][endPosition.getNumber()]);
//                                        g.setColor(Color.BLACK);
//                                        g.drawPolygon(polygon[endPosition.getLetter()][endPosition.getNumber()]);
//                                    }
//                                }
//                            }
//                        }
//                        g.setColor(Color.BLACK);
//                        g.drawPolygon(polygon[x][y]);
//                    }
//                }
                for(int y = 1; y <= viewer.getSize(); ++y) {
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

                            //g.setColor(Color.BLACK);
                            //g.drawPolygon(polygon[x][y]);
                            //int i = (x * (2 * distance) + (y - 1) * distance) - (polySize / 2);
                            //int i1 = (y * polySize + (y - 1) * (polySize / 2)) - (polySize / 2);
                            int i = hexagonGrid.getCenter(x, y).getX() - (polySize / 2);
                            int i1 = hexagonGrid.getCenter(x, y).getY() - (polySize / 2);
                            int i2 = polySize - (polySize / 32);
                            int i3 = i2;

                            g.fillOval(i, i1, i2, i3);
                            //g.fillOval(hexagonGrid.getHexagonCenterX(position), hexagonGrid.getHexagonCenterY(position), i2, i3);
                            g.setColor(Color.BLACK);
                            //g.drawOval(hexagonGrid.getHexagonCenterX(position), hexagonGrid.getHexagonCenterY(position), i2, i3);

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

            @Override
            public Dimension getPreferredSize() {
                return screenResolution;
            }
        };
    }

    /**
     * Setter of Viewer and Initialize of JPanel
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
        jFrame.add(jPanel, new FlowLayout(FlowLayout.LEFT));
        infoPanel = getInfoPanel();
        infoPanel.add(surrenderButton);
        jFrame.add(infoPanel, new FlowLayout(FlowLayout.RIGHT));
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public JPanel getInfoPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                graphics.drawString("test", 1, 1);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(50, 100);
            }
        };
    }

    public JButton getSurrenderButton() {
        JButton button = new JButton("Surrender");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("surrender");
            }
        });
        return button;
    }

    /**
     * Updates the JPanel
     */
    @Override
    public void visualize() {
        jPanel.repaint();
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
