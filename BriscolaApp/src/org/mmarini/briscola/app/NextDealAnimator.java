/**
 * 
 */
package org.mmarini.briscola.app;

import org.mmarini.briscola.Card;
import org.mmarini.briscola.GameHandler;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

/**
 * @author US00852
 * 
 */
public class NextDealAnimator extends AbstractDealAnimator {

	private CardDrawableFactory cardDrawableFactory;

	/**
	 * 
	 */
	public NextDealAnimator(Activity activity) {
		super(activity);
		cardDrawableFactory = CardDrawableFactory.getInstance();
	}

	/**
	 * 
	 * @param playerCardId
	 * @param newCard
	 * @return
	 */
	private Animation addDeaAIHand(int playerCardId, Card newCard) {
		int resId = cardDrawableFactory.findResId(newCard);
		addDealAI(R.id.aiCard3, computeDuration(1));
		Animation last = addDealPlayer(playerCardId, resId, computeDuration(2));
		return last;
	}

	/**
	 * 
	 * @param playerCardId
	 * @return
	 */
	private Animation addDeaAILastHand(int playerCardId) {
		long startOffset = computeDuration(1);
		Animation anim = createChangeDrawable(R.id.deck, R.drawable.ic_empty,
				startOffset);
		Activity activity = getActivity();
		activity.findViewById(R.id.deck).startAnimation(anim);

		activity.findViewById(R.id.deck).startAnimation(
				createChangeDrawable(R.id.deck, R.drawable.ic_empty,
						startOffset));

		addDealAI(R.id.aiCard3, startOffset);

		// Animate trump
		Animation last = addTrumpToPlayer(playerCardId, computeDuration(2));
		return last;
	}

	/**
	 * 
	 * @param playerCardId
	 * @param newCard
	 * @return
	 */
	private Animation addDealPlayerHand(int playerCardId, Card newCard) {
		int resId = cardDrawableFactory.findResId(newCard);
		addDealPlayer(playerCardId, resId, computeDuration(1));
		Animation last = addDealAI(R.id.aiCard3, computeDuration(2));
		return last;
	}

	/**
	 * 
	 * @param cardViewId
	 * @param card
	 * @return
	 */
	private Animation addDealPlayerLastHand(int cardViewId, Card card) {
		int resId = cardDrawableFactory.findResId(card);
		addDealPlayer(cardViewId, resId, computeDuration(1));
		getActivity().findViewById(R.id.deck).startAnimation(
				createChangeDrawable(R.id.deck, R.drawable.ic_empty,
						computeDuration(1)));
		Animation anim = addTrumpToAI(computeDuration(2));
		return anim;
	}

	/**
	 * 
	 * @param startOffset
	 * @return
	 */
	private Animation addTrumpToAI(long startOffset) {
		int trumpId = cardDrawableFactory.findResId(getHandler().getTrump());
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(createChangeDrawable(R.id.aiCard3, trumpId, 1));
		set.addAnimation(createBringOnFront(R.id.aiCard3, 1));
		set.addAnimation(createTranslation(R.id.aiCard3, R.id.trumpCard,
				R.id.aiCard3, 0));
		set.addAnimation(createVerticalFlipIn(computeDuration(1)));
		set.addAnimation(createChangeDrawable(R.id.aiCard3,
				R.drawable.ic_back_rev, computeDuration(2)));
		set.addAnimation(createVerticalFlipOut(computeDuration(2)));
		set.setStartOffset(startOffset);

		Activity activity = getActivity();
		activity.findViewById(R.id.aiCard3).startAnimation(set);

		activity.findViewById(R.id.trumpCard).startAnimation(
				createChangeDrawable(R.id.trumpCard, R.drawable.ic_empty,
						startOffset));
		return set;
	}

	/**
	 * 
	 * @param playerCardId
	 * @param i
	 * @return
	 */
	private Animation addTrumpToPlayer(int playerCardId, long startOffset) {
		AnimationSet set = new AnimationSet(false);
		int trumpId = cardDrawableFactory.findResId(getHandler().getTrump());
		set.addAnimation(createChangeDrawable(playerCardId, trumpId, 10));
		set.addAnimation(createTranslation(playerCardId, R.id.trumpCard,
				playerCardId, 0));
		set.addAnimation(createBringOnFront(playerCardId, 1));
		set.setStartOffset(startOffset);

		ImageView cardView = (ImageView) getActivity().findViewById(
				playerCardId);
		cardView.startAnimation(set);

		getActivity().findViewById(R.id.trumpCard).startAnimation(
				createChangeDrawable(R.id.trumpCard, R.drawable.ic_empty,
						startOffset));
		return set;
	}

	/**
	 * 
	 * @param playerCardId
	 * @param newCard
	 */
	public void start(int playerCardId, Card newCard) {
		addSwapOut(R.id.aiCard);
		Animation last = addSwapOut(R.id.playerCard);
		GameHandler handler = getHandler();
		if (handler.getAiCardCount() == 3) {
			if (handler.getDeckCount() == 0) {
				if (handler.isPlayerHand()) {
					last = addDealPlayerLastHand(playerCardId, newCard);
				} else {
					last = addDeaAILastHand(playerCardId);
				}
			} else if (handler.isPlayerHand()) {
				last = addDealPlayerHand(playerCardId, newCard);
			} else {
				last = addDeaAIHand(playerCardId, newCard);
			}
		}
		last.setAnimationListener(getAnimationListener());
	}
}
