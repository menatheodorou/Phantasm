package com.phantasm.phantasm.main.api.giphy;

import com.google.gson.annotations.Expose;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.api.giphy.model.PTGiphyMediaObject;
import com.phantasm.phantasm.main.model.PTPagination;

import java.io.Serializable;

/**
 * Created by Joseph Luns on 5/3/15.
 */
public class PTGiphyMediaResponse extends PTBaseMediaResponse<PTGiphyMediaObject> implements Serializable {
    @Expose
    public PTPagination pagination = new PTPagination();

    public PTPagination getPagination() {
        return pagination;
    }

    @Override
    public void setPagination(PTPagination pagination) {
        this.pagination = pagination;
    }
}

