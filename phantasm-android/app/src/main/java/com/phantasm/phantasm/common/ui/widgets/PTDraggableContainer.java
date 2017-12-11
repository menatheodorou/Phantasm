package com.phantasm.phantasm.common.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import su.levenetc.android.draggableview.DragController;
import su.levenetc.android.draggableview.DraggableView;
import su.levenetc.android.draggableview.SkewView;
import su.levenetc.android.draggableview.utils.Utils;

public class PTDraggableContainer extends FrameLayout implements DragController.IDragViewGroup {
	private DragController<PTDraggableContainer> mDragController = new DragController<>(this);
	private View mSelectedView;

	private boolean mFillAfter = false;
    private float mDragEndX;
    private float mDragEndY;
    private OnDragEventListener mListener;

	public PTDraggableContainer(Context context) {
		super(context);
	}

	public PTDraggableContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setFillAfter(boolean fillAfter) {
		mFillAfter = fillAfter;
	}

    public void setOnDragListener(OnDragEventListener listener) {
        mListener = listener;
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mDragController.onTouchEvent(event);
	}

	@Override
	public View onDownEvent(int x, int y) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (Utils.isViewContains(child, x, y, false)) {
				mSelectedView = child;
				return child;
			}
		}
		return null;
	}

	@Override
	public ViewGroup getContainerForDraggableView() {
        return this;
	}

	@Override
	public void onDragStart() {
		AlphaAnimation alphaAnim = new AlphaAnimation(1, 0.5f);
		alphaAnim.setDuration(500);
		alphaAnim.setFillAfter(true);
		startAnimation(alphaAnim);
        mSelectedView.setVisibility(View.INVISIBLE);

        float dragStartX = mSelectedView.getX();
        float dragStartY = mSelectedView.getY();
        if (mListener != null) mListener.onDragStart(dragStartX, dragStartY);
	}

	@Override
	public void onDragEnd() {
        clearAnimation();

        DraggableView draggableView = mDragController.getDraggableView();
        mDragEndX = draggableView.getX() + draggableView.getXTranslation();
        mDragEndY = draggableView.getY() + draggableView.getYTranslation();

        AnimatorSet translateSet = new AnimatorSet();

        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 0.5f, 1f);
		if (mFillAfter) {
            alpha.addListener(mDragEndAnimationListener);
            translateSet.playTogether(alpha);
		} else {
            ObjectAnimator transX = ObjectAnimator.ofFloat(
                    draggableView,
                    "translationX",
                    draggableView.getX(),
                    mSelectedView.getX() - draggableView.getXTranslation()
            );

            ObjectAnimator transY = ObjectAnimator.ofFloat(
                    draggableView,
                    "translationY",
                    draggableView.getY(),
                    mSelectedView.getY() - draggableView.getYTranslation()
            );

            transX.addListener(mDragEndAnimationListener);

            translateSet.playTogether(transX, transY, alpha);
		}

        translateSet.setInterpolator(new FastOutSlowInInterpolator());
        translateSet.setDuration(300);
        translateSet.start();
	}

	@Override
	public void onMoveEvent(float x, float y) {
        if (mListener != null) mListener.onDragging(x, y);
	}

	Animator.AnimatorListener mDragEndAnimationListener = new Animator.AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			mDragController.finishDrag();
			mSelectedView.setVisibility(View.VISIBLE);

			if (mListener != null) mListener.onDragEnd(mDragEndX, mDragEndY);
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}
	};

	@Override
	public DraggableView createDraggableView(
			Bitmap bitmap,
			VelocityTracker velocityTracker,
			PointF selectedViewPoint,
			PointF downEventPoint) {

		return new SkewView(
				getContext(),
				bitmap,
				velocityTracker,
				selectedViewPoint,
				downEventPoint,
				this
		);
	}
}