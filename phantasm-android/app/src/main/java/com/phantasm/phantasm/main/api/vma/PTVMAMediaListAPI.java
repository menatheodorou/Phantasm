package com.phantasm.phantasm.main.api.vma;

import android.content.Context;

import com.google.gson.Gson;
import com.phantasm.phantasm.main.api.PTBaseMediaListAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.model.PTPagination;

import junit.framework.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class PTVMAMediaListAPI extends PTBaseMediaListAPI {
    public final static int ALL_USER_ID = -1;
    private final static String CHANNEL_LIST_API_PATH =
            "vibe_videos?app_name=phantasm.wtf&filter=((media=%d %s)%s)&offset=%d&limit=%d&order=date DESC";

    protected PTMediaType mMediaType = PTMediaType.MediaTypeAudioOnly;

    public PTVMAMediaListAPI(Context context, PTMediaType mediaType, long userId, String keyword, int offset, int limit) {
        super(context, userId, keyword, offset, limit);

        mMediaType = mediaType;

        // Should clean this up by using a string array and joining with AND or OR, to
        // handle when we're searching by query only, category only, or query + category
        if (mKeyword != null && mKeyword.length() > 0) {
            try {
                mKeyword = URLEncoder.encode(" and (tags like '%" + mKeyword + "%' OR title like '%" + mKeyword + "%')", "UTF-8");// + SEARCH_URL_POST;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String userQuery = "";
        if (userId != ALL_USER_ID) {
            userQuery = "and user_id=" + userId;
        }

        int vmaMediaType = 1;
        switch (mMediaType) {
            case MediaTypeAudioVideo:
                vmaMediaType = 1;
                break;
            case MediaTypeAudioOnly:
                vmaMediaType = 2;
                break;
            case MediaTypeVideoOnly:
                vmaMediaType = 3;
                break;
            default:
                Assert.assertNotNull("Not supported media type");
        }
        String apiPath = String.format(Locale.getDefault(), CHANNEL_LIST_API_PATH, vmaMediaType,
                userQuery, mKeyword, mOffset, mLimit);
        setAPIPath(apiPath);
    }

    @Override
    protected PTBaseMediaResponse parseMediaResponse(Gson gson, String jsonResponse) {
        PTVMABaseMediaResponse mediaResponse;

        if (mMediaType.isVideo()) {
            mediaResponse = gson.fromJson(jsonResponse, PTVMAVideoResponse.class);
        } else {
            mediaResponse = gson.fromJson(jsonResponse, PTVMAAudioResponse.class);
        }

        PTPagination pagination = mediaResponse.getPagination();
        pagination.offset = mOffset;
        pagination.total_count = mediaResponse.getMediaObjects().size();
        pagination.count = mediaResponse.getMediaObjects().size();

        return mediaResponse;
    }
}
