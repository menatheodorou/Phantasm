package com.phantasm.phantasm.main.create;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dreamfactory.PrefUtil;
import com.google.gson.Gson;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.ui.widgets.PTBaseCustomView;
import com.phantasm.phantasm.common.ui.widgets.PTVerticalSlidingLayout;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import junit.framework.Assert;

public abstract class PTBaseChooseMediaView extends PTBaseCustomView implements
        View.OnTouchListener, GestureDetector.OnGestureListener {
    public static final String PREFS_KEY_PRELOAD_AUDIO_ITEM = "key_preload_audio_item";
    public static final String PREFS_KEY_PRELOAD_VIDEO_ITEM = "key_preload_video_item";
    public static final String PREFS_KEY_PRELOAD_AUDIO_VIDEO_ITEM = "key_preload_audio_video_item";

    protected PTBaseChooseContentFragment mChooseContentFragment;

    protected PTBaseMediaResponse<PTBaseMediaObject> mMediaResponse;
    protected PTBaseMediaObject mSelectedMediaObject;

    private GestureDetector mGestureDetector;
    private boolean mIsMovingY = false;

    public PTBaseChooseMediaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public PTBaseChooseMediaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PTBaseChooseMediaView(Context context) {
        super(context);

        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), this);
    }

    public void setParentFragment(PTBaseChooseContentFragment fragment) {
        mChooseContentFragment = fragment;
    }

    private String getPrefKey() {
        String prefKey = PREFS_KEY_PRELOAD_AUDIO_ITEM;

        switch (mChooseContentFragment.getMediaType()) {
            case MediaTypeAudioOnly:
                prefKey = PREFS_KEY_PRELOAD_AUDIO_ITEM;
                break;
            case MediaTypeVideoOnly:
                prefKey = PREFS_KEY_PRELOAD_VIDEO_ITEM;
                break;
            case MediaTypeAudioVideo:
                prefKey = PREFS_KEY_PRELOAD_AUDIO_VIDEO_ITEM;
                break;
        }

        return prefKey;
    }

    private void saveLatestMedia(PTBaseMediaObject object) {
        String prefKey = getPrefKey();

        if (object != null) {
            Gson gson = new Gson();
            String json = gson.toJson(object);
            PrefUtil.putString(getContext(), prefKey, json);
        } else {
            PrefUtil.putString(getContext(), prefKey, null);
        }
    }

    private <T extends PTBaseMediaObject> boolean restoreLatestMedia(Class<T> type) {
        Assert.assertTrue(mMediaResponse != null && mMediaResponse.getMediaObjects() != null);

        Gson gson = new Gson();
        String json = PrefUtil.getString(getContext(), getPrefKey());
        final PTBaseMediaObject selectedMedia = gson.fromJson(json, type);
        // Lets have delay till all of ui objects are created
        if (selectedMedia != null) {
            for (PTBaseMediaObject object : mMediaResponse.getMediaObjects()) {
                if (object.equals(selectedMedia)) {
                    // broadcast message to select it
                    onMediaSelected(selectedMedia);
                    return true;
                }
            }
        }

        return false;
    }

    public void setMedia(PTBaseMediaResponse response) {
        mSelectedMediaObject = null;
        mMediaResponse = response;
        if (mMediaResponse != null && mMediaResponse.getMediaObjects() != null &&
                mMediaResponse.getMediaObjects().size() > 0) {
            PTBaseMediaObject object = mMediaResponse.getMediaObjects().get(0);
            // Restore latest media from the preference
            if (!restoreLatestMedia(object.getClass())) {
                // Else lets choose first one
                onMediaSelected(object);
            }
        }
    }

    public void onMediaSelected(PTBaseMediaObject object) {
        mSelectedMediaObject = object;
        saveLatestMedia(mSelectedMediaObject);

        sendMediaSelectedMessage();

        updateUI();
    }

    protected void sendMediaSelectedMessage() {
        Intent intent = new Intent();
        intent.setAction(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
        intent.putExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE,
                mChooseContentFragment.getMediaType());
        if (mSelectedMediaObject != null) {
            intent.putExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA, mSelectedMediaObject);
        }
        getContext().sendBroadcast(intent);
    }

    @Nullable
    public PTBaseMediaObject getSelectedMediaObject() {
        return mSelectedMediaObject;
    }

    protected void updateUI() {}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);

        return result;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mIsMovingY = false;

        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int moveY = (int) (e2.getY() - e1.getY());

        if (Math.abs(moveY) > PTVerticalSlidingLayout.SENSITIVE_MOVE_Y_DISTANCE) {
            mIsMovingY = true;
        }

        if (mIsMovingY && moveY > 0) {
            boolean canScrollDown = canScrollVertically(-1);

            if (!canScrollDown) {
                // Pass this event to the parent
                mChooseContentFragment.onTouch(this, e2);
            }
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
