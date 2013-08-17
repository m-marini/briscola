/**
 * 
 */
package org.mmarini.briscola;

/**
 * @author US00852
 * 
 */
public interface GameListener {

	/**
	 * 
	 * @param handler
	 */
	public abstract void notifyAnalysis(GameHandler handler);

	/**
	 * 
	 * @param handler
	 */
	public abstract void notifyCardPlayed(GameHandler handler);
}
