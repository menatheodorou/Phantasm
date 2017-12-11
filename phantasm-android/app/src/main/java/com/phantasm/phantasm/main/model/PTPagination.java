package com.phantasm.phantasm.main.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Joseph Luns on 2016/2/21.
 */
public class PTPagination implements Serializable {
    @Expose
    public int total_count;
    @Expose
    public int count;
    @Expose
    public int offset;

    public String query;
}
