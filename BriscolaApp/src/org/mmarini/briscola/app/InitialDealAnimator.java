/**
 * 
 */
package org.mmarini.briscola.app;

import org.mmarini.briscola.GameHandler;

import android.app.Activity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * @author US00852
 * 
 */
public class InitialDealAnimator extends AbstractDealAnimator {

	private CardDrawableFactory cardDrawableFactory;

	/**
	 * 
	 */
	public InitialDealAnimator(Activity activity) {
		super(activity);
		cardDrawableFactory = CardDrawableFactory.getInstance();
	}

	/**
	 * @param resId
	 * 
	 */
	private void addBriscola(int resId) {

		int x0 = getHorizontalCenter(R.id.trumpCard);
		int y0 = getVerticalCenter(R.id.trumpCard);
		int x1 = getHorizontalCenter(R.id.deck);
		int y1 = getVerticalCenter(R.id.deck);

		TranslateAnimation shiftOut = new TranslateAnimation(x1 - x0, 0, y1
				- y0, 120);
		TranslateAnimation shiftIn = new TranslateAnimation(0, 0, 0, -120);

		shiftOut.setDuration(getAnimationDuration());
		shiftOut.setFillEnabled(true);
		shiftOut.setFillBefore(false);
		shiftOut.setFillAfter(true);
		shiftOut.setInterpolator(new AccelerateDecelerateInterpolator());

		shiftIn.setStartOffset(computeDuration(3));
		shiftIn.setDuration(getAnimationDuration());
		shiftIn.setFillEnabled(true);
		shiftIn.setFillBefore(false);
		shiftIn.setFillAfter(true);
		shiftIn.setInterpolator(new AccelerateDecelerateInterpolator());

		AnimationSet set = new AnimationSet(false);
		set.addAnimation(createChangeDrawable(R.id.trumpCard, R.drawable.retro,
				0));
		set.addAnimation(createBringOnFront(R.id.trumpCard, 1));
		set.addAnimation(createRotation(90f, 0f, 0));
		set.addAnimation(shiftOut);
		set.addAnimation(createHorizontalFlipIn(computeDuration(1)));
		set.addAnimation(createChangeDrawable(R.id.trumpCard, resId,
				computeDuration(2)));
		set.addAnimation(createHorizontalFlipOut(computeDuration(2)));
		set.addAnimation(shiftIn);
		set.setStartOffset(computeDuration(6));
		set.setAnimationListener(getAnimationListener());

		getActivity().findViewById(R.id.trumpCard).startAnimation(set);
		getActivity().findViewById(R.id.deck).startAnimation(
				createBringOnFront(R.id.deck, computeDuration(8)));
	}

	/**
	 * 
	 * @param viewId
	 */
	private void hideView(int viewId) {
		((ImageView) getActivity().findViewById(viewId))
				.setImageResource(R.drawable.empty);
	}

	/**
	 * 
	 */
	public void start() {
		GameHandler handler = getHandler();
		int cardId0 = cardDrawableFactory.findResId(handler.getPlayerCard(0));
		int cardId1 = cardDrawableFactory.findResId(handler.getPlayerCard(1));
		int cardId2 = cardDrawableFactory.findResId(handler.getPlayerCard(2));
		int trumpId = cardDrawableFactory.findResId(handler.getTrump());
		if (handler.isPlayerHand()) {
			addDealPlayer(R.id.playerCard1, cardId0, 0);
			addDealAI(R.id.aiCard1, computeDuration(1));
			addDealPlayer(R.id.playerCard2, cardId1, computeDuration(2));
			addDealAI(R.id.aiCard2, computeDuration(3));
			addDealPlayer(R.id.playerCard3, cardId2, computeDuration(4));
			addDealAI(R.id.aiCard3, computeDuration(5));
		} else {
			addDealAI(R.id.aiCard1, 0);
			addDealPlayer(R.id.playerCard1, cardId0, computeDuration(1));
			addDealAI(R.id.aiCard2, computeDuration(2));
			addDealPlayer(R.id.playerCard2, cardId1, computeDuration(3));
			addDealAI(R.id.aiCard3, computeDuration(4));
			addDealPlayer(R.id.playerCard3, cardId2, computeDuration(5));
		}
		addBriscola(trumpId);
		ImageView deckView = (ImageView) getActivity().findViewById(R.id.deck);
		deckView.setImageResource(R.drawable.deck);
		hideView(R.id.aiCard1);
		hideView(R.id.aiCard2);
		hideView(R.id.aiCard3);
		hideView(R.id.aiCard);
		hideView(R.id.playerCard1);
		hideView(R.id.playerCard2);
		hideView(R.id.playerCard3);
		hideView(R.id.playerCard);
		hideView(R.id.trumpCard);
	}
}
