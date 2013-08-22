package org.mmarini.briscola.app;

import org.mmarini.briscola.GameHandler;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 
 * @author us00852
 * 
 */
public abstract class AbstractAnimator {

	/**
	 * 
	 * @author us00852
	 * 
	 */
	private class OnAnimationEndBringOnFront implements AnimationListener {
		private View target;

		/**
		 * 
		 * @param target
		 * @param resId
		 */
		public OnAnimationEndBringOnFront(View target) {
			this.target = target;
		}

		/**
		 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd
		 *      (android.view.animation.Animation)
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			target.bringToFront();
			animation.setAnimationListener(null);
		}

		/**
		 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat
		 *      (android.view.animation.Animation)
		 */
		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		/**
		 * @see android.view.animation.Animation.AnimationListener#onAnimationStart
		 *      (android.view.animation.Animation)
		 */
		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	/**
	 * 
	 * @author us00852
	 * 
	 */
	private class OnAnimationEndChangeDrawable implements AnimationListener {
		private ImageView target;
		private int resId;

		/**
		 * 
		 * @param target
		 * @param resId
		 */
		public OnAnimationEndChangeDrawable(ImageView target, int resId) {
			this.target = target;
			this.resId = resId;
		}

		/**
		 * @see android.view.animation.Animation.AnimationListener#onAnimationEnd
		 *      (android.view.animation.Animation)
		 */
		@Override
		public void onAnimationEnd(Animation animation) {
			LoggerFactory.getLogger(OnAnimationEndChangeDrawable.class).info(
					"onAnimationEnd {} {}", target, Integer.toHexString(resId));
			target.setImageResource(resId);
			animation.setAnimationListener(null);
		}

		/**
		 * @see android.view.animation.Animation.AnimationListener#onAnimationRepeat
		 *      (android.view.animation.Animation)
		 */
		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		/**
		 * @see android.view.animation.Animation.AnimationListener#onAnimationStart
		 *      (android.view.animation.Animation)
		 */
		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	private static final int ANIMATION_DURATION = 500;
	private AnimationListener animationListener;
	private GameHandler handler;

	private long animationDuration;

	private Activity activity;

	/**
	 * 
	 */
	protected AbstractAnimator(Activity activity) {
		this.activity = activity;
		animationDuration = ANIMATION_DURATION;
	}

	/**
	 * 
	 * @param stepCount
	 * @return
	 */
	protected long computeDuration(int stepCount) {
		return animationDuration * stepCount;
	}

	/**
	 * 
	 * @param viewId
	 * @param startOffset
	 * @return
	 */
	protected Animation createBringOnFront(int viewId, long startOffset) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0);
		animation.setStartOffset(startOffset);
		animation.setDuration(10);
		animation.setFillEnabled(true);
		animation.setFillBefore(false);
		animation.setFillAfter(false);
		animation.setInterpolator(new LinearInterpolator());
		ImageView view = (ImageView) activity.findViewById(viewId);
		animation.setAnimationListener(new OnAnimationEndBringOnFront(view));
		return animation;
	}

