package org.mmarini.briscola;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

public class CardTest {

	private static final String RE_SPADE = "KING SWORDS";
	private static final String CAVALLO_SPADE = "KNIGHT SWORDS";
	private static final String FANTE_SPADE = "INFANTRY SWORDS";
	private static final String ASSO_SPADE = "ACE SWORDS";
	private static final String ASSO_BASTONI = "ACE CLUBS";
	private static final String TRE_COPPE = "THREE CUPS";
	private static final String ASSO_COPPE = "ACE CUPS";
	private static final String ASSO_DENARI = "ACE COINS";

	@Test
	public void testGetCardSeedInt() {
		Card card = Card.getCard(Figure.ACE, Suit.COINS);
		assertEquals(Figure.ACE, card.getFigure());
		assertEquals(Suit.COINS, card.getSuit());
		assertEquals(ASSO_DENARI, card.toString());

		card = Card.getCard(Figure.ACE, Suit.CUPS);
		assertEquals(Figure.ACE, card.getFigure());
		assertEquals(Suit.CUPS, card.getSuit());
		assertEquals(ASSO_COPPE, card.toString());

		card = Card.getCard(Figure.THREE, Suit.CUPS);
		assertEquals(Figure.THREE, card.getFigure());
		assertEquals(Suit.CUPS, card.getSuit());
		assertEquals(TRE_COPPE, card.toString());

		card = Card.getCard(Figure.ACE, Suit.CLUBS);
		assertEquals(Figure.ACE, card.getFigure());
		assertEquals(Suit.CLUBS, card.getSuit());
		assertEquals(ASSO_BASTONI, card.toString());

		card = Card.getCard(Figure.ACE, Suit.SWORDS);
		assertEquals(Figure.ACE, card.getFigure());
		assertEquals(Suit.SWORDS, card.getSuit());
		assertEquals(ASSO_SPADE, card.toString());

		card = Card.getCard(Figure.INFANTRY, Suit.SWORDS);
		assertEquals(Figure.INFANTRY, card.getFigure());
		assertEquals(Suit.SWORDS, card.getSuit());
		assertEquals(FANTE_SPADE, card.toString());

		card = Card.getCard(Figure.KNIGHT, Suit.SWORDS);
		assertEquals(Figure.KNIGHT, card.getFigure());
		assertEquals(Suit.SWORDS, card.getSuit());
		assertEquals(CAVALLO_SPADE, card.toString());

		card = Card.getCard(Figure.KING, Suit.SWORDS);
		assertEquals(Figure.KING, card.getFigure());
		assertEquals(Suit.SWORDS, card.getSuit());
		assertEquals(RE_SPADE, card.toString());
	}

	@Test
	public void testGetScore() {
		int score = Card.getCard(Figure.ACE, Suit.SWORDS).getScore();
		assertEquals(11, score);

		score = Card.getCard(Figure.ACE, Suit.CUPS).getScore();
		assertEquals(11, score);

		score = Card.getCard(Figure.THREE, Suit.CUPS).getScore();
		assertEquals(10, score);

		score = Card.getCard(Figure.ACE, Suit.CLUBS).getScore();
		assertEquals(11, score);

		score = Card.getCard(Figure.ACE, Suit.SWORDS).getScore();
		assertEquals(11, score);

		score = Card.getCard(Figure.INFANTRY, Suit.SWORDS).getScore();
		assertEquals(2, score);

		score = Card.getCard(Figure.KNIGHT, Suit.SWORDS).getScore();
		assertEquals(3, score);

		score = Card.getCard(Figure.KING, Suit.SWORDS).getScore();
		assertEquals(4, score);
	}

}
