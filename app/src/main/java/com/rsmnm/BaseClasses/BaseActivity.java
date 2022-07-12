package com.rsmnm.BaseClasses;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rsmnm.Networking.WebResponse;
import com.rsmnm.Utils.AppConstants;
import com.rsmnm.Utils.StaticMethods;
import com.rsmnm.Utils.permissionutils.ActivityManagePermission;


/**
 * Created by rohail on 4/18/2016.
 */
public abstract class BaseActivity extends ActivityManagePermission {

    private static final int REQUEST_CHECK_SETTINGS = 1123;
    public Activity mContext = this;
    public Handler handler;
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        StaticMethods.initPreferences(mContext);
    }

    public abstract void showLoader();

    public abstract void hideLoader();

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    public void Log(String tag, String value) {
        try {
            if (AppConstants.onTest) Log.e(tag, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeConnectionToast() {
        Toast.makeText(mContext, "Request Failed, Please Check Internet Connection", Toast.LENGTH_SHORT).show();
    }

    public void makeConnectionSnackbar() {
        makeSnackbar("Request Failed, Please Check Internet Connection");
    }

    public void makeToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void makeToastLong(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public void makeSnackbar(WebResponse body) {
        try {
            if (body == null || body.message == null)
                makeConnectionSnackbar();
            else
                makeSnackbar(body.message);
        } catch (Exception e) {
            e.printStackTrace();
            makeConnectionToast();
        }
    }

    public void makeSnackbar(String str) {
        try {
            if (parentView == null)
                makeToastLong(str + "");
            else {
                Snackbar snackbar = Snackbar.make(parentView, str + "", Snackbar.LENGTH_LONG);
                TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackbar.show();
                tv.setTextColor(Color.WHITE);
            }
            Log.e("snackbar", str + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
