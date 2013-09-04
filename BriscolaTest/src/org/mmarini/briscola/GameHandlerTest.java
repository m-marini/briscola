package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

public class GameHandlerTest {

	private static final String HANG_TEST_STATUS = "aiWonGame=0;"
			+ "playerCards=29,19;" + "playerHand=true;" + "playerWonGame=0;"
			+ "aiScore=25;" + "status=AI_MOVE;" + "playerFirstHand=false;"
			+ "aiCards=9,1,14;" + "deck=4,24,21,7,28;" + "playerCard=6;"
			+ "trump=26;" + "playerScore=42;";
	private static final Card CAVALLO_BASTONI = Card.getCard(Figure.KNIGHT,
			Suit.CLUBS);
	private static final Card ASSO_DENARI = Card
			.getCard(Figure.ACE, Suit.COINS);
	private static final String INITIAL_STATE = "aiWonGame=0;playerCards=;playerHand=false;playerWonGame=0;aiScore=0;status=FINISHED;playerFirstHand=true;aiCards=;deck=;playerScore=0;";
	private static final String DEAL_STATE = "aiWonGame=0;playerCards=15,31,23;playerHand=false;playerWonGame=0;aiScore=0;status=AI_MOVE;playerFirstHand=false;aiCards=32,10,8;"
			+ "deck=0,3,35,25,20,27,38,37,5,11,36,34,12,2,29,18,39,17,16,30,19,33,13,9,22,1,6,14,4,24,21,7,28;"
			+ "trump=26;playerScore=0;";

	// "aiWonGame=0;playerCards=28,22,4;playerHand=false;playerWonGame=0;aiScore=0;playerFirstHand=false;aiCards=7,26,16;"
	// +
	// "deck=0,1,2,3,5,6,9,10,11,12,13,14,15,17,18,19,20,21,23,24,25,27,29,30,31,32,33,34,35,36,37,38,39;status=DEAL;"
	// + "trump=8;playerScore=0;";
	private GameHandler handler;
	private GameMemento memento;

	@Before
	public void setUp() throws Exception {
		handler = new GameHandler();
		memento = new GameMemento();
		handler.setSeed(1);
	}

	/**
	 * Analyze
	 */
	@Test
	public void testAnalyze() {
		memento.load(HANG_TEST_STATUS);
		handler.applyMemento(memento);
		handler.setTimeout(0);
		handler.analyze();

		assertEquals(0, handler.getLevel());
		assertEquals(39., handler.getAiWinProbability() * 61, 1e-3);
		assertEquals(42. + 7.43 * 61 / 27 / 62,
				handler.getPlayerWinProbability() * 61, 1e-3);
		assertEquals(ASSO_DENARI, handler.getBestCard());
	}

	/**
	 * Analyze
	 */
	@Test
	public void testAnalyze1() {
		memento.load(HANG_TEST_STATUS);
		handler.applyMemento(memento);
		handler.setTimeout(1000000);
		handler.setAnalyzerListener(new AnalyzerListener() {

			@Override
			public void notifyAnalysis(GameHandler handler) {
				if (handler.getLevel() >= 10)
					handler.stopAnalysis();
			}
		});
		handler.analyze();

		assertEquals(3, handler.getLevel());
		assertEquals(39., handler.getAiWinProbability() * 61, 1e-3);
		assertEquals(42. + 7.43 * 61 / 27 / 62,
				handler.getPlayerWinProbability() * 61, 1e-3);
		assertEquals(ASSO_DENARI, handler.getBestCard());
	}

	@Test
	public void testApplyMemento1() {
		memento.load(INITIAL_STATE);
		handler.applyMemento(memento);
		assertTrue(handler.isFinished());
		assertEquals(0, handler.getPlayerScore());
		assertEquals(0, handler.getAiScore());
		assertEquals(0, handler.getPlayerWonGame());
		assertEquals(0, handler.getAiWonGame());
		assertNull(handler.getPlayerCard());
		assertNull(handler.getAiCard());
		assertNull(handler.getTrump());
	}

	@Test
	public void testApplyMemento2() {
		memento.load(DEAL_STATE);
		handler.applyMemento(memento);
		assertFalse(handler.isFinished());
		assertEquals(0, handler.getPlayerScore());
		assertEquals(0, handler.getAiScore());
		assertEquals(0, handler.getPlayerWonGame());
		assertEquals(0, handler.getAiWonGame());
		assertNull(handler.getPlayerCard());
		assertNull(handler.getAiCard());
		assertEquals(CAVALLO_BASTONI, handler.getTrump());
	}

	@Test
	public void testCreateMemento1() {
		handler.createMemento(memento);
		assertEquals(INITIAL_STATE, memento.toString());
	}

	@Test
	public void testCreateMemento2() {
		handler.deal();
		handler.createMemento(memento);
		assertEquals(DEAL_STATE, memento.toString());
	}
}
