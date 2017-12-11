package com.gpit.android.ui.common;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.gpit.android.app.BaseApp;
import com.gpit.android.app.BaseConst;
import com.gpit.android.library.R;
import com.gpit.android.logger.RemoteLogger;
import com.gpit.android.util.NetworkUtils;
import com.gpit.android.util.SysUtils;
import com.gpit.android.util.SystemUtils;
import com.gpit.android.util.TimeUtils;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
	protected static String TAG;

    private Resources resources;

	private ViewGroup mRootView;
	protected boolean mLiveOn = false;
	protected boolean mVisible = false;
	
	@Override
	protected void onCreate(Bundle bundle) {
		mLiveOn = true;
		
		TAG = getClass().getSimpleName();
		
		checkAvailability();
		
		super.onCreate(bundle);
		
		registerReceiver();
	}
	
	@Override 
	protected void onNewIntent(Intent intent) { 
		super.onNewIntent(intent); 
		
		setIntent(intent);
		checkAvailability();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mVisible = false;
		if (((BaseApp)getApplication()).isScreenVisible(getClass().getCanonicalName())) {
			((BaseApp)getApplication()).setCurrentVisibleActivityName(null);
		}
	}
	
	@Override
	protected void onResume() {
		checkAvailability();
		
		super.onResume();
		
		mVisible = true;
		((BaseApp)getApplication()).setCurrentVisibleActivityName(getClass().getCanonicalName());
	}
	
	@Override
	protected void onStart() {
		checkAvailability();
		
	    super.onStart();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
	}
	
	public void finishWithoutAnimation() {
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		mLiveOn = false;

		unregisterReceiver();

		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	public void setContentView(int layoutResId) {
		View contentView = View.inflate(this, layoutResId, (ViewGroup) null);

		setContentView(contentView);
	}
	
	public void setContentView(View contentView) {
		setContentView(contentView, null);
	}
	
	public void setContentView(View contentView, LayoutParams params) {
		View rootView = wrapContentView(contentView);
		
		if (params != null) {
			super.setContentView(rootView, params);
		} else {
			super.setContentView(rootView);
		}

		_initUI();
	}
	
	@Override
	public View findViewById(int resId) {
		if (mRootView == null)
			return null;

        return mRootView.findViewById(resId);
	}
	
	public boolean isAlive() {
		return mLiveOn;
	}
	
	private boolean checkAvailability() {
		// Check application life-cycle. If application instances are released, we have to restart the application again.
		if (shouldBeAppAlive() && getApplication() == null) {
			Log.e(TAG, "There is no singleton instance at the memory");
			restartApplication();
			
			return false;
		}
		
		if (!supportOffline()) {
			if (!NetworkUtils.isNetworkAvailable(this)) {
				Toast.makeText(this, getString(R.string.needs_online), Toast.LENGTH_LONG).show();
				finish();
				
				return false;
			}
		}
		
		// Check page security
		if (shouldBePassedLogin()) {
            return false;
		}
			
		
		return true;
	}
	
	public boolean shouldBeAppAlive() {
		return true;
	}
	
	private void restartApplication() {
		RemoteLogger.e(TAG, "Restart application due to background application's resource is released.");
		Intent intent;
		
		intent = new Intent(this, ((BaseApp)getApplication()).getMainActivityClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
		
		System.exit(0);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	}
	
	public void startActivityWithoutAnimation(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(0, 0);
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		intent.putExtra("requestCode", requestCode);
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	}
	
	@Override
	public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();

		if (fragments != null) {
			int fragmentCount = fragments.size();
			if (fragmentCount > 0) {
				for (int i = (fragmentCount - 1); i >= 0; i--) {
					Fragment fragment = fragments.get(i);
					if (fragment != null && fragment instanceof BaseFragment) {
						if (((BaseFragment) fragment).onBackPressed()) {
							return;
						}
					}
				}
			}
		}

		super.onBackPressed();
		overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
	}
	
	public boolean shouldBePassedLogin() {
		return true;
	}

	/************************* Broadcast Receiver **************************/
	private void registerReceiver() {
	}

	private void unregisterReceiver() {
	}

	/************************* Initialize Layout ***********************/
	// Wrap content view in common layout which is included debug version and number 
	private View wrapContentView(View contentView) {
		if (BaseConst.DEBUG_MODE_ON) {
			mRootView = (ViewGroup) View.inflate(this, R.layout.activity_common, (ViewGroup) null);
			ViewGroup containerView = (ViewGroup) mRootView.findViewById(R.id.flActivityContainer);
			containerView.addView(contentView);
		} else {
			mRootView = (ViewGroup) contentView;
		}
		
		return mRootView;
	}
	
	protected void _initUI() {
		if (BaseConst.DEBUG_MODE_ON) {
			// Show application version name & code
			String version = SystemUtils.getVersion(this);
			((TextView)findViewById(R.id.tvVersion)).setText(version);

			String buildTime = TimeUtils.getDateString(SysUtils.getCodeBuildTime(this), "MM/dd/yy hh:mm a");
			((TextView) findViewById(R.id.tvDate)).setText(buildTime);
		}
		
		initUI();
	}
	
	/************************* Abstract Interface ***********************/
	protected abstract void initUI();
	public abstract boolean supportOffline();
}
