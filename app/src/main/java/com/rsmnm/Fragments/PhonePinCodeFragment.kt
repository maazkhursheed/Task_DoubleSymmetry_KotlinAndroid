package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.BuildConfig
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.Utils.AppConstants
import com.rsmnm.Utils.DateTimeHelper
import com.rsmnm.Utils.afterTextChanged
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.fragment_phone_pincode.*

class PhonePinCodeFragment : BaseFragment() {

    var runnable: Runnable? = null
    private lateinit var loginViewModel: LoginViewModel
    lateinit var tempUser: UserItem

    companion object {
        fun newInstance(user: UserItem) = PhonePinCodeFragment().apply {
            arguments = Bundle(2).apply {
                tempUser = user
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_phone_pincode

    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)
        initTimer()

        txt_message.text = txt_message.text.toString().replace("{phone}", tempUser.phone)

    }

    override fun setEvents() {
        btn_back.setOnClickListener({ fragmentActivity.actionBack() })
        btn_continue.setOnClickListener({ actionVerifyCode() })
        pinview.afterTextChanged { s: String ->
            if (s.length == 4) {
                hideKeyboard()
                actionVerifyCode()
            }
        }

        btn_editnumber.setOnClickListener { fragmentActivity.actionBack() }

        btn_resend_code.setOnClickListener {
            txt_timer.setTag(0.toDouble())
            loginViewModel.resendCode(tempUser.phone).observe(this, Observer { response ->
                if (response?.status == Resource.Status.loading)
                    showLoader()
                else {
                    hideLoader()
                }
            })
        }
    }

    private fun actionVerifyCode() {
        var pin = pinview.text.toString()
        if (pin.length > 3) {
            loginViewModel.phonePinVerification(tempUser.phone, pin).observe(this, Observer { webResponse ->
                when (webResponse?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        goRegister(pin)
                    }
                    else -> {
                        hideLoader()
                        makeSnackbar(webResponse?.data)
                    }
                }
            })
        } else
            makeSnackbar("Please Enter pin before proceeding")
    }

    private fun goRegister(pin: String) {
        if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER))
            fragmentActivity.replaceFragmentWithBackstack(com.rsmnm.Fragments.passenger.RegisterFragment.newInstance(tempUser.apply { pincode = pin }))
        else
            fragmentActivity.replaceFragmentWithBackstack(com.rsmnm.Fragments.driver.RegisterDriverFragment.newInstance(tempUser.apply { pincode = pin }))
    }

    private fun initTimer() {
        runnable = Runnable {
            if (isAdded) {
                var tag = txt_timer.getTag()
                var time: Double
                if (tag != null)
                    time = tag as Double
                else
                    time = 0.toDouble()

                time += 1000.toDouble();
                if (time > 1000 * 60 * 5) {
                    makeSnackbar("Verification Process expired")
                    fragmentActivity.actionBack()
                } else {
                    txt_timer.setText(DateTimeHelper.getMinutesSeconds(time.toLong()))
                    txt_timer.setTag(time)
                    handler.postDelayed(runnable, 1000)
                }
            }
        }

        handler.postDelayed(runnable, 1000)
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }
}