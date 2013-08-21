/**
 * 
 */
package org.mmarini.briscola.app;

import org.mmarini.briscola.GameHandler;

import android.app.Activity;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

/**
 * @author US00852
 * 
 */
public class AIMoveAnimator extends AbstractAnimator {
	private CardDrawableFactory cardDrawableFactory;

	/**
	 * 
	 */
	public AIMoveAnimator(Activity activity) {
		super(activity);
		cardDrawableFactory = CardDrawableFactory.getInstance();
	}

	/**
	 * 
	 * @param resId
	 */
	public void start() {
		int cardId;
		GameHandler handler = getHandler();
		switch (handler.getAiCardCount()) {
		case 0:
			cardId = R.id.aiCard1;
			break;
		case 1:
			cardId = R.id.aiCard2;
			break;
		default:
			cardId = R.id.aiCard3;
			break;
		}
		int resId = cardDrawableFactory.findResId(handler.getAiCard());

		AnimationSet animation = new AnimationSet(false);
		;
		animation.addAnimation(createChangeDrawable(R.id.aiCard,
				R.drawable.retro_rot, 0));
		animation.addAnimation(createTranslation(R.id.aiCard, cardId,
				R.id.aiCard, 0));
		animation.addAnimation(createVerticalFlipIn(computeDuration(1)));
		animation.addAnimation(createChangeDrawable(R.id.aiCard, resId,
				computeDuration(2)));
		animation.addAnimation(createVerticalFlipOut(computeDuration(2)));
		animation.setAnimationListener(getAnimationListener());

		Activity activity = getActivity();
		activity.findViewById(R.id.aiCard).startAnimation(animation);

		ImageView aiCardFrom = (ImageView) activity.findViewById(cardId);
		aiCardFrom.setImageResource(R.drawable.empty);
	}
}
