package com.phantasm.phantasm.main.api.giphy;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.phantasm.phantasm.common.ui.ErrorDialog;
import com.phantasm.phantasm.main.api.giphy.model.PTImagesResponse;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by kevinfinn on 6/15/15.
 */
public class ImageRequestQueueWrapper_deprecated extends RequestQueueWrapper_deprecated {
    public static final int MAX_SEARCH_RESULTS = 150;
    public static final String imageJsonURL = "http://api.giphy.com/v1/gifs/trending?api_key=dc6zaTOxFJmzC&limit=100";
    final static String SEARCH_URL_PRE = "http://api.giphy.com/v1/gifs/search?q=";
    final static String SEARCH_URL_POST = "&api_key=dc6zaTOxFJmzC";

    int imageCount;

//    PTImagesResponse.Pagination pagination;
    OnResultListener mresultListener;
    DialogInterface.OnClickListener mforceExitListener;

    public interface OnResultListener{
        void onResult(PTImagesResponse response);
        void onPaginationResult(PTImagesResponse response, String query, int remaining, int offset);
        void onNewPaginationResult(PTImagesResponse response);
    }

    public ImageRequestQueueWrapper_deprecated(FragmentActivity activity, @NonNull String tag, OnResultListener resultListener, DialogInterface.OnClickListener forceExitListener) {
        super(activity,tag);
        mresultListener = resultListener;
        mforceExitListener = forceExitListener;
    }

    @Override
    public void onResult(String searchquery, JSONObject responseObject) {
        String jsonResponse = responseObject.toString();
        PTImagesResponse response = gson.fromJson(jsonResponse, PTImagesResponse.class);
        if (searchquery != null && response.pagination != null) { //new query or not a query
            if(response.pagination.count == 0)
                return;
            //TODO new CustomDialog.showVolleyError("Search returned 0 results");

            response.query = searchquery;

            if(mresultListener!=null){
                mresultListener.onNewPaginationResult(response);
            }

            if (response.pagination.total_count < MAX_SEARCH_RESULTS) { //low ic_search result #
                imageCount = response.pagination.total_count;
            } else { //MORE ic_search results than our MAX
                imageCount = MAX_SEARCH_RESULTS; //limit # of results to MAX_SEARCH_RESULTS
            }
            makeJsonPagingQuery(response.query, imageCount, response.pagination.count);
        } else { //last page or not a ic_search
            if(mresultListener!=null) {
                mresultListener.onResult(response);
            }
        }
    }

    @Override
    public void onError(final String searchquery, final VolleyError error) {
        if(mactivity!=null)
            new ErrorDialog().createVolleyError(mactivity, error, new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //retry query
//                    makeJsonRequest(searchquery);
                    mactivity.finish();
                }
            });
    }

    private String buildPaginationURL(final String query, final int offset) {
        try {
            String url = SEARCH_URL_PRE + URLEncoder.encode(query, "UTF-8") + "&offset=" + offset + SEARCH_URL_POST;
            Log.i(mtag, "pagination url = " + url);
            return url;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return imageJsonURL;
        }
    }

    /**
     * Method to make json array request for next pages
     */
    public void makeJsonPagingQuery(@NonNull final String query, final int remaining, final int offset) {
        final Gson gson = new Gson();
        final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObject) {
                String jsonResponse = responseObject.toString();
                if(mresultListener!=null){
                    PTImagesResponse response = gson.fromJson(jsonResponse, PTImagesResponse.class);
                    mresultListener.onPaginationResult(response, query, remaining - response.data.size(), offset);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(mactivity!=null)
                    new ErrorDialog().createVolleyError(mactivity, error, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //retry query
//                            makeJsonPagingQuery(query,remaining,offset);
                            mactivity.finish();
                        }
                    });
            }
        };
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, buildPaginationURL(query, offset),
                null, responseListener, errorListener);

        // Adding request to request queue
        addToRequestQueue(req);
    }


    public String buildSearchURL(final String query, int param1) throws UnsupportedEncodingException{
        String url = imageJsonURL;

        if (query != null)
                url = SEARCH_URL_PRE + URLEncoder.encode(query, "UTF-8") + SEARCH_URL_POST;

        return url;
    }
}
