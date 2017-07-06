package towerwarspp.io;

import towerwarspp.board.BViewer;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;
import towerwarspp.preset.Position;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Class {@link GraphicIO} creates the graphic in- output
 *
 * @version 0.5 July 05th 2017
 * @author Kai Kuhlmann
 */
public class GraphicIO extends JFrame implements IO {

    private BViewer viewer;
    private HexagonGrid hexagonGrid;
    private Polygon[][] polygon;
    private JFrame jFrame;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private int polySize;
    private BufferedImage image;
    private String homeDir;

    public GraphicIO(BViewer viewer) {
        homeDir = System.getProperty("user.dir");
        this.viewer = viewer;
        this.polySize = 20;
        this.hexagonGrid = new HexagonGrid(this.viewer.getSize(), polySize);
        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.polygon = this.hexagonGrid.getPolygon();
        try {
            this.image = ImageIO.read(new File(homeDir + "/assets/transparent.png"));
        } catch (Exception e) {
            System.out.println(e);
        }
        jPanel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, this);
                for(int y = 1; y <= viewer.getSize(); ++y) {
                    for(int x = 1; x <= viewer.getSize(); ++x){
                        g.setColor(Color.BLACK);
                        g.drawPolygon(polygon[x][y]);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                return new Dimension((viewer.getSize() - 1) / 2 * (2 * distance) * viewer.getSize(), 2 * polySize * viewer.getSize());
            }
        };
        jPanel1.setOpaque(false);
    }

    @Override
    public void visualize() {
        jPanel2 = new JPanel() {
            private char[] height;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, this);
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
                                char zweischen = Character.forDigit(intHeight, 10);
                                if(viewer.isBase(new Position(x, y))) {
                                    height = new char[1];
                                    height[0] = 'B';
                                } else if(viewer.isStone(new Position(x, y))) {
                                    height = new char[1];
                                    height[0] = 'S';
                                } else if(viewer.isTower(new Position(x, y))) {
                                    height = new char[2];
                                    height[0] = 'B';
                                    height[1] = zweischen;
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
                return new Dimension((viewer.getSize() - 1) / 2 * (2 * distance) * viewer.getSize(), 2 * polySize * viewer.getSize());
            }
        };
        jPanel2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == 1) {
                    System.out.println("possibleMoves");
                } else if(e.getButton() == 3) {
                    System.out.println("move");
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
        });
        jFrame.add(jPanel1);
        jFrame.add(jPanel2);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    @Override
    public Move deliver() throws Exception {
        return null;
    }
}
