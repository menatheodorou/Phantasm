package com.phantasm.phantasm.main.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTUIUtils;
import com.phantasm.phantasm.main.model.PTChannel;
import com.phantasm.phantasm.main.ui.PTBaseChooseChannelView;

import java.util.List;

public class PTChooseChannelGridAdapter extends BaseAdapter {
    private static DisplayImageOptions mRoundedOptions;

    public static DisplayImageOptions getRoundedImageOption() {
        if (mRoundedOptions == null) {
            mRoundedOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new RoundedBitmapDisplayer(1000))
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
        }

        return mRoundedOptions;
    }

    private Context mContext;
    private PTBaseChooseChannelView mChannelView;
    private List<PTChannel> mChannels;
    private Bitmap mDefaultChannelBitmap;

    public PTChooseChannelGridAdapter(Context context, PTBaseChooseChannelView channelView,
                                      List<PTChannel> channels) {
        mContext = context;
        mChannelView = channelView;
        mChannels = channels;

        mDefaultChannelBitmap = ((BitmapDrawable)context.getResources().getDrawable(
                R.drawable.ic_default_channel, null)).getBitmap();
    }

    public int getCount() {
        return mChannels.size();
    }

    public Object getItem(int position) {
        return mChannels.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View itemView, ViewGroup parent) {
        final PTChannelItemHolder holder;

        PTChannel channelItem = (PTChannel) getItem(position);

        if (itemView == null) {
            itemView = View.inflate(mContext, R.layout.grid_channel_item, null);

            holder = new PTChannelItemHolder(itemView, position);
            itemView.setTag(holder);
        } else {
            holder = (PTChannelItemHolder) itemView.getTag();
            holder.position = position;
        }
        itemView.setOnClickListener(mRowClickListener);

        PTUIUtils.loadImage(holder.ivChannel,
                channelItem.getAvatarURL(PTChannel.CHANNEL_IMAGE_WIDTH, PTChannel.CHANNEL_IMAGE_HEIGHT),
                mDefaultChannelBitmap, getRoundedImageOption());

        return itemView;
    }

    private View.OnClickListener mRowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PTChannelItemHolder holder = (PTChannelItemHolder) v.getTag();
            mChannelView.onItemClick(null, v, holder.position, 0);
        }
    };

    private class PTChannelItemHolder {
        private int position;
        private ImageView ivChannel;

        public PTChannelItemHolder(View view, int position) {
            ivChannel = (ImageView) view.findViewById(R.id.ivChannel);
            this.position = position;
        }
    }
}