package com.phantasm.phantasm.main.api.vma;

/**
 * Created by kevinfinn on 5/22/15.
 */
public class PTAudioCategory_unused {

    final static String IMAGE_BASE_URI = "http://phantasm.wtf/";
    public String cat_name, cat_desc, picture;
    public Integer cat_id = 0, type, sub, child_of;
    private static final String REQUEST_BASE_URI = "http://www.request.com/url/";

    @Override
    public String toString() {
        return cat_name;
    }

    public String getRequestUrl() {
        //TODO
        return getImageUri();
    }

    public String getImageUri() {
        //TODO
        return IMAGE_BASE_URI + picture;
    }
}