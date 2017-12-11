package com.phantasm.phantasm.mux;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;

import com.gpit.android.util.StringUtils;
import com.intel.inde.mp.domain.graphics.IEglUtil;
import com.phantasm.phantasm.R;

import intel.inde.mp.effects.OverlayEffect;

/**
 * Created by Joseph Luns on 2016/1/14.
 */
public class PTIndeVideoOverlayEffect extends OverlayEffect {
    private final static int NORMAL_VIDEO_WIDTH = 1080;
    private final static int OVERLAY_TEXT_SIZE = 42;
    private final static int OVERLAY_TEXT_ALPHA = 220;
    private final static float OVERLAY_TEXT_WIDTH_CHARGE_PERCENT = (0.4f);

    private Context mContext;

    private Paint mTitlePaint;
    private Paint mAuthorPaint;

    private String mTitle;
    private String mAuthor;

    private Rect mTitleRect = null;
    private Rect mAuthorRect = null;

    public PTIndeVideoOverlayEffect(Context context, int angle, IEglUtil eglUtil, String title, String author) {
        super(angle, eglUtil);

        mContext = context;

        mTitle = title;
        if (StringUtils.isNullOrEmpty(mTitle)) {
            mTitle = "";
        }

        mAuthor = author;
        if (StringUtils.isNullOrEmpty(mAuthor)) {
            mAuthor = "";
        }

        initPaint();
    }

    private void initPaint() {
        mAuthorPaint = new Paint();
        mAuthorPaint.setColor(Color.WHITE);
        mAuthorPaint.setAlpha(OVERLAY_TEXT_ALPHA);
        mAuthorPaint.setStyle(Paint.Style.FILL);
        mAuthorPaint.setAntiAlias(true);
        mAuthorPaint.setTypeface(Typeface.DEFAULT);

        mTitlePaint = new Paint();
        mTitlePaint.set(mAuthorPaint);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    private void initControlPosition(int width, int height) {
        float scale = (float) width / NORMAL_VIDEO_WIDTH;
        int maxTextWidth = (int) (width * OVERLAY_TEXT_WIDTH_CHARGE_PERCENT);
        mTitlePaint.setTextSize((float)OVERLAY_TEXT_SIZE * scale);
        mAuthorPaint.setTextSize((float)OVERLAY_TEXT_SIZE * scale);


        // Ellipsize title as max width
        TextPaint textPaint = new TextPaint(mTitlePaint);
        mTitle = TextUtils.ellipsize(mTitle, textPaint, maxTextWidth, TextUtils.TruncateAt.END).toString();

        // Ellipsize author as max width
        textPaint = new TextPaint(mAuthorPaint);
        mAuthor = TextUtils.ellipsize(mAuthor, textPaint, maxTextWidth, TextUtils.TruncateAt.END).toString();

        // Calculate text bounds to be displayed
        mTitleRect = new Rect();
        mAuthorRect = new Rect();
        mTitlePaint.getTextBounds(mTitle, 0, mTitle.length(), mTitleRect);
        mAuthorPaint.getTextBounds(mAuthor, 0, mAuthor.length(), mAuthorRect);

        int leftGap = (int)(mContext.getResources().getDimensionPixelSize(R.dimen.common_padding_small2) * scale);
        int topGap = (int)(mContext.getResources().getDimensionPixelSize(R.dimen.common_padding_small1) * scale);

        int titleLeft = leftGap;
        int titleTop = height - mAuthorRect.height() - mTitleRect.height() - topGap * 2;
        mTitleRect.set(titleLeft, titleTop, titleLeft + mTitleRect.width(), titleTop + mTitleRect.height());

        int authorLeft = titleLeft;
        int authorTop =  height - mAuthorRect.height() - topGap;
        mAuthorRect.set(authorLeft, authorTop, authorLeft + mAuthorRect.width(), authorTop + mAuthorRect.height());
    }

    @Override
    protected void drawCanvas(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mTitleRect == null) {
            initControlPosition(width, height);
        }

        // Show title
        canvas.drawText(mTitle, mTitleRect.left, mTitleRect.top, mTitlePaint);

        // Show author
        canvas.drawText(mAuthor, mAuthorRect.left, mAuthorRect.top, mAuthorPaint);
    }
}
