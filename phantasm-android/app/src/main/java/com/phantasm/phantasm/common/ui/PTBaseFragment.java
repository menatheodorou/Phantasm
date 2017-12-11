package com.phantasm.phantasm.common.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpit.android.ui.common.BaseFragment;
import com.phantasm.phantasm.common.ui.ErrorDialog;

public abstract class PTBaseFragment extends BaseFragment {
    protected FragmentManager mFragmentManager;

    public PTBaseFragment() {
        TAG = this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentManager = getChildFragmentManager();
        if(mFragmentManager == null) {
            ErrorDialog.createDialog(getActivity(), "Error", "Phantasm has encountered a mortal error and must shut down").show();
        }

        View view = super.onCreateView(inflater, container, savedInstanceState);

        registerReceiver();

        return view;
    }

    public void onDestroyView() {
        unregisterReceiver();

        super.onDestroyView();
    }

    protected void registerReceiver() {

    }

    protected void unregisterReceiver() {

    }
}
