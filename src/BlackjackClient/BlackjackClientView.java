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
    private static final int MINIMUM_FRAME_WIDTH = 800;
    private static final int MINIMUM_FRAME_HEIGHT = 500;
    private static final int BET_FIELD_SIZE = 5;
    private BlackjackClientModel model;

    private enum PanelNames {
        WELCOMEPANEL, BETPANEL, ROUNDINFORMATIONPANEL, TURNPANEL, CONTINUEPLAYINGPANEL
    }

    public BlackjackClientView(BlackjackClientModel model) {
        this.model = model;
        setupWindowListener(this.model);
        setupFrame();
        setupPanels();
        setupActionListeners();
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

    private void showChanges() {
        validate();
        repaint();
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Blackjack");
        setMinimumSize(new Dimension(MINIMUM_FRAME_WIDTH, MINIMUM_FRAME_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new CardLayout());
        showChanges();
    }

    private void setupPanels() {
        createWelcomePanel();
        createBetPanel();
        createRoundInformationPanel();
        createTurnPanel();
        createContinuePlayingPanel();
    }

    private void setupActionListeners() {
        betButton.addActionListener(this);
        yesInsuranceBetButton.addActionListener(this);
        noInsuranceBetButton.addActionListener(this);
        yesContinuePlayingButton.addActionListener(this);
        noContinuePlayingButton.addActionListener(this);
    }

    private void enableBetButton(Boolean b) {
        betButton.setEnabled(b);
        betButton.setVisible(b);
        showChanges();
    }

    private void enableYesInsuranceBetButton(Boolean b) {
        yesInsuranceBetButton.setEnabled(b);
        yesInsuranceBetButton.setVisible(b);
        showChanges();
    }

    private void enableNoInsuranceBetButton(Boolean b) {
        noInsuranceBetButton.setEnabled(b);
        noInsuranceBetButton.setVisible(b);
        showChanges();
    }

    private void enableYesContinuePlayingButton(Boolean b) {
        yesContinuePlayingButton.setEnabled(b);
        yesContinuePlayingButton.setVisible(b);
        showChanges();
    }

    private void enableNoContinuePlayingButton(Boolean b) {
        noContinuePlayingButton.setEnabled(b);
        noContinuePlayingButton.setVisible(b);
        showChanges();
    }

    private JLabel welcomeWaitingLabel;

    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel welcomeLabel = new JLabel("Welcome to Blackjack!");
        constraints.gridx = 0;
        constraints.gridy = 0;
        welcomePanel.add(welcomeLabel, constraints);
        welcomeWaitingLabel = new JLabel("Waiting for other players to join.");
        welcomeWaitingLabel.setVisible(false);
        constraints.gridy = 1;
        welcomePanel.add(welcomeWaitingLabel, constraints);
        add(welcomePanel, PanelNames.WELCOMEPANEL.toString());
    }

    public void setWelcomeWaiting(Boolean b) {
        welcomeWaitingLabel.setVisible(b);
        showChanges();
    }

    private JLabel minimumBetLabel;
    private JTextField betField;
    private JButton betButton;
    private JLabel betMoneyLabel;
    private JLabel betMessageLabel;
    private JLabel betWaitingLabel;

    private void createBetPanel() {
        JPanel betPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        minimumBetLabel = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        betPanel.add(minimumBetLabel, constraints);
        betField = new JTextField(BET_FIELD_SIZE);
        constraints.gridy = 1;
        betPanel.add(betField, constraints);
        betButton = new JButton("Place Bet");
        constraints.gridy = 2;
        betPanel.add(betButton, constraints);
        betMoneyLabel = new JLabel();
        constraints.gridy = 3;
        betPanel.add(betMoneyLabel, constraints);
        betMessageLabel = new JLabel("Place your bet.");
        constraints.gridy = 4;
        betPanel.add(betMessageLabel, constraints);
        betWaitingLabel = new JLabel("Waiting for other players to place their bets.");
        betWaitingLabel.setVisible(false);
        constraints.gridy = 5;
        betPanel.add(betWaitingLabel, constraints);
        add(betPanel, PanelNames.BETPANEL.toString());
    }

    public void setMinimumBetLabel(String minimumBet) {
        minimumBetLabel.setText("The minimum bet is $" + minimumBet + ". How much would you like to bet?");
        showChanges();
    }

    public void betError(String errorMessage) {
        betMessageLabel.setText(errorMessage);
        enableBetButton(true);
        showChanges();
    }

    public void betSuccess() {
        betMessageLabel.setText(null);
        showChanges();
    }

    public void setBetMoneyLabel(String money) {
        betMoneyLabel.setText("$" + money);
        showChanges();
    }

    public void setBetWaiting(Boolean b) {
        betWaitingLabel.setVisible(b);
        showChanges();
    }

    private DefaultListModel<String> dealerListModel;
    private DefaultListModel<String> playerListModel;
    private JLabel originalHandBetLabel;
    private JLabel roundInformationMoneyLabel;
    private JLabel roundInformationBlackjackLabel;
    private JLabel roundInformationInsuranceLabel;
    private JButton yesInsuranceBetButton;
    private JButton noInsuranceBetButton;
    private JLabel insuranceBetWaitingLabel;
    private JLabel beforeTurnWaitingLabel;

    private void createRoundInformationPanel() {
        JPanel roundInformationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel dealerCardsLabel = new JLabel("Dealer's Cards:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        roundInformationPanel.add(dealerCardsLabel, constraints);
        dealerListModel = new DefaultListModel<>();
        JList<String> dealerCardsList = new JList<>(dealerListModel);
        constraints.gridy = 1;
        roundInformationPanel.add(dealerCardsList, constraints);
        JLabel playerCardsLabel = new JLabel("Your Cards:");
        constraints.gridy = 2;
        roundInformationPanel.add(playerCardsLabel, constraints);
        playerListModel = new DefaultListModel<>();
        JList<String> playerCardsList = new JList<>(playerListModel);
        constraints.gridy = 3;
        roundInformationPanel.add(playerCardsList, constraints);
        originalHandBetLabel = new JLabel();
        constraints.gridy = 4;
        roundInformationPanel.add(originalHandBetLabel, constraints);
        roundInformationMoneyLabel = new JLabel();
        constraints.gridy = 5;
        roundInformationPanel.add(roundInformationMoneyLabel, constraints);
        roundInformationBlackjackLabel = new JLabel();
        constraints.gridy = 6;
        roundInformationPanel.add(roundInformationBlackjackLabel, constraints);
        roundInformationInsuranceLabel = new JLabel();
        constraints.gridy = 7;
        roundInformationPanel.add(roundInformationInsuranceLabel, constraints);
        yesInsuranceBetButton = new JButton("Yes");
        enableYesInsuranceBetButton(false);
        constraints.gridy = 8;
        roundInformationPanel.add(yesInsuranceBetButton, constraints);
        noInsuranceBetButton = new JButton("No");
        enableNoInsuranceBetButton(false);
        constraints.gridx = 1;
        roundInformationPanel.add(noInsuranceBetButton, constraints);
        insuranceBetWaitingLabel = new JLabel("Waiting for other players to place their insurance bets.");
        insuranceBetWaitingLabel.setVisible(false);
        constraints.gridx = 0;
        constraints.gridy = 9;
        roundInformationPanel.add(insuranceBetWaitingLabel, constraints);
        beforeTurnWaitingLabel = new JLabel("Waiting for other players to take their turns.");
        beforeTurnWaitingLabel.setVisible(false);
        constraints.gridy = 10;
        roundInformationPanel.add(beforeTurnWaitingLabel, constraints);
        add(roundInformationPanel, PanelNames.ROUNDINFORMATIONPANEL.toString());
    }

    public void addDealerCard(String card) {
        dealerListModel.addElement(card + "\n");
        showChanges();
    }

    public void removeDealerCard(int index) {
        dealerListModel.removeElementAt(index);
        showChanges();
    }

    public void addPlayerCard(String card) {
        playerListModel.addElement(card + "\n");
        showChanges();
    }

    public void setOriginalHandBetLabel(String bet) {
        originalHandBetLabel.setText(bet);
        showChanges();
    }

    public void setRoundInformationBlackjackLabel(String message) {
        roundInformationBlackjackLabel.setText(message);
        showChanges();
    }

    public void setRoundInformationInsuranceLabel(String message) {
        roundInformationInsuranceLabel.setText(message);
        showChanges();
    }

    public void enableInsuranceBet() {
        setRoundInformationInsuranceLabel("Would you like to place an insurance bet?");
        enableYesInsuranceBetButton(true);
        enableNoInsuranceBetButton(true);
        showChanges();
    }

    public void insuranceBetError() {
        setRoundInformationInsuranceLabel("ERROR");
        enableYesInsuranceBetButton(true);
        enableNoInsuranceBetButton(true);
        showChanges();
    }

    public void insuranceBetSuccess() {
        enableYesInsuranceBetButton(false);
        enableNoInsuranceBetButton(false);
        showChanges();
    }

    public void insuranceBetNotPlaced() {
        roundInformationInsuranceLabel.setVisible(false);
        showChanges();
    }

    public void setRoundInformationMoneyLabel(String money) {
        roundInformationMoneyLabel.setText("$" + money);
        showChanges();
    }

    public void setInsuranceBetWaiting(Boolean b) {
        insuranceBetWaitingLabel.setVisible(b);
        showChanges();
    }

    public void setBeforeTurnWaiting(Boolean b) {
        beforeTurnWaitingLabel.setVisible(b);
        showChanges();
    }

    private JLabel dealerHandValueLabel;
    private JPanel blackjackHandsPanel;
    private JLabel turnMoneyLabel;
    private JLabel turnBlackjackLabel;
    private JLabel afterTurnWaitingLabel;

    private void createTurnPanel() {
        JPanel turnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel dealerCardsTurnLabel = new JLabel("Dealer's Cards:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        turnPanel.add(dealerCardsTurnLabel, constraints);
        JList<String> dealerCardsTurnList = new JList<>(dealerListModel);
        constraints.gridy = 1;
        turnPanel.add(dealerCardsTurnList, constraints);
        dealerHandValueLabel = new JLabel();
        constraints.gridy = 2;
        turnPanel.add(dealerHandValueLabel, constraints);
        JLabel playerCardsTurnLabel = new JLabel("Your Cards:");
        constraints.gridy = 3;
        turnPanel.add(playerCardsTurnLabel, constraints);
        blackjackHandsPanel = new JPanel();
        constraints.gridy = 4;
        turnPanel.add(blackjackHandsPanel, constraints);
        turnMoneyLabel = new JLabel();
        constraints.gridy = 5;
        turnPanel.add(turnMoneyLabel, constraints);
        turnBlackjackLabel = new JLabel();
        constraints.gridy = 6;
        turnPanel.add(turnBlackjackLabel, constraints);
        afterTurnWaitingLabel = new JLabel("Waiting for other players to take their turns.");
        afterTurnWaitingLabel.setVisible(false);
        constraints.gridy = 7;
        turnPanel.add(afterTurnWaitingLabel, constraints);
        add(turnPanel, PanelNames.TURNPANEL.toString());
    }

    public void setDealerHandValueLabel(String handValue) {
        dealerHandValueLabel.setText(handValue);
        showChanges();
    }

    public void addBlackjackHandPanel(BlackjackHandPanel blackjackHandPanel, int index) {
        blackjackHandsPanel.add(blackjackHandPanel, index);
        showChanges();
    }

    public void removeBlackjackHandPanel(BlackjackHandPanel blackjackHandPanel) {
        blackjackHandsPanel.remove(blackjackHandPanel);
        showChanges();
    }

    public void setTurnMoneyLabel(String money) {
        turnMoneyLabel.setText("$" + money);
        showChanges();
    }

    public void setTurnBlackjackLabel(String blackjack) {
        turnBlackjackLabel.setText(blackjack);
        showChanges();
    }

    public void setAfterTurnWaiting(Boolean b) {
        afterTurnWaitingLabel.setVisible(b);
        showChanges();
    }

    private JLabel continuePlayingMessageLabel;
    private JButton yesContinuePlayingButton;
    private JButton noContinuePlayingButton;
    private JLabel continuePlayingMoneyLabel;
    private JLabel continuePlayingWaitingLabel;

    private void createContinuePlayingPanel() {
        JPanel continuePlayingPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        continuePlayingMessageLabel = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        continuePlayingPanel.add(continuePlayingMessageLabel, constraints);
        yesContinuePlayingButton = new JButton("Yes");
        enableYesContinuePlayingButton(false);
        constraints.gridy = 1;
        continuePlayingPanel.add(yesContinuePlayingButton, constraints);
        noContinuePlayingButton = new JButton("No");
        enableNoContinuePlayingButton(false);
        constraints.gridx = 1;
        continuePlayingPanel.add(noContinuePlayingButton, constraints);
        continuePlayingMoneyLabel = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 2;
        continuePlayingPanel.add(continuePlayingMoneyLabel, constraints);
        continuePlayingWaitingLabel = new JLabel("Waiting for other players to join.");
        continuePlayingWaitingLabel.setVisible(false);
        constraints.gridy = 3;
        continuePlayingPanel.add(continuePlayingWaitingLabel, constraints);
        add(continuePlayingPanel, PanelNames.CONTINUEPLAYINGPANEL.toString());
    }

    public void enableContinuePlaying() {
        enableYesContinuePlayingButton(true);
        enableNoContinuePlayingButton(true);
        showChanges();
    }

    public void setContinuePlayingMoneyLabel(String money) {
        continuePlayingMoneyLabel.setText("$" + money);
        showChanges();
    }

    public void setContinuePlayingMessageLabel(String message) {
        continuePlayingMessageLabel.setText(message);
        showChanges();
    }

    public void continuePlayingError() {
        setContinuePlayingMessageLabel("ERROR");
        enableYesContinuePlayingButton(true);
        enableNoContinuePlayingButton(true);
        showChanges();
    }

    public void gameOver() {
        setContinuePlayingMessageLabel("Thanks for playing!");
        showChanges();
    }

    public void setContinuePlayingWaiting(Boolean b) {
        continuePlayingWaitingLabel.setVisible(b);
        showChanges();
    }

    private void showPanel(PanelNames panel) {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(), panel.toString());
        showChanges();
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

    public void showContinuePlayingPanel() {
        showPanel(PanelNames.CONTINUEPLAYINGPANEL);
    }

    public void reset() {
        setupPanels();
        setupActionListeners();
        showContinuePlayingPanel();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object target = actionEvent.getSource();

        if (target == betButton) {
            model.sendClientMessage(betField.getText());
            enableBetButton(false);
        } else if (target == yesInsuranceBetButton) {
            model.sendClientMessage(yesInsuranceBetButton.getText());
            enableYesInsuranceBetButton(false);
            enableNoInsuranceBetButton(false);
        } else if (target == noInsuranceBetButton) {
            model.sendClientMessage(noInsuranceBetButton.getText());
            enableYesInsuranceBetButton(false);
            enableNoInsuranceBetButton(false);
        } else if (target == yesContinuePlayingButton) {
            model.sendClientMessage(yesContinuePlayingButton.getText());
            enableYesContinuePlayingButton(false);
            enableNoContinuePlayingButton(false);
        } else if (target == noContinuePlayingButton) {
            model.sendClientMessage(noContinuePlayingButton.getText());
            enableYesContinuePlayingButton(false);
            enableNoContinuePlayingButton(false);
        }
    }
}