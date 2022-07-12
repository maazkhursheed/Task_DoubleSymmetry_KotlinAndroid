package com.rsmnm.Services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationTracker implements LifecycleObserver {

    public MutableLiveData<Location> locationData = new MutableLiveData<>();
    Activity context;
    boolean isOneTimeOnly;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private boolean isDestroyed = false;


    public LocationTracker(Activity context, boolean isOneTimeOnly) {
        this.context = context;
        this.isOneTimeOnly = isOneTimeOnly;
    }

    @SuppressLint("MissingPermission")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onCreate() {
        if (isDestroyed) {
            Log.e("LocaitonUpdate", "skiped start, already performed task");
            return;
        }
        Log.e("LocaitonUpdate", "Started");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(context, location -> {
                    if (location != null) {
                        Log.e("LocaitonUpdate", location.toString());
                        locationData.postValue(location);
                        if (!isOneTimeOnly)
                            startLocationUpdates();
                    } else {
                        startLocationUpdates();
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.e("LocaitonUpdate", location.toString());
                    locationData.postValue(location);
                    if (isOneTimeOnly)
                        onDestroy();
                }
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onDestroy() {
        try {
            isDestroyed = true;
            Log.e("LocaitonUpdate", "Closed");
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
