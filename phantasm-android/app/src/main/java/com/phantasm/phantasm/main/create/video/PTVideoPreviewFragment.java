package com.phantasm.phantasm.main.create.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.common.ui.widgets.CustomViewPager;
import com.phantasm.phantasm.main.InfiniteViewPager;
import com.phantasm.phantasm.main.api.PTBaseMediaResponse;
import com.phantasm.phantasm.main.create.PTBasePreviewUX;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;
import com.phantasm.phantasm.main.model.PTMediaType;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import java.util.ArrayList;

public class PTVideoPreviewFragment extends PTBaseFragment implements PTBasePreviewUX {
    public static final String TAG = PTVideoPreviewFragment.class.getSimpleName();

    private static final int OFFSCREEN_PAGE_LIMIT = 3;

    private PTBaseMediaResponse<PTBaseMediaObject> mMediaList;
    private PTBaseMediaObject mSelectedMedia;

    private InfiniteViewPager mViewPager;
    private PTVideoPreviewAdapter mAdapter;

    public PTVideoPreviewFragment() {
    }

    public static PTVideoPreviewFragment newInstance() {
        return new PTVideoPreviewFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_preview_video;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
    }

    @Override
    protected void initUI() {
        mViewPager = (InfiniteViewPager) findViewById(R.id.vpVideoPreview);
        mViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    @Override
    protected void registerReceiver() {
        IntentFilter filter = new IntentFilter(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_LIST_LOADED);
        getActivity().registerReceiver(mMessageReceiver, filter);

        filter = new IntentFilter(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
        getActivity().registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void unregisterReceiver() {
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            setMedia(mMediaList);
        }
    }

    @Override
    public void setMedia(PTBaseMediaResponse mediaList) {
        mMediaList = mediaList;
        if (mMediaList != null) {
            mAdapter = new PTVideoPreviewAdapter(getChildFragmentManager(), mMediaList.getMediaObjects());
        } else {
            mAdapter = new PTVideoPreviewAdapter(getChildFragmentManager(), new ArrayList<PTBaseMediaObject>(0));
        }

        if (mViewPager == null) return;

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public PTBaseMediaObject getCurrentPreviewObject() {
        return mSelectedMedia;
    }

    public void setSelectedMedia(PTBaseMediaObject mediaObject) {
        if (mediaObject == null || mediaObject.equals(mSelectedMedia)) return;

        mSelectedMedia = mediaObject;

        int index = mMediaList.getMediaObjects().indexOf(mSelectedMedia);
        if (index >= 0) {
            mViewPager.setCurrentItem(index);
        }
    }

    private CustomViewPager.OnPageChangeListener mPageChangeListener = new CustomViewPager.OnPageChangeListener() {
        private boolean mPageScrolled = false;
        @Override
        public void onDrawFirstPage(int position) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mPageScrolled = true;
        }

        @Override
        public void onPageSelected(int position) {
            if (mPageScrolled) {
                PTBaseMediaObject mediaObject = mMediaList.getMediaObjects().get(position);

                Intent intent = new Intent();
                intent.setAction(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED);
                intent.putExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA, mediaObject);
                intent.putExtra(PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE,
                        PTMediaType.MediaTypeVideoOnly);
                getContext().sendBroadcast(intent);
            }

            mPageScrolled = false;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (!isVisible()) return;

            if (action.equals(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_LIST_LOADED)) {
                if (intent.hasExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA_LIST)) {
                    PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(
                            PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);
                    if (mediaType == PTMediaType.MediaTypeVideoOnly) {
                        PTBaseMediaResponse mediaList = (PTBaseMediaResponse) intent.
                                getSerializableExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA_LIST);
                        setMedia(mediaList);
                    }
                }
            } else if (action.equals(PTConst.BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED)) {
                PTMediaType mediaType = (PTMediaType) intent.getSerializableExtra(
                        PTBaseChooseChannelView.BROADCAST_MESSAGE_PARAM_MEDIA_TYPE);
                if (mediaType == PTMediaType.MediaTypeVideoOnly &&
                        intent.hasExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA)) {
                    PTBaseMediaObject mediaObject = (PTBaseMediaObject) intent.
                            getSerializableExtra(PTConst.BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA);
                    setSelectedMedia(mediaObject);
                }
            }
        }
    };
}
