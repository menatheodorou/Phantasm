package com.phantasm.phantasm.common.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phantasm.phantasm.R;

/**
 * Created by YNA on 5/6/2016.
 */
public class PTCustomIconButton extends LinearLayout {

    private ImageView symbol;
    private TextView label;

    public PTCustomIconButton(Context context) {
        super(context);
        initView();
    }

    public PTCustomIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public PTCustomIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        getAttrs(attrs, defStyleAttr);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        View v = inflater.inflate(R.layout.widget_icon_button, this, false);
        addView(v);

        symbol = (ImageView) findViewById(R.id.symbol);
        label = (TextView) findViewById(R.id.label);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PTCustomIconButton);
        setTypeArray(typedArray);
    }


    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PTCustomIconButton, defStyle, 0);
        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typedArray) {
        int symbolResId = typedArray.getResourceId(R.styleable.PTCustomIconButton_symbol, R.drawable.phone_icon);
        symbol.setImageResource(symbolResId);

        String text = typedArray.getString(R.styleable.PTCustomIconButton_label);
        label.setText(text);

        typedArray.recycle();
    }

    void setSymbol(int symbolResID) {
        symbol.setImageResource(symbolResID);
    }

    void setText(String textString) {
        label.setText(textString);
    }

    void setText(int textResID) {
        label.setText(textResID);
    }
}
