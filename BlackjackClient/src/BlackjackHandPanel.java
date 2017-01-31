import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * BlackjackHandPanel objects are panels that contain the cards, hand value, bet amount, and
 * hit, stand, split pairs, and double down buttons for a Blackjack hand.
 *
 * @author Jordan Segalman
 */

public class BlackjackHandPanel extends JPanel implements ActionListener {
    private static final Color CARD_TABLE_GREEN = new Color(37, 93, 54);
    private static final Color TEXT_COLOR = new Color(230, 230, 230);
    private static final Dimension BUTTONS_DIMENSION = new Dimension(110, 25);
    private BlackjackClient controller; // client GUI controller
    private JPanel cardsPanel;
    private JLabel handValueLabel;
    private JLabel handBetLabel;
    private JLabel handMessageLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton splitPairsButton;
    private JButton doubleDownButton;

    /**
     * Constructor for BlackjackHandPanel object.
     *
     * @param controller Client GUI controller
     */

    public BlackjackHandPanel(BlackjackClient controller) {
        this.controller = controller;
        setupPanel();
        setupActionListeners();
    }

    /**
     * Sets up the panel.
     */

    private void setupPanel() {
        setBackground(CARD_TABLE_GREEN);
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        cardsPanel = new JPanel();
        cardsPanel.setBackground(CARD_TABLE_GREEN);
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(cardsPanel, constraints);
        handValueLabel = new JLabel();
        handValueLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 1;
        add(handValueLabel, constraints);
        handBetLabel = new JLabel();
        handBetLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 2;
        add(handBetLabel, constraints);
        handMessageLabel = new JLabel();
        handMessageLabel.setForeground(TEXT_COLOR);
        constraints.gridy = 3;
        add(handMessageLabel, constraints);
        JPanel hitStandButtonsPanel = new JPanel();
        hitStandButtonsPanel.setBackground(CARD_TABLE_GREEN);
        hitButton = new JButton("Hit");
        hitButton.setPreferredSize(BUTTONS_DIMENSION);
        hitButton.setEnabled(false);
        hitButton.setVisible(false);
        standButton = new JButton("Stand");
        standButton.setPreferredSize(BUTTONS_DIMENSION);
        standButton.setEnabled(false);
        standButton.setVisible(false);
        hitStandButtonsPanel.add(hitButton);
        hitStandButtonsPanel.add(standButton);
        constraints.gridy = 4;
        add(hitStandButtonsPanel, constraints);
        JPanel yesNoButtonsPanel = new JPanel();
        yesNoButtonsPanel.setBackground(CARD_TABLE_GREEN);
        splitPairsButton = new JButton("Split Pairs");
        splitPairsButton.setPreferredSize(BUTTONS_DIMENSION);
        splitPairsButton.setEnabled(false);
        splitPairsButton.setVisible(false);
        doubleDownButton = new JButton("Double Down");
        doubleDownButton.setPreferredSize(BUTTONS_DIMENSION);
        doubleDownButton.setEnabled(false);
        doubleDownButton.setVisible(false);
        yesNoButtonsPanel.add(splitPairsButton);
        yesNoButtonsPanel.add(doubleDownButton);
        constraints.gridy = 5;
        add(yesNoButtonsPanel, constraints);
    }

    /**
     * Sets up the action listeners.
     */

    private void setupActionListeners() {
        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        splitPairsButton.addActionListener(this);
        doubleDownButton.addActionListener(this);
    }

    /**
     * Shows changes made to the panel.
     */

    private void showChanges() {
        revalidate();
        repaint();
        setVisible(true);
    }

    /**
     * Sets the hand value label to the given hand value.
     *
     * @param handValue Hand value to set label to
     */

    public void setHandValueLabel(String handValue) {
        handValueLabel.setText("Hand Value: " + handValue);
        showChanges();
    }

    /**
     * Sets the hand bet label to the given bet.
     *
     * @param bet Bet to set label to
     */

    public void setHandBet(String bet) {
        handBetLabel.setText("Bet: $" + bet);
        showChanges();
    }

    /**
     * Sets the hand message label to the given message.
     *
     * @param message Message to set label to
     */

    public void setHandMessageLabel(String message) {
        handMessageLabel.setText(message);
        showChanges();
    }

    /**
     * Sets the hand message label to the error message.
     */

    public void turnError() {
        setHandMessageLabel("ERROR");
        showChanges();
    }

    /**
     * Enables the hit and stand buttons.
     */

    public void enableHitStand() {
        enableHitButton(true);
        enableStandButton(true);
        showChanges();
    }

    /**
     * Adds a given JLabel containing the image of a card to the cards panel.
     *
     * @param cardLabel JLabel containing image of card
     */

    public void addCard(JLabel cardLabel) {
        cardsPanel.add(cardLabel);
        showChanges();
    }

    /**
     * Sets the hand message label to the busted message.
     */

    public void bust() {
        setHandMessageLabel("You busted.");
        showChanges();
    }

    /**
     * Enables the split pairs button.
     */

    public void enableSplitPairs() {
        enableSplitPairsButton(true);
        showChanges();
    }

    /**
     * Enables the double down button.
     */

    public void enableDoubleDown() {
        enableDoubleDownButton(true);
        showChanges();
    }

    /**
     * Sets the hand message label to the double down success message.
     */

    public void doubleDownSuccess() {
        setHandMessageLabel("Your bet on this hand has been doubled.");
        showChanges();
    }

    /**
     * Removes the face-down card added after doubling down.
     */

    public void removeDoubleDownFaceDownCard() {
        cardsPanel.remove(cardsPanel.getComponent(2));
        showChanges();
    }

    /**
     * Enables and shows or disables and hides the hit button.
     *
     * @param b If true, enables and shows the hit button; otherwise, disables and hides the hit button
     */

    private void enableHitButton(Boolean b) {
        hitButton.setEnabled(b);
        hitButton.setVisible(b);
        showChanges();
    }

    /**
     * Enables and shows or disables and hides the stand button.
     *
     * @param b If true, enables and shows the stand button; otherwise, disables and hides the stand button
     */

    private void enableStandButton(Boolean b) {
        standButton.setEnabled(b);
        standButton.setVisible(b);
        showChanges();
    }

    /**
     * Enables and shows or disables and hides the split pairs button.
     *
     * @param b If true, enables and shows the split pairs button; otherwise, disables and hides the split pairs button
     */

    private void enableSplitPairsButton(Boolean b) {
        splitPairsButton.setEnabled(b);
        splitPairsButton.setVisible(b);
        showChanges();
    }

    /**
     * Enables and shows or disables and hides the double down button.
     *
     * @param b If true, enables and shows the double down button; otherwise, disables and hides the double down button
     */

    private void enableDoubleDownButton(Boolean b) {
        doubleDownButton.setEnabled(b);
        doubleDownButton.setVisible(b);
        showChanges();
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e Event generated by component action
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        Object target = e.getSource();
        if(target == hitButton) {
            controller.sendClientMessage(hitButton.getText());
        } else if (target == standButton) {
            controller.sendClientMessage(standButton.getText());
        } else if (target == splitPairsButton) {
            controller.sendClientMessage(splitPairsButton.getText());
        } else if (target == doubleDownButton) {
            controller.sendClientMessage(doubleDownButton.getText());
        }
        enableHitButton(false);
        enableStandButton(false);
        enableSplitPairsButton(false);
        enableDoubleDownButton(false);
    }
}