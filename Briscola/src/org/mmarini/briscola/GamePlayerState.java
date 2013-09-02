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
public class GamePlayerState extends AbstractGameState {

	private Card playerCard;

	/**
	 * 
	 */
	public GamePlayerState() {
	}

	/**
	 * @see org.mmarini.briscola.AbstractGameState#estimate(org.mmarini.briscola.
	 *      Estimation, org.mmarini.briscola.StrategySearchContext)
	 */
	@Override
	public void estimate(Estimation estimation, StrategySearchContext ctx)
			throws InterruptedException {
		Card[] aiCards = getAiCards();
		estimation.setConfident(true);
		estimation.setAiWinProb(0.);
		estimation.setPlayerWinProb(0.);
		estimation.setBestCard(aiCards[0]);
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
		/*
		 * Per ogni combinazione di carte possibili calcola la probabilità di
		 * vincita e di perdita e sceglie quella a probabilità di vincita
		 * maggiore e minor probabilità di perdita
		 */
		Card[] deckCards = getDeckCards();
		int n = deckCards.length;
		Card[] playerCards = new Card[2];
		VirtualplayerMidState state = new VirtualplayerMidState();
		state.setTrump(getTrump());
		state.setPlayerCards(playerCards);
		state.setPlayerScore(getPlayerScore());
		state.setAiCards(aiCards);
		state.setAiScore(getAiScore());
		state.setPlayedCard(playerCard);
		double[] wins = new double[3];
		double[] losses = new double[3];
		boolean[] confidents = new boolean[3];
		Arrays.fill(confidents, true);
		double prob = 1. / n / (n - 1);
		for (int i = 0; i < n - 1; ++i) {
			playerCards[0] = deckCards[i];
			for (int j = i + 1; j < n; ++j) {
				playerCards[1] = deckCards[j];
				state.setDeckCards(createAndRemove(deckCards, playerCards));
				state.estimate(estimation, ctx);
				Card bestCard = estimation.getBestCard();
				int idx = 0;
				for (Card c : aiCards) {
					if (c.equals(bestCard))
						break;
					++idx;
				}
				wins[idx] += estimation.getAiWinProb() * prob;
				losses[idx] += estimation.getPlayerWinProb() * prob;
				confidents[idx] &= estimation.isConfident();
			}
		}

		Card bestCard = null;
		boolean confident = false;
		double loss = Double.NEGATIVE_INFINITY;
		double win = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < 3; ++i) {
			if (wins[i] > win || (wins[i] == win && losses[i] < loss)) {
				bestCard = aiCards[i];
				loss = losses[i];
				win = wins[i];
				confident = confidents[i];
			}
		}

		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setPlayerWinProb(loss);
		estimation.setAiWinProb(win);
	}

	public void setPlayerCard(Card oppositeCard) {
		this.playerCard = oppositeCard;
	}
}
