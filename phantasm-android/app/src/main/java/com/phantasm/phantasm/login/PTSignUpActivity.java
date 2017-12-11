package com.phantasm.phantasm.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
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
import com.phantasm.phantasm.welcome.PTTermsActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PTSignUpActivity extends PTBaseActivity {
    private static final String TAG_CONTACTS = "data";
    private static final String TAG_ERROR = "error";

    private  static final int REQUEST_CODE = 1;

    private EditText mETUser;
    private EditText mETEmail;
    private EditText mETPass;
    private EditText mETConfirmPass;
    private CheckBox mCBTerms;
    private RelativeLayout mVGSignup;
    private CallbackManager mCallbackManager;
    private LoginButton mBTLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Home");

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.deprecated_menu_sign_up, menu);
        return true;
    }

    public void initView(){
        mETUser = (EditText) findViewById(R.id.txtUserName);
        mETEmail = (EditText) findViewById(R.id.txtEmail);
        mETPass = (EditText) findViewById(R.id.txtPass);
        mETConfirmPass = (EditText) findViewById(R.id.txtPassConfirm);
        mCBTerms = (CheckBox) findViewById(R.id.cbxTerms);
        mCBTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCBTerms.setChecked(false);
                Intent i = new Intent(PTSignUpActivity.this, PTTermsActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
        mVGSignup = (RelativeLayout) findViewById(R.id.signup_layout);
        mVGSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    PTServiceHandler sh = new PTServiceHandler();

                    // Making a request to url and getting response
                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    NameValuePair pair = new BasicNameValuePair("app", "Phantasm");
                    NameValuePair pair1 = new BasicNameValuePair("service", "User");
                    NameValuePair pair2 = new BasicNameValuePair("action", "signUp");
                    NameValuePair pair3 = new BasicNameValuePair("json", "true");
                    NameValuePair pair4 = new BasicNameValuePair("json_arguments", "[\"" + mETEmail.getText().toString() + "\",\"" + mETPass.getText().toString() + "\",\"" + mETUser.getText().toString() + "\"]");
                    pairs.add(pair);
                    pairs.add(pair1);
                    pairs.add(pair2);
                    pairs.add(pair3);
                    pairs.add(pair4);
                    String jsonStr = sh.makeServiceCall(PTLoginAPIInfo.GATEWAY, PTServiceHandler.POST, pairs);

                    Log.i("Response1: ", "> " + jsonStr);
                    if (jsonStr != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(jsonStr);

                            // Getting JSON Array node
                            JSONObject contents = jsonObj.getJSONObject(TAG_CONTACTS);
                            if (jsonStr.contains(TAG_ERROR)) {
                                Log.i("success", "Faile");
                                String error = contents.getString(TAG_ERROR);
                                Utils.showAlertDialog(PTSignUpActivity.this, error, true, null);
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

                                Intent i = new Intent(PTSignUpActivity.this, PTMainActivity.class);
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
        mBTLogin = (LoginButton) findViewById(R.id.login_button);
        mBTLogin.setReadPermissions(Arrays.asList("public_profile, email, user_friends"));
        mBTLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i("asa", "aaaa");
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
                                    registerIdFacebook(object.getString("id"), object.getString("email"));
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }



        return super.onOptionsItemSelected(item);
    }

    private boolean checkInput(){
        String _users = mETUser.getText().toString();
        String _email = mETEmail.getText().toString();
        String _pass = mETPass.getText().toString();
        String _confirm_pass = mETConfirmPass.getText().toString();
        if(_users.matches("")){
            Utils.showAlertDialog(this, "Please enter your UserName.", true, null);
            return false;
        }else if(_email.matches("")){
            Utils.showAlertDialog(this, "Please enter your Email.", true, null);
            return false;
        }else if(_pass.matches("")){
            Utils.showAlertDialog(this, "Please enter your Password.", true, null);
            return false;
        }else if(_confirm_pass.matches("")){
            Utils.showAlertDialog(this, "Please enter your Confirm password.", true, null);
            return false;
        }else if(!mCBTerms.isChecked()){
            Utils.showAlertDialog(this, "Please checked Terms and Conditions.", true, null);
            return false;
        }

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = mETEmail.getText().toString();

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (!matcher.matches()) {
            Utils.showAlertDialog(this, "Your email address is invalid.", true, null);
            return false;
        }
        if(!mETPass.getText().toString().equals(mETConfirmPass.getText().toString())){
            Utils.showAlertDialog(this, "Confirm password and Password do not match.", true, null);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && requestCode==REQUEST_CODE){
            mCBTerms.setChecked(true);
        }else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void registerIdFacebook(String id,String email) {
        Log.i("email va id",email+"|"+id);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        NameValuePair pair = new BasicNameValuePair("app", "Phantasm");
        NameValuePair pair1 = new BasicNameValuePair("service", "User");
        NameValuePair pair2 = new BasicNameValuePair("action", "signUpByFacebook");
        NameValuePair pair3 = new BasicNameValuePair("json", "true");
        NameValuePair pair4 = new BasicNameValuePair("json_arguments", "[\"" + email + "\",\"\","+id+",4]");
        pairs.add(pair);
        pairs.add(pair1);
        pairs.add(pair2);
        pairs.add(pair3);
        pairs.add(pair4);
        PTServiceHandler sh = new PTServiceHandler();
        String jsonStr = sh.makeServiceCall(PTLoginAPIInfo.GATEWAY, PTServiceHandler.POST, pairs);

                        Log.i("Response1: ", "> " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONObject contents = jsonObj.getJSONObject(TAG_CONTACTS);
                if (jsonStr.contains("error")) {
                    Log.i("success","Faile");
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

                    Intent i = new Intent(PTSignUpActivity.this, PTMainActivity.class);
                    i.putExtra("json", jsonStr);
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
