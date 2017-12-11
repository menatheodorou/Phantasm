package com.phantasm.phantasm.main.api.giphy;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import org.json.JSONObject;

/**
 * Created by ABC on 2016/1/13.
 */
public class ResponseListener_deprecated implements Response.Listener<JSONObject>, Response.ErrorListener {
    private final static String TAG = ResponseListener_deprecated.class.getSimpleName();

    private RequestQueueWrapper_deprecated mWrapper;
    private String mSearchquery;

    public ResponseListener_deprecated(RequestQueueWrapper_deprecated wrapper, String searchquery) {
        mWrapper = wrapper;
        mSearchquery = searchquery;
    }

    @Override
    public void onResponse(JSONObject responseObject) {
        mWrapper.onResult(mSearchquery, responseObject);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        VolleyLog.d(TAG, "Error: " + error.getMessage());

        mWrapper.onError(mSearchquery, error);

    }
}