/**
 * 
 */
package org.mmarini.briscola;

import java.util.Arrays;

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
public class GameOppositeState extends AbstractGameState {

	private Card oppositeCard;

	/**
	 * 
	 */
	public GameOppositeState() {
	}

	/**
	 * @see org.mmarini.briscola.AbstractGameState#estimate(org.mmarini.briscola.
	 *      Estimation, org.mmarini.briscola.StrategySearchContext)
	 */
	@Override
	public void estimate(Estimation estimation, StrategySearchContext ctx)
			throws InterruptedException {
		Card[] playerCards = getPlayerCards();
		estimation.setConfident(true);
		estimation.setWin(0.);
		estimation.setLoss(0.);
		estimation.setBestCard(playerCards[0]);
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
		/*
		 * Per ogni combinazione di carte possibili calcola la probabilità di
		 * vincita e di perdita e sceglie quella a probabilità di vincita
		 * maggiore e minor probabilità di perdita
		 */
		Card[] deckCards = getDeckCards();
		int n = deckCards.length;
		Card[] oppositeCards = new Card[2];
		VirtualOppositeMidState state = new VirtualOppositeMidState();
		state.setTrump(getTrump());
		state.setOppositeCards(oppositeCards);
		state.setOppositeScore(getOppositeScore());
		state.setPlayerCards(playerCards);
		state.setPlayerScore(getPlayerScore());
		state.setPlayedCard(oppositeCard);
		double[] wins = new double[3];
		double[] losses = new double[3];
		boolean[] confidents = new boolean[3];
		Arrays.fill(confidents, true);
		double prob = 1. / n / (n - 1) / (n - 2);
		for (int i = 0; i < n - 2; ++i) {
			oppositeCards[0] = deckCards[i];
			for (int j = i + 1; j < n - 1; ++j) {
				oppositeCards[1] = deckCards[j];
				state.setDeckCards(createAndRemove(deckCards, oppositeCards));
				state.estimate(estimation, ctx);
				Card bestCard = estimation.getBestCard();
				int idx = 0;
				for (Card c : playerCards) {
					if (c.equals(bestCard))
						break;
					++idx;
				}
				wins[idx] += estimation.getWin() * prob;
				losses[idx] += estimation.getLoss() * prob;
				confidents[idx] &= estimation.isConfident();
			}
		}

		Card bestCard = null;
		boolean confident = false;
		double loss = Double.NEGATIVE_INFINITY;
		double win = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < 3; ++i) {
			if (wins[i] > win || (wins[i] == win && losses[i] < loss)) {
				bestCard = playerCards[i];
				loss = losses[i];
				win = wins[i];
				confident = confidents[i];
			}
		}

		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setLoss(loss);
		estimation.setWin(win);
	}

	public void setOppositeCard(Card oppositeCard) {
		this.oppositeCard = oppositeCard;
	}
}
