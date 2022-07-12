package com.rsmnm.Fragments.driver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.kbeanie.imagechooser.api.ChosenImage
import com.rsmnm.BaseClasses.ImageChooserFragment
import com.rsmnm.Models.CityItem
import com.rsmnm.Models.Resource
import com.rsmnm.Models.StateItem
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.Utils.DialogHelper
import com.rsmnm.Utils.isBelowMinLength
import com.rsmnm.Utils.isValidEmail
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_driver_register.*
import java.io.File

class RegisterDriverFragment : ImageChooserFragment() {

    lateinit var tempUser: UserItem
    private lateinit var loginViewModel: LoginViewModel
    private var imgFile: File? = null

    var selectedState: StateItem? = null
    var selectedCity: CityItem? = null

    companion object {
        fun newInstance(user: UserItem) = RegisterDriverFragment().apply {
            arguments = Bundle(2).apply {
                tempUser = user
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_driver_register

    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)
        loginViewModel.getStates()

        if (!tempUser.first_name.isNullOrEmpty())
            field_firstname.setText(tempUser.first_name)
        if (!tempUser.last_name.isNullOrEmpty())
            field_lastname.setText(tempUser.last_name)
        if (!tempUser.email.isNullOrEmpty())
            field_email.setText(tempUser.email)
    }

    override fun setEvents() {
        btn_back.setOnClickListener {
            fragmentActivity.actionBack()
        }

        img_layout.setOnClickListener {
            pickImage()
        }

        btn_next.setOnClickListener {
            var firstname = field_firstname.text.toString()
            var lastname = field_lastname.text.toString()
            var email = field_email.text.toString()
            var pass = field_password.text.toString()
            var inviteCode = field_invite_code.text.toString()


            if (areFieldsValid(firstname, lastname, email, pass)) {
                tempUser.first_name = firstname
                tempUser.last_name = lastname
                tempUser.email = email
                tempUser.password = pass
                tempUser.state = selectedState?.id
                tempUser.city = selectedCity?.id
                tempUser.inviter_code = inviteCode
                tempUser.profile_picture_file = imgFile

                loginViewModel.validateDriverPreRegisteration(selectedState?.id!!, selectedCity?.id!!, inviteCode).observe(this, Observer { response ->
                    when (response?.status) {
                        Resource.Status.loading -> showLoader()
                        Resource.Status.success -> {
                            hideLoader()
                            fragmentActivity.replaceFragmentWithBackstack(RegisterDriverFragment2.newInstance(tempUser))
                        }
                        Resource.Status.error -> {
                            DialogHelper.showAlertDialog(context!!, response.data?.message)
                            hideLoader()
                        }
                        else -> {
                            hideLoader()
                            makeSnackbar(response?.data)
                        }
                    }
                })
            }
        }

        field_state.setOnClickListener {
            loginViewModel.pickState(fragmentActivity).observe(this, Observer { response ->
                when (response?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        selectedState = response.data
                        field_state.setText(selectedState?.name)
                        loginViewModel.getCities(selectedState?.id!!)
                    }
                    Resource.Status.error -> {
                        hideLoader()
                        makeConnectionSnackbar()
                    }
                }
            })
        }

        field_city.setOnClickListener {
            if (selectedState != null)
                loginViewModel.pickCity(fragmentActivity, selectedState?.id!!).observe(this, Observer { response ->
                    when (response?.status) {
                        Resource.Status.loading -> showLoader()
                        Resource.Status.success -> {
                            hideLoader()
                            selectedCity = response.data
                            field_city.setText(selectedCity?.name)
                        }
                        Resource.Status.error -> {
                            hideLoader()
                            makeConnectionSnackbar()
                        }
                    }
                })
        }
    }

    private fun areFieldsValid(firstname: String, lastname: String, email: String, pass: String): Boolean {

        var valid = true

        if (imgFile == null) {
            makeSnackbar("Please select a profile image")
            valid = false
        }

        if (firstname.isBelowMinLength()) {
            inputlayout_firstname.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_firstname.isErrorEnabled = false

        if (lastname.isBelowMinLength()) {
            inputlayout_lastname.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_lastname.isErrorEnabled = false

        if (!email.isValidEmail()) {
            inputlayout_email.error = getString(R.string.error_validation_email)
            valid = false
        } else
            inputlayout_email.isErrorEnabled = false

        if (selectedState == null) {
            inputlayout_state.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_state.isErrorEnabled = false

        if (selectedCity == null) {
            inputlayout_city.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_city.isErrorEnabled = false

        if (pass.length < 6) {
            inputlayout_password.error = getString(R.string.error_validation_pass)
            valid = false
        } else
            inputlayout_password.isErrorEnabled = false

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