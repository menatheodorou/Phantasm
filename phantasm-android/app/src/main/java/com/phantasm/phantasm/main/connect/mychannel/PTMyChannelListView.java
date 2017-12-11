package com.phantasm.phantasm.main.connect.mychannel;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTPreference;
import com.phantasm.phantasm.main.PTMainActivity;
import com.phantasm.phantasm.main.PTMainFragment;
import com.phantasm.phantasm.main.connect.PTConnectFragment;

/**
 * Created by YNA on 5/4/2016.
 */
public class PTMyChannelListView extends LinearLayout implements View.OnClickListener {

    private PTPreference mPref;
    private PTMainActivity mMainActivity;
    private View mContentView;

    private PTConnectFragment mConnectFragment;

    public PTMyChannelListView(Context context) {
        super(context);
        initLayout(context);
    }

    public PTMyChannelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PTMyChannelListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    public PTMyChannelListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context);
    }

    private void initLayout(Context context) {
        this.mPref = PTPreference.getInstance(context);
        this.mMainActivity = (PTMainActivity) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContentView = inflater.inflate(R.layout.widget_signup, this, false);
        addView(this.mContentView);

        if(this.mPref.getIsSignin()) {
            initMyChannelListView();
        } else {
            initSignupView();
        }
    }

    private void initSignupView() {
        HorizontalScrollView hsView = (HorizontalScrollView) findViewById(R.id.hsv_my_channel);
        hsView.setVisibility(View.GONE);
        Button btnSignup = (Button) findViewById(R.id.btn_signup);
        btnSignup.setVisibility(View.VISIBLE);
        btnSignup.setOnClickListener(this);
    }

    public void initMyChannelListView() {
        HorizontalScrollView hsView = (HorizontalScrollView) findViewById(R.id.hsv_my_channel);
        hsView.setVisibility(View.VISIBLE);
        Button btnSignup = (Button) findViewById(R.id.btn_signup);
        btnSignup.setVisibility(View.GONE);
    }

    public void setParentFragment(PTConnectFragment fragment) {
        this.mConnectFragment = fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                onClickSignUp();
                break;
        }
    }

    private void onClickSignUp() {
        mConnectFragment.showSignupFragment();
    }
}
