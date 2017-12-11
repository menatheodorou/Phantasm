package com.phantasm.phantasm.main.api.giphy.model;

import com.google.gson.annotations.Expose;
import com.gpit.android.util.TimeUtils;
import com.phantasm.phantasm.main.model.PTBaseVideoObject;

import junit.framework.Assert;

import java.io.Serializable;
import java.util.HashMap;

// http://api.giphy.com/v1/gifs/search?q=test&offset=2&limit=3&api_key=dc6zaTOxFJmzC
public class PTGiphyMediaObject extends PTBaseVideoObject implements Serializable {
    @Expose
    public String type;
    @Expose
    public String id;
    @Expose
    public int category;
    @Expose
    public String url;
    @Expose
    public String bitly_gif_url;
    @Expose
    public String username;
    @Expose
    public String source;
    @Expose
    public String rating;
    @Expose
    public String import_datetime;
    @Expose
    public String trending_datetime;
    @Expose
    public HashMap<String, PTImageRecordSubType> images;

    public PTImageRecordSubType mFixedWidthImage = new PTImageRecordSubType();

    public PTGiphyMediaObject() {
        super(true);
    }

    /*********************** Normal Function ******************/
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getURL() {
        if (getOriginalImage() == null) return null;

        return getOriginalImage().mp4;
    }

    @Override
    public void setURL(String url) {
        if (getOriginalImage() == null) return;

        getOriginalImage().mp4 = url;
    }

    @Override
    public String getThumbURL() {
        if (getOriginalImage() == null) return null;

        return getOriginalImage().url;
    }

    @Override
    public void setThumbURL(String url) {
        if (getOriginalImage() == null) return;

        getOriginalImage().url = url;
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
        return "";
    }

    @Override
    public void setTitle(String title) {
        Assert.assertNull("Not supported function");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void setDescription(String description) {
        Assert.assertNull("Not supported function");
    }

    @Override
    public String getTag() {
        return "";
    }

    @Override
    public void setTag(String tag) {
        Assert.assertNull("Not supported function");
    }

    @Override
    public String getAuthor() {
        return username;
    }

    @Override
    public void setAuthor(String author) {
        username = author;
    }

    @Override
    public long getSize() {
        if (getOriginalImage() == null) return 0;

        return getOriginalImage().size;
    }

    @Override
    public void setSize(int size) {
        if (getOriginalImage() == null) return;

        getOriginalImage().size = size;
    }

    @Override
    public long getTime() {
        long timestamp = TimeUtils.getDate(import_datetime, "yyyy-MM-dd hhh:mm:ss").getTime();
        return timestamp;
    }

    @Override
    public void setTime(long timestamp) {
        import_datetime = TimeUtils.getDateString(timestamp, "yyyy-MM-dd hhh:mm:ss");
    }

    @Override
    public long getLength() {
        Assert.assertNull("Not supported function");
        return 0;
    }

    @Override
    public void setLength(int length) {
        Assert.assertNull("Not supported function");
    }

    /********************* Self Function ********************/
    public PTImageRecordSubType getFixedWidthImage() {
        PTImageRecordSubType fixedWidthImage = images.get("fixed_width");
        return fixedWidthImage;
    }

    public PTImageRecordSubType getOriginalImage() {
        PTImageRecordSubType fixedWidthImage = images.get("original");
        return fixedWidthImage;
    }
}
