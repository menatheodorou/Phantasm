package com.phantasm.phantasm.main.api.vma;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.gpit.android.util.TimeUtils;
import com.phantasm.phantasm.main.model.PTBaseAudioObject;
import com.phantasm.phantasm.main.model.PTMediaType;

import junit.framework.Assert;

import java.io.Serializable;

public class PTVMAAudioObject extends PTBaseAudioObject implements Serializable {
    private static final String TAG = PTVMAAudioObject.class.getSimpleName();

    static final String VMA_BASE_URL = "http://phantasm.wtf/";
    static final String VMA_MEDIA_BASE_URL = VMA_BASE_URL + "media/";

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
    @SerializedName("description")
    private String mAuthor;

    @Expose
    private String tags;

    public PTVMAAudioObject() {
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
                super.setMediaType(PTMediaType.MediaTypeAudioVideo);
                break;
            case 2:
                super.setMediaType(PTMediaType.MediaTypeAudioOnly);
                break;
            case 3:
                super.setMediaType(PTMediaType.MediaTypeVideoOnly);
                break;
        }

        return super.getMediaType();
    }

    @Override
    public void setMediaType(PTMediaType type){
        super.setMediaType(type);

        switch (type) {
            case MediaTypeAudioVideo:
                media = 1;
                break;
            case MediaTypeAudioOnly:
                media = 2;
                break;
            case MediaTypeVideoOnly:
                media = 3;
                break;
        }
    }

    @Override
    public String getURL() {
        if (source.startsWith("localfile/")) {
            source = source.replaceAll("localfile/", "");
        }

        return VMA_MEDIA_BASE_URL + source;
    }

    @Override
    public void setURL(String url) {
        source = url;
    }

    @Override
    public String getThumbURL() {
        return thumb;
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
        return mAuthor;
    }

    @Override
    public void setDescription(String description) {
        mAuthor = description;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
