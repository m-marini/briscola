/**
 * 
 */
package org.mmarini.briscola;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mmarini.briscola.GameHandler.GameStatus;

/**
 * @author US00852
 * 
 */
public class GameMemento {

	private static final String CARD_SEPARATOR = ",";
	private static final String SEPARATOR = ";";
	private static final String ASSIGNMENT = "=";

	/**
	 * 
	 * @param values
	 * @return
	 */
	public static GameMemento create(String values) {
		GameMemento memento = new GameMemento();
		memento.load(values);
		return memento;
	}

	private Map<String, String> properties;

	/**
	 * 
	 */
	public GameMemento() {
		properties = new HashMap<String, String>();
	}

	/**
	 * 
	 */
	public void clear() {
		properties.clear();
	}

	/**
	 * 
	 * @param cardList
	 * @return
	 */
	private String createCardList(List<Card> cardList) {
		StringBuilder bfr = new StringBuilder();
		boolean first = true;
		for (Card card : cardList) {
			if (first) {
				first = false;
			} else {
				bfr.append(CARD_SEPARATOR);
			}
			int idx = Card.getCardIndex(card);
			bfr.append(idx);
		}
		return bfr.toString();
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		String value = properties.get(key);
		if (value == null)
			return false;
		return Boolean.parseBoolean(value);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Card getCard(String key) {
		String value = properties.get(key);
		if (value == null)
			return null;
		int idx = Integer.parseInt(value);
		if (idx < 0)
			return null;
		return Card.getCard(idx);
	}

	/**
	 * 
	 * @param list
	 * @param key
	 */
	public void getCardList(List<Card> list, String key) {
		list.clear();
		String value = properties.get(key);
		if (value.length() > 0) {
			String[] cards = value.split(CARD_SEPARATOR);
			for (String card : cards) {
				list.add(Card.getCard(Integer.parseInt(card)));
			}
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return Integer.parseInt(properties.get(key));
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public GameStatus getStatus(String key) {
		if (key == null)
			return null;
		String value = properties.get(key);
		return GameStatus.valueOf(value);
	}

	/**
	 * 
	 * @param values
	 */
	public void load(String values) {
		properties.clear();
		String[] args = values.split(SEPARATOR);
		for (String assignment : args) {
			String[] rows = assignment.split(ASSIGNMENT);
			if (rows.length == 2)
				properties.put(rows[0], rows[1]);
			else if (rows.length == 1)
				properties.put(rows[0], "");
		}
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, boolean value) {
		properties.put(key, String.valueOf(value));
	}

	/**
	 * 
	 * @param key
	 * @param card
	 */
	public void set(String key, Card card) {
		if (card != null)
			properties.put(key, String.valueOf(Card.getCardIndex(card)));
	}

	/**
	 * 
	 * @param key
	 * @param status
	 */
	public void set(String key, GameStatus status) {
		properties.put(key, status.toString());
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, int value) {
		properties.put(key, String.valueOf(value));
	}

	/**
	 * 
	 * @param key
	 * @param cardList
	 */
	public void set(String key, List<Card> cardList) {
		properties.put(key, createCardList(cardList));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> entry : properties.entrySet()) {
			builder.append(entry.getKey());
			builder.append(ASSIGNMENT);
			builder.append(entry.getValue());
			builder.append(SEPARATOR);
		}
		return builder.toString();
	}
}
