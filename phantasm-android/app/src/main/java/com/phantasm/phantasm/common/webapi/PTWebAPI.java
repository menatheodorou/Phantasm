/*
 * Copyright (c) 2015 Abigail Esman. All rights reserved.
 * This file is part of Abigail Esman.
 *
 * @author Abigail Esman (abigail_esman@hotmail.com)
 */


package com.phantasm.phantasm.common.webapi;

import android.content.Context;

import com.gpit.android.util.JsonParserUtils;
import com.gpit.android.webapi.BaseJSONWebAPI;
import com.loopj.android.http.RequestParams;
import com.phantasm.phantasm.common.PTConst;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class PTWebAPI extends BaseJSONWebAPI<PTWebAPI> {
	protected JSONObject mResponseData;
	protected JSONArray mResponseList;

    public PTWebAPI(Context context, String apiPath) {
        super(context, apiPath);
    }

	@Override
	protected String getAbsoluteUrl(String path) {
        String url = PTConst.WEB_SERVICE_URL + path;

        return url;
	}
	
	public JSONObject getResponseData() {
		return mResponseData;
	}

	public JSONArray getResponseList() {
		return mResponseList;
	}


	@Override
	protected RequestParams createReqParams(RequestParams params) {
		return params;
	}
	
	/******************************* Native Call *****************************/
	@Override
	protected boolean handleResponse(JSONArray response) throws JSONException {
		return isSuccess();
	}

	@Override
	protected boolean handleResponse(JSONObject response) throws JSONException {
		mResponseData = response;
		mResponseList = JsonParserUtils.getJSONArrayValue(mResponseData, "record");
		// mErrorCode = JsonParserUtils.getIntValue(mResponseDataObj, "error");
		// mErrMessage = JsonParserUtils.getIntValue(mResponseDataObj, "error");

		if (isSuccess()) {
			return parseResponse(response);
		} else {
			return false;
		}
	}

	protected abstract boolean parseResponse(JSONObject response);
}
