package com.phantasm.phantasm.main.create.video;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTUIUtils;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;

import java.util.List;

/**
 * Created by Joseph Luns on 2016/2/10.
 */
public class PTVideoChooseVideoAdapter extends ArrayAdapter<PTBaseMediaObject> {
    private PTBaseMediaObject mSelectedMediaObject;

    public PTVideoChooseVideoAdapter(Context context, int resource, List<PTBaseMediaObject> mediaObjects) {
        super(context, resource, mediaObjects);
    }

    public void setSelectedAudioObject(PTBaseMediaObject object) {
        if (mSelectedMediaObject == object) return;

        mSelectedMediaObject = object;

        notifyDataSetChanged();
    }

    public View getView(int position, View row, ViewGroup parent) {
        final PTVideoChooseVideoItemHolder holder;

        PTBaseMediaObject mediaObject = getItem(position);

        if (row == null) {
            row = View.inflate(getContext(), R.layout.grid_video_item, null);

            holder = new PTVideoChooseVideoItemHolder(row);
            row.setTag(holder);
        } else {
            holder = (PTVideoChooseVideoItemHolder) row.getTag();
        }

        holder.pbLoading.setVisibility(View.VISIBLE);
        PTUIUtils.loadImage(holder.ivThumbnail, mediaObject.getThumbURL(), null, null,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        holder.pbLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        holder.pbLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        holder.pbLoading.setVisibility(View.GONE);
                    }
                });

        Resources res = getContext().getResources();
        if (mSelectedMediaObject != null && mediaObject.equals(mSelectedMediaObject)) {
            holder.vgRoot.setBackgroundColor(res.getColor(R.color.app_green_color));
        } else {
            holder.vgRoot.setBackgroundColor(Color.WHITE);
        }

        return row;
    }

    public class PTVideoChooseVideoItemHolder {
        private ViewGroup vgRoot;
        private ImageView ivThumbnail;
        private ProgressBar pbLoading;

        public PTVideoChooseVideoItemHolder(View view) {
            vgRoot = (ViewGroup) view.findViewById(R.id.vgRoot);
            ivThumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
            pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
        }
    }
}