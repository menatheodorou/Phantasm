package com.phantasm.phantasm.main.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.phantasm.phantasm.common.webapi.PTWebAPI;

import org.json.JSONObject;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public abstract class PTBaseMediaListAPI extends PTWebAPI {
    protected long mChannelId;

    protected int mOffset;
    protected int mLimit;
    protected String mKeyword;

    public PTBaseMediaResponse mediaResponse;

    protected abstract PTBaseMediaResponse parseMediaResponse(Gson gson, String jsonResponse);

    @Override
    protected boolean parseResponse(JSONObject response) {
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String jsonResponse = response.toString();
            mediaResponse = parseMediaResponse(gson, jsonResponse);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public PTBaseMediaListAPI(Context context, long channelId, String keyword, int offset, int limit) {
        super(context, "");

        mChannelId = channelId;
        mOffset = offset;
        mLimit = limit;
        mKeyword = keyword;

        // Remove special characters
        if (mKeyword != null && mKeyword.length() > 0) {
            mKeyword = mKeyword.replaceAll("[^a-zA-Z0-9\\s]", "");
        } else {
            mKeyword = "";
        }
    }
}
