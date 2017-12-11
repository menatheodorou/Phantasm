package com.gpit.android.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public abstract class BaseSplashActivity extends BaseActivity {
    public abstract int getLayoutId();
    public abstract int getSplashTimeSeconds();
    public abstract Class<? extends Activity> getNextActivity();

    protected MyCount mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
    }

    @Override
    protected void initUI() {
        mCounter = new MyCount(getSplashTimeSeconds() * 1000, getSplashTimeSeconds() * 1000);
        mCounter.start();
    }

    @Override
    public boolean supportOffline() {
        return true;
    }

    public final class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (isDestroyed()) return;

            Intent intent = new Intent(BaseSplashActivity.this, getNextActivity());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityWithoutAnimation(intent);

            finishWithoutAnimation();
        }
    }
}
