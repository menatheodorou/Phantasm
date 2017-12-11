package com.gpit.android.webapi;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.gpit.android.library.R;
import com.gpit.android.logger.RemoteLogger;
import com.gpit.android.util.StringUtils;

public abstract class OnCommonAPICompleteListener<T extends BaseJSONWebAPI> implements OnAPICompletedListener<T> {
	protected Context mContext;
	protected FragmentActivity mActivity;
	public OnCommonAPICompleteListener(Context context) {
		setContext(context);
	}

    public void setContext(Context context) {
        mContext = context;
        if (mContext instanceof FragmentActivity) {
            mActivity = (FragmentActivity) mContext;
        }
    }
	
	public void onFailed(T webapi) {
        try {
            showLogs(webapi);
            // showToasts(webapi);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public void onCanceled(T webapi) {
		RemoteLogger.d(getClass().getSimpleName(), "API calling canceled.");
	}

	protected void showLogs(T baseWebAPI) {
		if (!StringUtils.isNullOrEmpty(baseWebAPI.getErrorMsg())) {
			RemoteLogger.d(getClass().getSimpleName(), "API calling failed: " + baseWebAPI.getErrorMsg());
		} else {
			RemoteLogger.d(getClass().getSimpleName(), "API calling failed: " +
					mContext.getResources().getString(R.string.disconnected_server_communication));
		}
	}
	
	protected void showToasts(T baseWebAPI) {
		if (mActivity != null) {
			if (!StringUtils.isNullOrEmpty(baseWebAPI.getErrorMsg())) {
				Toast.makeText(mActivity, baseWebAPI.getErrorMsg(), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(mActivity, mActivity.getResources().getString(R.string.disconnected_server_communication), Toast.LENGTH_LONG).show();
			}
		}
	}
}
