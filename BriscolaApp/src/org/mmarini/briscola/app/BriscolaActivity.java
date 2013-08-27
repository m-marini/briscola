package org.mmarini.briscola.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mmarini.briscola.Card;
import org.mmarini.briscola.GameHandler;
import org.mmarini.briscola.GameMemento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @author us00852
 * 
 */
public class BriscolaActivity extends Activity {
	private static final String GAME_HANDLER_KEY = "gameHandler";
	private static final int THINK_DURATION = 1000;
	private static final String TEST_STATE = "aiWonGame=0;playerCards=18,10,11;playerHand=true;playerWonGame=0;aiScore=60;playerFirstHand=false;aiCards=0,1,2;status=PLAYER_MOVE;"
			+ "deck=20;" + "trump=8;playerScore=40;";

	private static Logger logger = LoggerFactory
			.getLogger(BriscolaActivity.class);

	private ImageView[] playerCards;
	private ImageView[] aiCards;
	private ImageView deck;
	private OnTouchListener dealInitListener;
	private OnTouchListener dealNextListener;
	private OnTouchListener playerCardListener;
	private AIMoveAnimator aiMoveAnimator;
	private GameHandler handler;
	private InitialDealAnimator initalDealAnimator;
	private NextDealAnimator nextDealAnimator;
	private PlayerMoveAnimator playerAnimator;
	private TextView aiWonGame;
	private TextView aiScore;
	private TextView playerWonGame;
	private TextView playerScore;
	private TextView deckCount;
	private ProgressBar progressBar;
	private Map<Card, Integer> cardResIdMap;
	private int movedCardId;
	private ImageView aiCard;
	private ImageView playerCard;
	private CleanUpAnimator cleanUpAnimator;
	private String gameStateBackUp;
	private CardDrawableFactory cardDrawableFactory;

