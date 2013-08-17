/**
 * 
 */
package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

/**
 * @author us00852
 * 
 */
public class FinalOppositeStartStateTest {

	private static final double EPSILON = 10e-6;
	private static final Card BRISCOLA = Card.getCard(Figure.TWO, Suit.SWORDS);
	private static final Card ASSO_DENARI = Card
			.getCard(Figure.ACE, Suit.COINS);
	private static final Card ASSO_COPPE = Card.getCard(Figure.ACE, Suit.CUPS);
	private static final Card RE_SPADE = Card.getCard(Figure.KING, Suit.SWORDS);
	private static final Card TRE_DENARI = Card.getCard(Figure.THREE,
			Suit.COINS);
	private TimerSearchContext ctx;
	private FinalOppositeStartState state;
	private Estimation estimation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new TimerSearchContext();
		ctx.setTimeout(1000000);
		estimation = new Estimation();
		state = new FinalOppositeStartState();
		state.setDeckCards();
		state.setTrump(BRISCOLA);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.FinalAIState#estimate(org.mmarini.briscola.Estimation, org.mmarini.briscola.StrategySearchContext)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimate1() throws InterruptedException {
		state.setPlayerScore(44);
		state.setOppositeScore(40);
		state.setPlayerCards(RE_SPADE, TRE_DENARI);
		state.setOppositeCards(ASSO_COPPE, ASSO_DENARI);
		ctx.estimate(estimation, state);

		assertEquals(0., estimation.getWin(), EPSILON);
		assertEquals(1., estimation.getLoss(), EPSILON);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.FinalAIState#estimate(org.mmarini.briscola.Estimation, org.mmarini.briscola.StrategySearchContext)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimate1i() throws InterruptedException {
		state.setPlayerScore(44);
		state.setOppositeScore(40);
		state.setPlayerCards(RE_SPADE, TRE_DENARI);
		state.setOppositeCards(ASSO_DENARI, ASSO_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(0., estimation.getWin(), EPSILON);
		assertEquals(1., estimation.getLoss(), EPSILON);
	}

}
