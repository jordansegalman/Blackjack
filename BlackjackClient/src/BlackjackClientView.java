import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * BlackjackClientView objects create the GUI for a Blackjack player.
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
    private BlackjackClient controller; // client GUI controller

    // welcome panel components
    private JLabel welcomeWaitingLabel;

    // bet panel components
    private JLabel minimumBetLabel;
    private JTextField betField;
    private JButton betButton;
    private JLabel betMoneyLabel;
    private JLabel betMessageLabel;
    private JLabel betWaitingLabel;

    // turn panel components
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

    // continue playing panel components
    private JLabel continuePlayingMessageLabel;
    private JLabel continuePlayingMoneyLabel;
    private JLabel continuePlayingWaitingLabel;

    /**
     * Names of panels in GUI.
     */

    private enum PanelNames {
        WELCOMEPANEL, BETPANEL, TURNPANEL, CONTINUEPLAYINGPANEL
    }

    /**
     * Constructor for BlackjackClientView object.
     *
     * @param controller Client GUI controller
     */

    public BlackjackClientView(BlackjackClient controller) {
        this.controller = controller;
        setupWindowListener(this.controller);
        setupFrame();
        createPanels();
        setupActionListeners();
    }

    /**
     * Sets up the window listener.
     *
     * @param controller Client GUI controller
     */

    private void setupWindowListener(BlackjackClient controller) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    controller.quitGame();
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Sets up the frame.
     */

    private void setupFrame() {
        setTitle("Blackjack");
        setMinimumSize(FRAME_MINIMUM_DIMENSION);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new CardLayout());
        showChanges();
    }

    /**
     * Creates all of the panels.
     */

    private void createPanels() {
        createWelcomePanel();
        createBetPanel();
        createTurnPanel();
        createContinuePlayingPanel();
    }

    /**
     * Sets up the action listeners.
     */

    private void setupActionListeners() {
        betField.addActionListener(this);
        betButton.addActionListener(this);
        yesButton.addActionListener(this);
        noButton.addActionListener(this);
    }

    /**
     * Shows changes made to GUI.
     */

    private void showChanges() {
        revalidate();
        repaint();
        setVisible(true);
    }

    /**
     * Creates the welcome panel.
     */

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

    /**
     * Shows the welcome waiting label.
     *
     * @param b If true, shows the waiting label; otherwise, hides the waiting label
     */

    public void setWelcomeWaiting(Boolean b) {
        welcomeWaitingLabel.setVisible(b);
        showChanges();
    }

    /**
     * Creates the bet panel.
     */

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

    /**
     * Sets the minimum bet label to the given minimum bet.
     *
     * @param minimumBet Minimum bet to set label to
     */

    public void setMinimumBetLabel(String minimumBet) {
        minimumBetLabel.setText("The minimum bet is $" + minimumBet + ". How much would you like to bet?");
        showChanges();
    }

    /**
     * Sets the bet message label to the given error message.
     *
     * @param errorMessage Error message to set label to
     */

    public void betError(String errorMessage) {
        betMessageLabel.setText(errorMessage);
        enableBetButton(true);
        enableBetField(true);
        showChanges();
    }

    /**
     * Removes the text from the bet message label.
     */

    public void betSuccess() {
        betMessageLabel.setText("");
        showChanges();
    }

    /**
     * Sets the bet money label to the given amount of money.
     *
     * @param money Amount of money to set label to
     */

    public void setBetMoneyLabel(String money) {
        betMoneyLabel.setText("Money: $" + money);
        showChanges();
    }

    /**
     * Shows the bet waiting label.
     *
     * @param b If true, shows the waiting label; otherwise, hides the waiting label
     */

    public void setBetWaiting(Boolean b) {
        betWaitingLabel.setVisible(b);
        showChanges();
    }

    /**
     * Creates the turn panel.
     */

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

    /**
     * Adds a given JLabel containing the image of a card to the dealer hand panel.
     *
     * @param cardLabel JLabel containing image of card
     */

    public void addDealerCard(JLabel cardLabel) {
        dealerHandPanel.add(cardLabel);
        showChanges();
    }

    /**
     * Removes the dealer's face-down card.
     */

    public void removeDealerFaceDownCard() {
        dealerHandPanel.remove(dealerHandPanel.getComponent(1));
        showChanges();
    }

    /**
     * Sets the dealer hand value label to the given hand value.
     *
     * @param dealerHandValue Hand value to set label to
     */

    public void setDealerHandValueLabel(String dealerHandValue) {
        dealerHandValueLabel.setText("Dealer Hand Value: " + dealerHandValue);
        showChanges();
    }

    /**
     * Adds the given BlackjackHandPanel to the playerHandsPanel at the given index.
     *
     * @param playerHandPanel BlackjackHandPanel to add to playerHandsPanel
     * @param index Index to add BlackjackHandPanel at
     */

    public void addPlayerHandPanel(BlackjackHandPanel playerHandPanel, int index) {
        playerHandsPanel.add(playerHandPanel, index);
        showChanges();
    }

    /**
     * Removes the given BlackjackHandPanel from the playerHandsPanel.
     *
     * @param playerHandPanel BlackjackHandPanel to remove from playerHandsPanel
     */

    public void removePlayerHandPanel(BlackjackHandPanel playerHandPanel) {
        playerHandsPanel.remove(playerHandPanel);
        showChanges();
    }

    /**
     * Sets the turn money label to the given amount of money.
     *
     * @param money Amount of money to set label to
     */

    public void setTurnMoneyLabel(String money) {
        turnMoneyLabel.setText("Money: $" + money);
        showChanges();
    }

    /**
     * Sets the message label to the given message.
     *
     * @param message Message to set label to
     */

    public void setMessageLabel(String message) {
        messageLabel.setText(message);
        showChanges();
    }

    /**
     * Sets the message label to the insurance bet message and enables the yes and no buttons.
     */

    public void enableInsuranceBet() {
        setMessageLabel("Would you like to place an insurance bet?");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    /**
     * Sets the message label to the error message and enables the yes and no buttons.
     */

    public void insuranceBetError() {
        setMessageLabel("ERROR");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    /**
     * Disables the yes and no buttons after either placing or not placing insurance bet.
     */

    public void insuranceBetSuccess() {
        enableYesButton(false);
        enableNoButton(false);
        showChanges();
    }

    /**
     * Removes the text from the message label.
     */

    public void removeInsuranceBetInfo() {
        messageLabel.setText("");
        showChanges();
    }

    /**
     * Sets the message label to the continue playing message and enables the yes and no buttons.
     */

    public void enableContinuePlaying() {
        setMessageLabel("Would you like to keep playing?");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    /**
     * Sets the message label to the error message and enables the yes and no buttons.
     */

    public void continuePlayingError() {
        setMessageLabel("ERROR");
        enableYesButton(true);
        enableNoButton(true);
        showChanges();
    }

    /**
     * Sets the Blackjack label to the given message.
     *
     * @param blackjackMessage Message to set label to
     */

    public void setBlackjackLabel(String blackjackMessage) {
        blackjackLabel.setText(blackjackMessage);
        showChanges();
    }

    /**
     * Shows the insurance bet waiting label.
     *
     * @param b If true, shows the waiting label; otherwise, hides the waiting label
     */

    public void setInsuranceBetWaiting(Boolean b) {
        insuranceBetWaitingLabel.setVisible(b);
        showChanges();
    }

    /**
     * Shows the turn waiting label.
     *
     * @param b If true, shows the waiting label; otherwise, hides the waiting label
     */

    public void setTurnWaiting(Boolean b) {
        turnWaitingLabel.setVisible(b);
        showChanges();
    }

    /**
     * Creates the continue playing panel.
     */

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

    /**
     * Sets the continue playing money label to the given amount of money.
     *
     * @param money Amount of money to set label to
     */

    public void setContinuePlayingMoneyLabel(String money) {
        continuePlayingMoneyLabel.setText("Money: $" + money);
        showChanges();
    }

    /**
     * Sets the continue playing message label to the game over message.
     */

    public void gameOver() {
        continuePlayingMessageLabel.setText("Thanks for playing!");
        showChanges();
    }

    /**
     * Shows the continue playing waiting label.
     *
     * @param b If true, shows the waiting label; otherwise, hides the waiting label
     */

    public void setContinuePlayingWaiting(Boolean b) {
        continuePlayingWaitingLabel.setVisible(b);
        showChanges();
    }

    /**
     * Enables or disables the bet field.
     *
     * @param b If true, enables and shows the bet field; otherwise, disables and hides the bet field
     */

    private void enableBetField(Boolean b) {
        betField.setEnabled(b);
        showChanges();
    }

    /**
     * Enables and shows or disables and hides the bet button.
     *
     * @param b If true, enables and shows the bet button; otherwise, disables and hides the bet button
     */

    private void enableBetButton(Boolean b) {
        betButton.setEnabled(b);
        betButton.setVisible(b);
        showChanges();
    }

    /**
     * Enables and shows or disables and hides the yes button.
     *
     * @param b If true, enables and shows the yes button; otherwise, disables and hides the yes button
     */

    private void enableYesButton(Boolean b) {
        yesButton.setEnabled(b);
        yesButton.setVisible(b);
        showChanges();
    }

    /**
     * Enables and shows or disables and hides the no button.
     *
     * @param b If true, enables and shows the no button; otherwise, disables and hides the no button
     */

    private void enableNoButton(Boolean b) {
        noButton.setEnabled(b);
        noButton.setVisible(b);
        showChanges();
    }

    /**
     * Shows the panel with the given name.
     *
     * @param panelName Name of panel to show
     */

    private void showPanel(PanelNames panelName) {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(), panelName.toString());
        showChanges();
    }

    /**
     * Shows the welcome panel.
     */

    public void showWelcomePanel() {
        showPanel(PanelNames.WELCOMEPANEL);
    }

    /**
     * Shows the bet panel.
     */

    public void showBetPanel() {
        showPanel(PanelNames.BETPANEL);
    }

    /**
     * Shows the turn panel.
     */

    public void showTurnPanel() {
        showPanel(PanelNames.TURNPANEL);
    }

    /**
     * Shows the continue playing panel.
     */

    public void showContinuePlayingPanel() {
        showPanel(PanelNames.CONTINUEPLAYINGPANEL);
    }

    /**
     * Resets the view.
     */

    public void reset() {
        createPanels();
        setupActionListeners();
        showContinuePlayingPanel();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e Event generated by component action
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        Object target = e.getSource();
        if (target == betField || target == betButton) {
            controller.sendClientMessage(betField.getText());
            enableBetField(false);
            enableBetButton(false);
        } else if (target == yesButton) {
            controller.sendClientMessage(yesButton.getText());
            enableYesButton(false);
            enableNoButton(false);
        } else if (target == noButton) {
            controller.sendClientMessage(noButton.getText());
            enableYesButton(false);
            enableNoButton(false);
        }
    }
}