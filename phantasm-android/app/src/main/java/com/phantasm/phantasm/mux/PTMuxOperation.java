package com.phantasm.phantasm.mux;

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

import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl;
import com.gpit.android.util.NetworkUtils;
import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.PTSettings;

import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PTMuxOperation {
    private final static String TAG = PTMuxOperation.class.getSimpleName();
    private final static int CONNECTION_TIMEOUT = 60 * 1000;

    public static int OUTPUT_WIDTH = 640;
    public static int OUTPUT_HEIGHT = 480;

    public static class MuxResult {
        public boolean success = false;
        public String outputFilepath = "";
    }

    public static class MuxAndPostAsyncTask extends AsyncTask<Void, Integer, File> {
        private final static int PROGRESS_STEP_0 = 1;
        private final static int PROGRESS_STEP_1 = PROGRESS_STEP_0 + 1;
        private final static int PROGRESS_STEP_2 = PROGRESS_STEP_1 + 1;
        private final static int PROGRESS_STEP_3 = PROGRESS_STEP_2 + 1;
        private final static int PROGRESS_STEP_4 = PROGRESS_STEP_3 + 1;
        private final static int PROGRESS_STEP_5 = PROGRESS_STEP_4 + 1;
        private final static int PROGRESS_STEP_6 = PROGRESS_STEP_5 + 1;
        private final static int PROGRESS_STEP_7 = PROGRESS_STEP_6 + 1;
        private final static int PROGRESS_STEP_8 = PROGRESS_STEP_7 + 1;
        private final static int PROGRESS_COUNT = PROGRESS_STEP_8;

        public Context mContext;
        private String mSourceVideoURL; // mp4
        private String mSourceAudioURL; // mp3
        private String mAudioFiletype;
        private String mTitle;
        private String mAuthor;

        private AlertDialog mProgressdialog;
        private ProgressBar mProgresBar;

        private boolean mUploaded = false;
        private int mProgress = 0;

        public MuxAndPostAsyncTask(Context context, String sourceVideoURL, String sourceAudioURL,
                                   String title, String author) {
            mContext = context;
            mSourceVideoURL = sourceVideoURL;
            mSourceAudioURL = sourceAudioURL;
            mTitle = title;
            mAuthor = author;

            mProgressdialog = new AlertDialog.Builder(mContext).create();
            mProgressdialog.setTitle(mContext.getString(R.string.muxing));
            mProgressdialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
            View view = LayoutInflater.from(mContext).inflate(R.layout.widget_progress_dialog, null);
            mProgresBar = (ProgressBar) view.findViewById(R.id.progress_horizontal);
            mProgresBar.setMax(PROGRESS_COUNT);
            mProgressdialog.setView(view);
            mProgressdialog.show();

            mAudioFiletype = getFileExtension(sourceAudioURL);
        }

        @Nullable
        @Override
        protected File doInBackground(Void... params) {
            File outputFile;
            File externalFile = null;

            try {
                MuxResult result = performMux();
                if(result == null)
                    return null;

                Log.i(TAG, "MuxResult file is "+result.outputFilepath);
                if(result.success) {
                    outputFile = new File(result.outputFilepath);
                    externalFile = File.createTempFile(mContext.getString(R.string.app_name), ".mp4",
                            PTSettings.getInstance(mContext).getOutputDir());

                    copy(outputFile, externalFile); //copy outputFile to externalFile
                    Log.i(TAG, "copy MuxResult to " + externalFile.getPath());
                    PTApp.clearCache(mContext);

                    publishProgress(PROGRESS_STEP_7);
                    publishProgress(PROGRESS_STEP_8);

                    /*
                    // Post created file to server
                    PTVMAPostMediaAPI postMediaAPI = new PTVMAPostMediaAPI(mContext, externalFile);
                    postMediaAPI.showProgress(false);
                    postMediaAPI.exec(false, new OnCommonAPICompleteListener<PTWebAPI>(mContext) {
                        @Override
                        public void onCompleted(PTWebAPI webapi) {
                            mUploaded = true;
                            publishProgress(++mProgress);
                        }
                    });

                    // If uploading is failed, lets delete created file
                    if (!mUploaded) {
                        externalFile.deleteOnExit();
                        externalFile = null;
                    }
                    */
                }
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
            if (mProgressdialog !=null && mProgressdialog.isShowing())
                mProgressdialog.dismiss();

            if (file != null) {
                showChoiceDialog(file);
            } else {
                showFailure();
            }
        }

        @Nullable
        private MuxResult performMux() throws Exception {
            MuxResult result = new MuxResult();

            result.success = false;

            File videoInputFile = new File(mContext.getCacheDir() + "/video.mp4");

            String fileExtension = getFileExtension(mSourceAudioURL);
            File audioInputFile = new File(mContext.getCacheDir() + "/audio." + fileExtension);

            publishProgress(PROGRESS_STEP_0);

            // 1. Download file from audio & video source to local storage
            boolean vidResult = NetworkUtils.downloadFile(mSourceVideoURL, videoInputFile,
                    CONNECTION_TIMEOUT, this);
            boolean audResult = NetworkUtils.downloadFile(mSourceAudioURL, audioInputFile,
                    CONNECTION_TIMEOUT, this);

            if (!vidResult)
                throw new Exception("video failed to download");
            if (!audResult)
                throw new Exception("audio failed to download");

            Log.i(TAG, "download vid result " + vidResult + " download audio result " + audResult);
            publishProgress(PROGRESS_STEP_1);

            Log.i(TAG, "Generating video with Inde");

            try {
                // Transcode video (resize + overlay)
                PTIndeOperation.getInstance(mContext).muxWithInde(videoInputFile, audioInputFile,
                        OUTPUT_WIDTH, OUTPUT_HEIGHT, mTitle, mAuthor, result);
                File transcodedVideo = new File(result.outputFilepath);

                Log.i(TAG, "Muxing with Mp4Parser");
                result = combineAudioVideo(audioInputFile, transcodedVideo);
                Log.i(TAG, "Done with muxing: " + result.outputFilepath);

                publishProgress(PROGRESS_STEP_6);
            } catch(RuntimeException e){
                e.printStackTrace();
            }

            return result;
        }

        public MuxResult combineAudioVideo(File audioInputFile, File videoInputFile) throws IOException{
            MuxResult muxResult = new MuxResult();
            Movie movie = null;
            Track audioTrack = null;
            DataSource sourceVideoFile = null;

            try {
                sourceVideoFile = new FileDataSourceImpl(videoInputFile);
                movie = MovieCreator.build(sourceVideoFile);
            } catch (Exception e) {
                Log.e(TAG, "Error loading video file:" + e);
            }

            publishProgress(PROGRESS_STEP_2);

            // Get audio & video tracks
            ArrayList<Track> videoTracks = getVideoTracks(movie);
            try {
                if (mAudioFiletype.equals("aac"))
                    audioTrack = new AACTrackImpl(new FileDataSourceImpl(audioInputFile.getAbsoluteFile()));
                else if (mAudioFiletype.equals("mp3")) {
                    // removing mp3 tags
                    PTAudioOperation.getInstance(mContext).removeMp3Tag(audioInputFile);
                    audioTrack = new MP3TrackImpl(new FileDataSourceImpl(audioInputFile.getAbsolutePath()));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading audio file: " + e);
            }

            double audioDuration = ((double) audioTrack.getDuration() / (double) audioTrack.getTrackMetaData().getTimescale());
            double videoDuration = getVideoDuration(movie);

            long loops = (long) (Math.ceil(audioDuration / videoDuration));
			ArrayList<Track> finalVideoTracks = new ArrayList<>();

			double durationSoFar = 0;
			for (int i = 0; i < loops; i++) {
				// Add each video track
				for (Track track : videoTracks) {

					Log.i(TAG, "Track samples: " + track.getSamples().size());
					double timeOfTrack = ((double)track.getDuration() / (double)track.getTrackMetaData().getTimescale());

					if (durationSoFar + timeOfTrack > audioDuration) {
						timeOfTrack = 0;
						int sampleTo = 0;
						int sampleCount = track.getSamples().size();

						for (sampleTo = 0; sampleTo < sampleCount && durationSoFar + timeOfTrack <= audioDuration; sampleTo++) {
							timeOfTrack += ((double)track.getSampleDurations()[sampleTo] / (double)track.getTrackMetaData().getTimescale());
						}

						track = new CroppedTrack(track, 0, sampleTo);
					}

					if (track != null) {
						finalVideoTracks.add(track);
					}

					durationSoFar = timeOfTrack + durationSoFar;
				}
			}

            Movie muxedMovie = new Movie();

            OUTPUT_WIDTH = (int) finalVideoTracks.get(0).getTrackMetaData().getWidth();
            OUTPUT_HEIGHT = (int) finalVideoTracks.get(0).getTrackMetaData().getHeight();

            if (finalVideoTracks.size() > 0) {
                muxedMovie.addTrack(new AppendTrack(finalVideoTracks.toArray(new Track[finalVideoTracks.size()])));
            }
            publishProgress(PROGRESS_STEP_3);

            muxedMovie.addTrack(new AppendTrack(audioTrack));

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(muxedMovie);
            publishProgress(PROGRESS_STEP_4);

            File outputFile = new File(mContext.getCacheDir() + "/" + "muxed.mp4");
            FileOutputStream fos = new FileOutputStream(outputFile);
            out.writeContainer(fos.getChannel());
            fos.close();
            publishProgress(PROGRESS_STEP_5);

            muxResult.success = true;
            muxResult.outputFilepath = outputFile.getAbsolutePath();

            return muxResult;
        }

        public double getVideoDuration(Movie m) {
            double result = 0;

            ArrayList<Track> videoTracks = getVideoTracks(m);

            for (Track t : videoTracks) {
                result += ((double) t.getDuration() / (double) t.getTrackMetaData().getTimescale());
            }

            return result;
        }

        public ArrayList<Track> getVideoTracks(Movie m) {
            Assert.assertTrue(m != null);

            ArrayList<Track> result = new ArrayList<>();

            for (Track t : m.getTracks()) {
                if (t instanceof Mp4TrackImpl) {
                    result.add(t);
                }
            }
            return result;
        }

        private String getFileExtension(String URL) {
            StringBuilder name = new StringBuilder(URL);
            try {
                // bad ex: https://p.scdn.co/mp3-preview/6b922581dd88ac355653f65b14772803195520cb
                // good ex: http://media1.giphy.com/media/26tPdVehXvFf1c5dS/giphy.mp4
                if (name.charAt(name.length() - 4) == '.' ) {
                    return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
                }
            } catch (Exception e) {
            }

            return "mp3";
        }

        private String getDateString(){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            Date now = new Date();
            return formatter.format(now);
        }

        private void copy(File src, File dst) throws IOException {
            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        }

        private File getOutputFile(Context context, String filename) {
            return new File(PTSettings.getInstance(context).getOutputDir(), filename);
        }

        private void showChoiceDialog(final File file) {
            new android.support.v7.app.AlertDialog.Builder(mContext).setMessage("Choose Play or Share Video").setNeutralButton("Play", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showPlayDialog(file);
                }
            }).setPositiveButton("Share", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showShareDialog(file);
                }
            }).show();
        }

        private boolean showPlayDialog(File videofile) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            try {
                intent.setDataAndType(Uri.parse(videofile.getAbsolutePath()), "video/*");

                mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share_video)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_sharing_apps), Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }

        private void showShareDialog(File sharedFile) {
            try {
                shareVideo(mContext, sharedFile, "Phantasm");
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_sharing_apps), Toast.LENGTH_LONG).show();
            }
        }

        public void shareVideo(final Context mContext, final File file, final String title) throws ActivityNotFoundException{

            Log.i(TAG, "video path: " + file.getAbsolutePath());

            final Uri fileUri = Uri.fromFile(file);

            MediaScannerConnection.scanFile(mContext, new String[]{file.getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Intent shareIntent = new Intent(
                                    android.content.Intent.ACTION_SEND);
                            shareIntent.setType("video/*");
                            shareIntent.putExtra(
                                    android.content.Intent.EXTRA_SUBJECT, title);
                            shareIntent.putExtra(
                                    android.content.Intent.EXTRA_TITLE, title);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            shareIntent
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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
}
