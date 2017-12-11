package com.gpit.android.setting;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class BaseSetting {
	protected Context mContext;
	
	// Preference
	protected SharedPreferences mPrefs;
	protected SharedPreferences.Editor mPrefsEditor;
	
	protected abstract SharedPreferences loadPreferences();
	
	public BaseSetting(Context context) {
		mContext = context;
		
		init();
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public SharedPreferences getPreferences() {
		return mPrefs;
	}
	
	public SharedPreferences.Editor getPreferencesEditor() {
		return mPrefsEditor;
	}
	
	private void init() {
		mPrefs = loadPreferences();
		if (mPrefs != null) {
			// Ensure to load components from mock-viewer.
			mPrefsEditor = mPrefs.edit();
		}
	}
}
