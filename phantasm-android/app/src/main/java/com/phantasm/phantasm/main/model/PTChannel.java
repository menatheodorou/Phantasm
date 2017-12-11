package com.phantasm.phantasm.main.model;

import android.content.Context;

import com.gpit.android.util.JsonParserUtils;
import com.gpit.android.webapi.OnAPICompletedListener;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaListAPI;
import com.phantasm.phantasm.main.api.giphy.PTGiphyVideoListAPI;
import com.phantasm.phantasm.main.api.vma.PTVMAMediaListAPI;
import com.phantasm.phantasm.main.api.spotify.PTSpotifyAudioListAPI;

import junit.framework.Assert;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by Joseph Luns on 2016/2/2.
 */
public class PTChannel implements Serializable {
    public static final int CHANNEL_IMAGE_WIDTH = 256;
    public static final int CHANNEL_IMAGE_HEIGHT = 256;

    private static final String AVATAR_THUMB_URL = "http://phantasm.wtf/res.php?src=%s&q=100&w=%d&h=%d";

    public long id;
    public long groupId;
    public String email;
    public String avatar;
    public String name;
    public long viewCounts;
    public String fbLink;
    public String twLink;
    public String gLink;
    public boolean isMale;
    public long registeredTime;
    public long lastNotyTime;
    public long lastLoginTime;

    public PTChannel() {}

    public PTChannel(JSONObject object) {
        id = JsonParserUtils.getLongValue(object, "id");
        groupId = JsonParserUtils.getLongValue(object, "group_id");
        email = JsonParserUtils.getStringValue(object, "email");
        avatar = JsonParserUtils.getStringValue(object, "avatar");
        name = JsonParserUtils.getStringValue(object, "name");
        viewCounts = JsonParserUtils.getLongValue(object, "views");
        fbLink = JsonParserUtils.getStringValue(object, "fblink");
        twLink = JsonParserUtils.getStringValue(object, "twlink");
        gLink = JsonParserUtils.getStringValue(object, "glink");
        isMale = JsonParserUtils.getBooleanValue(object, "gender");
        registeredTime = JsonParserUtils.getDateValue(object, "date_registered", "yyyy-MM-dd HH:mm:ss");
        lastNotyTime = JsonParserUtils.getDateValue(object, "lastNoty", "yyyy-MM-dd HH:mm:ss");
        lastLoginTime = JsonParserUtils.getDateValue(object, "lastlogin", "yyyy-MM-dd HH:mm:ss");
    }

    public String getAvatarURL(int width, int height) {
        String url = String.format(Locale.getDefault(), AVATAR_THUMB_URL, avatar, width, height);

        return url;
    }

    public void searchMedia(Context context, PTMediaType mediaType, String keyword,
                            int offset, int limit, OnAPICompletedListener<PTWebAPI> listener) {
        PTBaseMediaListAPI api;

        if (this.equals(PTVMAChannel.getInstance())) {
            api = new PTVMAMediaListAPI(context, mediaType, PTVMAMediaListAPI.ALL_USER_ID, keyword, offset, limit);
        } else if (this.equals(PTGiphyChannel.getInstance())) {
            Assert.assertTrue(mediaType.isVideo());
            api = new PTGiphyVideoListAPI(context, keyword, offset, limit);
        } else if (this.equals(PTSpotifyChannel.getInstance())) {
            Assert.assertTrue(mediaType == PTMediaType.MediaTypeAudioOnly);
            api = new PTSpotifyAudioListAPI(context, keyword, offset, limit);
        } else {
            api = new PTVMAMediaListAPI(context, mediaType, id, keyword, offset, limit);
        }

        Assert.assertTrue(api != null);
        api.showProgress(false);

        api.exec(listener);
    }

    @Override
    public boolean equals(Object channel) {
        if (channel == null) return false;

        return (id == ((PTChannel)channel).id);
    }
}
