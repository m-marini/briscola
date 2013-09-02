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

/**
 * 
 * @author us00852
 * 
 */
public class BriscolaActivity extends Activity {
	private static final String TRACE_FILENAME = "briscola.trace";
	private static final String BRISCOLA_FILE_SEPARATOR = ",";
	private static final String BRISCOLA_FILE = "briscola.txt";
	private static final String GAME_HANDLER_KEY = "gameHandler";
	private static final int THINK_DURATION = 1000;
	private static final String TEST_STATE = "aiWonGame=0;playerCards=18,10,11;playerHand=true;playerWonGame=0;aiScore=60;playerFirstHand=false;aiCards=0,1,2;status=PLAYER_MOVE;"
			+ "deck=20;" + "trump=8;playerScore=40;";
	private static final boolean TRACE_ENABLED = false;

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
		handler.think();
		trace();
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
	private boolean dealNext() {
		disableButtons();
		handler.closeHand();
		trace();
		refreshData();
		if (handler.isFinished()) {
			saveValues();
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
		trace();
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
			restoreHandler(savedInstanceState);
		} else {
			handler.clear();
			// restoreHandlerState(TEST_STATE);
			paused = false;
		}
		loadValues();

		File folder = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		folder = new File(folder.getParentFile(), "Briscola");
		folder.mkdir();
		logger.debug("File: {} {}", folder.getAbsolutePath(),
				folder.isDirectory());

		listFiles("/mnt/sdcard");
		listFiles("/mnt/sdcard/external_sd");
	}

	private void listFiles(String path) {
		File folder = new File(path);
		for (File file : folder.listFiles()) {
			logger.debug("File: {}", file.getAbsolutePath());
		}
	}

	/**
	 * 
	 */
	private void loadValues() {
		FileInputStream stream;
		try {
			stream = openFileInput(BRISCOLA_FILE);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						stream));
				String line = in.readLine();
				logger.debug("loadValues: {}", line);
				if (line != null) {
					String[] args = line.split(BRISCOLA_FILE_SEPARATOR);
					if (args.length >= 0)
						handler.setPlayerWonGame(Integer.parseInt(args[0]));
					if (args.length >= 1)
						handler.setAiWonGame(Integer.parseInt(args[1]));
					if (args.length >= 2)
						handler.setPlayerFirstHand(Boolean
								.parseBoolean(args[2]));
				}
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			logger.error("Error reading file " + BRISCOLA_FILE, e);
		}
	}

	/**
	 * 
	 */
	private void saveValues() {
		FileOutputStream stream;
		try {
			stream = openFileOutput(BRISCOLA_FILE, MODE_PRIVATE);
			try {
				StringBuilder line = new StringBuilder();
				line.append(handler.getPlayerWonGame())
						.append(BRISCOLA_FILE_SEPARATOR)
						.append(handler.getAiWonGame())
						.append(BRISCOLA_FILE_SEPARATOR)
						.append(handler.isPlayerFirstHand());

				PrintWriter out = new PrintWriter(stream);
				out.println(line);
				out.close();
				logger.debug("saveValues: {}", line);
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			logger.error("Error writting file " + BRISCOLA_FILE, e);
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
	 * 
	 */
	private void resumeGame() {
		logger.debug("resume");
		paused = false;
		reloadSettings();
		refreshViews();
		refreshData();
		restoreButtons();
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
		for (int i = 0; i < 3; ++i) {
			if (i >= n) {
				playerCardViews[i].setImageResource(R.drawable.ic_empty);
			} else {
				card = cards.get(i);
				int cardId = cardDrawableFactory.findResId(card);
				playerCardViews[i].setImageResource(cardId);
				cardResIdMap.put(card, playerCardViews[i].getId());
			}
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
		if (gameStateBackUp != null) {
			GameMemento memento = GameMemento.create(gameStateBackUp);
			handler.applyMemento(memento);
			reloadSettings();
			refreshData();
			refreshViews();
			restoreButtons();
		}
	}

	/**
	 * 
	 */
	private void restoreButtons() {
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
	private void trace() {
		if (TRACE_ENABLED
				&& Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {
			File folder = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File file = new File(folder, TRACE_FILENAME);
			logger.debug("external file: {}", file);

			folder.mkdirs();
			try {
				FileWriter fo = new FileWriter(file);
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
