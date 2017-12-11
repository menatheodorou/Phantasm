package com.phantasm.phantasm.main.model;

import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.R;

/**
 * Created by Joseph Luns on 2016/2/3.
 */
public class PTGiphyChannel extends PTChannel {
    public static final int CHANNEL_ID = PTSpotifyChannel.CHANNEL_ID - 1;

    private static PTGiphyChannel instance;

    public static PTGiphyChannel getInstance() {
        if (instance == null) {
            instance = new PTGiphyChannel();
        }

        return instance;
    }

    private PTGiphyChannel() {
        id = CHANNEL_ID;
        name = PTApp.getInstance().getString(R.string.giphy);
        avatar = "uploads/avatar_giphy.jpg";
    }
}
