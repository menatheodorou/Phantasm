package com.phantasm.phantasm.main.api.vma;

import android.content.Context;

import com.gpit.android.logger.RemoteLogger;
import com.gpit.android.util.JsonParserUtils;
import com.loopj.android.http.RequestParams;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.webapi.PTWebAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Created by Joseph Luns on 2015/12/7.
 */
public class PTVMAPostSignInAPI extends PTWebAPI {
    private final static String TAG = PTVMAPostSignInAPI.class.getSimpleName();

    public final static String SIGNIN_SIGNUP_API_PATH = "http://phantasm.wtf/api/phantasm/gateway.php";
    public final static String SIGNIN_PHONE_API_PATH = "http://phantasm.wtf/api/phantasm/gateway.php?app=Phantasm&service=User&action=signIn&json=true&json_arguments=['%s','$s']";
    public final static String SIGNUP_API_PATH = "http://phantasm.wtf/api/phantasm/gateway.php?app=Phantasm&service=User&action=signUp&json=true&json_arguments=['%s','$s']";
    public final static String VERIFY_PHONE_API_PATH = "http://phantasm.wtf/api/phantasm/gateway.php?app=Phantasm&service=User&action=signIn&json=true&json_arguments=['%s','$s']";

    private String mAction;
    private String mJsonArguments;

    public PTVMAPostSignInAPI(Context context) {
        super(context, "");

        setAPIPath(SIGNIN_SIGNUP_API_PATH);
    }

    public void setInfo(String action, String jsonArguments) {
        mAction = action;
        mJsonArguments = jsonArguments;
    }

    @Override
    protected String getAbsoluteUrl(String path) {
        String url = path;

        return url;
    }

    @Override
    protected RequestParams createReqParams(RequestParams params) {
        params = super.createReqParams(params);

        params.put("app", "Phantasm");
        params.put("service", "User");
        params.put("action", mAction);
        params.put("json", true);
        params.put("json_arguments", mJsonArguments);

        return params;
    }

    @Override
    protected boolean parseResponse(JSONObject response) {
        JSONObject dataObject = JsonParserUtils.getJSONValue(response, PTConst.TAG_DATA);
        if(dataObject.has(PTConst.TAG_ERROR)) {
            setErrorCode(-1);
            String error = JsonParserUtils.getStringValue(dataObject, PTConst.TAG_ERROR);
            setErrorMsg(error);
            return false;
        }

        setErrorCode(0);
        return true;
    }
}
