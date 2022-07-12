package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.Utils.isBelowMinLength
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class ForgotPasswordFragment : BaseFragment() {

    lateinit var tempUser: UserItem
    lateinit var loginViewModel: LoginViewModel

    companion object {
        fun newInstance(user: UserItem) = ForgotPasswordFragment().apply {
            arguments = Bundle(2).apply {
                tempUser = user
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_forgot_password

    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        loginViewModel.forgotPassword(tempUser.phone).observe(this, Observer { response -> if (response?.status == Resource.Status.success) makeSnackbar(response?.data?.body?.get("code")?.asString) })
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)
    }

    override fun setEvents() {
        btn_back.setOnClickListener({ fragmentActivity.actionBack() })
        btn_resend_code.setOnClickListener({
            loginViewModel.resendCode(tempUser.phone).observe(this, Observer { response ->
                if (response?.status == Resource.Status.loading)
                    showLoader()
                else
                    hideLoader()
            })
        })
        btn_continue.setOnClickListener({ actionResetPassword() })
    }

    private fun actionResetPassword() {
        var code = pinview.text.toString()
        var pass = field_pass.text.toString()

        if (areFieldsValif(code, pass)) {

            loginViewModel.resetPassword(tempUser.phone, pass, code).observe(this, Observer { response ->
                when (response?.status) {

                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        fragmentActivity.actionBack()
                        fragmentActivity.resetDelay()
                        fragmentActivity.actionBack()
                    }
                    else -> {
                        hideLoader()
                        makeSnackbar(response?.data)
                    }
                }
            })
        }
    }

    private fun areFieldsValif(code: String, pass: String): Boolean {

        var valid = true;

        if (code.isNullOrBlank() || code.length < 4) {
            makeSnackbar("Please Enter the code you received on your entered number")
            valid = false
        }

        if (pass.length < 6) {
            inputlayout_pass.error = getString(R.string.error_validation_pass)
            valid = false
        } else
            inputlayout_pass.isErrorEnabled = false;

        return valid
    }

}