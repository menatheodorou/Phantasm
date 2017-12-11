package com.phantasm.phantasm.main.api.vma;

import android.content.Context;

import com.gpit.android.util.JsonParserUtils;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.model.PTChannel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ABC on 2015/12/7.
 */
public class PTVMAChannelListAPI extends PTWebAPI {
    private final static String CHANNEL_LIST_API_PATH = "vibe_users?app_name=phantasm.wtf&filter=group_id=5&offset=%d&limit=%d";

    private int mOffset;
    private int mLimit;

    public List<PTChannel> channels = new ArrayList<>();

    public PTVMAChannelListAPI(Context context, int offset, int limit) {
        super(context, "");

        mOffset = offset;
        mLimit = limit;

        String apiPath = String.format(Locale.getDefault(), CHANNEL_LIST_API_PATH, mOffset, mLimit);
        setAPIPath(apiPath);
    }

    @Override
    protected boolean parseResponse(JSONObject response) {
        channels.clear();

        try {
            JSONArray channelListObject = JsonParserUtils.getJSONArrayValue(response, "record");
            if (channelListObject != null) {
                for (int i = 0 ; i < channelListObject.length() ; i++) {
                    JSONObject channelObject = channelListObject.getJSONObject(i);
                    PTChannel channel = new PTChannel(channelObject);
                    channels.add(channel);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}
