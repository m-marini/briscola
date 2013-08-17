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
public class VirtualOppositeMidState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory
			.getLogger(VirtualOppositeMidState.class);

	private Card playedCard;

	/**
	 * 
	 */
	public VirtualOppositeMidState() {
	}

	/**
	 * 
	 * @param opposite
	 * @param card
	 * @return
	 */
	private AbstractVirtualGameState createState(Card opposite, Card card) {
		Card[] playerCards = createAndRemove(getPlayerCards(), card);
		boolean winner = !opposite.wins(card, getTrump());
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
		state.setTrump(getTrump());
		state.setDeckCards(getDeckCards());
		state.setPlayerCards(playerCards);
		state.setPlayerScore(playerScore);
		state.setOppositeCards(getOppositeCards());
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

		boolean confident = false;
		double wpp = Double.NEGATIVE_INFINITY;
		double lpp = Double.NEGATIVE_INFINITY;
		Card bestCard = null;
		for (Card card : getPlayerCards()) {
			AbstractVirtualGameState state = createState(playedCard, card);
			state.estimate(estimation, ctx);
			double pl = estimation.getLoss();
			double pw = estimation.getWin();
			if (pw > wpp || (pw == wpp && pl < lpp)) {
				lpp = pl;
				wpp = pw;
				confident = estimation.isConfident();
				bestCard = card;
			}
		}
		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setLoss(lpp);
		estimation.setWin(wpp);
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
