package com.phantasm.phantasm.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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


public class PTLoginActivity extends PTBaseActivity {
    private RelativeLayout mVGLogin;
    private RelativeLayout mVGForgotPass;
    private EditText mETUser;
    private EditText mETPass;
    private CallbackManager mETCallbackManager;
    private LoginButton mLoginButton;

    private static final String TAG_CONTACTS = "data";
    private static final String TAG_ERROR = "error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mETCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Home");

        initView();
    }

    public void initView(){
        mETUser = (EditText) findViewById(R.id.txtUserName);
        mETPass = (EditText) findViewById(R.id.txtPass);
        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
        mLoginButton.registerCallback(mETCallbackManager, new FacebookCallback<LoginResult>() {
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

        mVGForgotPass = (RelativeLayout) findViewById(R.id.forgot_pass_layout);
        mVGForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://phantasm.wtf/login/&do=forgot"));
                startActivity(browserIntent);
            }
        });
        mVGLogin = (RelativeLayout) findViewById(R.id.login_layout);
        mVGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    NameValuePair pair = new BasicNameValuePair("app", "Phantasm");
                    NameValuePair pair1 = new BasicNameValuePair("service", "User");
                    NameValuePair pair2 = new BasicNameValuePair("action", "signIn");
                    NameValuePair pair3 = new BasicNameValuePair("json", "true");
                    NameValuePair pair4 = new BasicNameValuePair("json_arguments", "[\"" + mETUser.getText().toString() + "\",\"" + mETPass.getText().toString() + "\"]");
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
                            if (jsonStr.contains("Username or password is not correct")) {
                                Log.i("success", "Faile");
                                String error = contents.getString(TAG_ERROR);
                                Utils.showAlertDialog(PTLoginActivity.this, error, true, null);
                            } else {
                                Log.i("success", "OK");

                                SharedPreferences.Editor prefsEditor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                                prefsEditor.putBoolean("login", true);
                                prefsEditor.commit();

                                if (PTHomeActivity.INSTANCE != null) {
                                    try {
                                        PTHomeActivity.INSTANCE.finish();
                                    } catch (Exception e) {
                                    }
                                }

                                Intent i = new Intent(PTLoginActivity.this, PTMainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("PTServiceHandler", "Couldn't get any data from the url");
                    }
                }
            }
        });
    }

    private boolean checkInput(){
        String _users = mETUser.getText().toString();
        String _pass = mETPass.getText().toString();
        if(_users.matches("")){
            Utils.showAlertDialog(this, "Please enter your UserName or Email.", true, null);
            return false;
        }else if(_pass.matches("")){
            Utils.showAlertDialog(this, "Please enter your Password.", true, null);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deprecated_menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mETCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void checkLoginIdFacebook(String id) {
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

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONObject contents = jsonObj.getJSONObject(TAG_CONTACTS);
                if (jsonStr.contains("error")) {
                    Log.i("success","Failed");
                    String error = contents.getString(TAG_ERROR);
                    Utils.showAlertDialog(this, error, true, null);
                } else {
                    Log.i("success","OK");
                    SharedPreferences.Editor prefsEditor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                    prefsEditor.putBoolean("login", true);
                    prefsEditor.commit();

                    if(PTHomeActivity.INSTANCE != null) {
                        try {
                            PTHomeActivity.INSTANCE.finish();
                        } catch (Exception e) {}
                    }
                    Intent i = new Intent(PTLoginActivity.this, PTMainActivity.class);
                    startActivity(i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("PTServiceHandler", "Couldn't get any data from the url");
        }
    }
}
