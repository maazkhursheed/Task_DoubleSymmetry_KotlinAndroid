package com.rsmnm.Utils;


import com.rsmnm.BuildConfig;

import org.jetbrains.annotations.Nullable;

/**
 * Created by rohail on 20-Jan-17.
 */

public class AppConstants {

    public static final String IS_TOLL_REQUIRED = "IS_TOLL_REQUIRED";
    public static String url_local = "http://192.168.168.114:81/ridesharemnm/public/api/v1/";
    public static String url_staging = "http://dev.appmaisters.com/ridesharemnm/public/api/v1/";
    public static String url_live = url_staging;

    public static String ServerUrl = url_live;

    public static final String FLAVOUR_PASSENGER = "passenger";
    public static final String FLAVOUR_DRIVER = "driver";

    public static final String PROFILE_UPDATE="account/me";

    public static boolean onTest = true;
    public static boolean sDisableFragmentAnimations = false;
    public static String PreferencesName = "mnm_v" + BuildConfig.VERSION_NAME;

    public static final String STRIPE_KEY = "pk_test_aKrfF4F8gpVXjEoF8wQ0eJS3";


    public static int FIELD_MINIMUM_LENGTH = 3;

    public static float RATE_PER_MILE = 0.25f;

    public static final String KEY_USER = "user_pref_obj";
    public static final String KEY_DISTANCE = "trip_distance";
    public static final String KEY_NOTIF_DATA = "notif_data";

    public static int MAX_DROP_LIMIT = 3;
    public static final float DEFAULT_ZOOM = 14.0f;
    public static final float INTERVAL_BETWEEN_LOCATION_UPDATES = 1000 * 60;

}
