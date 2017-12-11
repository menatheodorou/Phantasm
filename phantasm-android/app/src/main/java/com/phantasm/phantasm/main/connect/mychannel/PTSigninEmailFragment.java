package com.phantasm.phantasm.main.connect.mychannel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gpit.android.util.Utils;
import com.gpit.android.webapi.OnAPICompletedListener;
import com.gpit.android.webapi.OnCommonAPICompleteListener;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.PTPreference;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.login.PTHomeActivity;
import com.phantasm.phantasm.main.PTMainActivity;
import com.phantasm.phantasm.main.api.vma.PTVMAPostSignInAPI;
import com.phantasm.phantasm.main.connect.PTConnectFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by osxcapitan on 5/5/16.
 */
public class PTSigninEmailFragment extends PTBaseFragment {

    private PTConnectFragment mConnectFragment;
    private PTVMAPostSignInAPI mLoginApi;

    private EditText mEditEmail;
    private EditText mEditPassword;

    public static PTSigninEmailFragment newInstance() {
        return new PTSigninEmailFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_signin_email;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mLoginApi = new PTVMAPostSignInAPI(getContext());
    }

    @Override
    protected void initUI() {
        initActionBar();

        mEditEmail = (EditText) findViewById(R.id.edit_email);
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

    private void onClickContinue() {
        final String strEmail = mEditEmail.getText().toString();
        if(strEmail.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the Email Address.", Toast.LENGTH_SHORT).show();
            mEditEmail.setFocusableInTouchMode(true);
            return;
        } else if(!Utils.checkEmailFormat(strEmail)) {
            Toast.makeText(getContext(), "Please enter the Email Address correctly.", Toast.LENGTH_SHORT).show();
            mEditEmail.setFocusableInTouchMode(true);
            mEditEmail.setSelected(true);
            return;
        }

        String strPwd = mEditPassword.getText().toString();
        if(strPwd.isEmpty()) {
            Toast.makeText(getContext(), "Please enter the Password.", Toast.LENGTH_SHORT).show();
            mEditPassword.setFocusableInTouchMode(true);
        }

        String jsonArguments = String.format(Locale.getDefault(), "[\"%s\",\"$s\"]", strEmail, strPwd);
        mLoginApi.setInfo(PTConst.ACTION_SIGNIN, jsonArguments);
        sendRequestSignin(new OnCommonAPICompleteListener<PTWebAPI>(getActivity()) {
            @Override
            public void onCompleted(PTWebAPI webapi) {
                try {
                    JSONObject jsonObj = webapi.getResponseData();

                    // Getting JSON Array node
                    JSONObject contents = jsonObj.getJSONObject(PTConst.TAG_DATA);
                    JSONObject user = contents.getJSONObject(PTConst.TAG_USER);

                    PTPreference ptPreference = PTPreference.getInstance(getContext());
                    ptPreference.putIsSignin(true);
                    ptPreference.putUserId(user.getInt("id"));
                    ptPreference.putUserName(user.getString("username"));
                    ptPreference.putEmail(user.getString("email"));

                    mConnectFragment.closeAllSubFragments();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(PTWebAPI webapi) {
                super.onFailed(webapi);
                try {
                    JSONObject jsonObj = webapi.getResponseData();

                    // Getting JSON Array node
                    JSONObject contents = jsonObj.getJSONObject(PTConst.TAG_DATA);
                    Utils.showAlertDialog(getActivity(), contents.getString(PTConst.TAG_ERROR), true, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendRequestSignin(OnAPICompletedListener<PTWebAPI> listener) {
        mLoginApi.setRequestMethod(false);
        mLoginApi.showProgress(true, "Signing in...", false);
        mLoginApi.exec(listener);
    }
}
