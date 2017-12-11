package com.phantasm.phantasm.main.connect.mychannel;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gpit.android.util.CountryCodes;
import com.gpit.android.util.Utils;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.main.connect.PTConnectFragment;

/**
 * Created by osxcapitan on 5/5/16.
 */
public class PTVerifyCodeFragment extends PTBaseFragment {

    private PTConnectFragment mConnectFragment;

    private CountryCodes mCountryCodes;
    private int mCountryCodeIdx;

    private TextView mTvCountryCode;
    private EditText mEditPhoneNumber;
    private EditText mEditPassword;

    public static PTVerifyCodeFragment newInstance() {
        return new PTVerifyCodeFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_signin_phonenumber;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initUI() {
        initActionBar();
        mCountryCodes = new CountryCodes(getContext());
        TelephonyManager manager = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
        mCountryCodeIdx = CountryCodes.getIndex(manager.getSimCountryIso());
        mTvCountryCode = (TextView) findViewById(R.id.tv_country_code);
        if(mCountryCodeIdx > -1) {
            mTvCountryCode.setText(mCountryCodes.getCountryCodes()[mCountryCodeIdx]);
        }
        mTvCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCountryCode();
            }
        });

        mEditPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        Button btnContinue = (Button) findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContinue();
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

    private void onClickCountryCode() {
//        TelecomManager manager = (TelecomManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        AlertDialog.Builder countryDlg = new AlertDialog.Builder(getContext());
        countryDlg.setTitle("Country Code");
        countryDlg.setItems(mCountryCodes.getCountryCodes(),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCountryCodeIdx = which;
                        mTvCountryCode.setText(mCountryCodes.getCountryCodes()[which]);
                    }
                }).show();
    }

    private void onClickContinue() {
        String strPhoneNumber = mEditPhoneNumber.getText().toString();
        if(strPhoneNumber.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the Phone Number.", Toast.LENGTH_SHORT).show();
            mEditPhoneNumber.setFocusableInTouchMode(true);
            return;
        } else if(!Utils.checkPhoneNoFormat(strPhoneNumber)) {
            Toast.makeText(getContext(), "Please enter the Phone Number correctly.", Toast.LENGTH_SHORT).show();
            mEditPhoneNumber.setFocusableInTouchMode(true);
            mEditPhoneNumber.setSelected(true);
            return;
        }

        String strPwd = mEditPassword.getText().toString();
        if(strPwd.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the Password.", Toast.LENGTH_SHORT).show();
            mEditPassword.setFocusableInTouchMode(true);
        }
    }
}
