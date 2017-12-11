package com.phantasm.phantasm.main.api.lorempixel;

/**
 * Created by kevinfinn on 5/22/15.
 */
public class PTCategory {

    final static String IMAGE_BASE_URI = "http://lorempixel.com/100/100/";
    public String name, description, image_url;
    public Integer id = 0;
    private static final String REQUEST_BASE_URI = "http://www.request.com/url/";

    public String getRequestUrl() {
        //TODO
        return getImageUri();
    }

    static String getImgCat(int pos) {
        switch (pos % 10) {
            case 0:
                return "abstract";
            case 1:
                return "animals";
            case 2:
                return "business";
            case 3:
                return "cats";
            case 4:
                return "city";
            case 5:
                return "food";
            case 6:
                return "nightlife";
            case 7:
                return "fashion";
            case 8:
                return "people";
            case 9:
                return "nature";
            default:
                return "technics";
        }
    }

    public String getImageUri() {
        //TODO
        return IMAGE_BASE_URI + getImgCat(id) + '/';
    }
}
