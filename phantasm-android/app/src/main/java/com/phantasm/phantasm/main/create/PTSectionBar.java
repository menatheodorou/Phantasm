package com.phantasm.phantasm.main.create;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gpit.android.util.StringUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTUIUtils;
import com.phantasm.phantasm.main.model.PTSectionItemType;
import com.phantasm.phantasm.main.ui.PTChooseChannelGridAdapter;

import junit.framework.Assert;

public class PTSectionBar extends LinearLayout {
    private static final int SECTION_SELECTED_ANIMATION_DURATION = 1000;

    private ViewGroup mVGVideo;
    private ViewGroup mVGShare;
    private ViewGroup mVGAudio;

    private PTSectionItemType mType = PTSectionItemType.SectionVideo;

    private OnSectionItemSelectedListener mListener;

    public PTSectionBar(Context context) {
        super(context);

        setup();
    }

    public PTSectionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup();

    }

    public PTSectionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setup();
    }

    public void setOnSectionItemSelectedListener(OnSectionItemSelectedListener listener) {
        mListener = listener;
    }

    public void selectSectionItem(PTSectionItemType type, boolean selection) {
        if (selection) {
            selectAllSection(false, type);
        }

        _selectSectionItem(type, selection);
    }

    public PTSectionItemType getSelectedSectionItemType() {
        return mType;
    }

    private void _selectSectionItem(PTSectionItemType type, boolean selection) {
        ViewGroup sectionItem = getSectionLayout(type);
        Assert.assertTrue(sectionItem != null);

        ImageButton imageButton = getImageButton(sectionItem);
        ImageButton largeImageButton = getLargeImageButton(sectionItem);

        // Lets apply filter at default section only
        /* #114238685 Remove green tint over any channel button. This feature should no longer be seen.
        if (imageButton.getTag() == null) {
            if (selection) {
                // Apply green color filter
                PorterDuffColorFilter filter = new PorterDuffColorFilter(getResources().getColor(R.color.app_bright_green_color),
                        PorterDuff.Mode.MULTIPLY);
                imageButton.getDrawable().setColorFilter(filter);
                largeImageButton.getDrawable().setColorFilter(filter);
            } else {
                imageButton.getDrawable().setColorFilter(null);
                largeImageButton.getDrawable().setColorFilter(null);
            }
        } */

        boolean isSelected = imageButton.isSelected();
        if (isSelected == selection) return;

        imageButton.setSelected(selection);
        largeImageButton.setSelected(selection);

        if (selection) {
            imageButton.setVisibility(View.INVISIBLE);
            largeImageButton.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.RubberBand)
                    .duration(SECTION_SELECTED_ANIMATION_DURATION)
                    .playOn(largeImageButton);
        } else {
            imageButton.setVisibility(View.VISIBLE);
            largeImageButton.setVisibility(View.INVISIBLE);
        }
    }

    private ViewGroup getSectionLayout(PTSectionItemType type) {
        ViewGroup sectionItem = mVGAudio;
        switch (type) {
            case SectionVideo:
                sectionItem = mVGVideo;
                break;
            case SectionAudio:
                sectionItem = mVGAudio;
                break;
            case SectionShare:
                sectionItem = mVGShare;
                break;
            default:
                Assert.assertTrue("Not supported type" == null);
        }

        return sectionItem;
    }

    private void selectAllSection(boolean selection, PTSectionItemType exceptionType) {
        if (exceptionType != PTSectionItemType.SectionVideo) _selectSectionItem(PTSectionItemType.SectionVideo, selection);
        if (exceptionType != PTSectionItemType.SectionAudio)_selectSectionItem(PTSectionItemType.SectionAudio, selection);
        if (exceptionType != PTSectionItemType.SectionShare)_selectSectionItem(PTSectionItemType.SectionShare, selection);
    }

    private void setup() {
        ViewGroup rootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.widget_section_bar, this, true);

        mVGVideo = (ViewGroup) rootView.findViewById(R.id.vgVideo);
        mVGAudio = (ViewGroup) rootView.findViewById(R.id.vgAudio);
        mVGShare = (ViewGroup) rootView.findViewById(R.id.vgShare);

        mVGVideo.setOnClickListener(mSectionItemClickListener);
        mVGAudio.setOnClickListener(mSectionItemClickListener);
        mVGShare.setOnClickListener(mSectionItemClickListener);

        // Setup default image
        setDefaultSectionImage(PTSectionItemType.SectionAudio);
        setDefaultSectionImage(PTSectionItemType.SectionVideo);
        setDefaultSectionImage(PTSectionItemType.SectionShare);

        // Auto select section
        selectSectionItem(mType, true);
    }

    private ImageButton getImageButton(ViewGroup sectionItemView) {
        ImageButton imageButton = (ImageButton) sectionItemView.getChildAt(0);
        return imageButton;
    }

    private ImageButton getLargeImageButton(ViewGroup sectionItemView) {
        ImageButton imageButton = (ImageButton) sectionItemView.getChildAt(1);
        return imageButton;
    }

    private void updateUI() {
        selectSectionItem(mType, true);
    }

    public void setSectionImage(PTSectionItemType type, String imageURL) {
        ViewGroup sectionItem = getSectionLayout(type);
        ImageButton imageButton = getImageButton(sectionItem);
        ImageButton largeImageButton = getLargeImageButton(sectionItem);

        if (largeImageButton.isSelected()) {
            YoYo.with(Techniques.RubberBand)
                    .duration(SECTION_SELECTED_ANIMATION_DURATION)
                    .playOn(largeImageButton);
        }
        if (StringUtils.isNullOrEmpty(imageURL)) {
            // Load default image
            setDefaultSectionImage(type);

            updateUI();
        } else {
            loadRemoteSectionImage(imageButton, imageURL);
            loadRemoteSectionImage(largeImageButton, imageURL);
        }
    }

    private void loadRemoteSectionImage(final ImageButton imageButton, String imageURL) {
        PTUIUtils.loadImage(imageButton, imageURL, null,
                PTChooseChannelGridAdapter.getRoundedImageOption(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        view.setTag(bitmap);
                        updateUI();
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                    }
                });
    }

    private void setDefaultSectionImage(PTSectionItemType type) {
        ImageButton imageButton;
        ImageButton largeImageButton;
        int imgResId = 0;

        switch (type) {
            case SectionAudio:
                imgResId = R.drawable.ic_audio;
                break;
            case SectionShare:
                imgResId = R.drawable.ic_share;
                break;
            case SectionVideo:
                imgResId = R.drawable.ic_video;
                break;
            default:
                Assert.assertTrue("Unknown type" == null);
        }

        imageButton = getImageButton(getSectionLayout(type));
        largeImageButton = getLargeImageButton(getSectionLayout(type));
        imageButton.setImageResource(imgResId);
        largeImageButton.setImageResource(imgResId);

        imageButton.setTag(null);
        largeImageButton.setTag(null);
    }

    /********************** EVENT LISTENER ********************/
    private OnClickListener mSectionItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mVGVideo) {
                mType = PTSectionItemType.SectionVideo;
            } else if (v == mVGAudio) {
                mType = PTSectionItemType.SectionAudio;
            } else if (v == mVGShare) {
                mType = PTSectionItemType.SectionShare;
            }

            if (v != mVGShare) {
                selectSectionItem(mType, true);
            }

            if (mListener != null) {
                mListener.onSectionItemSelected(mType);
            }
        }
    };
}
