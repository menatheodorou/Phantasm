
package com.gpit.android.util;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class contains utils methods for parsing JSON data.
 * 
 */
public class JsonParserUtils {
	/**
	 * @param obj
	 * @param key
	 * @return
	 * @throws org.json.JSONException
	 */
	public static String getValue(JSONObject obj, String key, String defaultValue) {
        String value = defaultValue;
        try {
            value = obj.has(key) ? obj.get(key).toString() : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return value;
	}

    public static String getStringValue(JSONObject obj, String key, String defaultValue) {
        return getValue(obj, key, defaultValue);
    }

	public static String getStringValue(JSONObject obj, String key) {
		return getValue(obj, key, null);
	}

    public static int getIntValue(JSONObject obj, String key) {
        return getIntValue(obj, key, 0);
    }

	public static int getIntValue(JSONObject obj, String key, int defaultValue) {
        try {
            int value = 0;
            if (obj.has(key)) {
                value = obj.getInt(key);
            }

            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return defaultValue;
	}

    public static long getLongValue(JSONObject obj, String key) {
        return getLongValue(obj, key, 0);
    }

	public static long getLongValue(JSONObject obj, String key, long defaultValue) {
		long value = defaultValue;
		if (obj.has(key)) {
			try {
				value = obj.getLong(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static boolean getBooleanValue(JSONObject obj, String key) {
		return getStringValue(obj, key, "").equals("1") || getStringValue(obj, key, "").equals("y") ||
                getStringValue(obj, key, "").equals("YES") || getIntValue(obj, key) == 1;
	}
	
	public static boolean getBooleanValue(JSONObject obj, String key, boolean defaultValue) {
		if (obj.has(key)) {
			return getBooleanValue(obj, key);
		} else {
			return defaultValue;
		}
	}

    public static float getFloatValue(JSONObject obj, String key) {
        return getFloatValue(obj, key, 0);
    }

	public static float getFloatValue(JSONObject obj, String key, float defaultValue) {
		float value = defaultValue;
		if (obj.has(key)) {
			try {
				value = (float) obj.getDouble(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

    public static double getDoubleValue(JSONObject obj, String key) {
        return getDoubleValue(obj, key, 0);
    }

	public static double getDoubleValue(JSONObject obj, String key, double defaultValue) {
		double value = defaultValue;
		if (obj.has(key)) {
			try {
				value = obj.getDouble(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static int getColorValue(JSONObject obj, String key) {
		return getColorValue(obj, key, 0xFFFFFF);
	}

	public static int getColorValue(JSONObject obj, String key, int defaultValue) {
		String colorValue = getValue(obj, key, null);

		if (colorValue == null) {
			return defaultValue;
		}

        int color = defaultValue;
        try {
            if (!colorValue.startsWith("#")) {
                colorValue = "#" + colorValue;
            }

            color = Color.parseColor(colorValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return color;
	}

	public static long getDateValue(JSONObject obj, String key, String format) {
		return getDateValue(obj, key, format, 0);
	}

	public static long getDateValue(JSONObject obj, String key, String format, long defaultValue) {
		long value = defaultValue;
		if (obj.has(key)) {
			try {
				String timeString = obj.getString(key);
				value = TimeUtils.getDate(timeString, format).getTime();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static JSONObject getJSONValue(JSONObject obj, String key) {
		JSONObject object = null;

		// It can be "null" value even its json object, so it can't be parse as JSON object.
		// In this case, we have to return null object can be handle outside.
		// Ex: Instagram's caption field can be "null" value or JSON object field
		try { 
			object = obj.has(key) ? obj.getJSONObject(key) : null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}

    public static JSONArray getJSONArrayValue(JSONObject obj, String key) {
        JSONArray array = null;

        // It can be "null" value even its json object, so it can't be parse as JSON object.
        // In this case, we have to return null object can be handle outside.
        // Ex: Instagram's caption field can be "null" value or JSON object field
        try {
            array = obj.has(key) ? obj.getJSONArray(key) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }
}
