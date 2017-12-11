package com.phantasm.phantasm.main.create.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gpit.android.util.StringUtils;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.ui.widgets.PTBaseCustomView;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.create.PTBasePreviewUX;
import com.phantasm.phantasm.main.create.PTCreateFragment;
import com.phantasm.phantasm.main.model.PTBaseAudioObject;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import java.io.IOException;
import java.util.List;

public class PTAudioControllerView extends PTBaseCustomView implements PTBasePreviewUX,
        PTAudioPlayerService.AudioPlayerCallbacks {

    private PTCreateFragment mParentFragment;

    private ViewGroup mVGRoot;
    private ImageView mIVSeparateLine;
    private ViewGroup mVGHeadBar;
    private ViewGroup mVGControlBar;

    private TextView mTVTitle;
    private TextView mTVAuthor;
    private ImageButton mIBArrow;
    private ImageButton mIBTopPause;

    private SeekBar mSBProgress;
    private ImageButton mIBPrev;
    private ImageButton mIBPlay;
    private ImageButton mIBNext;
    private TextView mTVProgress;
    private TextView mTVDuration;

    private PTBaseMediaResponse mMediaResponse;
    private PTBaseAudioObject mSelectedMedia;

    private PTAudioPlayerService mAudioPlayerService;

    private boolean mExpandedPlayControl = false;

    private boolean mStopped = false;

    public PTAudioControllerView(Context context) {
        super(context);
    }

    public PTAudioControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PTAudioControllerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParentFragment(PTCreateFragment fragment) {
        mParentFragment = fragment;
    }

    @Override
    public void setMedia(PTBaseMediaResponse media) {
        mSelectedMedia = null;
        mMediaResponse = media;
    }

    @Override
    public PTBaseMediaObject getCurrentPreviewObject() {
        return mSelectedMedia;
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.widget_audio_controller;
    }

    @Override
    protected void initData() {
        super.initData();

        mAudioPlayerService = PTAudioPlayerService.getInstance(getContext());
    }

    @Override
    public void initUI() {
        mVGRoot = (ViewGroup) findViewById(R.id.vgRoot);
        mIVSeparateLine = (ImageView) findViewById(R.id.ivSeparateLine);
        mVGHeadBar = (ViewGroup) findViewById(R.id.vgHeaderBar);
        mVGControlBar = (ViewGroup) findViewById(R.id.vgControlBar);

        mTVTitle = (TextView) findViewById(R.id.tvTitle);
        mTVAuthor = (TextView) findViewById(R.id.tvAuthor);

        mIBArrow = (ImageButton) findViewById(R.id.ibArrow);
        mIBArrow.setOnClickListener(mArrowClickListener);

        mIBTopPause = (ImageButton) findViewById(R.id.ibTopPause);
        mIBTopPause.setOnClickListener(mTopPauseClickListener);

        mSBProgress = (SeekBar) findViewById(R.id.sbProgress);
        mSBProgress.getProgressDrawable().setColorFilter(
                new PorterDuffColorFilter(getResources().getColor(R.color.app_green_color),
                        PorterDuff.Mode.MULTIPLY));
        mSBProgress.setOnSeekBarChangeListener(mSeekBarChangeListener);

        mIBPrev = (ImageButton) findViewById(R.id.ibPrev);
        mIBPrev.setOnClickListener(mPrevClickListener);

        mIBPlay = (ImageButton) findViewById(R.id.ibPlay);
        mIBPlay.setOnClickListener(mPlayClickListener);

        mIBNext = (ImageButton) findViewById(R.id.ibNext);
        mIBNext.setOnClickListener(mNextClickListener);

        mTVProgress = (TextView) findViewById(R.id.tvProgress);
        mTVDuration = (TextView) findViewById(R.id.tvDuration);

        updateUI();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        registerReceiver();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        unregisterReceiver();
    }

    protected void registerReceiver() {
        IntentFilter filter = new IntentFilter(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_LIST_LOADED);
        getContext().registerReceiver(mMessageReceiver, filter);

        filter = new IntentFilter(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
        getContext().registerReceiver(mMessageReceiver, filter);
    }

    protected void unregisterReceiver() {
        getContext().unregisterReceiver(mMessageReceiver);
    }

    private void updateUI() {
        if (mVGControlBar == null) return;

        if (mExpandedPlayControl) {
            setBackgroundColor(getResources().getColor(R.color.drag_panel_color));
            mIVSeparateLine.setVisibility(View.VISIBLE);
            mVGControlBar.setVisibility(View.VISIBLE);
            mIBTopPause.setVisibility(View.INVISIBLE);

            mIBArrow.setImageResource(R.drawable.ic_arrow_down);
        } else {
            setBackgroundColor(getResources().getColor(R.color.media_controller_widget_bg_color));
            mIBTopPause.setVisibility(View.VISIBLE);
            mIVSeparateLine.setVisibility(View.GONE);
            mVGControlBar.setVisibility(View.GONE);
            mIBArrow.setImageResource(R.drawable.ic_arrow_up);
        }

        if (mSelectedMedia != null) {
            mTVTitle.setText(mSelectedMedia.getTitle());
            mTVAuthor.setText(mSelectedMedia.getAuthor());
        }
        if (StringUtils.isNullOrEmpty(mTVAuthor.getText().toString())) {
            mTVAuthor.setVisibility(View.GONE);
        } else {
            mTVAuthor.setVisibility(View.VISIBLE);
        }

        mSBProgress.setMax(mAudioPlayerService.getTrackLength());
        mSBProgress.setProgress(mAudioPlayerService.getProgress());

        mTVDuration.setText(String.format("%s", getTimeString(mSBProgress.getMax())));
        mTVProgress.setText(String.format("%s", getTimeString(mSBProgress.getProgress())));

        if (mAudioPlayerService.isPrepared() && mAudioPlayerService.isPlaying()) {
            mIBTopPause.setImageResource(R.drawable.ic_action_playback_pause);
            mIBPlay.setImageResource(R.drawable.ic_action_playback_pause);
        } else {
            mIBTopPause.setImageResource(R.drawable.ic_action_playback_play);
            mIBPlay.setImageResource(R.drawable.ic_action_playback_play);
        }

        if (mSelectedMedia == null) {
            setVisibility(View.INVISIBLE);
        } else {
            setVisibility(View.VISIBLE);
        }
    }

    public boolean next() {
        if (mMediaResponse == null || mMediaResponse.getMediaObjects() == null ||
                mMediaResponse.getMediaObjects().size() == 0) return false;

        List<PTBaseMediaObject> objects = mMediaResponse.getMediaObjects();
        PTBaseAudioObject nextObject;
        if (mSelectedMedia == null) {
            nextObject = (PTBaseAudioObject) objects.get(0);
        } else {
            int index = objects.indexOf(mSelectedMedia);
            index++;
            if (index < objects.size()) {
                nextObject = (PTBaseAudioObject) objects.get(index);
            } else {
                return false;
            }
        }

        return play(nextObject);
    }

    public boolean prev() {
        if (mMediaResponse == null || mMediaResponse.getMediaObjects() == null ||
                mMediaResponse.getMediaObjects().size() == 0) return false;

        List<PTBaseMediaObject> objects = mMediaResponse.getMediaObjects();
        PTBaseAudioObject prevObject;
        if (mSelectedMedia == null) {
            prevObject = (PTBaseAudioObject) objects.get(0);
        } else {
            int index = objects.indexOf(mSelectedMedia);
            index--;
            if (index >= 0) {
                prevObject = (PTBaseAudioObject) mMediaResponse.getMediaObjects().get(index);
            } else {
                return false;
            }
        }

        return play(prevObject);
    }

    public void play() {
        if (mAudioPlayerService != null) {
            if (!mAudioPlayerService.resume()) {
                if (mSelectedMedia != null) {
                    play(mSelectedMedia, true);
                }
            }
        }

        updateUI();
    }

    public boolean play(PTBaseAudioObject audioObject) {
        return play(audioObject, false);
    }

    public boolean play(PTBaseAudioObject audioObject, boolean restart) {
        if (mSelectedMedia != null && mSelectedMedia.equals(audioObject) && !restart) return true;

        try {
            Intent intent = new Intent();
            intent.setAction(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
            intent.putExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA, audioObject);
            intent.putExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE,
                    PTMediaType.MediaTypeAudioOnly);
            getContext().sendBroadcast(intent);

            if (mParentFragment.isMenuVisible()) {
                mAudioPlayerService.setAudioObject(audioObject, true, this);
            }
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        mSelectedMedia = audioObject;

        updateUI();

        return true;
    }

    public void stop() {
        mStopped = true;
        if (mAudioPlayerService != null) {
            mAudioPlayerService.stop();
        }

        updateUI();
    }

    private String getTimeString(long duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (int) ((duration / 1000) - (minutes * 60));

        return minutes + ":" + String.format("%02d", seconds);
    }

    /******************** Event Listener ********************/
    private View.OnClickListener mArrowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mExpandedPlayControl = !mExpandedPlayControl;

            updateUI();
        }
    };

    private View.OnClickListener mTopPauseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPlayClickListener.onClick(v);
        }
    };

    private View.OnClickListener mPrevClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            prev();
        }
    };

    private View.OnClickListener mPlayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mAudioPlayerService.isPlaying()) {
                mAudioPlayerService.stop();
            } else {
                mAudioPlayerService.play();
            }

            updateUI();
        }
    };

    private View.OnClickListener mNextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            next();
        }
    };

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) mAudioPlayerService.seek(mSBProgress.getProgress());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /******************** AUDIO CALLBACK ********************/
    @Override
    public void onStartLoading() {
        updateUI();
    }

    @Override
    public void onAudioPrepared() {
        updateUI();
    }

    @Override
    public void onAudioStarted(int trackLength) {
        updateUI();
    }

    @Override
    public void onAudioProgressUpdate(int progress) {
        updateUI();
    }

    @Override
    public void onAudioPaused() {
        updateUI();
    }

    @Override
    public void onAudioResumed() {
        updateUI();
    }

    @Override
    public void onAudioComplete() {
        updateUI();

        mAudioPlayerService.stop();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_LIST_LOADED)) {
                if (intent.hasExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA_LIST)) {
                    PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(
                            PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);
                    if (mediaType == PTMediaType.MediaTypeAudioOnly) {
                        PTBaseMediaResponse mediaList = (PTBaseMediaResponse) intent.
                                getSerializableExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA_LIST);
                        setMedia(mediaList);
                    }
                }
            } else if (action.equals(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED)) {
                PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(
                        PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);
                if (mediaType == PTMediaType.MediaTypeAudioOnly && intent.hasExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA)) {
                    PTBaseMediaObject mediaObject = (PTBaseMediaObject) intent.
                            getSerializableExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA);
                    play((PTBaseAudioObject) mediaObject);
                }
            }
        }
    };
}
