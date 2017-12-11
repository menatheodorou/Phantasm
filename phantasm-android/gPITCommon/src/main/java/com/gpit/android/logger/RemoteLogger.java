package com.gpit.android.logger;

import android.content.Context;
import android.util.Log;

import com.gpit.android.app.BaseConst;
import com.gpit.android.util.SystemUtils;
import com.gpit.android.util.Utils;
import com.logentries.android.AndroidLogger;

public class RemoteLogger {
	private static AndroidLogger logger;
	private static Context context;
	private static BaseConst.LogLevel logLevel;

	public static void init(Context ctx, String token, BaseConst.LogLevel logLevel) {
		context = ctx;
		logger = AndroidLogger.getLogger(context, token, false);
		RemoteLogger.logLevel = logLevel;
	}
	
	public static void v(String tag, String msg) {
		msg = preprocessMsg(tag, msg);
		Log.v(tag, msg);
		
		if (logger == null) {
			return;
		}
		
		if (logger != null && (logLevel.ordinal() <= BaseConst.LogLevel.LOG_LEVEL_VERBOSE.ordinal())) {
			logger.verbose(msg);
		}
	}
	
	public static void d(String tag, String msg) {
		msg = preprocessMsg(tag, msg);
		Log.d(tag, msg);
		
		if (logger == null) {
			return;
		}
		
		if (logger != null && (logLevel.ordinal() <= BaseConst.LogLevel.LOG_LEVEL_DEBUG.ordinal())) {
			logger.debug(msg);
			logger.uploadAllLogs();
		}
	}
	
	public static void i(String tag, String msg) {
		msg = preprocessMsg(tag, msg);
		Log.i(tag, msg);
		
		if (logger == null) {
			return;
		}
		
		if (logger != null && (logLevel.ordinal() <= BaseConst.LogLevel.LOG_LEVEL_INFO.ordinal())) {
			logger.info(msg);
			logger.uploadAllLogs();
		}
	}
	
	public static void w(String tag, String msg) {
		if (logger == null) {
			return;
		}
		
		msg = preprocessMsg(tag, msg);
		
		Log.w(tag, msg);
		if (logger != null && (logLevel.ordinal() <= BaseConst.LogLevel.LOG_LEVEL_WARNING.ordinal())) {
			logger.warn(msg);
			logger.uploadAllLogs();
		}
	}
	
	public static void e(String tag, String msg) {
		msg = preprocessMsg(tag, msg);
		Log.e(tag, msg);
		
		if (logger == null) {
			return;
		}
		
		if (logger != null && (logLevel.ordinal() <= BaseConst.LogLevel.LOG_LEVEL_ERROR.ordinal())) {
			logger.error(msg);
			logger.uploadAllLogs();
		}
	}
	
	private static String preprocessMsg(String tag, String msg) {
		if (msg == null) {
			msg = "null";
		}
		
		String version = "N/A";
		if (context != null) {
			version = SystemUtils.getAppVersionName(context);
		}
		msg = String.format("[%s] %s: %s", version, tag, msg);

		return msg;
	}
}
