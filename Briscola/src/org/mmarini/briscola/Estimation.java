/**
 * 
 */
package org.mmarini.briscola;

/**
 * @author US00852
 * 
 */
public class Estimation {
	private double win;
	private double loss;
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
	public double getLoss() {
		return loss;
	}

	/**
	 * @return the win probability
	 */
	public double getWin() {
		return win;
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
	public void setLoss(double lossProbability) {
		this.loss = lossProbability;
	}

	/**
	 * @param probability
	 *            the win probability to set
	 */
	public void setWin(double probability) {
		this.win = probability;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Estimation [confident=").append(confident)
				.append(", win=").append(win).append(", loss=").append(loss)
				.append(", bestCard=").append(bestCard).append("]");
		return builder.toString();
	}

}
