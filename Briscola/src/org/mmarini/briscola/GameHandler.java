/**
 * 
 */
package org.mmarini.briscola;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author US00852
 * 
 */
public class GameHandler {
	private static Logger logger = LoggerFactory.getLogger(GameHandler.class);

	private AnalyzerListener analyzerListener;
	private List<Card> playerCards;
	private boolean playerHand;
	private Card trump;
	private List<Card> deck;
	private Random random;
	private int playerScore;
	private int aiScore;
	private Card aiCard;
	private Card playerCard;
	private boolean finished;
	private List<Card> aiCards;
	private boolean playerFirstHand;
	private int playerWonGame;
	private int aiWonGame;

	private boolean playerWinner;

	private boolean aiWinner;

	private long timeout;

	private boolean confident;

	private double aiLossProbability;

	private double aiWinProbability;

	private int level;

	private TimerSearchContext ctx;

	/**
	 * 
	 */
	public GameHandler() {
		playerCards = new ArrayList<Card>(3);
		aiCards = new ArrayList<Card>(3);
		deck = new ArrayList<Card>(40);
		random = new Random();
		finished = true;
		playerFirstHand = random.nextBoolean();
		ctx = new TimerSearchContext();
	}

	/**
	 * 
	 */
	private void analise() {
		AbstractGameState state = createState();
		Estimation estimation = new Estimation();
		Card bestCard = null;
		ctx.setTimeout(timeout);
		int maxLevel = 0;
		try {
			do {
				ctx.setMaxDeep(maxLevel);
				ctx.estimate(estimation, state);
				bestCard = estimation.getBestCard();
				aiWinProbability = estimation.getWin();
				aiLossProbability = estimation.getLoss();
				confident = estimation.isConfident();
				level = maxLevel;
				analyzerListener.notifyAnalysis(this);
				++maxLevel;
			} while (!estimation.isConfident());
		} catch (InterruptedException e) {
		}
		playAi(bestCard);
	}

	/**
	 * 
	 */
	public void closeHand() {
		boolean win;
		if (playerHand) {
			win = playerCard.wins(aiCard, trump);
		} else {
			win = !aiCard.wins(playerCard, trump);
		}
		int score = aiCard.getScore() + playerCard.getScore();
		if (win) {
			playerScore += score;
		} else {
			aiScore += score;
		}
		playerCard = null;
		aiCard = null;
		playerWinner = playerScore > 60;
		aiWinner = aiScore > 60;
		finished = playerWinner || aiWinner || playerCards.isEmpty();
		playerHand = win;
		if (finished) {
			if (aiWinner)
				++aiWonGame;
			if (playerWinner)
				++playerWonGame;
		}
	}

	/**
	 * 
	 * @return
	 */
	private AbstractGameState createState() {
		AbstractGameState s;
		if (playerHand) {
			if (deck.isEmpty()) {
				FinalOppositeMidState state = new FinalOppositeMidState();
				state.setDeckCards();
				state.setOppositeCards(playerCards.toArray(new Card[0]));
				state.setOppositeCard(playerCard);
				s = state;
			} else {
				List<Card> newDeck = new ArrayList<Card>(deck);
				newDeck.addAll(playerCards);
				GameOppositeState state = new GameOppositeState();
				state.setDeckCards(newDeck.toArray(new Card[0]));
				state.setOppositeCard(playerCard);
				s = state;
			}
		} else {
			if (deck.isEmpty()) {
				FinalAIState state = new FinalAIState();
				state.setOppositeCards(playerCards.toArray(new Card[0]));
				state.setDeckCards();
				s = state;
			} else {
				List<Card> newDeck = new ArrayList<Card>(deck);
				newDeck.addAll(playerCards);
				GameAIState state = new GameAIState();
				state.setDeckCards(newDeck.toArray(new Card[0]));
				s = state;
			}
		}
		s.setPlayerCards(aiCards.toArray(new Card[0]));
		s.setPlayerScore(aiScore);
		s.setOppositeScore(playerScore);
		s.setTrump(trump);
		return s;
	}

