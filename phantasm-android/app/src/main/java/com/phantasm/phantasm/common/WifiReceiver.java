package com.phantasm.phantasm.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import java.io.IOException;

public class WifiReceiver extends BroadcastReceiver {
    static final boolean isdebug = false;
    public interface ConnectivityChangeListener{
        void onConnectivityChange(boolean isConnected);
    }

    private static ConnectivityChangeListener mlistener;
    public static void setConnectivityChangeListener(ConnectivityChangeListener listener){
        mlistener = listener;
    }

    public static void removeConnectivityChangeListener(ConnectivityChangeListener listener){
        if(mlistener.equals(listener))
            mlistener = null;
    }
    public WifiReceiver(){
        super();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo netInfo = conMan.getActiveNetworkInfo();

        if(mlistener==null)
            return;

        if (conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isAvailable() && conMan.getActiveNetworkInfo().isConnected() )
        {
                mlistener.onConnectivityChange(true);
        }
        else {
                if(isdebug)mlistener.onConnectivityChange(true);

                mlistener.onConnectivityChange(false);
        }
    }

    public static boolean isOnline() {
        if(isdebug)return true;

        Runtime runtime = Runtime.getRuntime();

        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();

            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isWifiEnabled(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null;
    }

}


