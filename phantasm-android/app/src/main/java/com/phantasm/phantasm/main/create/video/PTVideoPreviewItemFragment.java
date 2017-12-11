package com.phantasm.phantasm.main.create.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.danikula.videocache.CacheListener;
import com.gpit.android.logger.RemoteLogger;
import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.main.PTPreloadedBaseFragment;
import com.phantasm.phantasm.main.flick.PTDraggableVideoView;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;

import junit.framework.Assert;

import java.io.File;

public class PTVideoPreviewItemFragment extends PTPreloadedBaseFragment implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener, View.OnTouchListener, CacheListener {
    public static final String TAG = PTVideoPreviewItemFragment.class.getSimpleName();

    // private static final int MIN_REQUIRED_BUFFERING_PERCENT = 10;

    private PTDraggableVideoView mVideoView;
    private ImageView mIVError;
    private ProgressBar mProgressBar;

    private PTBaseMediaObject mMedia;
    private int mPosition;
    private boolean mVisibleForUser;
    private boolean mIsPrepared = false;
    private boolean mIsPaused;

    public PTVideoPreviewItemFragment() {
    }

    public static PTVideoPreviewItemFragment newInstance(PTBaseMediaObject media) {
        Assert.assertNotNull(media);

        PTVideoPreviewItemFragment fragment = new PTVideoPreviewItemFragment();
        fragment.setMedia(media);

        return fragment;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_preview_video_item;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    @Override
    protected void initUI() {
        mVideoView = (PTDraggableVideoView) findViewById(R.id.videoView);
        mIVError = (ImageView) findViewById(R.id.ivError);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mVideoView.setKeepScreenOn(true);
        mVideoView.setEnableTouchPause(false);
        mVideoView.setOnPreparedListener(this);
        // There is a bug in looping api, so lets loop manually
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnTouchListener(this);

        reloadPlayer();
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    @Override
    public void onDestroy() {
        PTApp.getInstance().getHttpProxy().unregisterCacheListener(this);

        super.onDestroy();
    }


    public void setMedia(PTBaseMediaObject media) {
        if (mMedia != null && mMedia.equals(media)) return;

        mMedia = media;
        reloadPlayer();
    }

    private void reloadPlayer() {
        if (mVideoView == null) return;

        resetPlayer();

        Uri uri = Uri.parse(mMedia.getURL());
        RemoteLogger.d(TAG, "Play video: " + uri);
        // mVideoView.setVideoURI(uri);

        // Not working well in some case. Lets check again and enable it
        mVideoView.setVideoPath(PTApp.getInstance().getHttpProxy().getProxyUrl(uri.toString()));

        mProgressBar.setVisibility(View.VISIBLE);
        mIVError.setVisibility(View.GONE);

        if (mVisibleForUser) {
            startPlayer();
        }
    }

    private void startPlayer() {
        if (!mVisibleForUser || !mIsPrepared) return;

        if (mVideoView.isPlaying()) return;

        // mVideoView.seekTo(mPosition);

        mVideoView.start();
        mIsPaused = false;
    }

    private void resetPlayer() {
        mVideoView.stop();
        mVideoView.reset();

        mIsPrepared = false;
        mIsPaused = true;
    }

    @Override
    protected void onVisible(boolean isVisibleToUser) {
        super.onVisible(isVisibleToUser);

        mVisibleForUser = isVisibleToUser;
        if (mVideoView != null) {
            if (mVisibleForUser) {
                startPlayer();
            } else if (mVideoView.isPlaying()) {
                mVideoView.pause();
                mPosition = mVideoView.getCurrentPosition();
                mIsPaused = true;
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
    public void onCompletion(MediaPlayer mp) {
        mp.pause();
        mp.seekTo(0);
        mp.start();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (mIVError.getVisibility() == View.VISIBLE) {
                reloadPlayer();
                return true;
            }
        }

        return false;
    }
}
