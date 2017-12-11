package com.phantasm.phantasm.common;

import android.content.Context;
import android.os.Environment;

import com.dreamfactory.PrefUtil;
import com.google.gson.Gson;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.model.PTTabID;

import java.io.File;

/**
 * Created by Joseph Luns on 2016/3/18.
 */
public class PTSettings {
    public static final String PREFS_KEY_LATEST_TAB = "key_latest_tab";
    public static final String PREFS_KEY_PRELOAD_AUDIO_CHANNEL = "key_preload_audio_channel";
    public static final String PREFS_KEY_PRELOAD_VIDEO_CHANNEL = "key_preload_video_channel";
    public static final String PREFS_KEY_PRELOAD_AUDIO_VIDEO_CHANNEL = "key_preload_audio_video_channel";

    private static PTSettings instance;

    public static PTSettings getInstance(Context context) {
        if (instance == null) {
            instance = new PTSettings();
        }

        instance.mContext = context;

        return instance;
    }

    private Context mContext;

    public void setLatestTab(PTTabID tabId) {
        PrefUtil.putInt(mContext, PREFS_KEY_LATEST_TAB, tabId.getValue());
    }

    public PTTabID getLatestTab() {
        int tabId = PrefUtil.getInt(mContext, PREFS_KEY_LATEST_TAB, PTTabID.TAB_CONNECT.getValue());

        return PTTabID.getValue(tabId);
    }

    public String getPrefKey(PTMediaType mediaType) {
        String prefKey = PREFS_KEY_PRELOAD_AUDIO_CHANNEL;

        switch (mediaType) {
            case MediaTypeAudioOnly:
                prefKey = PREFS_KEY_PRELOAD_AUDIO_CHANNEL;
                break;
            case MediaTypeVideoOnly:
                prefKey = PREFS_KEY_PRELOAD_VIDEO_CHANNEL;
                break;
            case MediaTypeAudioVideo:
                prefKey = PREFS_KEY_PRELOAD_AUDIO_VIDEO_CHANNEL;
                break;
        }

        return prefKey;
    }

    public void saveLatestChannel(PTChannel channel, PTMediaType mediaType) {
        String prefKey = getPrefKey(mediaType);

        if (channel != null) {
            Gson gson = new Gson();
            String json = gson.toJson(channel);
            PrefUtil.putString(mContext, prefKey, json);
        } else {
            PrefUtil.putString(mContext, prefKey, null);
        }
    }

    public PTChannel getLatestChannel(PTMediaType mediaType) {
        Gson gson = new Gson();
        String json = PrefUtil.getString(mContext, getPrefKey(mediaType));
        PTChannel selectedChannel = gson.fromJson(json, PTChannel.class);

        return selectedChannel;
    }

    public File getOutputDir() {
        File folder;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            folder = new File(Environment.getExternalStorageDirectory(), mContext.getString(R.string.app_name));
            if (!folder.exists()) {
                folder.mkdir();
            }
        } else {
            folder = mContext.getCacheDir();
        }

        return folder;
    }
}
