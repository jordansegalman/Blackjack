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
    private JButton yesContinuePlayingButton = new JButton("Yes");
    private JButton noContinuePlayingButton = new JButton("No");

    private enum PanelNames {
        WELCOMEPANEL, BETPANEL, ROUNDINFORMATIONPANEL, TURNPANEL
    }

    public BlackjackClientView(BlackjackClientModel model) {
        this.model = model;
        this.setupWindowListener(this.model);
        this.setupFrame();
        this.setupActionListeners();
        this.createWelcomePanel();
        this.createBetPanel();
        this.createRoundInformationPanel();
        this.createTurnPanel();
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
    private DefaultListModel<String> dealerListModel = new DefaultListModel<>();
    private JList<String> dealerCardsList;
    private JLabel playerCardsLabel = new JLabel("Your Cards:");
    private DefaultListModel<String> playerListModel = new DefaultListModel<>();
    private JList<String> playerCardsList;
    private JLabel originalHandBetLabel = new JLabel();
    private JLabel roundInformationMoneyLabel = new JLabel();
    private JLabel roundInformationBlackjackLabel;
    private JLabel roundInformationInsuranceLabel;
    private JLabel insuranceBetWaitingLabel = new JLabel("Waiting for other players to place their insurance bets.");
    private JLabel beforeTurnWaitingLabel = new JLabel("Waiting for other players to take their turns.");

    private void createRoundInformationPanel() {
        this.roundInformationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.roundInformationPanel.add(this.dealerCardsLabel, constraints);
        this.dealerCardsList = new JList<>(dealerListModel);
        constraints.gridy = 1;
        this.roundInformationPanel.add(this.dealerCardsList, constraints);
        constraints.gridy = 2;
        this.roundInformationPanel.add(this.playerCardsLabel, constraints);
        this.playerCardsList = new JList<>(playerListModel);
        constraints.gridy = 3;
        this.roundInformationPanel.add(this.playerCardsList, constraints);
        constraints.gridy = 4;
        this.roundInformationPanel.add(this.originalHandBetLabel, constraints);
        constraints.gridy = 5;
        this.roundInformationPanel.add(this.roundInformationMoneyLabel, constraints);
        this.roundInformationBlackjackLabel = new JLabel();
        constraints.gridy = 6;
        this.roundInformationPanel.add(this.roundInformationBlackjackLabel, constraints);
        this.roundInformationInsuranceLabel = new JLabel();
        constraints.gridy = 7;
        this.roundInformationPanel.add(this.roundInformationInsuranceLabel, constraints);
        this.yesInsuranceBetButton.setVisible(false);
        constraints.gridy = 8;
        this.roundInformationPanel.add(this.yesInsuranceBetButton, constraints);
        this.noInsuranceBetButton.setVisible(false);
        constraints.gridx = 1;
        this.roundInformationPanel.add(this.noInsuranceBetButton, constraints);
        this.insuranceBetWaitingLabel.setVisible(false);
        constraints.gridx = 0;
        constraints.gridy = 9;
        this.roundInformationPanel.add(this.insuranceBetWaitingLabel, constraints);
        this.beforeTurnWaitingLabel.setVisible(false);
        constraints.gridy = 10;
        this.roundInformationPanel.add(this.beforeTurnWaitingLabel, constraints);
        add(this.roundInformationPanel, PanelNames.ROUNDINFORMATIONPANEL.toString());
    }

    public void addDealerCard(String card) {
        this.dealerListModel.addElement(card + "\n");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void removeDealerCard(int index) {
        this.dealerListModel.removeElementAt(index);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void addPlayerCard(String card) {
        this.playerListModel.addElement(card + "\n");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setOriginalHandBetLabel(String bet) {
        this.originalHandBetLabel.setText(bet);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setRoundInformationBlackjackLabel(String message) {
        this.roundInformationBlackjackLabel.setText(message);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setRoundInformationInsuranceLabel(String message) {
        this.roundInformationInsuranceLabel.setText(message);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void enableInsuranceBet() {
        this.setRoundInformationInsuranceLabel("Would you like to place an insurance bet?");
        this.yesInsuranceBetButton.setVisible(true);
        this.noInsuranceBetButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void insuranceBetError() {
        this.setRoundInformationInsuranceLabel("ERROR");
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

    public void insuranceBetNotPlaced() {
        this.roundInformationInsuranceLabel.setVisible(false);
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

    public void setBeforeTurnWaiting(Boolean waiting) {
        this.beforeTurnWaitingLabel.setVisible(waiting);
    }

    private JPanel turnPanel;
    private JLabel dealerCardsTurnLabel = new JLabel("Dealer's Cards:");
    private JList<String> dealerCardsTurnList;
    private JLabel dealerHandValueLabel = new JLabel();
    private JLabel playerCardsTurnLabel = new JLabel("Your Cards:");
    private JPanel playerHandsPanel;
    private JLabel turnMoneyLabel = new JLabel();
    private JLabel turnBlackjackLabel = new JLabel();
    private JLabel afterTurnWaitingLabel = new JLabel("Waiting for other players to take their turns.");

    private void createTurnPanel() {
        this.turnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.turnPanel.add(this.dealerCardsTurnLabel, constraints);
        this.dealerCardsTurnList = new JList<>(dealerListModel);
        constraints.gridy = 1;
        this.turnPanel.add(this.dealerCardsTurnList, constraints);
        constraints.gridy = 2;
        this.turnPanel.add(this.dealerHandValueLabel, constraints);
        constraints.gridy = 3;
        this.turnPanel.add(this.playerCardsTurnLabel, constraints);
        this.playerHandsPanel = new JPanel();
        constraints.gridy = 4;
        this.turnPanel.add(this.playerHandsPanel, constraints);
        constraints.gridy = 5;
        this.turnPanel.add(this.turnMoneyLabel, constraints);
        constraints.gridy = 6;
        this.turnPanel.add(this.turnBlackjackLabel, constraints);
        this.afterTurnWaitingLabel.setVisible(false);
        constraints.gridy = 7;
        this.turnPanel.add(this.afterTurnWaitingLabel, constraints);
        add(this.turnPanel, PanelNames.TURNPANEL.toString());
    }

    public void setDealerHandValueLabel(String handValue) {
        this.dealerHandValueLabel.setText(handValue);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void addPlayerHandPanel(PlayerHandPanel playerHandPanel, int index) {
        this.playerHandsPanel.add(playerHandPanel, index);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void removePlayerHandPanel(PlayerHandPanel playerHandPanel) {
        this.playerHandsPanel.remove(playerHandPanel);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setTurnMoneyLabel(String money) {
        this.turnMoneyLabel.setText("$" + money);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setTurnBlackjackLabel(String blackjack) {
        this.turnBlackjackLabel.setText(blackjack);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setAfterTurnWaiting(Boolean waiting) {
        this.afterTurnWaitingLabel.setVisible(waiting);
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

    public void showTurnPanel() {
        showPanel(PanelNames.TURNPANEL);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object target = actionEvent.getSource();

        if (target == this.betButton) {
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
        } else if (target == this.yesContinuePlayingButton) {

        } else if (target == this.noContinuePlayingButton) {

        }
    }
}