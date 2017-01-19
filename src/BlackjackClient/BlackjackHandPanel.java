package BlackjackClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackjackHandPanel extends JPanel implements ActionListener {
    private BlackjackClientModel model;
    private DefaultListModel<String> cardsListModel = new DefaultListModel<>();
    private JList<String> cardsList = new JList<>(this.cardsListModel);
    private JLabel handValueLabel = new JLabel();
    private JLabel handBetLabel = new JLabel();
    private JButton hitButton = new JButton("Hit");
    private JButton standButton = new JButton("Stand");
    private JButton yesButton = new JButton("Yes");
    private JButton noButton = new JButton("No");
    private JLabel handMessageLabel = new JLabel();

    public BlackjackHandPanel(BlackjackClientModel model, String firstCard, String secondCard) {
        this.model = model;
        this.cardsListModel.addElement(firstCard);
        this.cardsListModel.addElement(secondCard);
        this.setupHand();
    }

    public BlackjackHandPanel(BlackjackClientModel model) {
        this.model = model;
        this.setupHand();
    }

    private void setupHand() {
        this.setupActionListeners();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(this.cardsList, constraints);
        constraints.gridy = 1;
        add(this.handValueLabel, constraints);
        constraints.gridy = 2;
        add(this.handBetLabel, constraints);
        constraints.gridy = 3;
        add(this.handMessageLabel, constraints);
        this.hitButton.setEnabled(false);
        this.hitButton.setVisible(false);
        constraints.gridy = 4;
        add(this.hitButton, constraints);
        this.standButton.setEnabled(false);
        this.standButton.setVisible(false);
        constraints.gridx = 1;
        add(this.standButton, constraints);
        this.yesButton.setEnabled(false);
        this.yesButton.setVisible(false);
        constraints.gridx = 0;
        constraints.gridy = 5;
        add(this.yesButton, constraints);
        this.noButton.setEnabled(false);
        this.noButton.setVisible(false);
        constraints.gridx = 1;
        add(this.noButton, constraints);
    }

    public void setHandValueLabel(String handValue) {
        this.handValueLabel.setText(handValue);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setHandBet(String bet) {
        this.handBetLabel.setText(bet);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setHandMessageLabel(String message) {
        this.handMessageLabel.setText(message);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void enableHitStand() {
        setHandMessageLabel("Would you like to hit or stand?");
        this.hitButton.setEnabled(true);
        this.hitButton.setVisible(true);
        this.standButton.setEnabled(true);
        this.standButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void hitStandError() {
        setHandMessageLabel("ERROR");
        this.hitButton.setEnabled(true);
        this.hitButton.setVisible(true);
        this.standButton.setEnabled(true);
        this.standButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void addCard(String card) {
        this.cardsListModel.addElement(card);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void removeCard(int index) {
        this.cardsListModel.removeElementAt(index);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void bust() {
        setHandMessageLabel("You busted.");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void enableDoubleDown() {
        setHandMessageLabel("Would you like to double down?");
        this.yesButton.setEnabled(true);
        this.yesButton.setVisible(true);
        this.noButton.setEnabled(true);
        this.noButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void doubleDownSuccess() {
        setHandMessageLabel("Your bet on this hand has been doubled. You were given a card face down.");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void enableSplitPairs() {
        setHandMessageLabel("Would you like to split pairs?");
        this.yesButton.setEnabled(true);
        this.yesButton.setVisible(true);
        this.noButton.setEnabled(true);
        this.noButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void yesNoError() {
        setHandMessageLabel("ERROR");
        this.yesButton.setEnabled(true);
        this.yesButton.setVisible(true);
        this.noButton.setEnabled(true);
        this.noButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void revealDoubleDownCard(String message) {
        setHandMessageLabel(message);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private void setupActionListeners() {
        this.hitButton.addActionListener(this);
        this.standButton.addActionListener(this);
        this.yesButton.addActionListener(this);
        this.noButton.addActionListener(this);
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