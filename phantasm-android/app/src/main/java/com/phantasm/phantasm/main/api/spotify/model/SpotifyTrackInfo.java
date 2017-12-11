package com.phantasm.phantasm.main.api.spotify.model;

import com.gpit.android.util.JsonParserUtils;
import com.phantasm.phantasm.main.api.spotify.PTSpotifyMediaObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class SpotifyTrackInfo extends SpotifyBaseJsonInfo {
    public SpotifyAlbumInfo albumInfo;

    public String id;
    public String name;
    public float popularity;
    public JSONArray external_ids;
    public float length;
    public String href;
    public String preview_url;
    public String uri;

    public List<SpotifyArtistInfo> artists = new ArrayList<SpotifyArtistInfo>();

    public int track_number;

    public void parse(JSONObject object) throws JSONException {
        // Parse album
        JSONObject albumObj = JsonParserUtils.getJSONValue(object, "album");
        albumInfo = new SpotifyAlbumInfo();
        albumInfo.parse(albumObj);

        // Parse artist PTLoginAPIInfo
        JSONArray artistArray = JsonParserUtils.getJSONArrayValue(object, "artists");
        if (artistArray != null) {
            for (int i = 0 ; i < artistArray.length() ; i++) {
                JSONObject artistObj = artistArray.getJSONObject(i);

                SpotifyArtistInfo artistInfo = new SpotifyArtistInfo();
                artistInfo.parse(artistObj);
                artists.add(artistInfo);
            }
        }

        external_ids = JsonParserUtils.getJSONArrayValue(object, "external-ids");
        length = JsonParserUtils.getFloatValue(object, "duration_ms");

        href = JsonParserUtils.getStringValue(object, "href");
        id = JsonParserUtils.getStringValue(object, "id");
        name = JsonParserUtils.getStringValue(object, "name");
        popularity = JsonParserUtils.getFloatValue(object, "popularity");
        preview_url = JsonParserUtils.getStringValue(object, "preview_url");
        track_number = JsonParserUtils.getIntValue(object, "track-number");
        uri = JsonParserUtils.getStringValue(object, "uri");
    }

    public PTSpotifyMediaObject getAudioObject() {
        PTSpotifyMediaObject object = new PTSpotifyMediaObject();
        if (!artists.isEmpty()) {
            SpotifyArtistInfo artistInfo = artists.get(0);
            object.setAuthor(artistInfo.name);
        }
        object.setTitle(name);
        object.setURL(preview_url);
        object.setThumbURL(href);
        object.setId(id);
        object.setMeta(this);

        return object;
    }
}
