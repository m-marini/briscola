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

		Card[] aiCards = createAndRemove(getAiCards(), aiCard);
		Card[] playerCards = createAndRemove(getPlayerCards(), playerCard);
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

		Card[] oppositeCards = getPlayerCards();
		Card[] playerCards = getAiCards();
		double wp = Double.NEGATIVE_INFINITY;
		double lp = Double.NEGATIVE_INFINITY;
		for (Card opposite : oppositeCards) {
			double pw = Double.NEGATIVE_INFINITY;
			double pl = 0;
			for (Card card : playerCards) {
				AbstractGameState state = createState(opposite, card);
				state.estimate(estimation, ctx);
				double p = estimation.getAiWinProb();
				if (p > pw) {
					pw = p;
					pl = estimation.getPlayerWinProb();
				}
			}
			if (pl > lp) {
				lp = pl;
				wp = pw;
			}
		}
		estimation.setAiWinProb(wp);
		estimation.setPlayerWinProb(lp);
		estimation.setConfident(true);
		estimation.setBestCard(null);
	}
}
