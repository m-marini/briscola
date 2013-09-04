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
public class VirtualPlayerMidState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory
			.getLogger(VirtualPlayerMidState.class);

	private Card playedCard;

	/**
	 * 
	 */
	public VirtualPlayerMidState() {
	}

	/**
	 * @param state
	 */
	public VirtualPlayerMidState(AbstractGameState state) {
		super(state);
	}

	/**
	 * 
	 * @param playerCard
	 * @param aiCard
	 * @return
	 */
	private AbstractVirtualGameState createState(Card playerCard, Card aiCard) {
		boolean aiWinner = !playerCard.wins(aiCard, getTrump());
		int score = computeScore(aiCard, playerCard);
		int aiScore = getAiScore();
		int playerScore = getPlayerScore();
		AbstractVirtualGameState state = null;
		if (aiWinner) {
			aiScore += score;
			state = new VirtualAIEndState(this);
		} else {
			playerScore += score;
			state = new VirtualPlayerEndState(this);
		}
		state.removeFromAiCards(aiCard);
		state.setAiScore(aiScore);
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
		double bestAiWinProb = Double.NEGATIVE_INFINITY;
		double bestPlayerWinProb = Double.NEGATIVE_INFINITY;
		Card bestAiCard = null;
		for (Card aiCard : getAiCards()) {
			AbstractVirtualGameState state = createState(playedCard, aiCard);
			state.estimate(estimation, ctx);
			double playerWinProb = estimation.getPlayerWinProb();
			double aiWinProb = estimation.getAiWinProb();
			if (aiWinProb > bestAiWinProb
					|| (aiWinProb == bestAiWinProb && playerWinProb < bestPlayerWinProb)) {
				bestPlayerWinProb = playerWinProb;
				bestAiWinProb = aiWinProb;
				confident = estimation.isConfident();
				bestAiCard = aiCard;
			}
		}
		estimation.setBestCard(bestAiCard);
		estimation.setConfident(confident);
		estimation.setPlayerWinProb(bestPlayerWinProb);
		estimation.setAiWinProb(bestAiWinProb);
		logger.debug("{} vs {} = {}", playedCard, bestAiCard, estimation);
	}

	/**
	 * @param playedCard
	 *            the playedCard to set
	 */
	public void setPlayedCard(Card playedCard) {
		this.playedCard = playedCard;
	}
}
