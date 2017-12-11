package com.phantasm.phantasm.main.connect.mychannel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.main.connect.PTConnectFragment;

/**
 * Created by osxcapitan on 5/5/16.
 */
public class PTSignupFragment extends PTBaseFragment {

    private PTConnectFragment mConnectFragment;

    public static PTSignupFragment newInstance() {
        return new PTSignupFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_signup;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initUI() {
        initActionBar();

        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectFragment.onBackPressed();
            }
        });
        Button btnSignup = (Button) findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignup();
            }
        });
        TextView tvSignin = (TextView) findViewById(R.id.tv_link_signin);
        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectFragment.onBackPressed();
            }
        });

        updateUI();
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    private void initActionBar() {
        setHasOptionsMenu(true);
    }

    public void setParentFragment(PTConnectFragment fragment) {
        this.mConnectFragment = fragment;
    }

    private void updateUI() {
//        if (mSearchView == null) return;

//        updateLayout(false);
    }

    private void onClickSignup() {
        mConnectFragment.gotoNextFragment(PTConnectFragment.FRAGMENT_TAG_SIGNUP_CODE);
    }
}
