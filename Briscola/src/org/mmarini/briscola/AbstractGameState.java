/**
 * 
 */
package org.mmarini.briscola;

import java.util.Arrays;

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
	protected static Card[] createAndAdd(Card[] list, Card... cards) {
		Card[] result = Arrays.copyOf(list, list.length + cards.length);
		System.arraycopy(cards, 0, result, list.length, cards.length);
		return result;
	}

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

	/**
	 * 
	 * @param list
	 * @param cards
	 * @return
	 */
	protected static int[] createAndReplace(int[] list, int card, int newCard) {
		int[] result = new int[list.length];
		for (int i = 0; i < list.length; i++) {
			if (result[i] == card) {
				result[i] = newCard;
			} else {
				result[i] = list[i];
			}
		}
		return result;
	}

	private Card[] aiCards;
	private Card trump;
	private Card[] deckCards;
	private int aiScore;
	private int playerScore;

	/**
	 * 
	 */
	protected AbstractGameState() {
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
	 * @return the deckCards
	 */
	protected Card[] getDeckCards() {
		return deckCards;
	}

	/**
	 * @return the oppositeScore
	 */
	public int getPlayerScore() {
		return playerScore;
	}

	/**
	 * @return the playerCards
	 */
	protected Card[] getAiCards() {
		return aiCards;
	}

	/**
	 * @return the playerScore
	 */
	public int getAiScore() {
		return aiScore;
	}

	/**
	 * @return the briscola
	 */
	protected Card getTrump() {
		return trump;
	}

	/**
	 * @param deckCards
	 *            the deckCards to set
	 */
	public void setDeckCards(Card... deckCards) {
		this.deckCards = deckCards;
	}

	/**
	 * @param oppositeScore
	 *            the oppositeScore to set
	 */
	protected void setPlayerScore(int oppositeScore) {
		this.playerScore = oppositeScore;
	}

	/**
	 * @param playerCards
	 *            the playerCards to set
	 */
	public void setAiCards(Card... playerCards) {
		this.aiCards = playerCards;
	}

	/**
	 * @param playerScore
	 *            the playerScore to set
	 */
	protected void setAiScore(int playerScore) {
		this.aiScore = playerScore;
	}

	/**
	 * @param briscola
	 */
	public void setTrump(Card briscola) {
		this.trump = briscola;
	}
}
