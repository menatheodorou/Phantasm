package com.gpit.android.webapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.gpit.android.library.R;
import com.gpit.android.logger.RemoteLogger;
import com.gpit.android.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseJSONWebAPI<T> {
	protected Context mContext;
	protected String mAPIFullPath;
	protected String mAPIPath;
	
	private Object mTag;

	protected RequestParams mParams;
	private boolean isMethodGet = true;
	protected boolean mShowProgress = true;
	protected boolean mCanceled = false;
	protected OnAPICompletedListener<T> mListener;
	
	protected CoreRestClient mRestClient;
	
	private String mDialogTitle;
	private boolean mDialogCancelable = true;
	private ProgressDialog mProgressDialog;
	
	protected JSONObject mResponseObj;
	protected JSONArray mResponseArray;

    protected int mErrorCode;
	protected String mErrorMessage = "";
	
	protected abstract String getAbsoluteUrl(String path);
	protected abstract RequestParams createReqParams(RequestParams params);

	public BaseJSONWebAPI(Context context, String apiPath) {
		mContext = context;
		setAPIPath(apiPath);

		mRestClient = new CoreRestClient(mContext, true);
		
		mDialogTitle = context.getResources().getString(R.string.waiting);
		mListener = (OnAPICompletedListener<T>) mDefaultListener;
	}

	public void setAPIPath(String path) {
		mAPIPath = path;
	}

	public void setListener(OnAPICompletedListener<T> listener) {
		mListener = listener;
	}
	
	public boolean isRequestMethodGet() {
		return isMethodGet;
	}

	public void setRequestMethod(boolean isGet) {
		isMethodGet = isGet;
	}

	public void setBasicAuth(String username, String password) {
		if (mRestClient != null) {
			mRestClient.setBasicAuth(username, password);
		}
	}
	
	public void addHeader(String header, String value) {
		if (mRestClient != null) {
			mRestClient.addHeader(header, value);
		}
	}
	
	public void setTag(Object tag) {
		mTag = tag;
	}
	
	public Object getTag() {
		return mTag;
	}
	
	public void showProgress(boolean enabled) {
		showProgress(enabled, mDialogTitle, mDialogCancelable);
	}
	
	public void showProgress(boolean enabled, String title, boolean cancelable) {
		mShowProgress = enabled;
		mDialogTitle = title;
		mDialogCancelable = cancelable;
	}
	
	public void exec() {
		exec(true, null);
	}
	
	public void exec(OnAPICompletedListener<T> listener) {
        if (listener instanceof OnCommonAPICompleteListener) {
            ((OnCommonAPICompleteListener)listener).setContext(mContext);
        }

		exec(true, listener);
	}

	public RequestHandle exec(boolean asyncCall, OnAPICompletedListener<T> listener) {
		setListener(listener);

		if (mShowProgress) {
			mProgressDialog = Utils.openNewDialog(mContext, mDialogTitle, mDialogCancelable, false, null);
			if (mDialogCancelable) {
				mProgressDialog.setOnCancelListener(mDialogCancelListener);
			}
		}

		// Create the request params
		mParams = new RequestParams();
		RequestParams params = createReqParams(mParams);

		// Call
		return call(params, asyncCall);
	}

	public synchronized void cancel() {
		mCanceled = true;
		
		if (mRestClient != null) {
			mRestClient.cancel();
		}
		
		if (mListener != null) {
			if (!isObserverAlive(mListener)) {
	    		return;
	    	}
			
			if (isActivityAttached()) {
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							mListener.onCanceled((T) BaseJSONWebAPI.this);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}});
			} else {
				try {
					mListener.onCanceled((T) BaseJSONWebAPI.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isActivityAttached() {
		return (mContext instanceof Activity);
	}
	
	public boolean isObserverAlive(Object observer) {
		if (observer == null)
			return false;

        if (mContext instanceof Activity && ((Activity)mContext).isDestroyed()) {
            return false;
		}

		if ((observer instanceof Fragment) && !((Fragment)observer).isAdded()) {
			return false;
		}
		
		if ((observer instanceof Activity) && !((Fragment)observer).isAdded()) {
			return false;
		}
		
		return true;
	}

	public JSONObject getResponseObject() {
		return mResponseObj;
	}

	public JSONArray getResponseObjectArray() {
		return mResponseArray;
	}

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        mErrorCode = errorCode;
    }

    public boolean isSuccess() {
        return (mErrorCode == 0);
    }

	public String getErrorMsg() {
		return mErrorMessage;
	}

	public void setErrorMsg(String errorMsg) {
		mErrorMessage = errorMsg;
	}

	/******************************* Native Call *****************************/
	private RequestHandle call(RequestParams params, boolean asyncCall) {
        mAPIFullPath = getAbsoluteUrl(mAPIPath);

		synchronized (BaseJSONWebAPI.this) {
			mCanceled = false;
		}
		
		if (isRequestMethodGet()) {
			return mRestClient.get(mAPIFullPath, params, asyncCall, responseHandler);
		} else {
			return mRestClient.post(mAPIFullPath, params, asyncCall, responseHandler);
		}
	}
	
	private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
		/**
	     * Returns when request succeeds
	     *
	     * @param statusCode http response status line
	     * @param headers    response headers if any
	     * @param response   parsed response if any
	     */
	    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
	    	if (!isObserverAlive(mListener)) {
	    		return;
	    	}
	    	
	    	mResponseObj = response;
			
			try {
				synchronized (BaseJSONWebAPI.this) {
					if (!mCanceled) {
						if (handleResponse(response)) {
							onCompleted();
						} else {
							onFailed();
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				
				onFailed();
			}

			if (mShowProgress) {
				dismissDialog();
			}
	    }

	    /**
	     * Returns when request succeeds
	     *
	     * @param statusCode http response status line
	     * @param headers    response headers if any
	     * @param response   parsed response if any
	     */
	    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
	    	if (!isObserverAlive(mListener)) {
	    		return;
	    	}
	    	
	    	mResponseArray = response;
	    	
	    	try {
				if (handleResponse(response)) {
					onCompleted();
				} else {
					onFailed();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				
				onFailed();
			}

            if (mShowProgress && isActivityAttached()) {
                dismissDialog();
            }
	    }

		/**
		 * Returns when request failed
		 *
		 * @param statusCode    http response status line
		 * @param headers       response headers if any
		 * @param throwable     throwable describing the way request failed
		 */
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			if (!isObserverAlive(mListener)) {
				return;
			}

			if (throwable != null) mErrorMessage = throwable.getMessage();

			try {
				handleError(responseString);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			onFailed();

			if (mShowProgress && isActivityAttached()) {
				dismissDialog();
			}
		}


		/**
	     * Returns when request failed
	     *
	     * @param statusCode    http response status line
	     * @param headers       response headers if any
	     * @param throwable     throwable describing the way request failed
	     * @param errorResponse parsed response if any
	     */
	    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	    	if (!isObserverAlive(mListener)) {
	    		return;
	    	}
	    	
	    	mResponseObj = errorResponse;
			if (throwable != null) mErrorMessage = throwable.getMessage();

			try {
				handleError(mResponseObj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			onFailed();

            if (mShowProgress && isActivityAttached()) {
                dismissDialog();
            }
	    }

	    /**
	     * Returns when request failed
	     *
	     * @param statusCode    http response status line
	     * @param headers       response headers if any
	     * @param throwable     throwable describing the way request failed
	     * @param errorResponse parsed response if any
	     */
	    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
	    	if (!isObserverAlive(mListener)) {
	    		return;
	    	}
	    	
	    	mResponseArray = errorResponse;
            if (throwable != null) mErrorMessage = throwable.getMessage();
			
	    	try {
				handleError(mResponseArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	
	    	onFailed();

            if (mShowProgress && isActivityAttached()) {
                dismissDialog();
            }
	    }
    };
	

	protected boolean handleResponse(JSONArray response) throws JSONException {
        return false;
	}

    protected boolean handleResponse(JSONObject response) throws JSONException {
        return false;
    }

	public void handleError(String response) throws JSONException {

	}

	public void handleError(JSONObject response) throws JSONException {

	}
	
	public void handleError(JSONArray response) throws JSONException {
	}
	
    protected void handleError() throws JSONException {
    	handleError(mResponseObj);
    }
    
	@SuppressWarnings("unchecked")
	private void onCompleted() {
		if (mResponseObj != null) {
			RemoteLogger.d(getClass().getSimpleName(), mResponseObj.toString());
		} else if (mResponseArray != null) {
			RemoteLogger.d(getClass().getSimpleName(), mResponseArray.toString());
		} else {
			RemoteLogger.d(getClass().getSimpleName(), "Api calling completed with no response");
		}
		
    	if (!isObserverAlive(mListener)) {
    		return;
    	}
    	
    	if (isActivityAttached()) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						mListener.onCompleted((T) BaseJSONWebAPI.this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			try {
				mListener.onCompleted((T) BaseJSONWebAPI.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
	@SuppressWarnings("unchecked")
	private void onFailed() {
		if (mResponseObj != null) {
			RemoteLogger.d(getClass().getSimpleName(), mResponseObj.toString());
		} else if (mResponseArray != null) {
			RemoteLogger.d(getClass().getSimpleName(), mResponseArray.toString());
		} else if (!TextUtils.isEmpty(mErrorMessage)) {
			RemoteLogger.d(getClass().getSimpleName(), mErrorMessage);
		} else {
			RemoteLogger.d(getClass().getSimpleName(), "Api calling failed with no response");
		}
		
		if (!isObserverAlive(mListener)) {
    		return;
    	}
		
		if (isActivityAttached()) {
			((Activity) mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						mListener.onFailed((T) BaseJSONWebAPI.this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			try {
				mListener.onFailed((T) BaseJSONWebAPI.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
	
	private void dismissDialog() {
		try {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		} catch (Exception e) {}
	}
	
	/******************************* Listener ********************************/
	private OnAPICompletedListener<BaseJSONWebAPI<T>> mDefaultListener = new OnAPICompletedListener<BaseJSONWebAPI<T>>() {
        @Override
		public void onCompleted(BaseJSONWebAPI<T> api) {
		}

		@Override
		public void onFailed(BaseJSONWebAPI<T> api) {
		}

		@Override
		public void onCanceled(BaseJSONWebAPI<T> api) {
			
		}
	};
	
	private OnCancelListener mDialogCancelListener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			// Cancel network transfer
			cancel();
		}
	};
}
