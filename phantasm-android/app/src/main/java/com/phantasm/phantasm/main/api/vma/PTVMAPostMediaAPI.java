package com.phantasm.phantasm.main.api.vma;

import android.content.Context;

import com.gpit.android.logger.RemoteLogger;
import com.gpit.android.util.JsonParserUtils;
import com.loopj.android.http.RequestParams;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.model.PTChannel;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class PTVMAPostMediaAPI extends PTWebAPI {
    private final static String TAG = PTVMAPostMediaAPI.class.getSimpleName();

    private File mFile;

    public List<PTChannel> channels = new ArrayList<>();

    public PTVMAPostMediaAPI(Context context, File file) {
        super(context, "");

        mFile = file;
        setAPIPath("http://phantasm.wtf/lib/upload-ffmpeg.php");
    }

    @Override
    protected RequestParams createReqParams(RequestParams params) {
        params = super.createReqParams(params);

        try {
            params.put("file", mFile);
        } catch (FileNotFoundException e) {
            RemoteLogger.e(TAG, e.getLocalizedMessage());
        }
        return params;
    }

    @Override
    protected boolean parseResponse(JSONObject response) {
        String resultStr = JsonParserUtils.getStringValue(response, "success");
        if (resultStr.equals("true")) {
            setErrorCode(0);
            return true;
        } else {
            setErrorCode(-1);
            String error = JsonParserUtils.getStringValue(response, "details");
            setErrorMsg(error);

            return false;
        }
    }
}
