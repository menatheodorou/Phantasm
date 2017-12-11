package com.phantasm.phantasm.main.flick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.gpit.android.logger.RemoteLogger;
import com.gpit.android.util.BitmapUtils;
import com.phantasm.phantasm.PTApp;

import java.io.IOException;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by Joseph Luns on 2016/3/18.
 */
public class PTDraggableVideoView extends TextureView implements TextureView.SurfaceTextureListener,
        MediaPlayer.OnPreparedListener, View.OnTouchListener {
    private final static String TAG = PTDraggableVideoView.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private Surface mSurface;
    private String mPath;

    private MediaPlayer.OnPreparedListener mPreparedListener;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private MediaPlayer.OnErrorListener mErrorLIstener;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;

    private int mVideoWidth;
    private int mVideoHeight;

    private int mScaledVideoWidth;
    private int mScaledVideoHeight;

    private boolean mPrepared = false;
    private boolean mEnableTouchPause = true;

    public PTDraggableVideoView(Context context) {
        super(context);

        init();
    }

    public PTDraggableVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PTDraggableVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);

        setSurfaceTextureListener(this);
        setOnTouchListener(this);
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        mPreparedListener = listener;
    }

    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        mBufferingUpdateListener = listener;

        mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mCompletionListener = listener;

        mMediaPlayer.setOnCompletionListener(mCompletionListener);
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        mErrorLIstener = listener;

        mMediaPlayer.setOnErrorListener(mErrorLIstener);
    }

    public void stop() throws IllegalStateException {
        if (mMediaPlayer != null) mMediaPlayer.stop();
        mPrepared = false;
    }

    public void reset() {
        if (mMediaPlayer != null) mMediaPlayer.reset();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setVideoPath(final String path) {
        mPath = path;

        tryToStartPlay();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mMediaPlayer != null) {
            // Make sure we stop video and release resources when activity is destroyed.
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        boolean alreadyAttached = false;
        if (mSurface != null) alreadyAttached = true;

        mSurface = new Surface(surface);
        mMediaPlayer.setSurface(mSurface);

        if (!alreadyAttached) tryToStartPlay();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void tryToStartPlay() {
        if (mMediaPlayer == null || mSurface == null || TextUtils.isEmpty(mPath)) return;

        // If sureface is already prepared
        if (mMediaPlayer.isPlaying()) return;

        mMediaPlayer.stop();
        mMediaPlayer.reset();
        clearSurface(getSurfaceTexture());

        try {
            mPrepared = false;
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoWidth = mMediaPlayer.getVideoWidth();
        mVideoHeight = mMediaPlayer.getVideoHeight();

        adjustAspectRatio(mVideoWidth, mVideoHeight);

        mPrepared = true;
        if (mPreparedListener != null) mPreparedListener.onPrepared(mp);
    }

    public void setEnableTouchPause(boolean enable) {
        mEnableTouchPause = enable;
    }

    public void start() {
        PTApp.getInstance().gainAudioDevice();
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
        PTApp.getInstance().lossAudioDevice();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    private void calculateVideoSize() {
        try {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(mPath, new HashMap<String, String>());
            String height = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            mVideoWidth = (int) Float.parseFloat(width);
            mVideoHeight = (int) Float.parseFloat(height);
        } catch (Exception e) {
            RemoteLogger.d(TAG, e.getMessage());
        }
    }

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        if (viewHeight > (int) (viewWidth * aspectRatio)) {
            // limited by narrow width; restrict height
            mScaledVideoWidth = viewWidth;
            mScaledVideoHeight = (int) (viewWidth * aspectRatio);
        } else {
            // limited by short height; restrict width
            mScaledVideoWidth = (int) (viewHeight / aspectRatio);
            mScaledVideoHeight = viewHeight;
        }
        int offX = (viewWidth - mScaledVideoWidth) / 2;
        int offY = (viewHeight - mScaledVideoHeight) / 2;
        Log.v(TAG, "video=" + videoWidth + "x" + videoHeight +
                " view=" + viewWidth + "x" + viewHeight +
                " newView=" + mScaledVideoWidth + "x" + mScaledVideoHeight +
                " off=" + offX + "," + offY);

        Matrix matrix = new Matrix();
        getTransform(matrix);
        matrix.setScale((float) mScaledVideoWidth / viewWidth, (float) mScaledVideoHeight / viewHeight);
        matrix.postTranslate(offX, offY);
        setTransform(matrix);
    }

    @Override
    public Bitmap getDrawingCache() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Bitmap frame = getBitmap(mScaledVideoWidth, mScaledVideoHeight);
        if (frame == null) {
            return super.getDrawingCache();
        }

        Bitmap resizedBitmap = BitmapUtils.getScaledBitmap(frame, null, width, height, false, true, false);

        /*
        Matrix matrix = new Matrix();
        getTransform(matrix);
        matrix.invert(matrix);
        // matrix.postScale(1, 1);
        Bitmap resizedBitmap = Bitmap.createBitmap(frame, 0, 0,
                frame.getWidth(), frame.getHeight(), matrix, true);
        */

        return resizedBitmap;
    }

    /**
     * Clear the given surface Texture by attachign a GL context and clearing the surface.
     * @param texture a valid SurfaceTexture
     */
    private void clearSurface(SurfaceTexture texture) {
        if(texture == null){
            return;
        }

        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(display, null);

        int[] attribList = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, EGL10.EGL_WINDOW_BIT,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        egl.eglChooseConfig(display, attribList, configs, configs.length, numConfigs);
        EGLConfig config = configs[0];
        EGLContext context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, new int[]{
                12440, 2,
                EGL10.EGL_NONE
        });
        EGLSurface eglSurface = egl.eglCreateWindowSurface(display, config, texture,
                new int[]{
                        EGL10.EGL_NONE
                });

        egl.eglMakeCurrent(display, eglSurface, eglSurface, context);
        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        egl.eglSwapBuffers(display, eglSurface);
        egl.eglDestroySurface(display, eglSurface);
        egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroyContext(display, context);
        egl.eglTerminate(display);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mEnableTouchPause) return false;

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                } else {
                    mMediaPlayer.start();
                }

                return true;
            }
        }

        return false;
    }
}
