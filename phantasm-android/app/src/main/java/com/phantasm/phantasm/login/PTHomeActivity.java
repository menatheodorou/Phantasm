package com.phantasm.phantasm.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.gpit.android.util.Utils;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseActivity;
import com.phantasm.phantasm.main.PTMainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PTHomeActivity extends PTBaseActivity implements View.OnClickListener{

    public static PTHomeActivity INSTANCE = null;

    private static final String TAG_CONTACTS = "data";
    private static final String TAG_ERROR = "error";

    private RelativeLayout mVGLoginLayout;
    private RelativeLayout mVGSignupLayout;

    private LoginButton mLoginButton;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INSTANCE = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_home);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setRegisterCallBack(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        initView();
    }

    private void initView(){
        mVGLoginLayout = (RelativeLayout) findViewById(R.id.login_layout);
        mVGLoginLayout.setOnClickListener(this);
        mVGSignupLayout = (RelativeLayout) findViewById(R.id.signup_layout);
        mVGSignupLayout.setOnClickListener(this);
    }

    private void setRegisterCallBack(boolean isYes){
        if(isYes) {
            mLoginButton = null;
            mLoginButton = (LoginButton) findViewById(R.id.login_button);
            mLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
            mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                    LoginManager.getInstance().logOut();
                                    PTLoginAPIInfo.jsonFB = object;
                                    Log.i("ob", object.toString());
                                    try {
                                        checkLoginIdFacebook(object.getString("id"));
                                        Log.i("email", object.getString("email"));
                                    } catch (JSONException e) {
                                        Log.i("error", "cannot get respone");
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }


                @Override
                public void onCancel() {
                    Log.d("VIVZ", "onCancel");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.d("VIVZ", "onError " + e);
                }
            });
            Log.i("setRegisterCallBack", "True");
        }else{
            mLoginButton = null;
            mLoginButton = (LoginButton) findViewById(R.id.login_button);
            mLoginButton.setOnClickListener(this);
            Log.i("setRegisterCallBack", "False");
        }
    }

    private void checkLoginIdFacebook(String id) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        NameValuePair pair = new BasicNameValuePair("app", "Phantasm");
        NameValuePair pair1 = new BasicNameValuePair("service", "User");
        NameValuePair pair2 = new BasicNameValuePair("action", "signInByFacebook");
        NameValuePair pair3 = new BasicNameValuePair("json", "true");
        NameValuePair pair4 = new BasicNameValuePair("json_arguments", "[\"" + id + "\"]");
        pairs.add(pair);
        pairs.add(pair1);
        pairs.add(pair2);
        pairs.add(pair3);
        pairs.add(pair4);
        PTServiceHandler sh = new PTServiceHandler();
        String jsonStr = sh.makeServiceCall(PTLoginAPIInfo.GATEWAY, PTServiceHandler.POST, pairs);

        //                Log.i("Response1: ", "> " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONObject contents = jsonObj.getJSONObject(TAG_CONTACTS);
                if (jsonStr.contains("error")) {
                    Log.i("success", "Faile");
                    String error = contents.getString(TAG_ERROR);
                    Utils.showAlertDialog(this, error, true, null);
                } else {
                    SharedPreferences.Editor prefsEditor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                    prefsEditor.putBoolean("login", true);
                    prefsEditor.commit();
                    Intent i = new Intent(PTHomeActivity.this, PTMainActivity.class);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("PTServiceHandler", "Couldn't get any data from the url");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deprecated_menu_home, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(PTLoginAPIInfo.jsonFB.toString()!="")

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        INSTANCE = null;
    }

    /*************** EVENT LISTENER *****************/
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signup_layout){
            Intent i = new Intent(PTHomeActivity.this, PTSignUpActivity.class);
            startActivity(i);
        }else if(v.getId() == R.id.login_layout){
            Intent i = new Intent(PTHomeActivity.this, PTLoginActivity.class);
            startActivity(i);
        }else if(v.getId() == R.id.login_button){
            try {
                checkLoginIdFacebook(PTLoginAPIInfo.jsonFB.getString("id"));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
