package com.phantasm.phantasm.main.api.vma;

import android.content.Context;

import com.gpit.android.util.JsonParserUtils;
import com.loopj.android.http.RequestParams;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.webapi.PTWebAPI;

import org.json.JSONObject;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class PTVMASpotlightChannelAPI extends PTWebAPI {
    private final static String TAG = PTVMASpotlightChannelAPI.class.getSimpleName();

    public final static String FEATURED_API_PATH = "http://api.phantasmgif.com/rest/phantasmwtf/vibe_videos";

    public PTVMASpotlightChannelAPI(Context context) {
        super(context, "");

        setAPIPath(FEATURED_API_PATH);
    }

    @Override
    protected String getAbsoluteUrl(String path) {
        String url = path;

        return url;
    }

    @Override
    protected RequestParams createReqParams(RequestParams params) {
        params = super.createReqParams(params);

        params.put("app_name", "phantasm.wtf");
        params.put("filter", "(featured=1)");
        params.put("fields", "id,category,title,description,featured,user_id,tags,thumb,source");

        return params;
    }

    @Override
    protected boolean parseResponse(JSONObject response) {
        if(response.has(PTConst.TAG_RECORD)) {
            setErrorCode(0);
            return true;
        }

        setErrorCode(-1);
        String error = JsonParserUtils.getStringValue(response, PTConst.TAG_ERROR);
        setErrorMsg(error);
        return false;
    }
}
