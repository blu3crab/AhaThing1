/**
 * Copyright © 2015 Adaptive Handy Apps, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
// Project: AHA Smart Energy Explorer
// Contributor(s): M.A.Tucker
// Origination: FEB 2015
package com.adaptivehandyapps.ahathing.ahautils;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by mat on 2/24/2015.
 */
public class TimeUtils {

    private static final String TAG = "TimeUtils";

    public static final int SECS_IN_MIN = 60;
    public static final int MINS_IN_HOUR = 60;
    public static final int HOURS_IN_DAY = 24;
    public static final int DAYS_IN_WEEK = 7;
    public static final int DAYS_IN_MONTH = 30;
    public static final int MONTHS_IN_YEAR = 12;

    public static final int SECS_IN_HOUR = MINS_IN_HOUR * SECS_IN_MIN;
    public static final int SECS_IN_DAY = HOURS_IN_DAY * MINS_IN_HOUR * SECS_IN_MIN;
    public static final int SECS_IN_MONTH = DAYS_IN_MONTH * HOURS_IN_DAY * MINS_IN_HOUR * SECS_IN_MIN;

    ////////////////////////////////////////////////////////////////////////////
    // time related labels
    private static final List<String> textMonths = new ArrayList<>(
            Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
    public static List<String> getPrefsTextMonths(Context context) {
        return textMonths;
    }

    private static final List<String> textDays = new ArrayList<>(Arrays.asList(
//            "S", "M", "T", "W", "T", "F", "S"
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    ));
    public static List<String> getPrefsTextDays(Context context) {
        return textDays;
    }

    ///////////////////////////////////////////////////////////////////////////
    public static String secsToDate(long timeMs) {
        Date date = new Date(timeMs);
//        String pattern = "dd-MM-yyyy";
//        String pattern = "dd-MM-yyyy HH:mm:ss";
        String pattern = "ddMMMyy HH:mm:ss z";
//        String pattern = PrefsUtils.getPrefsDateFormat(context);

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String dateText = sdf.format(date);
//        Log.v(TAG, "secsToDate date:" + dateText);
        return dateText;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static String secsToDate(Context context, Integer timeSecs) {
        long timeMs = (long)timeSecs*1000;
//        Log.v(TAG, "secsToDate GMT timeMS:" + timeMs);
        Date date = new Date(timeMs);
//        String pattern = "dd-MM-yyyy";
//        String pattern = "dd-MM-yyyy HH:mm:ss";
        String pattern = "ddMMMyy HH:mm:ss z";
//        String pattern = PrefsUtils.getPrefsDateFormat(context);

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String dateText = sdf.format(date);
//        Log.v(TAG, "secsToDate date:" + dateText);
        return dateText;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static long dateToSecs(Context context, String dateText) {
        long timeSecs = 0;
//        String pattern = "dd-MM-yyyy";
//        String pattern = "dd-MM-yyyy HH:mm:ss";
        String pattern = "ddMMMyy HH:mm:ss z";
//        String pattern = PrefsUtils.getPrefsDateFormat(context);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date date = sdf.parse(dateText);
            timeSecs = date.getTime()/1000;
//            Log.v(TAG, " date: " + dateText + ", secs: " + timeSecs);
        } catch (ParseException ex) {
            Log.e(TAG, "dateToSecs exception:" + ex.getMessage());
        }
        return timeSecs;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static Integer getTodayMidnight() {
        // today
        Calendar date = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        long timeMs = date.getTimeInMillis();
        return (int)(timeMs / 1000);

//		// next day
//		date.add(Calendar.DAY_OF_MONTH, 1);
    }
    ///////////////////////////////////////////////////////////////////////////
    public static int getBeginningOfWeek(int timeSecs) {
        int begSecs;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long)timeSecs*1000);
        int day = c.get(Calendar.DATE);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // Get calendar, clear it and set week number and year.
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, year);

        begSecs = (int) (calendar.getTimeInMillis()/1000);
//        Log.i(TAG, "getBeginningOfWeek: " + begSecs + "(" + secsToDate(begSecs) + ")");
        return begSecs;
    }

    ///////////////////////////////////////////////////////////////////////////
    public static int getBeginningOfYear(int timeSecs) {
        int begSecs;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long)timeSecs*1000);
        int day = c.get(Calendar.DATE);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // Get calendar, clear it and set week number and year.
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.YEAR, year);

        begSecs = (int) (calendar.getTimeInMillis()/1000);
