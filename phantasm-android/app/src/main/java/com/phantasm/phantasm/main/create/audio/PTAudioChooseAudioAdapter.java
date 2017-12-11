package com.phantasm.phantasm.main.create.audio;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;

import java.util.List;

/**
 * Created by Joseph Luns on 2016/2/10.
 */
public class PTAudioChooseAudioAdapter extends ArrayAdapter<PTBaseMediaObject> {
    protected Context mContext;

    private PTBaseMediaObject mSelectedAudioObject;

    public PTAudioChooseAudioAdapter(Context context, int layoutResourceId, List<PTBaseMediaObject> data) {
        super(context, layoutResourceId, data);

        this.mContext = context;
    }

    public void setSelectedAudioObject(PTBaseMediaObject object) {
        if (mSelectedAudioObject == object) return;
        
        mSelectedAudioObject = object;

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final PTAudioChooseAudioItemHolder holder;

        PTBaseMediaObject item = getItem(position);
        if (item == null) return row;

        if (row == null || row.getTag() == null) {
            row = View.inflate(mContext, R.layout.list_audio_item, null);
            holder = new PTAudioChooseAudioItemHolder(row);
            row.setTag(holder);
        } else {
            holder = (PTAudioChooseAudioItemHolder) row.getTag();
        }
        holder.tvTitle.setText(item.getTitle());
        holder.tvAuthor.setText(item.getAuthor());

        Resources res = getContext().getResources();
        if (mSelectedAudioObject != null && item.equals(mSelectedAudioObject)) {
            holder.tvTitle.setTextColor(res.getColor(R.color.app_green_color));
            holder.tvAuthor.setTextColor(res.getColor(R.color.app_green_color));
        } else {
            holder.tvTitle.setTextColor(Color.WHITE);
            holder.tvAuthor.setTextColor(Color.WHITE);
        }

        return row;
    }

    private class PTAudioChooseAudioItemHolder {
        private TextView tvTitle;
        private TextView tvAuthor;

        public PTAudioChooseAudioItemHolder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
        }
    }
}