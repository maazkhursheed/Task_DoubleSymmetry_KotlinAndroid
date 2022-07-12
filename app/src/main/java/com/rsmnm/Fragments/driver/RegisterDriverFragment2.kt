package com.rsmnm.Fragments.driver

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.kbeanie.imagechooser.api.ChosenImage
import com.rsmnm.Activities.AccountActivity
import com.rsmnm.BaseClasses.ImageChooserFragment
import com.rsmnm.Fragments.PhoneVerificationFragment
import com.rsmnm.Interfaces.NumberSelected
import com.rsmnm.Interfaces.WorkCompletedInterface
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.Utils.initVerticalRecycler
import com.rsmnm.Utils.isBelowMinLength
import com.rsmnm.Utils.showNumberPickerDialog
import com.rsmnm.Utils.showRemoveImageDialog
import com.rsmnm.ViewHolders.CarPicsHolder
import com.rsmnm.ViewModels.LoginViewModel
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_driver_register_2.*
import java.io.File
import java.util.*
import android.content.Intent
import android.net.Uri
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.widget.TextView
import android.graphics.Paint


class RegisterDriverFragment2 : ImageChooserFragment(), View.OnClickListener {
    private lateinit var loginViewModel: LoginViewModel
    lateinit var tempUser: UserItem
    private var pickerId: Int? = null
    private var imgFile: File? = null
    private var mAdp: EfficientRecyclerAdapter<File>? = null
    private var car_pics = ArrayList<File>()

