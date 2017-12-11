package com.phantasm.phantasm.main.connect.mychannel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.main.connect.PTConnectFragment;

/**
 * Created by osxcapitan on 5/5/16.
 */
public class PTSigninFragment extends PTBaseFragment {

    private PTConnectFragment mConnectFragment;

    public static PTSigninFragment newInstance() {
        return new PTSigninFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_signin;
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
        LinearLayout btnPhoneNumber = (LinearLayout) findViewById(R.id.btn_phone_number);
        btnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPhoneNumber();
            }
        });
        LinearLayout btnEmail = (LinearLayout) findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickEmail();
            }
        });
        TextView tvLinkSignup = (TextView) findViewById(R.id.tv_link_signup);
        tvLinkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignup();
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

    private void onClickPhoneNumber() {
        mConnectFragment.gotoNextFragment(PTConnectFragment.FRAGMENT_TAG_SIGNIN_PHONE_NUMBER);
    }

    private void onClickEmail() {
        mConnectFragment.gotoNextFragment(PTConnectFragment.FRAGMENT_TAG_SIGNIN_EMAIL);
    }

    private void onClickSignup() {
        mConnectFragment.gotoNextFragment(PTConnectFragment.FRAGMENT_TAG_SIGNUP);
    }
}
