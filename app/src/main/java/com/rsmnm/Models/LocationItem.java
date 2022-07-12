package com.rsmnm.Models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by saqib on 9/11/2018.
 */
@Entity
public class LocationItem {

    @NonNull
    @PrimaryKey
    @SerializedName("title")
    public String address;

    @Nullable
    public String name;
    @SerializedName("lat")
    public String latitude;
    @SerializedName("long")
    public String longitude;

    @Ignore
    public LocationItem(String name, String address, String latitude, String longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationItem() {
    }

    public void copy(LocationItem item) {
        name = item.name;
        address = item.address;
        latitude = item.latitude;
        longitude = item.longitude;
    }

    public boolean isEmpty() {
        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude))
            return true;
        else
            return false;
    }

    public void clear() {
        name = "";
        address = "";
        latitude = "";
        longitude = "";
    }
}
