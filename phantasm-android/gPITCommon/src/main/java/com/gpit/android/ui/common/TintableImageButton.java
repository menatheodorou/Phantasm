package com.gpit.android.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.gpit.android.library.R;

public class TintableImageButton extends ImageButton{

        private ColorStateList tint;

        public TintableImageButton(Context context) {
            super(context);
        }

        public TintableImageButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs, 0);
        }

        public TintableImageButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(context, attrs, defStyle);
        }

        private void init(Context context, AttributeSet attrs, int defStyle) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintableImageButton, defStyle, 0);
            tint = a.getColorStateList(R.styleable.TintableImageButton_tint);
            a.recycle();
        }

        @Override
        protected void drawableStateChanged() {
            super.drawableStateChanged();
            if (tint != null && tint.isStateful())
                updateTintColor();
        }

        public void setColorFilter(ColorStateList tint) {
            this.tint = tint;
            super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
        }

        private void updateTintColor() {
            int color = tint.getColorForState(getDrawableState(), 0);
            setColorFilter(color);
        }

}
