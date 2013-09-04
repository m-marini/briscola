/**
 * 
 */
package org.mmarini.briscola;

/**
 * Game state in case the player moves.
 * <p>
 * The game is in the final hand where both the player has only one card. The
 * strategy can be completed defined.
 * </p>
 * 
 * @author US00852
 * 
 */
public class LastHandAIState extends AbstractVirtualGameState {

	/**
	 * 
	 */
	public LastHandAIState() {
		super();
	}

	/**
	 * @param state
	 */
	public LastHandAIState(AbstractVirtualGameState state) {
		super(state);
		clearDeck();
	}

	/**
	 * @see org.mmarini.briscola.AbstractGameState#estimate(org.mmarini.briscola.
	 *      Estimation, org.mmarini.briscola.StrategySearchContext)
	 */
	@Override
	public void estimate(Estimation estimation,
			StrategySearchContext strategySearchContext)
			throws InterruptedException {
		int aiScore = getAiScore();
		Card aiCard = getAiCards().get(0);
		Card playerCard = getPlayerCards().get(0);
		if (aiCard.wins(playerCard, getTrump())) {
			aiScore += computeScore(aiCard, playerCard);
		}
		estimation.setBestCard(aiCard);
		estimation.setConfident(true);
		estimation.setAiWinProb(0.);
		estimation.setPlayerWinProb(0.);
		if (aiScore > HALF_SCORE) {
			estimation.setAiWinProb(1.);
		} else if (aiScore < HALF_SCORE) {
			estimation.setPlayerWinProb(1.);
		}
	}
}
