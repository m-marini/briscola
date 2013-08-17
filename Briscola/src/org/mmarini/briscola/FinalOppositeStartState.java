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
public class FinalOppositeStartState extends AbstractVirtualGameState {

	private Logger logger = LoggerFactory
			.getLogger(FinalOppositeStartState.class);

	/**
	 * 
	 */
	public FinalOppositeStartState() {
	}

	/**
	 * 
	 * @param opposite
	 * @param card
	 * @return
	 */
	private AbstractGameState createState(Card opposite, Card card) {
		logger.debug("Creating state playing {}, {}", opposite, card);
		Card trump = getTrump();
		boolean winner = !opposite.wins(card, trump);
		int score = computeScore(card, opposite);

		Card[] playerCards = createAndRemove(getPlayerCards(), card);
		Card[] oppositeCards = createAndRemove(getOppositeCards(), opposite);
		int oppositeScore = getOppositeScore();
		int playerScore = getPlayerScore();
		AbstractVirtualGameState state = null;
		if (winner) {
			playerScore += score;
			if (playerCards.length == 1) {
				state = new LastHandAIState();
			} else {
				state = new FinalAIState();
			}
		} else {
			oppositeScore += score;
			if (playerCards.length == 1) {
				state = new LastHandOppositeState();
			} else {
				state = new FinalOppositeStartState();
			}
		}
		state.setTrump(trump);
		state.setPlayerCards(playerCards);
		state.setOppositeCards(oppositeCards);
		state.setDeckCards();
		state.setPlayerScore(playerScore);
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

		Card[] oppositeCards = getOppositeCards();
		Card[] playerCards = getPlayerCards();
		double wp = Double.NEGATIVE_INFINITY;
		double lp = Double.NEGATIVE_INFINITY;
		for (Card opposite : oppositeCards) {
			double pw = Double.NEGATIVE_INFINITY;
			double pl = 0;
			for (Card card : playerCards) {
				AbstractGameState state = createState(opposite, card);
				state.estimate(estimation, ctx);
				double p = estimation.getWin();
				if (p > pw) {
					pw = p;
					pl = estimation.getLoss();
				}
			}
			if (pl > lp) {
				lp = pl;
				wp = pw;
			}
		}
		estimation.setWin(wp);
		estimation.setLoss(lp);
		estimation.setConfident(true);
		estimation.setBestCard(null);
	}
}