	/**
	 * 
	 */
	public BriscolaActivity() {
		cardResIdMap = new HashMap<Card, Integer>();
		handler = new GameHandler();
		playerCards = new ImageView[3];
		aiCards = new ImageView[3];
		aiMoveAnimator = new AIMoveAnimator(this);
		initalDealAnimator = new InitialDealAnimator(this);
		nextDealAnimator = new NextDealAnimator(this);
		playerAnimator = new PlayerMoveAnimator(this);
		cleanUpAnimator = new CleanUpAnimator(this);
		cardDrawableFactory = CardDrawableFactory.getInstance();
		playerCardListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				movePlayerCard(view);
				return true;
			}
		};

		initalDealAnimator.setHandler(handler);
		nextDealAnimator.setHandler(handler);
		playerAnimator.setHandler(handler);
		aiMoveAnimator.setHandler(handler);
		cleanUpAnimator.setHandler(handler);

		// handler.setSeed(1);
		handler.setTimeout(THINK_DURATION);
		cleanUpAnimator.setAnimationListener(new AnimationListener() {

			/**
			 * 
			 * @param animation
			 */
			@Override
			public void onAnimationEnd(Animation animation) {
				onCleanUpEnd();
			}

			/**
			 * 
			 * @param animation
			 */
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			/**
			 * 
			 * @param animation
			 */
			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
		playerAnimator.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				onPlayerMoveEnd();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		nextDealAnimator.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				onNextDealEnd();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		initalDealAnimator.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				onInitialDealEnd();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		aiMoveAnimator.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				onAIMoveEnd();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});

		dealInitListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dealForStart();
				return true;
			}
		};

		dealNextListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dealNext();
				return true;
			}
		};
	}

	/**
	 * 
	 */
	private void analyze() {
		logger.debug("Running analisys ...");
		handler.think();
		logger.debug("Analisys completed.");
	}

	/**
	 * 
	 */
	private void backUpGameState() {
		gameStateBackUp = handler.createMemento().toString();
		logger.debug("Backup: {}", gameStateBackUp);
	}

	/**
	 * 
	 * @return
	 */
	void dealForStart() {
		disableButtons();
		handler.deal();
		refreshData();
		cardResIdMap.clear();
		cardResIdMap.put(handler.getPlayerCard(0), R.id.playerCard1);
		cardResIdMap.put(handler.getPlayerCard(1), R.id.playerCard2);
		cardResIdMap.put(handler.getPlayerCard(2), R.id.playerCard3);
		initalDealAnimator.start();
	}

	/**
	 * 
	 * @return
	 */
	private boolean dealNext() {
		disableButtons();
		handler.closeHand();
		refreshData();
		if (handler.isFinished()) {
			cleanUpAnimator.start();
		} else {
			handler.deal();
			List<Card> cards = handler.getPlayerCards();
			Card newCard = null;
			if (cards.size() == 3) {
				// Find new card references
				for (Card c : cards) {
					if (cardResIdMap.get(c) == null) {
						newCard = c;
						cardResIdMap.put(c, movedCardId);
					}
				}
			}
			nextDealAnimator.start(movedCardId, newCard);
		}
		return false;
	}

	/**
	 * 
	 */
	private void disableButtons() {
		deck.setEnabled(false);
		aiCard.setEnabled(false);
		playerCard.setEnabled(false);
		for (ImageView b : playerCards) {
			b.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	private void enableCardButtons() {
		Drawable drw = getResources().getDrawable(R.drawable.empty);
		for (ImageView b : playerCards) {
			if (!b.getDrawable().equals(drw)) {
				b.setEnabled(true);
			}
		}
	}

	/**
	 * 
	 * @param listener
	 */
	private void enableDeal(OnTouchListener listener) {
		deck.setOnTouchListener(listener);
		deck.setEnabled(true);
		aiCard.setOnTouchListener(listener);
		aiCard.setEnabled(true);
		playerCard.setOnTouchListener(listener);
		playerCard.setEnabled(true);
	}

	/**
	 * 
	 * @param resId
	 * @return
	 */
	private Card getCardByResId(int resId) {
		for (Entry<Card, Integer> entry : cardResIdMap.entrySet()) {
			if (entry.getValue() == resId) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @return the handler
	 */
	public GameHandler getHandler() {
		return handler;
	}

	/**
	 * 
	 * @param view
	 */
	void movePlayerCard(View view) {
		disableButtons();
		movedCardId = view.getId();
		Card card = getCardByResId(movedCardId);
		handler.play(card);
		cardResIdMap.remove(card);
		playerAnimator.start(movedCardId);
	}

	/**
	 * 
	 */
	private void onAIMoveEnd() {
		logger.debug("onAIMoveEnd");
		backUpGameState();
		if (handler.isPlayerHand()) {
			enableDeal(dealNextListener);
		} else {
			enableCardButtons();
		}
	}

	/**
	 * 
	 */
	private void onAnalysisEnd() {
		logger.debug("onAnalysisEnd");
		if (!isFinishing()) {
			progressBar.setVisibility(View.INVISIBLE);
			aiMoveAnimator.start();
		}
	}

	/**
	 * 
	 */
	private void onCleanUpEnd() {
		logger.debug("onCleanUpEnd");
		dealForStart();
	}

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		logger.debug("onCreate");
		if (savedInstanceState != null) {
			restoreHandler(savedInstanceState);
		} else {
			handler.clear();

		}
		setContentView(R.layout.activity_briscola);

		playerCards[0] = (ImageView) findViewById(R.id.playerCard1);
		playerCards[1] = (ImageView) findViewById(R.id.playerCard2);
		playerCards[2] = (ImageView) findViewById(R.id.playerCard3);

		aiCards[0] = (ImageView) findViewById(R.id.aiCard1);
		aiCards[1] = (ImageView) findViewById(R.id.aiCard2);
		aiCards[2] = (ImageView) findViewById(R.id.aiCard3);
		deck = (ImageView) findViewById(R.id.deck);
		aiCard = (ImageView) findViewById(R.id.aiCard);
		playerCard = (ImageView) findViewById(R.id.playerCard);

		deck.setOnTouchListener(dealInitListener);
		for (ImageView view : playerCards) {
			view.setOnTouchListener(playerCardListener);
		}
		disableButtons();
		deck.setOnTouchListener(dealInitListener);
		deck.setEnabled(true);

		aiWonGame = (TextView) findViewById(R.id.aiWonGame);
		aiScore = (TextView) findViewById(R.id.aiScore);
		playerWonGame = (TextView) findViewById(R.id.playerWonGame);
		playerScore = (TextView) findViewById(R.id.playerScore);
		deckCount = (TextView) findViewById(R.id.deckCount);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		// restoreHandlerState(TEST_STATE);
		reloadSettings();
		refreshData();
		backUpGameState();
	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		logger.debug("onDestroy");
	}

	/**
	 * 
	 */
	private void onInitialDealEnd() {
		logger.debug("onInitialDealEnd");
		refreshData();
		if (handler.isPlayerHand()) {
			backUpGameState();
			enableCardButtons();
		} else {
			startAnalysis();
		}
	}

	/**
	 * 
	 */
	private void onNextDealEnd() {
		logger.debug("onNextDealEnd");
		refreshData();
		if (handler.isPlayerHand()) {
			backUpGameState();
			enableCardButtons();
		} else {
			startAnalysis();
		}
	}

	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			showOptions();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		logger.debug("onPause");
	}

	/**
	 * 
	 */
	private void onPlayerMoveEnd() {
		logger.debug("onPlayerMoveEnd");
		if (handler.isPlayerHand()) {
			startAnalysis();
		} else {
			backUpGameState();
			enableDeal(dealNextListener);
		}
	}

	/**
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		logger.debug("onRestart");
	}

	/**
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		logger.debug("onRestoreInstanceState");
		restoreHandler(savedInstanceState);
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		logger.debug("onResume");
		reloadSettings();
	}

	/**
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		logger.debug("onSaveInstanceState");
		if (gameStateBackUp != null)
			outState.putString(GAME_HANDLER_KEY, gameStateBackUp);
	}

	/**
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		logger.debug("onStart");
	}

	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		logger.debug("onStop");
	}

	/**
	 * 
	 */
	private void refreshData() {
		int score = handler.getAiScore();
		Resources resources = getResources();
		aiScore.setText(resources.getString(R.string.aiScoreText, score));
		score = handler.getPlayerScore();
		playerScore.setText(resources
				.getString(R.string.playerScoreText, score));
		int win = handler.getAiWonGame();
		aiWonGame.setText(resources.getString(R.string.aiWonText, win));
		win = handler.getPlayerWonGame();
		playerWonGame.setText(resources.getString(R.string.playerWonText, win));
		int n = handler.getDeckCount();
		deckCount.setText(resources.getString(R.string.deckCountText, n));
	}

	/**
	 * 
	 */
	private void refreshViews() {
		// Set deck cards view
		int n = handler.getDeckCount();
		if (n == 0) {
			((ImageView) findViewById(R.id.trumpCard))
					.setImageResource(R.drawable.empty);
			((ImageView) findViewById(R.id.deck))
					.setImageResource(R.drawable.empty);
		} else {
			// Set trump card view
			Card card = handler.getTrump();
			if (card == null) {
				((ImageView) findViewById(R.id.trumpCard))
						.setImageResource(R.drawable.empty);
			} else {
				int cardId = cardDrawableFactory.findResId(card);
				((ImageView) findViewById(R.id.trumpCard))
						.setImageResource(cardId);
			}
			((ImageView) findViewById(R.id.deck))
					.setImageResource(R.drawable.deck);
		}

		// Set player card view
		Card card = handler.getPlayerCard();
		if (card == null) {
			((ImageView) findViewById(R.id.playerCard))
					.setImageResource(R.drawable.empty);
		} else {
			int cardId = cardDrawableFactory.findResId(card);
			((ImageView) findViewById(R.id.playerCard))
					.setImageResource(cardId);
		}

		// Set ai card view
		card = handler.getAiCard();
		if (card == null) {
			((ImageView) findViewById(R.id.playerCard))
					.setImageResource(R.drawable.empty);
		} else {
			int cardId = cardDrawableFactory.findResId(card);
			((ImageView) findViewById(R.id.aiCard)).setImageResource(cardId);
		}

		// Set ai cards view
		n = handler.getAiCardCount();
		switch (n) {
		case 0:
			((ImageView) findViewById(R.id.aiCard1))
					.setImageResource(R.drawable.empty);
			((ImageView) findViewById(R.id.aiCard2))
					.setImageResource(R.drawable.empty);
			((ImageView) findViewById(R.id.aiCard3))
					.setImageResource(R.drawable.empty);
			break;
		case 1:
			((ImageView) findViewById(R.id.aiCard1))
					.setImageResource(R.drawable.retro_rot);
			((ImageView) findViewById(R.id.aiCard2))
					.setImageResource(R.drawable.empty);
			((ImageView) findViewById(R.id.aiCard3))
					.setImageResource(R.drawable.empty);
			break;
		case 2:
			((ImageView) findViewById(R.id.aiCard1))
					.setImageResource(R.drawable.retro_rot);
			((ImageView) findViewById(R.id.aiCard2))
					.setImageResource(R.drawable.retro_rot);
			((ImageView) findViewById(R.id.aiCard3))
					.setImageResource(R.drawable.empty);
			break;
		default:
			((ImageView) findViewById(R.id.aiCard1))
					.setImageResource(R.drawable.retro_rot);
			((ImageView) findViewById(R.id.aiCard2))
					.setImageResource(R.drawable.retro_rot);
			((ImageView) findViewById(R.id.aiCard3))
					.setImageResource(R.drawable.retro_rot);
			break;
		}

		// Set player cards view
		cardResIdMap.clear();
		List<Card> cards = handler.getPlayerCards();
		n = cards.size();
		if (n == 0) {
			((ImageView) findViewById(R.id.playerCard1))
					.setImageResource(R.drawable.empty);
		} else {
			card = cards.get(0);
			int cardId = cardDrawableFactory.findResId(card);
			((ImageView) findViewById(R.id.playerCard1))
					.setImageResource(cardId);
			cardResIdMap.put(card, R.id.playerCard1);
		}
		if (n <= 1) {
			((ImageView) findViewById(R.id.playerCard2))
					.setImageResource(R.drawable.empty);
		} else {
			card = cards.get(1);
			int cardId = cardDrawableFactory.findResId(card);
			((ImageView) findViewById(R.id.playerCard2))
					.setImageResource(cardId);
			cardResIdMap.put(card, R.id.playerCard2);
		}
		if (n <= 2) {
			((ImageView) findViewById(R.id.playerCard3))
					.setImageResource(R.drawable.empty);
		} else {
			card = cards.get(2);
			int cardId = cardDrawableFactory.findResId(card);
			((ImageView) findViewById(R.id.playerCard3))
					.setImageResource(cardId);
			cardResIdMap.put(card, R.id.playerCard3);
		}
	}

	/**
	 * 
	 */
	private void reloadSettings() {
		SharedPreferences sharePrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String value = sharePrefs.getString("think_time", "10");
		try {
			long thinkTime = Long.parseLong(value);
			handler.setTimeout(thinkTime * 1000);
		} catch (NumberFormatException e) {
			logger.error("Invalid value", e);
		}
		boolean scoreVisible = sharePrefs.getBoolean("score_visible", true);
		if (scoreVisible) {
			findViewById(R.id.aiScore).setVisibility(View.VISIBLE);
			findViewById(R.id.playerScore).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.aiScore).setVisibility(View.INVISIBLE);
			findViewById(R.id.playerScore).setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 
	 * @param savedInstanceState
	 */
	private void restoreHandler(Bundle savedInstanceState) {
		restoreHandlerState(savedInstanceState.getString(GAME_HANDLER_KEY));
	}

	/**
	 * 
	 */
	private void restoreHandlerState() {
		logger.debug("restoreHandler: {}", gameStateBackUp);
		GameMemento memento = GameMemento.create(gameStateBackUp);
		handler.applyMemento(memento);
		reloadSettings();
		refreshData();

		disableButtons();
		refreshViews();
		switch (handler.getStatus()) {
		case PLAYER_MOVE:
			enableCardButtons();
			break;
		case CLOSE_HAND:
			enableDeal(dealNextListener);
			break;
		default:
			enableDeal(dealInitListener);
			break;
		}
	}

	/**
	 * 
	 * @param state
	 */
	void restoreHandlerState(String state) {
		gameStateBackUp = state;
		restoreHandlerState();
	}

	/**
	 * 
	 */
	private void showOptions() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * Start the analysis task
	 */
	private void startAnalysis() {
		logger.debug("Analysing ...");
		progressBar.setVisibility(View.VISIBLE);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				analyze();
				return null;
			}

			/**
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(Void result) {
				onAnalysisEnd();
			}
		}.execute();
	}
}
