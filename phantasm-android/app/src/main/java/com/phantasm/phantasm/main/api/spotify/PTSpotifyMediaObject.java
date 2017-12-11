package com.phantasm.phantasm.main.api.spotify;

import com.phantasm.phantasm.main.api.vma.PTVMAAudioObject;

import java.io.Serializable;

public class PTSpotifyMediaObject extends PTVMAAudioObject implements Serializable {
    private static final String TAG = PTSpotifyMediaObject.class.getSimpleName();

    @Override
    public String getURL() {
        return getSource();
    }
}
