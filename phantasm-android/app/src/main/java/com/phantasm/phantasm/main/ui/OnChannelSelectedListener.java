package com.phantasm.phantasm.main.ui;

import com.phantasm.phantasm.main.model.PTChannel;

import java.util.List;

/**
 * Created by Joseph Luns on 2016/2/2.
 */
public interface OnChannelSelectedListener {
    public void onChannelLoaded(List<PTChannel> channels);
    public void onChannelSelected(PTChannel channel);
}
