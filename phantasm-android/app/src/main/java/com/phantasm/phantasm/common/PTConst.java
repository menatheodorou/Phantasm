/*
 * Copyright (c) 2016 Joseph Luns. All rights reserved.
 * This file is part of Joseph Luns.
 *
 * @author Joseph Luns (josephluns15@hotmail.com)
 */
package com.phantasm.phantasm.common;


public class PTConst {
	// Web API
	public static String WEB_SERVICE_URL = "http://api.phantasmgif.com/rest/phantasmwtf/";

    public static final boolean DEBUG_MODE_ON = true;

	public static final int MAX_MEDIA_COUNT = 100;

    public static final String BROADCAST_MESSAGE_ACTION_MEDIA_LIST_LOADED = "action_media_list_loaded";
    public static final String BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA_LIST = "param_media_list";

	public static final String BROADCAST_MESSAGE_ACTION_MEDIA_SELECTED = "action_media_selected";
	public static final String BROADCAST_MESSAGE_PARAM_SELECTED_MEDIA = "param_media";

    public static final int SLIDNIG_LAYOUT_INITIAL_TOP_PERCENT = 60;

	public static final int CHOOSE_PHOTO_FROM_GALLERY = 10;
	public static final int TAKE_PHOTO_FROM_CAMERA = 11;
	public static final String FRAGMENT_SIGNUP_CODE = "FRAGMENT_SIGNUP_CODE";

	/********************* Active Configuration *********************/
	// Remote Logger for Logentires
	public static String REMOTE_LOGGER_TOKEN = "cc89dd16-482a-4083-ab0f-72d6c047ddc6";

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
	public static LogLevel REMOTE_LOGGER_LOGGING_LEVEL = LogLevel.LOG_LEVEL_VERBOSE;

	// Flurry
	public final static String MY_FLURRY_APIKEY = "3C9NR7VZY5BX598GRN6Q";
    /********************* Product Configuration ***********************/

    /********************* Dev Configuration ***********************/

	public static final String ACTION_SIGNIN = "signIn";
	public static final String ACTION_SIGNUP = "signUp";
	public static final String TAG_DATA = "data";
	public static final String TAG_ERROR = "error";
	public static final String TAG_USER = "user";
	public static final String TAG_RECORD = "record";

	public static class Params {
		public static final String IS_SIGNIN			= "is_signin";
		public static final String DEVICE_TOKEN			= "device_token";
		public static final String USER_ID              = "user_id";
		public static final String PHONE_NUMBER			= "phone_number";
		public static final String PASSWORD             = "password";
		public static final String USER_NAME            = "user_name";
		public static final String EMAIL				= "email";
		public static final String PHOTO				= "photo";
	}
}
