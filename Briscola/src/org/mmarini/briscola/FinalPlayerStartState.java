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
public class FinalPlayerStartState extends AbstractVirtualGameState {

	private Logger logger = LoggerFactory
			.getLogger(FinalPlayerStartState.class);

	/**
	 * 
	 */
	public FinalPlayerStartState() {
		super();
	}

	/**
	 * @param state
	 */
	public FinalPlayerStartState(AbstractVirtualGameState state) {
		super(state);
		clearDeck();
	}

	/**
	 * 
	 * @param playerCard
	 * @param aiCard
	 * @return
	 */
	private AbstractGameState createState(Card playerCard, Card aiCard) {
		logger.debug("Creating state playing {}, {}", playerCard, aiCard);
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
		state.removeFromPlayerCards(playerCard);
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

		double bestAiWinProb = Double.NEGATIVE_INFINITY;
		double bestPlayerProb = Double.NEGATIVE_INFINITY;
		Card bestPlayerCard = null;
		for (Card playerCard : getPlayerCards()) {
			double innerBestAiWinProb = Double.NEGATIVE_INFINITY;
			double innerPlayerAIProb = 0;
			for (Card aiCard : getAiCards()) {
				AbstractGameState state = createState(playerCard, aiCard);
				state.estimate(estimation, ctx);
				double aiWinProb = estimation.getAiWinProb();
				double playerWinProb = estimation.getPlayerWinProb();
				if (aiWinProb > innerBestAiWinProb
						|| (aiWinProb == innerBestAiWinProb && playerWinProb < innerPlayerAIProb)) {
					innerBestAiWinProb = aiWinProb;
					innerPlayerAIProb = playerWinProb;
				}
			}
			if (innerPlayerAIProb > bestPlayerProb
					|| (innerPlayerAIProb == bestPlayerProb && innerBestAiWinProb < bestAiWinProb)) {
				bestPlayerProb = innerPlayerAIProb;
				bestAiWinProb = innerBestAiWinProb;
				bestPlayerCard = playerCard;
			}
		}
		estimation.setAiWinProb(bestAiWinProb);
		estimation.setPlayerWinProb(bestPlayerProb);
		estimation.setConfident(true);
		estimation.setBestCard(bestPlayerCard);
	}
}
