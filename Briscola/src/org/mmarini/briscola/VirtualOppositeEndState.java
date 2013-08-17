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
public class VirtualOppositeEndState extends AbstractVirtualGameState {

	/**
	 * 
	 */
	public VirtualOppositeEndState() {
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
		int oppositeScore = getOppositeScore();
		Card[] deckCards = getDeckCards();
		if (score > HALF_SCORE) {
			estimation.setWin(1.);
		} else if (oppositeScore > HALF_SCORE) {
			estimation.setLoss(1.);
		} else if (deckCards.length == 1) {
			// valuta direttamente la finale di partita
			Card[] playerCard = createAndAdd(getPlayerCards(), getTrump());
			Card[] oppositeCards = createAndAdd(getOppositeCards(),
					deckCards[0]);
			FinalOppositeStartState state = new FinalOppositeStartState();
			state.setDeckCards();
			state.setPlayerCards(playerCard);
			state.setPlayerScore(getPlayerScore());
			state.setOppositeCards(oppositeCards);
			state.setOppositeScore(getOppositeScore());
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
				VirtualOppositeStartState state = new VirtualOppositeStartState();
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
