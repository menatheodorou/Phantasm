package com.phantasm.phantasm.main.create;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.ui.widgets.OnSlidingLayoutInterface;
import com.phantasm.phantasm.common.ui.widgets.PTVerticalSlidingLayout;
import com.phantasm.phantasm.main.PTTabBaseFragment;
import com.phantasm.phantasm.main.create.audio.PTAudioChooseContentFragment;
import com.phantasm.phantasm.main.create.audio.PTAudioControllerView;
import com.phantasm.phantasm.main.create.video.PTVideoChooseContentFragment;
import com.phantasm.phantasm.main.create.video.PTVideoPreviewFragment;
import com.phantasm.phantasm.main.model.PTBaseAudioObject;
import com.phantasm.phantasm.main.model.PTBaseVideoObject;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.model.PTSectionItemType;
import com.phantasm.phantasm.main.model.PTTabID;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;
import com.phantasm.phantasm.mux.PTMuxOperation;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;

public class PTCreateFragment extends PTTabBaseFragment implements OnSlidingLayoutInterface {
    private final static String FRAGMENT_TAG_VIDEO_PREVIEW = "video_preview";
    private final static String FRAGMENT_TAG_CHOOSE_CONTENT = "choose_content";

    private SearchView mSearchView;
    private boolean mSearchClosing = false;

    // Sliding layout
    private PTVerticalSlidingLayout mVGSlidingLayout;

    // Section bar
    private PTSectionBar mSectionBar;
    private PTSectionItemType mCurrentSectionType = PTSectionItemType.SectionVideo;

    // Audio Preview
    private PTAudioControllerView mAudioController;
    private boolean mFirstVisible = true;
    private int mAudioControllerHeight = 0;

    // Video Preview
    private ViewGroup mVGVideoPreview;
    private PTVideoPreviewFragment mVideoPreviewFragment;

    // Sub fragments
    private PTBaseChooseContentFragment mCurrentChooseContentFragment;
    private PTVideoChooseContentFragment mChooseVideoFragment;
    private PTAudioChooseContentFragment mChooseAudioFragment;

    public PTCreateFragment() {
        super(PTTabID.TAB_CREATE);
    }

    public static PTCreateFragment newInstance() {
        return new PTCreateFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_create;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mVideoPreviewFragment = PTVideoPreviewFragment.newInstance();
        mChooseVideoFragment = new PTVideoChooseContentFragment();
        mChooseVideoFragment.setParentFragment(this);
        Bundle bundle = new Bundle();
        mChooseVideoFragment.setArguments(bundle);

        mChooseAudioFragment = new PTAudioChooseContentFragment();
        mChooseAudioFragment.setParentFragment(this);
        bundle = new Bundle();
        mChooseAudioFragment.setArguments(bundle);
    }

    @Override
    protected void initUI() {
        initActionBar();

        mContentView.addOnLayoutChangeListener(mLayoutChangeListener);

        mVGSlidingLayout = (PTVerticalSlidingLayout) mContentView.findViewById(R.id.slidingLayout);
        mVGSlidingLayout.setSlidingLayoutInterface(this);
        // mVGSlidingLayout.setOnTouchListener(mSlidingLayoutTouchListener);

        mSectionBar = (PTSectionBar) mContentView.findViewById(R.id.sectionBar);
        mSectionBar.setOnSectionItemSelectedListener(mSectionItemSelectedListener);

        mVGVideoPreview = (ViewGroup) findViewById(R.id.fragment_video);
        mAudioController = (PTAudioControllerView) findViewById(R.id.vgAudioPlayer);
        mAudioController.setParentFragment(this);
        mAudioController.addOnLayoutChangeListener(mLayoutChangeListener);

        initVideoFragment();
        initChooseFragment();
        setChooseFragment(mCurrentSectionType);

        updateUI();
    }

