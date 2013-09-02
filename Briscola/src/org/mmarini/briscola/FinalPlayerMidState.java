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

		Card[] aiCards = createAndRemove(getAiCards(), aiCard);
		Card[] playerCards = getPlayerCards();
		int playerScore = getPlayerScore();
		int aiScore = getAiScore();
		AbstractVirtualGameState state = null;
		if (aiWinner) {
			aiScore += score;
			if (aiCards.length == 1) {
				state = new LastHandAIState();
			} else {
				state = new FinalAIState();
			}
		} else {
			playerScore += score;
			if (aiCards.length == 1) {
				state = new LastHandPlayerState();
			} else {
				state = new FinalPlayerStartState();
			}
		}
		state.setTrump(trump);
		state.setAiCards(aiCards);
		state.setPlayerCards(playerCards);
		state.setDeckCards();
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

		Card[] aiCards = getAiCards();
		double wp = Double.NEGATIVE_INFINITY;
		double lp = 0;
		Card bestCard = null;
		for (Card aiCard : aiCards) {
			AbstractGameState state = createState(aiCard);
			state.estimate(estimation, ctx);
			double p = estimation.getAiWinProb();
			if (p > wp) {
				wp = p;
				lp = estimation.getPlayerWinProb();
				bestCard = aiCard;
			}
		}
		estimation.setAiWinProb(wp);
		estimation.setPlayerWinProb(lp);
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
