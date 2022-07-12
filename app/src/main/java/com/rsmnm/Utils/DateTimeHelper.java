package com.rsmnm.Utils;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by rohail on 27-Mar-17.
 */

public class DateTimeHelper {

    public static String DATE_TO_SHOW_FORMAT = "MM/dd/yyyy";
    public static String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static String SERVER_DATE_FORMAT = "yyyy-MM-dd";


    public static String getForServerDate(Date mDate) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
            return daysFormat.format(mDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDate(String date) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT, Locale.US);
            return daysFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date parseDate(String date, String format) {
        try {
            SimpleDateFormat daysFormat = new SimpleDateFormat(format, Locale.US);
            return daysFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFormattedDate(Date mDate) {
        SimpleDateFormat daysFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT);
        return daysFormat.format(mDate);
    }
    public static String getFormattedDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format(DATE_TO_SHOW_FORMAT, cal).toString();
        return date;

    }

    public static String getFormattedDate(Date mDate, String format) {
        SimpleDateFormat daysFormat = new SimpleDateFormat(format, Locale.US);
        return daysFormat.format(mDate);
    }

    public static String getDateToShow(String date_time) {
        if (TextUtils.isEmpty(date_time))
            return "";
        String dateStr = "";
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date date = serverFormat.parse(date_time);
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT, Locale.US);
            dateStr = showFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String getDateToShow(Date date_time) {
        String dateStr = "";
        try {
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT, Locale.US);
            dateStr = showFormat.format(date_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String getDateTimeToShow(String date_time) {
        String dateStr = "";
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            Date date = serverFormat.parse(date_time);
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT + " h:mm a", Locale.US);
            dateStr = showFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    public static String getDateTimeToShow(Date date_time) {
        try {
            SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT + " h:mm a", Locale.US);
            return showFormat.format(date_time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTimeToShow(Date date) {
        try {
            SimpleDateFormat showFormat = new SimpleDateFormat("h:mm a", Locale.US);
            return showFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMinutesSeconds(long date_time) {
        try {
            Date date = new Date(date_time);
            SimpleDateFormat showFormat = new SimpleDateFormat("mm:ss", Locale.US);
            return showFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date convertFromUTC(Date date) {
        try {
            int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
            return new Date(offset + date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getChatDateTime(Date date1) {
        try {
            if (DateUtils.isToday(date1.getTime())) {
                SimpleDateFormat showFormat = new SimpleDateFormat("h:mm a", Locale.US);
                return showFormat.format(date1);
            } else {
                SimpleDateFormat showFormat = new SimpleDateFormat(DATE_TO_SHOW_FORMAT + " h:mm a", Locale.US);
                return showFormat.format(date1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static Date getNonUTCTimeStamp(Date tag) {
        Calendar rightNow = Calendar.getInstance();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);
        long millis = tag.getTime() + offset;
        tag.setTime(millis);
        return tag;
    }

    public static boolean isSameDay(@Nullable Date date) {
        Calendar today = Calendar.getInstance();
        Calendar paramDat = Calendar.getInstance();
        paramDat.setTime(date);

        return today.get(Calendar.DAY_OF_YEAR) == paramDat.get(Calendar.DAY_OF_YEAR) &&
                today.get(Calendar.YEAR) == paramDat.get(Calendar.YEAR);
    }



    public static boolean isSameDay(@Nullable Date date1, Date date2) {
        Calendar today = Calendar.getInstance();
        Calendar paramDat = Calendar.getInstance();
        today.setTime(date1);
        paramDat.setTime(date2);

        return today.get(Calendar.DAY_OF_YEAR) == paramDat.get(Calendar.DAY_OF_YEAR) &&
                today.get(Calendar.YEAR) == paramDat.get(Calendar.YEAR);
    }

    public static Date convertServerTimeStamp(String datetime) {
        return DateTimeHelper.convertFromUTC(new Date(Long.parseLong(datetime) * 1000));
    }
}
