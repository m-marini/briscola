/**
 * 
 */
package org.mmarini.briscola;

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
public class VirtualPlayerEndState extends AbstractVirtualGameState {

	/**
	 * 
	 */
	public VirtualPlayerEndState() {
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
		int aiScore = getAiScore();
		int playerScore = getPlayerScore();
		Card[] deckCards = getDeckCards();
		if (aiScore > HALF_SCORE) {
			estimation.setAiWinProb(1.);
		} else if (playerScore > HALF_SCORE) {
			estimation.setPlayerWinProb(1.);
		} else if (deckCards.length == 1) {
			// valuta direttamente la finale di partita
			Card[] aiCards = createAndAdd(getAiCards(), getTrump());
			Card[] playerCards = createAndAdd(getPlayerCards(), deckCards[0]);
			FinalPlayerStartState state = new FinalPlayerStartState();
			state.setDeckCards();
			state.setAiCards(aiCards);
			state.setAiScore(getAiScore());
			state.setPlayerCards(playerCards);
			state.setPlayerScore(getPlayerScore());
			state.setTrump(getTrump());
			state.estimate(estimation, ctx);
		} else if (ctx.isSearchingDeeper()) {
			estimateNextStates(estimation, ctx);
		} else {
			estimateEmpiric(estimation, ctx);
		}
	}

	/**
	 * 
	 * @param estimation
	 * @param ctx
	 * @throws InterruptedException
	 */
	private void estimateNextStates(Estimation estimation,
			StrategySearchContext ctx) throws InterruptedException {
		if (ctx.isStop())
			throw new InterruptedException();
		ctx.addLevel(1);
		/*
		 * Valuta il livello successivo.
		 */
		Card[] deckCards = getDeckCards();
		int n = deckCards.length;
		double win = 0;
		double loss = 0;
		boolean confident = true;
		int m = n * (n - 1);
		for (int i = 0; i < n; ++i) {
			Card aiCard = deckCards[i];
			for (int j = 0; j < n - 1; ++j) {
				Card playerCard;
				if (j < i)
					playerCard = deckCards[j];
				else
					playerCard = deckCards[j + 1];
				Card[] aiCards = createAndAdd(getAiCards(), aiCard);
				Card[] playerCards = createAndAdd(getPlayerCards(), playerCard);
				Card[] deckCards1 = createAndRemove(getDeckCards(), aiCard,
						playerCard);
				VirtualPlayerStartState state = new VirtualPlayerStartState();
				state.setTrump(getTrump());
				state.setDeckCards(deckCards1);
				state.setAiCards(aiCards);
				state.setAiScore(getAiScore());
				state.setPlayerCards(playerCards);
				state.setPlayerScore(getPlayerScore());
				state.estimate(estimation, ctx);

				confident &= estimation.isConfident();
				win += estimation.getAiWinProb() / m;
				loss += estimation.getPlayerWinProb() / m;
			}
		}
		estimation.setConfident(confident);
		estimation.setAiWinProb(win);
		estimation.setPlayerWinProb(loss);
		estimation.setBestCard(null);
		ctx.addLevel(-1);
	}
}
