package com.phantasm.phantasm.main;

import android.support.annotation.CallSuper;

import com.phantasm.phantasm.common.PTSettings;
import com.phantasm.phantasm.main.model.PTTabID;

public abstract class PTTabBaseFragment extends PTPreloadedBaseFragment {
    protected PTTabID mTabId;

    public PTTabBaseFragment(PTTabID tabId) {
        mTabId = tabId;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        onVisible(isVisibleToUser);
    }

    @CallSuper
    protected void onVisible(boolean isVisibleToUser) {
        super.onVisible(isVisibleToUser);

        if (isVisibleToUser) {
            PTSettings.getInstance(getActivity()).setLatestTab(mTabId);
        } else {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
