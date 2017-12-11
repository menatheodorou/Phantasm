/**
 * Define application constant variable
 */

package com.gpit.android.app;


public class BaseConst {
    public static final int LOG_LEVEL_VERBOSE = 0;
    public static final int LOG_LEVEL_DEBUG = 1;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_WARNING = 3;
    public static final int LOG_LEVEL_ERROR = 4;
    public enum LogLevel {
        LOG_LEVEL_VERBOSE,
        LOG_LEVEL_DEBUG,
        LOG_LEVEL_INFO,
        LOG_LEVEL_WARNING,
        LOG_LEVEL_ERROR,
    }

    // Mandatory required field should be set before use this class
    public static boolean DEBUG_MODE_ON = true;

	// Remote Logger for Logentires
	public static String REMOTE_LOGGER_TOKEN = null;
    public static String MINT_TOKEN = null;

	public static LogLevel REMOTE_LOGGER_LOGGING_LEVEL = LogLevel.LOG_LEVEL_VERBOSE;

    /********************* Product Configuration ***********************/
    public static String PRODUCT_REMOTE_LOGGER_TOKEN = null;
    public static String PRODUCT_MINT_TOKEN = null;
    public static LogLevel PRODUCT_REMOTE_LOGGER_LOGGING_LEVEL = LogLevel.LOG_LEVEL_DEBUG;

    /********************* Dev Configuration ***********************/
    public static String DEV_REMOTE_LOGGER_TOKEN = null;
    public static String DEV_MINT_TOKEN = null;
    public static LogLevel DEV_REMOTE_LOGGER_LOGGING_LEVEL = LogLevel.LOG_LEVEL_VERBOSE;
}
