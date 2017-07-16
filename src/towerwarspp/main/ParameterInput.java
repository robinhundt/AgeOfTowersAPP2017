package towerwarspp.main;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
/**
 * Class Parameter Input to provide a graphical option to select settings and flags for the game
 *
 * @author Niklas Mueller, Kai Kuhlmann
 * @version 0.3 July 16th 2017
 */
public class ParameterInput {
    private String string[];
    private JFrame frame;
    private JRadioButton text;
    private JRadioButton graphic;
    private JRadioButton none;
    private JRadioButton redHuman;
    private JRadioButton blueHuman;
    private JRadioButton redRandom;
    private JRadioButton blueRandom;
    private JRadioButton redSimple;
    private JRadioButton blueSimple;
    private JRadioButton redAdvanced1;
    private JRadioButton blueAdvanced1;
    private JRadioButton redAdvanced2;
    private JRadioButton blueAdvanced2;
    private JRadioButton redAdvanced3;
    private JRadioButton blueAdvanced3;
    private JRadioButton redRemote;
    private JRadioButton blueRemote;
    private JLabel redThinkTimeLabel;
    private JLabel blueThinkTimeLabel;
    private JLabel redHostLabel;
    private JLabel redPortLabel;
    private JLabel blueHostLabel;
    private JLabel bluePortLabel;
    private JTextField redThinkTime;
    private JTextField blueThinkTime;
    private JTextField redHostInput;
    private JTextField redPortInput;
    private JTextField blueHostInput;
    private JTextField bluePortInput;
    private ArrayList<JRadioButton> redRadioButtons;
    private ArrayList<JRadioButton> blueRadioButtons;
    private JButton done = new JButton("Done");
    /**
     * Consturctor of this Class which initialize all Elements which should be displayed.
     *
     * There were also added some ActionListener.
    */
    ParameterInput() {
        String[] debugOutput = {"", "IO", "Board", "Main", "Network", "Player"};
        String[] debugLevel = {"", "1", "2", "3", "4", "5", "6", "7"};
        JComboBox<String> outputBox = new JComboBox<>(debugOutput);
        JComboBox<String> levelBox = new JComboBox<>(debugLevel);
        frame = new JFrame("Parameter Input");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);
        panel.setPreferredSize(new Dimension(400, 870));
        redThinkTimeLabel = new JLabel("Thinking time:");
        redThinkTimeLabel.setVisible(false);
        blueThinkTimeLabel = new JLabel("Thinking time:");
        blueThinkTimeLabel.setVisible(false);
        JLabel output = new JLabel("Output type:");
        JLabel size = new JLabel("Size:");
        JLabel delayTime = new JLabel("Delaytime:");
        JLabel gamesNo = new JLabel("Number of games:");
        JLabel red = new JLabel("Red's playertype");
        redHostLabel = new JLabel("Host:");
        redPortLabel = new JLabel("Port:");
        blueHostLabel = new JLabel("Host:");
        bluePortLabel = new JLabel("Port:");
        JLabel blue = new JLabel("Blue's playertype");
        JLabel debugOutputLevelLabel = new JLabel("Debug Level:");
        JLabel debugOutputLabel = new JLabel("Debug Output:");
        text = new JRadioButton("text");
        graphic = new JRadioButton("graphic");
        graphic.setSelected(true);
        none = new JRadioButton("none");
        ButtonGroup outputGroup = new ButtonGroup();
        outputGroup.add(graphic);
        outputGroup.add(text);
        outputGroup.add(none);
        redRadioButtons = new ArrayList<>();
        blueRadioButtons = new ArrayList<>();
        ButtonGroup redPlayer = new ButtonGroup();
        ButtonGroup bluePlayer = new ButtonGroup();
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
        redHostInput = new JTextField();
        redPortInput = new JTextField();
        blueHostInput = new JTextField();
        bluePortInput = new JTextField();
        redHostInput.setVisible(false);
        redHostLabel.setVisible(false);
        redPortInput.setVisible(false);
        redPortLabel.setVisible(false);
        blueHostInput.setVisible(false);
        blueHostLabel.setVisible(false);
        bluePortInput.setVisible(false);
        bluePortLabel.setVisible(false);
        redThinkTime = new JTextField();
        redThinkTime.setVisible(false);
        blueThinkTime = new JTextField();
        blueThinkTime.setVisible(false);
        debugOutputLabel.setVisible(false);
        outputBox.setVisible(false);
        debugOutputLevelLabel.setVisible(false);
        levelBox.setVisible(false);
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
                panel.add(redThinkTimeLabel);
                panel.add(redThinkTime);
            } else if(radioButton.getActionCommand().equals("remote")) {
                panel.add(redHostLabel);
                panel.add(redHostInput);
                panel.add(redPortLabel);
                panel.add(redPortInput);
            }
        }
        panel.add(blue);
        for(JRadioButton radioButton : blueRadioButtons) {
            bluePlayer.add(radioButton);
            panel.add(radioButton);
            if(radioButton.getActionCommand().equals("adv2")) {
                panel.add(blueThinkTimeLabel);
                panel.add(blueThinkTime);
            } else if(radioButton.getActionCommand().equals("remote")) {
                panel.add(blueHostLabel);
                panel.add(blueHostInput);
                panel.add(bluePortLabel);
                panel.add(bluePortInput);
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
        panel.add(debugOutputLabel);
        panel.add(outputBox);
        panel.add(debugOutputLevelLabel);
        panel.add(levelBox);
        panel.add(statistic);
        panel.add(done);
        setBluePlayerActionListener();
        setRedPlayerActionListener();
        debug.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(debug.isSelected()) {
                    debugOutputLabel.setVisible(true);
                    outputBox.setVisible(true);
                    debugOutputLevelLabel.setVisible(true);
                    levelBox.setVisible(true);
                } else {
                    debugOutputLabel.setVisible(false);
                    outputBox.setVisible(false);
                    debugOutputLevelLabel.setVisible(false);
                    levelBox.setVisible(false);
                }
                frame.repaint();
                frame.pack();
            }
        });
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
                frame.pack();
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
                frame.pack();
            }
        });
        /* Building the String which are the Arguments for the ArgumentParser. */
        done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuilder args = new StringBuilder();
                StringBuilder error = new StringBuilder();
                // Output
                if(none.isSelected()) {
                    args.append("-output none").append(" ");
                } else if(text.isSelected()) {
                    args.append("-output text").append(" ");
                } else if(graphic.isSelected()) {
                    args.append("-output graphic").append(" ");
                } else {
                    error.append("No Output selected.").append(";");
                }
                // Size
                if(!sizeInput.getText().equals("")) {
                    args.append("-size ").append(sizeInput.getText()).append(" ");
                } else {
                    error.append("No Size is defined.").append(";");
                }
                // RedPlayer
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
                    if(!redHostInput.getText().equals("")) {
                        args.append("-host ").append(redHostInput.getText()).append(" ");
                    }
                    if(!redPortInput.getText().equals("")) {
                        args.append("-port ").append(redPortInput.getText()).append(" ");
                    }
                } else {
                    error.append("no RedPlayer selected.").append(";");
                }
                // BluePlayer
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
                } else if(blueRemote.isSelected()) {
                    args.append("-blue remote").append(" ");
                    if(!blueHostInput.getText().equals("")) {
                        args.append("-host ").append(blueHostInput.getText()).append(" ");
                    }
                    if(!bluePortInput.getText().equals("")) {
                        args.append("-port ").append(bluePortInput.getText()).append(" ");
                    }
                } else {
                    error.append("no BluePlayer selected.").append(";");
                }
                // Thinking Time for Adv2
                if((blueAdvanced2.isSelected() || redAdvanced2.isSelected()) && (!blueThinkTime.getText().equals("") || !redThinkTime.getText().equals(""))) {
                    args.append("-thinktime ").append(blueThinkTime.getText()).append(" ");
                }
                // Tournament
                if(games.isSelected()) {
                    if(!gamesInput.getText().equals("")) {
                        args.append("-games ").append(gamesInput.getText()).append(" ");
                    } else {
                        error.append("No number for Games inserted.").append(";");
                    }
                }
                // Delay
                if(delay.isSelected()) {
                    if(!delayInput.getText().equals("")) {
                        args.append("-delay ").append(delayInput.getText()).append(" ");
                    } else {
                        error.append("No Delaytime definied.").append(";");
                    }
                }
                // Debug
                if(debug.isSelected()) {
                    args.append("--debug").append(" ");
                    if(!debugOutput[outputBox.getSelectedIndex()].equals("")) {
                        args.append("-dsource ").append(debugOutput[outputBox.getSelectedIndex()].toLowerCase()).append(" ");
                    }
                    args.append("-dlevel ").append(debugLevel[levelBox.getSelectedIndex()]).append(" ");
                }
                // Statistic
                if(statistic.isSelected()) {
                    args.append("--statistic").append(" ");
                }
                if(!error.toString().equals("")) {
                    // Error Dialog
                    JDialog errorDialog = new JDialog(frame,"Result");
                    JTextArea errorArea = new JTextArea();
                    JButton close = new JButton("Close");
                    close.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            errorDialog.dispose();
                        }
                    });
                    errorArea.setEditable(false);
                    errorArea.setText(error.toString().replaceAll(";", "\n"));
                    errorDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    errorDialog.add(errorArea, BorderLayout.LINE_START);
                    errorDialog.add(close, BorderLayout.AFTER_LAST_LINE);
                    errorDialog.setSize(new Dimension(100, 100));
                    errorDialog.setVisible(true);
                    errorDialog.pack();
                    done.setEnabled(false);
                } else {
                    String argsString = args.toString();
                    string = argsString.split(" ");
                    frame.dispose();
                    wakeup();
                }
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.pack();
    }
    /**
     * Add the ActionListener to the redPlayer RadioButtons.
     */
    private void setRedPlayerActionListener() {
        for(JRadioButton radioButton : redRadioButtons) {
            if(radioButton.getActionCommand().equals("adv2")) {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(!redThinkTime.isVisible()) {
                            redThinkTime.setVisible(true);
                            redThinkTimeLabel.setVisible(true);
                            frame.repaint();
                            frame.pack();
                        }
                    }
                });
            } else if(radioButton.getActionCommand().equals("remote")) {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(!redHostInput.isVisible()) {
                            redHostInput.setVisible(true);
                            redHostLabel.setVisible(true);
                            redPortInput.setVisible(true);
                            redPortLabel.setVisible(true);
                            frame.repaint();
                            frame.pack();
                        }
                    }
                });
            } else {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(redThinkTime.isVisible()) {
                            redThinkTime.setVisible(false);
                            redThinkTimeLabel.setVisible(false);
                            redHostInput.setVisible(false);
                            redHostLabel.setVisible(false);
                            redPortInput.setVisible(false);
                            redPortLabel.setVisible(false);
                            frame.repaint();
                            frame.pack();
                        }
                    }
                });
            }
        }
    }
    /**
     * Add the ActionListener to the bluePlayer RadioButtons.
     */
    private void setBluePlayerActionListener() {
        for(JRadioButton radioButton : blueRadioButtons) {
            if(radioButton.getActionCommand().equals("adv2")) {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(!blueThinkTime.isVisible()) {
                            blueThinkTime.setVisible(true);
                            blueThinkTimeLabel.setVisible(true);
                            frame.repaint();
                            frame.pack();
                        }
                    }
                });
            } else if(radioButton.getActionCommand().equals("remote")) {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(!blueHostInput.isVisible()) {
                            blueHostInput.setVisible(true);
                            blueHostLabel.setVisible(true);
                            bluePortInput.setVisible(true);
                            bluePortLabel.setVisible(true);
                            frame.repaint();
                            frame.pack();
                        }
                    }
                });
            } else {
                radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        if(blueThinkTime.isVisible()) {
                            blueThinkTime.setVisible(false);
                            blueThinkTimeLabel.setVisible(false);
                            blueHostInput.setVisible(false);
                            blueHostLabel.setVisible(false);
                            bluePortInput.setVisible(false);
                            bluePortLabel.setVisible(false);
                            frame.repaint();
                            frame.pack();
                        }
                    }
                });
            }
        }
    }
    /**
     * Method which wakes the called Thread.
     */
    synchronized private void wakeup() {
        notify();
    }
    /**
     * Returns the Arguments as an Array.
     * @return The Arguments
     * @throws Exception if Thread is interrupted
     */
    synchronized public String[] getString() throws Exception {
        wait();
        return string;
    }
}
