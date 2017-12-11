package com.phantasm.phantasm.main.model;

/**
 * Created by osxcapitan on 5/11/16.
 */
public class PTFeaturedVideoObject extends PTBaseVideoObject {

    private String id;
    private int category;
    private String url;
    private String thumbURL;
    private String title;
    private String description;
    private String tag;
    private String author;
    private long size;
    private long time;
    private long length;

    public PTFeaturedVideoObject(boolean hasVideo) {
        super(hasVideo);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
    public String getURL() {
        return url;
    }

    @Override
    public void setURL(String url) {
        this.url = url;
    }

    @Override
    public String getThumbURL() {
        return thumbURL;
    }

    @Override
    public void setThumbURL(String url) {
        this.thumbURL = url;
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
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long timestamp) {
        this.time = timestamp;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
    }
}
