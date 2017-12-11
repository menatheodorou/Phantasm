package com.phantasm.phantasm.main.model;

import android.support.annotation.CallSuper;

import java.io.Serializable;

public abstract class PTBaseVideoObject extends PTBaseMediaObject implements Serializable {
    private static final String TAG = PTBaseVideoObject.class.getSimpleName();

    public PTBaseVideoObject(boolean hasVideo) {
        if (hasVideo) {
            setMediaType(PTMediaType.MediaTypeAudioVideo);
        } else {
            setMediaType(PTMediaType.MediaTypeAudioOnly);
        }
    }
}
