/**
 * 
 */
package org.mmarini.briscola;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author us00852
 * 
 */
public abstract class AbstractVirtualGameState extends AbstractGameState {

	private static final int MAX_TRUMP_LEVEL = 10 + 9 + 8;

	/**
	 * 
	 * @param a
	 * @param min
	 * @param max
	 * @return
	 */
	private static double between(double a, double min, double max) {
		return Math.min(Math.max((a - min) / (max - min), 0), 1);
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static double more(double a, double b) {
		return Math.min(1, a + b);
	}

	private List<Card> playerCards;

	/**
	 * 
	 */
	protected AbstractVirtualGameState() {
		playerCards = new ArrayList<Card>(3);
	}

	/**
	 * 
	 */
	protected AbstractVirtualGameState(AbstractGameState state) {
		super(state);
		playerCards = new ArrayList<Card>(3);
	}

	/**
	 * 
	 */
	protected AbstractVirtualGameState(AbstractVirtualGameState state) {
		this((AbstractGameState) state);
		playerCards.addAll(state.playerCards);
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	protected void addToPlayerCards(Card card) {
		playerCards.add(card);
	}

	/**
	 * 
	 * @param cards
	 */
	protected void addToPlayerCards(Collection<Card> cards) {
		playerCards.addAll(cards);
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	private int computeTrumpLevel(List<Card> list) {
		int score = 0;
		for (Card c : list) {
			if (c.hasSameSeed(getTrump())) {
				score += c.getFigure().ordinal() + 1;
			}
		}
		return score;
	}

	/**
	 * 
	 * @param estimation
	 * @param ctx
	 */
	protected void estimateEmpiric(Estimation estimation,
			StrategySearchContext ctx) {
		/*
		 * La stima deve basarsi sui punti accumulati dai due giocatori, i punti
		 * in mano ai giocatori, le briscole in mano ai giocatori, i punti
		 * rimasti in gioco e le carte briscole in gioco e la carta di briscola
		 */
		double aiScore = between(getAiScore(), 0, 61);
		double playerScore = between(getPlayerScore(), 0, 61);
		double aiTrumps = between(computeTrumpLevel(getAiCards()), 0,
				MAX_TRUMP_LEVEL) / 62;
		double playerTrumps = between(computeTrumpLevel(getPlayerCards()), 0,
				MAX_TRUMP_LEVEL) / 62;

		double aiWinProb = more(aiScore, aiTrumps);
		double playerWinProb = more(playerScore, playerTrumps);
		estimation.setConfident(false);
		estimation.setAiWinProb(aiWinProb);
		estimation.setPlayerWinProb(playerWinProb);
	}

	/**
	 * @return the oppositeCards
	 */
	protected List<Card> getPlayerCards() {
		return playerCards;
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	protected void removeFromPlayerCards(Card card) {
		playerCards.remove(card);
	}
}
