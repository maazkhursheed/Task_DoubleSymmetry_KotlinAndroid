package com.rsmnm.Models;

import org.jetbrains.annotations.Nullable;

public class InvoiceItem {

    public String id;
    public String ride_id;
    public String driver_car_id;
    public String minimum_fare;
    public String base_fare;
    public String per_minute;
    public String per_minute_wait;
    public String per_mile;
    public String service_fee;
    public String cancellation_fee;
    public String surcharge;
    public int wait_time;
    public String travelled;
    public int duration;
    public String fare;
    public int is_paid;
    public String created_at;
    public String updated_at;

    public String updated_ts;
    public String created_ts;

    @Nullable
    public String getDuration() {
        StringBuilder builder = new StringBuilder();

        if (duration == 0)
            builder.append("Less then a minute");
        else if (duration > 60) {
            int hour = duration / 60;
            int minutes = duration % 60;
            builder.append(hour).append(" hour ").append(minutes).append(" min");
        } else {
            builder.append(duration).append(" min");
        }

        return builder.toString();
    }

    public String getWaitTime() {
        StringBuilder builder = new StringBuilder();

        if (wait_time == 0)
            builder.append("Less then a minute");
        else if (wait_time > 60) {
            int minutes = wait_time / 60;
            int seconds = wait_time % 60;
            builder.append(minutes).append(" hour ").append(seconds).append(" min");
        } else {
            builder.append(wait_time).append(" min");
        }

        return builder.toString();
    }

    public String getBase_fare() {
        return "$" + base_fare;
    }

    public String getFare() {
        return "$" + fare;
    }
}
