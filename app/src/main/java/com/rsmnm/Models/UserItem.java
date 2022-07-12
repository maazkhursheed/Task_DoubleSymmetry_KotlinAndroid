package com.rsmnm.Models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Part;

/**
 */

public class UserItem {

    public String user_id;
    public String user_type;
    public String first_name;
    public String last_name;
    public String email;
    public String rating = "0.0";
    public String profile_picture;
    public File profile_picture_file;
    public String phone;
    public String address;
    public String city;
    public String state;
    public String notifications;
    @SerializedName("_token")
    public String token;

    public String password;
    public Integer destination_limit;
    public String inviter_code;
    public String license_no;

    public String vehicle_registration;
    public String make;
    public String model;
    public String year;
    public File _car_pic;
    public ArrayList<String> car_pic = new ArrayList<>();
    public Document inspection_pic;
    public Document student_id;
    public Document license_pic;
    public Document insurance_pic;
    public Document vehicle_registration_pic;

    public File _inspection_pic;
    public File _student_id;
    public File _license_pic;
    public File _insurance_pic;
    public File _regisration_pic;

    public String pincode;
    public String facebook_token;
    public int is_online;

    public String stripe_id;
    public String redirect_url;
    public String success_url;
    public String fail_url;
    public String firebase_token;

    public VehicleTypeItem car;

    public enum UserType {
        normal, driver
    }

    public String getFullName() {
        return String.format("%s %s", first_name, last_name);
    }

}
