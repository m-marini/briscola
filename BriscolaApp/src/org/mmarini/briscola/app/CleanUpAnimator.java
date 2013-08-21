/**
 * 
 */
package org.mmarini.briscola.app;

import android.app.Activity;
import android.view.animation.Animation;

/**
 * @author US00852
 * 
 */
public class CleanUpAnimator extends AbstractDealAnimator {

	/**
	 * 
	 */
	public CleanUpAnimator(Activity activity) {
		super(activity);
	}

	/**
	 * 
	 */
	public void start() {
		Animation last = addSwapOut(R.id.playerCard);
		Animation anim = addSwapOut(R.id.aiCard);
		if (anim != null)
			last = anim;
		last = addSwapOut(R.id.aiCard1);
		if (anim != null)
			last = anim;
		last = addSwapOut(R.id.aiCard2);
		if (anim != null)
			last = anim;
		last = addSwapOut(R.id.aiCard3);
		if (anim != null)
			last = anim;
		last = addSwapOut(R.id.playerCard1);
		if (anim != null)
			last = anim;
		last = addSwapOut(R.id.playerCard2);
		if (anim != null)
			last = anim;
		last = addSwapOut(R.id.playerCard3);
		if (anim != null)
			last = anim;
		anim = addSwapOut(R.id.trumpCard);
		if (anim != null)
			last = anim;
		last.setAnimationListener(getAnimationListener());
	}
}
