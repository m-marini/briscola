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
public class LastHandOppositeState extends AbstractVirtualGameState {

	/**
	 * 
	 */
	public LastHandOppositeState() {
	}

	/**
	 * @see org.mmarini.briscola.AbstractGameState#estimate(org.mmarini.briscola.
	 *      Estimation, org.mmarini.briscola.StrategySearchContext)
	 */
	@Override
	public void estimate(Estimation estimation,
			StrategySearchContext strategySearchContext)
			throws InterruptedException {
		int score = getPlayerScore();
		Card card = getPlayerCards()[0];
		Card opposite = getOppositeCards()[0];
		if (!opposite.wins(card, getTrump())) {
			score += computeScore(card, opposite);
		}
		estimation.setBestCard(card);
		estimation.setConfident(true);
		estimation.setWin(0.);
		estimation.setLoss(0.);
		if (score > HALF_SCORE) {
			estimation.setWin(1.);
		} else if (score < HALF_SCORE) {
			estimation.setLoss(1.);
		}
	}
}
