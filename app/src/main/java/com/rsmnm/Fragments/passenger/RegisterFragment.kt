package com.rsmnm.Fragments.passenger

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kbeanie.imagechooser.api.ChosenImage
import com.rsmnm.Activities.AccountActivity
import com.rsmnm.BaseClasses.ImageChooserFragment
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.Utils.isBelowMinLength
import com.rsmnm.Utils.isValidEmail
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.File
import java.util.*

class RegisterFragment : ImageChooserFragment() {

    private lateinit var loginViewModel: LoginViewModel
    lateinit var tempUser: UserItem

    private var imgFile: File? = null

    companion object {
        fun newInstance(user: UserItem) = RegisterFragment().apply {
            arguments = Bundle(2).apply {
                tempUser = user
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_register

    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)

        if (!tempUser.first_name.isNullOrEmpty())
            field_firstname.setText(tempUser.first_name)
        if (!tempUser.last_name.isNullOrEmpty())
            field_lastname.setText(tempUser.last_name)
        if (!tempUser.email.isNullOrEmpty())
            field_email.setText(tempUser.email)
    }

    override fun setEvents() {
        btn_back.setOnClickListener { fragmentActivity.actionBack() }
        img_layout.setOnClickListener { pickImage() }

        btn_continue.setOnClickListener {
            val firstname = field_firstname.text.toString()
            val lastname = field_lastname.text.toString()
            val email = field_email.text.toString()
            var pass = field_pass.text.toString()

            if (areFieldsValid(firstname, lastname, email, pass)) {
                var map = TreeMap<String, String>()
                map.put("first_name", firstname);
                map.put("last_name", lastname);
                map.put("email", email);
                map.put("phone", tempUser.phone);
                map.put("password", pass);
                map.put("code", tempUser.pincode!!);
                map.put("user_type", UserItem.UserType.normal.name);
                map.put("device_type", "android");
                if (!tempUser.facebook_token.isNullOrEmpty())
                    map.put("facebook_token", tempUser.facebook_token)

                loginViewModel.register(map, imgFile).observe(this, Observer { webResponse ->
                    when (webResponse?.status) {
                        Resource.Status.loading -> showLoader()
                        Resource.Status.success -> {
                            hideLoader()
                            makeSnackbar(webResponse?.data)
                            if (fragmentActivity is AccountActivity)
                                (fragmentActivity as AccountActivity).loginSuccess(webResponse?.data?.body)
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

    private fun areFieldsValid(firstname: String, lastname: String, email: String, pass: String): Boolean {

        var valid = true

        if (firstname.isBelowMinLength()) {
            inputlayout_firstname.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_firstname.isErrorEnabled = false;

        if (lastname.isBelowMinLength()) {
            inputlayout_lastname.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_lastname.isErrorEnabled = false;

        if (lastname.isValidEmail()) {
            inputlayout_email.error = getString(R.string.error_validation_email)
            valid = false
        } else
            inputlayout_email.isErrorEnabled = false;

        if (pass.length < 6) {
            inputlayout_pass.error = getString(R.string.error_validation_pass)
            valid = false
        } else
            inputlayout_pass.isErrorEnabled = false;


        return valid
    }

    override fun removeImage() {
    }

    override fun onImageChosen(image: ChosenImage?) {
        handler.post {
            imgFile = File(image?.fileThumbnail)
            Picasso.get().load(imgFile!!).fit().centerCrop().into(img_user)
        }
    }

}