package com.phantasm.phantasm.main;

import android.support.annotation.CallSuper;

import com.phantasm.phantasm.common.ui.PTBaseFragment;

public abstract class PTPreloadedBaseFragment extends PTBaseFragment {
    public PTPreloadedBaseFragment() {
    }

    @Override
    public void onPause() {
        super.onPause();

        onVisible(false);
    }

    @Override
    public void onStop() {
        super.onStop();

        onVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint()) {
            onVisible(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        onVisible(isVisibleToUser);
    }

    @CallSuper
    protected void onVisible(boolean isVisibleToUser) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
