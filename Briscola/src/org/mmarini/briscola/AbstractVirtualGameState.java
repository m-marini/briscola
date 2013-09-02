/**
 * 
 */
package org.mmarini.briscola;

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

	private Card[] oppositeCards;

	/**
	 * 
	 */
	protected AbstractVirtualGameState() {
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
		double playerScore = between(getPlayerScore(), 0, 61);
		double oppositeScore = between(getOppositeScore(), 0, 61);
		double playerTrumps = between(computeTrumpLevel(getPlayerCards()), 0,
				MAX_TRUMP_LEVEL) / 62;
		double oppositeTrumps = between(computeTrumpLevel(getOppositeCards()),
				0, MAX_TRUMP_LEVEL) / 62;

		double win = more(playerScore, playerTrumps);
		double loss = more(oppositeScore, oppositeTrumps);
		estimation.setConfident(false);
		estimation.setWin(win);
		estimation.setLoss(loss);
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

	/**
	 * 
	 * @param cards
	 * @return
	 */
	private int computeTrumpLevel(Card[] cards) {
		int score = 0;
		for (Card c : cards) {
			if (c.hasSameSeed(getTrump())) {
				score += c.getFigure().ordinal() + 1;
			}
		}
		return score;
	}

	/**
	 * @return the oppositeCards
	 */
	protected Card[] getOppositeCards() {
		return oppositeCards;
	}

	/**
	 * @param oppositeCards
	 *            the oppositeCards to set
	 */
	protected void setOppositeCards(Card... oppositeCards) {
		this.oppositeCards = oppositeCards;
	}
}
