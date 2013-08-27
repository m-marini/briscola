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

	private static final Card TRE_DENARI = Card.getCard(Figure.THREE,
			Suit.COINS);
	private static final String INITIAL_STATE = "aiWonGame=0;playerCards=;playerHand=false;playerWonGame=0;aiScore=0;playerFirstHand=true;aiCards=;finished=true;deck=;playerScore=0;";
	private static final String DEAL_STATE = "aiWonGame=0;playerCards=28,22,4;playerHand=false;playerWonGame=0;aiScore=0;playerFirstHand=false;aiCards=7,26,16;finished=false;"
			+ "deck=0,1,2,3,5,6,9,10,11,12,13,14,15,17,18,19,20,21,23,24,25,27,29,30,31,32,33,34,35,36,37,38,39;"
			+ "trump=8;playerScore=0;";
	private GameHandler handler;
	private GameMemento memento;

	@Before
	public void setUp() throws Exception {
		handler = new GameHandler();
		memento = new GameMemento();
		handler.setSeed(1);
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
		assertEquals(TRE_DENARI, handler.getTrump());
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
