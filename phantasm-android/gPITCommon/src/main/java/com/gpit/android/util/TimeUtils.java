package com.gpit.android.util;


import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {
    public static final int SECOND_MILISECONDS = 1000;
    public static final int MIN_MILISECONDS = 60 * SECOND_MILISECONDS;
    public static final int HOUR_MILISECONDS = 60 * MIN_MILISECONDS;
    public static final int DAY_MILISECONDS = 24 * HOUR_MILISECONDS;
    public static final int WEEK_MILISECONDS = 7 * DAY_MILISECONDS;

    public static long getTimeSecs() {
        return TimeUtils.getTimeMilis() / 1000;
    }

    public static long getTimeMilis() {
        return System.currentTimeMillis();
    }

    // Ex: tiemzone: "UTC+8"
    public static long getTimeMilis(String timezone) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        // cal.setTimeInMillis(getTimeMilis());
        long milis = cal.getTimeInMillis();

        return milis;
    }

	/*
    	// We have to consider local timezone at here, because start of date is difference based on timezone
		// Remove offset timezone from timestamp
		timestamp -= Utils.getTimezoneOffset();
	 */

    public static long getDateTimeMilis() {
        Date nowDate = new Date();
        return getDateTimeMilis(nowDate.getTime());
    }

    private static void resetDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.getTimeInMillis();
    }

    public static long getDateTimeMilis(long timestamp) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        date.setTime(timestamp);
        cal.setTime(date);
        resetDate(cal);

        timestamp = cal.getTimeInMillis();

        return timestamp;
    }

    public static long getWeekTimeMilis() {
        Date nowDate = new Date();
        return getWeekTimeMilis(nowDate.getTime());
    }

    public static long getWeekTimeMilis(long timestamp) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        date.setTime(timestamp);
        cal.setTime(date);
        resetDate(cal);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        timestamp = cal.getTimeInMillis();

        return timestamp;
    }

    public static long getWeekTimeMilis(long timestamp, int weekIndex) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        date.setTime(timestamp);
        cal.setTime(date);
        resetDate(cal);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.getTimeInMillis();
        cal.set(Calendar.WEEK_OF_MONTH, weekIndex);

        return cal.getTimeInMillis();
    }

    public static long getDayTimeMilis(long timestamp, int dayIndex) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        date.setTime(timestamp);
        cal.setTime(date);
        resetDate(cal);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + dayIndex);

        return cal.getTimeInMillis();
    }

    public static long getDateTimeMilis(long timestamp, int dateIndex) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        date.setTime(timestamp);
        cal.setTime(date);
        resetDate(cal);

        cal.set(Calendar.DATE, dateIndex + 1);

        return cal.getTimeInMillis();
    }

    public static long getMonthTimeMilis(long miliseconds) {
        Date date = new Date(miliseconds);

        return getMonthTimeMilis(miliseconds, date.getMonth());
    }

    public static long getMonthTimeMilis(long timestamp, int monthIndex) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        date.setTime(timestamp);
        cal.setTime(date);
        resetDate(cal);

        cal.set(Calendar.MONTH, monthIndex);

        return cal.getTimeInMillis();
    }

    public static long getYearTimeMilis(long miliseconds) {
        Date date = new Date(miliseconds);

        return getYearTimeMilis(miliseconds, date.getYear() + 1900);
    }

    public static long getYearTimeMilis(long timestamp, int year) {
        Calendar cal = new GregorianCalendar();
        Date date = new Date();
        date.setTime(timestamp);
        cal.setTime(date);
        resetDate(cal);

        cal.set(Calendar.YEAR, year);

        return cal.getTimeInMillis();
    }

    public static final int getYearsDifference(Date date1, Date date2) {
        int y1 = date1.getYear();
        int y2 = date2.getYear();
        return y2 - y1;
    }

    public static final int getMonthsDifference(Date date1, Date date2) {
        int m1 = date1.getYear() * 12 + date1.getMonth();
        int m2 = date2.getYear() * 12 + date2.getMonth();
        return m2 - m1;
    }

    public static final long getDaysDifference(Date date1, Date date2) {
        long diff = getTimeMilisecondsDifference(date1, date2) / DAY_MILISECONDS;

        return diff;
    }

    public static final long getHoursDifference(Date date1, Date date2) {
        long diff = getTimeMilisecondsDifference(date1, date2) / HOUR_MILISECONDS;

        return diff;
    }

    public static final long getMinsDifference(Date date1, Date date2) {
        long diff = getTimeMilisecondsDifference(date1, date2) / MIN_MILISECONDS;

        return diff;
    }

    public static final long getSecondsDifference(Date date1, Date date2) {
        long diff = getTimeMilisecondsDifference(date1, date2) / SECOND_MILISECONDS;

        return diff;
    }

    public static final long getTimeMilisecondsDifference(Date date1, Date date2) {
        long t1 = date1.getTime();
        long t2 = date2.getTime();

        return t2 - t1;
    }

    public static String getStringDate(long longDate) {
        String resultDate;

        // TimeZone mytimezone = TimeZone.getDefault();
        // int offset = mytimezone.getRawOffset();

        //long tmpPubDate = Long.parseLong(longDate + "000") + offset;
        long tmpPubDate = Long.parseLong(longDate + "000");
        //Long tmpPubDate = Long.valueOf(longDate*1000);

        Date publicationDate = new Date(tmpPubDate);

        SimpleDateFormat formatter_date = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        // formatter_date.setTimeZone(TimeZone.getTimeZone("UTC"));
        resultDate = formatter_date.format(publicationDate);

        return resultDate;
    }

    public static String getEnStringDate(String dateString) {

        SimpleDateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat mediumFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        // shortFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        // mediumFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String resultDate = null;

        try {
            resultDate = mediumFormat.format(shortFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return resultDate;
    }

    public static String getDateFromEnDate(String dateEnString) {

        SimpleDateFormat formatter_one = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat formatter_two = new SimpleDateFormat("yyyy-MM-dd");
        // formatter_one.setTimeZone(TimeZone.getTimeZone("UTC"));
        // formatter_two.setTimeZone(TimeZone.getTimeZone("UTC"));

        ParsePosition pos = new ParsePosition(0);
        Date frmTime = formatter_one.parse(dateEnString, pos);
        String returnString = formatter_two.format(frmTime);

        return returnString;
    }

    public static String getDateString(long time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String dateString;

        // dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateString = dateFormat.format(new Date(time));

        return dateString;
    }

    /**
     * Get date from string
     *
     * @param dateStr
     * @return
     */
    public static Date getDate(String dateStr) {
        return getDate(dateStr, "dd/MM/yyyy");
    }

    public static Date getDate(String dateStr, String format) {
        return getDate(dateStr, format, TimeZone.getDefault());
    }

    public static Date getDate(String dateStr, String format, TimeZone timezone) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = null;

        dateFormat.setTimeZone(timezone);

        try {
            date = dateFormat.parse(dateStr);
            // Log.i("date", "year = " + (1900 + date.getYear()) + ", month = " + date.getMonth() + ", date = " + date.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static int getTimezoneOffset() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();

        return mGMTOffset;
    }
}
