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
public class VirtualAIStartState extends AbstractVirtualGameState {
	private static Logger logger = LoggerFactory
			.getLogger(VirtualAIStartState.class);

	/**
	 * 
	 */
	public VirtualAIStartState() {
	}

	/**
	 * @param state
	 */
	public VirtualAIStartState(AbstractGameState state) {
		super(state);
	}

	/**
	 * @param state
	 */
	public VirtualAIStartState(AbstractVirtualGameState state) {
		super(state);
	}

	/**
	 * 
	 * @param aiCard
	 * @param playerCard
	 * @return
	 */
	private AbstractVirtualGameState createState(Card aiCard, Card playerCard) {
		Card trump = getTrump();
		boolean aiWinner = aiCard.wins(playerCard, trump);
		int score = computeScore(aiCard, playerCard);
		int aiScore = getAiScore();
		int playerScore = getPlayerScore();
		AbstractVirtualGameState state = null;
		if (aiWinner) {
			aiScore += score;
			state = new VirtualAIEndState(this);
		} else {
			playerScore += score;
			state = new VirtualPlayerEndState(this);
		}
		state.removeFromAiCards(aiCard);
		state.removeFromPlayerCards(playerCard);
		state.setAiScore(aiScore);
		state.setPlayerScore(playerScore);
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
		estimation.setAiWinProb(0.);
		estimation.setPlayerWinProb(0.);
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
		 * Per ogni carta giocata prende la risposta con stima di perdita più
		 * alta e seleziona quella con probabilità di vincita migliore.
		 */
		boolean bestConfident = false;
		double bestAiWinProb = Double.NEGATIVE_INFINITY;
		double bestPlayerWinProb = 0;
		Card bestAiCard = null;
		Card bestPlayerCard = null;
		for (Card aiCard : getAiCards()) {
			double innerAiBestWinProb = 0;
			double innerPlayerBestWinProb = Double.NEGATIVE_INFINITY;
			boolean playerConfident = false;
			Card innerBestPlayerCard = null;
			for (Card playerCard : getPlayerCards()) {
				AbstractVirtualGameState state = createState(aiCard, playerCard);
				state.estimate(estimation, ctx);
				double playerWinprob = estimation.getPlayerWinProb();
				double aiWinProb = estimation.getAiWinProb();
				if (playerWinprob > innerPlayerBestWinProb
						|| (playerWinprob == innerPlayerBestWinProb && aiWinProb < innerAiBestWinProb)) {
					innerPlayerBestWinProb = playerWinprob;
					innerAiBestWinProb = aiWinProb;
					playerConfident = estimation.isConfident();
					innerBestPlayerCard = playerCard;
				}
			}
			if (innerAiBestWinProb > bestAiWinProb
					|| (innerAiBestWinProb == bestAiWinProb && innerPlayerBestWinProb < bestPlayerWinProb)) {
				bestAiWinProb = innerAiBestWinProb;
				bestPlayerWinProb = innerPlayerBestWinProb;
				bestConfident = playerConfident;
				bestAiCard = aiCard;
				bestPlayerCard = innerBestPlayerCard;
			}
		}
		estimation.setBestCard(bestAiCard);
		estimation.setConfident(bestConfident);
		estimation.setPlayerWinProb(bestPlayerWinProb);
		estimation.setAiWinProb(bestAiWinProb);
		logger.debug("{} vs {} = {}", bestAiCard, bestPlayerCard, estimation);
	}
}