	/**
	 * 
	 */
	public void deal() {
		int n = deck.size();
		if (finished) {
			initNewGame();
		} else if (n == 1) {
			Card card = dealCard(deck);
			if (playerHand) {
				playerCards.add(card);
				aiCards.add(trump);
			} else {
				aiCards.add(card);
				playerCards.add(trump);
			}
		} else if (n > 1) {
			Card card = dealCard(deck);
			playerCards.add(card);
			card = dealCard(deck);
			aiCards.add(card);
		}
	}

	/**
	 * 
	 * @return
	 */
	private Card dealCard(List<Card> list) {
		int n = list.size();
		int idx = 0;
		if (n > 1)
			idx = random.nextInt(n);
		Card card = list.remove(idx);
		return card;
	}

	/**
	 * 
	 * @return
	 */
	public Card getAiCard() {
		return aiCard;
	}

	/**
	 * 
	 * @return
	 */
	public double getAiLossProbability() {
		return aiLossProbability;
	}

	/**
	 * 
	 * @return
	 */
	public int getAiScore() {
		return aiScore;
	}

	/**
	 * 
	 * @return
	 */
	public double getAiWinProbability() {
		return aiWinProbability;
	}

	/**
	 * @return the aIWonGame
	 */
	public int getAiWonGame() {
		return aiWonGame;
	}

	/**
	 * 
	 * @return
	 */
	public int getDeckCount() {
		return deck.size();
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the playerCard
	 */
	public Card getPlayerCard() {
		return playerCard;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public Card getPlayerCard(int i) {
		return playerCards.get(i);
	}

	/**
	 * 
	 * @return
	 */
	public List<Card> getPlayerCards() {
		return playerCards;
	}

	/**
	 * 
	 * @return
	 */
	public int getPlayerScore() {
		return playerScore;
	}

	/**
	 * @return the playerWonGame
	 */
	public int getPlayerWonGame() {
		return playerWonGame;
	}

	/**
	 * 
	 * @return
	 */
	public Card getTrump() {
		return trump;
	}

	/**
	 * 
	 */
	private void initNewGame() {
		playerFirstHand = !playerFirstHand;
		playerHand = playerFirstHand;
		playerScore = 0;
		aiScore = 0;
		deck.clear();
		for (Card c : Card.getDeck()) {
			deck.add(c);
		}
		playerCards.clear();
		aiCards.clear();
		for (int i = 0; i < 3; ++i) {
			Card card = dealCard(deck);
			playerCards.add(card);
			card = dealCard(deck);
			aiCards.add(card);
		}
		trump = dealCard(deck);
	}

	/**
	 * @return the aiWinner
	 */
	public boolean isAiWinner() {
		return aiWinner;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isConfident() {
		return confident;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPlayerHand() {
		return playerHand;
	}

	/**
	 * 
	 * @param card
	 */
	public void play(Card card) {
		playerCard = card;
		playerCards.remove(card);
	}

	/**
	 * 
	 * @param card
	 */
	private void playAi(Card card) {
		aiCards.remove(card);
		aiCard = card;
	}

	/**
	 * s
	 * 
	 * @param listener
	 */
	public void setAnalyzerListener(AnalyzerListener listener) {
		analyzerListener = listener;
	}

	/**
	 * 
	 * @param seed
	 */
	public void setSeed(long seed) {
		random.setSeed(seed);
		playerFirstHand = random.nextBoolean();
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 *
	 */
	public void stopAnalysis() {
		ctx.setTimeout(0);
	}

	/**
	 * 
	 */
	public void think() {
		logger.debug("Thinking ...");
		level = 0;
		aiWinProbability = 0;
		aiLossProbability = 0;
		confident = false;
		if (aiCards.size() == 1) {
			playAi(aiCards.get(0));
		} else {
			analise();
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getAiCardCount() {
		return aiCards.size();
	}

	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return finished;
	}
}
