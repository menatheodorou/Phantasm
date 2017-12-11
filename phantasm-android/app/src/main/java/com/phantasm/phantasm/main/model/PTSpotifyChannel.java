package com.phantasm.phantasm.main.model;

import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.R;

/**
 * Created by Joseph Luns on 2016/2/3.
 */
public class PTSpotifyChannel extends PTChannel {
    public static final int CHANNEL_ID = PTVMAChannel.CHANNEL_ID - 1;

    private static PTSpotifyChannel instance;

    public static PTSpotifyChannel getInstance() {
        if (instance == null) {
            instance = new PTSpotifyChannel();
        }

        return instance;
    }

    private PTSpotifyChannel() {
        id = CHANNEL_ID;
        name = PTApp.getInstance().getString(R.string.spotify);
        avatar = "uploads/avatar_spotify.jpg";
    }
}
