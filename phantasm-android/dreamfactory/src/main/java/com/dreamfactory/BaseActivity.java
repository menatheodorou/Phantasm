package com.dreamfactory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kevinfinn on 5/26/15.
 */
public class BaseActivity extends AppCompatActivity {
    protected String dsp_url;
    protected String session_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 11){
            try{
                ActionBar actionbar = getSupportActionBar();
            }catch(Exception e){

            }
        }
        dsp_url = PrefUtil.getString(getApplicationContext(), IAppConstants.DSP_URL);
        dsp_url += IAppConstants.DSP_URL_SUFIX;
        session_id = PrefUtil.getString(getApplicationContext(), IAppConstants.SESSION_ID);
    }

    protected void log(String message){
        System.out.println("log: " + message);
    }

    protected void logout(){
        PrefUtil.putString(getApplicationContext(), IAppConstants.SESSION_ID, "");
        PrefUtil.putString(getApplicationContext(), IAppConstants.EMAIL, "");
        PrefUtil.putString(getApplicationContext(), IAppConstants.PWD, "");
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }
}
