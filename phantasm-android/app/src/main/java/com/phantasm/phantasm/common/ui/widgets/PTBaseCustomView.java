package com.phantasm.phantasm.common.ui.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class PTBaseCustomView extends FrameLayout {
    protected static String TAG = PTBaseCustomView.class.getSimpleName();

    protected abstract int getRootLayoutId();
    protected abstract void initUI();

    private boolean isCreated = false;

    public PTBaseCustomView(Context context) {
        super(context);
    }

    public PTBaseCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public PTBaseCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!isCreated) {
            isCreated = true;
            init();
        }
    }

    private void createLayout() {
        TAG = this.getClass().getSimpleName();

        inflate(getContext(), getRootLayoutId(), this);
    }

    private void init() {
        initData();
        createLayout();
        initUI();
    }

    protected void initData() {}
}
