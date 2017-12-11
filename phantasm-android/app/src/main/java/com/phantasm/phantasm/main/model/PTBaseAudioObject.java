package com.phantasm.phantasm.main.model;

import java.io.Serializable;

public abstract class PTBaseAudioObject extends PTBaseMediaObject implements Serializable {
    private static final String TAG = PTBaseAudioObject.class.getSimpleName();

    public PTBaseAudioObject() {
        setMediaType(PTMediaType.MediaTypeAudioOnly);
    }
}
