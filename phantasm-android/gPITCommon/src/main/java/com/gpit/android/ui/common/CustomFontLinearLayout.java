package com.gpit.android.ui.common;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.gpit.android.util.FontUtils;

public class CustomFontLinearLayout extends LinearLayout {
	private CustomFontHierarchyChangeListener mInternalHierarchyChangeListener = null;

    public CustomFontLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mInternalHierarchyChangeListener = new CustomFontHierarchyChangeListener(getContext(), attrs);
		super.setOnHierarchyChangeListener(mInternalHierarchyChangeListener);
	}

	public void setOnHierarchyChangeListener(CustomFontHierarchyChangeListener listener) {
		mInternalHierarchyChangeListener.setHierarchyChangeListener(listener);
	}
}
