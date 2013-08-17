/**
 * 
 */
package org.mmarini.briscola;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

/**
 * @author US00852
 * 
 */
public class MockPlayerGameHandler extends GameHandler {

	private GameListener gameListener;
	private List<Card> playerCards;
	private boolean playerHand;
	private Card trump;
	private List<Card> deck;
	private Random random;
	private int playerScore;
	private int aIScore;
	private Card playerCard;
	private Card aICard;

	/**
	 * 
	 */
	public MockPlayerGameHandler() {
		playerCards = new ArrayList<>(3);
		playerHand = true;
		deck = new ArrayList<>(40);
		random = new Random();
	}

	/**
	 * 
	 */
	@Override
	public void closeHand() {
		aIScore = 10;
		playerScore = 10;
		aICard = null;
		playerCard = null;
	}

	/**
	 * 
	 */
	@Override
	public void deal() {
		for (Card c : Card.getDeck()) {
			deck.add(c);
		}
		int n = deck.size();
		for (int i = 0; i < 3; ++i) {
			int idx = random.nextInt(n);
			playerCards.add(deck.remove(idx));
			--n;
		}
		int idx = random.nextInt(n);
		trump = deck.get(idx);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public Card getAiCard() {
		return aICard;
	}

	/**
	 * 
	 * @return
	 */
	public int getAIScore() {
		return aIScore;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public int getDeckCount() {
		return deck.size();
	}

	/**
	 * @return the playerCard
	 */
	@Override
	public Card getPlayerCard() {
		return playerCard;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	@Override
	public Card getPlayerCard(int i) {
		return playerCards.get(i);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public List<Card> getPlayerCards() {
		return playerCards;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public int getPlayerScore() {
		return playerScore;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public Card getTrump() {
		return trump;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public boolean isPlayerHand() {
		return playerHand;
	}

	/**
	 * 
	 * @param card
	 */
	@Override
	public void play(Card card) {
		playerCards.remove(card);
		playerCard = card;
	}

	/**
	 * s
	 * 
	 * @param listener
	 */
	@Override
	public void setGameListener(GameListener listener) {
		gameListener = listener;
	}

	/**
	 * 
	 * @param seed
	 */
	@Override
	public void setSeed(long seed) {
		random.setSeed(seed);
	}

	/**
	 * 
	 */
	@Override
	public void think() {
		Timer timer = new Timer(5000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int idx = random.nextInt(deck.size());
				aICard = deck.remove(idx);
				gameListener.notifyCardPlayed(MockPlayerGameHandler.this);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

}
