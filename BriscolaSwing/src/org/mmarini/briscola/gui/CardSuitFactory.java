/**
 * 
 */
package org.mmarini.briscola.gui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.mmarini.briscola.Card;

/**
 * @author US00852
 * 
 */
public class CardSuitFactory {
	/**
	 * 
	 */
	public CardSuitFactory() {
	}

	/**
	 * 
	 * @param card
	 * @return
	 */
	private String createImageId(Card card) {
		int id = 0;
		switch (card.getSuit()) {
		case COINS:
			break;
		case CUPS:
			id += 10;
			break;
		case SWORDS:
			id += 20;
			break;
		case CLUBS:
			id += 30;
			break;
		}
		switch (card.getFigure()) {
		case ACE:
			id += 1;
			break;
		case TWO:
			id += 2;
			break;
		case THREE:
			id += 3;
			break;
		case FOUR:
			id += 4;
			break;
		case FIVE:
			id += 5;
			break;
		case SIX:
			id += 6;
			break;
		case SEVEN:
			id += 7;
			break;
		case INFANTRY:
			id += 8;
			break;
		case KNIGHT:
			id += 9;
			break;
		case KING:
			id += 10;
			break;
		}
		return id < 10 ? "0" + id : String.valueOf(id);
	}

	/**
	 * @return
	 */
	public Map<Card, ImageIcon> createMap() {
		Map<Card, ImageIcon> map = new HashMap<>();
		for (Card c : Card.getDeck()) {
			String name = "/img/" + createImageId(c) + "Trevigiane.jpg";
			URL url = getClass().getResource(name);
			ImageIcon image = new ImageIcon(url);
			map.put(c, image);
		}
		return map;
	}

	/**
	 * 
	 * @return
	 */
	public ImageIcon createRetro() {
		URL url = getClass().getResource("/img/retroTrevigiane.jpg");
		ImageIcon image = new ImageIcon(url);
		return image;
	}
}