	/**
	 * 
	 * @param viewId
	 * @param resId
	 * @param startOffset
	 * @return
	 */
	protected Animation createChangeDrawable(int viewId, int resId,
			long startOffset) {
		TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 0);
		animation.setStartOffset(startOffset);
		animation.setDuration(10);
		animation.setFillEnabled(true);
		animation.setFillBefore(false);
		animation.setFillAfter(false);
		animation.setInterpolator(new LinearInterpolator());
		ImageView view = (ImageView) activity.findViewById(viewId);
		animation.setAnimationListener(new OnAnimationEndChangeDrawable(view,
				resId));
		return animation;
	}

	/**
	 * 
	 * @param startOffset
	 * @return
	 */
	protected Animation createHorizontalFlipIn(long startOffset) {
		ScaleAnimation flip = new ScaleAnimation(1f, 0f, 1f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		flip.setStartOffset(startOffset);
		flip.setDuration(animationDuration);
		flip.setFillEnabled(true);
		flip.setFillAfter(false);
		flip.setFillBefore(false);
		flip.setInterpolator(new AccelerateInterpolator());
		return flip;
	}

	/**
	 * 
	 * @param startOffset
	 * @return
	 */
	protected Animation createHorizontalFlipOut(long startOffset) {
		ScaleAnimation flip = new ScaleAnimation(0f, 1f, 1f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		flip.setStartOffset(startOffset);
		flip.setDuration(animationDuration);
		flip.setFillEnabled(true);
		flip.setFillAfter(false);
		flip.setFillBefore(false);
		flip.setInterpolator(new DecelerateInterpolator());
		return flip;
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param startOffset
	 * @return
	 */
	protected Animation createRotation(float from, float to, long startOffset) {
		RotateAnimation rotation = new RotateAnimation(from, to,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotation.setStartOffset(startOffset);
		rotation.setDuration(animationDuration);
		rotation.setFillEnabled(true);
		rotation.setFillAfter(false);
		rotation.setFillBefore(false);
		rotation.setInterpolator(new AccelerateDecelerateInterpolator());
		return rotation;
	}

	/**
	 * 
	 * @param offsetViewId
	 * @param fromViewId
	 * @param toViewId
	 * @param startOffset
	 * @return
	 */
	protected Animation createTranslation(int offsetViewId, int fromViewId,
			int toViewId, long startOffset) {
		int x0 = getHorizontalCenter(offsetViewId);
		int y0 = getVerticalCenter(offsetViewId);
		int dx0 = getHorizontalCenter(fromViewId) - x0;
		int dy0 = getVerticalCenter(fromViewId) - y0;
		int dx1 = getHorizontalCenter(toViewId) - x0;
		int dy1 = getVerticalCenter(toViewId) - y0;

		TranslateAnimation shift = new TranslateAnimation(dx0, dx1, dy0, dy1);
		shift.setStartOffset(startOffset);
		shift.setDuration(animationDuration);
		shift.setFillEnabled(true);
		shift.setFillAfter(false);
		shift.setFillBefore(false);
		shift.setInterpolator(new AccelerateDecelerateInterpolator());
		return shift;
	}

	/**
	 * 
	 * @param startOffset
	 * @return
	 */
	protected Animation createVerticalFlipIn(long startOffset) {
		ScaleAnimation flip = new ScaleAnimation(1f, 1f, 1f, 0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		flip.setStartOffset(startOffset);
		flip.setDuration(animationDuration);
		flip.setFillEnabled(true);
		flip.setFillAfter(false);
		flip.setFillBefore(false);
		flip.setInterpolator(new AccelerateInterpolator());
		return flip;
	}

	/**
	 * 
	 * @param startOffset
	 * @return
	 */
	protected Animation createVerticalFlipOut(long startOffset) {
		ScaleAnimation flip = new ScaleAnimation(1f, 1f, 0f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		flip.setStartOffset(startOffset);
		flip.setDuration(animationDuration);
		flip.setFillEnabled(true);
		flip.setFillAfter(false);
		flip.setFillBefore(false);
		flip.setInterpolator(new DecelerateInterpolator());
		return flip;
	}

	/**
	 * @return the activity
	 */
	protected Activity getActivity() {
		return activity;
	}

	/**
	 * @return the animationDuration
	 */
	public long getAnimationDuration() {
		return animationDuration;
	}

	/**
	 * @return the listener
	 */
	protected AnimationListener getAnimationListener() {
		return animationListener;
	}

	/**
	 * @return the handler
	 */
	protected GameHandler getHandler() {
		return handler;
	}

	/**
	 * 
	 * @param resId
	 * @return
	 */
	protected int getHorizontalCenter(int resId) {
		View target = activity.findViewById(resId);
		return (target.getLeft() + target.getRight()) / 2;
	}

	/**
	 * 
	 * @param resId
	 * @return
	 */
	protected int getVerticalCenter(int resId) {
		View target = activity.findViewById(resId);
		return (target.getTop() + target.getBottom()) / 2;
	}

	/**
	 * @param animationDuration
	 *            the animationDuration to set
	 */
	public void setAnimationDuration(long animationDuration) {
		this.animationDuration = animationDuration;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setAnimationListener(AnimationListener listener) {
		this.animationListener = listener;
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(GameHandler handler) {
		this.handler = handler;
	}
}