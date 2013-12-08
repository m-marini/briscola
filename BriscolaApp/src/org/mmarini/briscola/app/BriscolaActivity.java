package org.mmarini.briscola.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

/**
 * 
 * @author us00852
 * 
 */
public class BriscolaActivity extends Activity {
	private static final String TRACE_FOLDER_NAME = "Briscola";
	private static final String TRACE_FILENAME = "briscola.log";
	private static final String BRISCOLA_FILE = "briscola.txt";
	private static final int THINK_DURATION = 1000;
	private static final String TEST_STATE = "aiWonGame=6;playerCards=23,38,3;playerHand=true;playerWonGame=2;aiScore=6;status=PLAYER_MOVE;playerFirstHand=true;aiCards=24,30,18;deck=14,2,33,32,20,1,19,16,9,39,11,26,12;trump=34;playerScore=55;";
	private static final boolean TEST_ENABLED = false;
	private static final boolean TRACE_ENABLED = true;

	private static Logger logger = LoggerFactory
			.getLogger(BriscolaActivity.class);

	private ImageView[] playerCardViews;
	private ImageView[] aiCardViews;
	private ImageView deckView;
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
	private ImageView aiCardView;
	private ImageView playerCardView;
	private CleanUpAnimator cleanUpAnimator;
	private String gameStateBackUp;
	private CardDrawableFactory cardDrawableFactory;
	private boolean paused;
	private ImageView trumpCardView;
	private MediaPlayer mediaPlayer;
	private boolean analisysReported;
	private AsyncTask<Void, Void, Void> analysisTask;

	/**
	 * 
	 */
	public BriscolaActivity() {
		paused = false;
		cardResIdMap = new HashMap<Card, Integer>();
		handler = new GameHandler();
		playerCardViews = new ImageView[3];
		aiCardViews = new ImageView[3];
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
		handler.analyze();
		trace();
		logger.debug("Analisys completed.");
	}

	/**
	 * Apply the handler state and restore the activity views, data
	 * 
	 * @param state
	 */
	void applyState(String state) {
		logger.debug("applyState: {}", state);
		gameStateBackUp = state;
		if (gameStateBackUp != null) {
			GameMemento memento = GameMemento.create(gameStateBackUp);
			handler.applyMemento(memento);
			reloadSettings();
			refreshData();
			restoreViews();
			restoreButtonsState();
		}
	}

