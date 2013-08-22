package org.mmarini.briscola.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mmarini.briscola.AnalyzerListener;
import org.mmarini.briscola.Card;
import org.mmarini.briscola.GameHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
public class BriscolaActivity extends Activity implements AnalyzerListener {
	private static final int THINK_DURATION = 1000;

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
		playerCardListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				movePlayerCard(view);
				return true;
			}
		};

		handler.setAnalyzerListener(this);
		initalDealAnimator.setHandler(handler);
		nextDealAnimator.setHandler(handler);
		playerAnimator.setHandler(handler);
		aiMoveAnimator.setHandler(handler);
		cleanUpAnimator.setHandler(handler);

		handler.setSeed(1);
		handler.setTimeout(THINK_DURATION);
		cleanUpAnimator.setAnimationListener(new AnimationListener() {

			/**
			 * 
			 * @param animation
			 */
			@Override
			public void onAnimationStart(Animation animation) {
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
			public void onAnimationEnd(Animation animation) {
				onCleanUpEnd();
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
	private void onCleanUpEnd() {
		dealForStart();
	}

	/**
	 * 
	 */
	private void analyze() {
		logger.debug("Running analisys ...");
		handler.think();
		logger.debug("Analisys completed.");
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				onAnalysisEnd();
			}
		});
	}

	/**
	 * 
	 * @return
	 */
	private void dealForStart() {
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
	 * 
	 * @param view
	 */
	private void movePlayerCard(View view) {
		disableButtons();
		movedCardId = view.getId();
		Card card = getCardByResId(movedCardId);
		handler.play(card);
		cardResIdMap.remove(card);
		playerAnimator.start(movedCardId);
	}

	/**
	 * @see org.mmarini.briscola.AnalyzerListener#notifyAnalysis(org.mmarini.briscola
	 *      .GameHandler)
	 */
	@Override
	public void notifyAnalysis(GameHandler handler) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		refreshSettings();
		refreshData();
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
	 * 
	 */
	private void onAIMoveEnd() {
		logger.debug("handleEndAIMove");
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
		progressBar.setVisibility(View.INVISIBLE);
		aiMoveAnimator.start();
	}

	/**
	 * 
	 */
	private void onInitialDealEnd() {
		refreshData();
		if (handler.isPlayerHand()) {
			enableCardButtons();
		} else {
			startAnalysis();
		}
	}

	/**
	 * 
	 */
	private void onNextDealEnd() {
		refreshData();
		if (handler.isPlayerHand()) {
			enableCardButtons();
		} else {
			startAnalysis();
		}
	}

	/**
	 * 
	 */
	private void onPlayerMoveEnd() {
		if (handler.isPlayerHand()) {
			startAnalysis();
		} else {
			enableDeal(dealNextListener);
		}
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
	private void startAnalysis() {
		logger.debug("Analysing ...");
		progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				analyze();
			}
		}.start();
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
	 * 
	 */
	private void showOptions() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		refreshSettings();
		super.onResume();
	}

	/**
	 * 
	 */
	private void refreshSettings() {
		SharedPreferences sharePrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String value = sharePrefs.getString("think_time", "10");
		try {
			long thinkTime = Long.parseLong(value);
			handler.setTimeout(thinkTime);
		} catch (NumberFormatException e) {
			logger.error("Invalid value", e);
		}
	}
}
