package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

public class VirtualPlayerMidStateTest {

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
	private static final Card DUE_SPADE = Card.getCard(Figure.TWO, Suit.SWORDS);

	private VirtualPlayerMidState state;
	private TimerSearchContext ctx;
	private Estimation estimation;
	private List<Card> cards;

	@Before
	public void setUp() throws Exception {
		state = new VirtualPlayerMidState();
		ctx = new TimerSearchContext();
		estimation = new Estimation();

		ctx.setTimeout(60000);
		state.setTrump(DUE_SPADE);
		cards = new ArrayList<>(40);
		for (Card c : Card.getDeck()) {
			cards.add(c);
		}
	}

	@Test
	public void testEstimate() throws InterruptedException {

		state.addToDeckCards(cards);
		state.removeFromDeckCards(ASSO_DENARI);
		state.removeFromDeckCards(DUE_COPPE);
		state.removeFromDeckCards(TRE_BASTONI);
		state.removeFromDeckCards(ASSO_BASTONI);
		state.removeFromDeckCards(TRE_COPPE);
		state.removeFromDeckCards(QUATTRO_SPADE);

		state.addToAiCards(ASSO_DENARI);
		state.addToAiCards(DUE_COPPE);
		state.addToAiCards(TRE_BASTONI);

		state.addToPlayerCards(TRE_COPPE);
		state.addToPlayerCards(QUATTRO_SPADE);

		state.setPlayedCard(ASSO_BASTONI);
		state.setAiScore(0);
		state.setPlayerScore(0);
		ctx.setMaxDeep(0);
		ctx.estimate(estimation, state);

		assertFalse(estimation.isConfident());
		assertEquals(DUE_COPPE, estimation.getBestCard());
		assertEquals(0, estimation.getAiWinProb(), EPSILON);
		assertEquals(11. / 61. + 1. / 27 / 62, estimation.getPlayerWinProb(),
				EPSILON);
	}

}
