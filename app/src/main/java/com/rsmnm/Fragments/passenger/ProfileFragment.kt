package com.rsmnm.Fragments.passenger

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import com.kbeanie.imagechooser.api.ChosenImage
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.BaseClasses.ImageChooserFragment
import com.rsmnm.BuildConfig
import com.rsmnm.Models.Resource
import com.rsmnm.R
import com.rsmnm.Utils.*
import com.rsmnm.ViewModels.PassengerViewModel
import com.rsmnm.Views.TitleBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File
import java.util.*

/**
 * Created by saqib on 9/10/2018.
 */
class ProfileFragment : ImageChooserFragment() {

    lateinit var viewModel: PassengerViewModel
    var selectedFile: File? = null

    override fun getLayout(): Int = R.layout.fragment_profile

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.resetTitleBar().setTitle("Profile").enableBack()
    }

    override fun activityCreated(savedInstanceState: Bundle?) {

    }

    override fun inits() {

        viewModel = ViewModelProviders.of(fragmentActivity).get(PassengerViewModel::class.java)

        getUserLiveData().observe(this, Observer { userItem ->
            userItem?.apply {
                field_firstname.setText(first_name)
                field_lastname.setText(last_name)
                field_email.setText(email)
                field_phone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
                field_phone.setText(phone)
                if (!profile_picture.isNullOrEmpty())
                    Picasso.get().load(profile_picture).fit().centerCrop().into(img_user)
            }
        })
    }

    override fun setEvents() {

        img_user.setOnClickListener { pickImage() }

        btn_update.setOnRippleCompleteListener {
            var map: TreeMap<String, String> = TreeMap()
            map.put("_token", getUserItem()!!.token)
            map.put("first_name", field_firstname.text.toString())
            map.put("last_name", field_lastname.text.toString())

            if (!field_password.text.toString().isEmpty()) {
                if (passwordChangeValid()) {
                    map.put("old_pwd", field_password.text.toString())
                    map.put("password", field_password_new.text.toString())
                } else
                    return@setOnRippleCompleteListener
            }

            viewModel.updateProfile(map, selectedFile?.getRequestBody("profile_picture")).observe(this, Observer {
                when (it?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        field_password.setText("")
                        field_password_new.setText("")
                        hideLoader()
                        makeSnackbar(it.data?.message)
                        var userItem = it.data?.body
                        if (userItem != null)
                            setUserItem(userItem)
                    }
                    else -> {
                        hideLoader()
                        makeSnackbar(it?.data?.message)
                    }
                }
            })
        }
    }

    private fun passwordChangeValid(): Boolean {

        var valid = true

        if (field_password.text!!.length < 5) {
            inputlayout_password.error = "Must be minimum 6 characters"
            valid = false
        } else
            inputlayout_password.isErrorEnabled = false

        if (field_password_new.text!!.length < 5) {
            inputlayout_password2.error = "Must be minimum 6 characters"
            valid = false
        } else
            inputlayout_password2.isErrorEnabled = false

        return valid
    }

    override fun removeImage() {
    }

    override fun onImageChosen(image: ChosenImage?) {
        handler.post({
            selectedFile = File(image?.fileThumbnail)
            Picasso.get().load(selectedFile!!).fit().centerCrop().into(img_user)
        })
    }
}