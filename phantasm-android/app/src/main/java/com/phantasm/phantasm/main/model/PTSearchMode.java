package com.phantasm.phantasm.main.model;

import android.content.Context;

import com.phantasm.phantasm.R;

import junit.framework.Assert;

/**
 * Created by ABC on 2015/12/17.
 */
public enum PTSearchMode {
    SearchVideo(1),
    SearchAudio(2);

    private int value;

    private PTSearchMode(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public String getValue(Context context) {
        String value = null;
        switch (this) {
            case SearchVideo:
                value = context.getString(R.string.section_video);
                break;
            case SearchAudio:
                value = context.getString(R.string.audio);
                break;
            default:
                Assert.assertTrue("Unknown type" == null);
        }

        return value;
    }

    public boolean compare(int value){return this.value == value;}

    public boolean isVideoSearchMode() {
        if (this == SearchVideo) return true;

        return false;
    }

    public static PTSearchMode getValue(int value) {
        PTSearchMode[] as = PTSearchMode.values();
        for(int i = 0; i < as.length; i++) {
            if(as[i].compare(value))
                return as[i];
        }

        return PTSearchMode.SearchVideo;
    }
}
