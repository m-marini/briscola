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
public class FinalPlayerMidState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory
			.getLogger(FinalPlayerMidState.class);

	private Card playerCard;

	/**
	 * 
	 */
	public FinalPlayerMidState() {
		super();
	}

	/**
	 * 
	 * @param aiCard
	 * @return
	 */
	private AbstractGameState createState(Card aiCard) {
		logger.debug("Creating state playing {}", aiCard);
		Card trump = getTrump();
		boolean aiWinner = !playerCard.wins(aiCard, trump);

		int score = computeScore(aiCard, playerCard);

		int playerScore = getPlayerScore();
		int aiScore = getAiScore();
		AbstractVirtualGameState state = null;
		int m = getAiCards().size() - 1;
		if (aiWinner) {
			aiScore += score;
			if (m == 1) {
				state = new LastHandAIState(this);
			} else {
				state = new FinalAIState(this);
			}
		} else {
			playerScore += score;
			if (m == 1) {
				state = new LastHandPlayerState(this);
			} else {
				state = new FinalPlayerStartState(this);
			}
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

		double bestAiWinProb = Double.NEGATIVE_INFINITY;
		double bestPlayerWinProb = 0;
		Card bestCard = null;
		for (Card aiCard : getAiCards()) {
			AbstractGameState state = createState(aiCard);
			state.estimate(estimation, ctx);
			double aiWinProb = estimation.getAiWinProb();
			double playerWinProb = estimation.getPlayerWinProb();
			if (aiWinProb > bestAiWinProb
					|| (aiWinProb == bestAiWinProb && playerWinProb < bestPlayerWinProb)) {
				bestAiWinProb = aiWinProb;
				bestPlayerWinProb = playerWinProb;
				bestCard = aiCard;
			}
		}
		estimation.setAiWinProb(bestAiWinProb);
		estimation.setPlayerWinProb(bestPlayerWinProb);
		estimation.setConfident(true);
		estimation.setBestCard(bestCard);
	}

	/**
	 * @param playerCard
	 *            the playerCard to set
	 */
	public void setPlayerCard(Card playerCard) {
		this.playerCard = playerCard;
	}
}
