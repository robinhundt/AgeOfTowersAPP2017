package towerwarspp.io;

import towerwarspp.board.BViewer;
import towerwarspp.preset.Move;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
        try {
            this.image = ImageIO.read(new File(homeDir + "/assets/transparent.png"));
        } catch (Exception e) {
            System.out.println(e);
        }
        this.viewer = viewer;
        this.polySize = 20;
        this.hexagonGrid = new HexagonGrid(getBoardSize(), polySize);
        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.polygon = this.hexagonGrid.getPolygon();
        jPanel1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, this);
                for(int y = 1; y <= getBoardSize(); ++y) {
                    for(int x = 1; x <= getBoardSize(); ++x){
                        g.setColor(Color.BLACK);
                        g.drawPolygon(polygon[x][y]);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                return new Dimension((getBoardSize() - 1) / 2 * (2 * distance) * getBoardSize(), 2 * polySize * getBoardSize());
            }
        };
        jPanel1.setOpaque(false);
    }

    /**
     * Get the size of the Board
     * @return Size of Board
     */
    private int getBoardSize() {
        //return viewer.getSize();
        return 10;
    }

    @Override
    public void visualize() {
        jPanel2 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                System.out.println("test");
                super.paintComponent(g);
                g.drawImage(image, 0, 0, this);
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                for(int y = 1; y <= getBoardSize(); ++y) {
                    for(int x = 1; x <= getBoardSize(); ++x) {
                        g.setColor(Color.BLUE);
                        g.fillOval((y * (2 * distance) + (x - 1) * distance) - (polySize / 2), (x * polySize + (x - 1) * (polySize / 2)) - (polySize / 2), polySize - (polySize / 32), polySize - (polySize / 32));
                        g.setColor(Color.BLACK);
                        g.drawOval((y * (2 * distance) + (x - 1) * distance) - (polySize / 2), (x * polySize + (x - 1) * (polySize / 2)) - (polySize / 2), polySize - (polySize / 32), polySize - (polySize / 32));
                        g.setColor(Color.WHITE);
                        char[] height = {'1'};
                        g.drawChars(height, 0, height.length, y * (2 * distance) + (x - 1) * distance, x * polySize + (x - 1) * (polySize / 2));
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                int distance = (int) (Math.cos(Math.toRadians(30.0)) * polySize);
                return new Dimension((getBoardSize() - 1) / 2 * (2 * distance) * getBoardSize(), 2 * polySize * getBoardSize());
            }
        };
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
