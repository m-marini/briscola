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
public class VirtualAIEndState extends AbstractVirtualGameState {

	/**
	 * 
	 */
	public VirtualAIEndState() {
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
		Card[] deckCards = getDeckCards();
		if (deckCards.length == 1) {
			// valuta direttamente la finale di partita
			Card[] playerCard = createAndAdd(getPlayerCards(), deckCards[0]);
			Card[] oppositeCards = createAndAdd(getOppositeCards(), getTrump());
			FinalAIState state = new FinalAIState();
			state.setTrump(getTrump());
			state.setDeckCards();
			state.setPlayerCards(playerCard);
			state.setPlayerScore(getPlayerScore());
			state.setOppositeCards(oppositeCards);
			state.setOppositeScore(getOppositeScore());
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
			Card card = deckCards[i];
			for (int j = 0; j < n - 1; ++j) {
				Card opposite;
				if (j < i)
					opposite = deckCards[j];
				else
					opposite = deckCards[j + 1];
				Card[] playerCards = createAndAdd(getPlayerCards(), card);
				Card[] oppositeCards = createAndAdd(getOppositeCards(),
						opposite);
				Card[] deckCards1 = createAndRemove(getDeckCards(), card,
						opposite);
				VirtualAIStartState state = new VirtualAIStartState();
				state.setTrump(getTrump());
				state.setDeckCards(deckCards1);
				state.setPlayerCards(playerCards);
				state.setPlayerScore(getPlayerScore());
				state.setOppositeCards(oppositeCards);
				state.setOppositeScore(getOppositeScore());
				state.estimate(estimation, ctx);

				confident &= estimation.isConfident();
				win += estimation.getWin() / m;
				loss += estimation.getLoss() / m;
			}
		}
		estimation.setConfident(confident);
		estimation.setWin(win);
		estimation.setLoss(loss);
		estimation.setBestCard(null);
		ctx.addLevel(-1);
	}
}
