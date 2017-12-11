package com.phantasm.phantasm.common.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.gpit.android.util.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.phantasm.phantasm.main.model.PTChannel;

/**
 * Created by Joseph Luns on 6/2/15.
 */
public class PTUIUtils {
    public static ImageLoader getImageLoader(Context context) {
        // Config image downloader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoader imageLoader = getImageLoader(context, defaultOptions);

        return imageLoader;
    }

    public static ImageLoader getImageLoader(Context context, DisplayImageOptions options) {
        ImageLoader imageLoader = ImageLoader.getInstance();

        if (imageLoader.isInited())
            return imageLoader;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .diskCacheExtraOptions(PTChannel.CHANNEL_IMAGE_WIDTH,
                        PTChannel.CHANNEL_IMAGE_HEIGHT, null)
                .build();
        ImageLoader.getInstance().init(config);

        return imageLoader;
    }

    public static void loadImage(ImageView imageView, String url,
                          final Bitmap defaultBitmap) {
        ImageLoader imageLoader = getImageLoader(imageView.getContext());

        loadImage(imageView, url, defaultBitmap, null, imageLoader);
    }

    public static void loadImage(ImageView imageView, String url,
                                  final Bitmap defaultBitmap, final DisplayImageOptions options) {
        ImageLoader imageLoader = getImageLoader(imageView.getContext());

        loadImage(imageView, url, defaultBitmap, options, imageLoader);
    }

    public static void loadImage(ImageView imageView, String url,
                                 final Bitmap defaultBitmap, final DisplayImageOptions options,
                                 final ImageLoadingListener listener) {
        ImageLoader imageLoader = getImageLoader(imageView.getContext());

        loadImage(imageView, url, defaultBitmap, options, imageLoader, listener);
    }

    public static void loadImage(ImageView imageView, String url,
                                 final Bitmap defaultBitmap, final DisplayImageOptions options,
                                 final ImageLoader imageLoader) {
        loadImage(imageView, url, defaultBitmap, options, imageLoader, null);
    }

    public static void loadImage(ImageView imageView, String url,
                                 final Bitmap defaultBitmap, final DisplayImageOptions options,
                                 final ImageLoader imageLoader, final ImageLoadingListener listener) {
        imageView.setTag(imageView.getId(), url);
        imageView.setImageBitmap(defaultBitmap);

        imageLoader.displayImage(url, imageView, options,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        if (listener != null) listener.onLoadingStarted(s, view);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view,
                                                FailReason failReason) {
                        if (listener != null) listener.onLoadingFailed(s, view, failReason);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (view == null)
                            return;

                        String newUrl = (String) view.getTag(view.getId());

                        if (StringUtils.isNullOrEmpty(newUrl)) {
                            ((ImageView) view).setImageBitmap(defaultBitmap);
                        }

                        if (listener != null) listener.onLoadingComplete(s, view, bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        if (listener != null) listener.onLoadingCancelled(s, view);
                    }
                });
    }
}
