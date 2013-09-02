package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

public class GameOppositeStateTest {

	private static final double EPSILON = 1e-3;
	private static final Card ASSO_DENARI = Card
			.getCard(Figure.ACE, Suit.COINS);
	private static final Card DUE_SPADE = Card.getCard(Figure.TWO, Suit.SWORDS);
	private static final Card DUE_COPPE = Card.getCard(Figure.TWO, Suit.CUPS);
	private static final Card TRE_BASTONI = Card.getCard(Figure.THREE,
			Suit.CLUBS);
	private static final Card RE_DENARI = Card.getCard(Figure.KING, Suit.COINS);

	private GamePlayerState state;
	private TimerSearchContext ctx;
	private Estimation estimation;

	@Before
	public void setUp() throws Exception {
		state = new GamePlayerState();
		state.setTrump(DUE_SPADE);
		estimation = new Estimation();
		ctx = new TimerSearchContext();
		ctx.setTimeout(60000);
	}

	@Test
	public void testEstimate() throws InterruptedException {
		Card[] deckCards = AbstractGameState.createAndRemove(Card.getDeck(),
				ASSO_DENARI, DUE_COPPE, TRE_BASTONI);
		state.setDeckCards(deckCards);
		state.setPlayerCard(RE_DENARI);
		state.setAiCards(DUE_COPPE, ASSO_DENARI, TRE_BASTONI);
		state.setAiScore(0);
		state.setPlayerScore(0);
		ctx.setMaxDeep(0);
		ctx.estimate(estimation, state);

		assertEquals(ASSO_DENARI, estimation.getBestCard());
		assertFalse(estimation.isConfident());
		assertEquals(3.33e-3, estimation.getAiWinProb(), EPSILON);
		assertEquals(19e-6, estimation.getPlayerWinProb(), EPSILON);
	}

}
