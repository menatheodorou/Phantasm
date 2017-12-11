package com.phantasm.phantasm.common.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dreamfactory.LoginActivity;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.WebViewActivity;

/**
 * Created by kevinfinn on 5/22/15.
 */
public class PTLoginView_unused extends LinearLayout {
    public static final String LOGIN_URL = "";
    public static final String SIGNUP_URL = "";

    public PTLoginView_unused(Context context) {
        super(context);

        setup();
    }

    public PTLoginView_unused(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup();

    }

    public PTLoginView_unused(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setup();
    }

    public PTLoginView_unused(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        setup();
    }


    private void setup() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_login, this, true);

        Button loginButton, signupButton;

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO launch webview
//                getContext().startActivity(new Intent(getContext(), WebViewActivity.class).putExtra(WebViewActivity.EXTRA_URL, LOGIN_URL));
                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        signupButton = (Button) findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO launch webview
                getContext().startActivity(new Intent(getContext(), WebViewActivity.class).putExtra(WebViewActivity.EXTRA_URL, SIGNUP_URL));
            }
        });
    }
}
