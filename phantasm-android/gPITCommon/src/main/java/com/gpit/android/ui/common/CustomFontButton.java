package com.gpit.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.gpit.android.util.FontUtils;
import com.gpit.android.util.Utils;

public class CustomFontButton extends Button {

	public CustomFontButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		FontUtils.applyFontsInView(context, attrs, this);
	}
}
