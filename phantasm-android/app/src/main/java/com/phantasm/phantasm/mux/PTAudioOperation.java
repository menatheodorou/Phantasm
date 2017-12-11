package com.phantasm.phantasm.mux;

import android.content.Context;
import android.util.Log;

import com.gpit.android.util.FileUtils;
import com.mpatric.mp3agic.Mp3File;

import java.io.File;

public class PTAudioOperation {

    public static final String TAG = PTAudioOperation.class.getSimpleName();

    private static PTAudioOperation instance;

    public static PTAudioOperation getInstance(Context context) {
        if (instance == null) {
            instance = new PTAudioOperation(context);
        }
        instance.mContext = context;

        return instance;
    }

    private Context mContext;

    private PTAudioOperation(Context context) {
        mContext = context;
    }

    public void removeMp3Tag(File audioInputFile) {
        try {
            File tempFile = File.createTempFile("audio", ".phantasm");
            String tempMp3Path = tempFile.getAbsolutePath();
            String mp3Path = audioInputFile.getAbsolutePath();

            // Copy audio file to temp
            FileUtils.copyFile(mp3Path, tempMp3Path);

            boolean rewrite = false;
            // Remove MP3 Tag such as ID1 ~ 3
            Mp3File mp3file = new Mp3File(tempMp3Path);
            if (mp3file.hasId3v1Tag()) {
                mp3file.removeId3v1Tag();
                rewrite = true;
                Log.d("MP3agic", "removeId3v1Tag");
            }
            if (mp3file.hasId3v2Tag()) {
                mp3file.removeId3v2Tag();
                rewrite = true;
                Log.d("MP3agic", "removeId3v2Tag");
            }
            if (mp3file.hasCustomTag()) {
                mp3file.removeCustomTag();
                rewrite = true;
                Log.d("MP3agic", "removeCustomTag");
            }

            if (rewrite) {
                // Clear destination file
                audioInputFile.delete();
                mp3file.save(mp3Path);
                audioInputFile = new File(mp3Path);
            }

            tempFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
