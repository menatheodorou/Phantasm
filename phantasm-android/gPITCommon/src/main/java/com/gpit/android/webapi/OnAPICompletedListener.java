package com.gpit.android.webapi;


public interface OnAPICompletedListener<T> {
	public void onCompleted(T webapi);
	public void onFailed(T webapi);
	public void onCanceled(T webapi);
}
