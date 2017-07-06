package towerwarspp.io;

import towerwarspp.board.MoveList;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;
import towerwarspp.preset.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

/**
 * Class {@link GraphicIO} creates the graphic in- output
 *
 * @version 0.5 July 05th 2017
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
    private MoveList possibleMoves;
    /**
     * deliveMove-Object
     */
    private Move deliverMove;

    /**
     * Constructor
     */
    public GraphicIO() {
        this.polySize = 20;
        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                                    possibleMoves = viewer.getPossibleMoves(positionStart);
                                } catch (Exception ex) {
                                    possibleMoves = null;
                                }
                            }
                        }
                    }
                    visualize();
                } else if(e.getButton() == 3) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    for(int y = 1; y <= viewer.getSize(); ++y) {
                        for(int x = 1; x <= viewer.getSize(); ++x) {
                            if(polygon[x][y].contains(mouseX, mouseY)) {
                                deliverMove = new Move(positionStart, new Position(x, y));
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
                for(int y = 1; y <= viewer.getSize(); ++y) {
                    for(int x = 1; x <= viewer.getSize(); ++x){
                        if(possibleMoves != null && !possibleMoves.isEmpty()) {
                            Iterator<Move> moveListIterator = null;
                            try {
                                moveListIterator = possibleMoves.iterator();
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            Move move = null;
                            Position endPosition = null;
                            while(moveListIterator != null && moveListIterator.hasNext()) {
                                move = moveListIterator.next();
                                if(move.getStart().equals(new Position(x, y))) {
                                    endPosition = move.getEnd();
                                }
                                if(endPosition != null) {
                                    if(viewer.getTurn() == 1 ? viewer.getPlayerColor(new Position(x, y)) == PlayerColor.RED : viewer.getPlayerColor(new Position(x, y)) == PlayerColor.BLUE) {
                                        g.setColor(Color.GREEN);
                                        g.fillPolygon(polygon[endPosition.getLetter()][endPosition.getNumber()]);
                                        g.setColor(Color.BLACK);
                                        g.drawPolygon(polygon[endPosition.getLetter()][endPosition.getNumber()]);
                                    }
                                }
                            }
                        }
                        g.setColor(Color.BLACK);
                        g.drawPolygon(polygon[x][y]);
                    }
                }
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                for(int y = 1; y <= viewer.getSize(); ++y) {
                    for(int x = 1; x <= viewer.getSize(); ++x) {
                        if(!viewer.isEmpty(new Position(x, y))) {
                            if(viewer.getPlayerColor(new Position(x, y)) == PlayerColor.RED) {
                                g.setColor(Color.RED);
                            } else {
                                g.setColor(Color.BLUE);
                            }
                            g.fillOval((y * (2 * distance) + (x - 1) * distance) - (polySize / 2), (x * polySize + (x - 1) * (polySize / 2)) - (polySize / 2), polySize - (polySize / 32), polySize - (polySize / 32));
                            g.setColor(Color.BLACK);
                            g.drawOval((y * (2 * distance) + (x - 1) * distance) - (polySize / 2), (x * polySize + (x - 1) * (polySize / 2)) - (polySize / 2), polySize - (polySize / 32), polySize - (polySize / 32));
                            if(viewer.getHeight(new Position(x, y)) >= 0) {
                                g.setColor(Color.WHITE);
                                int intHeight = viewer.getHeight(new Position(x, y));
                                char zwischen = Character.forDigit(intHeight, 10);
                                if(viewer.isBase(new Position(x, y))) {
                                    height = new char[1];
                                    height[0] = 'B';
                                } else if(viewer.isStone(new Position(x, y))) {
                                    height = new char[1];
                                    height[0] = 'S';
                                } else if(viewer.isTower(new Position(x, y))) {
                                    height = new char[2];
                                    height[0] = 'T';
                                    height[1] = zwischen;
                                }
                                g.drawChars(height, 0, height.length, y * (2 * distance) + (x - 1) * distance, x * polySize + (x - 1) * (polySize / 2));
                            }
                        }
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                return new Dimension((viewer.getSize()) / 2 * (2 * distance) * viewer.getSize(), 2 * polySize * viewer.getSize());
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
        jPanel.updateUI();
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
        visualize();
        return deliverMove;
    }
}
