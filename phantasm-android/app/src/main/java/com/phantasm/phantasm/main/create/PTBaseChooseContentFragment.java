package com.phantasm.phantasm.main.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gpit.android.util.UiUtils;
import com.gpit.android.webapi.OnCommonAPICompleteListener;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.PTSettings;
import com.phantasm.phantasm.common.ui.widgets.PTBaseCustomView;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.PTPreloadedBaseFragment;
import com.phantasm.phantasm.main.api.PTBaseMediaListAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.OnChannelSelectedListener;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import junit.framework.Assert;

public abstract class PTBaseChooseContentFragment extends PTPreloadedBaseFragment implements OnChannelSelectedListener {
    protected ViewGroup mVGContent;
    private ProgressBar mProgressBar;
    protected PTBaseChooseChannelView mChooseChannelView;
    protected PTBaseChooseMediaView mChooseMediaView;

    protected PTCreateFragment mParentFragment;

    protected PTMediaType mMediaType = PTMediaType.MediaTypeAudioOnly;
    private PTMediaChooseMode mMediaChooseMode;

    private boolean mFirstVisible = true;

    public abstract PTBaseChooseChannelView createChooseChannelView();
    public abstract PTBaseChooseMediaView createChooseMediaView();

    public PTBaseChooseContentFragment(PTMediaType mediaType) {
        mMediaType = mediaType;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_choose_content;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void reload(Bundle bundle) {
    }

    @Override
    protected void initUI() {
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.progressBar);
        mVGContent = (ViewGroup) mContentView.findViewById(R.id.vgContent);

        mChooseChannelView = null;
        setMediaChooseMode(PTMediaChooseMode.CHOOSE_CHANNEL);
    }

    @Override
    protected void onVisible(boolean isVisibleToUser) {
        super.onVisible(isVisibleToUser);

        if (mFirstVisible && isVisibleToUser) {
            mFirstVisible = false;

            restoreLatestChannel();
        }
    }

    private void restoreLatestChannel() {
        final PTChannel latestChannel = PTSettings.getInstance(getContext()).getLatestChannel(mMediaType);
        // Lets have delay till all of ui objects are created
        if (latestChannel != null) {
            getChooseChannelView().selectChannel(latestChannel);
        }
    }

    protected PTBaseChooseChannelView getChooseChannelView() {
        if (mChooseChannelView == null) {
            mChooseChannelView = createChooseChannelView();
            mChooseChannelView.setParentFragment(this);
        }
        mChooseChannelView.setOnChannelSelectedListener(this);

        return mChooseChannelView;
    }

    protected PTBaseChooseMediaView getChooseMediaView() {
        if (mChooseMediaView == null) {
            mChooseMediaView = createChooseMediaView();
            mChooseMediaView.setParentFragment(this);
        }

        return mChooseMediaView;
    }

    public void setParentFragment(PTCreateFragment fragment) {
        mParentFragment = fragment;
    }

    public PTMediaType getMediaType() {
        return mMediaType;
    }

    public void setMedia(PTBaseMediaResponse media) {
        // Broadcast list of media
        Intent intent = new Intent();
        intent.setAction(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_LIST_LOADED);
        if (media != null) {
            intent.putExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA_LIST, media);
            intent.putExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE, mMediaType);
        }
        getContext().sendBroadcast(intent);

        if (mChooseMediaView != null) {
            mChooseMediaView.setMedia(media);
        }

    }

    public PTChannel getSelectedChannel() {
        PTChannel channel = null;

        if (getChooseChannelView() != null) {
            channel = getChooseChannelView().getSelectedChannel();
        }

        return channel;
    }

    @Nullable
    public PTBaseMediaObject getCurrentSelectedObject() {
        if (mChooseMediaView != null) {
            return mChooseMediaView.getSelectedMediaObject();
        }

        return null;
    }

    public PTMediaChooseMode getMediaChooseMode() {
        return mMediaChooseMode;
    }

    public void setMediaChooseMode(PTMediaChooseMode chooseMode) {
        PTBaseCustomView chooseView = null;

        mVGContent.removeAllViews();

        mMediaChooseMode = chooseMode;
        if (mMediaChooseMode != null) {
            switch (mMediaChooseMode) {
                case CHOOSE_CHANNEL:
                    chooseView = getChooseChannelView();
                    mChooseChannelView.selectChannel(null);
                    break;
                case CHOOSE_MEDIA:
                    chooseView = getChooseMediaView();
                    mChooseMediaView.setMedia(null);
                    break;
                default:
                    Assert.assertTrue("Unsupported choose mode" == null);
            }

            UiUtils.removeParentView(chooseView);
            mVGContent.addView(chooseView);
        }
    }

    public void search(String keyword) {
        search(keyword, getMediaType());
    }

    public void search(String keyword, PTMediaType mediaType) {
        // Search channel or media
        if (getMediaChooseMode() == PTMediaChooseMode.CHOOSE_CHANNEL) {
            if (mChooseChannelView != null) mChooseChannelView.applySearch(keyword);
        } else {
            // Search media per selected channel
            final PTChannel channel = getSelectedChannel();
            if (channel != null) {
                // Search media
                showProgressBar();
                channel.searchMedia(getActivity(), mediaType,
                        keyword, 0, PTConst.MAX_MEDIA_COUNT, new OnCommonAPICompleteListener<PTWebAPI>(getActivity()) {
                            @Override
                            public void onCompleted(PTWebAPI webapi) {
                                if (channel != getSelectedChannel()) return;

                                hideProgressBar();

                                // Switch to media list view
                                PTBaseMediaResponse media = ((PTBaseMediaListAPI) webapi).mediaResponse;
                                setMedia(media);
                            }

                            public void onFailed(PTWebAPI webapi) {
                                super.onFailed(webapi);

                                if (channel != getSelectedChannel()) return;

                                hideProgressBar();
                            }

                            public void onCanceled(PTWebAPI webapi) {
                                super.onCanceled(webapi);

                                if (channel != getSelectedChannel()) return;

                                hideProgressBar();
                            }
                        });
            }
        }
    }

    public void showProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
    }

    public boolean canScrollVertically(int direction) {
        if (mMediaChooseMode != null) {
            switch (mMediaChooseMode) {
                case CHOOSE_CHANNEL:
                    return mChooseChannelView.canScrollVertically(direction);
                case CHOOSE_MEDIA:
                    return mChooseMediaView.canScrollVertically(direction);
                default:
                    Assert.assertTrue("Unsupported choose mode" == null);
            }
        }

        return false;
    }

    // Callback touch event from child when it should be handled in parent
    public boolean onTouch(View v, MotionEvent event) {
        return mParentFragment.onTouch(v, event);
    }

    /*********************** Callback ********************/
    @Override
    public boolean onBackPressed() {
        if (!super.onBackPressed()) {
            if (mMediaChooseMode == PTMediaChooseMode.CHOOSE_MEDIA) {
                setMediaChooseMode(PTMediaChooseMode.CHOOSE_CHANNEL);

                return true;
            }
        }

        return false;
    }

    @Override
    public void onChannelSelected(PTChannel channel) {
        PTSettings.getInstance(getContext()).saveLatestChannel(channel, mMediaType);

        if (channel == null) return;

        setMediaChooseMode(PTMediaChooseMode.CHOOSE_MEDIA);
        search("");
    }
}
