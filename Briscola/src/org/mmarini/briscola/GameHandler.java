/**
 * 
 */
package org.mmarini.briscola;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author US00852
 * 
 */
public class GameHandler {
	public enum GameStatus {
		DEAL, AI_MOVE, PLAYER_MOVE, CLOSE_HAND, FINISHED
	}

	private static final String STATUS_KEY = "status";
	private static final String PLAYER_CARDS_KEY = "playerCards";
	private static final String AI_CARDS_KEY = "aiCards";
	private static final String DECK_KEY = "deck";
	private static final String PLAYER_SCORE_KEY = "playerScore";
	private static final String AI_SCORE_KEY = "aiScore";
	private static final String TRUMP_KEY = "trump";
	private static final String PLAYER_WON_GAME_KEY = "playerWonGame";
	private static final String AI_WON_GAME_KEY = "aiWonGame";
	private static final String AI_CARD_KEY = "aiCard";
	private static final String PLAYER_CARD_KEY = "playerCard";
	private static final String PLAYER_HAND_KEY = "playerHand";

	private static final String PLAYER_FIRST_HAND_KEY = "playerFirstHand";

	private static Logger logger = LoggerFactory.getLogger(GameHandler.class);

	private AnalyzerListener analyzerListener;
	private List<Card> playerCards;
	private List<Card> aiCards;
	private List<Card> deck;
	private Card trump;
	private int playerScore;
	private int aiScore;
	private boolean playerHand;
	private boolean playerFirstHand;
	private Random random;
	private Card aiCard;
	private Card playerCard;
	private int playerWonGame;
	private int aiWonGame;
	private long timeout;
	private boolean confident;
	private double playerWinProbability;
	private double aiWinProbability;
	private int level;
	private GameStatus status;
	private TimerSearchContext ctx;
	private Card bestCard;

	/**
	 * 
	 */
	public GameHandler() {
		playerCards = new ArrayList<Card>(3);
		aiCards = new ArrayList<Card>(3);
		deck = new ArrayList<Card>(40);
		random = new Random();
		playerFirstHand = random.nextBoolean();
		ctx = new TimerSearchContext();
		status = GameStatus.FINISHED;
	}

	/**
	 * 
	 */
	public void analyze() {
		logger.debug("Thinking ...");
		level = 0;
		aiWinProbability = 0;
		playerWinProbability = 0;
		confident = false;
		if (aiCards.size() == 1) {
			playAi(aiCards.get(0));
		} else {
			analyzeTreeGame();
		}
	}

	/**
	 * 
	 */
	private void analyzeTreeGame() {
		AbstractGameState state = createState();
		Estimation estimation = new Estimation();
		bestCard = null;
		ctx.setTimeout(timeout);
		int analizedLevel = 0;
		try {
			do {
				ctx.setMaxDeep(analizedLevel);
				ctx.estimate(estimation, state);
				bestCard = estimation.getBestCard();
				aiWinProbability = estimation.getAiWinProb();
				playerWinProbability = estimation.getPlayerWinProb();
				confident = estimation.isConfident();
				level = analizedLevel;
				if (analyzerListener != null) {
					analyzerListener.notifyAnalysis(this);
				}
				++analizedLevel;
			} while (!estimation.isConfident());
		} catch (InterruptedException e) {
		}
		playAi(bestCard);
	}

	/**
	 * 
	 * @param memento
	 */
	public void applyMemento(GameMemento memento) {
		memento.getCardList(playerCards, PLAYER_CARDS_KEY);
		memento.getCardList(aiCards, AI_CARDS_KEY);
		memento.getCardList(deck, DECK_KEY);
		playerScore = memento.getInt(PLAYER_SCORE_KEY);
		aiScore = memento.getInt(AI_SCORE_KEY);
		trump = memento.getCard(TRUMP_KEY);
		playerWonGame = memento.getInt(PLAYER_WON_GAME_KEY);
		aiWonGame = memento.getInt(AI_WON_GAME_KEY);
		aiCard = memento.getCard(AI_CARD_KEY);
		playerCard = memento.getCard(PLAYER_CARD_KEY);
		playerHand = memento.getBoolean(PLAYER_HAND_KEY);
		playerFirstHand = memento.getBoolean(PLAYER_FIRST_HAND_KEY);
		status = memento.getStatus(STATUS_KEY);
	}

	/**
	 * 
	 */
	public void clear() {
		playerCards.clear();
		aiCards.clear();
		deck.clear();
		playerFirstHand = random.nextBoolean();
		trump = null;
		playerScore = 0;
		aiScore = 0;
		aiCard = null;
		playerCard = null;
		playerWonGame = 0;
		aiWonGame = 0;
		status = GameStatus.FINISHED;
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
		boolean playerWinner = playerScore > 60;
		boolean aiWinner = aiScore > 60;
		boolean finished = playerWinner || aiWinner || playerCards.isEmpty();
		playerHand = win;
		if (finished) {
			if (aiWinner)
				++aiWonGame;
			if (playerWinner)
				++playerWonGame;
			status = GameStatus.FINISHED;
		} else {
			status = GameStatus.DEAL;
		}
	}

	/**
	 * 
	 * @return
	 */
	public GameMemento createMemento() {
		GameMemento memento = new GameMemento();
		createMemento(memento);
		return memento;
	}

