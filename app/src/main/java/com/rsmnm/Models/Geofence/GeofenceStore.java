package com.rsmnm.Models.Geofence;

import android.text.format.DateUtils;

import com.google.android.gms.location.Geofence;

import java.util.HashMap;

public class GeofenceStore {
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS
            * DateUtils.HOUR_IN_MILLIS;
    protected HashMap<String, GeofenceModel> geofences = new HashMap<String, GeofenceModel>();
    private static GeofenceStore instance = new GeofenceStore();

    public static GeofenceStore getInstance() {
        return instance;
    }

    private GeofenceStore() {
//        geofences.put("The Shire", new GeofenceModel("The Shire", 51.663398, -0.209118,
//                100, GEOFENCE_EXPIRATION_IN_MILLISECONDS,
//                Geofence.GEOFENCE_TRANSITION_ENTER
//                        | Geofence.GEOFENCE_TRANSITION_DWELL
//                        | Geofence.GEOFENCE_TRANSITION_EXIT));
    }

    public HashMap<String, GeofenceModel> getSimpleGeofences() {
        return this.geofences;
    }
}
