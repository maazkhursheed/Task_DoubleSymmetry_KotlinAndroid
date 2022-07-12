package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.rsmnm.Activities.AccountActivity
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.BuildConfig
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.Utils.AppConstants
import com.rsmnm.Utils.isBelowMinLength
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.fragment_password.*

class PasswordFragment : BaseFragment() {

    lateinit var tempUser: UserItem
    lateinit var loginViewModel: LoginViewModel

    companion object {
        fun newInstance(user: UserItem) = PasswordFragment().apply {
            arguments = Bundle(2).apply {
                tempUser = user
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_password


    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)
    }

    override fun setEvents() {
        btn_back.setOnClickListener { fragmentActivity.actionBack() }
        btn_continue.setOnClickListener { actionLogin() }
        btn_forgotpass.setOnClickListener { fragmentActivity.replaceFragmentWithBackstack(ForgotPasswordFragment.newInstance(tempUser)) }
    }

    private fun actionLogin() {
        var pass = field_pass.text.toString()
        if (pass.isBelowMinLength())
            makeSnackbar(getString(R.string.error_validation_null))
        else
            loginViewModel.login(tempUser.phone, pass).observe(this, Observer { webResponse ->
                when (webResponse?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        if (fragmentActivity is AccountActivity) {
                            if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER))
                                (fragmentActivity as AccountActivity).loginSuccess(webResponse?.data?.body)
                            else
                                (fragmentActivity as AccountActivity).loginSuccessDriver(webResponse?.data,false)
                        }
                    }
                    else -> {
                        hideLoader()
                        makeSnackbar(webResponse?.data)
                    }
                }
            })
    }
}