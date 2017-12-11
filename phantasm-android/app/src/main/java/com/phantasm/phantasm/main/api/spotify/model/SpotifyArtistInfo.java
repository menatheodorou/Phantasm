package com.phantasm.phantasm.main.api.spotify.model;

import com.gpit.android.util.JsonParserUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ABC on 2015/12/7.
 */
public class SpotifyArtistInfo extends SpotifyBaseJsonInfo {
    public String href;
    public String name;

    // {"href":"spotify:artist:7d58WZ8qQHy2Sm5p52V2NP","name":"Aperture Science Psychoacoustic Laboratories"}
    public void parse(JSONObject object) throws JSONException {
        href = JsonParserUtils.getStringValue(object, "href");
        name = JsonParserUtils.getStringValue(object, "name");
    }
}
