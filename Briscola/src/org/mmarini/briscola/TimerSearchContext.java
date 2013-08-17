/**
 * 
 */
package org.mmarini.briscola;

/**
 * @author us00852
 * 
 */
public class TimerSearchContext implements StrategySearchContext {
	private int maxDeep;
	private int currentLevel;
	private long timer;

	/**
	 * 
	 */
	public TimerSearchContext() {
	}

	/**
	 * 
	 * @param level
	 */
	@Override
	public void addLevel(int level) {
		currentLevel += level;
	}

	/**
	 * 
	 * @param estimation
	 * @param state
	 * @throws InterruptedException
	 */
	@Override
	public void estimate(Estimation estimation, AbstractGameState state)
			throws InterruptedException {
		currentLevel = 0;
		state.estimate(estimation, this);
	}

	/**
	 * @return the maxDeep
	 */
	public int getMaxDeep() {
		return maxDeep;
	}

	/**
	 * 
	 */
	@Override
	public boolean isSearchingDeeper() {
		return currentLevel < maxDeep;
	}

	/**
	 * @return the stop
	 */
	@Override
	public boolean isStop() {
		return System.currentTimeMillis() >= timer;
	}

	/**
	 * @param maxDeep
	 *            the maxDeep to set
	 */
	public void setMaxDeep(int maxDeep) {
		this.maxDeep = maxDeep;
	}

	/**
	 * 
	 * @param timeout
	 */
	public void setTimeout(long timeout) {
		timer = System.currentTimeMillis() + timeout;
	}
}
