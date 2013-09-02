/**
 * 
 */
package org.mmarini.briscola.gui;

import org.mmarini.briscola.GameHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author us00852
 * 
 */
public class Tracer {

	private static Logger logger = LoggerFactory.getLogger(Tracer.class);

	/**
	 * 
	 */
	public Tracer() {
	}

	/**
	 * 
	 * @param handler
	 */
	public void trace(GameHandler handler) {
		logger.info(handler.createMemento().toString());
	}
}
