package com.phantasm.phantasm.main.create.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gpit.android.logger.RemoteLogger;
import com.phantasm.phantasm.PTApp;
import com.phantasm.phantasm.main.model.PTBaseMediaObject;

import java.io.IOException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by kevinfinn on 4/27/15.
 */
public class PTAudioPlayerService {
    private static final int UPDATE_DELAY = 0;
    boolean mPlayOnPrepared = true;
    private MediaPlayer mMediaPlayer = null;
    final static String TAG = "AudioPlayer";
    PTBaseMediaObject mAudioObject;
    boolean isPrepared;
    Context mContext;
    AudioPlayerCallbacks mListener = null;
    private Handler durationHandler = new Handler();
    private boolean shouldLoop = false;

    public void setAudioPlayerListener(AudioPlayerCallbacks listener) {
        mListener = listener;
    }

    public static PTAudioPlayerService getInstance(Context context) {
        return new PTAudioPlayerService(context);
    }

    private PTAudioPlayerService(Context context) {
        assertNotNull(context);
        mContext = context;
        if (mMediaPlayer == null)
            setup();
    }

    public String getUrl() {
        return mAudioObject.getURL();
    }

    public boolean getLooping() {
        return mMediaPlayer.isLooping();
    }

    public void setLooping(boolean shouldLoop) {
        this.shouldLoop = shouldLoop;

        Log.i(TAG, "setLooping(" + shouldLoop + ")");

        if (mMediaPlayer.isPlaying()) {
            Log.i(TAG, "mMediaPlayer is playing, mMediaPlayer.setLooping(" + shouldLoop + ")");
            mMediaPlayer.setLooping(shouldLoop);
        }
    }


    private void setup() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i(TAG, "prepared");
                    isPrepared = true;
                    if (mListener != null)
                        mListener.onAudioPrepared();

                    if (PTAudioPlayerService.this.mPlayOnPrepared)
                        play();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.i(TAG, "error " + what + " extra " + extra);
                    return false;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mListener != null) {
                        mListener.onAudioComplete();
                    }
                }
            });
        } catch (Exception e) {
            RemoteLogger.e(TAG, e.getLocalizedMessage());
        }
    }

    public void setAudioObject(@NonNull PTBaseMediaObject audioObject, boolean playOnPrepared, AudioPlayerCallbacks apc) throws IOException {
        this.setAudioPlayerListener(apc);
        assertTrue(audioObject.getURL() != null);

        if (mAudioObject != null && mAudioObject.getURL().equals(audioObject.getURL()))
            return;

        if (mListener != null) mListener.onStartLoading();

        mAudioObject = audioObject;
        isPrepared = false;

        try {
            mMediaPlayer.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            setup();
        }
        // mMediaPlayer.setDataSource(audioObject.getURL());
        mMediaPlayer.setDataSource(PTApp.getInstance().getHttpProxy().getProxyUrl(audioObject.getURL()));
        mPlayOnPrepared = playOnPrepared;
        try{
            mMediaPlayer.prepareAsync();
        }
        catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

    public boolean play() {
        Log.i(TAG, "play()");

        if (!isPrepared) {
            mPlayOnPrepared = true;
            return false;
        } else {
            try {
                if (mListener != null) {
                    mListener.onAudioStarted(getTrackLength());
                    Log.i(TAG, "return track length " + getTrackLength());
//                    mListener.onAudioProgressUpdate(mMediaPlayer.getCurrentPosition());
                }
                durationHandler.postDelayed(updateSeekBarTime, UPDATE_DELAY);
                PTApp.getInstance().gainAudioDevice();
                mMediaPlayer.start();

                setLooping(shouldLoop);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if (!mMediaPlayer.isPlaying())
                return;

            //get current position
            int timeElapsed = mMediaPlayer.getCurrentPosition();

            int trackLength = getTrackLength();
            if (timeElapsed >= trackLength) {
                Log.i(TAG, "finish seekbar update at " + timeElapsed);
                return;
            }

            // Log.i(TAG, "update seek bar time " + timeElapsed+".  Will update in "+UPDATE_DELAY+" ms");
            //set seekbar progress
            if(mListener !=null)
                mListener.onAudioProgressUpdate(timeElapsed);

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, UPDATE_DELAY);
        }
    };

    public boolean resume() {
        Log.i(TAG, "resume()");

        if (play()) {
            try {
                if (mListener != null)
                    mListener.onAudioResumed();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean stop() {
        Log.i(TAG, "stop()");

        if (mMediaPlayer.isPlaying()) {
            try {
                mMediaPlayer.pause();
                PTApp.getInstance().lossAudioDevice();
                if (mListener != null)
                    mListener.onAudioPaused();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void rewind() {
        mMediaPlayer.seekTo(0);
    }

    public void seek(int position) {
        mMediaPlayer.seekTo(position);
    }

    public boolean prepare() {
        try {
            mMediaPlayer.prepareAsync();
            if (mListener != null)
                mListener.onAudioStarted(getTrackLength());
            Log.i(TAG, "return track length "+getTrackLength());

        } catch (IllegalStateException e) {
            return false;
        }
        return true;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isPlaying() {
        try {
            return mMediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            return false;
        }
    }
    public void release() {
        mMediaPlayer.release();
    }

    public int getProgress(){
        if (!isPrepared) return 0;

        return mMediaPlayer.getCurrentPosition();
    }
    public int getTrackLength(){
        if (!isPrepared) return 0;

        return mMediaPlayer.getDuration();
    }
    public int getId() throws NumberFormatException{ return Integer.valueOf(mAudioObject.getId());}
    public PTBaseMediaObject getAudioObject(){return mAudioObject;}


    public interface AudioPlayerCallbacks {
        void onStartLoading();
        void onAudioPrepared();

        void onAudioStarted(int tracklength);

        void onAudioProgressUpdate(int progress);

        void onAudioPaused();

        void onAudioResumed();

        void onAudioComplete();

    }
}
