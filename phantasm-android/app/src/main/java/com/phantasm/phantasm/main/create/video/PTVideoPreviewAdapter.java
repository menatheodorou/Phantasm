package com.phantasm.phantasm.main.create.video;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.phantasm.phantasm.main.model.PTBaseMediaObject;

import java.util.ArrayList;
import java.util.List;

public class PTVideoPreviewAdapter extends FragmentStatePagerAdapter {
    private List<PTBaseMediaObject> mMediaList = new ArrayList<>();

    public PTVideoPreviewAdapter(FragmentManager fragmentManager, List<PTBaseMediaObject> mediaList) {
        super(fragmentManager);

        mMediaList = mediaList;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mMediaList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PTVideoPreviewItemFragment fragment = (PTVideoPreviewItemFragment) super.instantiateItem(container, position);
        fragment.setMedia(mMediaList.get(position));

        return fragment;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        PTVideoPreviewItemFragment fragment = PTVideoPreviewItemFragment.newInstance(
                mMediaList.get(position));
        return fragment;
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }
}
