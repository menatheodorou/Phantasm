package com.phantasm.phantasm.main.api.giphy.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by kevinfinn on 5/14/15.
 */
public class PTImageRecordSubType implements Serializable {
    @Expose
    public String url;
    @Expose
    public int width;
    @Expose
    public int height;
    @Expose
    public int size;
    @Expose
    public String mp4;

    public PTImageRecordSubType() {
    }
}
