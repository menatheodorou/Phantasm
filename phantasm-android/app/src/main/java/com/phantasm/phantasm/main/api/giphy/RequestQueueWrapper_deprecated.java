package com.phantasm.phantasm.main.api.giphy;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.phantasm.phantasm.common.ui.ErrorDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by kevinfinn on 6/15/15.
 */
abstract public class RequestQueueWrapper_deprecated {

    public Gson gson = new Gson();
    public String mtag = "";
    private static RequestQueue requestQueue;
    public FragmentActivity mactivity;

    public RequestQueueWrapper_deprecated(FragmentActivity activity, @NonNull String tag) {
        mtag = tag;
        mactivity = activity;
        requestQueue = Volley.newRequestQueue(mactivity);
    }

    protected void addToRequestQueue(Request<JSONObject> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? mtag : tag);
        requestQueue.add(req);
    }

    protected void addToRequestQueue(Request<JSONObject> req) {
        req.setTag(mtag);
        requestQueue.add(req);
    }

    protected void cancelPendingRequests(Object tag) {
        requestQueue.cancelAll(tag);
    }

    public void makeJsonRequest(final String searchquery, final int categoryId) {
        String url = null;
        try {
            url = buildSearchURL(searchquery, categoryId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            ErrorDialog.createWarningDialog(mactivity,e.getMessage());
        }

        ResponseListener_deprecated responseListener = new ResponseListener_deprecated(this, searchquery);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                null, responseListener, responseListener);

        // Adding request to request queue
        addToRequestQueue(req);
        Log.i(mtag, "request " + req.getUrl());


    }

    public abstract void onResult(final String searchquery, final JSONObject result);
    public abstract void onError(final String searchquery, final VolleyError error);

    protected abstract String buildSearchURL(final String query, final int categoryId) throws UnsupportedEncodingException;
}
