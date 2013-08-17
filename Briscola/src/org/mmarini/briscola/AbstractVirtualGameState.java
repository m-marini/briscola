/**
 * 
 */
package org.mmarini.briscola;

/**
 * @author us00852
 * 
 */
public abstract class AbstractVirtualGameState extends AbstractGameState {

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

		double win = playerScore;
		double loss = oppositeScore;
		estimation.setConfident(false);
		estimation.setWin(win);
		estimation.setLoss(loss);
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
