/**
 * 
 */
package org.mmarini.briscola;

/**
 * 
 * @author US00852
 * 
 */
public class Card {
	public enum Figure {
		TWO, FOUR, FIVE, SIX, SEVEN, INFANTRY, KNIGHT, KING, THREE, ACE
	}

	public enum Suit {
		COINS, CUPS, CLUBS, SWORDS
	}

	private static final int KING_SCORE = 4;
	private static final int KNIGHT_SCORE = 3;
	private static final int INFANTRY_SCORE = 2;
	private static final int THREE_SCORE = 10;
	private static final int ACE_SCORE = 11;
	private static final int NONE_SCORE = 0;

	private static Card[] deck;

	/**
	 * 
	 * @param value
	 * @param suit
	 * @return
	 */
	public static Card getCard(Figure value, Suit suit) {
		for (Card card : getDeck()) {
			if (suit.equals(card.getSuit()) && value.equals(card.getFigure())) {
				return card;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public static Card getCard(int index) {
		return deck[index];
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	public static int getCardIndex(Card card) {
		int n = deck.length;
		for (int i = 0; i < n; ++i) {
			if (card.equals(deck[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @return the deck
	 */
	public static Card[] getDeck() {
		if (deck == null) {
			deck = new Card[40];
			int idx = 0;
			for (Suit seed : Suit.values()) {
				for (Figure value : Figure.values()) {
					deck[idx++] = new Card(value, seed);
				}
			}
		}
		return deck;
	}

	private Suit seed;

	private Figure figure;

	/**
	 * 
	 * @param figure
	 * @param seed
	 */
	private Card(Figure figure, Suit seed) {
		this.seed = seed;
		this.figure = figure;
	}

	/**
	 * 
	 * @param c1
	 * @return
	 */
	public int compare(Card c1) {
		return figure.compareTo(c1.figure);
	}

	/**
	 * @return the value
	 */
	public Figure getFigure() {
		return figure;
	}

	/**
	 * 
	 * @return
	 */
	public int getScore() {
		switch (figure) {
		case ACE:
			return ACE_SCORE;
		case THREE:
			return THREE_SCORE;
		case INFANTRY:
			return INFANTRY_SCORE;
		case KNIGHT:
			return KNIGHT_SCORE;
		case KING:
			return KING_SCORE;
		default:
			return NONE_SCORE;
		}
	}

	/**
	 * @return the seed
	 */
	public Suit getSuit() {
		return seed;
	}

	/**
	 * 
	 * @param c2
	 * @return
	 */
	public boolean hasSameSeed(Card c2) {
		return seed.equals(c2.seed);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(figure).append(" ").append(seed);
		return builder.toString();
	}

	/**
	 * 
	 * @param card
	 * @param trump
	 * @return
	 */
	public boolean wins(Card card, Card trump) {
		boolean winner = true;
		if (hasSameSeed(card)) {
			if (this.compare(card) < 0) {
				winner = false;
			}
		} else if (card.hasSameSeed(trump)) {
			winner = false;
		}
		return winner;
	}
}
