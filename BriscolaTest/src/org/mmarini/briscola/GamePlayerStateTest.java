package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

public class GamePlayerStateTest {

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
	private List<Card> cards;

	@Before
	public void setUp() throws Exception {
		state = new GamePlayerState();
		state.setTrump(DUE_SPADE);
		estimation = new Estimation();
		ctx = new TimerSearchContext();
		ctx.setTimeout(60000);
		cards = new ArrayList<>(40);
		for (Card c : Card.getDeck()) {
			cards.add(c);
		}
	}

	@Test
	public void testEstimate() throws InterruptedException {
		state.addToDeckCards(cards);
		state.removeFromDeckCards(DUE_COPPE);
		state.removeFromDeckCards(ASSO_DENARI);
		state.removeFromDeckCards(TRE_BASTONI);
		state.removeFromDeckCards(RE_DENARI);

		state.addToAiCards(DUE_COPPE);
		state.addToAiCards(ASSO_DENARI);
		state.addToAiCards(TRE_BASTONI);
		state.setPlayerCard(RE_DENARI);
		state.setAiScore(0);
		state.setPlayerScore(0);
		ctx.setMaxDeep(0);
		ctx.estimate(estimation, state);

		assertFalse(estimation.isConfident());
		assertEquals(15., estimation.getAiWinProb() * 61, EPSILON);
		assertEquals(3.054 * 61 / 27 / 62, estimation.getPlayerWinProb() * 61,
				EPSILON);
		assertEquals(ASSO_DENARI, estimation.getBestCard());
	}

}
