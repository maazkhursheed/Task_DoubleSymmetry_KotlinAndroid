package com.rsmnm.Activities

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.firebase.messaging.FirebaseMessaging
import com.rsmnm.BaseClasses.FragmentHandlingActivity
import com.rsmnm.BuildConfig
import com.rsmnm.Fragments.LoginFragment
import com.rsmnm.Interfaces.AuthenticationStatus
import com.rsmnm.Models.UserItem
import com.rsmnm.Networking.WebResponse
import com.rsmnm.R
import com.rsmnm.Utils.*
import com.rsmnm.ViewModels.AccountsViewModel
import kotlinx.android.synthetic.main.activity_account.*
import android.arch.lifecycle.Observer
import com.rsmnm.Interfaces.LogoutInterface
import com.rsmnm.Models.Resource

class AccountActivity : FragmentHandlingActivity(),LogoutInterface {
    lateinit var viewMode: AccountsViewModel;
    var count=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        viewMode = ViewModelProviders.of(this).get(AccountsViewModel::class.java)
        replaceFragment(LoginFragment())

    }

    override fun getFrameLayoutId(): Int = R.id.frame_main


    override fun showLoader() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        progressBar.visibility = View.GONE
    }

    fun loginSuccess(user: UserItem?) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/android1")
            FirebaseMessaging.getInstance().subscribeToTopic(String.format("/topics/user_%s", user?.user_id))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        GetUserAuth(user)
    }

    fun GetUserAuth(user:UserItem?){
        AuthenticateUser(user?.firebase_token, this, AuthenticationStatus {
            if (it) {
                setUserItem(user!!)
                if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) startActivity<PassengerActivity>() else startActivity<DriverActivity>()
                finish()
            } else {
                if(count==0) {
                    user?.token?.let { it1 -> GetProfile(it1) }
                }else{
                    actionLogout()
                }
            }
        })
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

    fun GetProfile(token: String) {
        count++
        viewMode.getUserProfile(token).observe(this, Observer {
            when (it?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    loginSuccess(it.data?.body)
                }
                else -> {
                   actionLogout()
                    makeSnackbar(it?.data)
                }
            }
        })

    }

    override fun actionBack(): Boolean {
        if (webview.visibility == View.VISIBLE) {
            webview.visibility = View.GONE
            return true
        }
        return super.actionBack()
    }

    fun loginSuccessDriver(response: WebResponse<UserItem>?, isFromSignup: Boolean = true) {

        if (response?.body?.stripe_id.isNullOrEmpty()) {
            webview.visibility = View.VISIBLE
            val webSettings = webview.getSettings()
            webSettings.setJavaScriptEnabled(true)

            webview.loadUrl(response?.body?.redirect_url)
            webview.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    showLoader()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    hideLoader()
                    Log("String URL", url)
                    when (url) {
                        response?.body?.success_url -> {
                            webview.visibility = View.GONE
                            if (isFromSignup)
                                DialogHelper.showAlertDialog(mContext, "Please check your email to verify your account.")
                            else {
                                loginSuccess(response?.body!!)
                            }
                            clearBackStack()
                        }
                        response?.body?.fail_url -> {
                            webview.visibility = View.GONE
                            makeSnackbar("Stripe registration Failed")
                        }
                    }
                }
            }
        } else
            loginSuccess(response?.body!!)
    }
}