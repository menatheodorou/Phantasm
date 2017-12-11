package com.gpit.android.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by liulei on 8/7/15.
 */
public class SysUtils {
    public static long getCodeBuildTime(Context context) {
        try{
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();

            return time;
        }catch(Exception e){
        }

        return 0;
    }
}
