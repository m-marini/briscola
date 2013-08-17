/**
 * 
 */
package org.mmarini.briscola;

import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author US00852
 * 
 */
public abstract class AbstractAgent implements Agent {
	private Logger logger = LoggerFactory.getLogger(AbstractAgent.class);

	private Thread thread;
	private Queue<Message> queue;

	/**
	 * 
	 */
	protected AbstractAgent() {
		queue = new LinkedList<Message>();
	}

	/**
	 * 
	 */
	private void handleMessages() {
		try {
			Message msg;
			logger.debug("Agent started.");
			while ((msg = receive()) != null) {
				logger.debug("Processing message {} ...", msg);
				onMessage(msg);
			}
			logger.debug("Agent stopped.");
		} catch (InterruptedException e) {
			logger.error("Error handling agent message", e);
		}
	}

	/**
	 * 
	 * @return
	 */
	public synchronized boolean isAlive() {
		return (thread != null) ? thread.isAlive() : false;
	}

	/**
	 * 
	 * @param message
	 */
	protected abstract void onMessage(Message message);

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	private synchronized Message receive() throws InterruptedException {
		if (queue.isEmpty() && thread != null) {
			logger.debug("Waiting for message ...");
			wait();
			logger.debug("Woken up.");
		}
		Message msg = queue.poll();
		logger.debug("Message read {}", msg);
		return msg;
	}

	/**
	 * @see org.mmarini.briscola.Agent#send(org.mmarini.briscola.Message)
	 */
	@Override
	public synchronized void send(Message message) {
		queue.add(message);
		logger.debug("Message queued {}", message);
		notify();
		logger.debug("Notify.");
	}

	/**
	 * 
	 */
	public synchronized void start() {
		if (thread == null) {
			thread = new Thread() {

				/**
				 * @see java.lang.Thread#run()
				 */
				@Override
				public void run() {
					handleMessages();
				}

			};
			logger.debug("Starting agent");
			thread.start();
		}
	}

	/**
	 * 
	 */
	public synchronized void stop() {
		thread = null;
		logger.debug("Stopping agent");
		notify();
		logger.debug("Notify.");
	}
}
