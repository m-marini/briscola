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
public class FinalOppositeMidState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory
			.getLogger(FinalOppositeMidState.class);

	private Card oppositeCard;

	/**
	 * 
	 */
	public FinalOppositeMidState() {
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	private AbstractGameState createState(Card card) {
		logger.debug("Creating state playing {}", card);
		Card trump = getTrump();
		boolean winner = !oppositeCard.wins(card, trump);

		int score = computeScore(card, oppositeCard);

		Card[] playerCards = createAndRemove(getPlayerCards(), card);
		Card[] oppositeCards = getOppositeCards();
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

		Card[] playerCards = getPlayerCards();
		double wp = Double.NEGATIVE_INFINITY;
		double lp = 0;
		Card bestCard = null;
		for (Card card : playerCards) {
			AbstractGameState state = createState(card);
			state.estimate(estimation, ctx);
			double p = estimation.getWin();
			if (p > wp) {
				wp = p;
				lp = estimation.getLoss();
				bestCard = card;
			}
		}
		estimation.setWin(wp);
		estimation.setLoss(lp);
		estimation.setConfident(true);
		estimation.setBestCard(bestCard);
	}

	/**
	 * @param oppositeCard
	 *            the oppositeCard to set
	 */
	public void setOppositeCard(Card oppositeCard) {
		this.oppositeCard = oppositeCard;
	}
}
