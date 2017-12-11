package com.phantasm.phantasm.main.api.giphy.model;

import com.google.gson.annotations.Expose;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTPagination;

import java.io.Serializable;

public class PTImagesResponse extends PTBaseMediaResponse<PTGiphyMediaObject> implements Serializable {
    @Expose
    public PTPagination pagination = new PTPagination();

    public String query; //query that resulted in this PTImagesResponse

    @Override
    public PTPagination getPagination() {
        return pagination;
    }

    @Override
    public void setPagination(PTPagination pagination) {
        this.pagination = pagination;
    }
}
