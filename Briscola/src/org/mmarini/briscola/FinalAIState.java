/**
 * 
 */
package org.mmarini.briscola;

import java.util.List;

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
public class FinalAIState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory.getLogger(FinalAIState.class);

	/**
	 * 
	 */
	public FinalAIState() {
		super();
	}

	/**
	 * 
	 * @param state
	 */
	public FinalAIState(AbstractVirtualGameState state) {
		super(state);
		clearDeck();
	}

	/**
	 * 
	 * @param aiCard
	 * @param playerCard
	 * @return
	 */
	private AbstractGameState createState(Card aiCard, Card playerCard) {
		logger.debug("Creating state playing {}, {}", aiCard, playerCard);
		Card trump = getTrump();
		boolean winner = aiCard.wins(playerCard, trump);
		int score = computeScore(aiCard, playerCard);

		List<Card> aiCards = getAiCards();
		int playerScore = getPlayerScore();
		int aiScore = getAiScore();
		AbstractVirtualGameState state = null;
		int m = aiCards.size() - 1;
		if (winner) {
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

		Card bestAiCard = getAiCards().get(0);
		double bestAiWinProb = Double.NEGATIVE_INFINITY;
		double bestPlayerWinProb = Double.NEGATIVE_INFINITY;
		for (Card aiCard : getAiCards()) {
			double innerBestPlayerWinProb = Double.NEGATIVE_INFINITY;
			double innerBestAiWinProb = Double.NEGATIVE_INFINITY;
			for (Card playerCard : getPlayerCards()) {
				AbstractGameState state = createState(aiCard, playerCard);
				state.estimate(estimation, ctx);
				double playerWinProb = estimation.getPlayerWinProb();
				double aiWinProb = estimation.getAiWinProb();
				if (playerWinProb > innerBestPlayerWinProb
						|| (playerWinProb == innerBestPlayerWinProb && aiWinProb < innerBestAiWinProb)) {
					innerBestPlayerWinProb = playerWinProb;
					innerBestAiWinProb = aiWinProb;
				}
			}
			if (innerBestAiWinProb > bestAiWinProb
					|| (innerBestAiWinProb == bestAiWinProb && innerBestPlayerWinProb < bestPlayerWinProb)) {
				bestAiWinProb = innerBestAiWinProb;
				bestPlayerWinProb = innerBestPlayerWinProb;
				bestAiCard = aiCard;
			}
		}
		estimation.setAiWinProb(bestAiWinProb);
		estimation.setPlayerWinProb(bestPlayerWinProb);
		estimation.setConfident(true);
		estimation.setBestCard(bestAiCard);
	}
}
