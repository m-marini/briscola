package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

public class VirtualOppositeMidStateTest {

	private static final Card DUE_SPADE = Card.getCard(Figure.TWO, Suit.SWORDS);
	private VirtualOppositeMidState state;
	private TimerSearchContext ctx;
	private Estimation estimation;
	private static final Card ASSO_BASTONI = Card.getCard(Figure.ACE,
			Suit.CLUBS);
	private static final Card ASSO_DENARI = Card
			.getCard(Figure.ACE, Suit.COINS);
	private static final Card DUE_COPPE = Card.getCard(Figure.TWO, Suit.CUPS);
	private static final double EPSILON = 1e-3;
	private static final Card QUATTRO_SPADE = Card.getCard(Figure.FOUR,
			Suit.SWORDS);
	private static final Card TRE_BASTONI = Card.getCard(Figure.THREE,
			Suit.CLUBS);
	private static final Card TRE_COPPE = Card.getCard(Figure.THREE, Suit.CUPS);

	@Before
	public void setUp() throws Exception {
		state = new VirtualOppositeMidState();
		ctx = new TimerSearchContext();
		estimation = new Estimation();

		ctx.setTimeout(60000);
		state.setTrump(DUE_SPADE);
	}

	@Test
	public void testEstimate() throws InterruptedException {
		Card[] deckCards = AbstractGameState.createAndRemove(Card.getDeck(),
				ASSO_DENARI, DUE_COPPE, TRE_BASTONI, ASSO_BASTONI, TRE_COPPE,
				QUATTRO_SPADE);
		state.setDeckCards(deckCards);
		state.setPlayerCards(ASSO_DENARI, DUE_COPPE, TRE_BASTONI);
		state.setOppositeCards(TRE_COPPE, QUATTRO_SPADE);
		state.setPlayedCard(ASSO_BASTONI);
		state.setPlayerScore(0);
		state.setOppositeScore(0);
		ctx.setMaxDeep(0);
		ctx.estimate(estimation, state);

		assertFalse(estimation.isConfident());
		assertEquals(DUE_COPPE, estimation.getBestCard());
		assertEquals(0, estimation.getWin(), EPSILON);
		assertEquals(11. / 61., estimation.getLoss(), EPSILON);
	}

}
