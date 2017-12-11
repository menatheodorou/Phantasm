package com.phantasm.phantasm.welcome;

import android.app.Activity;

import com.gpit.android.ui.common.BaseSplashActivity;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.WifiReceiver;
import com.phantasm.phantasm.main.PTMainActivity;

/**
 * Created by kevinfinn on 5/20/15.
 * Modified by Joseph Luns on 1/12/16.
 */
public class PTSplashActivity extends BaseSplashActivity implements WifiReceiver.ConnectivityChangeListener {
    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public int getSplashTimeSeconds() {
        return 3;
    }

    @Override
    public Class<? extends Activity> getNextActivity() {
        return PTMainActivity.class;
    }

    @Override
    protected void onStart() {
        super.onStart();

        WifiReceiver.setConnectivityChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnectivityChange(boolean isConnected) {
        if (!isConnected) {
            mCounter.cancel();
            finish();
        }
    }
}
