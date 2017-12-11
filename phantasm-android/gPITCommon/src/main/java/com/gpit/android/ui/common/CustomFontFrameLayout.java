package com.gpit.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.gpit.android.util.FontUtils;

public class CustomFontFrameLayout extends FrameLayout {
    private CustomFontHierarchyChangeListener mInternalHierarchyChangeListener = null;

    public CustomFontFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInternalHierarchyChangeListener = new CustomFontHierarchyChangeListener(getContext(), attrs);
        super.setOnHierarchyChangeListener(mInternalHierarchyChangeListener);
    }

    public void setOnHierarchyChangeListener(CustomFontHierarchyChangeListener listener) {
        mInternalHierarchyChangeListener.setHierarchyChangeListener(listener);
    }
}
