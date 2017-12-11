package com.gpit.android.util;


import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

public class UiUtils {
	public static void removeParentView(View view) {
		if ((view.getParent()) != null) {
			((ViewGroup) view.getParent()).removeView(view);
		}
	}

	public static boolean containView(ViewGroup parent, View childView) {
		for (int i = 0 ; i < parent.getChildCount() ; i++) {
			View subView = parent.getChildAt(i);
			if (subView == childView) return true;
		}

		return false;
	}

	public static void setVisibleAll(ViewGroup parent, int visibility) {
		setVisibleAll(parent, visibility, true);
	}

	public static void setVisibleAll(ViewGroup parent, int visibility, boolean includeOwn) {
		if (includeOwn) {
			parent.setVisibility(visibility);
		}
		for (int i = 0 ; i < parent.getChildCount() ; i++) {
			View subView = parent.getChildAt(i);
			subView.setVisibility(visibility);

			if (subView instanceof ViewGroup) {
				setVisibleAll((ViewGroup) subView, visibility, includeOwn);
			}
		}
	}

	public static boolean canScroll(View view, int direction) {
		if (view == null) return false;

		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (view instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) view;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView
						.getChildAt(0).getTop() < absListView.getPaddingTop());
			} else {
				return view.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(view, direction);
		}
	}
}
