package com.gpit.android.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseFragment extends Fragment {
	protected static String TAG;

	protected ViewGroup mContentView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		TAG = getClass().getSimpleName();

		super.onCreate(savedInstanceState);
		
		initData(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getContentLayout() != 0) {
			mContentView = (ViewGroup) View.inflate(getActivity(), getContentLayout(), null);
			initUI();
		}
		
		return mContentView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public View findViewById(int id) {
		if (mContentView == null) {
			return null;
		}
		
		View view = mContentView.findViewById(id);
		
		return view;
	}

	public boolean onBackPressed() {
		FragmentManager manager = getChildFragmentManager();
		List<Fragment> fragments = manager.getFragments();

        if (fragments == null) return false;

		int fragmentCount = fragments.size();
		if (fragmentCount > 0) {
			for (int i = (fragmentCount - 1) ; i >= 0 ; i--) {
				Fragment fragment = fragments.get(i);
				if (fragment != null && fragment.isVisible() && fragment instanceof BaseFragment) {
					if (((BaseFragment) fragment).onBackPressed()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	protected abstract int getContentLayout();
	protected abstract void initData(Bundle savedInstanceState);
	protected abstract void initUI();
	protected abstract void reload(Bundle bundle);
}
