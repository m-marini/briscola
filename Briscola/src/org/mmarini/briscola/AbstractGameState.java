/**
 * 
 */
package org.mmarini.briscola;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author US00852
 * 
 */
public abstract class AbstractGameState implements Cloneable {

	public static final int MAX_SCORE = 120;
	public static final int HALF_SCORE = MAX_SCORE / 2;

	/**
	 * 
	 * @param list
	 * @param cards
	 * @return
	 */
	protected static Card[] createAndRemove(Card[] list, Card... cards) {
		Card[] result = new Card[list.length - cards.length];
		int i = 0;
		for (Card c : list) {
			boolean valid = true;
			for (Card d : cards) {
				if (c.equals(d)) {
					valid = false;
					break;
				}
			}
			if (valid) {
				result[i++] = c;
			}
		}
		return result;
	}

	private List<Card> deckCards;
	private List<Card> aiCards;
	private Card trump;
	private int aiScore;
	private int playerScore;

	/**
	 * 
	 */
	protected AbstractGameState() {
		aiCards = new ArrayList<Card>(3);
		deckCards = new ArrayList<Card>(40);
	}

	/**
	 * 
	 */
	protected AbstractGameState(AbstractGameState state) {
		this();
		aiCards.addAll(state.aiCards);
		deckCards.addAll(state.deckCards);
		trump = state.trump;
		aiScore = state.aiScore;
		playerScore = state.playerScore;
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	protected void addToAiCards(Card card) {
		aiCards.add(card);
	}

	/**
	 * 
	 * @param cards
	 * @return
	 */
	protected void addToAiCards(Collection<Card> cards) {
		aiCards.addAll(cards);
	}

	/**
	 * 
	 * @param cards
	 */
	protected void addToDeckCards(Collection<Card> cards) {
		deckCards.addAll(cards);
	}

	/**
	 * 
	 */
	protected void clearDeck() {
		deckCards.clear();
	}

	/**
	 * 
	 * @param card1
	 * @param card2
	 * @return
	 */
	protected int computeScore(Card card1, Card card2) {
		return card1.getScore() + card2.getScore();
	}

	/**
	 * 
	 * @param estimation
	 * @param strategySearchContext
	 * @throws InterruptedException
	 */
	public abstract void estimate(Estimation estimation,
			StrategySearchContext strategySearchContext)
			throws InterruptedException;

	/**
	 * @return the playerCards
	 */
	protected List<Card> getAiCards() {
		return aiCards;
	}

	/**
	 * @return the playerScore
	 */
	public int getAiScore() {
		return aiScore;
	}

	/**
	 * @return the deckCards
	 */
	protected List<Card> getDeckCards() {
		return deckCards;
	}

	/**
	 * @return the oppositeScore
	 */
	public int getPlayerScore() {
		return playerScore;
	}

	/**
	 * @return the briscola
	 */
	protected Card getTrump() {
		return trump;
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	protected void removeFromAiCards(Card card) {
		aiCards.remove(card);
	}

	/**
	 * 
	 * @param card
	 */
	protected void removeFromDeckCards(Card card) {
		deckCards.remove(card);
	}

	/**
	 * @param playerScore
	 *            the playerScore to set
	 */
	protected void setAiScore(int playerScore) {
		this.aiScore = playerScore;
	}

	/**
	 * @param oppositeScore
	 *            the oppositeScore to set
	 */
	protected void setPlayerScore(int oppositeScore) {
		this.playerScore = oppositeScore;
	}

	/**
	 * @param briscola
	 */
	public void setTrump(Card briscola) {
		this.trump = briscola;
	}
}
