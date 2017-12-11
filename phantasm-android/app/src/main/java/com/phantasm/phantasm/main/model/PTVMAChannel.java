package com.phantasm.phantasm.main.model;

/**
 * Created by Joseph Luns on 2016/2/3.
 */
public class PTVMAChannel extends PTChannel {
    public static final int CHANNEL_ID = -1;

    private static PTVMAChannel instance;

    public static PTVMAChannel getInstance() {
        if (instance == null) {
            instance = new PTVMAChannel();
        }

        return instance;
    }

    private PTVMAChannel() {
        id = CHANNEL_ID;
        name = "Overall VMA Channel";
    }
}
