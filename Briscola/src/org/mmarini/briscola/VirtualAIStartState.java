/**
 * 
 */
package org.mmarini.briscola;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Game state in case the player moves.
 * <p>
 * The game is in the final state where both the player knows the opposite
 * cards. The strategy can be completed defined.
 * </p>
 * 
 * @author US00852
 * 
 */
public class VirtualAIStartState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory
			.getLogger(VirtualAIStartState.class);

	/**
	 * 
	 */
	public VirtualAIStartState() {
	}

	/**
	 * 
	 * @param card
	 * @param opposite
	 * @return
	 */
	private AbstractVirtualGameState createState(Card card, Card opposite) {
		Card[] playerCards = createAndRemove(getPlayerCards(), card);
		Card[] oppositeCards = createAndRemove(getOppositeCards(), opposite);
		Card trump = getTrump();
		boolean winner = card.wins(opposite, trump);
		int score = computeScore(card, opposite);
		int playerScore = getPlayerScore();
		int oppositeScore = getOppositeScore();
		AbstractVirtualGameState state = null;
		if (winner) {
			playerScore += score;
			state = new VirtualAIEndState();
		} else {
			oppositeScore += score;
			state = new VirtualOppositeEndState();
		}
		state.setTrump(trump);
		state.setDeckCards(getDeckCards());
		state.setPlayerCards(playerCards);
		state.setPlayerScore(playerScore);
		state.setOppositeCards(oppositeCards);
		state.setOppositeScore(oppositeScore);
		return state;
	}

	/**
	 * @see org.mmarini.briscola.AbstractGameState#estimate(org.mmarini.briscola.
	 *      Estimation, org.mmarini.briscola.StrategySearchContext)
	 */
	@Override
	public void estimate(Estimation estimation, StrategySearchContext ctx)
			throws InterruptedException {
		estimation.setConfident(true);
		estimation.setWin(0.);
		estimation.setLoss(0.);
		int score = getPlayerScore();
		if (score > HALF_SCORE) {
			estimation.setWin(1.);
			return;
		}
		int oppositeScore = getOppositeScore();
		if (oppositeScore > HALF_SCORE) {
			estimation.setLoss(1.);
			return;
		}
		/*
		 * Per ogni carta giocata prende la risposta con stima di perdita più
		 * alta e seleziona quella con probabilità di vincita migliore.
		 */
		boolean confident = false;
		double wpp = Double.NEGATIVE_INFINITY;
		double lpp = 0;
		Card bestCard = null;
		Card opBest = null;
		for (Card card : getPlayerCards()) {
			double wop = 0;
			double lop = Double.NEGATIVE_INFINITY;
			boolean oppositeConfident = false;
			Card best = null;
			for (Card opposite : getOppositeCards()) {
				AbstractVirtualGameState state = createState(card, opposite);
				state.estimate(estimation, ctx);
				double pl = estimation.getLoss();
				double pw = estimation.getWin();
				if (pl > lop || (pl == lop && pw < wop)) {
					lop = pl;
					wop = pw;
					oppositeConfident = estimation.isConfident();
					best = opposite;
				}
			}
			if (wop > wpp || (wop == wpp && lop < lpp)) {
				wpp = wop;
				lpp = lop;
				confident = oppositeConfident;
				bestCard = card;
				opBest = best;
			}
		}
		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setLoss(lpp);
		estimation.setWin(wpp);
		logger.debug("{} vs {} = {}", bestCard, opBest, estimation);
	}
}
