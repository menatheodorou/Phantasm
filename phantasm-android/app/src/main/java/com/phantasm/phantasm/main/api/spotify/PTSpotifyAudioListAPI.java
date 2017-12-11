package com.phantasm.phantasm.main.api.spotify;

import android.content.Context;

import com.google.gson.Gson;
import com.gpit.android.webapi.OnAPICompletedListener;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaListAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.api.spotify.core.SpotifyTrackSearchAPI;
import com.phantasm.phantasm.main.api.spotify.model.SpotifySearchInfo;
import com.phantasm.phantasm.main.api.spotify.model.SpotifyTrackInfo;
import com.phantasm.phantasm.main.api.spotify.model.SpotifyTrackSearchResult;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTPagination;
import com.phantasm.phantasm.main.model.PTSpotifyChannel;

import junit.framework.Assert;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class PTSpotifyAudioListAPI extends PTBaseMediaListAPI {
    private SpotifyTrackSearchAPI mSearchAPI;
    private OnAPICompletedListener<PTWebAPI> mListener;

    public PTSpotifyAudioListAPI(Context context, String keyword, int offset, int limit) {
        super(context, PTSpotifyChannel.CHANNEL_ID, keyword, offset, limit);
        mKeyword = keyword;

        // TODO: We have to convert offset to page no in spotify api
        mSearchAPI = new SpotifyTrackSearchAPI(context, keyword, offset, limit);
    }

    @Override
    public void exec(OnAPICompletedListener<PTWebAPI> listener) {
        mListener = listener;

        mSearchAPI.exec(mSpotifyListener);
    }

    @Override
    protected PTBaseMediaResponse parseMediaResponse(Gson gson, String jsonResponse) {
        Assert.assertNull("Impossible callback");
        return null;
    }

    public void showProgress(boolean enabled) {
        mSearchAPI.showProgress(enabled);
    }

    private OnAPICompletedListener<PTWebAPI> mSpotifyListener = new OnAPICompletedListener<PTWebAPI>() {
        @Override
        public void onCompleted(PTWebAPI webapi) {
            // Convert track list to PTBaseMediaResponse objects
            SpotifyTrackSearchResult result = ((SpotifyTrackSearchAPI)webapi).searchResult;
            SpotifySearchInfo searchInfo = result.searchInfo;
            mediaResponse = new PTSpotifyMediaResponse();

            // Convert ic_search PTLoginAPIInfo to pagination
            PTPagination pagination = mediaResponse.getPagination();
            pagination.query = searchInfo.query;
            pagination.total_count = result.results.size();
            pagination.offset = searchInfo.offset;

            for (SpotifyTrackInfo trackInfo : result.results) {
                PTBaseMediaObject object = trackInfo.getAudioObject();
                mediaResponse.getMediaObjects().add(object);
            }

            if (mListener != null) mListener.onCompleted(PTSpotifyAudioListAPI.this);
        }

        @Override
        public void onFailed(PTWebAPI webapi) {
            mErrorCode = webapi.getErrorCode();
            mErrorMessage = webapi.getErrorMsg();

            if (mListener != null) mListener.onFailed(PTSpotifyAudioListAPI.this);
        }

        @Override
        public void onCanceled(PTWebAPI webapi) {
            if (mListener != null) mListener.onCanceled(PTSpotifyAudioListAPI.this);
        }
    };
}
