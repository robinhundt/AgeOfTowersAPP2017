package towerwarspp.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

/**
 * Class Parameter Input to provide a graphical option to select settings and flags for the game
 *
 * @author Niklas Mueller
 * @version 0.1 July 12th 2017
 */
public class ParameterInput {

    String string[];

    JFrame frame;

    JRadioButton text;
    JRadioButton graphic;
    JRadioButton none;
    JRadioButton redHuman;
    JRadioButton blueHuman;
    JRadioButton redRandom;
    JRadioButton blueRandom;
    JRadioButton redSimple;
    JRadioButton blueSimple;
    JRadioButton redAdvanced1;
    JRadioButton blueAdvanced1;
    JRadioButton redAdvanced2;
    JRadioButton blueAdvanced2;
    JRadioButton redAdvanced3;
    JRadioButton blueAdvanced3;
    JRadioButton redRemote;
    JRadioButton blueRemote;

    JButton done = new JButton("Done");

    ParameterInput() {
        string = new String[13];
        frame = new JFrame("Parameter Input");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);
        panel.setPreferredSize(new Dimension(400, 650));

        JLabel output = new JLabel("Output type:");
        JLabel size = new JLabel("Size:");
        JLabel delayTime = new JLabel("Delaytime:");
        JLabel gamesNo = new JLabel("Number of games:");
        JLabel red = new JLabel("Red's playertype");
        JLabel blue = new JLabel("Blue's playertype");
        text = new JRadioButton("text");
        graphic = new JRadioButton("graphic");
        graphic.setSelected(true);
        none = new JRadioButton("none");
        redHuman = new JRadioButton("human");
        blueHuman = new JRadioButton("human");
        redRandom = new JRadioButton("random");
        blueRandom = new JRadioButton("random");
        redSimple = new JRadioButton("simple");
        blueSimple = new JRadioButton("simple");
        redAdvanced1 = new JRadioButton("adv1");
        blueAdvanced1 = new JRadioButton("adv1");
        redAdvanced2 = new JRadioButton("adv2");
        blueAdvanced2 = new JRadioButton("adv2");
        redAdvanced3 = new JRadioButton("adv3");
        blueAdvanced3 = new JRadioButton("adv3");
        redRemote = new JRadioButton("remote");
        blueRemote = new JRadioButton("remote");
        JCheckBox games = new JCheckBox("Tournament");
        JCheckBox delay = new JCheckBox("Delay");
        JCheckBox debug = new JCheckBox("Debug-Mode");
        JCheckBox statistic = new JCheckBox("Statistic");
        JTextField sizeInput = new JTextField();
        JTextField gamesInput = new JTextField();
        JTextField delayInput = new JTextField();
        panel.add(output);
        panel.add(text);
        panel.add(graphic);
        panel.add(none);
        panel.add(size);
        panel.add(sizeInput);
        panel.add(red);
        panel.add(redHuman);
        panel.add(redRandom);
        panel.add(redSimple);
        panel.add(redAdvanced1);
        panel.add(redAdvanced2);
        panel.add(redAdvanced3);
        panel.add(redRemote);
        panel.add(blue);
        panel.add(blueHuman);
        panel.add(blueRandom);
        panel.add(blueSimple);
        panel.add(blueAdvanced1);
        panel.add(blueAdvanced2);
        panel.add(blueAdvanced3);
        panel.add(blueRemote);
        panel.add(games);
        panel.add(gamesNo);
        panel.add(gamesInput);
        panel.add(delay);
        panel.add(delayTime);
        panel.add(delayInput);
        panel.add(debug);
        panel.add(statistic);
        panel.add(done);

        text.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (text.isSelected()) {
                    graphic.setSelected(false);
                    none.setSelected(false);
                }
            }
        });
        graphic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (graphic.isSelected()) {
                    text.setSelected(false);
                    none.setSelected(false);
                }
            }
        });
        none.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (none.isSelected()) {
                    text.setSelected(false);
                    graphic.setSelected(false);
                }
            }
        });

        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if (text.isSelected()) {
                    string[5] = ("-output text");
                }
                else if (none.isSelected()) {
                    string[5] = ("-output none");
                }

                //if (!offer.isChecked())
                /*RED*/
                if (redHuman.isSelected()) {
                    string[0] = ("-red human");
                }
                else if (redAdvanced1.isSelected()) {
                    string[0] = ("red- adv1");
                }
                else if (redAdvanced2.isSelected()) {
                    string[0] = ("-red adv2");
                }
                else if (redAdvanced3.isSelected()) {
                    string[0] = ("-red adv3");
                }
                else if (redSimple.isSelected()) {
                    string[0] = ("-red simple");
                }
                else if (redRemote.isSelected()) {
                    string[0] = ("-red remote");
                }

                /*BLUE*/
                if (blueHuman.isSelected()) {
                    string[1] = ("-blue human");
                }
                else if (blueAdvanced1.isSelected()) {
                    string[1] = ("blue- adv1");
                }
                else if (blueAdvanced2.isSelected()) {
                    string[1] = ("-blue adv2");
                }
                else if (blueAdvanced3.isSelected()) {
                    string[1] = ("-blue adv3");
                }
                else if (blueSimple.isSelected()) {
                    string[1] = ("-blue simple");
                }
                else if (blueRemote.isSelected()) {
                    string[1] = ("-blue remote");
                }

                /*SIZE*/
                if (!size.getText().equals("")) {
                    string[2] = ("-size " + size.getText());
                }

                /*DEBUG*/
                if (debug.isSelected()) {
                    string[12] = ("--debug");
                }
                /*STATISTIC*/
                if (statistic.isSelected()) {
                    string[11] = ("--statistic");
                }
                /*TOURNAMENT*/
                if (games.isSelected() && !gamesInput.getText().equals("")) {
                    string[6] = (gamesInput.getText());
                }
                /*DELAY*/
                if (delay.isSelected() && !delayInput.getText().equals("")) {
                    string[7] = ("-delay " + delayInput.getText());
                }


                frame.dispose();
                wakeup();
            }
        });

        //TODO
        /*
        same here with playertype, but thats gonna be pretty much code, so maybe allow
        to tick more than one radiobutton and output an error if this happens

        synchronising needed

        parsing to string needs to be implemented, offered game (maybe with checkbox and if box is ticked,
        open new options (-name -port -offer)
         */

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.pack();
    }

    synchronized private void wakeup() {
        notify();
    }

    synchronized public String[] getString() {
        return string;
    }
}
