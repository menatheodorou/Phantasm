package com.phantasm.phantasm.main.model;

import android.content.Context;

import com.phantasm.phantasm.R;

import junit.framework.Assert;

/**
 * Created by ABC on 2015/12/17.
 */
public enum PTSectionItemType {
    SectionVideo(1),
    SectionShare (2),
    SectionAudio(3);

    private int value;

    private PTSectionItemType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public String getValue(Context context) {
        String value = null;
        switch (this) {
            case SectionVideo:
                value = context.getString(R.string.section_video);
                break;
            case SectionAudio:
                value = context.getString(R.string.section_audio);
                break;
            case SectionShare:
                value = context.getString(R.string.section_share);
                break;
            default:
                Assert.assertTrue("Unknown type" == null);
        }

        return value;
    }

    public boolean compare(int value){return this.value == value;}

    public static PTSectionItemType getValue(int value) {
        PTSectionItemType[] as = PTSectionItemType.values();
        for(int i = 0; i < as.length; i++) {
            if(as[i].compare(value))
                return as[i];
        }

        return PTSectionItemType.SectionVideo;
    }
}
