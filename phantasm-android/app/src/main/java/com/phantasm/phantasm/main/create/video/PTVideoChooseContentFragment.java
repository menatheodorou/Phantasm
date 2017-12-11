package com.phantasm.phantasm.main.create.video;

import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;
import com.phantasm.phantasm.main.create.PTBaseChooseContentFragment;
import com.phantasm.phantasm.main.create.PTBaseChooseMediaView;
import com.phantasm.phantasm.main.ui.PTVideoChooseChannelView;

import java.util.List;

public class PTVideoChooseContentFragment extends PTBaseChooseContentFragment {
    public PTVideoChooseContentFragment() {
        super(PTMediaType.MediaTypeVideoOnly);
    }

    @Override
    public PTBaseChooseChannelView createChooseChannelView() {
        return new PTVideoChooseChannelView(getContext());
    }

    @Override
    public PTBaseChooseMediaView createChooseMediaView() {
        return new PTVideoChooseVideoView(getContext());
    }

    @Override
    public void setMedia(PTBaseMediaResponse media) {
        super.setMedia(media);
    }

    @Override
    public void onChannelLoaded(List<PTChannel> channels) {

    }
}
