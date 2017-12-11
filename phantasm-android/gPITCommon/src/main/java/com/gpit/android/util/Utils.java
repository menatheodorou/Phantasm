package com.gpit.android.util;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.gpit.android.library.R;
import com.gpit.android.ui.common.Constant;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final int SECOND_MILISECONDS = 1000;
    public static final int MIN_MILISECONDS = 60 * SECOND_MILISECONDS;
    public static final int HOUR_MILISECONDS = 60 * MIN_MILISECONDS;
    public static final int DAY_MILISECONDS = 24 * HOUR_MILISECONDS;
    public static final int WEEK_MILISECONDS = 7 * DAY_MILISECONDS;

    public static ProgressDialog mProgressDlg;

    /*************************************************************************************
     * CHECK AVAILBILITY
     ************************************************************************************/
    /**
     * Check if this device has a camera
     */
    private static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Intent getBestIntentInfo(Context context, Intent intent, String compare, String recommandedAppPackage) {
        return getBestIntentInfo(context, intent, compare, recommandedAppPackage, null);
    }

    public static Intent getBestIntentInfo(Context context, Intent intent, String compare, String recommandedAppPackage, String exceptClass) {
        Assert.assertTrue(intent != null);

        if (compare == null)
            return intent;

        ResolveInfo best = null;
        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);

        for (final ResolveInfo info : matches) {
            if (info.activityInfo.name.toLowerCase().contains(compare)) {
                if (exceptClass != null && exceptClass.equals(info.activityInfo.name))
                    continue;

                best = info;
                if (info.activityInfo.applicationInfo.packageName.equals(recommandedAppPackage))
                    break;
            }
        }

        if (best != null) {
            intent.setPackage(best.activityInfo.packageName);
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        } else {
            String marketURL = String.format("https://market.android.com/details?id=%s", recommandedAppPackage);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketURL));
            Toast.makeText(context, "Please download sharing app from \"" + recommandedAppPackage + "\"", Toast.LENGTH_LONG).show();
        }

        return intent;
    }

    // Get string from object
    public static String getClassString(Object object, String prefix) {
        StringBuffer result = new StringBuffer();
        @SuppressWarnings("rawtypes")
        Class cls;

        if (object == null)
            return "";

        cls = object.getClass();
        result.append("\n");
        result.append(prefix);
        result.append(cls.getName());
        result.append(" = {");

        try {
            // Retrieve all variables.
            Field[] fields = cls.getDeclaredFields();
            Field field;
            Object obj;
            String name, value;

            for (int i = 0; i < fields.length; i++) {
                field = fields[i];

                name = field.getName();
                value = "";
                try {
                    field.setAccessible(true);
                    if ((field.getModifiers() & Modifier.FINAL) > 0)
                        continue;
                    obj = field.get(object);
                    if ((obj.getClass() != cls))
                        value = obj.toString();
                    else
                        continue;
                } catch (Exception e) {
                }

                result.append("\n" + prefix + "\t" + prefix);
                result.append(name);
                result.append(" = ");
                result.append(value);
            }
        } catch (Exception e) {
            result.append("exception = " + e.getMessage());
        }
        result.append("\n" + prefix + "}");

        return result.toString();
    }

    /*************************************************************************************
     * LANGUAGE & IME FUNCTIONS
     ************************************************************************************/
    /**
     * Switch language of APP
     *
     * @param context
     * @param pLocale
     */
    public static void switchLanguage(Context context, Locale pLocale) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = pLocale;
        res.updateConfiguration(conf, dm);
    }

    /*************************************************************************************
     * UI FUNCTIONS
     ************************************************************************************/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT < 16) { //ye olde method
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else { // Jellybean and up, new hotness
            if (activity.getActionBar() != null)
                activity.getActionBar().hide();

            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public static boolean isWaitingDlgShowed() {
        return mProgressDlg != null;
    }

    public static ProgressDialog showWaitingDlg(Context context) {
        return showWaitingDlg(context, Constant.DLG_WAIT_MSG);
    }

    public static ProgressDialog showWaitingDlg(Context context, String msg) {
        return showWaitingDlg(context, msg, false);
    }

    public static ProgressDialog showWaitingDlg(Context context, boolean cancelable) {
        return showWaitingDlg(context, Constant.DLG_WAIT_MSG, cancelable);
    }

    public static ProgressDialog showWaitingDlg(Context context, String msg, boolean cancelable) {
        return showWaitingDlg(context, msg, cancelable, null);
    }

    public static ProgressDialog showWaitingDlg(Context context, String msg, boolean cancelable,
                                                DialogInterface.OnCancelListener listener) {
        hideWaitingDlg();

        if (mProgressDlg == null) {
            mProgressDlg = openNewDialog(context, msg, cancelable, false, listener);
        }

        return mProgressDlg;
    }

    public static ProgressDialog openNewDialog(Context context, String msg, boolean cancelable) {
        return openNewDialog(context, msg, cancelable, false, null);
    }

    public static ProgressDialog openNewDialog(Context context, String msg, boolean cancelable,
                                               boolean outsideTouchCancelable,
                                               DialogInterface.OnCancelListener listener) {
        ProgressDialog dialog = null;
        try {
            dialog = new ProgressDialog(context);

            dialog.setMessage(msg);
            dialog.setIndeterminate(true);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(cancelable);
            dialog.setCanceledOnTouchOutside(outsideTouchCancelable);
            if (listener != null) {
                dialog.setOnCancelListener(listener);
            }
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dialog;
    }

    public static void hideWaitingDlg() {
        if (mProgressDlg != null) {
            try {
                mProgressDlg.dismiss();
            } catch (Exception e) {
            }
            ;
        }

        mProgressDlg = null;
    }

    public static void showAlertDialog(Context context, String msg, boolean cancelable, OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(cancelable);
        builder.setPositiveButton("OK", listener);
        builder.show();
    }

    @Deprecated
    public static void hideWaitingDialog() {
        hideWaitingDlg();
    }

    // Check if current orientation is portrait or not
    @Deprecated
    public static boolean isPortraitScreen(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();

        if (orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180) {
            return true;
        } else {
            Assert.assertTrue(orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270);
            return false;
        }
    }

    public static boolean isPortraitScreen1(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        } else {
            return false;
        }
    }

    public static int getOrientation(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();

        switch (orientation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }

        Assert.assertTrue(false);
        return -1;
    }

    public static boolean isXLargeScreen(Context context) {
        int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return sizeMask >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static boolean isLargeScreen(Context context) {
        int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return sizeMask < Configuration.SCREENLAYOUT_SIZE_XLARGE && sizeMask >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isNormalScreen(Context context) {
        int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return sizeMask < Configuration.SCREENLAYOUT_SIZE_LARGE && sizeMask >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }

    public static boolean isSmallScreen(Context context) {
        int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return sizeMask < Configuration.SCREENLAYOUT_SIZE_NORMAL && sizeMask >= Configuration.SCREENLAYOUT_SIZE_SMALL;
    }

    public static boolean isPhoneSize(Context c) {
        return !Utils.isXLargeScreen(c);
    }

    // Enable/Disable all sub views.
    public static void setEnableAllSubViews(ViewGroup parent, boolean enable) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            child.setEnabled(enable);

            if (child instanceof ViewGroup)
                setEnableAllSubViews((ViewGroup) child, enable);
        }
    }

    public static int getPixels(Resources resources, int dp) {
        final float scale = resources.getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);

        return px;
    }

    /*************************************************************************************
     * BITMAP FUNCTIONS
     ************************************************************************************/
    /**
     * Retrieve bitmap drawable object from specified image path
     */
	/*
	 * This version tries to get a NinePatchDrawable directly from the resource stream
	 */
    // private static int mSuccess = 0;
    public static Drawable getBitmapDrawable(Context context, String imagePath) throws IOException {
        return getBitmapDrawable3(context, imagePath);

//		if(mSuccess == 1)
//			return getBitmapDrawable1(context, imagePath);
//		else if(mSuccess == 2)
//			return getBitmapDrawable2(context, imagePath);
//		else if(mSuccess == 3)
//			return getBitmapDrawable3(context, imagePath);
//		else
//			return getBitmapDrawable1(context, imagePath);
    }


    public static Drawable getBitmapDrawable1(Context context, String imagePath) throws IOException {
        Drawable drawable = null;

        Assert.assertTrue(imagePath != null);

        InputStream inStream;
        try {
            inStream = context.getAssets().open(imagePath);
            TypedValue value = new TypedValue();
            value.density = TypedValue.DENSITY_DEFAULT;
            drawable = Drawable.createFromResourceStream(context.getResources(), value, inStream, imagePath);
        } catch (Exception e) {
//			reportError_getBitmapDrawable(context, e, imagePath);
            return getBitmapDrawable2(context, imagePath);
        }

//		if(drawable instanceof NinePatchDrawable) {
//			Rect padding = new Rect();
//			drawable.getPadding(padding);
//			Log.d(KeyboardApp.LOG_TAG, "getBitmapDrawable():" + drawable.toString() + ",imagePath=" + imagePath + ",padding=" + padding.left + "," + padding.right + "," + padding.top + "," + padding.bottom);
//		}

        // mSuccess = 1;
        return drawable;
    }


    /*
     * This version tries to decode the stream to a regular Bitmap, then construct a NinePatchDrawable using the bitmap and 9-patch chunk
     */
    public static Drawable getBitmapDrawable2(Context context, String imagePath) throws IOException {
        Drawable drawable = null;

        Assert.assertTrue(imagePath != null);

        try {
            InputStream inStream = context.getAssets().open(imagePath);
            Bitmap bitmap = BitmapFactory.decodeStream(inStream);
            byte[] chunk = bitmap.getNinePatchChunk();
            boolean isNinePatch = NinePatch.isNinePatchChunk(chunk);
            if (isNinePatch) {
                // <HACK>
                float density = context.getResources().getDisplayMetrics().density;
                Rect padding = new Rect();
                if (imagePath.contains("_ss_") || imagePath.contains("_any_")) {
                    padding.left = (int) (12f * density);
                    padding.top = (int) (16f * density);
                    padding.right = (int) (12f * density);
                    padding.bottom = (int) (8f * density);
                } else {
                    padding.left = (int) (12f * density);
                    padding.top = (int) (12f * density);
                    padding.right = (int) (12f * density);
                    padding.bottom = (int) (12f * density);
                }
                // </HACK>
                drawable = new NinePatchDrawable(context.getResources(), bitmap, chunk, padding, imagePath);
//				drawable.getPadding(padding);
//				Log.d(KeyboardApp.LOG_TAG, "getBitmapDrawable2():" + drawable.toString() + ",imagePath=" + imagePath + ",padding=" + padding.left + "," + padding.right + "," + padding.top + "," + padding.bottom);
            } else
                drawable = new BitmapDrawable(context.getResources(), bitmap);
        } catch (Exception e) {
            return getBitmapDrawable3(context, imagePath);
        }

        // mSuccess = 2;
        return drawable;
    }


    public static Drawable getBitmapDrawable3(Context context, String imagePath) throws IOException {
        String resourceName = context.getPackageName() + ":drawable/" + imagePath.replace('/', '_').replace(".9.png", "").replace(".png", "");
        int drawableId = context.getResources().getIdentifier(resourceName, null, null);
        if (drawableId <= 0)
            throw (new FileNotFoundException(imagePath));

        Drawable drawable = context.getResources().getDrawable(drawableId);
//		Rect padding = new Rect();
//		drawable.getPadding(padding);
//		Log.d(KeyboardApp.LOG_TAG, "getBitmapDrawable3():" + drawable.toString() + ",imagePath=" + imagePath + ",padding=" + padding.left + "," + padding.right + "," + padding.top + "," + padding.bottom);

        // mSuccess = 3;

        return drawable;
    }


    public static BitmapDrawable safeGetBitmapDrawable(Context context, String imagePath, int maxWidth, int maxHeight,
                                                       int degree, ImageScaleType scaleType, boolean isPanScan) {
        Assert.assertTrue(imagePath != null);

        BitmapDrawable mDrawable = null;
        Options option = getBitmapInfo(imagePath);
        int width = option.outWidth;
        int height = option.outHeight;


        try {
            if (width > maxWidth || height > maxHeight)
                throw new OutOfMemoryError();
            mDrawable = new BitmapDrawable(context.getResources(), imagePath);
        } catch (OutOfMemoryError error) {
            Bitmap bitmap;
            bitmap = makeThumb(imagePath, width, height, degree, scaleType, isPanScan);
            if (bitmap != null) {
                mDrawable = new BitmapDrawable(bitmap);
            }
        }

        return mDrawable;
    }

    public static Options getBitmapInfo(String pathName) {
        Options options = new Options();

        options.inJustDecodeBounds = true;
        options.outWidth = 0;
        options.outHeight = 0;
        options.inSampleSize = 1;

        BitmapFactory.decodeFile(pathName, options);

        return options;
    }

    public static Bitmap makeThumb(String pathName, int iWidth, int iHeight, int degree,
                                   ImageScaleType scaleType, boolean isPanScan) {
        return makeThumb(pathName, iWidth, iHeight, degree, scaleType, isPanScan, false);
    }

    public static Bitmap makeThumb(String pathName, int iWidth, int iHeight, int degree,
                                   ImageScaleType scaleType, boolean isPanScan, boolean resizeBoundary) {
        Options options = null;
        Bitmap thumb = null;

        try {
            if ((new File(pathName)).exists())
                options = getBitmapInfo(pathName);
            else
                return null;

            int rotate = 0;

            ExifInterface exif = new ExifInterface(pathName);     //Since API Level 5
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (exifOrientation != -1) {
                switch (exifOrientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                }
            }
            degree += rotate;

            if (options.outWidth > 0 && options.outHeight > 0) {
                // Now see how much we need to scale it down.
                if (iWidth == -1 || iHeight == -1) {
                    iWidth = options.outWidth;
                    iHeight = options.outHeight;
                }
                int widthFactor = (options.outWidth + iWidth - 1) / iWidth;
                int heightFactor = (options.outHeight + iHeight - 1) / iHeight;

                widthFactor = Math.min(widthFactor, heightFactor);
                widthFactor = Math.max(widthFactor, 1);

                // Now turn it into a power of two.
                if (widthFactor > 1) {
                    int factor = 1;
                    for (int i = 0; i < widthFactor; i++) {
                        int temp = (int) Math.pow(2, i);
                        if (temp > widthFactor)
                            break;
                        factor = temp;
                    }
                    widthFactor = factor;
                }

                options.inSampleSize = widthFactor;
                options.inJustDecodeBounds = false;
				
				/*
				BitmapDrawable drawable = new BitmapDrawable(imageBitmap);
				drawable.setGravity(gravity);
				drawable.setBounds(0, 0, iWidth, iHeight);
				thumb = drawable.getBitmap();

				boolean isPanScan = false;
				if (imageBitmap.getWidth() > iWidth)
					isPanScan =
				*/

                thumb = BitmapFactory.decodeFile(pathName, options);
            } else {
                thumb = BitmapFactory.decodeFile(pathName);
            }

            thumb = Utils.getResizeBitmap(thumb, iWidth, iHeight, degree, scaleType, isPanScan, resizeBoundary);
        } catch (java.lang.Exception e) {
        }

        return thumb;
    }

    // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap b, int degrees, boolean shouldFreeOrgBitmap) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    if (shouldFreeOrgBitmap)
                        b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    public static Bitmap getResizeBitmap(Bitmap source, int newWidth, int newHeight,
                                         int degree, ImageScaleType scaleType, boolean isPanScan) {
        return getResizeBitmap(source, newWidth, newHeight, degree, scaleType, isPanScan, false);
    }

    public static Bitmap getResizeBitmap(Bitmap source, int newWidth, int newHeight,
                                         int degree, ImageScaleType scaleType, boolean isPanScan, boolean resizeBoundary) {
        source = rotate(source, degree, false);

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        if (sourceWidth == newWidth && sourceHeight == newHeight)
            return source;

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger 
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = 1;

        switch (scaleType) {
            case SCALE_FIT_X:
                scale = xScale;
                break;
            case SCALE_FIT_Y:
                scale = yScale;
                break;
            case SCALE_FIT_XY:
                Assert.assertTrue("Not Implemented" == null);
                break;
            case SCALE_FIT_PROPER:
                if (isPanScan) {
                    scale = Math.max(xScale, yScale);
                } else {
                    scale = Math.min(xScale, yScale);
                }
                break;
            default:
                Assert.assertTrue(false);
        }

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;
        Bitmap dest;
        RectF targetRect;
        if (!resizeBoundary) {
            // Let's find out the upper left coordinates if the scaled bitmap
            // should be centered in the new size give by the parameters
            float left = (newWidth - scaledWidth) / 2;
            float top = (newHeight - scaledHeight) / 2;

            // The target rectangle for the new, scaled version of the source bitmap will now
            // be
            targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

            // Finally, we create a new bitmap of the specified size and draw our new,
            // scaled bitmap onto it.
            dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        } else {
            dest = Bitmap.createBitmap((int) scaledWidth, (int) scaledHeight, source.getConfig());
            targetRect = new RectF(0, 0, scaledWidth, scaledHeight);
        }

        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
            drawableWidth = width;
            drawableHeight = height;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawableWidth, drawableHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenWidth(Context context) {
        return getDisplaySize(context).x;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenHeight(Context context) {
        return getDisplaySize(context).y;
    }

    private static Point getDisplaySize(Context context) {

        if (Build.VERSION.SDK_INT >= 17) {
            return getDisplaySizeMinSdk17(context);
        } else if (Build.VERSION.SDK_INT >= 13) {
            return getDisplaySizeMinSdk13(context);
        } else {
            return getDisplaySizeMinSdk1(context);
        }
    }

    @TargetApi(17)
    private static Point getDisplaySizeMinSdk17(Context context) {
        final WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);

        final Point size = new Point();
        size.x = metrics.widthPixels;
        size.y = metrics.heightPixels;

        return size;
    }

    @TargetApi(13)
    private static Point getDisplaySizeMinSdk13(Context context) {

        final WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final Point size = new Point();
        display.getSize(size);

        return size;
    }

    @SuppressWarnings("deprecation")
    private static Point getDisplaySizeMinSdk1(Context context) {

        final WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final Point size = new Point();
        size.x = display.getWidth();
        size.y = display.getHeight();

        return size;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int isImage(Context context, String filename) {
        String[] music_extensions = context.getResources().getStringArray(R.array.image_extensions);
        int length = 0;

        for (String music_extension : music_extensions) {
            if (filename.endsWith(music_extension)) {
                length = music_extension.length();
                break;
            }
        }

        return length;
    }

    public static File createBitmapFile(Bitmap bitmap, String path) {
        Assert.assertTrue(bitmap != null);

        File file = null;

        try {
            path = new File(path).getAbsolutePath();
            // Save bitmap to local path
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            file = new File(path);
            file.createNewFile();

            // write the bytes in file
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            file = null;
        }

        return file;
    }

    /************************************************************************************
     * TAKEN CAMERA & IMAGE
     ***********************************************************************************/
    public static String getCameraStorePath() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (path.exists()) {
            File test1 = new File(path, "100MEDIA/");
            if (test1.exists()) {
                path = test1;
            } else {
                File test2 = new File(path, "100ANDRO/");
                if (test2.exists()) {
                    path = test2;
                } else {
                    File test3 = new File(path, "Camera/");
                    if (!test3.exists()) {
                        test3.mkdirs();
                    }
                    path = test3;
                }
            }
        } else {
            path = new File(path, "Camera/");
            path.mkdirs();
        }

        return path.getAbsolutePath();
    }

    public static String getImagePath(Context context, Uri imgUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(imgUri,
                filePathColumn, null, null, null);

        if(cursor == null) {
            return imgUri.getPath();
        }

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }

    /*************************************************************************************
     * CHECK VALIDATION
     ************************************************************************************/
    public static boolean checkEmailFormat(String email) {
        if (email == null)
            return false;

        if (email.length() == 0) {
            return false;
        }

        // String pttn = "^[a-zA-Z0-9.-_@]+$";
        String pttn = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
//		String pttn = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern p = Pattern.compile(pttn);
        Matcher m = p.matcher(email);

        if (m.matches()) {
            return true;
        }

        return false;

    }

    public static boolean checkNameFormat(String name) {
        name = name.trim();
        if (name.length() == 0) {
            return false;
        }

        return true;

    }

    public static boolean checkUrlFormat(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    // TODO: Update
    public static boolean checkPhoneNoFormat(String phoneNo) {
        if (phoneNo == null)
            return false;

        Pattern p = Pattern.compile("^[\\+]\\d{3}\\d{7}$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(phoneNo);
        return m.find();
    }

    public static String stripExceptNumbers(String str, boolean includePlus) {
        StringBuilder res = new StringBuilder(str);
        String phoneChars = "0123456789";
        if (includePlus) {
            phoneChars += "+";
        }
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!phoneChars.contains(res.substring(i, i + 1))) {
                res.deleteCharAt(i);
            }
        }
        return res.toString();
    }

    public static boolean hasSpecialCharacters(String string) {
        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }

    public static String getPhoneNoWithPlus(String phoneNo) {
        if (phoneNo != null && !phoneNo.startsWith("+")) {
            return "+" + phoneNo;
        }

        return phoneNo;
    }

    public static String getPhoneNoWithoutPlus(String phoneNo) {
        if (phoneNo != null && phoneNo.length() > 0 && phoneNo.startsWith("+")) {
            return phoneNo.substring(1);
        }

        return phoneNo;
    }

    /*********************************************************************
     * NETWORK
     *********************************************************************/
    public static boolean isConnected(Context context) {
        ConnectivityManager connect = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
                || connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
                || connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
                || connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
    }

    public static String getHost(String url) {
        String host = null;
        try {
            URI uri = new URI(url);
            host = uri.getHost();
            if (host == null)
                host = uri.toString();
        } catch (Exception e) {
        }

        return host;
    }

    public static Map<String, List<String>> getUrlParameters(String url) {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        try {
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String pair[] = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }
                    List<String> values = params.get(key);
                    if (values == null) {
                        values = new ArrayList<String>();
                        params.put(key, values);
                    }
                    values.add(value);
                }
            }
        } catch (UnsupportedEncodingException e) {
        }

        return params;
    }

    public static boolean sendSMS(Context context, String number, String message) {
        if (number == null || !PhoneNumberUtils.isWellFormedSmsAddress(number) || number.equals("CDMA"))
            return false;

        SmsManager sms = SmsManager.getDefault();

        try {
            sms.sendTextMessage(number, null, message, null, null);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static InetAddress getLocalAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        //return inetAddress.getHostAddress().toString();
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Common", ex.toString());
        }

        return null;
    }
	
	/*
	public static String getMacAddress() {
		try {
			InetAddress addresses = InetAddress.getByName(InetAddress
					.getLocalHost().getHostName());

			NetworkInterface ni = NetworkInterface.getByInetAddress(addresses);

			byte[] mac = ni.getHardwareAddress();
			System.out.println("=====" + mac);
			for (int i = 0; i < mac.length; i++) {

				System.out.format("%02X%s", mac[i], (i < mac.length - 1) ? "-"
						: "");

			}

		} catch (Exception e) {
			System.out.println("exception was occureddddddddd" + e);

		}
	}
	*/

    /*********************************************************************
     * GPS
     *********************************************************************/
    public static Location getLocationFromName(Geocoder geocder, String name) {
        List<Address> addresses = null;
        Location location = null;

        try {
            // MUST BE CHECKED
            addresses = geocder.getFromLocationName(name, 1);
        } catch (IOException e) {
        }

        if (addresses != null) {
            for (Address singleAddress : addresses) {
                location = new Location("");
                location.setLatitude(singleAddress.getLatitude());
                location.setLongitude(singleAddress.getLongitude());
            }
        }

        return location;
    }

    public static Address getAddressFromName(Geocoder geocoder, String location) {
        List<Address> addresses = null;
        Address address = null;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null) {
                for (Address singleAddress : addresses) {
                    address = singleAddress;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    /**********************************************************************************
     * CURSOR TO CSV
     *********************************************************************************/
    public static StringBuffer cursorToCSV(Cursor cursor) {
        StringBuffer out = new StringBuffer();

        // loop thru all the records
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            String columnName = cursor.getColumnName(i);
            if (i != 0)
                out.append(", ");
            out.append("{");
            out.append(columnName); // adds a column
            out.append("}");
        }
        out.append("\n");

        while (cursor.moveToNext()) {
            // loop thru all the columns
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String column = cursor.getString(i);
                if (i != 0)
                    out.append(", ");
                out.append(column); // adds a column
            }
            out.append("\n");
        }

        return out;
    }

    /**********************************************************************************
     * XML PARSER
     *********************************************************************************/
    public static String getNodeValue(NodeList list, int index) {
        String nodeValue = "";

        try {
            nodeValue = list.item(index).getChildNodes().item(0).getNodeValue();
        } catch (Exception e) {
        }

        return nodeValue;
    }

    public static int getNodeIntValue(NodeList list, int index) {
        int nodeValue = 0;

        try {
            nodeValue = Integer.parseInt(list.item(index).getChildNodes().item(0).getNodeValue());
        } catch (Exception e) {
        }

        return nodeValue;
    }

    public static long getNodeLongValue(NodeList list, int index) {
        long nodeValue = 0;

        try {
            nodeValue = Long.parseLong(list.item(index).getChildNodes().item(0).getNodeValue());
        } catch (Exception e) {
        }

        return nodeValue;
    }

    public static float getNodeFloatValue(NodeList list, int index) {
        float nodeValue = 0;

        try {
            nodeValue = Float.parseFloat(list.item(index).getChildNodes().item(0).getNodeValue());
        } catch (Exception e) {
        }

        return nodeValue;
    }

    public static boolean getNodeBooleanValue(NodeList list, int index) {
        boolean nodeValue = false;

        try {
            nodeValue = Boolean.parseBoolean(list.item(index).getChildNodes().item(0).getNodeValue());
        } catch (Exception e) {
        }

        return nodeValue;
    }

    /**********************************************************************************
     * JSON PARSER
     *********************************************************************************/
    @Deprecated
    public static String getJsonSafeGetString(JSONObject object, String key) {
        return safeGetJsonString(object, key);
    }

    public static String safeGetJsonString(JSONObject object, String key) {
        String value = "";

        if (!object.isNull(key)) {
            try {
                value = object.getString(key);
            } catch (JSONException e) {
            }
        }

        return value;
    }

    @Deprecated
    public static int getJsonSafeGetInt(JSONObject object, String key) {
        return safeGetJsonInt(object, key);
    }

    public static int safeGetJsonInt(JSONObject object, String key) {
        int value = -1;

        if (!object.isNull(key)) {
            try {
                value = object.getInt(key);
            } catch (JSONException e) {
            }
        }

        return value;
    }

    public static long safeGetJsonLong(JSONObject object, String key) {
        long value = -1;

        if (!object.isNull(key)) {
            try {
                value = object.getLong(key);
            } catch (JSONException e) {
            }
        }

        return value;
    }

    public static double safeGetJsonDouble(JSONObject object, String key) {
        double value = Double.NaN;

        if (!object.isNull(key)) {
            try {
                value = object.getDouble(key);
            } catch (JSONException e) {
                value = Double.NaN;
            }
        }

        return value;
    }

    /*************************************************************************************
     * SECRET
     ************************************************************************************/
    public static String md5(String source) {
        String hash = null;

        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = source.getBytes();

            digester.update(bytes, 0, bytes.length);
            hash = new BigInteger(1, digester.digest()).toString(16);
        } catch (Exception e) {
        }


        return hash;
    }

    /**********************************************************************************
     * Audio
     *********************************************************************************/
    public static boolean isMicOn(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        return manager.isWiredHeadsetOn();
    }

    /**********************************************************************************
     * Encode/Decode
     *********************************************************************************/
    public static String safeUrlEncode(String msg) {
        if (msg != null)
            msg = URLEncoder.encode(msg);

        return msg;
    }

    /**********************************************************************************
     * KEYBOARD
     *********************************************************************************/
    public static void showSoftInput(Activity activity, View view, boolean isShow) {
        if (view == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } else {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /*************************************************************************************
     * MAP FUNCTIONS
     ************************************************************************************/
    public static void openMapWithAddress(Context context, String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static void openNavigationMapWithAddress(Context context, String address) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    public static void openMapWithGPS(Context context, float latitude, float longitude) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + ", " + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }
}