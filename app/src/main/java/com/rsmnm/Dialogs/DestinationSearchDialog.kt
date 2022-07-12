package com.rsmnm.Dialogs

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.rsmnm.BaseClasses.FragmentHandlingActivity
import com.rsmnm.Interfaces.WorkCompletedInterface
import com.rsmnm.R
import com.rsmnm.Utils.getUserItem
import com.rsmnm.ViewModels.DriverViewModel
import kotlinx.android.synthetic.main.dialog_destination_search.*

/**
 * Created by saqib on 9/25/2018.
 */
class DestinationSearchDialog : DialogFragment() {

    lateinit var iface: WorkCompletedInterface
    var PLACE_PICKER_REQUEST = 1532;

    companion object {
        fun newInstance(iface: WorkCompletedInterface): DestinationSearchDialog {
            return DestinationSearchDialog().apply {
                this.iface = iface
            }
        }
    }

    lateinit var driverViewModel: DriverViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_destination_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        driverViewModel = ViewModelProviders.of(activity as FragmentHandlingActivity).get(DriverViewModel::class.java)



        if (getUserItem()?.destination_limit == 0) {
            txt_info.text = getUserItem()?.destination_limit.toString() + " of 2 remaining today. (you have exceeded your preferred destination feature limit for the day)"
            btn_destination.visibility = View.GONE
        } else {
            txt_info.text = getUserItem()?.destination_limit.toString() + " of 2 remaining today"
            btn_destination.visibility = View.VISIBLE
        }

        btn_destination.setOnRippleCompleteListener {
            dismiss()
            iface.onCompleted()
        }
        btn_cancel.setOnRippleCompleteListener { dismiss() }


    }

}