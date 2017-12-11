package com.phantasm.phantasm.main.api.spotify.core;

/**
 * Created by ABC on 2015/12/11.
 */
public enum SpotifySearchType {
    SPOTIFY_SEARCH_TRACK (0),
    SPOTIFY_SEARCH_ALBUM (1),
    SPOTIFY_SEARCH_ARTIST (2);

    private int value;

    private SpotifySearchType(final int newValue) {
        value = newValue;
    }
}
