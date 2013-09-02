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
public class VirtualplayerMidState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory
			.getLogger(VirtualplayerMidState.class);

	private Card playedCard;

	/**
	 * 
	 */
	public VirtualplayerMidState() {
	}

	/**
	 * 
	 * @param playerCard
	 * @param aiCard
	 * @return
	 */
	private AbstractVirtualGameState createState(Card playerCard, Card aiCard) {
		Card[] aiCards = createAndRemove(getAiCards(), aiCard);
		boolean aiWinner = !playerCard.wins(aiCard, getTrump());
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
		state.setTrump(getTrump());
		state.setDeckCards(getDeckCards());
		state.setAiCards(aiCards);
		state.setAiScore(aiScore);
		state.setPlayerCards(getPlayerCards());
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
		int score = getAiScore();
		if (score > HALF_SCORE) {
			estimation.setAiWinProb(1.);
			return;
		}
		int oppositeScore = getPlayerScore();
		if (oppositeScore > HALF_SCORE) {
			estimation.setPlayerWinProb(1.);
			return;
		}

		boolean confident = false;
		double wpp = Double.NEGATIVE_INFINITY;
		double lpp = Double.NEGATIVE_INFINITY;
		Card bestCard = null;
		for (Card card : getAiCards()) {
			AbstractVirtualGameState state = createState(playedCard, card);
			state.estimate(estimation, ctx);
			double pl = estimation.getPlayerWinProb();
			double pw = estimation.getAiWinProb();
			if (pw > wpp || (pw == wpp && pl < lpp)) {
				lpp = pl;
				wpp = pw;
				confident = estimation.isConfident();
				bestCard = card;
			}
		}
		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setPlayerWinProb(lpp);
		estimation.setAiWinProb(wpp);
		logger.debug("{} vs {} = {}", playedCard, bestCard, estimation);
	}

	/**
	 * @param playedCard
	 *            the playedCard to set
	 */
	public void setPlayedCard(Card playedCard) {
		this.playedCard = playedCard;
	}
}
