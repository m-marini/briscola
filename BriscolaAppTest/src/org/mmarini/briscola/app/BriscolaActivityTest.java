/**
 * 
 */
package org.mmarini.briscola.app;

import org.mmarini.briscola.GameHandler;

import android.app.Instrumentation;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

/**
 * @author us00852
 * 
 */
public class BriscolaActivityTest extends
		ActivityInstrumentationTestCase2<BriscolaActivity> {

	private static final String STATE1 = "aiWonGame=0;playerCards=28,22,4;playerHand=true;playerWonGame=0;aiScore=0;playerFirstHand=false;aiCards=7,26,16;status=PLAYER_MOVE;"
			+ "deck=0;" + "trump=8;playerScore=0;";
	private BriscolaActivity activity;
	private Instrumentation instr;
	private GameHandler handler;
	private Bundle outState;

	public BriscolaActivityTest() {
		super(BriscolaActivity.class);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		instr = getInstrumentation();
		activity = getActivity();
		handler = activity.getHandler();
		handler.setSeed(2);
		handler.clear();
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.app.BriscolaActivity#onCreateOptionsMenu(android.view.Menu)}
	 * .
	 */
	public void testOnStart() {
		instr.waitForIdleSync();
		TextView v = (TextView) activity.findViewById(R.id.deckCount);
		assertEquals("Carte: 0", v.getText());
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.app.BriscolaActivity#onCreateOptionsMenu(android.view.Menu)}
	 * .
	 * 
	 * @throws Throwable
	 */
	public void testOnPause1() throws Throwable {

		instr.waitForIdleSync();

		TextView v = (TextView) activity.findViewById(R.id.deckCount);
		assertEquals("Carte: 0", v.getText());

		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.dealForStart();
			}
		});
		instr.waitForIdleSync();
		assertEquals("Carte: 33", v.getText());

		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				outState = new Bundle();
			}
		});
		instr.callActivityOnSaveInstanceState(activity, outState);
		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				instr.callActivityOnRestoreInstanceState(activity, outState);
			}
		});

		// instr.callActivityOnPause(activity);
		// instr.waitForIdleSync();
		//
		// instr.callActivityOnStop(activity);
		// instr.waitForIdleSync();
		//
		//
		//
		// runTestOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// activity.finish();
		// }
		// });
		// instr.waitForIdleSync();
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.app.BriscolaActivity#onCreateOptionsMenu(android.view.Menu)}
	 * .
	 * 
	 * @throws Throwable
	 */
	public void testLastHand() throws Throwable {

		instr.waitForIdleSync();
		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.restoreHandlerState(STATE1);
			}
		});
		instr.waitForIdleSync();
		TextView v = (TextView) activity.findViewById(R.id.deckCount);
		assertEquals("Carte: 1", v.getText());

		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				View iv = activity.findViewById(R.id.playerCard1);
				activity.movePlayerCard(iv);
			}
		});
		instr.waitForIdleSync();
		Thread.sleep(10000);
	}
}
