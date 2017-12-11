package com.phantasm.phantasm.main.model;

import android.content.Context;

import com.phantasm.phantasm.R;

import junit.framework.Assert;

import java.io.Serializable;

/**
 * Created by Joseph Luns on 2016/3/18.
 */
public enum PTMediaType implements Serializable {
    MediaTypeAudioOnly(0x01),
    MediaTypeVideoOnly(0x02),
    MediaTypeAudioVideo(0x03);

    private int value;

    private PTMediaType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public String getValue(Context context) {
        String value = null;
        switch (this) {
            case MediaTypeAudioOnly:
                value = context.getString(R.string.section_video);
                break;
            case MediaTypeVideoOnly:
                value = context.getString(R.string.audio);
                break;
            case MediaTypeAudioVideo:
                value = context.getString(R.string.video_audio);
                break;
            default:
                Assert.assertTrue("Unknown type" == null);
        }

        return value;
    }

    public boolean compare(int value){return this.value == value;}

    public boolean isVideo() {
        if ((getValue() & MediaTypeVideoOnly.getValue()) > 0) return true;

        return false;
    }
}
