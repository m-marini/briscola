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
public class FinalOppositeMidStateTest {

	private static final double EPSILON = 10e-6;
	private static final Card BRISCOLA = Card.getCard(Figure.TWO, Suit.SWORDS);
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
	private FinalOppositeMidState state;
	private Estimation estimation;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new TimerSearchContext();
		ctx.setTimeout(1000000);
		estimation = new Estimation();
		state = new FinalOppositeMidState();
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
		state.setPlayerCards(RE_DENARI, ASSO_DENARI);
		state.setOppositeCards(TRE_DENARI);
		state.setOppositeCard(CAVALLO_DENARI);
		ctx.estimate(estimation, state);

		assertEquals(RE_DENARI, estimation.getBestCard());
		assertEquals(1., estimation.getWin(), EPSILON);
		assertEquals(0., estimation.getLoss(), EPSILON);
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
		state.setPlayerCards(ASSO_DENARI, RE_DENARI);
		state.setOppositeCards(TRE_DENARI);
		state.setOppositeCard(CAVALLO_DENARI);
		ctx.estimate(estimation, state);

		assertEquals(RE_DENARI, estimation.getBestCard());
		assertEquals(1., estimation.getWin(), EPSILON);
		assertEquals(0., estimation.getLoss(), EPSILON);
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
		state.setPlayerScore(61 - 57);
		state.setOppositeScore(59);
		state.setPlayerCards(ASSO_SPADE, ASSO_DENARI, TRE_COPPE);
		state.setOppositeCards(TRE_DENARI, ASSO_COPPE);
		state.setOppositeCard(RE_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(TRE_COPPE, estimation.getBestCard());
		assertEquals(1., estimation.getWin(), EPSILON);
		assertEquals(0., estimation.getLoss(), EPSILON);
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
		state.setPlayerScore(61 - 57);
		state.setOppositeScore(59);
		state.setPlayerCards(ASSO_DENARI, ASSO_SPADE, TRE_COPPE);
		state.setOppositeCards(TRE_DENARI, ASSO_COPPE);
		state.setOppositeCard(RE_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(TRE_COPPE, estimation.getBestCard());
		assertEquals(1., estimation.getWin(), EPSILON);
		assertEquals(0., estimation.getLoss(), EPSILON);
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
		state.setPlayerScore(61 - 57);
		state.setOppositeScore(59);
		state.setPlayerCards(ASSO_DENARI, TRE_COPPE, ASSO_SPADE);
		state.setOppositeCards(TRE_DENARI, ASSO_COPPE);
		state.setOppositeCard(RE_COPPE);
		ctx.estimate(estimation, state);

		assertEquals(TRE_COPPE, estimation.getBestCard());
		assertEquals(1., estimation.getWin(), EPSILON);
		assertEquals(0., estimation.getLoss(), EPSILON);
	}

}
