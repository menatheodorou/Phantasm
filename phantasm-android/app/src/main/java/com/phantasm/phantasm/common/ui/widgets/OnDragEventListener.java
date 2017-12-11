package com.phantasm.phantasm.common.ui.widgets;

/**
 * Created by Joseph Luns on 2016/3/18.
 */
public interface OnDragEventListener {
    public void onDragStart(float x, float y);
    public void onDragging(float x, float y);
    public void onDragEnd(float x, float y);
}
