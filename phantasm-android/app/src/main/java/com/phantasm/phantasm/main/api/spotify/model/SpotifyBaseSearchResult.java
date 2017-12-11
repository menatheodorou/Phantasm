package com.phantasm.phantasm.main.api.spotify.model;

import com.gpit.android.util.JsonParserUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class SpotifyBaseSearchResult<T extends SpotifyBaseJsonInfo> {
    public SpotifySearchInfo searchInfo;
    public ArrayList<T> results = new ArrayList<T>();

    public void parse(Class<T> clz, JSONObject object) throws JSONException {
        JSONObject trackObject = JsonParserUtils.getJSONValue(object, "tracks");

        // Parse PTLoginAPIInfo object
        searchInfo = new SpotifySearchInfo();
        searchInfo.parse(trackObject);

        // Parse tracks object
        JSONArray itemArray = JsonParserUtils.getJSONArrayValue(trackObject, "items");
        if (itemArray != null) {
            for (int i = 0 ; i < itemArray.length() ; i++) {
                JSONObject jsonObject = itemArray.getJSONObject(i);

                T resultInfo = null;
                try {
                    resultInfo = clz.newInstance();
                    resultInfo.parse(jsonObject);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                results.add(resultInfo);
            }
        }

        searchInfo.limit = results.size();
    }

    public int getCount() {
        return results.size();
    }
}
