package com.rsmnm.Dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.rsmnm.Interfaces.DateTimeSelectedInterface
import com.rsmnm.R
import com.rsmnm.Utils.DateTimeHelper
import com.rsmnm.Utils.showDatePopup
import com.rsmnm.Utils.showTimePopup
import kotlinx.android.synthetic.main.dialog_ridelater.*
import java.util.*
import java.util.Calendar.*

class RideLaterDialog : DialogFragment() {

    lateinit var iface: DateTimeSelectedInterface

    val selectedDateTime: Calendar = Calendar.getInstance()


    companion object {
        fun newInstance(iface: DateTimeSelectedInterface): RideLaterDialog {
            return RideLaterDialog().apply {
                this.iface = iface
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_ridelater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE)
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view_date.setOnClickListener {
            showDatePopup(context!!, DateTimeSelectedInterface {
                selectedDateTime.apply {
                    set(YEAR, it.get(YEAR))
                    set(MONTH, it.get(MONTH))
                    set(DAY_OF_MONTH, it.get(DAY_OF_MONTH))
                }
                setFormatedDayText()
            })
        }

        view_time.setOnClickListener {
            showTimePopup(context!!, DateTimeSelectedInterface {
                selectedDateTime.apply {
                    set(HOUR, it.get(HOUR))
                    set(MINUTE, it.get(MINUTE))
                }
                setFormatedTimeText()
            })
        }

        view_bg.setOnClickListener { dismiss() }

        btn_confirm.setOnRippleCompleteListener {
            if (txt_day.text.isNullOrEmpty() || txt_time.text.isNullOrEmpty())
                Toast.makeText(context, "Please Select Date and Time to Schedule a future Ride", Toast.LENGTH_LONG).show()
            else {
                dismiss()
                iface.onDateTimeSelected(selectedDateTime)
            }
        }
    }

    private fun setFormatedTimeText() {
        val laterTime = Date()
        laterTime.time = selectedDateTime.time.time
        laterTime.time = laterTime.time + (1000 * 60 * 15)
        txt_time.text = DateTimeHelper.getTimeToShow(selectedDateTime.time) + " - " + DateTimeHelper.getTimeToShow(laterTime)
    }

    private fun setFormatedDayText() {
        txt_day.text = DateTimeHelper.getFormattedDate(selectedDateTime.time, "EEE, MMM d")
    }
}