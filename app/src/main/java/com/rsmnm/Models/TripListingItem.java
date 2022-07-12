package com.rsmnm.Models;

import com.rsmnm.Utils.DateTimeHelper;

import java.util.Date;

public class TripListingItem {

    public String ride_id;
    public String pickup;
    public String dropoff;
    public String datetime;
    public String date;
    public String earning;
    public String base_fare;
    public String total_fare;
    public String distance_text;
    public String distance_fare;
    public String time_text;
    public String time_fare;
    public String wait_time_text;
    public String wait_time_fare;
    public String toll_charges;
    public int is_paid;
    public UserItem passenger;

    public Date getDatetime() {
        return new Date(Long.parseLong(datetime + "000"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TripListingItem) {
            return ((TripListingItem) obj).ride_id.equals(ride_id);

        }
        return super.equals(obj);
    }
}
