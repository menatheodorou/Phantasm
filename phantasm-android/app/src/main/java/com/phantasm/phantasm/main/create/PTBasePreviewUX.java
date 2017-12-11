package com.phantasm.phantasm.main.create;

import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;

/**
 * Created by Joseph Luns on 2016/2/25.
 */
public interface PTBasePreviewUX {
    public void setMedia(PTBaseMediaResponse media);
    public PTBaseMediaObject getCurrentPreviewObject();
}
