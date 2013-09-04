/**
 * 
 */
package org.mmarini.briscola;

import java.util.Arrays;
import java.util.List;

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
public class GameAIState extends AbstractGameState {

	/**
	 * 
	 */
	public GameAIState() {
	}

	/**
	 * @see org.mmarini.briscola.AbstractGameState#estimate(org.mmarini.briscola.
	 *      Estimation, org.mmarini.briscola.StrategySearchContext)
	 */
	@Override
	public void estimate(Estimation estimation, StrategySearchContext ctx)
			throws InterruptedException {
		List<Card> aiCards = getAiCards();
		estimation.setConfident(true);
		estimation.setAiWinProb(0.);
		estimation.setPlayerWinProb(0.);
		estimation.setBestCard(aiCards.get(0));
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
		List<Card> deckCards = getDeckCards();
		int n = deckCards.size();

		double[] aiWinProbs = new double[3];
		double[] playerWInProbs = new double[3];
		boolean[] confidents = new boolean[3];
		Arrays.fill(confidents, true);
		int ct = 0;
		for (int i = 0; i < n - 2; ++i) {
			Card iCard = deckCards.get(i);
			for (int j = i + 1; j < n - 1; ++j) {
				Card jCard = deckCards.get(j);
				for (int k = j + 1; k < n; ++k) {
					Card kCard = deckCards.get(k);

					VirtualAIStartState state = new VirtualAIStartState(this);
					state.addToPlayerCards(iCard);
					state.addToPlayerCards(jCard);
					state.addToPlayerCards(kCard);

					state.removeFromDeckCards(iCard);
					state.removeFromDeckCards(jCard);
					state.removeFromDeckCards(kCard);

					state.estimate(estimation, ctx);

					Card bestCard = estimation.getBestCard();
					int idx = aiCards.indexOf(bestCard);
					aiWinProbs[idx] += estimation.getAiWinProb();
					playerWInProbs[idx] += estimation.getPlayerWinProb();
					confidents[idx] &= estimation.isConfident();
					++ct;
				}
			}
		}

		Card bestCard = null;
		boolean confident = false;
		double bestPlayerWinProb = Double.NEGATIVE_INFINITY;
		double bestAiWinProb = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < 3; ++i) {
			if (aiWinProbs[i] > bestAiWinProb
					|| (aiWinProbs[i] == bestAiWinProb && playerWInProbs[i] < bestPlayerWinProb)) {
				bestCard = aiCards.get(i);
				bestPlayerWinProb = playerWInProbs[i];
				bestAiWinProb = aiWinProbs[i];
				confident = confidents[i];
			}
		}

		estimation.setBestCard(bestCard);
		estimation.setConfident(confident);
		estimation.setPlayerWinProb(bestPlayerWinProb / ct);
		estimation.setAiWinProb(bestAiWinProb / ct);
	}
}
