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
public class FinalPlayerMidStateTest {

	private static final double EPSILON = 10e-6;
	private static final Card DUE_SPADE = Card.getCard(Figure.TWO, Suit.SWORDS);
	private static final Card ASSO_DENARI = Card
			.getCard(Figure.ACE, Suit.COINS);
	private static final Card TRE_DENARI = Card.getCard(Figure.THREE,
			Suit.COINS);
	private static final Card RE_DENARI = Card.getCard(Figure.KING, Suit.COINS);
	private static final Card CAVALLO_DENARI = Card.getCard(Figure.KNIGHT,
			Suit.COINS);
	private static final Card TRE_COPPE = Card.getCard(Figure.THREE, Suit.CUPS);
	private static final Card ASSO_SPADE = Card
			.getCard(Figure.ACE, Suit.SWORDS);
	private static final Card ASSO_COPPE = Card.getCard(Figure.ACE, Suit.CUPS);
	private static final Card RE_COPPE = Card.getCard(Figure.KING, Suit.CUPS);
	private TimerSearchContext ctx;
	private FinalPlayerMidState state;
	private Estimation estimation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new TimerSearchContext();
		ctx.setTimeout(1000000);
		estimation = new Estimation();
		state = new FinalPlayerMidState();
		state.setTrump(DUE_SPADE);
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
		state.addToAiCards(RE_DENARI);
		state.addToAiCards(ASSO_DENARI);
		state.addToPlayerCards(TRE_DENARI);
		state.setPlayerCard(CAVALLO_DENARI);
		ctx.estimate(estimation, state);

		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
		assertEquals(RE_DENARI, estimation.getBestCard());
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
		state.addToAiCards(ASSO_DENARI);
		state.addToAiCards(RE_DENARI);
		state.addToPlayerCards(TRE_DENARI);
		state.setPlayerCard(CAVALLO_DENARI);
		ctx.estimate(estimation, state);

		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
		assertEquals(RE_DENARI, estimation.getBestCard());
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
		state.setAiScore(61 - 57);
		state.setPlayerScore(59);
		state.addToAiCards(ASSO_SPADE);
		state.addToAiCards(ASSO_DENARI);
		state.addToAiCards(TRE_COPPE);
		state.addToPlayerCards(TRE_DENARI);
		state.addToPlayerCards(ASSO_COPPE);
		state.setPlayerCard(RE_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
		assertEquals(TRE_COPPE, estimation.getBestCard());
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
		state.setAiScore(61 - 57);
		state.setPlayerScore(59);
		state.addToAiCards(ASSO_DENARI);
		state.addToAiCards(ASSO_SPADE);
		state.addToAiCards(TRE_COPPE);
		state.addToPlayerCards(TRE_DENARI);
		state.addToPlayerCards(ASSO_COPPE);
		state.setPlayerCard(RE_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
		assertEquals(TRE_COPPE, estimation.getBestCard());
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
		state.setAiScore(61 - 57);
		state.setPlayerScore(59);
		state.addToAiCards(ASSO_DENARI);
		state.addToAiCards(TRE_COPPE);
		state.addToAiCards(ASSO_SPADE);
		state.addToPlayerCards(TRE_DENARI);
		state.addToPlayerCards(ASSO_COPPE);
		state.setPlayerCard(RE_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(1., estimation.getAiWinProb(), EPSILON);
		assertEquals(0., estimation.getPlayerWinProb(), EPSILON);
		assertEquals(TRE_COPPE, estimation.getBestCard());
	}

}