    private void initActionBar() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem menuSearch = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) menuSearch.getActionView();
        mSearchView.setInputType(mSearchView.getInputType() | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                mSearchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!mSearchClosing && !mSearchView.isIconified() && TextUtils.isEmpty(s)) {
                    search("");
                }

                return false;
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    mSearchClosing = true;
                    mSearchView.setQuery("", false);
                    mSearchView.setIconified(true);
                    menuSearch.collapseActionView();
                    mSearchClosing = false;
                }
            }
        });

        updateUI();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onBackPressed() {
        boolean result = super.onBackPressed();

        updateUI();

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    private void initVideoFragment() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mFragmentManager.findFragmentById(R.id.fragment_video) == null) {
            ft.add(R.id.fragment_video, mVideoPreviewFragment, FRAGMENT_TAG_VIDEO_PREVIEW);
        }
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }

    private void initChooseFragment() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(R.id.fragment_choose_audio_content, mChooseAudioFragment, FRAGMENT_TAG_CHOOSE_CONTENT);
        ft.add(R.id.fragment_choose_audio_content, mChooseVideoFragment, FRAGMENT_TAG_CHOOSE_CONTENT);
        ft.commit();
        mFragmentManager.executePendingTransactions();
    }

    @Override
    protected void registerReceiver() {
        IntentFilter filter = new IntentFilter(PTBaseChooseChannelView.BROADCAST_MESSAGE_ACTION_CHANNEL_SELECTED);
        getActivity().registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void unregisterReceiver() {
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    private void updateUI() {
        if (mSearchView == null) return;

        mSectionBar.selectSectionItem(mCurrentSectionType, true);

        mAudioController.setVisibility(View.INVISIBLE);
        switch (mCurrentSectionType) {
            case SectionAudio:
                mSearchView.setQueryHint(getString(R.string.audio_search));
                mAudioController.setVisibility(View.VISIBLE);
                break;
            case SectionVideo:
                mSearchView.setQueryHint(getString(R.string.video_search));
                break;
            case SectionShare:
                break;
        }

        updateLayout(false);
    }

    @Override
    protected void onVisible(boolean isVisibleToUser) {
        super.onVisible(isVisibleToUser);

        if (isVisibleToUser) {
            if (mAudioController != null) mAudioController.play();
        } else {
            if (mAudioController != null) mAudioController.stop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setChooseFragment(PTSectionItemType type) {
        PTBaseChooseContentFragment newChooseFragment = null;
        switch (type) {
            case SectionVideo:
                newChooseFragment = mChooseVideoFragment;
                break;
            case SectionAudio:
                newChooseFragment = mChooseAudioFragment;
                break;
            case SectionShare:
                return;
            default:
                Assert.assertNotNull("Not selectable section");
        }

        mCurrentSectionType = type;
        if (mCurrentChooseContentFragment == newChooseFragment) return;
        mCurrentChooseContentFragment = newChooseFragment;

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.hide(mChooseAudioFragment);
        ft.hide(mChooseVideoFragment);
        ft.show(mCurrentChooseContentFragment);
        ft.commit();
        mFragmentManager.executePendingTransactions();

        updateUI();
    }

    public PTBaseChooseContentFragment getCurrentChooseContentFragment() {
        return mCurrentChooseContentFragment;
    }

    private void search(final String keyword) {
        Assert.assertTrue(mCurrentChooseContentFragment != null);

        mCurrentChooseContentFragment.search(keyword);
    }

    // Mux audio & video and share it
    private boolean muxAndShare() {
        if (mVideoPreviewFragment == null || !mVideoPreviewFragment.isAdded() ||
                mChooseAudioFragment == null || !mChooseAudioFragment.isAdded()) {
            return false;
        }

        PTBaseVideoObject videoObject = (PTBaseVideoObject) mVideoPreviewFragment.getCurrentPreviewObject();
        PTBaseAudioObject audioObject = (PTBaseAudioObject) mAudioController.getCurrentPreviewObject();
        if (videoObject != null && audioObject != null) {
            // String selectedVideoUrl = videoObject.getURL();
            String selectedVideoUrl = PTApp.getInstance().getHttpProxy().getProxyUrl(videoObject.getURL());
            // String selectedAudioUrl = audioObject.getURL();
            String selectedAudioUrl = PTApp.getInstance().getHttpProxy().getProxyUrl(audioObject.getURL());

            Map<String, String> params = new HashMap<String, String>();
            params.put("video", selectedVideoUrl);
            params.put("audio", selectedAudioUrl);
            FlurryAgent.logEvent("Share & Muxing" + params);

            PTMuxOperation.MuxAndPostAsyncTask muxTask = new PTMuxOperation.MuxAndPostAsyncTask(getActivity(),
                    selectedVideoUrl, selectedAudioUrl, audioObject.getTitle(), audioObject.getAuthor());
            muxTask.execute();

            return true;
        } else {
            Toast.makeText(getActivity(), "Muxing Process Error", Toast.LENGTH_LONG).show();
            //TODO

            return false;
        }
    }

    public void updateLayout(boolean minimize) {
        int fragmentHeight = mContentView.getMeasuredHeight();
        int sectionBarHeight = mSectionBar.getMeasuredHeight();
        if (fragmentHeight == 0 || sectionBarHeight == 0 || mAudioControllerHeight == 0) return;

        boolean isChanged = false;
        FrameLayout.LayoutParams layoutParam = (FrameLayout.LayoutParams) mVGSlidingLayout.getLayoutParams();
        // Check bottom
        if (minimize || (((layoutParam.topMargin + sectionBarHeight) + mAudioControllerHeight) > fragmentHeight)) {
            layoutParam.topMargin = fragmentHeight - mAudioControllerHeight - sectionBarHeight;
            isChanged = true;
        }

        // Check top
        if (layoutParam.topMargin < 0) {
            layoutParam.topMargin = 0;
            isChanged = true;
        }

        if (isChanged) mVGSlidingLayout.setLayoutParams(layoutParam);
    }

    /********************** Event Listener ************************/
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(PTBaseChooseChannelView.BROADCAST_MESSAGE_ACTION_CHANNEL_SELECTED)) {
                if (mSectionBar == null) return;

                if (intent.hasExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_SELECTED_CHANNEL)) {
                    PTChannel channel = (PTChannel) intent.getSerializableExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_SELECTED_CHANNEL);
                    PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);

                    if (mediaType != PTMediaType.MediaTypeAudioOnly && mediaType != PTMediaType.MediaTypeVideoOnly) {
                        return;
                    }

                    String imageURL = channel.getAvatarURL(PTChannel.CHANNEL_IMAGE_WIDTH, PTChannel.CHANNEL_IMAGE_HEIGHT);
                    mSectionBar.setSectionImage(
                            mediaType.isVideo() ? PTSectionItemType.SectionVideo : PTSectionItemType.SectionAudio,
                            imageURL);
                } else {
                    // Remove selected channel icon
                    mSectionBar.setSectionImage(mSectionBar.getSelectedSectionItemType(), null);
                }
            }
        }
    };

    public void switchSectionTab() {
        PTSectionItemType sectionItemType;
        if (mCurrentSectionType == PTSectionItemType.SectionVideo) {
            sectionItemType = PTSectionItemType.SectionAudio;
        } else {
            sectionItemType = PTSectionItemType.SectionVideo;
        }

        mSectionItemSelectedListener.onSectionItemSelected(sectionItemType);
    }

    // Callback touch event from child when it should be handled in parent
    public boolean onTouch(View v, MotionEvent event) {
        return mVGSlidingLayout.onTouchEvent(event);
    }

    public OnSectionItemSelectedListener mSectionItemSelectedListener = new OnSectionItemSelectedListener() {
        public void onSectionItemSelected(PTSectionItemType type) {
            FlurryAgent.logEvent("Selected " + type.getValue(getActivity()) + " section");

            boolean mShowChannelWindow = false;
            if (mCurrentSectionType == type) {
                // Its double selected, lets back to channel selection window
                mShowChannelWindow = true;
            }
            setChooseFragment(type);
            if (mShowChannelWindow) {
                mCurrentChooseContentFragment.setMediaChooseMode(PTMediaChooseMode.CHOOSE_CHANNEL);
            }

            switch (type) {
                case SectionShare:
                    muxAndShare();
                    break;
            }

            updateUI();
        }
    };

    private View.OnLayoutChangeListener mLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            int height = bottom - top;

            if(height > 0) {
                if(v == mAudioController) {
                    mAudioControllerHeight = height;
                    updateLayout(false);
                } else if(v == mContentView && mFirstVisible) {
                    mFirstVisible = false;

                    // Reset top position of sliding layout
                    FrameLayout.LayoutParams layoutParam = (FrameLayout.LayoutParams) mVGSlidingLayout.getLayoutParams();
                    int slidingTopPos = layoutParam.topMargin = (int) ((float)height *
                            PTConst.SLIDNIG_LAYOUT_INITIAL_TOP_PERCENT / 100);
                    mVGSlidingLayout.setLayoutParams(layoutParam);

                    // Resize video preview fragment
                    layoutParam = (FrameLayout.LayoutParams) mVGVideoPreview.getLayoutParams();
                    layoutParam.height = slidingTopPos;
                    mVGVideoPreview.setLayoutParams(layoutParam);
                }
            }
        }
    };

    @Override
    public boolean canScrollVertically(int direction) {
        return getCurrentChooseContentFragment().canScrollVertically(direction);
    }

    @Override
    public void updateLayout() {
        updateLayout(false);
    }

    @Override
    public void swipLeft() {
        switchSectionTab();
    }

    @Override
    public void swipRight() {
        switchSectionTab();
    }
}
