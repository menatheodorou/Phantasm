package com.gpit.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;

import com.gpit.android.library.R;

/**
 * Created by osxcapitan on 5/5/16.
 */
public class CustomFourDigitEditText extends TableLayout {

    private EditText mText1;
    private EditText mText2;
    private EditText mText3;
    private EditText mText4;

    public CustomFourDigitEditText(Context context) {
        super(context);
        initView();
    }

    public CustomFourDigitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(infService);
        View v = inflater.inflate(R.layout.four_digit_edit_view, this, false);
        addView(v);

        mText1 = (EditText) findViewById(R.id.text1);
        mText2 = (EditText) findViewById(R.id.text2);
        mText3 = (EditText) findViewById(R.id.text3);
        mText4 = (EditText) findViewById(R.id.text4);
    }

    public boolean isCompleted() {
        if(mText1.getText().toString().isEmpty()) {
            mText1.setFocusableInTouchMode(true);
            return false;
        }
        if(mText2.getText().toString().isEmpty()) {
            mText2.setFocusableInTouchMode(true);
            return false;
        }
        if(mText3.getText().toString().isEmpty()) {
            mText3.setFocusableInTouchMode(true);
            return false;
        }
        if(mText4.getText().toString().isEmpty()) {
            mText4.setFocusableInTouchMode(true);
            return false;
        }

        return true;
    }

    public String getText() {
        String result = "";
        result += mText1.getText().toString();
        result += mText2.getText().toString();
        result += mText3.getText().toString();
        result += mText4.getText().toString();

        return result;
    }
}
