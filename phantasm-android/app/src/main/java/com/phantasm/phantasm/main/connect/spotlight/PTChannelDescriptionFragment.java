package com.phantasm.phantasm.main.connect.spotlight;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseFragment;
import com.phantasm.phantasm.main.connect.PTConnectFragment;
import com.phantasm.phantasm.main.model.PTFeaturedVideoObject;

/**
 * Created by osxcapitan on 5/5/16.
 */
public class PTChannelDescriptionFragment extends PTBaseFragment {

    private PTConnectFragment mConnectFragment;

    private PTFeaturedVideoObject mVideoObj;

    public static PTChannelDescriptionFragment newInstance() {
        return new PTChannelDescriptionFragment();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_channel_description;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initUI() {
        initActionBar();

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(mVideoObj.getTitle());
        String localThumbPath = getContext().getCacheDir() + "/" + mVideoObj.getThumbURL();
        ImageView imgView = (ImageView) findViewById(R.id.img_thumb);
        imgView.setImageDrawable(Drawable.createFromPath(localThumbPath));

        updateUI();
    }

    @Override
    protected void reload(Bundle bundle) {

    }

    public void setChannelVideoObject(PTFeaturedVideoObject obj) {
        mVideoObj = obj;
    }

    private void initActionBar() {
        setHasOptionsMenu(true);
    }

    public void setParentFragment(PTConnectFragment fragment) {
        this.mConnectFragment = fragment;
    }

    private void updateUI() {
//        if (mSearchView == null) return;

//        updateLayout(false);
    }
}
