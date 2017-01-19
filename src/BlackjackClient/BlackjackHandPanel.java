package BlackjackClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackjackHandPanel extends JPanel implements ActionListener {
    private BlackjackClientModel model;
    private DefaultListModel<String> cardsListModel;
    private JList<String> cardsList;
    private JLabel handValueLabel;
    private JLabel handBetLabel;
    private JLabel handMessageLabel;
    private JButton hitButton;
    private JButton standButton;
    private JButton yesButton;
    private JButton noButton;

    public BlackjackHandPanel(BlackjackClientModel model, String firstCard, String secondCard) {
        this.model = model;
        this.setupHand();
        this.setupActionListeners();
        this.cardsListModel.addElement(firstCard);
        this.cardsListModel.addElement(secondCard);
    }

    public BlackjackHandPanel(BlackjackClientModel model) {
        this.model = model;
        this.setupHand();
        this.setupActionListeners();
    }

    private void setupHand() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.cardsListModel = new DefaultListModel<>();
        this.cardsList = new JList<>(this.cardsListModel);
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(this.cardsList, constraints);
        this.handValueLabel = new JLabel();
        constraints.gridy = 1;
        add(this.handValueLabel, constraints);
        this.handBetLabel = new JLabel();
        constraints.gridy = 2;
        add(this.handBetLabel, constraints);
        this.handMessageLabel = new JLabel();
        constraints.gridy = 3;
        add(this.handMessageLabel, constraints);
        this.hitButton = new JButton("Hit");
        this.hitButton.setEnabled(false);
        this.hitButton.setVisible(false);
        constraints.gridy = 4;
        add(this.hitButton, constraints);
        this.standButton = new JButton("Stand");
        this.standButton.setEnabled(false);
        this.standButton.setVisible(false);
        constraints.gridx = 1;
        add(this.standButton, constraints);
        this.yesButton = new JButton("Yes");
        this.yesButton.setEnabled(false);
        this.yesButton.setVisible(false);
        constraints.gridx = 0;
        constraints.gridy = 5;
        add(this.yesButton, constraints);
        this.noButton = new JButton("No");
        this.noButton.setEnabled(false);
        this.noButton.setVisible(false);
        constraints.gridx = 1;
        add(this.noButton, constraints);
    }

    private void setupActionListeners() {
        this.hitButton.addActionListener(this);
        this.standButton.addActionListener(this);
        this.yesButton.addActionListener(this);
        this.noButton.addActionListener(this);
    }

    private void showChanges() {
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setHandValueLabel(String handValue) {
        this.handValueLabel.setText(handValue);
        this.showChanges();
    }

    public void setHandBet(String bet) {
        this.handBetLabel.setText(bet);
        this.showChanges();
    }

    public void setHandMessageLabel(String message) {
        this.handMessageLabel.setText(message);
        this.showChanges();
    }

    public void enableHitStand() {
        setHandMessageLabel("Would you like to hit or stand?");
        this.hitButton.setEnabled(true);
        this.hitButton.setVisible(true);
        this.standButton.setEnabled(true);
        this.standButton.setVisible(true);
        this.showChanges();
    }

    public void hitStandError() {
        setHandMessageLabel("ERROR");
        this.hitButton.setEnabled(true);
        this.hitButton.setVisible(true);
        this.standButton.setEnabled(true);
        this.standButton.setVisible(true);
        this.showChanges();
    }

    public void addCard(String card) {
        this.cardsListModel.addElement(card);
        this.showChanges();
    }

    public void removeCard(int index) {
        this.cardsListModel.removeElementAt(index);
        this.showChanges();
    }

    public void bust() {
        setHandMessageLabel("You busted.");
        this.showChanges();
    }

    public void enableDoubleDown() {
        setHandMessageLabel("Would you like to double down?");
        this.yesButton.setEnabled(true);
        this.yesButton.setVisible(true);
        this.noButton.setEnabled(true);
        this.noButton.setVisible(true);
        this.showChanges();
    }

    public void doubleDownSuccess() {
        setHandMessageLabel("Your bet on this hand has been doubled. You were given a card face down.");
        this.showChanges();
    }

    public void enableSplitPairs() {
        setHandMessageLabel("Would you like to split pairs?");
        this.yesButton.setEnabled(true);
        this.yesButton.setVisible(true);
        this.noButton.setEnabled(true);
        this.noButton.setVisible(true);
        this.showChanges();
    }

    public void yesNoError() {
        setHandMessageLabel("ERROR");
        this.yesButton.setEnabled(true);
        this.yesButton.setVisible(true);
        this.noButton.setEnabled(true);
        this.noButton.setVisible(true);
        this.showChanges();
    }

    public void revealDoubleDownCard(String message) {
        setHandMessageLabel(message);
        this.showChanges();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object target = actionEvent.getSource();

        if(target == this.hitButton) {
            this.model.sendClientMessage(this.hitButton.getText());
            this.hitButton.setEnabled(false);
            this.hitButton.setVisible(false);
            this.standButton.setEnabled(false);
            this.standButton.setVisible(false);
        } else if (target == this.standButton) {
            this.model.sendClientMessage(this.standButton.getText());
            this.hitButton.setEnabled(false);
            this.hitButton.setVisible(false);
            this.standButton.setEnabled(false);
            this.standButton.setVisible(false);
        } else if (target == this.yesButton) {
            this.model.sendClientMessage(this.yesButton.getText());
            this.yesButton.setEnabled(false);
            this.yesButton.setVisible(false);
            this.noButton.setEnabled(false);
            this.noButton.setVisible(false);
        } else if (target == this.noButton) {
            this.model.sendClientMessage(this.noButton.getText());
            this.yesButton.setEnabled(false);
            this.yesButton.setVisible(false);
            this.noButton.setEnabled(false);
            this.noButton.setVisible(false);
        }
    }
}