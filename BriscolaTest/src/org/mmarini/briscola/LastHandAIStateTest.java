/**
 * 
 */
package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

/**
 * @author us00852
 * 
 */
public class LastHandAIStateTest {

	private static final Card DUE_COPPE = Card.getCard(Figure.TWO, Suit.CUPS);
	private static final Card FANTE_DENARI = Card.getCard(Figure.INFANTRY,
			Suit.COINS);
	private static final Card DUE_DENARI = Card.getCard(Figure.TWO, Suit.COINS);
	private static final double EPSILON = 1e-10;
	private static final Card QUATTRO_COPPE = Card.getCard(Figure.FOUR,
			Suit.CUPS);
	private TimerSearchContext ctx;
	private LastHandAIState state;
	private Estimation estimation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new TimerSearchContext();
		ctx.setTimeout(1000000);
		estimation = new Estimation();
		state = new LastHandAIState();
		state.setDeckCards();
		state.setTrump(DUE_DENARI);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.StrategySearchContext#search(org.mmarini.briscola.Estimation, org.mmarini.briscola.AbstractGameState)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimateDraw1() throws InterruptedException {
		state.setPlayerScore(60);
		state.setAiScore(60);
		state.setPlayerCards(QUATTRO_COPPE);
		state.setAiCards(DUE_COPPE);

		ctx.estimate(estimation, state);
		assertTrue(estimation.isConfident());
		assertEquals(0., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.StrategySearchContext#search(org.mmarini.briscola.Estimation, org.mmarini.briscola.AbstractGameState)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimateDraw2() throws InterruptedException {
		state.setPlayerScore(60);
		state.setAiScore(60);
		state.setAiCards(QUATTRO_COPPE);
		state.setPlayerCards(DUE_COPPE);

		ctx.estimate(estimation, state);
		assertTrue(estimation.isConfident());
		assertEquals(0., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.StrategySearchContext#search(org.mmarini.briscola.Estimation, org.mmarini.briscola.AbstractGameState)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimateLoss() throws InterruptedException {
		state.setPlayerScore(59);
		state.setAiScore(59);
		state.setPlayerCards(FANTE_DENARI);
		state.setAiCards(DUE_COPPE);

		ctx.estimate(estimation, state);
		assertTrue(estimation.isConfident());
		assertEquals(0., estimation.getAiWinProb(), EPSILON);
		assertEquals(1., estimation.getPlayerWinProb(), EPSILON);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.StrategySearchContext#search(org.mmarini.briscola.Estimation, org.mmarini.briscola.AbstractGameState)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimateWin() throws InterruptedException {
		state.setPlayerScore(59);
		state.setAiScore(59);
		state.setAiCards(FANTE_DENARI);
		state.setPlayerCards(DUE_COPPE);

		ctx.estimate(estimation, state);
		assertTrue(estimation.isConfident());
		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
	}
}
