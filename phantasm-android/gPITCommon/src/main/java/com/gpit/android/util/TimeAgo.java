package com.gpit.android.util;

import android.content.Context;

import com.gpit.android.library.R;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1) );
    public static List<String> timesString = null;

    private static void initTimeString(Context context) {
        timesString = Arrays.asList(
                context.getString(R.string.year_ago), context.getString(R.string.years_ago),
                context.getString(R.string.month_ago), context.getString(R.string.months_ago),
                context.getString(R.string.day_ago), context.getString(R.string.days_ago),
                context.getString(R.string.hour_ago), context.getString(R.string.hours_ago),
                context.getString(R.string.min_ago), context.getString(R.string.mins_ago),
                context.getString(R.string.second_ago), context.getString(R.string.seconds_ago));
    };

    public static String getTimeAgo(Context context, long time) {
        if (timesString == null) {
            initTimeString(context);
        }

        long duration = new Date().getTime() - time;
        if (duration <= 0) return "";

        String timeAgo = null;
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long unitCount = duration / current;

            if (unitCount > 0) {
                int index = i * 2;
                if (unitCount > 1) index++;
                timeAgo = String.format(Locale.getDefault(), timesString.get(index), unitCount);
                break;
            }
        }


        if (timeAgo == null) {
            timeAgo = String.format(Locale.getDefault(), context.getString(R.string.second_ago), 0);
        }

        return timeAgo;
    }
}