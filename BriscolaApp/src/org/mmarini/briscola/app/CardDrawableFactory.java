/**
 * 
 */
package org.mmarini.briscola.app;

import java.util.HashMap;
import java.util.Map;

import org.mmarini.briscola.Card;
import org.mmarini.briscola.Card.Figure;
import org.mmarini.briscola.Card.Suit;

/**
 * @author US00852
 * 
 */
public class CardDrawableFactory {

	private static CardDrawableFactory instance = new CardDrawableFactory();

	/**
	 * @return the instance
	 */
	public static CardDrawableFactory getInstance() {
		return instance;
	}

	private Map<Card, Integer> cardMap;

	/**
	 * 
	 */
	protected CardDrawableFactory() {
		cardMap = new HashMap<Card, Integer>();
		cardMap.put(Card.getCard(Figure.ACE, Suit.COINS), R.drawable.ic_card01);
		cardMap.put(Card.getCard(Figure.TWO, Suit.COINS), R.drawable.ic_card02);
		cardMap.put(Card.getCard(Figure.THREE, Suit.COINS),
				R.drawable.ic_card03);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.COINS), R.drawable.ic_card04);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.COINS), R.drawable.ic_card05);
		cardMap.put(Card.getCard(Figure.SIX, Suit.COINS), R.drawable.ic_card06);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.COINS),
				R.drawable.ic_card07);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.COINS),
				R.drawable.ic_card08);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.COINS),
				R.drawable.ic_card09);
		cardMap.put(Card.getCard(Figure.KING, Suit.COINS), R.drawable.ic_card10);
		cardMap.put(Card.getCard(Figure.ACE, Suit.CUPS), R.drawable.ic_card11);
		cardMap.put(Card.getCard(Figure.TWO, Suit.CUPS), R.drawable.ic_card12);
		cardMap.put(Card.getCard(Figure.THREE, Suit.CUPS), R.drawable.ic_card13);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.CUPS), R.drawable.ic_card14);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.CUPS), R.drawable.ic_card15);
		cardMap.put(Card.getCard(Figure.SIX, Suit.CUPS), R.drawable.ic_card16);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.CUPS), R.drawable.ic_card17);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.CUPS),
				R.drawable.ic_card18);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.CUPS),
				R.drawable.ic_card19);
		cardMap.put(Card.getCard(Figure.KING, Suit.CUPS), R.drawable.ic_card20);
		cardMap.put(Card.getCard(Figure.ACE, Suit.CLUBS), R.drawable.ic_card31);
		cardMap.put(Card.getCard(Figure.TWO, Suit.CLUBS), R.drawable.ic_card32);
		cardMap.put(Card.getCard(Figure.THREE, Suit.CLUBS),
				R.drawable.ic_card33);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.CLUBS), R.drawable.ic_card34);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.CLUBS), R.drawable.ic_card35);
		cardMap.put(Card.getCard(Figure.SIX, Suit.CLUBS), R.drawable.ic_card36);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.CLUBS),
				R.drawable.ic_card37);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.CLUBS),
				R.drawable.ic_card38);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.CLUBS),
				R.drawable.ic_card39);
		cardMap.put(Card.getCard(Figure.KING, Suit.CLUBS), R.drawable.ic_card40);
		cardMap.put(Card.getCard(Figure.ACE, Suit.SWORDS), R.drawable.ic_card21);
		cardMap.put(Card.getCard(Figure.TWO, Suit.SWORDS), R.drawable.ic_card22);
		cardMap.put(Card.getCard(Figure.THREE, Suit.SWORDS),
				R.drawable.ic_card23);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.SWORDS),
				R.drawable.ic_card24);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.SWORDS),
				R.drawable.ic_card25);
		cardMap.put(Card.getCard(Figure.SIX, Suit.SWORDS), R.drawable.ic_card26);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.SWORDS),
				R.drawable.ic_card27);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.SWORDS),
				R.drawable.ic_card28);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.SWORDS),
				R.drawable.ic_card29);
		cardMap.put(Card.getCard(Figure.KING, Suit.SWORDS),
				R.drawable.ic_card30);
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	public int findResId(Card card) {
		return cardMap.get(card);
	}

}
