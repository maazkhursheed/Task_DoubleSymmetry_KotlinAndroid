package com.rsmnm.Activities

import android.arch.lifecycle.ViewModelProviders
import android.media.MediaPlayer
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessaging
import com.rsmnm.BaseClasses.BaseActivity
import com.rsmnm.BuildConfig
import com.rsmnm.Interfaces.AuthenticationStatus
import com.rsmnm.Interfaces.LogoutInterface
import com.rsmnm.Models.Resource
import com.rsmnm.R
import com.rsmnm.Utils.*
import com.rsmnm.Utils.permissionutils.PermissionResult
import com.rsmnm.Utils.permissionutils.PermissionUtils
import com.rsmnm.ViewModels.AccountsViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import android.arch.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.rsmnm.Models.UserItem

class SplashActivity : BaseActivity(), LogoutInterface {
    lateinit var viewMode: AccountsViewModel;
    var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseAuth.getInstance().signOut()
        viewMode = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
        img_logo.animate().alpha(1.0f).setDuration(2000).start()
        DeveloperHelper.logTokens(mContext)

        GlobalScope.launch(Dispatchers.Main) {
            val userObj = getUserItem()
            GlobalScope.async(Dispatchers.IO) { delay(5000) }.await()
//            finish()
            if (userObj == null) {
                finish()
                startActivity<AccountActivity>()
            } else {
                //    if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) startActivity<PassengerActivity>() else startActivity<DriverActivity>()
                GetUserAuth(userObj)
            }
        }
    }

    fun GetUserAuth(userObj: UserItem?) {
        AuthenticateUser(userObj?.firebase_token, this, AuthenticationStatus {
            if (it) {
                finish()
                if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) startActivity<PassengerActivity>() else startActivity<DriverActivity>()
            } else {
                if (count == 0) {
                    userObj?.token?.let { it1 -> GetProfile(it1) }
                } else {
                    actionLogout()
                }
            }
        })
    }

    fun GetProfile(token: String) {
        count++
        viewMode.getUserProfile(token).observe(this, Observer {
            when (it?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    GetUserAuth(it.data?.body)
                }
                else -> {
                    actionLogout()
                    makeSnackbar(it?.data)
                }
            }
        })

    }

    override fun showLoader() {

    }

    override fun hideLoader() {

    }

    override fun actionLogout() {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/android1")
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.format("/topics/user_%s", getUserItem()?.user_id))
            AppStore.getInstance().sessionExpireObservable.postValue(null)
            viewMode.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setUserItem(null)
        startActivity<AccountActivity>()
        finish()
    }

}
