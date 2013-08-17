/**
 * 
 */
package org.mmarini.briscola;

/**
 * @author us00852
 * 
 */
public interface StrategySearchContext {

	/**
	 * 
	 * @param i
	 */
	public abstract void addLevel(int i);

	/**
	 * 
	 * @param estimation
	 * @param state
	 * @throws InterruptedException
	 */
	public abstract void estimate(Estimation estimation, AbstractGameState state)
			throws InterruptedException;

	/**
	 * 
	 * @return
	 */
	public abstract boolean isSearchingDeeper();

	/**
	 * 
	 * @return
	 */
	public abstract boolean isStop();
}
