package com.phantasm.phantasm.main.api.spotify.core;

import android.content.Context;

import com.gpit.android.webapi.OnCommonAPICompleteListener;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.api.spotify.model.SpotifyBaseJsonInfo;
import com.phantasm.phantasm.main.api.spotify.model.SpotifyBaseSearchResult;

import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by ABC on 2015/12/7.
 */
public abstract class SpotifyCoreSearchAPI<T extends SpotifyBaseSearchResult, V extends SpotifyBaseJsonInfo> extends PTWebAPI {
    // Optional. The maximum number of results to return. Default: 20. Minimum: 1. Maximum: 50.
    public final static int SPOTIFY_MAX_LIMIT = 50;
    public final static String SPOTIFY_SEARCH_API_PATH = "https://api.spotify.com/v1/search?type=%s&q=%s&offset=%d&limit=%d";

    private Class<T> mResultClz;
    private Class<V> mResultItemClz;

    private String mNativeQuery;
    protected String mHighLevelQuery;
    protected int mOffset;
    protected int mLimit;
    protected int mPerLimit;

    public T searchResult;

    public abstract SpotifyCoreSearchAPI newInstance(String query, int startPageNo, int pageCount);
    public abstract String getSearchType();
    public abstract String getNativeQuery(String highLevelQuery);

    public SpotifyCoreSearchAPI(Context context, Class<T> resultClz,
                                Class<V> resultItemClz, String query, int offset, int limit) {
        super(context, "");

        mResultClz = resultClz;
        mResultItemClz = resultItemClz;
        mOffset = offset;
        mLimit = limit;

        setQuery(query);
    }

    @Override
    protected String getAbsoluteUrl(String path) {
        mPerLimit = Math.min(SPOTIFY_MAX_LIMIT, mLimit);
        String url = String.format(Locale.getDefault(), SPOTIFY_SEARCH_API_PATH, getSearchType(),
                    mNativeQuery, mOffset, mPerLimit);

        return url;
    }

    public String getQuery() {
        return mHighLevelQuery;
    }

    public String getNativeQuery() {
        return mNativeQuery;
    }

    public void setQuery(String query) {
        mHighLevelQuery = query;
        setNativeQuery(getNativeQuery(mHighLevelQuery));
    }

    public void setNativeQuery(String query) {
        mNativeQuery = query;
    }

    @Override
    protected boolean parseResponse(JSONObject response) {
        try {
            searchResult = mResultClz.newInstance();
            searchResult.parse(mResultItemClz, response);

            int moreCount = mLimit - searchResult.getCount();
            if (moreCount > 0 && mPerLimit <= searchResult.getCount()) {
                // Load more
                int newOffset = mOffset + searchResult.getCount();

                final SpotifyCoreSearchAPI loadMoreAPI = newInstance(getQuery(), newOffset, moreCount);
                loadMoreAPI.showProgress(mShowProgress);
                loadMoreAPI.exec(true, new OnCommonAPICompleteListener<PTWebAPI>(mContext) {
                    @Override
                    public void onCompleted(PTWebAPI webapi) {
                        boolean noMoreResult = false;
                        if (loadMoreAPI.mPerLimit > loadMoreAPI.searchResult.getCount()) {
                            // There is no more result, so lets finalize it.
                            noMoreResult = true;
                        }

                        searchResult.searchInfo.limit += loadMoreAPI.searchResult.getCount();
                        searchResult.results.addAll(loadMoreAPI.searchResult.results);
                        if (noMoreResult || searchResult.results.size() >= searchResult.searchInfo.limit) {
                            if (mListener != null) mListener.onCompleted(SpotifyCoreSearchAPI.this);
                        }
                    }

                    public void onFailed(PTWebAPI webapi) {
                        super.onFailed(webapi);
                        if (mListener != null) mListener.onFailed(SpotifyCoreSearchAPI.this);
                    }

                    public void onCanceled(PTWebAPI webapi) {
                        super.onCanceled(webapi);
                        if (mListener != null) mListener.onCanceled(SpotifyCoreSearchAPI.this);
                    }
                });
            } else {
                mLimit = searchResult.getCount();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}
