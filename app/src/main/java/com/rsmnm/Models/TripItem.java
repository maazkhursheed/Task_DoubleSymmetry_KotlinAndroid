package com.rsmnm.Models;

import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by saqib on 9/11/2018.
 */

public class TripItem {
    public String day;

    public String passenger_id;
    public String car_type_id;
    public String city_id;
    public String pickup_title;
    public String pickup_latitude;
    public String pickup_longitude;
    public int ride_status;
    public String updated_at;
    public String created_at;
    public String ride_id;
    public String driver_id;
    public TripStatus ride_status_text;
    public ArrayList<LocationItem> dropoffs;
    public String pickup_state;
    public String pickup_city;
    public Long start_time;
    public Long arrive_time;
    private ArrayList<LocationItem> tolls;

    // For Ride Later Time
    public Long scheduled_at;

    public UserItem driver;
    public UserItem passenger;

    public InvoiceItem invoice;

    private boolean isPickupCompleted = false;

    private int is_blind;

    public TripItem(String day) {
        this.day = day;
    }

    public TripItem() {
    }

    public boolean isPickupCompleted() {
        if (TextUtils.isEmpty(pickup_latitude) || TextUtils.isEmpty(pickup_longitude) || !isPickupCompleted)
            return false;
        else return true;
    }

    public void setPickupCompleted(boolean pickupCompleted) {
        isPickupCompleted = pickupCompleted;
    }

    public boolean isPickDataValid() {
        if (TextUtils.isEmpty(pickup_latitude) || TextUtils.isEmpty(pickup_longitude) || TextUtils.isEmpty(pickup_state) || TextUtils.isEmpty(pickup_city))
            return false;
        else return true;
    }

    public int getIs_blind() {
        return is_blind;
    }

    public void setIs_blind(int is_blind) {
        this.is_blind = is_blind;
    }

    public ArrayList<LocationItem> getTolls() {
        return tolls;
    }

    public void setTolls(ArrayList<LocationItem> tolls) {
        this.tolls = tolls;
    }


    public enum TripStatus {
        active,
        arrived,
        started,
        ended,
        canceled
    }
}
