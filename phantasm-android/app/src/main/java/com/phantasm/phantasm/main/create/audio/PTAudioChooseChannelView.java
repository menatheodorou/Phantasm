package com.phantasm.phantasm.main.create.audio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTSpotifyChannel;

import java.util.ArrayList;
import java.util.List;

public class PTAudioChooseChannelView extends PTBaseChooseChannelView {
    private List<PTChannel> mAudioChannels = new ArrayList<>();
    public PTAudioChooseChannelView(Context context) {
        super(context);

        init();
    }

    public PTAudioChooseChannelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PTAudioChooseChannelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private void init() {
        loadChannels(channels);
    }

    @Override
    protected void loadChannels(List<PTChannel> channels) {
        // Lets sure its initialized
        if (mAudioChannels == null) return;

        if (channels != null) {
            mAudioChannels.clear();
            mAudioChannels.addAll(channels);

            // Add spotify channel
            mAudioChannels.add(PTSpotifyChannel.getInstance());
        }

        super.loadChannels(mAudioChannels);
    }
}
