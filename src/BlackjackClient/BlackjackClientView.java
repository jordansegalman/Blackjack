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
    private BorderLayout borderLayout = new BorderLayout();
    private GridBagLayout gridBagLayout = new GridBagLayout();
    private GridBagConstraints gridBagConstraints = new GridBagConstraints();
    private JButton betButton = new JButton("Place Bet");
    private JButton insuranceBetButton = new JButton("Place Insurance Bet");
    private JButton hitButton = new JButton("Hit");
    private JButton standButton = new JButton("Stand");
    private JButton splitPairsButton = new JButton("Split Pairs");
    private JButton doubleDownButton = new JButton("Double Down");
    private JButton yesContinuePlayingButton = new JButton("Yes");
    private JButton noContinuePlayingButton = new JButton("No");
    private JLabel waitingLabel = new JLabel("Waiting for Other Players");
    private JLabel moneyLabel = new JLabel();

    public BlackjackClientView(BlackjackClientModel model) {
        this.model = model;
        this.setupWindowListener(this.model);
        this.setupFrame();
        this.setupActionListeners();
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
        setLayout(borderLayout);
        pack();
        setVisible(true);
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

    public void addWelcomePanel() {
        this.welcomePanel = new JPanel();
        this.welcomePanel.setLayout(this.gridBagLayout);
        this.gridBagConstraints.gridx = 0;
        this.gridBagConstraints.gridy = 0;
        this.welcomePanel.add(this.welcomeLabel, this.gridBagConstraints);
        this.gridBagConstraints.gridx = 0;
        this.gridBagConstraints.gridy = 1;
        this.welcomePanel.add(this.waitingLabel, this.gridBagConstraints);
        add(this.welcomePanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private JPanel betPanel;
    private JLabel minimumBetLabel;
    private JTextField betField = new JTextField(5);
    private JLabel betErrorLabel;

    public void addBetPanel(String money, String minimumBet) {
        this.borderLayout.removeLayoutComponent(this.borderLayout.getLayoutComponent(BorderLayout.CENTER));
        this.betPanel = new JPanel();
        this.betPanel.setLayout(this.gridBagLayout);
        this.minimumBetLabel = new JLabel("The minimum bet is $" + minimumBet + ". How much would you like to bet?");
        this.gridBagConstraints.gridx = 0;
        this.gridBagConstraints.gridy = 0;
        this.betPanel.add(this.minimumBetLabel, this.gridBagConstraints);
        this.gridBagConstraints.gridx = 0;
        this.gridBagConstraints.gridy = 1;
        this.betPanel.add(this.betField, this.gridBagConstraints);
        this.gridBagConstraints.gridx = 0;
        this.gridBagConstraints.gridy = 2;
        this.betPanel.add(this.betButton, this.gridBagConstraints);
        this.moneyLabel.setText("$" + money);
        this.gridBagConstraints.gridx = 0;
        this.gridBagConstraints.gridy = 3;
        this.betPanel.add(this.moneyLabel, this.gridBagConstraints);
        this.betErrorLabel = new JLabel("Place your bet.");
        this.gridBagConstraints.gridx = 0;
        this.gridBagConstraints.gridy = 4;
        this.betPanel.add(this.betErrorLabel, this.gridBagConstraints);
        add(this.betPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public void betError(String errorMessage) {
        this.betErrorLabel.setText(errorMessage);
        this.betButton.setEnabled(true);
        pack();
        setVisible(true);
    }

    public void addRoundInformationPanel(String money) {

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