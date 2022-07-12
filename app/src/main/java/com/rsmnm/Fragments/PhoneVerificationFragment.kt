package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.fragment_phone_verification.*

class PhoneVerificationFragment : BaseFragment() {

    lateinit var tempUser: UserItem
    lateinit var loginViewModel: LoginViewModel

    companion object {
        fun newInstance(user: UserItem) = PhoneVerificationFragment().apply {
            arguments = Bundle(2).apply {
                tempUser = user
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_phone_verification

    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)
    }

    override fun setEvents() {
        btn_back.setOnClickListener({ fragmentActivity.actionBack() })
        btn_continue.setOnClickListener({ actionContinue() })
        field_phone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    private fun actionContinue() {

        var phone = "+${txt_countrycode.selectedCountryCode + field_phone.text.toString()}"
        tempUser.phone = phone

        loginViewModel.phoneValidation(phone).observe(this, Observer { webResponse ->
            when (webResponse?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    fragmentActivity.replaceFragmentWithBackstack(PasswordFragment.newInstance(tempUser))
                }
                Resource.Status.action_signup -> {
                    hideLoader()
                    makeSnackbar(webResponse?.data)
                    fragmentActivity.replaceFragmentWithBackstack(PhonePinCodeFragment.newInstance(tempUser))
                }
                else -> {
                    hideLoader()
                    makeSnackbar(webResponse?.data)
                }
            }
        })
    }
}