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
public class VirtualPlayerStartState extends AbstractVirtualGameState {

	private static Logger logger = LoggerFactory
			.getLogger(VirtualPlayerStartState.class);

	/**
	 * 
	 */
	public VirtualPlayerStartState() {
	}

	/**
	 * @param state
	 */
	public VirtualPlayerStartState(AbstractVirtualGameState state) {
		super(state);
	}

	/**
	 * 
	 * @param playerCard
	 * @param aiCard
	 * @return
	 */
	private AbstractVirtualGameState createState(Card playerCard, Card aiCard) {
		Card trump = getTrump();
		boolean aiWinner = !playerCard.wins(aiCard, trump);
		int score = computeScore(aiCard, playerCard);
		int aiScore = getAiScore();
		int playerScore = getPlayerScore();
		AbstractVirtualGameState state = null;
		if (aiWinner) {
			aiScore += score;
			state = new VirtualAIEndState();
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
		int plazerScore = getPlayerScore();
		if (plazerScore > HALF_SCORE) {
			estimation.setPlayerWinProb(1.);
			return;
		}
		/*
		 * Per ogni carta giocata prende la risposta con stima di perdita più
		 * alta e seleziona quella con probabilità di vincita migliore.
		 */
		boolean bestConfident = false;
		double bestAiWinProb = Double.NEGATIVE_INFINITY;
		double bestPlayerWinProb = Double.NEGATIVE_INFINITY;
		Card bestPlayerCard = null;
		Card bestAiCard = null;
		for (Card playerCard : getPlayerCards()) {
			double innerBestAiWinProb = Double.NEGATIVE_INFINITY;
			double innerBestPlayerWinProb = Double.NEGATIVE_INFINITY;
			boolean aiConfident = false;
			Card innerBestAiCard = null;
			for (Card aiCard : getAiCards()) {
				AbstractVirtualGameState state = createState(playerCard, aiCard);
				state.estimate(estimation, ctx);
				double playerWinProb = estimation.getPlayerWinProb();
				double aiWinProb = estimation.getAiWinProb();
				if (aiWinProb > innerBestAiWinProb
						|| (aiWinProb == innerBestAiWinProb && playerWinProb < innerBestPlayerWinProb)) {
					innerBestPlayerWinProb = playerWinProb;
					innerBestAiWinProb = aiWinProb;
					aiConfident = estimation.isConfident();
					innerBestAiCard = aiCard;
				}
			}
			if (innerBestPlayerWinProb > bestPlayerWinProb
					|| (innerBestPlayerWinProb == bestPlayerWinProb && innerBestAiWinProb < bestAiWinProb)) {
				bestAiWinProb = innerBestAiWinProb;
				bestPlayerWinProb = innerBestPlayerWinProb;
				bestConfident = aiConfident;
				bestPlayerCard = playerCard;
				bestAiCard = innerBestAiCard;
			}
		}
		estimation.setBestCard(bestPlayerCard);
		estimation.setConfident(bestConfident);
		estimation.setPlayerWinProb(bestPlayerWinProb);
		estimation.setAiWinProb(bestAiWinProb);
		logger.debug("{} vs {} = {}", bestPlayerCard, bestAiCard, estimation);
	}
}
