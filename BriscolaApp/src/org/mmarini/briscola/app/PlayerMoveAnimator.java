/**
 * 
 */
package org.mmarini.briscola.app;

import org.mmarini.briscola.Card;

import android.app.Activity;
import android.view.View;
import android.view.animation.AnimationSet;

/**
 * @author US00852
 * 
 */
public class PlayerMoveAnimator extends AbstractAnimator {
	private CardDrawableFactory cardDrawableFactory;

	/**
	 * 
	 */
	public PlayerMoveAnimator(Activity activity) {
		super(activity);
		cardDrawableFactory = CardDrawableFactory.getInstance();
	}

	/**
	 * 
	 * @param cardViewId
	 * @param cardIndex
	 */
	public void start(int cardViewId) {

		/*
		 * Move player card to desk then hide player card
		 */
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(createTranslation(cardViewId, cardViewId,
				R.id.playerCard, 0));
		set.addAnimation(createChangeDrawable(cardViewId, R.drawable.empty,
				computeDuration(1)));
		set.setAnimationListener(getAnimationListener());
		Activity activity = getActivity();
		activity.findViewById(cardViewId).startAnimation(set);

		/*
		 * Show player card on desk
		 */
		Card playerCard = getHandler().getPlayerCard();
		int cardId = cardDrawableFactory.findResId(playerCard);
		View playerCardView = activity.findViewById(R.id.playerCard);
		playerCardView.startAnimation(createChangeDrawable(R.id.playerCard,
				cardId, computeDuration(1)));
	}
}
