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
		cardMap.put(Card.getCard(Figure.ACE, Suit.COINS),
				R.drawable.card01trevigiane);
		cardMap.put(Card.getCard(Figure.TWO, Suit.COINS),
				R.drawable.card02trevigiane);
		cardMap.put(Card.getCard(Figure.THREE, Suit.COINS),
				R.drawable.card03trevigiane);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.COINS),
				R.drawable.card04trevigiane);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.COINS),
				R.drawable.card05trevigiane);
		cardMap.put(Card.getCard(Figure.SIX, Suit.COINS),
				R.drawable.card06trevigiane);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.COINS),
				R.drawable.card07trevigiane);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.COINS),
				R.drawable.card08trevigiane);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.COINS),
				R.drawable.card09trevigiane);
		cardMap.put(Card.getCard(Figure.KING, Suit.COINS),
				R.drawable.card10trevigiane);
		cardMap.put(Card.getCard(Figure.ACE, Suit.CUPS),
				R.drawable.card11trevigiane);
		cardMap.put(Card.getCard(Figure.TWO, Suit.CUPS),
				R.drawable.card12trevigiane);
		cardMap.put(Card.getCard(Figure.THREE, Suit.CUPS),
				R.drawable.card13trevigiane);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.CUPS),
				R.drawable.card14trevigiane);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.CUPS),
				R.drawable.card15trevigiane);
		cardMap.put(Card.getCard(Figure.SIX, Suit.CUPS),
				R.drawable.card16trevigiane);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.CUPS),
				R.drawable.card17trevigiane);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.CUPS),
				R.drawable.card18trevigiane);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.CUPS),
				R.drawable.card19trevigiane);
		cardMap.put(Card.getCard(Figure.KING, Suit.CUPS),
				R.drawable.card20trevigiane);
		cardMap.put(Card.getCard(Figure.ACE, Suit.CLUBS),
				R.drawable.card31trevigiane);
		cardMap.put(Card.getCard(Figure.TWO, Suit.CLUBS),
				R.drawable.card32trevigiane);
		cardMap.put(Card.getCard(Figure.THREE, Suit.CLUBS),
				R.drawable.card33trevigiane);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.CLUBS),
				R.drawable.card34trevigiane);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.CLUBS),
				R.drawable.card35trevigiane);
		cardMap.put(Card.getCard(Figure.SIX, Suit.CLUBS),
				R.drawable.card36trevigiane);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.CLUBS),
				R.drawable.card37trevigiane);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.CLUBS),
				R.drawable.card38trevigiane);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.CLUBS),
				R.drawable.card39trevigiane);
		cardMap.put(Card.getCard(Figure.KING, Suit.CLUBS),
				R.drawable.card40trevigiane);
		cardMap.put(Card.getCard(Figure.ACE, Suit.SWORDS),
				R.drawable.card21trevigiane);
		cardMap.put(Card.getCard(Figure.TWO, Suit.SWORDS),
				R.drawable.card22trevigiane);
		cardMap.put(Card.getCard(Figure.THREE, Suit.SWORDS),
				R.drawable.card23trevigiane);
		cardMap.put(Card.getCard(Figure.FOUR, Suit.SWORDS),
				R.drawable.card24trevigiane);
		cardMap.put(Card.getCard(Figure.FIVE, Suit.SWORDS),
				R.drawable.card25trevigiane);
		cardMap.put(Card.getCard(Figure.SIX, Suit.SWORDS),
				R.drawable.card26trevigiane);
		cardMap.put(Card.getCard(Figure.SEVEN, Suit.SWORDS),
				R.drawable.card27trevigiane);
		cardMap.put(Card.getCard(Figure.INFANTRY, Suit.SWORDS),
				R.drawable.card28trevigiane);
		cardMap.put(Card.getCard(Figure.KNIGHT, Suit.SWORDS),
				R.drawable.card29trevigiane);
		cardMap.put(Card.getCard(Figure.KING, Suit.SWORDS),
				R.drawable.card30trevigiane);
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
