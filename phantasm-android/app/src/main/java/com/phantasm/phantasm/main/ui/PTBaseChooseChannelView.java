package com.phantasm.phantasm.main.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.gpit.android.util.UiUtils;
import com.gpit.android.webapi.OnCommonAPICompleteListener;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.widgets.PTBaseCustomView;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.api.vma.PTVMAChannelListAPI;
import com.phantasm.phantasm.main.create.PTBaseChooseContentFragment;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTMediaType;

import java.util.ArrayList;
import java.util.List;

public class PTBaseChooseChannelView extends PTBaseCustomView implements AdapterView.OnItemClickListener {
    public final static String BROADCAST_MESSAGE_ACTION_CHANNEL_SELECTED = "action_channel_selected";
    public final static String BROADCAST_MESSAGE_PARAM_MEDIA_TYPE = "param_media_type";
    public final static String BROADCAST_MESSAGE_PARAM_SELECTED_CHANNEL = "param_channel";

    private static final int CHANNEL_LIMIT = 200;

    protected ViewGroup mVGRoot;
    private ProgressBar mProgressBar;
    private GridView mGridView;
    private PTChooseChannelGridAdapter mGridAdapter;

    protected PTBaseChooseContentFragment mChooseContentFragment;
    protected PTMediaType mMediaType = PTMediaType.MediaTypeAudioOnly;

    protected static List<PTChannel> channels;
    protected PTChannel mSelectedChannel;

    protected List<PTChannel> mFilteredChannels = new ArrayList<>();
    protected String mSearchKey = null;

    protected OnChannelSelectedListener mListener;

    public PTBaseChooseChannelView(Context context) {
        super(context);
    }

    public PTBaseChooseChannelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public PTBaseChooseChannelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParentFragment(PTBaseChooseContentFragment fragment) {
        mChooseContentFragment = fragment;
        if (mChooseContentFragment != null) {
            mMediaType = mChooseContentFragment.getMediaType();
        }
    }

    public void setMediaType(PTMediaType mediaType) {
        mMediaType = mediaType;
    }

    public void selectChannel(PTChannel channel) {
        if (mSelectedChannel == channel) return;
        mSelectedChannel = channel;

        Intent intent = new Intent();
        intent.setAction(BROADCAST_MESSAGE_ACTION_CHANNEL_SELECTED);
        if (mSelectedChannel != null) {
            intent.putExtra(BROADCAST_MESSAGE_PARAM_MEDIA_TYPE, mMediaType);
            intent.putExtra(BROADCAST_MESSAGE_PARAM_SELECTED_CHANNEL, mSelectedChannel);
        }
        getContext().sendBroadcast(intent);

        if (mListener != null) {
            mListener.onChannelSelected(mSelectedChannel);
        }

    }

    public void applySearch(String searchKey) {
        if (TextUtils.equals(mSearchKey, searchKey)) return;

        mSearchKey = searchKey;

        loadChannels(channels);
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_choose_channel;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initUI() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);

        loadChannelData();
    }

    public void showProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
    }

    private void loadChannelData() {
        if (channels == null || channels.size() == 0) {
            showProgressBar();

            PTVMAChannelListAPI api = new PTVMAChannelListAPI(getContext(), 0, CHANNEL_LIMIT);
            api.showProgress(false);
            api.exec(new OnCommonAPICompleteListener<PTWebAPI>(getContext()) {
                @Override
                public void onCompleted(PTWebAPI webapi) {
                    hideProgressBar();

                    channels = ((PTVMAChannelListAPI) webapi).channels;
                    loadChannels(channels);
                }

                public void onFailed(PTWebAPI webapi) {
                    hideProgressBar();
                }

                public void onCanceled(PTWebAPI webapi) {
                    hideProgressBar();
                }
            });
        } else {
            loadChannels(channels);
        }
    }

    protected void loadChannels(List<PTChannel> channels) {
        if (channels == null) return;

        mFilteredChannels.clear();
        for (PTChannel channel : channels) {
            if (mSearchKey == null ||
                    (channel.name != null && channel.name.toLowerCase().contains(mSearchKey.toLowerCase())) ||
                    (channel.email != null && channel.email.toLowerCase().contains(mSearchKey.toLowerCase()))) {
                mFilteredChannels.add(channel);
            }
        }
        // Lets sure its initialized
        if (mGridView == null) return;

        mGridAdapter = new PTChooseChannelGridAdapter(getContext(), this, mFilteredChannels);
        mGridView.setAdapter(mGridAdapter);

        if (mListener != null) mListener.onChannelLoaded(mFilteredChannels);
    }

    public void setOnChannelSelectedListener(OnChannelSelectedListener listener) {
        mListener = listener;
    }

    public PTChannel getSelectedChannel() {
        return mSelectedChannel;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return UiUtils.canScroll(mGridView, direction);
    }

    /****************** EVENT LISTENER ***************/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setHapticFeedbackEnabled(true);
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

        PTChannel channel = (PTChannel) mGridAdapter.getItem(position);

        selectChannel(channel);
    }
}
