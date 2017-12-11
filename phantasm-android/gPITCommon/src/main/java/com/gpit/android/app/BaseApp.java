package com.gpit.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.gpit.android.library.BuildConfig;
import com.gpit.android.logger.FileLog;
import com.gpit.android.logger.RemoteLogger;

import java.util.Locale;

public abstract class BaseApp extends MultiDexApplication {
    public abstract Class<? extends Activity> getMainActivityClass();

    public final static String TAG = BaseApp.class.getSimpleName();

    public static BaseApp APP;

    // UI
    private static String visibleActivityName;

    public static SharedPreferences getPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs;
    }

    public static SharedPreferences.Editor getPreferenceEditor(Context context) {
        return getPreference(context).edit();
    }

    public static BaseApp getInstance() {
        return APP;
    }

    private boolean mIsProductMode = false;
    private Resources resources;

    @Override
    public void onCreate() {
        preCreate();

        super.onCreate();

        APP = this;

        initApp();
    }

    private void preCreate() {
        // java.lang.ClassNotFoundException: android.os.AsyncTask caused by AdMob / Google Play Services
        // https://code.google.com/p/android/issues/detail?id=81083
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Throwable ignore) {
        }
    }

    public void initApp() {
        // Init ImageLoader

        if (BuildConfig.DEBUG) {
            // Log
            BaseConst.REMOTE_LOGGER_LOGGING_LEVEL = BaseConst.DEV_REMOTE_LOGGER_LOGGING_LEVEL;
            BaseConst.REMOTE_LOGGER_LOGGING_LEVEL = BaseConst.DEV_REMOTE_LOGGER_LOGGING_LEVEL;
        } else {
            // Log
            BaseConst.REMOTE_LOGGER_TOKEN = BaseConst.PRODUCT_REMOTE_LOGGER_TOKEN;
            BaseConst.REMOTE_LOGGER_LOGGING_LEVEL = BaseConst.PRODUCT_REMOTE_LOGGER_LOGGING_LEVEL;
        }

        // Init Remote Logger
        initRemoteLogger();
    }

    public void initEnv() {
        if (mIsProductMode) {
        } else {
        }
    }

    public void setPackageEnv(boolean isProduct) {
        mIsProductMode = isProduct;

        initEnv();
    }

    private void initRemoteLogger() {
        if (BaseConst.REMOTE_LOGGER_TOKEN != null) {
            RemoteLogger.init(getApplicationContext(), BaseConst.REMOTE_LOGGER_TOKEN, BaseConst.REMOTE_LOGGER_LOGGING_LEVEL);
        }

        RemoteLogger.i(TAG, "*************************** Application Initialized! ***************************");
        FileLog.logMessage(getApplicationContext(), "*************************** Application Initialized! ***************************");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        FileLog.logMessage(getApplicationContext(), "BaseApp onConfigurationChanged called");

        super.onConfigurationChanged(newConfig);

    }

    @Override
    public void onLowMemory() {
        FileLog.logMessage(getApplicationContext(), "BaseApp onLowMemory called");

        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        FileLog.logMessage(getApplicationContext(),
                String.format(Locale.getDefault(), "BaseApp onTrimMemory called : level = %d", level));

        super.onTrimMemory(level);
    }

    @Override
    public void onTerminate() {
        FileLog.logMessage(getApplicationContext(), "BaseApp onTerminate called");
        super.onTerminate();
    }

    /******************************
     * UI
     ******************************/
    public static String getCurrentVisibleActivityName() {
        return visibleActivityName;
    }

    public static void setCurrentVisibleActivityName(String name) {
        if (name != null) {
            RemoteLogger.d(TAG, "Current page (" + name + ")");
        } else if (visibleActivityName != null) {
            RemoteLogger.d(TAG, "Hidden page (" + visibleActivityName + ")");
        }

        visibleActivityName = name;
    }

    public static boolean isScreenVisible(String activityName) {
        if (visibleActivityName == null) {
            return false;
        }

        if (visibleActivityName.equals(activityName)) {
            return true;
        }

        return false;
    }

    public void onUpgrade() {
        // TODO Upgrade the database
    }
}