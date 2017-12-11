package com.gpit.android.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

public class BasePreference {
	protected Context mContext;
	protected SharedPreferences mPrefs;
    protected SharedPreferences.Editor mPrefsEditor;

    private HashMap<String, Object> mDefaultValues = new HashMap<String, Object>();

	protected BasePreference(Context context) {
		mContext = context;

        mPrefs = initPreference();
        mPrefsEditor = mPrefs.edit();

        mDefaultValues = getDefaultValues();
	}

	// Return individual preference handler. It needs to be implemented separately per preference manager
	private SharedPreferences initPreference() {
        SharedPreferences prefs = null;
        if (getPreferenceName() == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        } else {
            prefs = mContext.getSharedPreferences(getPreferenceName(),
                    Context.MODE_PRIVATE);

        }

		return prefs;
	}

    // Return default option values. It will be individual implemented in inherited classes
    protected HashMap<String, Object> getDefaultValues() {
        return mDefaultValues;
    }

    protected String getPreferenceName() {
        return null;
    }

    public boolean getBoolean(String key) {
        if (mDefaultValues.containsKey(key)) {
            return mPrefs.getBoolean(key, (Boolean) mDefaultValues.get(key));
        } else {
            return mPrefs.getBoolean(key, false);
        }
    }

    public String getString(String key) {
        if (mDefaultValues.containsKey(key)) {
            return mPrefs.getString(key, (String) mDefaultValues.get(key));
        } else {
            return mPrefs.getString(key, null);
        }
    }

    public int getInt(String key) {
        if (mDefaultValues.containsKey(key)) {
            return mPrefs.getInt(key, (Integer) mDefaultValues.get(key));
        } else {
            return mPrefs.getInt(key, 0);
        }
    }

    public long getLong(String key) {
        if (mDefaultValues.containsKey(key)) {
            return mPrefs.getLong(key, (Long) mDefaultValues.get(key));
        } else {
            return mPrefs.getLong(key, 0);
        }
    }

    public float getFloat(String key) {
        if (mDefaultValues.containsKey(key)) {
            return mPrefs.getFloat(key, (Float) mDefaultValues.get(key));
        } else {
            return mPrefs.getFloat(key, 0);
        }
    }
}
