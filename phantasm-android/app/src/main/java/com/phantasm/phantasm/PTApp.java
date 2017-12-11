package com.phantasm.phantasm;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.FacebookSdk;
import com.flurry.android.FlurryAgent;
import com.gpit.android.app.BaseApp;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.phantasm.phantasm.common.PTConst;
import com.phantasm.phantasm.main.PTMainActivity;

import net.ralphpina.permissionsmanager.PermissionsManager;

import java.io.File;

/**
 * Created by kevinfinn on 4/21/15.
 * Modified by JosephLuns on 1/12/16
 */
public class PTApp extends BaseApp {
    private static PTApp APP;

    private HttpProxyCacheServer mHttpProxy;

    @Override
    public Class<? extends Activity> getMainActivityClass() {
        return PTMainActivity.class;
    }

    public static PTApp getInstance() {
        return APP;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        APP = this;

        init();
    }

    @Override
    public void onTerminate() {
        lossAudioDevice();

        super.onTerminate();
    }

    private void init() {
        initFacebook();
        initFlurry();
        initImageLoader();
        initHttpProxy();

        // Init permission manager
        PermissionsManager.init(this);

        // Gain audio device to disallow to play other app's playing
        gainAudioDevice();
    }

    private void initImageLoader() {

        // Config image downloader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheExtraOptions(240, 240, null)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private void initFlurry() {
        // configure Flurry
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogEvents(true);
        FlurryAgent.setLogLevel(Log.VERBOSE);

        // init Flurry
        FlurryAgent.init(this, PTConst.MY_FLURRY_APIKEY);
    }

    private void initHttpProxy() {
        mHttpProxy = new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .build();
    }

    public boolean gainAudioDevice() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        int result = am.requestAudioFocus(null,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);


        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }

        return false;
    }

    public boolean lossAudioDevice() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        int result = am.abandonAudioFocus(null);


        if (result == AudioManager.AUDIOFOCUS_LOSS) {
            return true;
        }

        return false;
    }

    public static void clearCache(Context context) {
        File[] directory = context.getCacheDir().listFiles();
        if (directory != null) {
            for (File file : directory) {
                file.delete();
            }
        }
    }
    public HttpProxyCacheServer getHttpProxy() {
        return mHttpProxy;
    }

    /*
    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.phantasm.phantasm", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("VIVZ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    */
}
