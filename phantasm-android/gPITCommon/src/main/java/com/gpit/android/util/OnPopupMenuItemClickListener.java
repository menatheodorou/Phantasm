package com.gpit.android.util;

public interface OnPopupMenuItemClickListener {
	@Deprecated
	public void onPopupMenuItemClicked(int position, String title);
	public void onPopupMenuItemClicked(int position, String title, Object tag);
}
