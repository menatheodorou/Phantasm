package com.phantasm.phantasm.common.ui;

import android.support.annotation.CallSuper;

import com.flurry.android.FlurryAgent;
import com.gpit.android.ui.common.BaseActivity;

/**
 * Created by ABC on 2016/1/4.
 */
public class PTBaseActivity extends BaseActivity {
    @Override
    protected void onStart() {
        super.onStart();

        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void initUI() {

    }

    @Override
    public boolean supportOffline() {
        return true;
    }

    @CallSuper
    @Override
    protected void onStop() {
        super.onStop();

        FlurryAgent.onEndSession(this);
    }
}
