package com.phantasm.phantasm.mux;

import android.content.Context;
import android.media.MediaCodecInfo;
import android.util.Log;

import com.intel.inde.mp.IProgressListener;
import com.intel.inde.mp.IVideoEffect;
import com.intel.inde.mp.MediaComposer;
import com.intel.inde.mp.MediaFileInfo;
import com.intel.inde.mp.Uri;
import com.intel.inde.mp.android.AndroidMediaObjectFactory;
import com.intel.inde.mp.android.AudioFormatAndroid;
import com.intel.inde.mp.android.VideoFormatAndroid;
import com.intel.inde.mp.domain.Pair;

import java.io.File;
import java.io.IOException;

import intel.inde.mp.effects.SubstituteAudioEffect;

//import com.intel.inde.mp.MediaComposer;


public class PTIndeOperation {

    public final static String TAG = PTIndeOperation.class.getSimpleName();
    // Intel Inde transcoding settings
    protected final static String VIDEO_MIME_TYPE = "video/avc";
    protected final static int VIDEO_BIT_RATE_IN_K_BYTES = 10000;
    protected final static int VIDEO_FRAME_RATE = 30;

    protected final static int VIDEO_I_FRAME_INTERVAL = 1;
    // Audio
    protected final static String AUDIO_MIME_TYPE = "audio/mp4a-latm";
    protected final static int AUDIO_SAMPLE_RATE = 48000;
    protected final static int AUDIO_CHANNEL_COUNT = 2;

    protected final static int AUDIO_BIT_RATE = 96 * 1024;

    private static PTIndeOperation instance;

    public static PTIndeOperation getInstance(Context context) {
        if (instance == null) {
            instance = new PTIndeOperation(context);
        }
        instance.mContext = context;

        return instance;
    }

    private Context mContext;

    private ProgressListener mProgressListener = new ProgressListener();

    private AndroidMediaObjectFactory mFactory;
    private MediaComposer mMediaComposer = null;
    private VideoFormatAndroid mVideoFromat;
    private AudioFormatAndroid mAudioFromat;
    private String mTitle;
    private String mAuthor;

    private Uri mAudioUri;
    private Uri mVideoUri;

    private PTIndeOperation(Context context) {
        mContext = context;

        mFactory = new AndroidMediaObjectFactory(mContext);
    }

    // Attention: Blocking call
    // Attention: Audio muxing is not working for now. I don't know why. Its happened from previous one.
    public void muxWithInde(File videoInputFile, File audioInputFile, int outputWidth, int outputHeight,
                            String title, String author,
                            PTMuxOperation.MuxResult result) throws IOException, InterruptedException, RuntimeException {
        mTitle = title;
        mAuthor = author;

        // Retrieve media uri
        MediaFileInfo videoFileInfo = new MediaFileInfo(mFactory);
        videoFileInfo.setUri(new Uri(videoInputFile.getAbsolutePath()));
        mVideoUri = videoFileInfo.getUri();

        MediaFileInfo audioFileInfo = new MediaFileInfo(mFactory);
        audioFileInfo.setUri(new Uri(audioInputFile.getAbsolutePath()));
        mAudioUri = audioFileInfo.getUri();

        AndroidMediaObjectFactory factory = new AndroidMediaObjectFactory(mContext);
        mProgressListener.done = false;
        mMediaComposer = new MediaComposer(factory, mProgressListener);

        // Set transcode parameters
        String outPath = mContext.getCacheDir() + "/" + "muxed_inde.mp4";
        mMediaComposer.addSourceFile(mVideoUri);
        mMediaComposer.setTargetFile(outPath);

        configEncoders(outputWidth, outputHeight);
        mMediaComposer.start();

        // Wait till its completed
        synchronized (this) {
            while (!(mProgressListener.done)) {
                wait(100);
            }
        }


        result.success = true;
        result.outputFilepath = outPath;
    }

    private void configEncoders(int outputWidth, int outputHeight) throws IOException {
        configureVideoEncoder(outputWidth, outputHeight);
        configureVideoEffect();
        configureAudioEncoder();
    }

    private void configureVideoEncoder(int width, int height) {
        mVideoFromat = new VideoFormatAndroid(VIDEO_MIME_TYPE, width, height);

        mVideoFromat.setVideoBitRateInKBytes(VIDEO_BIT_RATE_IN_K_BYTES);
        mVideoFromat.setVideoFrameRate(VIDEO_FRAME_RATE);
        mVideoFromat.setVideoIFrameInterval(VIDEO_I_FRAME_INTERVAL);

        mMediaComposer.setTargetVideoFormat(mVideoFromat);
    }

    private void configureAudioEncoder() {
        mAudioFromat = new AudioFormatAndroid(AUDIO_MIME_TYPE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL_COUNT);

        mAudioFromat.setAudioBitrateInBytes(AUDIO_BIT_RATE);
        mAudioFromat.setAudioProfile(MediaCodecInfo.CodecProfileLevel.AACObjectLC);

        mMediaComposer.setTargetAudioFormat(mAudioFromat);
    }

    private void configureVideoEffect() {
        IVideoEffect effect = new PTIndeVideoOverlayEffect(mContext, 0, mFactory.getEglUtil(), mTitle, mAuthor);

        if (effect != null) {
            effect.setSegment(new Pair<Long, Long>(0l, 0l)); // Apply to the entire stream
            mMediaComposer.addVideoEffect(effect);
        }
    }

    private void configureAudioEffect() {
        // Add the audio
        // This should work, but doesn't, because Inde is broken.
        SubstituteAudioEffect effect = new SubstituteAudioEffect();
        effect.setFileUri(mContext, mAudioUri, mAudioFromat);
        mMediaComposer.addAudioEffect(effect);
    }

    public class ProgressListener implements IProgressListener {
        boolean done = false;

        @Override
        public void onMediaStart() {
            Log.i(TAG, "Encoding: onMediaStart()");
        }

        @Override
        public void onError(Exception e) {
            Log.e(TAG, "Error encoding: " + e);
            mMediaComposer.stop();

            onCompleted();
        }

        @Override
        public void onMediaDone() {
            Log.i(TAG, "Encoding: onMediaDone()");

            onCompleted();
        }

        @Override
        public void onMediaPause() {
            Log.i(TAG, "Encoding: onMediaPause()");
        }

        @Override
        public void onMediaProgress(float arg0) {
            Log.i(TAG, "Encoding: onMediaProgress(" + arg0 + ")");
        }

        @Override
        public void onMediaStop() {
            Log.i(TAG, "Encoding: onMediaStop()");
        }

        private void onCompleted() {
            synchronized (this) {
                done = true;
                notify();
            }
        }
    };
}
