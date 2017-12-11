package com.phantasm.phantasm.main.api;

import com.google.gson.annotations.Expose;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTPagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Luns on 5/3/15.
 */
public abstract class PTBaseMediaResponse<T extends PTBaseMediaObject> implements Serializable {
    @Expose
    public List<T> data = new ArrayList<>();

    public List<T> getMediaObjects() {
        return data;
    }

    public abstract PTPagination getPagination();
    public abstract void setPagination(PTPagination pagination);
}

