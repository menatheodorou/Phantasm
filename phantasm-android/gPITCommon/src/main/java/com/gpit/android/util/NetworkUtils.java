package com.gpit.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
	private final static String TAG = NetworkUtils.class.getSimpleName();

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							|| info[i].getState() == NetworkInfo.State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean downloadFile(String sourceURL, File destinationFile, int timeout, AsyncTask task) {
		OutputStream output = null;
		HttpURLConnection connection = null;
		InputStream input = null;

		try {
			URL url = new URL(sourceURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(timeout); //set timeout to 5 seconds
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.e(TAG, "Server returned HTTP " + connection.getResponseCode()
						+ " " + connection.getResponseMessage());
				return false;
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			int fileLength = connection.getContentLength();

			// download the file
			input = connection.getInputStream();
			output = new FileOutputStream(destinationFile);

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				if (task != null && task.isCancelled()) {
					input.close();
					return false;
				}

				total += count;
				// publishing the progress....
				//	if (fileLength > 0) // only if total length is known
				//		publishProgress((int) (total * 100 / fileLength));
				output.write(data, 0, count);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error: " + e.toString());
			return false;
		} finally {
			try {
				if (output != null) output.close();
				if (input != null) input.close();
			} catch (IOException ignored) {
			}

			if (connection != null)
				connection.disconnect();
		}
		return true;
	}
}
