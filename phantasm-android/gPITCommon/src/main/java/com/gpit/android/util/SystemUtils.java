package com.gpit.android.util;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

public class SystemUtils {
	// App
	public final static int DEVICE_TYPE_BETA = 0;
	public final static int DEVICE_TYPE_10 = 1;
	public final static int DEVICE_TYPE_11 = 2;
	public final static int DEVICE_TYPE_CUPCAKE = 3;
	public final static int DEVICE_TYPE_DONUT = 4;
	public final static int DEVICE_TYPE_ECLAIR = 5;
	public final static int DEVICE_TYPE_FROYO = 6;
	public final static int DEVICE_TYPE_GINGERBREAD = 7;
	public final static int DEVICE_TYPE_HONEYCOMB = 8;
	public final static int DEVICE_TYPE_ICS = 9;

	public final static String[] deviceTypes = new String[] {
			"Beta",
			"1.0",
			"1.1",
			"Cupcake",
			"Donut",
			"Eclair",
			"Froyo",
			"Gingerbread",
			"Honeycomb",
			"Ice Cream Sandwich",
	};

	// Special - Application Detail
	private static final String SCHEME = "package";
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	private static final String APP_PKG_NAME_22 = "pkg";
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	private static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";

	// Version
	private final static String APP_VERSION_TITLE = "%s (v%s - No.%d)";
	private final static String APP_VERSION = "v%s - No.%d";

	/*************************************************************************************
	 * APP & SYSTEM FUNCTIONS
	 ************************************************************************************/
	public static String getGmailAcount(Context context) {
		AccountManager manager = AccountManager.get(context);
		Account[] accounts = manager.getAccountsByType("com.google");
		String acount = "";

		if (accounts != null && accounts.length > 0)
			acount = accounts[0].name;

		return acount;
	}

	public static String[] getOwners(Context context) {
		final AccountManager manager = AccountManager.get(context);
		final Account[] accounts = manager.getAccountsByType("com.google");
		final int size = accounts.length;
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = accounts[i].name;
		}

