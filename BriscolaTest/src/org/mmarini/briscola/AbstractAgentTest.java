package org.mmarini.briscola;

import org.junit.Before;
import org.junit.Test;

public class AbstractAgentTest {

	private AbstractAgent agent;

	@Before
	public void setUp() throws Exception {
		agent = new AbstractAgent() {

			@Override
			protected void onMessage(Message message) {
				System.out.println(message);
			}
		};
	}

	@Test
	public void testSend() throws InterruptedException {
		agent.start();

		agent.send(new Message() {

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Messaggio 1";
			}

		});
		Thread.sleep(1000);
		agent.send(new Message() {

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Messaggio 2";
			}

		});
		agent.stop();
	}

	@Test
	public void testSend1() throws InterruptedException {

		agent.send(new Message() {

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Messaggio 1";
			}

		});
		Thread.sleep(1000);
		agent.send(new Message() {

			/**
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "Messaggio 2";
			}

		});
		agent.start();
		agent.stop();
	}

}
