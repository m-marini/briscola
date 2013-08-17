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
public class FinalAIState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory.getLogger(FinalAIState.class);

	/**
	 * 
	 */
	public FinalAIState() {
	}

	/**
	 * 
	 * @param card
	 * @param opposite
	 * @return
	 */
	private AbstractGameState createState(Card card, Card opposite) {
		logger.debug("Creating state playing {}, {}", card, opposite);
		Card trump = getTrump();
		boolean winner = card.wins(opposite, trump);
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
		Card bestCard = playerCards[0];
		double wp = Double.NEGATIVE_INFINITY;
		double lp = 0;
		for (Card card : playerCards) {
			double pl = Double.NEGATIVE_INFINITY;
			double pw = 0;
			for (Card opposite : oppositeCards) {
				AbstractGameState state = createState(card, opposite);
				state.estimate(estimation, ctx);
				double p = estimation.getLoss();
				if (p > pl) {
					pl = p;
					pw = estimation.getWin();
				}
			}
			if (pw > wp) {
				wp = pw;
				lp = pl;
				bestCard = card;
			}
		}
		estimation.setWin(wp);
		estimation.setLoss(lp);
		estimation.setConfident(true);
		estimation.setBestCard(bestCard);
	}
}
