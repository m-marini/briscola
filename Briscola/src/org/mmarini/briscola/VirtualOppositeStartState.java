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
public class VirtualOppositeStartState extends AbstractVirtualGameState {

	private static Logger logger = LoggerFactory
			.getLogger(VirtualOppositeStartState.class);

	/**
	 * 
	 */
	public VirtualOppositeStartState() {
	}

	/**
	 * 
	 * @param opposite
	 * @param card
	 * @return
	 */
	private AbstractVirtualGameState createState(Card opposite, Card card) {
		Card[] playerCards = createAndRemove(getPlayerCards(), card);
		Card[] oppositeCards = createAndRemove(getOppositeCards(), opposite);
		Card trump = getTrump();
		boolean winner = !opposite.wins(card, trump);
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
		double lpp = Double.NEGATIVE_INFINITY;
		Card bestCard = null;
		Card playerBest = null;
		for (Card opposite : getOppositeCards()) {
			double wop = Double.NEGATIVE_INFINITY;
			double lop = Double.NEGATIVE_INFINITY;
			boolean playerConfident = false;
			Card best = null;
			for (Card card : getPlayerCards()) {
				AbstractVirtualGameState state = createState(opposite, card);
				state.estimate(estimation, ctx);
				double pl = estimation.getLoss();
				double pw = estimation.getWin();
				if (pw > wop || (pw == wop && pl < lop)) {
					lop = pl;
					wop = pw;
					playerConfident = estimation.isConfident();
					best = card;
				}
			}
			if (lop > lpp || (lop == lpp && wop < wpp)) {
				wpp = wop;
				lpp = lop;
				confident = playerConfident;
				bestCard = opposite;
				playerBest = best;
			}
		}
		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setLoss(lpp);
		estimation.setWin(wpp);
		logger.debug("{} vs {} = {}", bestCard, playerBest, estimation);
	}
}
