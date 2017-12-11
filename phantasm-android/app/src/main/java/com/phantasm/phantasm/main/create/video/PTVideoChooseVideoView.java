package com.phantasm.phantasm.main.create.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gpit.android.util.UiUtils;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.create.PTBaseChooseMediaView;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import junit.framework.Assert;

public class PTVideoChooseVideoView extends PTBaseChooseMediaView {
    private GridView mGVVideo;
    private PTVideoChooseVideoAdapter mAdapter;

    public PTVideoChooseVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PTVideoChooseVideoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.widget_choose_video;
    }

    @Override
    protected void initUI() {
        mGVVideo = (GridView) findViewById(R.id.gvVideo);
        mGVVideo.setOnItemClickListener(mGridItemClickListener);

        updateUI();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        registerReceiver();

        updateUI();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        unregisterReceiver();
    }

    protected void registerReceiver() {
        IntentFilter filter = new IntentFilter(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
        getContext().registerReceiver(mMessageReceiver, filter);
    }

    protected void unregisterReceiver() {
        getContext().unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void updateUI() {
        if (mSelectedMediaObject == null) return;

        int index = mMediaResponse.getMediaObjects().indexOf(mSelectedMediaObject);
        Assert.assertTrue(index >= 0);

        mGVVideo.requestFocusFromTouch();
        mGVVideo.setItemChecked(index, true);
        mGVVideo.setSelection(index);

        if (mAdapter != null) mAdapter.setSelectedAudioObject(mSelectedMediaObject);
    }

    @Override
    public void setMedia(PTBaseMediaResponse response) {
        super.setMedia(response);

        if (mGVVideo == null) return;

        if (mMediaResponse == null || mMediaResponse.getMediaObjects() == null) {
            mGVVideo.setAdapter(null);
        } else {
            mAdapter = new PTVideoChooseVideoAdapter(getContext(), R.id.gvVideo,
                    response.getMediaObjects());
            mAdapter.setSelectedAudioObject(mSelectedMediaObject);
            mGVVideo.setAdapter(mAdapter);
        }
    }

    public PTVideoChooseVideoView(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return UiUtils.canScroll(mGVVideo, direction);
    }

    /****************** Event Listener ***************/
    private AdapterView.OnItemClickListener mGridItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PTBaseMediaObject mediaObject = mMediaResponse.getMediaObjects().get(position);
            onMediaSelected(mediaObject);
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED)) {
                PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(
                        PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);
                if (mediaType == PTMediaType.MediaTypeVideoOnly &&
                        intent.hasExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA)) {
                    PTBaseMediaObject mediaObject = (PTBaseMediaObject) intent.
                            getSerializableExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA);

                    if (mSelectedMediaObject != null && mSelectedMediaObject.equals(mediaObject)) {
                        return;
                    }

                    int index = mMediaResponse.getMediaObjects().indexOf(mediaObject);
                    if (index >= 0) {
                        onMediaSelected(mediaObject);
                    }
                }
            }
        }
    };

}
