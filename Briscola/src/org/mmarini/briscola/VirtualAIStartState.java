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
	 * @param aiCard
	 * @param playerCard
	 * @return
	 */
	private AbstractVirtualGameState createState(Card aiCard, Card playerCard) {
		Card[] aiCards = createAndRemove(getAiCards(), aiCard);
		Card[] playerCards = createAndRemove(getPlayerCards(), playerCard);
		Card trump = getTrump();
		boolean aiWinner = aiCard.wins(playerCard, trump);
		int score = computeScore(aiCard, playerCard);
		int aiScore = getAiScore();
		int playerScore = getPlayerScore();
		AbstractVirtualGameState state = null;
		if (aiWinner) {
			aiScore += score;
			state = new VirtualAIEndState();
		} else {
			playerScore += score;
			state = new VirtualPlayerEndState();
		}
		state.setTrump(trump);
		state.setDeckCards(getDeckCards());
		state.setAiCards(aiCards);
		state.setAiScore(aiScore);
		state.setPlayerCards(playerCards);
		state.setPlayerScore(playerScore);
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
		estimation.setAiWinProb(0.);
		estimation.setPlayerWinProb(0.);
		int aiScore = getAiScore();
		if (aiScore > HALF_SCORE) {
			estimation.setAiWinProb(1.);
			return;
		}
		int playerScore = getPlayerScore();
		if (playerScore > HALF_SCORE) {
			estimation.setPlayerWinProb(1.);
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
		for (Card aiCard : getAiCards()) {
			double wop = 0;
			double lop = Double.NEGATIVE_INFINITY;
			boolean playerConfident = false;
			Card best = null;
			for (Card playerCard : getPlayerCards()) {
				AbstractVirtualGameState state = createState(aiCard, playerCard);
				state.estimate(estimation, ctx);
				double pl = estimation.getPlayerWinProb();
				double pw = estimation.getAiWinProb();
				if (pl > lop || (pl == lop && pw < wop)) {
					lop = pl;
					wop = pw;
					playerConfident = estimation.isConfident();
					best = playerCard;
				}
			}
			if (wop > wpp || (wop == wpp && lop < lpp)) {
				wpp = wop;
				lpp = lop;
				confident = playerConfident;
				bestCard = aiCard;
				opBest = best;
			}
		}
		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setPlayerWinProb(lpp);
		estimation.setAiWinProb(wpp);
		logger.debug("{} vs {} = {}", bestCard, opBest, estimation);
	}
}
