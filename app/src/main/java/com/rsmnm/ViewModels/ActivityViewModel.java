package com.rsmnm.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.rsmnm.Models.UserItem;
import com.rsmnm.Utils.AppConstants;
import com.rsmnm.Utils.PreferencesManager;


/**
 * Created by rohail on 26-Oct-17.
 */

public class ActivityViewModel extends AndroidViewModel {

    public ActivityViewModel(@NonNull Application application) {
        super(application);
    }

}