//        Log.i(TAG, "getBeginningOfYear: " + begSecs + "(" + secsToDate(begSecs) + ")");
        return begSecs;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static int getBegNextMonth(int timeSecs) {
        int begSecs;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long)timeSecs*1000);
        int day = c.get(Calendar.DATE);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // Get calendar, clear it and set week number and year.
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.YEAR, year);

        begSecs = (int) (calendar.getTimeInMillis()/1000);
//        Log.i(TAG, "getBegNextMonth: " + begSecs + "(" + secsToDate(begSecs) + ")");
        return begSecs;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static int getEndNextMonth(int timeSecs) {
        int begSecs;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long)timeSecs*1000);
        int day = c.get(Calendar.DATE);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // Get calendar, clear it and set week number and year.
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month + 2);
        calendar.set(Calendar.YEAR, year);

        begSecs = (int) (calendar.getTimeInMillis()/1000) - 1;
//        Log.i(TAG, "getEndNextMonth: " + begSecs + "(" + secsToDate(begSecs) + ")");
        return begSecs;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static int getYear(int timeSecs) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long) timeSecs * 1000);
        int day = c.get(Calendar.DATE);
        int week = c.get(Calendar.WEEK_OF_YEAR);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        return year;
    }
    ///////////////////////////////////////////////////////////////////////////
    public static String getFormattedDate(String dateTimePattern, long timeMs, boolean hoursOnlyToday) {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(timeMs);

        Calendar now = Calendar.getInstance();
        // if today
        if (now.get(Calendar.DATE) == time.get(Calendar.DATE) && hoursOnlyToday) {
            // use today format including seconds h:mm:ss with no date
            final String timeFormatString = "h:mm:ss aa";
//            return "Today at " + DateFormat.format(timeFormatString, time);
            return "Today " + DateFormat.format(timeFormatString, time);
        }
        else {
            // use input pattern time format
//            return DateFormat.format("MMM dd yyyy, h:mm:ss aa", time).toString();
            return DateFormat.format(dateTimePattern, time).toString();
        }
//        final String dateTimeFormatString = "MM-dd-yyyy, h:mm aa";
//        final long HOURS = 60 * 60 * 60;
//        if(now.get(Calendar.DATE) == time.get(Calendar.DATE) ){
//            return "Today " + DateFormat.format(timeFormatString, time);
//        }else if(now.get(Calendar.DATE) - time.get(Calendar.DATE) == 1 ){
//            return "Yesterday " + DateFormat.format(timeFormatString, time);
//        }else if(now.get(Calendar.YEAR) == time.get(Calendar.YEAR)){
//            return DateFormat.format(dateTimeFormatString, time).toString();
//        }else
//            return DateFormat.format("MMMM dd yyyy, h:mm aa", time).toString();
    }
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // DST
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    public static final String TIMEZONE_EST = "EST";
//    public static final String TIMEZONE_UTC = "UTC";
//
//    public static final int DST_START_DAY = 8;
//    public static final int DST_START_MONTH = 3;
//    public static final int DST_END_DAY = 1;
//    public static final int DST_END_MONTH = 11;
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    public static int getDaylightSavingsTimeOffset(long timeMs) {
//        int dstOffset = 5;  // default not daylight savings time
//
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(timeMs);
//        int day = c.get(Calendar.DATE);
//        int month = c.get(Calendar.MONTH);
//        if((month > DST_START_MONTH && month < DST_END_MONTH) ||
//                (month == DST_START_MONTH && day >= DST_START_DAY) ||
//                (month == DST_END_MONTH && day <= DST_END_DAY)) {
//            dstOffset = 4;
//        }
//        return dstOffset;
//    }
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    public static long shiftUtcMillisToEst(long timeMs) {
//        int dstOffset = getDaylightSavingsTimeOffset(timeMs);
//        timeMs += (dstOffset * SECS_IN_HOUR * 1000);
////        timeMs += (4 * FeedManager.SECS_IN_HOUR * 1000);
////        Log.v(TAG, "secsToDate GMT+4 timeMS:" + timeMs);
//        return timeMs;
//    }
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    public static long shiftEstMillisToUtc(long timeMs) {
//        int dstOffset = getDaylightSavingsTimeOffset(timeMs);
//        timeMs -= (dstOffset * SECS_IN_HOUR * 1000);
////        timeMs -= (4 * FeedManager.SECS_IN_HOUR * 1000);
////        Log.v(TAG, "secsToDate GMT-4 timeMS:" + timeMs);
//        return timeMs;
//    }
//    ////////////////////////////////////////////////////////////////////////////////////////////////
}
////////////////////////////////////////////////////////////////////////////////////////////////////
