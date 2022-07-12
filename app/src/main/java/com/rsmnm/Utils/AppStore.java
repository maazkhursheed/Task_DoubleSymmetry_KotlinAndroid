package com.rsmnm.Utils;

import android.arch.lifecycle.MutableLiveData;
import android.location.Location;
import android.util.Pair;

import com.rsmnm.Models.NotifObject;
import com.rsmnm.Models.TripItem;
import com.rsmnm.Models.TripListingItem;
import com.rsmnm.Models.UserItem;

/**
 * Created by rohail on 30-Jan-17.
 */
public class AppStore {

    private static AppStore ourInstance;

    public TripItem tripRequestItem;

    public MutableLiveData<Boolean> sessionExpireObservable = new MutableLiveData<>();
    public MutableLiveData<Boolean> validDocumentsObservable = new MutableLiveData<>();
    public MutableLiveData<Location> locationLiveData = new MutableLiveData<>();
    public MutableLiveData<NotifObject> notificationLiveData = new MutableLiveData<>();

    public MutableLiveData<Pair<String, TripListingItem>> myTripsLiveData = new MutableLiveData<>();

    public static AppStore getInstance() {
        if (ourInstance == null)
            ourInstance = new AppStore();
        return ourInstance;
    }

    public static void clearInstance() {
        ourInstance = null;
    }

    private AppStore() {
    }


}