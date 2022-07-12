package com.rsmnm.BaseClasses;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rsmnm.Activities.DriverActivity;
import com.rsmnm.Activities.PassengerActivity;
import com.rsmnm.Networking.WebResponse;
import com.rsmnm.Utils.AppConstants;
import com.rsmnm.Utils.StaticMethods;
import com.rsmnm.Utils.database.AppDatabase;
import com.rsmnm.Utils.permissionutils.FragmentManagePermission;
import com.rsmnm.Views.TitleBar;

public abstract class BaseFragment extends FragmentManagePermission {

    private Activity context;
    public FragmentHandlingActivity frragmentActivity;
    public Handler handler;
    public boolean isLoaded = false;

    private Gson gson;
    public View view;

    public AppDatabase roomDb;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayout(), container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    protected abstract int getLayout();

    protected abstract void getTitleBar(TitleBar titleBar);

    protected abstract void activityCreated(Bundle savedInstanceState);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new Handler(Looper.getMainLooper());
        if (context == null)
            context = getActivity();
        roomDb = AppDatabase.getInstance(context);


        if (context instanceof PassengerActivity)
            getTitleBar(((PassengerActivity) context).viewTitlebar);
        else if (context instanceof DriverActivity)
            getTitleBar(((DriverActivity) context).viewTitlebar);

        inits();
        setEvents();
        activityCreated(savedInstanceState);


        isLoaded = true;
    }

    public Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }


    public void showLoader() {
        StaticMethods.hideSoftKeyboard(getContext());
        getFragmentActivity().showLoader();
    }

    public void hideLoader() {
        getFragmentActivity().hideLoader();
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (AppConstants.sDisableFragmentAnimations) {
            Animation a = new Animation() {
            };
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    public FragmentHandlingActivity getFragmentActivity() {
        if (context == null)
            context = (Activity) getActivity();

        if (frragmentActivity == null) {
            if (context instanceof FragmentHandlingActivity) {
                frragmentActivity = (FragmentHandlingActivity) context;
            }
        }
        return frragmentActivity;
    }

    public Activity getContext() {
        if (context == null)
            context = getActivity();

        return context;
    }

    public void hideKeyboard() {
        StaticMethods.hideSoftKeyboard(getActivity());
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (context == null)
            context = (Activity) activity;
    }

    public String getFieldTexT(EditText edit) {
        try {
            return edit.getText().toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    abstract public void inits();

    abstract public void setEvents();


    public void Log(String tag, String value) {
        if (AppConstants.onTest) Log.e(tag, value);
    }

    public void Log(String value) {
        if (AppConstants.onTest) Log.e(getClass().getSimpleName() + "", value);
    }

    public void commingSoonToast() {
        makeToast("Will be implemented in BETA");
    }

    public void makeConnectionSnackbar() {
        ((BaseActivity) context).makeConnectionSnackbar();
    }

    public void makeSnackbar(WebResponse body) {
        ((BaseActivity) context).makeSnackbar(body);
    }

    public void makeSnackbar(String str) {
        ((BaseActivity) context).makeSnackbar(str);
    }

    public void makeToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void makeToastLong(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public String getText(String string) {
        if (string == null)
            return "";
        else return string;
    }

    @Override
    public void onStop() {
        try {
            getFragmentActivity().hideLoader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }


}
