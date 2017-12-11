package com.gpit.android.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpit.android.library.R;

public class SquareImageView extends ImageView {
	private boolean mKeepWidth;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initAttrs(attrs);
    }

    public void initAttrs(AttributeSet attrs) {
    	TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ViewGroup);
        mKeepWidth = a.getBoolean(R.styleable.ViewGroup_keep_width, true);
		a.recycle();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (mKeepWidth) {
            setMeasuredDimension(width, width);
        } else {
            setMeasuredDimension(height, height);
        }
    }
}
