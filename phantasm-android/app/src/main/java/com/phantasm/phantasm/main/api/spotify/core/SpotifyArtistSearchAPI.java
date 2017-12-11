package com.phantasm.phantasm.main.api.spotify.core;

import android.content.Context;

import com.phantasm.phantasm.main.api.spotify.model.SpotifyArtistInfo;
import com.phantasm.phantasm.main.api.spotify.model.SpotifyArtistSearchResult;

import java.util.Locale;

/**
 * Created by ABC on 2015/12/7.
 */
// TODO - Not completed yet
public class SpotifyArtistSearchAPI extends SpotifyCoreSearchAPI<SpotifyArtistSearchResult, SpotifyArtistInfo> {
    public SpotifyArtistSearchAPI(Context context, String query, int offset, int limit) {
        super(context, SpotifyArtistSearchResult.class, SpotifyArtistInfo.class, query, offset, limit);
    }

    @Override
    public SpotifyCoreSearchAPI newInstance(String query, int offset, int limit) {
        SpotifyArtistSearchAPI api = new SpotifyArtistSearchAPI(mContext, query, offset, limit);
        return api;
    }

    public String getSearchType() { return "artist"; };

    public String getNativeQuery(String highLevelQuery) {
        String query = String.format(Locale.getDefault(), "artist:%s",
                highLevelQuery, highLevelQuery);
        return query;
    }
}
