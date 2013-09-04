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
public class FinalAIStateTest {

	private static final double EPSILON = 10e-6;
	private static final Card BRISCOLA = Card.getCard(Figure.TWO, Suit.SWORDS);
	private static final Card ASSO_DENARI = Card
			.getCard(Figure.ACE, Suit.COINS);
	private static final Card ASSO_COPPE = Card.getCard(Figure.ACE, Suit.CUPS);
	private static final Card RE_SPADE = Card.getCard(Figure.KING, Suit.SWORDS);
	private static final Card TRE_DENARI = Card.getCard(Figure.THREE,
			Suit.COINS);
	private static final Card RE_DENARI = Card.getCard(Figure.KING, Suit.COINS);
	private static final Card ASSO_SPADE = Card
			.getCard(Figure.ACE, Suit.SWORDS);
	private static final Card CAVALLO_COPPE = Card.getCard(Figure.KNIGHT,
			Suit.CUPS);
	private TimerSearchContext ctx;
	private FinalAIState state;
	private Estimation estimation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new TimerSearchContext();
		ctx.setTimeout(1000000);
		estimation = new Estimation();
		state = new FinalAIState();
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
		state.setAiScore(40);
		state.setPlayerScore(44);

		state.addToAiCards(ASSO_COPPE);
		state.addToAiCards(ASSO_DENARI);

		state.addToPlayerCards(RE_SPADE);
		state.addToPlayerCards(TRE_DENARI);
		ctx.estimate(estimation, state);

		assertEquals(ASSO_COPPE, estimation.getBestCard());
		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
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
		state.setAiScore(40);
		state.setPlayerScore(44);
		state.addToAiCards(ASSO_DENARI);
		state.addToAiCards(ASSO_COPPE);
		state.addToPlayerCards(RE_SPADE);
		state.addToPlayerCards(TRE_DENARI);
		ctx.estimate(estimation, state);

		assertEquals(ASSO_COPPE, estimation.getBestCard());
		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.FinalAIState#estimate(org.mmarini.briscola.Estimation, org.mmarini.briscola.StrategySearchContext)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimate2_1() throws InterruptedException {
		state.setAiScore(48);
		state.setPlayerScore(22);
		state.addToAiCards(TRE_DENARI);
		state.addToAiCards(ASSO_COPPE);
		state.addToAiCards(RE_DENARI);
		state.addToPlayerCards(ASSO_SPADE);
		state.addToPlayerCards(ASSO_DENARI);
		state.addToPlayerCards(CAVALLO_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(TRE_DENARI, estimation.getBestCard());
		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.FinalAIState#estimate(org.mmarini.briscola.Estimation, org.mmarini.briscola.StrategySearchContext)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimate2_2() throws InterruptedException {
		state.setAiScore(48);
		state.setPlayerScore(22);
		state.addToAiCards(ASSO_COPPE);
		state.addToAiCards(TRE_DENARI);
		state.addToAiCards(RE_DENARI);
		state.addToPlayerCards(ASSO_SPADE);
		state.addToPlayerCards(ASSO_DENARI);
		state.addToPlayerCards(CAVALLO_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(TRE_DENARI, estimation.getBestCard());
		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
	}

	/**
	 * Test method for
	 * {@link org.mmarini.briscola.FinalAIState#estimate(org.mmarini.briscola.Estimation, org.mmarini.briscola.StrategySearchContext)}
	 * .
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testEstimate2_3() throws InterruptedException {
		state.setAiScore(48);
		state.setPlayerScore(22);
		state.addToAiCards(RE_DENARI);
		state.addToAiCards(ASSO_COPPE);
		state.addToAiCards(TRE_DENARI);
		state.addToPlayerCards(ASSO_SPADE);
		state.addToPlayerCards(ASSO_DENARI);
		state.addToPlayerCards(CAVALLO_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(TRE_DENARI, estimation.getBestCard());
		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
	}
}
