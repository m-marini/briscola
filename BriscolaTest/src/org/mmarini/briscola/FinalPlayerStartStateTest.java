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
public class FinalPlayerStartStateTest {

	private static final double EPSILON = 10e-6;
	private static final Card BRISCOLA = Card.getCard(Figure.TWO, Suit.SWORDS);
	private static final Card ASSO_DENARI = Card
			.getCard(Figure.ACE, Suit.COINS);
	private static final Card ASSO_COPPE = Card.getCard(Figure.ACE, Suit.CUPS);
	private static final Card RE_SPADE = Card.getCard(Figure.KING, Suit.SWORDS);
	private static final Card TRE_DENARI = Card.getCard(Figure.THREE,
			Suit.COINS);
	private TimerSearchContext ctx;
	private FinalPlayerStartState state;
	private Estimation estimation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new TimerSearchContext();
		ctx.setTimeout(1000000);
		estimation = new Estimation();
		state = new FinalPlayerStartState();
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
		state.setAiScore(44);
		state.setPlayerScore(40);
		state.addToAiCards(RE_SPADE);
		state.addToAiCards(TRE_DENARI);
		state.addToPlayerCards(ASSO_COPPE);
		state.addToPlayerCards(ASSO_DENARI);

		ctx.estimate(estimation, state);

		assertEquals(0., estimation.getAiWinProb(), EPSILON);
		assertEquals(1., estimation.getPlayerWinProb(), EPSILON);
		assertEquals(ASSO_COPPE, estimation.getBestCard());
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
		state.setAiScore(44);
		state.setPlayerScore(40);
		state.addToAiCards(RE_SPADE);
		state.addToAiCards(TRE_DENARI);
		state.addToPlayerCards(ASSO_DENARI);
		state.addToPlayerCards(ASSO_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(0., estimation.getAiWinProb(), EPSILON);
		assertEquals(1., estimation.getPlayerWinProb(), EPSILON);
		assertEquals(ASSO_COPPE, estimation.getBestCard());
	}

}
