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
public class LastHandPlayerState extends AbstractVirtualGameState {

	/**
	 * 
	 */
	public LastHandPlayerState() {
	}

	/**
	 * @param state
	 */
	public LastHandPlayerState(AbstractVirtualGameState state) {
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
		int score = getAiScore();
		Card aiCard = getAiCards().get(0);
		Card playerCard = getPlayerCards().get(0);
		if (!playerCard.wins(aiCard, getTrump())) {
			score += computeScore(aiCard, playerCard);
		}
		estimation.setBestCard(aiCard);
		estimation.setConfident(true);
		estimation.setAiWinProb(0.);
		estimation.setPlayerWinProb(0.);
		if (score > HALF_SCORE) {
			estimation.setAiWinProb(1.);
		} else if (score < HALF_SCORE) {
			estimation.setPlayerWinProb(1.);
		}
	}
}
