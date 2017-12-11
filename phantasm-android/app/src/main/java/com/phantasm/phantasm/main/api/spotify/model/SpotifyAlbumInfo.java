package com.phantasm.phantasm.main.api.spotify.model;

import com.gpit.android.util.JsonParserUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ABC on 2015/12/7.
 */
public class SpotifyAlbumInfo extends SpotifyBaseJsonInfo {
    public String released;
    public String href;
    public String name;

    public JSONObject availability;

    public void parse(JSONObject object) throws JSONException {
        released = JsonParserUtils.getStringValue(object, "released");
        href = JsonParserUtils.getStringValue(object, "href");
        name = JsonParserUtils.getStringValue(object, "name");
        availability = JsonParserUtils.getJSONValue(object, "availability");
    }
}
