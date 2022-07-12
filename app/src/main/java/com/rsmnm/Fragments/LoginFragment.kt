package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.facebook.AccessToken
import com.rsmnm.Activities.AccountActivity
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : FacebookAuthFragment() {

    private lateinit var loginViewModel: LoginViewModel

    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        initFaceBook()
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)
        BounceView.addAnimTo(btn_login_phone)
        BounceView.addAnimTo(btn_login_fb)
    }

    override fun setEvents() {
        btn_login_phone.setOnClickListener { fragmentActivity.replaceFragmentWithBackstack(PhoneVerificationFragment.newInstance(UserItem())) }
        btn_login_fb.setOnClickListener {
            loginWithFaceBook { `object`, response ->
                loginViewModel.fbLogin(AccessToken.getCurrentAccessToken().token).observe(this, Observer { webResponse ->
                    when (webResponse?.status) {
                        Resource.Status.loading -> showLoader()
                        Resource.Status.success -> {
                            hideLoader()
                            makeSnackbar(webResponse?.data)
                            if (fragmentActivity is AccountActivity)
                                (fragmentActivity as AccountActivity).loginSuccess(webResponse?.data?.body)
                        }
                        Resource.Status.action_signup -> {
                            hideLoader()
                            fragmentActivity.replaceFragmentWithBackstack(PhoneVerificationFragment.newInstance(UserItem().apply {
                                facebook_token = AccessToken.getCurrentAccessToken().token
                                var fullname = (`object`.get("name") as String?)?.split(" ")
                                first_name = fullname!![0]
                                if (fullname.size > 1)
                                    last_name = fullname!![1]
                                if (`object`.has("email"))
                                    email = `object`.get("email") as String?
                            }))
                        }
                        else -> {
                            hideLoader()
                            makeSnackbar(webResponse?.data)
                        }
                    }
                })
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_login
}