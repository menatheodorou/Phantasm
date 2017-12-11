package com.gpit.android.util;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gpit.android.library.R;

import java.util.WeakHashMap;
import java.util.jar.Attributes;

public class FontUtils {
    // Fonts
    private static WeakHashMap<String, Typeface> fontMap = new WeakHashMap<String, Typeface>();

	// Set customized font located at asset folder
	public static Typeface loadFont(Context context, String fontName) {
		Typeface typeface = fontMap.get(fontName);
		if (typeface == null) {
			typeface = Typeface.createFromAsset(context.getAssets(), fontName);
			fontMap.put(fontName, typeface);
		}
		
		return typeface;
	}
	
	public static void setAssetFont(Context context, View view, String fontName) {
		setAssetFont(context, view, fontName, fontName, fontName);
	}
	
	public static void setAssetFont(Context context, View view, String regularFont, String boldFont, String italicFont) {
		Typeface regularTypeface = null;
		Typeface boldTypeface = null;
		Typeface italicTypeface = null;
		Typeface selectedFace = null;
		
		try {
			regularTypeface = loadFont(context, regularFont);
			boldTypeface = loadFont(context, boldFont);
			italicTypeface = loadFont(context, italicFont);
			
			if (view instanceof TextView) {
				TextView textView = ((TextView)view);
				
				selectedFace = regularTypeface;
				if (textView.getTypeface() != null) {
					Typeface typeFace = textView.getTypeface();
					if (typeFace.isBold()) {
						selectedFace = boldTypeface;
					} else if (typeFace.isItalic()) {
						selectedFace = italicTypeface;
					}
				}
				textView.setTypeface(selectedFace);
			}
		} catch (Exception e) {
		}
	}

	public static void setAssetFont(Context context, ViewGroup group, String fontName) {
		setAssetFont(context, group, fontName, fontName, fontName);
	}

	public static void setAssetFont(Context context, ViewGroup group, String regularFont, String boldFont, String italicFont) {
		for (int i = 0; i < group.getChildCount(); i++) {
			View child = group.getChildAt(i);
			
			if (child instanceof ViewGroup) {
				setAssetFont(context, (ViewGroup)child, regularFont, boldFont, italicFont);
			} else if (child instanceof TextView) {
				setAssetFont(context, child, regularFont, boldFont, italicFont);
			}
		}
	}

	public static void applyFontsInView(Context context, AttributeSet attrs, View rootView) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFont);

        String fontName = a.getString(R.styleable.CustomFont_font);
        String regularFontName = a.getString(R.styleable.CustomFont_regularFont);
        String boldFontName = a.getString(R.styleable.CustomFont_boldFont);
        String italicFontName = a.getString(R.styleable.CustomFont_italicFont);

        applyFontsInView(context, fontName, regularFontName, boldFontName, italicFontName, rootView);
    }

    public static void applyFontsInView(Context context, String fontName,
                                        String regularFontName, String boldFontName,
                                        String italicFontName, View rootView) {
		if (!StringUtils.isNullOrEmpty(fontName)) {
			if (rootView instanceof ViewGroup)
				FontUtils.setAssetFont(context, (ViewGroup)rootView, fontName);
			else
				FontUtils.setAssetFont(context, rootView, fontName);
		} else {
			if (rootView instanceof ViewGroup)
				FontUtils.setAssetFont(context, (ViewGroup)rootView, regularFontName, boldFontName, italicFontName);
			else
				FontUtils.setAssetFont(context, rootView, regularFontName, boldFontName, italicFontName);
		}
	}
}
