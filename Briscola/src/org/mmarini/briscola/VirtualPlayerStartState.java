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
public class VirtualPlayerStartState extends AbstractVirtualGameState {

	private static Logger logger = LoggerFactory
			.getLogger(VirtualPlayerStartState.class);

	/**
	 * 
	 */
	public VirtualPlayerStartState() {
	}

	/**
	 * 
	 * @param playerCard
	 * @param aiCard
	 * @return
	 */
	private AbstractVirtualGameState createState(Card playerCard, Card aiCard) {
		Card[] aiCards = createAndRemove(getAiCards(), aiCard);
		Card[] playerCards = createAndRemove(getPlayerCards(), playerCard);
		Card trump = getTrump();
		boolean aiWinner = !playerCard.wins(aiCard, trump);
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
		int plazerScore = getPlayerScore();
		if (plazerScore > HALF_SCORE) {
			estimation.setPlayerWinProb(1.);
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
		Card aiBest = null;
		for (Card playerCard : getPlayerCards()) {
			double wop = Double.NEGATIVE_INFINITY;
			double lop = Double.NEGATIVE_INFINITY;
			boolean aiConfident = false;
			Card best = null;
			for (Card aiCard : getAiCards()) {
				AbstractVirtualGameState state = createState(playerCard, aiCard);
				state.estimate(estimation, ctx);
				double pl = estimation.getPlayerWinProb();
				double pw = estimation.getAiWinProb();
				if (pw > wop || (pw == wop && pl < lop)) {
					lop = pl;
					wop = pw;
					aiConfident = estimation.isConfident();
					best = aiCard;
				}
			}
			if (lop > lpp || (lop == lpp && wop < wpp)) {
				wpp = wop;
				lpp = lop;
				confident = aiConfident;
				bestCard = playerCard;
				aiBest = best;
			}
		}
		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setPlayerWinProb(lpp);
		estimation.setAiWinProb(wpp);
		logger.debug("{} vs {} = {}", bestCard, aiBest, estimation);
	}
}
