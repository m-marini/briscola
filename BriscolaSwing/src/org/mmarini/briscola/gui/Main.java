/**
 * 
 */
package org.mmarini.briscola.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.mmarini.briscola.Card;
import org.mmarini.briscola.GameHandler;
import org.mmarini.briscola.GameListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author US00852
 * 
 */
public class Main extends JFrame implements GameListener {

	private static final long serialVersionUID = -4312307693703220205L;

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}

	private GameHandler handler;
	private AbstractAction dealAction;
	private AbstractAction closeAction;
	private AbstractAction stopAction;
	private JLabel playerCard;
	private JLabel aiCard;
	private JLabel trumpCard;
	private JFormattedTextField deckCount;
	private JFormattedTextField playerScore;
	private JFormattedTextField aiScore;
	private JFormattedTextField playerWonGame;
	private JFormattedTextField aiWonGame;
	private JFormattedTextField aiWinProb;
	private JFormattedTextField aiLossProb;
	private JCheckBox confident;
	private JButton[] playerCards;
	private JButton continueButton;
	private Map<Card, ImageIcon> cardSuitMap;
	private ImageIcon retro;
	private JFormattedTextField thinkLevel;

	private JProgressBar progressBar;

	/**
	 * @throws HeadlessException
	 */
	public Main() throws HeadlessException {
		handler = new GameHandler();
		handler.setSeed(1);
		playerCard = new JLabel();
		aiCard = new JLabel();
		trumpCard = new JLabel();
		deckCount = new JFormattedTextField(NumberFormat.getIntegerInstance());
		playerScore = new JFormattedTextField(NumberFormat.getIntegerInstance());
		aiScore = new JFormattedTextField(NumberFormat.getIntegerInstance());
		playerWonGame = new JFormattedTextField(
				NumberFormat.getIntegerInstance());
		aiWonGame = new JFormattedTextField(NumberFormat.getIntegerInstance());
		aiWinProb = new JFormattedTextField(NumberFormat.getPercentInstance());
		aiLossProb = new JFormattedTextField(NumberFormat.getPercentInstance());
		thinkLevel = new JFormattedTextField(NumberFormat.getIntegerInstance());
		progressBar = new JProgressBar();
		confident = new JCheckBox();
		continueButton = new JButton();
		playerCards = new JButton[3];
		for (int i = 0; i < playerCards.length; ++i) {
			playerCards[i] = new JButton();
		}

		CardSuitFactory cardSuitFactory = new CardSuitFactory();
		cardSuitMap = cardSuitFactory.createMap();
		retro = cardSuitFactory.createRetro();
		createActions();

		handler.setGameListener(this);

		deckCount.setEditable(false);
		deckCount.setColumns(3);
		deckCount.setHorizontalAlignment(SwingConstants.CENTER);

		playerScore.setEditable(false);
		playerScore.setColumns(4);
		playerScore.setHorizontalAlignment(SwingConstants.CENTER);

		aiScore.setEditable(false);
		aiScore.setColumns(4);
		aiScore.setHorizontalAlignment(SwingConstants.CENTER);

		playerWonGame.setEditable(false);
		playerWonGame.setColumns(4);
		playerWonGame.setHorizontalAlignment(SwingConstants.CENTER);

		aiWonGame.setEditable(false);
		aiWonGame.setColumns(4);
		aiWonGame.setHorizontalAlignment(SwingConstants.CENTER);

		aiWinProb.setEditable(false);
		aiWinProb.setColumns(4);
		aiWinProb.setHorizontalAlignment(SwingConstants.CENTER);

		aiLossProb.setEditable(false);
		aiLossProb.setColumns(4);
		aiLossProb.setHorizontalAlignment(SwingConstants.CENTER);

		thinkLevel.setEditable(false);
		thinkLevel.setColumns(4);
		thinkLevel.setHorizontalAlignment(SwingConstants.CENTER);

		confident.setEnabled(false);
		confident.setText("Confidente");

		createContent();

		disableButtons();
		dealAction.setEnabled(true);
		handler.setTimeout(30000);

		setTitle("Briscola");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);
	}

	/**
	 * 
	 * @param button
	 * @param card
	 */
	private void applyText(JButton button, Card card) {
		if (card != null) {
			button.setIcon(cardSuitMap.get(card));
		} else {
			button.setIcon(null);
			button.setText("-");
		}
	}

	/**
	 * 
	 * @param field
	 * @param card
	 */
	private void applyText(JLabel field, Card card) {
		if (card != null) {
			field.setIcon(cardSuitMap.get(card));
		} else {
			field.setIcon(null);
		}
	}

	/**
	 * 
	 */
	private void closeHand() {
		logger.debug("Close hand");
		handler.closeHand();
		refresh();
		continueButton.setAction(dealAction);
		dealAction.setEnabled(true);
	}

	/**
	 * 
	 */
	private void createActions() {
		playerCards[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playCard(0);
			}
		});
		playerCards[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playCard(1);
			}
		});
		playerCards[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playCard(2);
			}
		});
		dealAction = new AbstractAction() {
			private static final long serialVersionUID = 9176873850943151834L;

			@Override
			public void actionPerformed(ActionEvent e) {
				deal();
			}
		};
		stopAction = new AbstractAction() {
			private static final long serialVersionUID = 9176873850943151834L;

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		};
		closeAction = new AbstractAction() {
			private static final long serialVersionUID = -5218821693988834943L;

			@Override
			public void actionPerformed(ActionEvent e) {
				closeHand();
			}
		};
		dealAction.putValue(Action.NAME, "Dai carte");
		closeAction.putValue(Action.NAME, "Chiudi");
		stopAction.putValue(Action.NAME, "Stop analisi");
		continueButton.setAction(dealAction);
	}

	/**
	 * 
	 */
	private void createContent() {
		Container p = getContentPane();
		p.setLayout(new BorderLayout());
		JComponent deskPane = createDesk();
		p.add(deskPane, BorderLayout.CENTER);
	}

	/**
	 * 
	 * @return
	 */
	private JComponent createDesk() {
		JPanel p1 = createPanel1();
		JPanel p2 = createPanel2();
		JPanel p0 = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		p0.setLayout(gbl);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(p1, gbc);
		p0.add(p1);

		gbc.weightx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(p2, gbc);
		p0.add(p2);

		return p0;
	}

	/**
	 * 
	 * @return
	 */
	private JPanel createPanel1() {
		JPanel p1 = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.weightx = 1;

		p1.setLayout(gbl);

		Component comp = new JLabel("Briscola");
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbl.setConstraints(comp, gbc);
		p1.add(comp);

		comp = new JLabel("Carta avversario");
		gbl.setConstraints(comp, gbc);
		p1.add(comp);

		comp = new JLabel("Carta giocatore");
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(comp, gbc);
		p1.add(comp);

		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(trumpCard, gbc);
		p1.add(trumpCard);

		gbl.setConstraints(aiCard, gbc);
		p1.add(aiCard);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(playerCard, gbc);
		p1.add(playerCard);

		gbc.gridwidth = 1;
		gbl.setConstraints(playerCards[0], gbc);
		p1.add(playerCards[0]);

		gbl.setConstraints(playerCards[1], gbc);
		p1.add(playerCards[1]);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl.setConstraints(playerCards[2], gbc);
		p1.add(playerCards[2]);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(progressBar, gbc);
		p1.add(progressBar);
		return p1;
	}

	/**
	 * 
	 * @return
	 */
	private JPanel createPanel2() {
		JPanel p2 = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		p2.setLayout(gbl);
		p2.setBorder(BorderFactory.createEtchedBorder());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weighty = 1;
		gbc.insets = new Insets(2, 2, 2, 2);

		JLabel comp = new JLabel("Partite vinte");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(playerWonGame, gbc);
		p2.add(playerWonGame);

		comp = new JLabel("Partite perse");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(aiWonGame, gbc);
		p2.add(aiWonGame);

		comp = new JLabel("Punti giocatore");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(playerScore, gbc);
		p2.add(playerScore);

		comp = new JLabel("Punti avversario");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(aiScore, gbc);
		p2.add(aiScore);

		comp = new JLabel("Carte nel mazzo");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(deckCount, gbc);
		p2.add(deckCount);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(confident, gbc);
		p2.add(confident);

		comp = new JLabel("Livello di analisi");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(thinkLevel, gbc);
		p2.add(thinkLevel);

		comp = new JLabel("Vincita");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(aiLossProb, gbc);
		p2.add(aiLossProb);

		comp = new JLabel("Perdita");
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(comp, gbc);
		p2.add(comp);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(aiWinProb, gbc);
		p2.add(aiWinProb);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbl.setConstraints(continueButton, gbc);
		p2.add(continueButton);

		return p2;
	}

	/**
	 * 
	 */
	private void deal() {
		logger.debug("Deal");
		disableButtons();
		handler.deal();
		refresh();
		if (handler.isPlayerHand()) {
			enableCardButtons();
		} else {
			handler.think();
			progressBar.setIndeterminate(true);
			continueButton.setAction(stopAction);
			stopAction.setEnabled(true);
		}
	}

	/**
	 * 
	 */
	private void disableButtons() {
		dealAction.setEnabled(false);
		closeAction.setEnabled(false);
		stopAction.setEnabled(false);
		for (JButton b : playerCards) {
			b.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	private void enableCardButtons() {
		int n = handler.getPlayerCards().size();
		for (JButton b : playerCards) {
			if (n > 0)
				b.setEnabled(true);
			else
				b.setEnabled(false);
			--n;
		}
	}

	@Override
	public void notifyAnalysis(GameHandler handler) {
		aiWinProb.setValue(handler.getAiWinProbability());
		aiLossProb.setValue(handler.getAiLossProbability());
		confident.setSelected(handler.isConfident());
		thinkLevel.setValue(handler.getLevel());
	}

	/**
	 * @see org.mmarini.briscola.GameListener#notifyCardPlayed(org.mmarini.briscola
	 *      .GameHandler)
	 */
	@Override
	public void notifyCardPlayed(GameHandler handler) {
		logger.debug("AI plays {}", handler.getAiCard());
		stopAction.setEnabled(false);
		progressBar.setIndeterminate(false);
		refresh();
		if (handler.isPlayerHand()) {
			continueButton.setAction(closeAction);
			closeAction.setEnabled(true);
		} else {
			enableCardButtons();
		}
	}

	/**
	 * 
	 * @param i
	 */
	private void playCard(int i) {
		Card c = handler.getPlayerCard(i);
		logger.debug("Player plays {}", c);
		handler.play(c);
		refresh();
		disableButtons();
		if (handler.isPlayerHand()) {
			handler.think();
			progressBar.setIndeterminate(true);
			continueButton.setAction(stopAction);
			stopAction.setEnabled(true);
		} else {
			continueButton.setAction(closeAction);
			closeAction.setEnabled(true);
		}
	}

	/**
	 * 
	 */
	private void refresh() {
		playerScore.setValue(handler.getPlayerScore());
		aiScore.setValue(handler.getAiScore());

		deckCount.setValue(handler.getDeckCount());

		playerWonGame.setValue(handler.getPlayerWonGame());
		aiWonGame.setValue(handler.getAiWonGame());

		applyText(trumpCard, handler.getTrump());
		applyText(playerCard, handler.getPlayerCard());
		applyText(aiCard, handler.getAiCard());

		aiWinProb.setValue(handler.getAiWinProbability());
		aiLossProb.setValue(handler.getAiLossProbability());
		confident.setSelected(handler.isConfident());
		thinkLevel.setValue(handler.getLevel());

		List<Card> cards = handler.getPlayerCards();
		int idx = 0;
		for (JButton b : playerCards) {
			if (idx >= cards.size()) {
				applyText(b, null);
			} else {
				applyText(b, cards.get(idx));
			}
			++idx;
		}
	}

	/**
	 * 
	 */
	private void stop() {
		handler.stopAnalysis();
	}
}
