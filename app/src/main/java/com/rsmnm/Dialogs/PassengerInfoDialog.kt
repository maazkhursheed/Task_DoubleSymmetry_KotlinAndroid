package com.rsmnm.Dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.rsmnm.BaseClasses.FragmentHandlingActivity
import com.rsmnm.Fragments.driver.ReceiptFragment
import com.rsmnm.Models.TripItem
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.rsmnm.Utils.createContactIntent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_user_info.*
import kotlinx.android.synthetic.main.dialog_user_info.view.*

/**
 * Created by saqib on 9/25/2018.
 */
class PassengerInfoDialog : DialogFragment() {

    lateinit var userItem: UserItem

    companion object {
        fun newInstance(item: UserItem) = PassengerInfoDialog().apply {
            arguments = Bundle(2).apply {
                userItem = item
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        txt_name.text = userItem.fullName
        rating.text = userItem.rating
        if (!userItem.profile_picture.isNullOrEmpty())
            Picasso.get().load(userItem.profile_picture).fit().centerCrop().into(img_user)


        btn_cancel.setOnRippleCompleteListener { dismiss() }
        btn_contact.setOnRippleCompleteListener { (activity as FragmentHandlingActivity).createContactIntent(userItem.phone) }
    }
}