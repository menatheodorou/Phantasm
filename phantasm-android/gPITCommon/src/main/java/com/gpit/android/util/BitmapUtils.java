package com.gpit.android.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import java.util.Locale;

public class BitmapUtils {
	private static final String TAG = BitmapUtils.class.getSimpleName();

	public static Bitmap getCircleCroppedBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap getScaledBitmap(Bitmap srcBitmap, BitmapFactory.Options options, int reqWidth, int reqHeight,
										 boolean aspectFill,
										 boolean crop, boolean recycle) {
		if(srcBitmap == null)
			return null;

		if (reqWidth == -1 || reqHeight == -1) return srcBitmap;

		int bmpWidth = srcBitmap.getWidth();
		int bmpHeight = srcBitmap.getHeight();

		if (options != null) {
			if (bmpWidth == 0) bmpWidth = options.outWidth;
			if (bmpHeight == 0) bmpHeight = options.outHeight;
		}
		if (bmpWidth == -1 || bmpHeight == -1) return srcBitmap;

		Log.d(TAG, String.format(Locale.getDefault(), "Scale Bitmap: imgWidth=%d, imgHeight=%d, reqWidth=%d, reqHeight=%d, crop = %d",
				bmpWidth, bmpHeight, reqWidth, reqHeight, crop ? 1 : 0));

		float x = 1; float y = 1;
		if(reqWidth > bmpWidth) {
			x = (float) reqWidth / bmpWidth;
		}
		if(reqHeight > bmpHeight) {
			y = (float) reqHeight / bmpHeight;
		}
		float scale = aspectFill ? Math.max(x, y) : Math.min(x, y);
		if (bmpWidth == reqWidth && bmpHeight == reqHeight) return srcBitmap;
		if (!crop && scale == 1) return srcBitmap;

		Bitmap destBitmap = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(destBitmap);
		float xTranslation = (reqWidth - bmpWidth * scale) / 2.0f;
		float yTranslation = (reqHeight - bmpHeight * scale) / 2.0f;

		Matrix transformation = new Matrix();
		if (crop) {
			transformation.postTranslate(xTranslation, yTranslation);
		}
		transformation.preScale(scale, scale);
		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		canvas.drawBitmap(srcBitmap, transformation, paint);

		if (recycle) {
			srcBitmap.recycle();
		}

		return destBitmap;
	}
}
