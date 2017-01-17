package BlackjackClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerHandPanel extends JPanel implements ActionListener {
    private BlackjackClientModel model;
    private DefaultListModel<String> cardsListModel = new DefaultListModel<>();
    private JList<String> cardsList = new JList<>(this.cardsListModel);
    private JLabel handValueLabel = new JLabel();
    private JButton hitButton = new JButton("Hit");
    private JButton standButton = new JButton("Stand");
    private JButton yesButton = new JButton("Yes");
    private JButton noButton = new JButton("No");
    private JLabel handMessageLabel = new JLabel();

    public PlayerHandPanel(BlackjackClientModel model, String firstCard, String secondCard) {
        this.model = model;
        this.setupActionListeners();
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.cardsListModel.addElement(firstCard);
        this.cardsListModel.addElement(secondCard);
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(this.cardsList, constraints);
        constraints.gridy = 1;
        add(this.handValueLabel, constraints);
        constraints.gridy = 2;
        add(this.handMessageLabel, constraints);
        this.hitButton.setEnabled(false);
        constraints.gridy = 3;
        add(this.hitButton, constraints);
        this.standButton.setEnabled(false);
        constraints.gridx = 1;
        add(this.standButton, constraints);
        this.yesButton.setEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 4;
        add(this.yesButton, constraints);
        this.noButton.setEnabled(false);
        constraints.gridx = 1;
        add(this.noButton, constraints);
    }

    public void setHandValueLabel(String handValue) {
        this.handValueLabel.setText(handValue);
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
        this.standButton.setEnabled(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void hitStandError() {
        setHandMessageLabel("ERROR");
        this.hitButton.setEnabled(true);
        this.standButton.setEnabled(true);
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

    public void bust() {
        setHandMessageLabel("You busted.");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setAfterTurnWaiting() {
        setHandMessageLabel("Waiting for other players to take their turns.");
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
            this.model.sendClientMessage("Hit");
            this.hitButton.setEnabled(false);
            this.standButton.setEnabled(false);
        } else if (target == this.standButton) {
            this.model.sendClientMessage("Stand");
            this.hitButton.setEnabled(false);
            this.standButton.setEnabled(false);
        } else if (target == this.yesButton) {
            this.model.sendClientMessage("Yes");
            this.yesButton.setEnabled(false);
            this.noButton.setEnabled(false);
        } else if (target == this.noButton) {
            this.model.sendClientMessage("No");
            this.yesButton.setEnabled(false);
            this.noButton.setEnabled(false);
        }
    }
}