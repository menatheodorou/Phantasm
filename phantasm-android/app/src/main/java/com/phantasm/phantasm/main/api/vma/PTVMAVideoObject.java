package com.phantasm.phantasm.main.api.vma;

import com.google.gson.annotations.Expose;
import com.gpit.android.util.TimeUtils;
import com.phantasm.phantasm.main.model.PTBaseVideoObject;
import com.phantasm.phantasm.main.model.PTMediaType;

import junit.framework.Assert;

import java.io.Serializable;

public class PTVMAVideoObject extends PTBaseVideoObject implements Serializable {
    private static final String TAG = PTVMAVideoObject.class.getSimpleName();

    @Expose
    private String id;

    @Expose
    private int category;

    @Expose
    private int media;

    @Expose
    private String user_id;

    @Expose
    private String date;

    @Expose
    private String source;

    @Expose
    private String title;

    @Expose
    private String thumb;

    @Expose
    private long duration;

    @Expose
    private String description;

    @Expose
    private String tags;

    // Self properties
    private String mAuthor;

    public PTVMAVideoObject(boolean hasVideo) {
        super(hasVideo);
    }

    /***************** Normal function *****************/
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public PTMediaType getMediaType() {
        switch (media) {
            case 1:
                super.setMediaType(PTMediaType.MediaTypeVideoOnly);
                break;
            case 2:
                super.setMediaType(PTMediaType.MediaTypeAudioOnly);
                break;
            case 3:
                super.setMediaType(PTMediaType.MediaTypeAudioVideo);
                break;
        }

        return super.getMediaType();
    }

    @Override
    public void setMediaType(PTMediaType type){
        super.setMediaType(type);

        switch (type) {
            case MediaTypeVideoOnly:
                media = 1;
                break;
            case MediaTypeAudioOnly:
                media = 2;
                break;
            case MediaTypeAudioVideo:
                media = 3;
                break;
        }
    }

    @Override
    public String getURL() {
        if (source.startsWith("localfile/")) {
            source = source.replaceAll("localfile/", "");
        }

        return PTVMAAudioObject.VMA_MEDIA_BASE_URL + source;
    }

    @Override
    public void setURL(String url) {
        source = url;
    }

    @Override
    public String getThumbURL() {
        return PTVMAAudioObject.VMA_BASE_URL + thumb;
    }

    @Override
    public void setThumbURL(String url) {
        thumb = url;
    }

    @Override
    public int getCategory() {
        return category;
    }

    @Override
    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTag() {
        return tags;
    }

    @Override
    public void setTag(String tag) {
        tags = tag;
    }

    @Override
    public String getAuthor() {
        return mAuthor;
    }

    @Override
    public void setAuthor(String author) {
        mAuthor = author;
    }

    @Override
    public long getSize() {
        Assert.assertNull("Not supported function");
        return 0;
    }

    @Override
    public void setSize(int size) {
        Assert.assertNull("Not supported function");
    }

    @Override
    public long getTime() {
        long timestamp = TimeUtils.getDate(date, "yyyy-MM-dd hhh:mm:ss").getTime();
        return timestamp;
    }

    @Override
    public void setTime(long timestamp) {
        date = TimeUtils.getDateString(timestamp, "yyyy-MM-dd hhh:mm:ss");
    }

    @Override
    public long getLength() {
        return duration;
    }

    @Override
    public void setLength(int duration) {

    }

    /***************** Self function *****************/
    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        user_id = userId;
    }
}
