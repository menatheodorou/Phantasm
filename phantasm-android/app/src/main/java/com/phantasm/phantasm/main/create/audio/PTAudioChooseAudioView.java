package com.phantasm.phantasm.main.create.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gpit.android.util.UiUtils;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.create.PTBaseChooseMediaView;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import java.util.List;

public class PTAudioChooseAudioView extends PTBaseChooseMediaView implements
        AdapterView.OnItemClickListener {
    private ListView mListView;
    private PTAudioChooseAudioAdapter mListAdapter;

    public PTAudioChooseAudioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PTAudioChooseAudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PTAudioChooseAudioView(Context context) {
        super(context);
    }

    @Override
    public void setMedia(PTBaseMediaResponse response) {
        super.setMedia(response);

        if (mListView == null) return;

        if (mMediaResponse == null || mMediaResponse.getMediaObjects() == null) {
            mListView.setAdapter(null);
        } else {
            List<PTBaseMediaObject> objects = mMediaResponse.getMediaObjects();

            mListAdapter = new PTAudioChooseAudioAdapter(getContext(), R.id.listView, objects);
            mListAdapter.setSelectedAudioObject(mSelectedMediaObject);
            mListView.setAdapter(mListAdapter);
        }
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.widget_choose_audio;
    }

    @Override
    protected void initUI() {
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);
        // mListView.setOnTouchListener(this);

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
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        updateUI();
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return UiUtils.canScroll(mListView, direction);
    }

    @Override
    protected void updateUI() {
        if (mListAdapter != null) mListAdapter.setSelectedAudioObject(mSelectedMediaObject);
    }

    /****************** Event Listener ***************/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PTBaseMediaObject mediaObject = mMediaResponse.getMediaObjects().get(position);
        onMediaSelected(mediaObject);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED)) {
                PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(
                        PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);
                if (mediaType == PTMediaType.MediaTypeAudioOnly && intent.hasExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA)) {
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
