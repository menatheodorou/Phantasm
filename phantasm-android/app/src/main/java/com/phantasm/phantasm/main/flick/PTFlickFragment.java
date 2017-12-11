package com.phantasm.phantasm.main.flick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gpit.android.webapi.OnCommonAPICompleteListener;
import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.PTSettings;
import com.phantasm.phantasm.common.ui.PTUIUtils;
import com.phantasm.phantasm.common.ui.widgets.OnSlidingLayoutInterface;
import com.phantasm.phantasm.common.ui.widgets.PTVerticalSlidingLayout;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.PTTabBaseFragment;
import com.phantasm.phantasm.main.api.PTBaseMediaListAPI;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.model.PTTabID;
import com.phantasm.phantasm.main.ui.OnChannelSelectedListener;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;
import com.phantasm.phantasm.main.ui.PTChooseChannelGridAdapter;
import com.phantasm.phantasm.main.ui.PTVideoChooseChannelView;

import junit.framework.Assert;

import java.util.List;

public class PTFlickFragment extends PTTabBaseFragment implements OnSlidingLayoutInterface,
        OnChannelSelectedListener, View.OnClickListener {
    private final static String FRAGMENT_TAG_FLICK_VIDEO_VIEW = "flick_video_view";
    private static final int RESTORE_CHANNEL_DELAY_TIME = 500;

    private PTVerticalSlidingLayout mVGSlidingLayout;
    private ViewGroup mVGVideoFlickView;
    private PTFlickVideoFragment mFlickVideoFragment;
    private ImageButton mIBShare;
    private PTVideoChooseChannelView mVGChooseChannel;

    private ProgressBar mProgressBar;

    private ViewGroup mVGVideoController;
    private ImageView mIVChannelIcon;
    private TextView mTVVideoTitle;
    private ImageButton mIBPlay;
    private boolean mFirstVisible = true;

    private Bitmap mDefaultChannelBitmap;

    public PTFlickFragment() {
        super(PTTabID.TAB_FLICK);
    }

    public static PTFlickFragment newInstance() {
        return new PTFlickFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_flick;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mDefaultChannelBitmap = ((BitmapDrawable)getResources().getDrawable(
                R.drawable.ic_default_channel, null)).getBitmap();
    }

    @Override
    protected void initUI() {
        initActionBar();

        mContentView.addOnLayoutChangeListener(mLayoutChangeListener);

        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.progressBar);

        mVGSlidingLayout = (PTVerticalSlidingLayout) mContentView.findViewById(R.id.vgSlidingLayout);
        mVGSlidingLayout.setSlidingLayoutInterface(this);

        mVGVideoFlickView = (ViewGroup) findViewById(R.id.vgVideoFlickView);
        mFlickVideoFragment = PTFlickVideoFragment.newInstance(this);
        mFlickVideoFragment.setUserVisibleHint(getUserVisibleHint());
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mFragmentManager.findFragmentById(R.id.fragment_video) == null) {
            ft.add(R.id.vgVideoFlickView, mFlickVideoFragment, FRAGMENT_TAG_FLICK_VIDEO_VIEW);
        }
        ft.commit();
        mFragmentManager.executePendingTransactions();

        mIBShare = (ImageButton) findViewById(R.id.ibShare);
        mIBShare.setOnClickListener(this);

        mVGChooseChannel = (PTVideoChooseChannelView) findViewById(R.id.vgChooseChannelView);
        mVGChooseChannel.setVMAChannelOnly(true);
        mVGChooseChannel.setMediaType(PTMediaType.MediaTypeAudioVideo);
        mVGChooseChannel.setOnChannelSelectedListener(this);

        mVGVideoController = (ViewGroup) findViewById(R.id.vgVideoController);
        mVGVideoController.addOnLayoutChangeListener(mLayoutChangeListener);
        mIVChannelIcon = (ImageView) mVGVideoController.findViewById(R.id.ivChannel);
        mTVVideoTitle = (TextView) mVGVideoController.findViewById(R.id.tvTitle);
        mIBPlay = (ImageButton) mVGVideoController.findViewById(R.id.ibPlayPause);
        mIBPlay.setOnClickListener(this);

        // Restore latest channel
        restoreLatestChannel();

        updateUI();
    }

    private void restoreLatestChannel() {
        final PTChannel latestChannel = PTSettings.getInstance(getContext()).getLatestChannel(
                PTMediaType.MediaTypeAudioVideo);
        // Lets have delay till all of ui objects are created
        if (latestChannel != null) {
            mVGChooseChannel.selectChannel(latestChannel);
        }
    }

    private void initActionBar() {
        setHasOptionsMenu(false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    @Override
    protected void registerReceiver() {
        IntentFilter filter = new IntentFilter(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
        getContext().registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void unregisterReceiver() {
        getContext().unregisterReceiver(mMessageReceiver);
    }

    private void updateUI() {
        if (mFlickVideoFragment == null) return;

        PTBaseMediaObject media = mFlickVideoFragment.getCurrentMedia();
        if (media == null) {
            mVGVideoController.setVisibility(View.GONE);
        } else {
            Assert.assertTrue(mVGChooseChannel.getSelectedChannel() != null);

            mVGVideoController.setVisibility(View.VISIBLE);

            // Update video controller
            PTUIUtils.loadImage(mIVChannelIcon,
                    mVGChooseChannel.getSelectedChannel().getAvatarURL(PTChannel.CHANNEL_IMAGE_WIDTH, PTChannel.CHANNEL_IMAGE_HEIGHT),
                    mDefaultChannelBitmap, PTChooseChannelGridAdapter.getRoundedImageOption());
            mTVVideoTitle.setText(media.getTitle());
            if (mFlickVideoFragment.isPlaying()) {
                mIBPlay.setImageResource(R.drawable.ic_action_pause);
                mIBPlay.setTag(R.drawable.ic_action_pause);
            } else {
                mIBPlay.setImageResource(R.drawable.ic_action_play);
                mIBPlay.setTag(R.drawable.ic_action_play);
            }
        }

        updateLayout(false);
    }

    public void updateLayout(boolean minimize) {
        int fragmentHeight = mContentView.getMeasuredHeight();
        int slidingLayoutHeight = mVGSlidingLayout.getMeasuredHeight();
        int channelListHeight = mVGChooseChannel.getMeasuredHeight();
        int videoControllerHeight = mVGVideoController.getMeasuredHeight();
        if (fragmentHeight == 0 || slidingLayoutHeight == 0 || channelListHeight == 0 || videoControllerHeight == 0) return;

        boolean isChanged = false;
        FrameLayout.LayoutParams slidingLayoutParam = (FrameLayout.LayoutParams) mVGSlidingLayout.getLayoutParams();
        int diffHeight = slidingLayoutHeight - channelListHeight;

        // Check bottom
        if (minimize || ((slidingLayoutParam.topMargin + videoControllerHeight + diffHeight) > fragmentHeight)) {
            slidingLayoutParam.topMargin = fragmentHeight - videoControllerHeight - diffHeight;
            isChanged = true;
        }

        // Check top
        if (slidingLayoutParam.topMargin < 0) {
            slidingLayoutParam.topMargin = 0;
            isChanged = true;
        }

        if (isChanged) mVGSlidingLayout.setLayoutParams(slidingLayoutParam);
    }

    @Override
    public void onPause() {
        super.onPause();

        PTApp.getInstance().lossAudioDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        PTApp.getInstance().gainAudioDevice();
    }

    @Override
    public void onVisible(boolean isVisibleToUser) {
        super.onVisible(isVisibleToUser);

        // I don't understand why its not working in sub fragment
        if (mFlickVideoFragment != null) mFlickVideoFragment.onVisible(isVisibleToUser);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
    }


    /********************** Event Listener ************************/
    @Override
    public void onChannelLoaded(List<PTChannel> channels) {
        if (channels != null && channels.size() > 0) {
            // Auto select first channel
            if (mVGChooseChannel.getSelectedChannel() == null) {
                mVGChooseChannel.selectChannel(channels.get(0));
            }
        }
    }

    @Override
    public void onChannelSelected(PTChannel channel) {
        PTSettings.getInstance(getContext()).saveLatestChannel(channel, PTMediaType.MediaTypeAudioVideo);

        if (channel != null) {
            // Search media
            showProgressBar();
            channel.searchMedia(getActivity(), PTMediaType.MediaTypeAudioVideo,
                    null, 0, PTConst.MAX_MEDIA_COUNT, new OnCommonAPICompleteListener<PTWebAPI>(getActivity()) {
                        @Override
                        public void onCompleted(PTWebAPI webapi) {
                            hideProgressBar();

                            // Switch to media list view
                            PTBaseMediaResponse media = ((PTBaseMediaListAPI) webapi).mediaResponse;
                            mFlickVideoFragment.setMedia(media);
                        }

                        public void onFailed(PTWebAPI webapi) {
                            super.onFailed(webapi);
                            hideProgressBar();
                        }

                        public void onCanceled(PTWebAPI webapi) {
                            super.onCanceled(webapi);
                            hideProgressBar();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mIBPlay) {
            int resId = (Integer) mIBPlay.getTag();
            if (resId == R.drawable.ic_action_play) {
                mFlickVideoFragment.play();
            } else {
                Assert.assertTrue(resId == R.drawable.ic_action_pause);
                mFlickVideoFragment.pause();
            }
        } else if (v == mIBShare) {
            PTBaseMediaObject currentMedia = mFlickVideoFragment.getCurrentMedia();
            if (currentMedia != null) {
                // Share with mp4
                PTShareTask shareTask = new PTShareTask(getActivity(), currentMedia.getURL());
                shareTask.execute();
                /* Share with URL
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentMedia.getURL());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                */
            }
        }
    }

    private View.OnLayoutChangeListener mLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            int height = bottom - top;

            if(height > 0) {
                if(v == mVGVideoController) {
                    updateLayout(false);
                } else if(v == mContentView && mFirstVisible) {
                    mFirstVisible = false;

                    // Reset top position of sliding layout
                    FrameLayout.LayoutParams layoutParam = (FrameLayout.LayoutParams) mVGSlidingLayout.getLayoutParams();
                    int slidingTopPos = layoutParam.topMargin = (int) ((float)height *
                            PTConst.SLIDNIG_LAYOUT_INITIAL_TOP_PERCENT / 100);
                    mVGSlidingLayout.setLayoutParams(layoutParam);

                    // Resize video preview fragment
                    layoutParam = (FrameLayout.LayoutParams) mVGVideoFlickView.getLayoutParams();
                    layoutParam.height = slidingTopPos;
                    mVGVideoFlickView.setLayoutParams(layoutParam);
                }
            }
        }
    };

    @Override
    public boolean canScrollVertically(int direction) {
        return mVGChooseChannel.canScrollVertically(direction);
    }

    @Override
    public void updateLayout() {
        updateLayout(false);
    }

    @Override
    public void swipLeft() {
    }

    @Override
    public void swipRight() {
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isVisible()) return;

            String action = intent.getAction();

            if (action.equals(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED)) {
                PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(
                        PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);
                if (mediaType == PTMediaType.MediaTypeAudioVideo &&
                        intent.hasExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA)) {
                    updateUI();
                }
            }
        }
    };
}
