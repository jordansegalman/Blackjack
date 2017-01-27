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
    private static final Dimension FRAME_MINIMUM_DIMENSION = new Dimension(960, 600);
    private static final Dimension DEALER_HAND_PANEL_DIMENSION = new Dimension(930, 170);
    private static final Dimension PLAYER_HANDS_PANEL_DIMENSION = new Dimension(930, 265);
    private static final Dimension BUTTONS_DIMENSION = new Dimension(110, 25);
    private static final int BET_FIELD_SIZE = 5;
    private static final Color CARD_TABLE_GREEN = new Color(37, 93, 54);
    private static final Color TEXT_COLOR = new Color(230, 230, 230);
    private static final Float WELCOME_LABEL_SIZE = 24.0f;
    private static final Float HANDS_LABEL_SIZE = 18.0f;
    private BlackjackClientModel model;

    private enum PanelNames {
        WELCOMEPANEL, BETPANEL, TURNPANEL, CONTINUEPLAYINGPANEL
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

    private void setupFrame() {
        setTitle("Blackjack");
        setMinimumSize(FRAME_MINIMUM_DIMENSION);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new CardLayout());
        showChanges();
    }

    private void setupPanels() {
        createWelcomePanel();
        createBetPanel();
        createTurnPanel();
        createContinuePlayingPanel();
    }

    private void setupActionListeners() {
        betField.addActionListener(this);
        betButton.addActionListener(this);
        yesButton.addActionListener(this);
        noButton.addActionListener(this);
    }

    private void showChanges() {
        revalidate();
        repaint();
        setVisible(true);
    }

    private JLabel welcomeWaitingLabel;

    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(CARD_TABLE_GREEN);
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel welcomeLabel = new JLabel("Welcome to Blackjack!");
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(WELCOME_LABEL_SIZE));
        constraints.gridx = 0;
        constraints.gridy = 0;
        welcomePanel.add(welcomeLabel, constraints);
        welcomeWaitingLabel = new JLabel("Waiting for other players to join.");
        welcomeWaitingLabel.setForeground(TEXT_COLOR);
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
        betPanel.setBackground(CARD_TABLE_GREEN);
        GridBagConstraints constraints = new GridBagConstraints();
        minimumBetLabel = new JLabel();
        minimumBetLabel.setForeground(TEXT_COLOR);
        constraints.gridx = 0;
        constraints.gridy = 0;
        betPanel.add(minimumBetLabel, constraints);
        betField = new JTextField(BET_FIELD_SIZE);
        constraints.gridy = 1;
        betPanel.add(betField, constraints);
        betMessageLabel = new JLabel();
        betMessageLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 2;
        betPanel.add(betMessageLabel, constraints);
        betButton = new JButton("Place Bet");
        betButton.setPreferredSize(BUTTONS_DIMENSION);
        constraints.gridy = 3;
        betPanel.add(betButton, constraints);
        betMoneyLabel = new JLabel();
        betMoneyLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 4;
        betPanel.add(betMoneyLabel, constraints);
        betWaitingLabel = new JLabel("Waiting for other players to place their bets.");
        betWaitingLabel.setForeground(TEXT_COLOR);
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
        enableBetField(true);
        showChanges();
    }

    public void betSuccess() {
        betMessageLabel.setText("");
        showChanges();
    }

    public void setBetMoneyLabel(String money) {
        betMoneyLabel.setText("Money: $" + money);
        showChanges();
    }

    public void setBetWaiting(Boolean b) {
        betWaitingLabel.setVisible(b);
        showChanges();
    }

    private JPanel dealerHandPanel;
    private JLabel dealerHandValueLabel;
    private JPanel playerHandsPanel;
    private JLabel messageLabel;
    private JButton yesButton;
    private JButton noButton;
    private JLabel blackjackLabel;
    private JLabel turnMoneyLabel;
    private JLabel insuranceBetWaitingLabel;
    private JLabel turnWaitingLabel;

    private void createTurnPanel() {
        JPanel turnPanel = new JPanel(new GridBagLayout());
        turnPanel.setBackground(CARD_TABLE_GREEN);
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel dealerHandLabel = new JLabel("Dealer's Hand:");
        dealerHandLabel.setForeground(TEXT_COLOR);
        dealerHandLabel.setFont(dealerHandLabel.getFont().deriveFont(HANDS_LABEL_SIZE));
        constraints.gridx = 0;
        constraints.gridy = 0;
        turnPanel.add(dealerHandLabel, constraints);
        dealerHandPanel = new JPanel();
        dealerHandPanel.setBackground(CARD_TABLE_GREEN);
        JScrollPane dealerHandScrollPane = new JScrollPane(dealerHandPanel);
        dealerHandScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dealerHandScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        dealerHandScrollPane.setPreferredSize(DEALER_HAND_PANEL_DIMENSION);
        dealerHandScrollPane.setBorder(BorderFactory.createEmptyBorder());
        constraints.gridy = 1;
        turnPanel.add(dealerHandScrollPane, constraints);
        dealerHandValueLabel = new JLabel();
        dealerHandValueLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 2;
        turnPanel.add(dealerHandValueLabel, constraints);
        JLabel playerHandsLabel = new JLabel("Your Hands:");
        playerHandsLabel.setForeground(TEXT_COLOR);
        playerHandsLabel.setFont(playerHandsLabel.getFont().deriveFont(HANDS_LABEL_SIZE));
        constraints.gridy = 3;
        turnPanel.add(playerHandsLabel, constraints);
        playerHandsPanel = new JPanel();
        playerHandsPanel.setBackground(CARD_TABLE_GREEN);
        JScrollPane playerHandsScrollPane = new JScrollPane(playerHandsPanel);
        playerHandsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        playerHandsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        playerHandsScrollPane.setPreferredSize(PLAYER_HANDS_PANEL_DIMENSION);
        playerHandsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        constraints.gridy = 4;
        turnPanel.add(playerHandsScrollPane, constraints);
        messageLabel = new JLabel();
        messageLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 5;
        turnPanel.add(messageLabel, constraints);
        JPanel insuranceBetButtonsPanel = new JPanel();
        insuranceBetButtonsPanel.setBackground(CARD_TABLE_GREEN);
        constraints.gridy = 6;
        turnPanel.add(insuranceBetButtonsPanel, constraints);
        yesButton = new JButton("Yes");
        yesButton.setPreferredSize(BUTTONS_DIMENSION);
        enableYesButton(false);
        noButton = new JButton("No");
        noButton.setPreferredSize(BUTTONS_DIMENSION);
        enableNoButton(false);
        insuranceBetButtonsPanel.add(yesButton);
        insuranceBetButtonsPanel.add(noButton);
        blackjackLabel = new JLabel();
        blackjackLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 7;
        turnPanel.add(blackjackLabel, constraints);
        turnMoneyLabel = new JLabel();
        turnMoneyLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 8;
        turnPanel.add(turnMoneyLabel, constraints);
        insuranceBetWaitingLabel = new JLabel("Waiting for other players to place their insurance bets.");
        insuranceBetWaitingLabel.setForeground(TEXT_COLOR);
        insuranceBetWaitingLabel.setVisible(false);
        constraints.gridy = 9;
        turnPanel.add(insuranceBetWaitingLabel, constraints);
        turnWaitingLabel = new JLabel("Waiting for other players to take their turns.");
        turnWaitingLabel.setForeground(TEXT_COLOR);
        turnWaitingLabel.setVisible(false);
        constraints.gridy = 10;
        turnPanel.add(turnWaitingLabel, constraints);
        add(turnPanel, PanelNames.TURNPANEL.toString());
    }

    public void addDealerCard(JLabel card) {
        dealerHandPanel.add(card);
        showChanges();
    }

    public void removeDealerFaceDownCard() {
        dealerHandPanel.remove(dealerHandPanel.getComponent(1));
        showChanges();
    }

    public void setDealerHandValueLabel(String dealerHandValue) {
        dealerHandValueLabel.setText("Dealer Hand Value: " + dealerHandValue);
        showChanges();
    }

    public void addPlayerHandPanel(BlackjackHandPanel playerHandPanel, int index) {
        playerHandsPanel.add(playerHandPanel, index);
        showChanges();
    }

    public void removePlayerHandPanel(BlackjackHandPanel playerHandPanel) {
        playerHandsPanel.remove(playerHandPanel);
        showChanges();
    }

    public void setTurnMoneyLabel(String money) {
        turnMoneyLabel.setText("Money: $" + money);
        showChanges();
    }

    public void setMessageLabel(String message) {
        messageLabel.setText(message);
        showChanges();
    }

    public void enableInsuranceBet() {
        setMessageLabel("Would you like to place an insurance bet?");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    public void insuranceBetError() {
        setMessageLabel("ERROR");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    public void insuranceBetSuccess() {
        enableYesButton(false);
        enableNoButton(false);
        showChanges();
    }

    public void insuranceBetNotPlaced() {
        messageLabel.setText("");
        showChanges();
    }

    public void removeInsuranceBetInfo() {
        messageLabel.setText("");
        showChanges();
    }

    public void enableContinuePlaying() {
        setMessageLabel("Would you like to keep playing?");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    public void continuePlayingError() {
        setMessageLabel("ERROR");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    public void setBlackjackLabel(String blackjack) {
        blackjackLabel.setText(blackjack);
        showChanges();
    }

    public void setInsuranceBetWaiting(Boolean b) {
        insuranceBetWaitingLabel.setVisible(b);
        showChanges();
    }

    public void setTurnWaiting(Boolean b) {
        turnWaitingLabel.setVisible(b);
        showChanges();
    }

    private JLabel continuePlayingMessageLabel;
    private JLabel continuePlayingMoneyLabel;
    private JLabel continuePlayingWaitingLabel;

    private void createContinuePlayingPanel() {
        JPanel continuePlayingPanel = new JPanel(new GridBagLayout());
        continuePlayingPanel.setBackground(CARD_TABLE_GREEN);
        GridBagConstraints constraints = new GridBagConstraints();
        continuePlayingMessageLabel = new JLabel();
        continuePlayingMessageLabel.setForeground(TEXT_COLOR);
        constraints.gridx = 0;
        constraints.gridy = 0;
        continuePlayingPanel.add(continuePlayingMessageLabel, constraints);
        JPanel continuePlayingButtonsPanel = new JPanel();
        continuePlayingButtonsPanel.setBackground(CARD_TABLE_GREEN);
        constraints.gridy = 1;
        continuePlayingPanel.add(continuePlayingButtonsPanel, constraints);
        continuePlayingMoneyLabel = new JLabel();
        continuePlayingMoneyLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 2;
        continuePlayingPanel.add(continuePlayingMoneyLabel, constraints);
        continuePlayingWaitingLabel = new JLabel("Waiting for other players to join.");
        continuePlayingWaitingLabel.setForeground(TEXT_COLOR);
        continuePlayingWaitingLabel.setVisible(false);
        constraints.gridy = 3;
        continuePlayingPanel.add(continuePlayingWaitingLabel, constraints);
        add(continuePlayingPanel, PanelNames.CONTINUEPLAYINGPANEL.toString());
    }

    public void setContinuePlayingMoneyLabel(String money) {
        continuePlayingMoneyLabel.setText("Money: $" + money);
        showChanges();
    }

    public void setContinuePlayingMessageLabel(String message) {
        continuePlayingMessageLabel.setText(message);
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

    private void enableBetButton(Boolean b) {
        betButton.setEnabled(b);
        betButton.setVisible(b);
        showChanges();
    }

    private void enableBetField(Boolean b) {
        betField.setEnabled(b);
        showChanges();
    }

    private void enableYesButton(Boolean b) {
        yesButton.setEnabled(b);
        yesButton.setVisible(b);
        showChanges();
    }

    private void enableNoButton(Boolean b) {
        noButton.setEnabled(b);
        noButton.setVisible(b);
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

        if (target == betField) {
            model.sendClientMessage(betField.getText());
            enableBetButton(false);
            enableBetField(false);
        } else if (target == betButton) {
            model.sendClientMessage(betField.getText());
            enableBetButton(false);
            enableBetField(false);
        } else if (target == yesButton) {
            model.sendClientMessage(yesButton.getText());
            enableYesButton(false);
            enableNoButton(false);
        } else if (target == noButton) {
            model.sendClientMessage(noButton.getText());
            enableYesButton(false);
            enableNoButton(false);
        }
    }
}