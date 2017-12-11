package com.phantasm.phantasm.main.api.spotify.model;

import com.gpit.android.util.JsonParserUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ABC on 2015/12/7.
 */
public class SpotifySearchInfo extends SpotifyBaseJsonInfo {
    public int num_results;
    public int offset;
    public int limit;
    public String query;

    // {"num_results":42887939,"limit":100,"offset":0,"query":"track:test OR artist:test tag:hipster","type":"track","page":1}
    public void parse(JSONObject object) throws JSONException {
        num_results = JsonParserUtils.getIntValue(object, "total");
        limit = JsonParserUtils.getIntValue(object, "limit");
        offset = JsonParserUtils.getIntValue(object, "offset");
        query = JsonParserUtils.getStringValue(object, "href");
    }
}
