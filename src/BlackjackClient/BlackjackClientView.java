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
        this.setupWindowListener(this.model);
        this.setupFrame();
        this.setupPanels();
        this.setupActionListeners();
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
        setMinimumSize(new Dimension(MINIMUM_FRAME_WIDTH, MINIMUM_FRAME_HEIGHT));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new CardLayout());
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private void setupPanels() {
        this.createWelcomePanel();
        this.createBetPanel();
        this.createRoundInformationPanel();
        this.createTurnPanel();
        this.createContinuePlayingPanel();
    }

    private void setupActionListeners() {
        this.betButton.addActionListener(this);
        this.yesInsuranceBetButton.addActionListener(this);
        this.noInsuranceBetButton.addActionListener(this);
        this.yesContinuePlayingButton.addActionListener(this);
        this.noContinuePlayingButton.addActionListener(this);
    }

    private JPanel welcomePanel;
    private JLabel welcomeLabel;
    private JLabel welcomeWaitingLabel;

    private void createWelcomePanel() {
        this.welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.welcomeLabel = new JLabel("Welcome to Blackjack!");
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.welcomePanel.add(this.welcomeLabel, constraints);
        this.welcomeWaitingLabel = new JLabel("Waiting for other players to join.");
        this.welcomeWaitingLabel.setVisible(false);
        constraints.gridy = 1;
        this.welcomePanel.add(this.welcomeWaitingLabel, constraints);
        add(this.welcomePanel, PanelNames.WELCOMEPANEL.toString());
    }

    public void setWelcomeWaiting(Boolean waiting) {
        this.welcomeWaitingLabel.setVisible(waiting);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private JPanel betPanel;
    private JLabel minimumBetLabel;
    private JTextField betField;
    private JButton betButton;
    private JLabel betMoneyLabel;
    private JLabel betMessageLabel;
    private JLabel betWaitingLabel;

    private void createBetPanel() {
        this.betPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.minimumBetLabel = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.betPanel.add(this.minimumBetLabel, constraints);
        this.betField = new JTextField(BET_FIELD_SIZE);
        constraints.gridy = 1;
        this.betPanel.add(this.betField, constraints);
        this.betButton = new JButton("Place Bet");
        constraints.gridy = 2;
        this.betPanel.add(this.betButton, constraints);
        this.betMoneyLabel = new JLabel();
        constraints.gridy = 3;
        this.betPanel.add(this.betMoneyLabel, constraints);
        this.betMessageLabel = new JLabel("Place your bet.");
        constraints.gridy = 4;
        this.betPanel.add(this.betMessageLabel, constraints);
        this.betWaitingLabel = new JLabel("Waiting for other players to place their bets.");
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
        this.betButton.setVisible(true);
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
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private JPanel roundInformationPanel;
    private JLabel dealerCardsLabel;
    private DefaultListModel<String> dealerListModel;
    private JList<String> dealerCardsList;
    private JLabel playerCardsLabel;
    private DefaultListModel<String> playerListModel;
    private JList<String> playerCardsList;
    private JLabel originalHandBetLabel;
    private JLabel roundInformationMoneyLabel;
    private JLabel roundInformationBlackjackLabel;
    private JLabel roundInformationInsuranceLabel;
    private JButton yesInsuranceBetButton;
    private JButton noInsuranceBetButton;
    private JLabel insuranceBetWaitingLabel;
    private JLabel beforeTurnWaitingLabel;

    private void createRoundInformationPanel() {
        this.roundInformationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.dealerCardsLabel = new JLabel("Dealer's Cards:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.roundInformationPanel.add(this.dealerCardsLabel, constraints);
        this.dealerListModel = new DefaultListModel<>();
        this.dealerCardsList = new JList<>(dealerListModel);
        constraints.gridy = 1;
        this.roundInformationPanel.add(this.dealerCardsList, constraints);
        this.playerCardsLabel = new JLabel("Your Cards:");
        constraints.gridy = 2;
        this.roundInformationPanel.add(this.playerCardsLabel, constraints);
        this.playerListModel = new DefaultListModel<>();
        this.playerCardsList = new JList<>(playerListModel);
        constraints.gridy = 3;
        this.roundInformationPanel.add(this.playerCardsList, constraints);
        this.originalHandBetLabel = new JLabel();
        constraints.gridy = 4;
        this.roundInformationPanel.add(this.originalHandBetLabel, constraints);
        this.roundInformationMoneyLabel = new JLabel();
        constraints.gridy = 5;
        this.roundInformationPanel.add(this.roundInformationMoneyLabel, constraints);
        this.roundInformationBlackjackLabel = new JLabel();
        constraints.gridy = 6;
        this.roundInformationPanel.add(this.roundInformationBlackjackLabel, constraints);
        this.roundInformationInsuranceLabel = new JLabel();
        constraints.gridy = 7;
        this.roundInformationPanel.add(this.roundInformationInsuranceLabel, constraints);
        this.yesInsuranceBetButton = new JButton("Yes");
        this.yesInsuranceBetButton.setEnabled(false);
        this.yesInsuranceBetButton.setVisible(false);
        constraints.gridy = 8;
        this.roundInformationPanel.add(this.yesInsuranceBetButton, constraints);
        this.noInsuranceBetButton = new JButton("No");
        this.noInsuranceBetButton.setEnabled(false);
        this.noInsuranceBetButton.setVisible(false);
        constraints.gridx = 1;
        this.roundInformationPanel.add(this.noInsuranceBetButton, constraints);
        this.insuranceBetWaitingLabel = new JLabel("Waiting for other players to place their insurance bets.");
        this.insuranceBetWaitingLabel.setVisible(false);
        constraints.gridx = 0;
        constraints.gridy = 9;
        this.roundInformationPanel.add(this.insuranceBetWaitingLabel, constraints);
        this.beforeTurnWaitingLabel = new JLabel("Waiting for other players to take their turns.");
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
        this.yesInsuranceBetButton.setEnabled(true);
        this.yesInsuranceBetButton.setVisible(true);
        this.noInsuranceBetButton.setEnabled(true);
        this.noInsuranceBetButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void insuranceBetError() {
        this.setRoundInformationInsuranceLabel("ERROR");
        this.yesInsuranceBetButton.setEnabled(true);
        this.yesInsuranceBetButton.setVisible(true);
        this.noInsuranceBetButton.setEnabled(true);
        this.noInsuranceBetButton.setVisible(true);
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
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setBeforeTurnWaiting(Boolean waiting) {
        this.beforeTurnWaitingLabel.setVisible(waiting);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private JPanel turnPanel;
    private JLabel dealerCardsTurnLabel;
    private JList<String> dealerCardsTurnList;
    private JLabel dealerHandValueLabel;
    private JLabel playerCardsTurnLabel;
    private JPanel blackjackHandsPanel;
    private JLabel turnMoneyLabel;
    private JLabel turnBlackjackLabel;
    private JLabel afterTurnWaitingLabel;

    private void createTurnPanel() {
        this.turnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.dealerCardsTurnLabel = new JLabel("Dealer's Cards:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.turnPanel.add(this.dealerCardsTurnLabel, constraints);
        this.dealerCardsTurnList = new JList<>(dealerListModel);
        constraints.gridy = 1;
        this.turnPanel.add(this.dealerCardsTurnList, constraints);
        this.dealerHandValueLabel = new JLabel();
        constraints.gridy = 2;
        this.turnPanel.add(this.dealerHandValueLabel, constraints);
        this.playerCardsTurnLabel = new JLabel("Your Cards:");
        constraints.gridy = 3;
        this.turnPanel.add(this.playerCardsTurnLabel, constraints);
        this.blackjackHandsPanel = new JPanel();
        constraints.gridy = 4;
        this.turnPanel.add(this.blackjackHandsPanel, constraints);
        this.turnMoneyLabel = new JLabel();
        constraints.gridy = 5;
        this.turnPanel.add(this.turnMoneyLabel, constraints);
        this.turnBlackjackLabel = new JLabel();
        constraints.gridy = 6;
        this.turnPanel.add(this.turnBlackjackLabel, constraints);
        this.afterTurnWaitingLabel = new JLabel("Waiting for other players to take their turns.");
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

    public void addBlackjackHandPanel(BlackjackHandPanel blackjackHandPanel, int index) {
        this.blackjackHandsPanel.add(blackjackHandPanel, index);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void removeBlackjackHandPanel(BlackjackHandPanel blackjackHandPanel) {
        this.blackjackHandsPanel.remove(blackjackHandPanel);
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
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    private JPanel continuePlayingPanel;
    private JLabel continuePlayingMessageLabel;
    private JButton yesContinuePlayingButton;
    private JButton noContinuePlayingButton;
    private JLabel continuePlayingMoneyLabel;
    private JLabel continuePlayingWaitingLabel;

    private void createContinuePlayingPanel() {
        this.continuePlayingPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.continuePlayingMessageLabel = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.continuePlayingPanel.add(this.continuePlayingMessageLabel, constraints);
        this.yesContinuePlayingButton = new JButton("Yes");
        this.yesContinuePlayingButton.setEnabled(false);
        this.yesContinuePlayingButton.setVisible(false);
        constraints.gridy = 1;
        this.continuePlayingPanel.add(this.yesContinuePlayingButton, constraints);
        this.noContinuePlayingButton = new JButton("No");
        this.noContinuePlayingButton.setEnabled(false);
        this.noContinuePlayingButton.setVisible(false);
        constraints.gridx = 1;
        this.continuePlayingPanel.add(this.noContinuePlayingButton, constraints);
        this.continuePlayingMoneyLabel = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 2;
        this.continuePlayingPanel.add(this.continuePlayingMoneyLabel, constraints);
        this.continuePlayingWaitingLabel = new JLabel("Waiting for other players to join.");
        this.continuePlayingWaitingLabel.setVisible(false);
        constraints.gridy = 3;
        this.continuePlayingPanel.add(this.continuePlayingWaitingLabel, constraints);
        add(this.continuePlayingPanel, PanelNames.CONTINUEPLAYINGPANEL.toString());
    }

    public void enableContinuePlaying() {
        this.yesContinuePlayingButton.setEnabled(true);
        this.yesContinuePlayingButton.setVisible(true);
        this.noContinuePlayingButton.setEnabled(true);
        this.noContinuePlayingButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setContinuePlayingMoneyLabel(String money) {
        this.continuePlayingMoneyLabel.setText("$" + money);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setContinuePlayingMessageLabel(String message) {
        this.continuePlayingMessageLabel.setText(message);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void continuePlayingError() {
        this.setContinuePlayingMessageLabel("ERROR");
        this.yesContinuePlayingButton.setEnabled(true);
        this.yesContinuePlayingButton.setVisible(true);
        this.noContinuePlayingButton.setEnabled(true);
        this.yesContinuePlayingButton.setVisible(true);
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void gameOver() {
        this.setContinuePlayingMessageLabel("Thanks for playing!");
        this.validate();
        this.repaint();
        this.setVisible(true);
    }

    public void setContinuePlayingWaiting(Boolean waiting) {
        this.continuePlayingWaitingLabel.setVisible(waiting);
        this.validate();
        this.repaint();
        this.setVisible(true);
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

    public void showContinuePlayingPanel() {
        showPanel(PanelNames.CONTINUEPLAYINGPANEL);
    }

    public void reset() {
        this.setupPanels();
        this.setupActionListeners();
        this.showContinuePlayingPanel();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object target = actionEvent.getSource();

        if (target == this.betButton) {
            this.model.sendClientMessage(this.betField.getText());
            this.betButton.setEnabled(false);
            this.betButton.setVisible(false);
        } else if (target == this.yesInsuranceBetButton) {
            this.model.sendClientMessage(this.yesInsuranceBetButton.getText());
            this.yesInsuranceBetButton.setEnabled(false);
            this.yesInsuranceBetButton.setVisible(false);
            this.noInsuranceBetButton.setEnabled(false);
            this.noInsuranceBetButton.setVisible(false);
        } else if (target == this.noInsuranceBetButton) {
            this.model.sendClientMessage(this.noInsuranceBetButton.getText());
            this.yesInsuranceBetButton.setEnabled(false);
            this.yesInsuranceBetButton.setVisible(false);
            this.noInsuranceBetButton.setEnabled(false);
            this.noInsuranceBetButton.setVisible(false);
        } else if (target == this.yesContinuePlayingButton) {
            this.model.sendClientMessage(this.yesContinuePlayingButton.getText());
            this.yesContinuePlayingButton.setEnabled(false);
            this.yesContinuePlayingButton.setVisible(false);
            this.noContinuePlayingButton.setEnabled(false);
            this.noContinuePlayingButton.setVisible(false);
        } else if (target == this.noContinuePlayingButton) {
            this.model.sendClientMessage(this.noContinuePlayingButton.getText());
            this.yesContinuePlayingButton.setEnabled(false);
            this.yesContinuePlayingButton.setVisible(false);
            this.noContinuePlayingButton.setEnabled(false);
            this.noContinuePlayingButton.setVisible(false);
        }
    }
}