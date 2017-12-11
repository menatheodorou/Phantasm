package com.phantasm.phantasm.main.api.spotify.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ABC on 2015/12/11.
 */
public abstract class SpotifyBaseJsonInfo {
    public SpotifyBaseJsonInfo() {
    }

    public abstract void parse(JSONObject object) throws JSONException;
}
