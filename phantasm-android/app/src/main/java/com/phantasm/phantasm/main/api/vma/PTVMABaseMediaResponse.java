package com.phantasm.phantasm.main.api.vma;

import com.google.gson.annotations.Expose;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTPagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Luns on 5/3/15.
 */
public abstract class PTVMABaseMediaResponse<T extends PTBaseMediaObject> extends PTBaseMediaResponse<T> implements Serializable {
    @Expose
    public List<T> record = new ArrayList<>();

    @Override
    public List<T> getMediaObjects() {
        return record;
    }

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

