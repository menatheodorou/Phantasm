package com.phantasm.phantasm.common.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class PTVerticalSlidingLayout extends FrameLayout implements GestureDetector.OnGestureListener {
    public final static int SENSITIVE_MOVE_X_DISTANCE = 200;
    public final static int SENSITIVE_MOVE_Y_DISTANCE = 200;

    private OnSlidingLayoutInterface mSlidingLayoutInterface;
    private GestureDetector mGestureDetector;
    private boolean mIsMovingY = false;
    private boolean mIsIntercept = false;

    public PTVerticalSlidingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public PTVerticalSlidingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PTVerticalSlidingLayout(Context context) {
        super(context);

        init();
    }

    public void setSlidingLayoutInterface(OnSlidingLayoutInterface slidingLayoutInterface) {
        mSlidingLayoutInterface = slidingLayoutInterface;
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        mIsIntercept = true;

        boolean result = mGestureDetector.onTouchEvent(event);

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mIsIntercept = false;

        boolean result = mGestureDetector.onTouchEvent(event);

        return result;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mIsMovingY = false;

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int moveX = (int) (e2.getX() - e1.getX());
        int moveY = (int) (e2.getY() - e1.getY());

        if (Math.abs(moveY) > SENSITIVE_MOVE_Y_DISTANCE) {
            mIsMovingY = true;
        }

        FrameLayout.LayoutParams layoutParam = (FrameLayout.LayoutParams) getLayoutParams();
        int orgY = layoutParam.topMargin;
        int newY;

        if (mIsIntercept) {
            if (Math.abs(moveY) > Math.abs(moveX)) {
                // Lets pass this event to child first
                if (moveY > 0) {
                    if (mSlidingLayoutInterface != null && mSlidingLayoutInterface.canScrollVertically(-1)) {
                        return false;
                    }
                } else {
                    if (layoutParam.topMargin == 0) {
                        return false;
                    }
                }
            }
        }

        if (mIsMovingY) {
            layoutParam.topMargin = layoutParam.topMargin + moveY;
            setLayoutParams(layoutParam);

            if (mSlidingLayoutInterface != null) mSlidingLayoutInterface.updateLayout();

            newY = ((FrameLayout.LayoutParams) getLayoutParams()).topMargin;
            if (orgY == newY) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int moveX = (int)Math.abs(e2.getX() - e1.getX());
        int moveY = (int)Math.abs(e2.getY() - e1.getY());

        if (!mIsMovingY && (moveX > moveY) & (moveX > SENSITIVE_MOVE_X_DISTANCE)) {
            if (mSlidingLayoutInterface != null) {
                if (moveX > 0) {
                    mSlidingLayoutInterface.swipRight();
                } else {
                    mSlidingLayoutInterface.swipLeft();
                }
            }
            return true;
        }

        return false;
    }
}
