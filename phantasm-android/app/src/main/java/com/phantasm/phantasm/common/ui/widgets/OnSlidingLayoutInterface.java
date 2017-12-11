package com.phantasm.phantasm.common.ui.widgets;

/**
 * Created by Joseph Luns on 2016/3/15.
 */
public interface OnSlidingLayoutInterface {
    public boolean canScrollVertically(int direction);
    public void updateLayout();
    public void swipLeft();
    public void swipRight();
}