	/**
	 * 
	 * @param memento
	 */
	public void createMemento(GameMemento memento) {
		memento.clear();
		memento.set(PLAYER_CARDS_KEY, playerCards);
		memento.set(AI_CARDS_KEY, aiCards);
		memento.set(DECK_KEY, deck);
		memento.set(PLAYER_SCORE_KEY, playerScore);
		memento.set(AI_SCORE_KEY, aiScore);
		memento.set(TRUMP_KEY, trump);
		memento.set(PLAYER_WON_GAME_KEY, playerWonGame);
		memento.set(AI_WON_GAME_KEY, aiWonGame);
		memento.set(AI_CARD_KEY, aiCard);
		memento.set(PLAYER_CARD_KEY, playerCard);
		memento.set(PLAYER_HAND_KEY, playerHand);
		memento.set(PLAYER_FIRST_HAND_KEY, playerFirstHand);
		memento.set(STATUS_KEY, status);
	}

	/**
	 * 
	 * @return
	 */
	private AbstractGameState createState() {
		AbstractGameState s;
		if (playerHand) {
			if (deck.isEmpty()) {
				FinalPlayerMidState state = new FinalPlayerMidState();
				state.addToPlayerCards(playerCards);
				state.setPlayerCard(playerCard);
				s = state;
			} else {
				GamePlayerState state = new GamePlayerState();
				state.addToDeckCards(deck);
				state.addToDeckCards(playerCards);
				state.setPlayerCard(playerCard);
				s = state;
			}
		} else {
			if (deck.isEmpty()) {
				FinalAIState state = new FinalAIState();
				state.addToPlayerCards(playerCards);
				s = state;
			} else {
				List<Card> newDeck = new ArrayList<Card>(deck);
				newDeck.addAll(playerCards);
				GameAIState state = new GameAIState();
				state.addToDeckCards(deck);
				state.addToDeckCards(playerCards);
				s = state;
			}
		}
		s.addToAiCards(aiCards);
		s.setAiScore(aiScore);
		s.setPlayerScore(playerScore);
		s.setTrump(trump);
		return s;
	}

	/**
	 * 
	 */
	public void deal() {
		int n = deck.size();
		if (isFinished()) {
			initNewGame();
		} else if (n == 1) {
			Card card = dealCard();
			if (playerHand) {
				playerCards.add(card);
				aiCards.add(trump);
			} else {
				aiCards.add(card);
				playerCards.add(trump);
			}
		} else if (n > 1) {
			Card card = dealCard();
			playerCards.add(card);
			card = dealCard();
			aiCards.add(card);
		}
		if (playerHand) {
			status = GameStatus.PLAYER_MOVE;
		} else {
			status = GameStatus.AI_MOVE;
		}
	}

	/**
	 * 
	 * @return
	 */
	private Card dealCard() {
		Card card = deck.remove(0);
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
	public int getAiCardCount() {
		return aiCards.size();
	}

	/**
	 * @return the aiCards
	 */
	public List<Card> getAiCards() {
		return aiCards;
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
	 * @return the bestCard
	 */
	public Card getBestCard() {
		return bestCard;
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
	 * 
	 * @return
	 */
	public double getPlayerWinProbability() {
		return playerWinProbability;
	}

	/**
	 * @return the playerWonGame
	 */
	public int getPlayerWonGame() {
		return playerWonGame;
	}

	/**
	 * @return the status
	 */
	public GameStatus getStatus() {
		return status;
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
		shuffle();
		playerCards.clear();
		aiCards.clear();
		for (int i = 0; i < 3; ++i) {
			Card card = dealCard();
			playerCards.add(card);
			card = dealCard();
			aiCards.add(card);
		}
		trump = dealCard();
	}

	/**
	 * @return the aiWinner
	 */
	public boolean isAiWinner() {
		return aiScore > 60;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isConfident() {
		return confident;
	}

	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return GameStatus.FINISHED.equals(status);
	}

	/**
	 * @return the playerFirstHand
	 */
	public boolean isPlayerFirstHand() {
		return playerFirstHand;
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
		if (playerHand) {
			status = GameStatus.AI_MOVE;
		} else {
			status = GameStatus.CLOSE_HAND;
		}
	}

	/**
	 * 
	 * @param card
	 */
	private void playAi(Card card) {
		aiCards.remove(card);
		aiCard = card;
		if (playerHand) {
			status = GameStatus.CLOSE_HAND;
		} else {
			status = GameStatus.PLAYER_MOVE;
		}
	}

	/**
	 * @param aiWonGame
	 *            the aiWonGame to set
	 */
	public void setAiWonGame(int aiWonGame) {
		this.aiWonGame = aiWonGame;
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
	 * @param playerFirstHand
	 *            the playerFirstHand to set
	 */
	public void setPlayerFirstHand(boolean playerFirstHand) {
		this.playerFirstHand = playerFirstHand;
	}

	/**
	 * @param playerWonGame
	 *            the playerWonGame to set
	 */
	public void setPlayerWonGame(int playerWonGame) {
		this.playerWonGame = playerWonGame;
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
	private void shuffle() {
		Collections.shuffle(deck, random);
	}

	/**
	 *
	 */
	public void stopAnalysis() {
		ctx.setTimeout(0);
	}
}
