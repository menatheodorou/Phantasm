package com.phantasm.phantasm.main.api.spotify.core;

import android.content.Context;

import com.phantasm.phantasm.main.api.spotify.model.SpotifyTrackInfo;
import com.phantasm.phantasm.main.api.spotify.model.SpotifyTrackSearchResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by ABC on 2015/12/7.
 */
public class SpotifyTrackSearchAPI extends SpotifyCoreSearchAPI<SpotifyTrackSearchResult, SpotifyTrackInfo> {
    public SpotifyTrackSearchAPI(Context context, String query, int offset, int limit) {
        super(context, SpotifyTrackSearchResult.class, SpotifyTrackInfo.class, query, offset, limit);
    }

    @Override
    public SpotifyCoreSearchAPI newInstance(String query, int offset, int limit) {
        SpotifyTrackSearchAPI api = new SpotifyTrackSearchAPI(mContext, query, offset, limit);
        return api;
    }

    public String getSearchType() { return "track"; };

    public String getNativeQuery(String highLevelQuery) {
        try {
            highLevelQuery = URLEncoder.encode(highLevelQuery, "UTF-8");// + SEARCH_URL_POST;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String query = String.format(Locale.getDefault(), "track:%s+OR+artist:%s",
                highLevelQuery, highLevelQuery);

        return query;
    }
}
