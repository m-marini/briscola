/**
 * 
 */
package org.mmarini.briscola;

/**
 * @author US00852
 * 
 */
public class Estimation {
	private double aiWinProb;
	private double playerWinProb;
	private boolean confident;
	private Card bestCard;

	/**
	 * 
	 */
	public Estimation() {
	}

	/**
	 * @return the bestCard
	 */
	public Card getBestCard() {
		return bestCard;
	}

	/**
	 * @return the loss probability
	 */
	public double getPlayerWinProb() {
		return playerWinProb;
	}

	/**
	 * @return the win probability
	 */
	public double getAiWinProb() {
		return aiWinProb;
	}

	/**
	 * @return the confident
	 */
	public boolean isConfident() {
		return confident;
	}

	/**
	 * @param card
	 *            the bestCard to set
	 */
	public void setBestCard(Card card) {
		this.bestCard = card;
	}

	/**
	 * @param confident
	 *            the confident to set
	 */
	public void setConfident(boolean confident) {
		this.confident = confident;
	}

	/**
	 * @param lossProbability
	 *            the loss probability to set
	 */
	public void setPlayerWinProb(double lossProbability) {
		this.playerWinProb = lossProbability;
	}

	/**
	 * @param probability
	 *            the win probability to set
	 */
	public void setAiWinProb(double probability) {
		this.aiWinProb = probability;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Estimation [confident=").append(confident)
				.append(", win=").append(aiWinProb).append(", loss=")
				.append(playerWinProb).append(", bestCard=").append(bestCard)
				.append("]");
		return builder.toString();
	}

}
