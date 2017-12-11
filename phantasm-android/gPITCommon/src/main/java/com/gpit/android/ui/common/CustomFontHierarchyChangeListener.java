package com.gpit.android.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.gpit.android.library.R;
import com.gpit.android.util.FontUtils;
import com.gpit.android.util.StringUtils;

public class CustomFontHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
    private ViewGroup.OnHierarchyChangeListener mListener;
    private String mFontName;
    private String mRegularFontName;
    private String mBoldFontName;
    private String mItalicFontName;

    public CustomFontHierarchyChangeListener(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);

        mFontName = a.getString(R.styleable.CustomFont_font);
        mRegularFontName = a.getString(R.styleable.CustomFont_regularFont);
        mBoldFontName = a.getString(R.styleable.CustomFont_boldFont);
        mItalicFontName = a.getString(R.styleable.CustomFont_italicFont);
        /*
        mFontName = StringUtils.getString(context, a.getResourceId(R.styleable.CustomFont_font, 0), null);
        mRegularFontName = StringUtils.getString(context, a.getResourceId(R.styleable.CustomFont_regularFont, 0), null);
        mBoldFontName = StringUtils.getString(context, a.getResourceId(R.styleable.CustomFont_boldFont, 0), null);
        mItalicFontName = StringUtils.getString(context, a.getResourceId(R.styleable.CustomFont_italicFont, 0), null);
        */
    }

    public void setHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        FontUtils.applyFontsInView(parent.getContext(), mFontName, mRegularFontName,
                mBoldFontName, mItalicFontName, child);

        if (mListener != null) mListener.onChildViewAdded(parent, child);
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if (mListener != null) mListener.onChildViewRemoved(parent, child);
    }
}
