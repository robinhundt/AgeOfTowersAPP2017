package towerwarspp.io;

import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
     * Constructor
     */
    public GraphicIO() {
        this.jFrame = new JFrame();
        this.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
        this.jFrame.setSize(this.screenResolution);
        this.jFrame.addComponentListener(getFrameResize());
    }

    private ComponentListener getFrameResize() {
        return new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
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

    private void setPolygonSize() {
        double frameWidth = this.jFrame.getWidth();
        double frameHeight = this.jFrame.getHeight();
        int maxWidthPolygon = 0;
        int maxWidth = 0;
        if(frameHeight < frameWidth) {
            maxWidth = (int)frameWidth / this.viewer.getSize();
            maxWidthPolygon = maxWidth - (maxWidth / 16 * 11);
        }
        this.polySize = maxWidthPolygon;
        System.out.println("poly: " + this.polySize + " maxW: " + maxWidth + " maxWP: " + maxWidthPolygon);
        System.out.println("w: " + frameWidth + " h: " + frameHeight);
    }

    private int getPolygonSize() {
        return this.hexagonGrid.getPolygonSize();
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
                    System.out.println("mouse x: " + mouseX + " mouse y: " + mouseY);
                    for(int y = 1; y <= viewer.getSize(); ++y) {
                        for(int x = 1; x <= viewer.getSize(); ++x) {
                            if(polygon[x][y].contains(mouseX, mouseY)) {
                                System.out.println("x: " + x + " y: " + y);
                                try {
                                    positionStart = new Position(x, y);

                                    System.out.println("pos: " + positionStart);
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
                                System.out.println("Move: " + deliverMove);
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
                super.paintComponent(g);
                if (possibleMoves != null) {
                    for (Move move : possibleMoves) {
                        System.out.println(move + " ");
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
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                for(int y = 1; y <= viewer.getSize(); ++y) {
                    for(int x = 1; x <= viewer.getSize(); ++x) {

                        /*-- draw Grid --*/
                        g.setColor(Color.BLACK);
                        g.drawPolygon(polygon[x][y]);
                        /*-----*/

                        Position position = new Position(x, y);
                        /*draw entities*/
                        if(!viewer.isEmpty(position)) {
                            /*set color*/
                            if(viewer.getPlayerColor(position) == PlayerColor.RED) {
                                g.setColor(Color.RED);
                            } else {
                                g.setColor(Color.BLUE);
                            }

                            //g.setColor(Color.BLACK);
                            //g.drawPolygon(polygon[x][y]);
                            int i = (x * (2 * distance) + (y - 1) * distance) - (polySize / 2);
                            int i1 = (y * polySize + (y - 1) * (polySize / 2)) - (polySize / 2);
                            int i2 = polySize - (polySize / 32);
                            int i3 = i2;

                            g.fillOval(i, i1, i2, i3);
                            //g.fillOval(hexagonGrid.getHexagonCenterX(position), hexagonGrid.getHexagonCenterY(position), i2, i3);
                            g.setColor(Color.BLACK);
                            //g.drawOval(hexagonGrid.getHexagonCenterX(position), hexagonGrid.getHexagonCenterY(position), i2, i3);

                            g.drawOval(i, i1, i2, i3);
                            if(viewer.getHeight(position) >= 0) {
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
                                    height[0] = 'T';
                                    height[1] = zwischen;
                                }
                                g.drawChars(height, 0, height.length, i+5, y * polySize + (y - 1) * (polySize / 2) + 5);
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
        jFrame.add(jPanel);
        jFrame.pack();
        jFrame.setVisible(true);
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
