package org.mmarini.briscola.app;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

/**
 * 
 * @author us00852
 * 
 */
public abstract class AbstractDealAnimator extends AbstractAnimator {

	/**
	 * 
	 * @param activity
	 */
	protected AbstractDealAnimator(Activity activity) {
		super(activity);
	}

	/**
	 * 
	 * @param cardViewId
	 * @param startOffset
	 */
	protected Animation addDealAI(int cardViewId, long startOffset) {

		AnimationSet main = new AnimationSet(false);
		main.addAnimation(createChangeDrawable(cardViewId,
				R.drawable.ic_back_rev, 10));
		main.addAnimation(createBringOnFront(cardViewId, 1));
		main.addAnimation(createRotation(-90f, 0f, 0));
		main.addAnimation(createTranslation(cardViewId, R.id.deck, cardViewId,
				0));
		main.setStartOffset(startOffset);
		View cardView = getActivity().findViewById(cardViewId);
		cardView.startAnimation(main);
		return main;
	}

	/**
	 * 
	 * @param cardViewId
	 * @param resId
	 * @param startOffset
	 */
	protected Animation addDealPlayer(int cardViewId, int resId,
			long startOffset) {
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(createChangeDrawable(cardViewId, R.drawable.ic_back, 1));
		set.addAnimation(createBringOnFront(cardViewId, 1));
		set.addAnimation(createRotation(90f, 0f, 0));
		set.addAnimation(createTranslation(cardViewId, R.id.deck, cardViewId, 0));
		set.addAnimation(createHorizontalFlipIn(computeDuration(1)));
		set.addAnimation(createChangeDrawable(cardViewId, resId,
				computeDuration(2)));
		set.addAnimation(createHorizontalFlipOut(computeDuration(2)));
		set.setStartOffset(startOffset);
		View cardView = getActivity().findViewById(cardViewId);
		cardView.startAnimation(set);
		return set;
	}

	/**
	 * 
	 * @param resId
	 * @return
	 */
	protected Animation addSwapOut(int resId) {
		ImageView view = (ImageView) getActivity().findViewById(resId);
		Drawable empty = getActivity().getResources().getDrawable(
				R.drawable.ic_empty);
		if (view.getDrawable().equals(empty))
			return null;
		AnimationSet anim = new AnimationSet(false);
		anim.addAnimation(createHorizontalFlipIn(0));
		anim.addAnimation(createChangeDrawable(resId, R.drawable.ic_empty,
				computeDuration(1)));
		view.startAnimation(anim);
		return anim;
	}
}