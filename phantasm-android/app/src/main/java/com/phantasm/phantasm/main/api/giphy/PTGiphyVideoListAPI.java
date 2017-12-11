package com.phantasm.phantasm.main.api.giphy;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.gpit.android.webapi.OnAPICompletedListener;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaListAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.api.giphy.model.PTImagesResponse;
import com.phantasm.phantasm.main.model.PTGiphyChannel;

import java.util.Locale;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class PTGiphyVideoListAPI extends PTBaseMediaListAPI {
    private final static String CHANNEL_LIST_API_PATH =
            "http://api.giphy.com/v1/gifs/search?q=%s&offset=%d&limit=%d&&api_key=dc6zaTOxFJmzC";
    
    public static final String TREND_LIST_API_PATH =
            "http://api.giphy.com/v1/gifs/trending?api_key=dc6zaTOxFJmzC&limit=100";

    private OnAPICompletedListener<PTWebAPI> mListener;
    public PTGiphyVideoListAPI(Context context, String keyword, int offset, int limit) {
        super(context, PTGiphyChannel.CHANNEL_ID, keyword, offset, limit);

        mKeyword = keyword;

        String apiPath;
        if (!TextUtils.isEmpty(mKeyword)) {
            apiPath = String.format(Locale.getDefault(), CHANNEL_LIST_API_PATH, mKeyword, mOffset,
                    mLimit);
        } else {
            apiPath = TREND_LIST_API_PATH;
        }

        setAPIPath(apiPath);
    }

    @Override
    protected String getAbsoluteUrl(String path) {
        String url = path;

        return url;
    }

    @Override
    protected PTBaseMediaResponse parseMediaResponse(Gson gson, String jsonResponse) {
        mediaResponse = gson.fromJson(jsonResponse, PTImagesResponse.class);

        return mediaResponse;
    }
}
