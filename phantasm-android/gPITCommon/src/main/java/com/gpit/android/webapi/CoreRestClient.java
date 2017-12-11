package com.gpit.android.webapi;

import android.content.Context;

import com.gpit.android.logger.RemoteLogger;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import junit.framework.Assert;

public class CoreRestClient {
	private final static int REST_CLIENT_MTIMEOUT = 60 * 1000;

	private Context mContext;
	private AsyncHttpClient mAsyncClient;
	private SyncHttpClient mSyncClient;

	public CoreRestClient(Context context, boolean sslMode) {
		mContext = context;

		if (sslMode) {
			mAsyncClient = new AsyncHttpClient(true, 80, 443);
			mSyncClient = new SyncHttpClient(true, 80, 443);
		} else {
			mAsyncClient = new AsyncHttpClient();
			mSyncClient = new SyncHttpClient();
		}
		// Initialize async client
		mAsyncClient.setTimeout(REST_CLIENT_MTIMEOUT);
		
		// Initialize sync client
		mSyncClient.setTimeout(REST_CLIENT_MTIMEOUT);
	}

	public RequestHandle get(String url, RequestParams params, boolean asyncCall,
			AsyncHttpResponseHandler responseHandler) {
		RemoteLogger.d(CoreRestClient.class.getSimpleName(), "Http Get: "
				+ AsyncHttpClient.getUrlWithQueryString(false, url, params));

		Assert.assertTrue(responseHandler != null);
		
		if (asyncCall) {
			return mAsyncClient.get(url, params, responseHandler);
		} else {
			return mSyncClient.get(url, params, responseHandler);
		}
	}

	public RequestHandle post(String url, RequestParams params, boolean asyncCall,
			AsyncHttpResponseHandler responseHandler) {
		RemoteLogger.d(CoreRestClient.class.getSimpleName(), "Http Post: "
				+ params.toString());

		Assert.assertTrue(responseHandler != null);
		
		if (asyncCall) {
			return mAsyncClient.post(url, params, responseHandler);
		} else {
			return mSyncClient.post(url, params, responseHandler);
		}
	}

	public void setBasicAuth(String username, String password) {
		mAsyncClient.setBasicAuth(username, password);
		mSyncClient.setBasicAuth(username, password);
	}
	
	public void addHeader(String header, String value) {
		mAsyncClient.addHeader(header, value);
		mSyncClient.addHeader(header, value);
	}

	public void cancel() {
		try {
			mAsyncClient.cancelRequests(mContext, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			mSyncClient.cancelRequests(mContext, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
