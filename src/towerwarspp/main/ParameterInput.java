package towerwarspp.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    JLabel thinkTime;
    JTextField redThinkTime;
    JTextField blueThinkTime;
    ButtonGroup redPlayer;
    ButtonGroup bluePlayer;
    ButtonGroup outputGroup;

    ArrayList<JRadioButton> redRadioButtons;
    ArrayList<JRadioButton> blueRadioButtons;

    JButton done = new JButton("Done");

    ParameterInput() {
        frame = new JFrame("Parameter Input");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);
        panel.setPreferredSize(new Dimension(400, 650));

        thinkTime = new JLabel("Thinking time:");
        thinkTime.setVisible(false);
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
        outputGroup = new ButtonGroup();
        outputGroup.add(graphic);
        outputGroup.add(text);
        outputGroup.add(none);

        redRadioButtons = new ArrayList<>();
        blueRadioButtons = new ArrayList<>();

        redPlayer = new ButtonGroup();
        bluePlayer = new ButtonGroup();
        redRadioButtons.add(redHuman = new JRadioButton("human"));
        blueRadioButtons.add(blueHuman = new JRadioButton("human"));
        redRadioButtons.add(redRandom = new JRadioButton("random"));
        blueRadioButtons.add(blueRandom = new JRadioButton("random"));
        redRadioButtons.add(redSimple = new JRadioButton("simple"));
        blueRadioButtons.add(blueSimple = new JRadioButton("simple"));
        redRadioButtons.add(redAdvanced1 = new JRadioButton("adv1"));
        blueRadioButtons.add(blueAdvanced1 = new JRadioButton("adv1"));
        redRadioButtons.add(redAdvanced2 = new JRadioButton("adv2"));
        blueRadioButtons.add(blueAdvanced2 = new JRadioButton("adv2"));
        redRadioButtons.add(redAdvanced3 = new JRadioButton("adv3"));
        blueRadioButtons.add(blueAdvanced3 = new JRadioButton("adv3"));
        redRadioButtons.add(redRemote = new JRadioButton("remote"));
        blueRadioButtons.add(blueRemote = new JRadioButton("remote"));
        JCheckBox games = new JCheckBox("Tournament");
        JCheckBox delay = new JCheckBox("Delay");
        JCheckBox debug = new JCheckBox("Debug-Mode");
        JCheckBox statistic = new JCheckBox("Statistic");
        JTextField sizeInput = new JTextField();
        JTextField gamesInput = new JTextField();
        JTextField delayInput = new JTextField();
        redThinkTime = new JTextField();
        redThinkTime.setVisible(false);
        blueThinkTime = new JTextField();
        blueThinkTime.setVisible(false);
        panel.add(output);
        panel.add(text);
        panel.add(graphic);
        panel.add(none);
        panel.add(size);
        panel.add(sizeInput);
        panel.add(red);
        for(JRadioButton radioButton : redRadioButtons) {
            redPlayer.add(radioButton);
            panel.add(radioButton);
            if(radioButton.getActionCommand().equals("adv2")) {
                panel.add(thinkTime);
                panel.add(redThinkTime);
            }
        }
        panel.add(blue);
        for(JRadioButton radioButton : blueRadioButtons) {
            bluePlayer.add(radioButton);
            panel.add(radioButton);
            if(radioButton.getActionCommand().equals("adv2")) {
                panel.add(thinkTime);
                panel.add(blueThinkTime);
            }
        }
        gamesNo.setVisible(false);
        gamesInput.setVisible(false);
        panel.add(games);
        panel.add(gamesNo);
        panel.add(gamesInput);
        delayTime.setVisible(false);
        delayInput.setVisible(false);
        panel.add(delay);
        panel.add(delayTime);
        panel.add(delayInput);
        panel.add(debug);
        panel.add(statistic);
        panel.add(done);
        setBluePlayerActionListener();
        setRedPlayerActionListener();

        games.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(games.isSelected()) {
                    gamesNo.setVisible(true);
                    gamesInput.setVisible(true);
                } else {
                    gamesNo.setVisible(false);
                    gamesInput.setVisible(false);
                }
                frame.repaint();
            }
        });

        delay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(delay.isSelected()) {
                    delayTime.setVisible(true);
                    delayInput.setVisible(true);
                } else  {
                    delayTime.setVisible(false);
                    delayInput.setVisible(false);
                }
                frame.repaint();
            }
        });

        debug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(debug.isSelected()) {

                }
            }
        });

        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                StringBuilder args = new StringBuilder();
                StringBuilder error = new StringBuilder();

                if(none.isSelected()) {
                    args.append("-output none").append(" ");
                } else if(text.isSelected()) {
                    args.append("-output text").append(" ");
                } else if(graphic.isSelected()) {
                    args.append("-output graphic").append(" ");
                } else {
                    error.append("No Output selected.").append(" ");
                }

                if(!sizeInput.getText().equals("")) {
                    args.append("-size ").append(sizeInput.getText()).append(" ");
                } else {
                    error.append("No Size is defined.").append(" ");
                }

                if(redHuman.isSelected()) {
                    args.append("-red human").append(" ");
                } else if(redRandom.isSelected()) {
                    args.append("-red random").append(" ");
                } else if(redSimple.isSelected()) {
                    args.append("-red simple").append(" ");
                } else if (redAdvanced1.isSelected()) {
                    args.append("-red adv1").append(" ");
                } else if (redAdvanced2.isSelected()) {
                    args.append("-red adv2").append(" ");
                } else if (redAdvanced3.isSelected()) {
                    args.append("-red adv3").append(" ");
                } else if(redRemote.isSelected()) {
                    args.append("-red remote").append(" ");
                }

                if(blueHuman.isSelected()) {
                    args.append("-blue human").append(" ");
                } else if(blueRandom.isSelected()) {
                    args.append("-blue random").append(" ");
                } else if(blueSimple.isSelected()) {
                    args.append("-blue simple").append(" ");
                } else if (blueAdvanced1.isSelected()) {
                    args.append("-blue adv1").append(" ");
                } else if (blueAdvanced2.isSelected()) {
                    args.append("-blue adv2").append(" ");
                } else if (blueAdvanced3.isSelected()) {
                    args.append("-blue adv3").append(" ");
                } else if(redRemote.isSelected()) {
                    args.append("-blue remote").append(" ");
                }

                if((blueAdvanced2.isSelected() || redAdvanced2.isSelected()) && (!blueThinkTime.getText().equals("") || !redThinkTime.getText().equals(""))) {
                    args.append("-thinktime ").append(blueThinkTime.getText()).append(" ");
                }



                System.out.println(args);
                String argsString = args.toString();
                string = argsString.split(" ");
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

    private void setRedPlayerActionListener() {
        for(JRadioButton radioButton : redRadioButtons) {
            if(radioButton.getActionCommand().equals("adv2")) {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(!redThinkTime.isVisible()) {
                            redThinkTime.setVisible(true);
                            thinkTime.setVisible(true);
                            frame.repaint();
                        }
                    }
                });
            } else {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(redThinkTime.isVisible()) {
                            redThinkTime.setVisible(false);
                            thinkTime.setVisible(false);
                            frame.repaint();
                        }
                    }
                });
            }
        }
    }
    private void setBluePlayerActionListener() {
        for(JRadioButton radioButton : blueRadioButtons) {
            if(radioButton.getActionCommand().equals("adv2")) {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(!blueThinkTime.isVisible()) {
                            blueThinkTime.setVisible(true);
                            thinkTime.setVisible(true);
                            frame.repaint();
                        }
                    }
                });
            } else {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(blueThinkTime.isVisible()) {
                            blueThinkTime.setVisible(false);
                            thinkTime.setVisible(false);
                            frame.repaint();
                        }
                    }
                });
            }
        }
    }

    synchronized private void wakeup() {
        notify();
    }

    synchronized public String[] getString() throws Exception {
        wait();
        for(int i = 0; i < string.length; ++i) {
            System.out.print(string[i]);
        }
        return string;
    }
}
