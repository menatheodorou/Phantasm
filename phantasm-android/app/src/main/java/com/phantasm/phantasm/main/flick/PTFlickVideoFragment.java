package com.phantasm.phantasm.main.flick;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.danikula.videocache.CacheListener;
import com.dreamfactory.PrefUtil;
import com.google.gson.Gson;
import com.gpit.android.logger.RemoteLogger;
import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.common.ui.widgets.OnDragEventListener;
import com.phantasm.phantasm.common.ui.widgets.PTDraggableContainer;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.create.PTBaseChooseMediaView;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import junit.framework.Assert;

import java.io.File;
import java.util.List;

public class PTFlickVideoFragment extends PTBaseFragment implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener, CacheListener, OnDragEventListener {
    public static final String TAG = PTFlickVideoFragment.class.getSimpleName();

    private static final int DRAG_SENSITIVE = 300;

    private PTBaseFragment mParentFragment;

    private PTDraggableContainer mDraggableContainer;
    private PTDraggableVideoView mVideoView;
    private ImageView mIVError;
    private ProgressBar mProgressBar;

    private PTBaseMediaResponse<PTBaseMediaObject> mMediaResponse;
    private PTBaseMediaObject mCurrentMedia;
    private int mCurrentPosition = 0;
    private boolean mPlayerStarted;
    private boolean mVisibleForUser;
    private boolean mIsPrepared = false;

    private float mStartDragX;
    private float mStartDragY;

    public static PTFlickVideoFragment newInstance(PTBaseFragment parentFragment) {
        PTFlickVideoFragment flickFragment = new PTFlickVideoFragment();
        flickFragment.mParentFragment = flickFragment;

        return flickFragment;
    }

    public PTFlickVideoFragment() {
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_flick_video_view;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    @Override
    protected void initUI() {
        mDraggableContainer = (PTDraggableContainer) findViewById(R.id.dragableContainer);
        mVideoView = (PTDraggableVideoView) findViewById(R.id.videoView);
        mIVError = (ImageView) findViewById(R.id.ivError);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mDraggableContainer.setFillAfter(true);
        mDraggableContainer.setOnDragListener(this);

        mVideoView.setKeepScreenOn(true);
        mVideoView.setEnableTouchPause(true);
        mVideoView.setOnPreparedListener(this);
        // There is a bug in looping api, so lets loop manually
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnBufferingUpdateListener(this);

        reloadPlayer();
    }

    @Override
    protected void reload(Bundle bundle) {
    }

    @Override
    public void onDestroy() {
        reset();
        PTApp.getInstance().getHttpProxy().unregisterCacheListener(this);

        super.onDestroy();
    }


    public void setMedia(PTBaseMediaResponse media) {
        mMediaResponse = media;
        if (mMediaResponse != null && mMediaResponse.getMediaObjects() != null &&
                mMediaResponse.getMediaObjects().size() > 0) {
            PTBaseMediaObject object = mMediaResponse.getMediaObjects().get(0);
            // Restore latest media from the preference
            if (!restoreLatestMedia(object.getClass())) {
                play(0);
            }
        }
    }

    private <T extends PTBaseMediaObject> boolean restoreLatestMedia(Class<T> type) {
        Assert.assertTrue(mMediaResponse != null && mMediaResponse.getMediaObjects() != null);

        Gson gson = new Gson();
        String json = PrefUtil.getString(getContext(), PTBaseChooseMediaView.PREFS_KEY_PRELOAD_AUDIO_VIDEO_ITEM);
        final PTBaseMediaObject selectedMedia = gson.fromJson(json, type);
        // Lets have delay till all of ui objects are created
        if (selectedMedia != null) {
            List<PTBaseMediaObject> mediaList = mMediaResponse.getMediaObjects();
            for (int i = 0 ; i < mediaList.size() ; i++) {
                PTBaseMediaObject object = mediaList.get(i);

                if (object.equals(selectedMedia)) {
                    play(i);
                    return true;
                }
            }
        }

        return false;
    }

    private void saveLatestMedia(PTBaseMediaObject object) {
        String prefKey = PTBaseChooseMediaView.PREFS_KEY_PRELOAD_AUDIO_VIDEO_ITEM;

        if (object != null) {
            Gson gson = new Gson();
            String json = gson.toJson(object);
            PrefUtil.putString(getContext(), prefKey, json);
        } else {
            PrefUtil.putString(getContext(), prefKey, null);
        }
    }

    public PTBaseMediaObject getCurrentMedia() {
        return mCurrentMedia;
    }

    public boolean isPlaying() {
        if (mVideoView != null && mVideoView.isPlaying()) return true;

        return false;
    }

    public void play() {
        if (mVideoView != null && !isPlaying()) {
            mVideoView.start();
        }
    }

    public void pause() {
        if (isPlaying()) {
            mVideoView.pause();
        }
    }

    public void reset() {
        mVideoView.stop();
        mVideoView.reset();
        mIsPrepared = false;
        mPlayerStarted = false;
    }

    private void playNext() {
        if (mMediaResponse == null || mMediaResponse.getMediaObjects().isEmpty()) return;

        int position;
        if (mCurrentMedia == null) {
            position = 0;
        } else {
            position = mCurrentPosition + 1;
        }

        play(position);
    }

    public void play(int position) {
        if (mMediaResponse == null || mMediaResponse.getMediaObjects().isEmpty()) return;

        List<PTBaseMediaObject> mediaList = mMediaResponse.getMediaObjects();

        mCurrentPosition = position;
        mCurrentPosition = mCurrentPosition % mediaList.size();
        mCurrentMedia = mediaList.get(mCurrentPosition);

        reloadPlayer();
    }

    private void reloadPlayer() {
        if (mVideoView == null || mCurrentMedia == null) return;

        Intent intent = new Intent();
        intent.setAction(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
        intent.putExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE,
                PTMediaType.MediaTypeAudioVideo);
        intent.putExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA, mCurrentMedia);
        getContext().sendBroadcast(intent);

        reset();

        mProgressBar.setVisibility(View.VISIBLE);
        // Not working well in some case. Lets check again and enable it
        mVideoView.setVideoPath(PTApp.getInstance().getHttpProxy().getProxyUrl(mCurrentMedia.getURL()));

        saveLatestMedia(mCurrentMedia);
    }

    private void startPlayer() {
        if (!mVisibleForUser || !mIsPrepared) return;

        // mVideoView.seekTo(mPosition);
        mVideoView.start();
        mPlayerStarted = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        onVisible(isVisibleToUser);
    }

    protected void onVisible(boolean isVisibleToUser) {
        mVisibleForUser = isVisibleToUser;
        if (mVideoView != null) {
            if (mVisibleForUser) {
                startPlayer();
            } else if (mPlayerStarted) {
                mVideoView.pause();
            }
        }
    }

    @Override
    public void onCacheAvailable(File file, String url, int percentsAvailable) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsPrepared = true;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mp.setLooping(true);
        startPlayer();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIVError.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.pause();
        mp.seekTo(0);
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        RemoteLogger.d(TAG, "setOnErrorListener: what = " + what + ", extra = " + extra);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mIVError.setVisibility(View.VISIBLE);
            }
        });

        return true;
    }

    /*************** Flicking *******************/
    @Override
    public void onDragStart(float x, float y) {
        mStartDragX = x;
        mStartDragY = y;
    }

    @Override
    public void onDragging(float x, float y) {

    }

    @Override
    public void onDragEnd(float x, float y) {
        float move = Math.max(Math.abs(mStartDragX - x), Math.abs(mStartDragY - y));
        if (move > DRAG_SENSITIVE) {
            playNext();
        }
    }
}
