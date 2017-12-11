package com.phantasm.phantasm.main.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTGiphyChannel;

import java.util.ArrayList;
import java.util.List;

public class PTVideoChooseChannelView extends PTBaseChooseChannelView {
    private List<PTChannel> mVideoChannels = new ArrayList<>();
    protected boolean mVMAChannelOnly = false;

    public PTVideoChooseChannelView(Context context) {
        super(context);

        init();
    }

    public PTVideoChooseChannelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PTVideoChooseChannelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public void setVMAChannelOnly(boolean vmaOnly) {
        mVMAChannelOnly = vmaOnly;
    }

    private void init() {
        loadChannels(channels);
    }

    @Override
    protected void loadChannels(List<PTChannel> channels) {
        // Lets sure its initialized
        if (mVideoChannels == null) return;

        if (channels != null) {
            mVideoChannels.clear();
            mVideoChannels.addAll(channels);

            if (!mVMAChannelOnly) {
                // Add giphy channel
                mVideoChannels.add(PTGiphyChannel.getInstance());
            }
        }

        super.loadChannels(mVideoChannels);
    }
}
