package com.phantasm.phantasm.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class PTPreference {
    // prefname
    private final String PREF_NAME = "PHANTASM_PREF";
    private static PTPreference instance = null;
    private Context context;
    private SharedPreferences app_prefs;
    private Bitmap m_pictureBitmap;

    private PTPreference() {
    }


    public static synchronized PTPreference getInstance(Context context) {
        if(instance == null) {
            instance = new PTPreference();
        }
        instance.setContext(context);

        return instance;
    }

    private void setContext(Context context) {
        this.context= context;
        app_prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putIsSignin(boolean isSignin) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean(PTConst.Params.IS_SIGNIN, isSignin);
        edit.commit();
    }

    public boolean getIsSignin() {
        return app_prefs.getBoolean(PTConst.Params.IS_SIGNIN, false);
    }

    public void putUserId(int userId) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putInt(PTConst.Params.USER_ID, userId);
        edit.commit();
    }

    public int getUserId() {
        return app_prefs.getInt(PTConst.Params.USER_ID, -1);
    }

    public void putPhoneNumber(String phoneNumber) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PTConst.Params.PHONE_NUMBER, phoneNumber);
        edit.commit();
    }

    public String getPhoneNumber() {
        return app_prefs.getString(PTConst.Params.PHONE_NUMBER, null);
    }

    public void putUserName(String name) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PTConst.Params.USER_NAME, name);
        edit.commit();
    }

    public String getUserName() {
        return app_prefs.getString(PTConst.Params.USER_NAME, null);
    }

    public void putEmail(String email) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PTConst.Params.EMAIL, email);
        edit.commit();
    }

    public String getEmail() {
        return app_prefs.getString(PTConst.Params.EMAIL, null);
    }

    public void putPhoto(String photoUrl) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PTConst.Params.PHOTO, photoUrl);
        edit.commit();
    }

    public String getPhoto() {
        return app_prefs.getString(PTConst.Params.PHOTO, null);
    }
}
