package com.rsmnm.Models;

import android.text.TextUtils;

import com.rsmnm.Utils.DateTimeHelper;

import java.util.Date;

public class NotifObject {


    public NotificationAction data_click_action;
    public String data_message;
    public String data_title;
    public String title;
    public String message;

    public String ride_id;
    public String datetime;

    public enum NotificationAction {
        new_ride,
        ride_canceled,
        driver_arrived,
        ride_started,
        ride_ended,
        future_ride_assigned,
        marked_offline
    }

    public String getTitle() {
        return TextUtils.isEmpty(data_title) ? title : data_title;
    }

    public String getMessage() {
        return TextUtils.isEmpty(data_message) ? message : data_message;
    }

    public Date getDatetime() {
        return new Date(Long.parseLong(datetime + "000"));
    }
}
