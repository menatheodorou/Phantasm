package com.phantasm.phantasm.main.model;

import android.support.annotation.CallSuper;

import java.io.Serializable;

public abstract class PTBaseMediaObject implements Serializable {
    private static final String TAG = PTBaseMediaObject.class.getSimpleName();

    protected PTMediaType mMediaType;

    protected transient Object mMeta;

    public PTBaseMediaObject() {
    }

    @Override
    public boolean equals (Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof PTBaseMediaObject) {
            PTBaseMediaObject mediaObject = (PTBaseMediaObject) other;
            return getId() == null ? false : getId().equals(mediaObject.getId());
        } else {
            return false;
        }
    }

    /***************** Normal function *****************/
    public abstract String getId();
    public abstract void setId(String id);

    public PTMediaType getMediaType() {
        return mMediaType;
    }

    public void setMediaType(PTMediaType type){
        mMediaType = type;
    }

    public abstract String getURL();
    public abstract void setURL(String url);

    public abstract String getThumbURL();
    public abstract void setThumbURL(String url);

    public abstract int getCategory();
    public abstract void setCategory(int category);

    public abstract String getTitle();
    public abstract void setTitle(String title);

    public abstract String getDescription();
    public abstract void setDescription(String description);

    public abstract String getTag();
    public abstract void setTag(String tag);

    public abstract String getAuthor();
    public abstract void setAuthor(String author);

    public abstract long getSize();
    public abstract void setSize(int size);

    public abstract long getTime();
    public abstract void setTime(long timestamp);

    public abstract long getLength();
    public abstract void setLength(int length);

    public Object getMeta() {
        return mMeta;
    }

    public void setMeta(Object meta) {
        mMeta = meta;
    }

    @Override
    public String toString() {
        return getId() + " " + getAuthor() + " " + getTitle() + " " + getDescription() + " " + getURL();
    }
}
