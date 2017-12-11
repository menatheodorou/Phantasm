package com.phantasm.phantasm.main.connect.spotlight;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.gpit.android.util.JsonParserUtils;
import com.gpit.android.util.NetworkUtils;
import com.gpit.android.webapi.OnAPICompletedListener;
import com.gpit.android.webapi.OnCommonAPICompleteListener;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.common.webapi.PTWebAPI;
import com.phantasm.phantasm.main.PTMainActivity;
import com.phantasm.phantasm.main.api.vma.PTVMASpotlightChannelAPI;
import com.phantasm.phantasm.main.connect.PTConnectFragment;
import com.phantasm.phantasm.main.model.PTFeaturedVideoObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by YNA on 5/4/2016.
 */
public class PTSpotlightView extends LinearLayout {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private final static int CONNECTION_TIMEOUT = 60 * 1000;
    private static int[] SLIDER_BAR = { R.drawable.slide_1, R.drawable.slide_2,
            R.drawable.slide_3 };

    private PTMainActivity mMainActivity;
    private PTConnectFragment mConnectFragment;
    private View mContentView;
    private PTVMASpotlightChannelAPI mChannelApi;

    private ViewFlipper mViewFlipper;
    private Animation.AnimationListener mAnimationListener;
    private ImageView mImgSliderBar;
    private int mImageIdx;
    private int mSlideCount;
    private PTFeaturedVideoObject[] mVideoObjs;

    private final GestureDetector mDetector = new GestureDetector(new SwipeGestureDetector());

    public PTSpotlightView(Context context) {
        super(context);
        initLayout(context);
    }

    public PTSpotlightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PTSpotlightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    public PTSpotlightView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context);
    }

    private void initLayout(Context context) {
        this.mMainActivity = (PTMainActivity) context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContentView = inflater.inflate(R.layout.widget_spotlight, this, false);
        addView(this.mContentView);

        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });
        mChannelApi = new PTVMASpotlightChannelAPI(getContext());
        sendRequestSpotlightChannel(new OnCommonAPICompleteListener<PTWebAPI>(getContext()) {
            @Override
            public void onCompleted(PTWebAPI webapi) {
                JSONObject dataObject = webapi.getResponseData();
                JSONArray recordArray = JsonParserUtils.getJSONArrayValue(dataObject, PTConst.TAG_RECORD);
                mSlideCount = recordArray.length();
                if(mSlideCount > 0) {
                    mVideoObjs = new PTFeaturedVideoObject[mSlideCount];
                    for (int i = 0; i < mSlideCount; i++) {
                        try {
                            JSONObject recordItem = recordArray.getJSONObject(i);
                            mVideoObjs[i] = new PTFeaturedVideoObject(true);
                            mVideoObjs[i].setId(recordItem.getInt("id") + "");
                            mVideoObjs[i].setCategory(recordItem.getInt("category"));
                            mVideoObjs[i].setTag(recordItem.getString("tags"));
                            mVideoObjs[i].setTitle(recordItem.getString("title"));
                            mVideoObjs[i].setDescription(recordItem.getString("description"));
                            mVideoObjs[i].setThumbURL(recordItem.getString("thumb"));
                            mVideoObjs[i].setURL(recordItem.getString("source"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    SpotlightThumbDownloadAsyncTask asyncTask = new SpotlightThumbDownloadAsyncTask(getContext());
                    asyncTask.execute();
                }
                initViewFliper();
            }

            @Override
            public void onFailed(PTWebAPI webapi) {
                super.onFailed(webapi);
            }
        });
    }

    private void initViewFliper() {
        mImageIdx = 0;
        mImgSliderBar = (ImageView) findViewById(R.id.img_slide_bar);

        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_out));
        // controlling animation
        //animation listener
        mAnimationListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mImageIdx++;
                if(mImageIdx > mSlideCount - 1) mImageIdx = 0;
                if(mImageIdx < 0) mImageIdx = mSlideCount - 1;
                mImgSliderBar.setImageResource(SLIDER_BAR[mImageIdx % 3]);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
    }

    private void sendRequestSpotlightChannel(OnAPICompletedListener<PTWebAPI> listener) {
        mChannelApi.showProgress(false);
        mChannelApi.exec(listener);
    }

    public void setParentFragment(PTConnectFragment fragment) {
        this.mConnectFragment = fragment;
    }

    private void onClickSlideItem() {
        if(mImageIdx < 0)
            return;
        int curIdx = mImageIdx - 1;
        if(curIdx > mSlideCount - 1) curIdx = 0;
        if(curIdx < 0) curIdx = mSlideCount - 1;
        mConnectFragment.showChannelDescriptionFragment(mVideoObjs[curIdx]);
    }

    class SpotlightThumbDownloadAsyncTask extends AsyncTask<Void, Integer, File> {

        private Context mContext;

        public SpotlightThumbDownloadAsyncTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected File doInBackground(Void... params) {
            File dir = new File(mContext.getCacheDir() + "/media/thumbs");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            for(int i = 0; i < mVideoObjs.length; i++) {
                String srcUrl = "http://phantasm.wtf/" + mVideoObjs[i].getThumbURL();
                File thumbFile = new File(mContext.getCacheDir() + "/" + mVideoObjs[i].getThumbURL());
                boolean vidResult = NetworkUtils.downloadFile(srcUrl, thumbFile,
                        CONNECTION_TIMEOUT, this);
                if(vidResult) {

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            for(int i = 0; i < mVideoObjs.length; i++) {
                String localThumbPath = mContext.getCacheDir() + "/" + mVideoObjs[i].getThumbURL();
                ImageView imgView = new ImageView(mContext);
//                Uri uri = Uri.parse(localThumbPath);
//                imgView.setImageURI(uri);
                imgView.setLayoutParams(lp);
                mViewFlipper.addView(imgView);
                imgView.setImageDrawable(Drawable.createFromPath(localThumbPath));
            }
            mViewFlipper.invalidate();
            mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
            mViewFlipper.setAutoStart(true);
            mViewFlipper.setFlipInterval(5000);
            mViewFlipper.startFlipping();
        }
    }
    
    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mMainActivity, R.anim.left_in));
//                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mMainActivity, R.anim.left_out));
//                    // controlling animation
//                    mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mViewFlipper.showNext();
//                    mImageIdx++;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mMainActivity, R.anim.right_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mMainActivity,R.anim.right_out));
                    // controlling animation
                    mImageIdx--;
                    mViewFlipper.showPrevious();
                    if(mImageIdx > mSlideCount - 1) mImageIdx = 0;
                    if(mImageIdx < 0) mImageIdx = mSlideCount - 1;
                    mImgSliderBar.setImageResource(SLIDER_BAR[mImageIdx % 3]);
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mMainActivity, R.anim.left_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mMainActivity, R.anim.left_out));
                    // controlling animation
                } else {
                    onClickSlideItem();
                }
                mViewFlipper.getInAnimation().setAnimationListener(mAnimationListener);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }
}
