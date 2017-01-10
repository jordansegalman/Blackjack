package BlackjackClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * BlackjackClientView objects represent a Blackjack client GUI.
 *
 * @author Jordan Segalman
 */

public class BlackjackClientView extends JFrame implements ActionListener {
    private BlackjackClientModel model;
    private JButton betButton = new JButton("Place Bet");
    private JButton insuranceBetButton = new JButton("Place Insurance Bet");
    private JButton hitButton = new JButton("Hit");
    private JButton standButton = new JButton("Stand");
    private JButton splitPairsButton = new JButton("Split Pairs");
    private JButton doubleDownButton = new JButton("Double Down");
    private JButton yesContinuePlayingButton = new JButton("Yes");
    private JButton noContinuePlayingButton = new JButton("No");
    private JLabel moneyLabel = new JLabel();

    private enum PanelNames {
        WELCOMEPANEL, BETPANEL, ROUNDINFORMATIONPANEL
    }

    public BlackjackClientView(BlackjackClientModel model) {
        this.model = model;
        this.setupWindowListener(this.model);
        this.setupFrame();
        this.setupActionListeners();
        this.createWelcomePanel();
        this.createBetPanel();
        this.createRoundInformationPanel();
    }

    private void setupWindowListener(BlackjackClientModel model) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    model.quitGame();
                    System.exit(0);
                }
            }
        });
    }

    private void setupFrame() {
        setTitle("Blackjack");
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new CardLayout());
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private void setupActionListeners() {
        this.betButton.addActionListener(this);
        this.insuranceBetButton.addActionListener(this);
        this.hitButton.addActionListener(this);
        this.standButton.addActionListener(this);
        this.splitPairsButton.addActionListener(this);
        this.doubleDownButton.addActionListener(this);
        this.yesContinuePlayingButton.addActionListener(this);
        this.noContinuePlayingButton.addActionListener(this);
    }

    private JPanel welcomePanel;
    private JLabel welcomeLabel = new JLabel("Welcome to Blackjack!");
    private JLabel welcomeMessageLabel = new JLabel("Waiting for other players.");

    public void createWelcomePanel() {
        this.welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.welcomePanel.add(this.welcomeLabel, constraints);
        constraints.gridy = 1;
        this.welcomePanel.add(this.welcomeMessageLabel, constraints);
        add(this.welcomePanel, PanelNames.WELCOMEPANEL.toString());
    }

    private JPanel betPanel;
    private JLabel minimumBetLabel = new JLabel();
    private JTextField betField = new JTextField(5);
    private JLabel betMessageLabel;

    public void createBetPanel() {
        this.betPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.betPanel.add(this.minimumBetLabel, constraints);
        constraints.gridy = 1;
        this.betPanel.add(this.betField, constraints);
        constraints.gridy = 2;
        this.betPanel.add(this.betButton, constraints);
        constraints.gridy = 3;
        this.betPanel.add(this.moneyLabel, constraints);
        this.betMessageLabel = new JLabel("Place your bet.");
        constraints.gridy = 4;
        this.betPanel.add(this.betMessageLabel, constraints);
        add(this.betPanel, PanelNames.BETPANEL.toString());
    }

    public void setMinimumBetLabel(String minimumBet) {
        this.minimumBetLabel.setText("The minimum bet is $" + minimumBet + ". How much would you like to bet?");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void betError(String errorMessage) {
        this.betMessageLabel.setText(errorMessage);
        this.betButton.setEnabled(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void betSuccess() {
        this.betMessageLabel.setText("Waiting for other players.");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void createRoundInformationPanel() {

    }

    public void setMoneyLabel(String money) {
        this.moneyLabel.setText("$" + money);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private void showPanel(PanelNames panel) {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(), panel.toString());
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void showWelcomePanel() {
        showPanel(PanelNames.WELCOMEPANEL);
    }

    public void showBetPanel() {
        showPanel(PanelNames.BETPANEL);
    }

    public void showRoundInformationPanel() {
        showPanel(PanelNames.ROUNDINFORMATIONPANEL);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object target = actionEvent.getSource();

        if(target == this.betButton) {
            this.model.sendClientMessage(this.betField.getText());
            this.betButton.setEnabled(false);
        } else if (target == this.insuranceBetButton) {

        } else if (target == this.hitButton) {

        } else if (target == this.standButton) {

        } else if (target == this.splitPairsButton) {

        } else if (target == this.doubleDownButton) {

        } else if (target == this.yesContinuePlayingButton) {

        } else if (target == this.noContinuePlayingButton) {

        }
    }
}