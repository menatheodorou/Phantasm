package com.phantasm.phantasm.main.create.audio;

import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;
import com.phantasm.phantasm.main.create.PTBaseChooseContentFragment;
import com.phantasm.phantasm.main.create.PTBaseChooseMediaView;

import java.util.List;

public class PTAudioChooseContentFragment extends PTBaseChooseContentFragment {
    private PTAudioChooseChannelView mChooseAudioChannelView;
    private PTAudioChooseAudioView mChooseAudioView;
    public PTAudioChooseContentFragment() {
        super(PTMediaType.MediaTypeAudioOnly);
    }

    @Override
    protected void initUI() {
        super.initUI();
    }

    @Override
    public PTBaseChooseChannelView createChooseChannelView() {
        mChooseAudioChannelView = new PTAudioChooseChannelView(getContext());
        return mChooseAudioChannelView;
    }

    @Override
    public PTBaseChooseMediaView createChooseMediaView() {
        mChooseAudioView = new PTAudioChooseAudioView(getContext());
        return mChooseAudioView;
    }

    @Override
    public void onChannelLoaded(List<PTChannel> channels) {

    }
}