		return names;
	}

	public static UserName spiltName(Context context, String fullName) {
		UserName userName = new UserName();
		int start = fullName.indexOf(' ');
		int end = fullName.lastIndexOf(' ');

		if (start >= 0) {
			userName.firstName = fullName.substring(0, start);
			if (end > start)
				userName.middleName = fullName.substring(start + 1, end);
			userName.lastName = fullName.substring(end + 1, fullName.length());
		}

		return userName;
	}

	public static void showAppDetailView(Context context) {
		Intent intent;

		if (android.os.Build.VERSION.SDK_INT >= 9) {
	        /* on 2.3 and newer, use APPLICATION_DETAILS_SETTINGS with proper URI */
			Uri packageURI = Uri.parse("package:" + context.getPackageName());
			intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI);
		}  else  {
	        /* on older Androids, use trick to show app details */
			intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void showSetting(Context context) {
		Intent intent;

		intent = new Intent(Settings.ACTION_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		context.startActivity(intent);
	}

	public static void switchToHome(Context context) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN)
				.addCategory(Intent.CATEGORY_HOME)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Check to see if a recognition activity is present
	 * @param context
	 * @return
	 */
	public static boolean isExistVoiceRecognizeActivity(Context context) {
		boolean isExist;

		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		isExist = (activities.size() != 0);

		return isExist;
	}

	public static int getApiLevel() {
		int apiLevel = Build.VERSION.SDK_INT;

		return apiLevel;
	}

	public static String getDeviceID(Context context) {
		String androidID = Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);

		return androidID;
	}

	public static String getDeviceName(Context context) {
		return android.os.Build.DEVICE;
	}

	public static int getDeviceTypeID() {
		int apiLevel = getApiLevel();
		int deviceTypeID = DEVICE_TYPE_BETA;

		if (apiLevel <= 1) {
			deviceTypeID = DEVICE_TYPE_10;
		} else if (apiLevel <= 2) {
			deviceTypeID = DEVICE_TYPE_11;
		} else if (apiLevel <= 3) {
			deviceTypeID = DEVICE_TYPE_CUPCAKE;
		} else if (apiLevel <= 4) {
			deviceTypeID = DEVICE_TYPE_DONUT;
		} else if (apiLevel <= 6) {
			deviceTypeID = DEVICE_TYPE_ECLAIR;
		} else if (apiLevel <= 8) {
			deviceTypeID = DEVICE_TYPE_FROYO;
		} else if (apiLevel <= 10) {
			deviceTypeID = DEVICE_TYPE_GINGERBREAD;
		} else if (apiLevel <= 13) {
			deviceTypeID = DEVICE_TYPE_HONEYCOMB;
		} else if (apiLevel <= 15) {
			deviceTypeID = DEVICE_TYPE_ICS;
		}

		return deviceTypeID;
	}

	public static String getDeviceType() {
		int deviceTypeID = getDeviceTypeID();

		return deviceTypes[deviceTypeID];
	}

	public static String getOSVersion() {
		String osVersion = System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
		return osVersion;
	}

	@Deprecated // 2012/05/17
	public static int getAppVersion(Context context) {
		return getAppVersionCode(context);
	}

	public static int getAppVersionCode(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return pInfo.versionCode;
		} catch (Exception e) {
			return 0;
		}
	}

	public static String getAppVersionName(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return "";
		}
	}

	public static String getAppstoreLink(Context context) {
		String appstoreLink = "https://play.google.com/store/apps/details?id=" + context.getPackageName();

		return appstoreLink;
	}

	public static String getIMEID(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String getSimSerialNumber = tm.getSimSerialNumber();

		return getSimSerialNumber;
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager mTelephonyMgr;
		String phoneNumber;

		mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		phoneNumber = mTelephonyMgr.getLine1Number();

		return phoneNumber;
	}

	public static String getMarketURL(Context context) {
		String marketURL = String.format("https://market.android.com/details?id=%s", context.getPackageName());

		return marketURL;
	}

	public static String getVersionTitle(Activity activity) {
		// Show application version name & code
		String versionName = getAppVersionName(activity);
		int versionCode = getAppVersionCode(activity);
		String versionTitle = String.format(APP_VERSION_TITLE, getApplicationName(activity), versionName, versionCode);

		return versionTitle;
	}

	public static String getVersion(Context context) {
		// Show application version name & code
		String versionName = getAppVersionName(context);
		int versionCode = getAppVersionCode(context);
		String versionTitle = String.format(APP_VERSION, versionName, versionCode);

		return versionTitle;
	}

	public static String getApplicationName(Context context) {
		int stringId = context.getApplicationInfo().labelRes;
		return context.getString(stringId);
	}

	public static void showVersionTitle(Activity activity) {
		activity.setTitle(getVersionTitle(activity));
	}

	public static boolean launchApp(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);

		if (intent != null) {
			// We found the activity now start the activity
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);

			return true;
		} else {
			// Bring user to the market or let them choose an app?
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("market://details?id=" + packageName));
			context.startActivity(intent);

			return false;
		}
	}


	public static void showInstalledAppDetails(Context context, String packageName) {
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // above 2.3
			intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else { // below 2.3
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);
	}

	public static void killActivities(Context context) {
		Intent killIntent = new Intent("killMyActivity");
		killIntent.setType("text/plain");
		context.sendBroadcast(killIntent);
	}

	public static ArrayList<String> getGmailAccounts(Context context) {
		ArrayList<String> gmailAccounts = new ArrayList<String>();

		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accountss = AccountManager.get(context).getAccountsByType("com.google");

		for (Account account : accountss) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;

				gmailAccounts.add(possibleEmail);
			}
		}

		return gmailAccounts;
	}
}
