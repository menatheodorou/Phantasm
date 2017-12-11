/*
 * Copyright (c) 2015 Abigail Esman. All rights reserved.
 * This file is part of Abigail Esman.
 *
 * @author Abigail Esman (abigail_esman@hotmail.com)
 */

package com.phantasm.phantasm.common.webapi;

import android.content.Context;

import com.gpit.android.logger.RemoteLogger;
import com.gpit.android.webapi.OnCommonAPICompleteListener;

public abstract class OnPTAPICompleteListener<T extends PTWebAPI> extends OnCommonAPICompleteListener<T> {
	public OnPTAPICompleteListener(Context context) {
		super(context);
	}

	public void onFailed(T webapi) {
		super.onFailed(webapi);

		if (webapi.getErrorCode() == PTWebAPIError.SKErrorConnectionFailed.getValue()) {

        }
	}

    @Override
    protected void showLogs(T baseWebAPI) {
        super.showLogs(baseWebAPI);

        RemoteLogger.d(getClass().getSimpleName(), "error (" + baseWebAPI.getErrorCode() + ")");
    }

    @Override
    protected void showToasts(T baseWebAPI) {
        /*
        super.showToasts(baseWebAPI);

        Toast.makeText(mActivity, String.valueOf(baseWebAPI.getErrorCode()), Toast.LENGTH_LONG).show();
        */
    }
}