	/**
	 * 
	 * @return
	 */
	void dealForStart() {
		disableButtons();
		handler.deal();
		trace();
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
	boolean dealNext() {
		disableButtons();
		handler.closeHand();
		trace();
		refreshData();
		if (handler.isFinished()) {
			cleanUpAnimator.start();
		} else {
			handler.deal();
			trace();
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
		deckView.setEnabled(false);
		aiCardView.setEnabled(false);
		playerCardView.setEnabled(false);
		for (ImageView b : playerCardViews) {
			b.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	private void enableCardButtons() {
		Drawable drw = getResources().getDrawable(R.drawable.ic_empty);
		for (ImageView b : playerCardViews) {
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
		deckView.setOnTouchListener(listener);
		deckView.setEnabled(true);
		aiCardView.setOnTouchListener(listener);
		aiCardView.setEnabled(true);
		playerCardView.setOnTouchListener(listener);
		playerCardView.setEnabled(true);
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
	GameHandler getHandler() {
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
		trace();
		cardResIdMap.remove(card);
		playerAnimator.start(movedCardId);
	}

	/**
	 * 
	 */
	private void onAIMoveEnd() {
		logger.debug("onAIMoveEnd");
		storeGame();
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
			if (!analisysReported && handler.isConfident()) {
				if (handler.getAiWinProbability() == 1.) {
					analisysReported = true;
					Toast.makeText(this,
							getResources().getString(R.string.aiWin_message),
							Toast.LENGTH_SHORT).show();
				} else if (handler.getPlayerWinProbability() == 1.) {
					analisysReported = true;
					Toast.makeText(
							this,
							getResources()
									.getString(R.string.playerWin_message),
							Toast.LENGTH_SHORT).show();
				}
			}
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
		setContentView(R.layout.activity_briscola);

		playerCardViews[0] = (ImageView) findViewById(R.id.playerCard1);
		playerCardViews[1] = (ImageView) findViewById(R.id.playerCard2);
		playerCardViews[2] = (ImageView) findViewById(R.id.playerCard3);

		aiCardViews[0] = (ImageView) findViewById(R.id.aiCard1);
		aiCardViews[1] = (ImageView) findViewById(R.id.aiCard2);
		aiCardViews[2] = (ImageView) findViewById(R.id.aiCard3);
		deckView = (ImageView) findViewById(R.id.deck);
		aiCardView = (ImageView) findViewById(R.id.aiCard);
		playerCardView = (ImageView) findViewById(R.id.playerCard);
		trumpCardView = (ImageView) findViewById(R.id.trumpCard);

		aiWonGame = (TextView) findViewById(R.id.aiWonGame);
		aiScore = (TextView) findViewById(R.id.aiScore);
		playerWonGame = (TextView) findViewById(R.id.playerWonGame);
		playerScore = (TextView) findViewById(R.id.playerScore);
		deckCount = (TextView) findViewById(R.id.deckCount);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		for (ImageView view : playerCardViews) {
			view.setOnTouchListener(playerCardListener);
		}

		if (savedInstanceState != null) {
			paused = true;
		} else {
			paused = false;
		}
		analisysReported = false;
		if (TEST_ENABLED) {
			applyState(TEST_STATE);
			storeGame();
		}
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
		analisysReported = false;
		refreshData();
		if (handler.isPlayerHand()) {
			storeGame();
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
			storeGame();
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
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		// if (analysisTask != null)
		// analysisTask.cancel(true);
		paused = true;
	}

	/**
	 * 
	 */
	private void onPlayerMoveEnd() {
		logger.debug("onPlayerMoveEnd");
		if (handler.isPlayerHand()) {
			startAnalysis();
		} else {
			storeGame();
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
		restoreGame();
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		logger.debug("onResume");
		analisysReported = false;
		if (paused)
			startResumeDialog();
		else {
			resumeGame();
		}
	}

	/**
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		logger.debug("onSaveInstanceState");
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
	 * Refresh the game info view depending on game handler state
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
	 * Reload the game setting from preferences
	 */
	private void reloadSettings() {
		SharedPreferences sharePrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		long thinkTime = 10;
		String value = sharePrefs.getString("think_time", "10");
		try {
			thinkTime = Long.parseLong(value);
		} catch (NumberFormatException e) {
			logger.error("Invalid value", e);
		}
		handler.setTimeout(thinkTime * 1000);
		boolean scoreVisible = sharePrefs.getBoolean("score_visible", true);
		if (scoreVisible) {
			aiScore.setVisibility(View.VISIBLE);
			playerScore.setVisibility(View.VISIBLE);
		} else {
			aiScore.setVisibility(View.INVISIBLE);
			playerScore.setVisibility(View.INVISIBLE);
		}
		boolean musicEnabled = sharePrefs.getBoolean("music_enabled", true);
		if (musicEnabled) {
			mediaPlayer = MediaPlayer.create(this, R.raw.music);
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		}
	}

	/**
	 * Restore the state of buttons depending on game handler state
	 */
	private void restoreButtonsState() {
		logger.debug("restoreButtons");
		disableButtons();
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
	 * Restore the state of game and activity from persistence file
	 */
	private void restoreGame() {
		try {
			FileInputStream stream = openFileInput(BRISCOLA_FILE);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						stream));
				String state = in.readLine();
				logger.debug("LoadValues {}", state);
				applyState(state);
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			logger.error("Error reading file " + BRISCOLA_FILE, e);
		}
	}

	/**
	 * Restore the views depending on game handler state
	 */
	private void restoreViews() {
		// Set deck cards view
		int n = handler.getDeckCount();
		if (handler.isFinished()) {
			trumpCardView.setImageResource(R.drawable.ic_empty);
			deckView.setImageResource(R.drawable.ic_deck);
		} else if (n > 0) {
			// Set trump card view
			Card card = handler.getTrump();
			if (card == null) {
				trumpCardView.setImageResource(R.drawable.ic_empty);
			} else {
				int cardId = cardDrawableFactory.findResId(card);
				trumpCardView.setImageResource(cardId);
			}
			deckView.setImageResource(R.drawable.ic_deck);
		} else {
			trumpCardView.setImageResource(R.drawable.ic_empty);
			deckView.setImageResource(R.drawable.ic_empty);
		}

		// Set player card view
		Card card = handler.getPlayerCard();
		if (card == null) {
			playerCardView.setImageResource(R.drawable.ic_empty);
		} else {
			int cardId = cardDrawableFactory.findResId(card);
			playerCardView.setImageResource(cardId);
		}

		// Set ai card view
		card = handler.getAiCard();
		if (card == null) {
			playerCardView.setImageResource(R.drawable.ic_empty);
		} else {
			int cardId = cardDrawableFactory.findResId(card);
			aiCardView.setImageResource(cardId);
		}

		// Set ai cards view
		n = handler.getAiCardCount();
		for (int i = 0; i < 3; ++i) {
			if (i >= n) {
				aiCardViews[i].setImageResource(R.drawable.ic_empty);
			} else {
				aiCardViews[i].setImageResource(R.drawable.ic_back_rev);
			}
		}

		// Set player cards view
		cardResIdMap.clear();
		List<Card> cards = handler.getPlayerCards();
		n = cards.size();
		movedCardId = 0;
		for (int i = 0; i < 3; ++i) {
			ImageView imageView = playerCardViews[i];
			if (i >= n) {
				imageView.setImageResource(R.drawable.ic_empty);
				if (movedCardId == 0) {
					movedCardId = imageView.getId();
				}
			} else {
				card = cards.get(i);
				int cardId = cardDrawableFactory.findResId(card);
				imageView.setImageResource(cardId);
				cardResIdMap.put(card, imageView.getId());
			}
		}
	}

	/**
	 * Resume the game from persistence file
	 */
	private void resumeGame() {
		logger.debug("resumeGame");
		paused = false;
		restoreGame();
	}

	/**
	 * Show the options
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
		analysisTask = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				analyze();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				onAnalysisEnd();
			}
		}.execute();
	}

	/**
	 * 
	 */
	private void startResumeDialog() {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.resume_message);
		builder.setPositiveButton(R.string.yes_text, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				resumeGame();
			}
		});
		builder.setNegativeButton(R.string.no_text, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				BriscolaActivity.this.finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * Store the state of game to the persistence file
	 */
	private void storeGame() {
		gameStateBackUp = handler.createMemento().toString();
		logger.debug("storeStatuts: {}", gameStateBackUp);
		try {
			FileOutputStream stream = openFileOutput(BRISCOLA_FILE,
					MODE_PRIVATE);
			try {
				PrintWriter out = new PrintWriter(stream);
				out.println(gameStateBackUp);
				out.close();
				logger.debug("saveValues: {}", gameStateBackUp);
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			logger.error("Error writting file " + BRISCOLA_FILE, e);
		}
	}

	/**
	 * 
	 */
	private void trace() {
		if (TRACE_ENABLED
				&& Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {
			File folder = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			folder = new File(folder.getParentFile(), TRACE_FOLDER_NAME);
			folder.mkdirs();
			File file = new File(folder, TRACE_FILENAME);
			try {
				FileWriter fo = new FileWriter(file, true);
				try {
					PrintWriter out = new PrintWriter(fo);
					out.print(new Date());
					out.print(": ");
					out.print(handler.createMemento().toString());
					out.println();
					out.close();
				} finally {
					fo.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

		}
	}
}
