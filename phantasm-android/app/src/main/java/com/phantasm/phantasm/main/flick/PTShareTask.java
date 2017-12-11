package com.phantasm.phantasm.main.flick;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gpit.android.util.NetworkUtils;
import com.gpit.android.util.Utils;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTSettings;

import java.io.File;
import java.net.URLConnection;

public class PTShareTask extends AsyncTask<Void, Integer, File> {
    private final static String TAG = PTShareTask.class.getSimpleName();
    private final static int CONNECTION_TIMEOUT = 60 * 1000;

    private final static int PROGRESS_STEP_0 = 1;
    private final static int PROGRESS_STEP_1 = PROGRESS_STEP_0 + 1;
    private final static int PROGRESS_STEP_2 = PROGRESS_STEP_1 + 1;
    private final static int PROGRESS_COUNT = PROGRESS_STEP_2;

    public Context mContext;
    private String mSourceURL; // mp4

    private AlertDialog mProgressDialog;
    private ProgressBar mProgresBar;

    public PTShareTask(Context context, String sourceURL) {
        mContext = context;
        mSourceURL = sourceURL;

        mProgressDialog = new AlertDialog.Builder(mContext).create();
        mProgressDialog.setTitle(mContext.getString(R.string.sharing));
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        View view = LayoutInflater.from(mContext).inflate(R.layout.widget_progress_dialog, null);
        mProgresBar = (ProgressBar) view.findViewById(R.id.progress_horizontal);
        mProgresBar.setMax(PROGRESS_COUNT);
        mProgressDialog.setView(view);
        mProgressDialog.show();
    }

    @Nullable
    @Override
    protected File doInBackground(Void... params) {
        File externalFile = null;

        try {
            externalFile = File.createTempFile(mContext.getString(R.string.app_name), ".mp4",
                    PTSettings.getInstance(mContext).getOutputDir());

            publishProgress(PROGRESS_STEP_0);

            if (!NetworkUtils.downloadFile(mSourceURL, externalFile, CONNECTION_TIMEOUT, this)) {
                externalFile.deleteOnExit();
                externalFile = null;
            }
            publishProgress(PROGRESS_STEP_2);
        } catch (Exception e) {
            Log.e(TAG, "Error during mux operation: " + e);
            e.printStackTrace();
        }

        return externalFile;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        mProgresBar.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(File file) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (file != null) {
            showShareDialog(file);
        } else {
            showFailure();
        }
    }

    private void showShareDialog(File sharedFile) {
        try {
            shareVideo(mContext, sharedFile, "Phantasm");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.no_sharing_apps), Toast.LENGTH_LONG).show();
        }
    }

    public void shareVideo(final Context mContext, final File file, final String title) throws ActivityNotFoundException {
        Log.i(TAG, "video path: " + file.getAbsolutePath());

        final Uri fileUri = Uri.fromFile(file);

        MediaScannerConnection.scanFile(mContext, new String[]{file.getAbsolutePath()},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        shareIntent.setDataAndType(fileUri, URLConnection.guessContentTypeFromName(fileUri.toString()));
                        mContext.startActivity(Intent.createChooser(shareIntent,
                                mContext.getString(R.string.share_video)));

                    }
                });
    }

    private void showFailure() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.error_generating), Toast.LENGTH_LONG).show();

    }
}
