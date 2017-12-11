package com.gpit.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.gpit.android.util.FontUtils;

public class CustomFontRelativeLayout extends RelativeLayout {
	private CustomFontHierarchyChangeListener mInternalHierarchyChangeListener = null;

	public CustomFontRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mInternalHierarchyChangeListener = new CustomFontHierarchyChangeListener(getContext(), attrs);
		super.setOnHierarchyChangeListener(mInternalHierarchyChangeListener);
	}

	public void setOnHierarchyChangeListener(CustomFontHierarchyChangeListener listener) {
		mInternalHierarchyChangeListener.setHierarchyChangeListener(listener);
	}
}
