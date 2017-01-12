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
    private JButton yesInsuranceBetButton = new JButton("Yes");
    private JButton noInsuranceBetButton = new JButton("No");
    private JButton hitButton = new JButton("Hit");
    private JButton standButton = new JButton("Stand");
    private JButton splitPairsButton = new JButton("Split Pairs");
    private JButton doubleDownButton = new JButton("Double Down");
    private JButton yesContinuePlayingButton = new JButton("Yes");
    private JButton noContinuePlayingButton = new JButton("No");

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
        this.yesInsuranceBetButton.addActionListener(this);
        this.noInsuranceBetButton.addActionListener(this);
        this.hitButton.addActionListener(this);
        this.standButton.addActionListener(this);
        this.splitPairsButton.addActionListener(this);
        this.doubleDownButton.addActionListener(this);
        this.yesContinuePlayingButton.addActionListener(this);
        this.noContinuePlayingButton.addActionListener(this);
    }

    private JPanel welcomePanel;
    private JLabel welcomeLabel = new JLabel("Welcome to Blackjack!");
    private JLabel welcomeWaitingLabel = new JLabel("Waiting for other players to join.");

    private void createWelcomePanel() {
        this.welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.welcomePanel.add(this.welcomeLabel, constraints);
        this.welcomeWaitingLabel.setVisible(false);
        constraints.gridy = 1;
        this.welcomePanel.add(this.welcomeWaitingLabel, constraints);
        add(this.welcomePanel, PanelNames.WELCOMEPANEL.toString());
    }

    public void setWelcomeWaiting(Boolean waiting) {
        this.welcomeWaitingLabel.setVisible(waiting);
    }

    private JPanel betPanel;
    private JLabel minimumBetLabel = new JLabel();
    private JTextField betField = new JTextField(5);
    private JLabel betMoneyLabel = new JLabel();
    private JLabel betMessageLabel;
    private JLabel betWaitingLabel = new JLabel("Waiting for other players to place their bets.");

    private void createBetPanel() {
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
        this.betPanel.add(this.betMoneyLabel, constraints);
        this.betMessageLabel = new JLabel("Place your bet.");
        constraints.gridy = 4;
        this.betPanel.add(this.betMessageLabel, constraints);
        this.betWaitingLabel.setVisible(false);
        constraints.gridy = 5;
        this.betPanel.add(this.betWaitingLabel, constraints);
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
        this.betMessageLabel.setText(null);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setBetMoneyLabel(String money) {
        this.betMoneyLabel.setText("$" + money);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setBetWaiting(Boolean waiting) {
        this.betWaitingLabel.setVisible(waiting);
    }

    private JPanel roundInformationPanel;
    private JLabel dealerCardsLabel = new JLabel("Dealer's Cards:");
    private JTextArea dealerCardsArea;
    private JLabel playerCardsLabel = new JLabel("Your Cards:");
    private JTextArea playerCardsArea;
    private JLabel roundInformationMoneyLabel = new JLabel();
    private JLabel roundInformationMessageLabel;
    private JLabel roundInformationSecondMessageLabel;
    private JLabel insuranceBetWaitingLabel = new JLabel("Waiting for other players to place their insurance bets.");
    private JLabel turnWaitingLabel = new JLabel("Waiting for other players to take their turns.");

    private void createRoundInformationPanel() {
        this.roundInformationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.roundInformationPanel.add(this.dealerCardsLabel, constraints);
        this.dealerCardsArea = new JTextArea(5, 20);
        this.dealerCardsArea.setEditable(false);
        constraints.gridy = 1;
        this.roundInformationPanel.add(this.dealerCardsArea, constraints);
        constraints.gridy = 2;
        this.roundInformationPanel.add(this.playerCardsLabel, constraints);
        this.playerCardsArea = new JTextArea(5, 20);
        this.playerCardsArea.setEditable(false);
        constraints.gridy = 3;
        this.roundInformationPanel.add(this.playerCardsArea, constraints);
        constraints.gridy = 4;
        this.roundInformationPanel.add(this.roundInformationMoneyLabel, constraints);
        this.roundInformationMessageLabel = new JLabel();
        constraints.gridy = 5;
        this.roundInformationPanel.add(this.roundInformationMessageLabel, constraints);
        this.roundInformationSecondMessageLabel = new JLabel();
        constraints.gridy = 6;
        this.roundInformationPanel.add(this.roundInformationSecondMessageLabel, constraints);
        this.yesInsuranceBetButton.setVisible(false);
        constraints.gridy = 7;
        this.roundInformationPanel.add(this.yesInsuranceBetButton, constraints);
        this.noInsuranceBetButton.setVisible(false);
        constraints.gridx = 1;
        this.roundInformationPanel.add(this.noInsuranceBetButton, constraints);
        this.insuranceBetWaitingLabel.setVisible(false);
        constraints.gridx = 0;
        constraints.gridy = 8;
        this.roundInformationPanel.add(this.insuranceBetWaitingLabel, constraints);
        this.turnWaitingLabel.setVisible(false);
        constraints.gridy = 9;
        this.roundInformationPanel.add(this.turnWaitingLabel, constraints);
        add(this.roundInformationPanel, PanelNames.ROUNDINFORMATIONPANEL.toString());
    }

    public void addDealerCard(String card) {
        this.dealerCardsArea.append(card + "\n");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void addPlayerCard(String card) {
        this.playerCardsArea.append(card + "\n");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setRoundInformationMessage(String message) {
        this.roundInformationMessageLabel.setText(message);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setRoundInformationSecondMessage(String message) {
        this.roundInformationSecondMessageLabel.setText(message);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void enableInsuranceBet() {
        this.setRoundInformationSecondMessage("Would you like to place an insurance bet?");
        this.yesInsuranceBetButton.setVisible(true);
        this.noInsuranceBetButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void insuranceBetError() {
        this.setRoundInformationSecondMessage("Error placing insurance bet.");
        this.yesInsuranceBetButton.setEnabled(true);
        this.noInsuranceBetButton.setEnabled(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void insuranceBetSuccess() {
        this.yesInsuranceBetButton.setVisible(false);
        this.noInsuranceBetButton.setVisible(false);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setRoundInformationMoneyLabel(String money) {
        this.roundInformationMoneyLabel.setText("$" + money);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setInsuranceBetWaiting(Boolean waiting) {
        this.insuranceBetWaitingLabel.setVisible(waiting);
    }

    public void setTurnWaiting(Boolean waiting) {
        this.turnWaitingLabel.setVisible(waiting);
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
        } else if (target == this.yesInsuranceBetButton) {
            this.model.sendClientMessage(this.yesInsuranceBetButton.getText());
            this.yesInsuranceBetButton.setEnabled(false);
            this.noInsuranceBetButton.setEnabled(false);
        } else if (target == this.noInsuranceBetButton) {
            this.model.sendClientMessage(this.noInsuranceBetButton.getText());
            this.noInsuranceBetButton.setEnabled(false);
            this.yesInsuranceBetButton.setEnabled(false);
        } else if (target == this.hitButton) {

        } else if (target == this.standButton) {

        } else if (target == this.splitPairsButton) {

        } else if (target == this.doubleDownButton) {

        } else if (target == this.yesContinuePlayingButton) {

        } else if (target == this.noContinuePlayingButton) {

        }
    }
}