package com.rsmnm.Fragments.driver

import android.os.Bundle
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.BaseClasses.ContactPickerFragment
import com.rsmnm.Interfaces.ContactPickedInterface
import com.rsmnm.R
import com.rsmnm.Utils.isBelowMinLength
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.fragment_recruit.*
import android.content.Intent
import android.net.Uri
import android.widget.RadioGroup
import com.rsmnm.Utils.StaticMethods
import com.rsmnm.Utils.getUserItem
import com.rsmnm.Utils.isValidEmail


/**
 * Created by saqib on 9/25/2018.
 */
class RecruitFragment : ContactPickerFragment() {
    override fun getLayout(): Int = R.layout.fragment_recruit

    lateinit var inviteMessage: String

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.resetTitleBar().setTitle("Recruit")
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        phoneCheck()
        if (!getUserItem()?.inviter_code.isNullOrEmpty())
            inviteMessage = getString(R.string.message_invite)
                    .replace("{link}", StaticMethods.getPlayStoreLink(context))
                    .replace("{code}", getUserItem()?.inviter_code!!)
        else
            inviteMessage = getString(R.string.message_invite_temp).replace("{link}", StaticMethods.getPlayStoreLink(context))
    }

    override fun setEvents() {
        btn_contacts.setOnRippleCompleteListener {
            pickContact(object : ContactPickedInterface {
                override fun onContactSelected(name: String, contact: String) {
                    field_name.setText(name)
                    field_mobile.setText(contact)
                }
            })
        }
        btn_send.setOnRippleCompleteListener {
            if (radio_phone.isChecked)
                sendSMS()
            else sendEmail()
        }

        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                if (radio_email.isChecked) {
                    emailCheck()
                } else {
                    phoneCheck()
                }
            }
        })
    }

    private fun phoneCheck() {
        inputlayout_email.isErrorEnabled = false
        field_email.isEnabled = false
        field_name.isEnabled = true
        field_mobile.isEnabled = true
        btn_contacts.isEnabled = true
    }

    private fun emailCheck() {
        inputlayout_firstname.isErrorEnabled = false
        inputlayout_mobile.isErrorEnabled = false
        field_email.isEnabled = true
        field_name.isEnabled = false
        field_mobile.isEnabled = false
        btn_contacts.isEnabled = false
    }

    private fun sendSMS() {
        var valid = true

        if (field_name.text.toString().isBelowMinLength()) {
            inputlayout_firstname.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_firstname.isErrorEnabled = false

        if (field_mobile.text.toString().isBelowMinLength()) {
            inputlayout_mobile.error = getString(R.string.error_validation_null)
            valid = false
        } else
            inputlayout_mobile.isErrorEnabled = false

        if (valid) {
            val msgIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", field_mobile.text.toString(), null))
            msgIntent.putExtra(Intent.EXTRA_TEXT, inviteMessage)
            startActivity(msgIntent)
        } else return
    }

    private fun sendEmail() {
        var valid = true

        if (!field_email.text.toString().isValidEmail()) {
            inputlayout_email.error = getString(R.string.error_validation_email)
            valid = false
        } else
            inputlayout_email.isErrorEnabled = false

        if (valid) {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + field_email.text.toString()))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Ride Share")
            emailIntent.putExtra(Intent.EXTRA_TEXT, inviteMessage)
            startActivity(Intent.createChooser(emailIntent, "Recruit Driver"))
        } else return
    }
}