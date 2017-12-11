package com.gpit.android.util;


import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

public class StringUtils {
	/*************************************************************************************
	 * STRING FUNCTIONS
	 ************************************************************************************/
	public static int stringToInt(String value){
	    try
	    {
	      // the String to int conversion happens here
	      int i = Integer.parseInt(value.trim());
	      return i;
	    }
	    catch (NumberFormatException nfe)
	    {
	      System.out.println("NumberFormatException: " + nfe.getMessage());
	    }
		return -1;
	}

	public static void append(StringBuffer buffer, String prefix, String value) {
		buffer.append(prefix);
		buffer.append(value);
	}

	public static void appendLine(StringBuffer buffer, String prefix, String value) {
		buffer.append("\n");
		buffer.append(prefix);
		buffer.append(value);
	}

	public static boolean isNullOrEmpty(String string) {
		if (string == null || string.isEmpty())
			return true;

		return false;
	}

	public static boolean equalsStringExceptNull(String string1, String string2) {
		if (isNullOrEmpty(string1) || isNullOrEmpty(string2))
			return false;

		return string1.equals(string2);
	}

	public static boolean equalsStringIncludeNull(String string1, String string2) {
		if (string1 == string2) {
			return true;
		}

		if (string1 == null || string2 == null) {
			return false;
		}

		return string1.equals(string2);
	}

	public static boolean containInSensitiveString(String string1, String string2) {
		if (isNullOrEmpty(string1) || isNullOrEmpty(string2))
			return false;

		String lcString1 = string1.toLowerCase(Locale.getDefault());
		String lcString2 = string2.toLowerCase(Locale.getDefault());

		return lcString1.contains(lcString2);
	}

    public static String getString(Context context, int resId, String defaultValue) {
        String value = defaultValue;

        try {
            value = context.getString(resId);
        } catch (Exception e) {}

        return value;
    }
}