    companion object {
        fun newInstance(user: UserItem) = RegisterDriverFragment2().apply {
            arguments = Bundle(2).apply {
                tempUser = user
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_driver_register_2

    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        loginViewModel = ViewModelProviders.of(fragmentActivity).get(LoginViewModel::class.java)
        initVerticalRecycler(recyclerview, false, false)
        mAdp = EfficientRecyclerAdapter(R.layout.item_car_pics, CarPicsHolder::class.java, car_pics)
        recyclerview.setAdapter(mAdp)
    }

    override fun setEvents() {
        mAdp!!.setOnItemClickListener { adapter, view, `object`, position -> removeCarPic(position) }

        btn_back.setOnClickListener({ fragmentActivity.actionBack() })
        field_year.setOnClickListener({
            fragmentActivity.showNumberPickerDialog(NumberSelected { value ->
                field_year.setText(value.toString())
            })
        })
        field_license.setOnClickListener(this)
        btn_add_car_pics.setOnClickListener(this)
        field_insurance.setOnClickListener(this)
        field_registration.setOnClickListener(this)
        field_student_id.setOnClickListener(this)

        terms_and_conditions_text.setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ourrideshare.com/tos/"))
            startActivity(browserIntent)
        }

        terms_and_conditions_text.paintFlags = terms_and_conditions_text.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        btn_submit.setOnClickListener({ submit() })
    }

    private fun submit() {
        var vehicle_reg = field_vehicle_reg.text.toString()
        var make = field_make.text.toString()
        var model = field_model.text.toString()
        var year = field_year.text.toString()
        var license_no = field_licence_no.text.toString()
        var license = field_license.text.toString()
        var insurance = field_insurance.text.toString()
        var registration = field_registration.text.toString()
        var studentID = field_registration.text.toString()
        var agreeTermsConditions = terms_and_conditions.isChecked();

        if (areFieldsValid(vehicle_reg, make, model, year, license_no, license, insurance, registration, studentID,agreeTermsConditions)) {

            if (car_pics.size < 1) {
                makeSnackbar("Please add pictures of your Car.")
                return
            }

            var map = TreeMap<String, String>()
            map.put("first_name", tempUser.first_name)
            map.put("last_name", tempUser.last_name)
            map.put("email", tempUser.email)
            map.put("password", tempUser.password)
            map.put("user_type", UserItem.UserType.driver.name)
            map.put("phone", tempUser.phone)
            map.put("code", tempUser.pincode)
            map.put("inviter_code", tempUser.inviter_code)
            map.put("state", tempUser.state)
            map.put("city", tempUser.city)
            map.put("device_type", "android")
            map.put("device_token", "dummy_token")
            map.put("license_no", license_no)
            map.put("vehicle_registration", vehicle_reg)
            map.put("make", make)
            map.put("model", model)
            map.put("year", year)
            if (!tempUser.facebook_token.isNullOrEmpty())
                map.put("facebook_token", tempUser.facebook_token)

            loginViewModel.registerDriver(map, tempUser.profile_picture_file, tempUser._license_pic, tempUser._insurance_pic, tempUser._regisration_pic, tempUser._student_id, car_pics).observe(this, android.arch.lifecycle.Observer { webResponse ->
                when (webResponse?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        makeSnackbar(webResponse.data)
                        if (fragmentActivity is AccountActivity) {
                            fragmentActivity.clearBackStack()
                            fragmentActivity.resetDelay()
                            fragmentActivity.replaceFragmentWithBackstack(PhoneVerificationFragment.newInstance(UserItem()))
                            (fragmentActivity as AccountActivity).loginSuccessDriver(webResponse.data)
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

    private fun areFieldsValid(vehicle_reg: String, make: String, model: String, year: String, license_no: String, license: String, insurance: String, registration: String, studentID: String , agreeTermsConditions : Boolean): Boolean {

        var valid = true

        if (vehicle_reg.isBelowMinLength()) {
            inputlayout_vehicle_reg.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_vehicle_reg.isErrorEnabled = false

        if (make.isBelowMinLength()) {
            inputlayout_make.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_make.isErrorEnabled = false

        if (model.isBelowMinLength()) {
            inputlayout_model.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_model.isErrorEnabled = false

        if (year.isBelowMinLength()) {
            inputlayout_year.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_year.isErrorEnabled = false

//        if (license_no.isBelowMinLength()) {
//            inputlayout_license_no.error = getString(R.string.error_validation_null)
//            valid = false
//        } else
//            inputlayout_license_no.isErrorEnabled = false

        if (license.isBelowMinLength()) {
            inputlayout_driver_license.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_driver_license.isErrorEnabled = false

        if (insurance.isBelowMinLength()) {
            inputlayout_insurance.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_insurance.isErrorEnabled = false

        if (registration.isBelowMinLength()) {
            inputlayout_registration.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_registration.isErrorEnabled = false

        if (studentID.isBelowMinLength()) {
            inputlayout_student_id.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_student_id.isErrorEnabled = false

        if (agreeTermsConditions == false) {
            inputlayout_terms_and_conditions.error = getString(R.string.error_validation_terms_conditions)
            valid = false
        } else
            inputlayout_terms_and_conditions.isErrorEnabled = false


        return valid
    }

    override fun removeImage() {
    }

    override fun onImageChosen(image: ChosenImage?) {
        handler.post({
            imgFile = File(image?.fileThumbnail)
            when (pickerId) {
                R.id.field_license -> {
                    field_license.setText(imgFile?.name)
                    tempUser._license_pic = imgFile
                }
                R.id.btn_add_car_pics -> {
                    car_pics.add(imgFile!!)
                    mAdp?.notifyDataSetChanged()
                }
                R.id.field_insurance -> {
                    field_insurance.setText(imgFile?.name)
                    tempUser._insurance_pic = imgFile
                }
                R.id.field_registration -> {
                    field_registration.setText(imgFile?.name)
                    tempUser._regisration_pic = imgFile
                }
                R.id.field_student_id -> {
                    field_student_id.setText(imgFile?.name)
                    tempUser._student_id = imgFile
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        pickerId = p0?.id
        pickImage()
    }

    fun removeCarPic(adapterPosition: Int) {
        fragmentActivity.showRemoveImageDialog(WorkCompletedInterface {
            car_pics.removeAt(adapterPosition)
            mAdp?.notifyDataSetChanged()
        })
    }

}