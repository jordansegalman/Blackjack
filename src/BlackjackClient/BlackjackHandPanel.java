package BlackjackClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackjackHandPanel extends JPanel implements ActionListener {
    private static final Color CARD_TABLE_GREEN = new Color(37, 93, 54);
    private static final Color TEXT_COLOR = new Color(230, 230, 230);
    private static final Dimension BUTTONS_DIMENSION = new Dimension(110, 25);
    private BlackjackClientModel model;
    private JPanel cardsPanel;
    private JLabel handValueLabel;
    private JLabel handBetLabel;
    private JLabel handMessageLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton splitPairsButton;
    private JButton doubleDownButton;

    public BlackjackHandPanel(BlackjackClientModel model) {
        this.model = model;
        setupHand();
        setupActionListeners();
    }

    private void setupHand() {
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

    private void setupActionListeners() {
        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        splitPairsButton.addActionListener(this);
        doubleDownButton.addActionListener(this);
    }

    private void showChanges() {
        revalidate();
        repaint();
        setVisible(true);
    }

    public void setHandValueLabel(String handValue) {
        handValueLabel.setText("Hand Value: " + handValue);
        showChanges();
    }

    public void setHandBet(String bet) {
        handBetLabel.setText("Bet: $" + bet);
        showChanges();
    }

    public void setHandMessageLabel(String message) {
        handMessageLabel.setText(message);
        showChanges();
    }

    public void turnError() {
        setHandMessageLabel("ERROR");
        enableHitButton(true);
        enableStandButton(true);
        showChanges();
    }

    public void enableHitStand() {
        enableHitButton(true);
        enableStandButton(true);
        showChanges();
    }

    public void addCard(JLabel card) {
        cardsPanel.add(card);
        showChanges();
    }

    public void bust() {
        setHandMessageLabel("You busted.");
        showChanges();
    }

    public void enableSplitPairs() {
        enableSplitPairsButton(true);
        showChanges();
    }

    public void enableDoubleDown() {
        enableDoubleDownButton(true);
        showChanges();
    }

    public void doubleDownSuccess() {
        setHandMessageLabel("Your bet on this hand has been doubled.");
        showChanges();
    }

    public void removeDoubleDownFaceDownCard() {
        cardsPanel.remove(cardsPanel.getComponent(2));
        showChanges();
    }

    private void enableHitButton(Boolean b) {
        hitButton.setEnabled(b);
        hitButton.setVisible(b);
        showChanges();
    }

    private void enableStandButton(Boolean b) {
        standButton.setEnabled(b);
        standButton.setVisible(b);
        showChanges();
    }

    private void enableSplitPairsButton(Boolean b) {
        splitPairsButton.setEnabled(b);
        splitPairsButton.setVisible(b);
        showChanges();
    }

    private void enableDoubleDownButton(Boolean b) {
        doubleDownButton.setEnabled(b);
        doubleDownButton.setVisible(b);
        showChanges();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object target = actionEvent.getSource();

        if(target == hitButton) {
            model.sendClientMessage(hitButton.getText());
            enableHitButton(false);
            enableStandButton(false);
            enableSplitPairsButton(false);
            enableDoubleDownButton(false);
        } else if (target == standButton) {
            model.sendClientMessage(standButton.getText());
            enableHitButton(false);
            enableStandButton(false);
            enableSplitPairsButton(false);
            enableDoubleDownButton(false);
        } else if (target == splitPairsButton) {
            model.sendClientMessage(splitPairsButton.getText());
            enableHitButton(false);
            enableStandButton(false);
            enableSplitPairsButton(false);
            enableDoubleDownButton(false);
        } else if (target == doubleDownButton) {
            model.sendClientMessage(doubleDownButton.getText());
            enableHitButton(false);
            enableStandButton(false);
            enableSplitPairsButton(false);
            enableDoubleDownButton(false);
        }
    }
}