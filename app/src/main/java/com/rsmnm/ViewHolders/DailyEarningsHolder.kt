package com.rsmnm.ViewHolders

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import com.rsmnm.Models.TripListingItem
import com.rsmnm.R
import com.rsmnm.Utils.DateTimeHelper
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder
import java.util.*

/**
 * Created by saqib on 9/11/2018.
 */
class DailyEarningsHolder(itemView: View?) : EfficientViewHolder<TripListingItem>(itemView) {
    override fun updateView(context: Context, earningItem: TripListingItem) {

        val date = earningItem.getDatetime()
        if (DateTimeHelper.isSameDay(date))
            findViewByIdEfficient<TextView>(R.id.txt_day).setText("Today " + DateTimeHelper.getTimeToShow(date))
        else if (isYesturday(date))
            findViewByIdEfficient<TextView>(R.id.txt_day).setText("Yesturday " + DateTimeHelper.getTimeToShow(date))
        else
            findViewByIdEfficient<TextView>(R.id.txt_day).setText(DateTimeHelper.getDateTimeToShow(date))

        findViewByIdEfficient<TextView>(R.id.txt_origin).setText(earningItem.pickup)
        findViewByIdEfficient<TextView>(R.id.txt_destination).setText(earningItem.dropoff)

        findViewByIdEfficient<TextView>(R.id.txt_distance_formulae).setText(earningItem.distance_text)
        findViewByIdEfficient<TextView>(R.id.txt_distance_amount).setText(earningItem.distance_fare)
        findViewByIdEfficient<TextView>(R.id.txt_base_fare).setText(earningItem.base_fare)
        findViewByIdEfficient<TextView>(R.id.txt_time_formulae).setText(earningItem.time_text)
        findViewByIdEfficient<TextView>(R.id.txt_time_amount).setText(earningItem.time_fare)
        findViewByIdEfficient<TextView>(R.id.txt_waiting_formulae).setText(earningItem.wait_time_text)
        findViewByIdEfficient<TextView>(R.id.txt_waiting_amount).setText(earningItem.wait_time_fare)
        findViewByIdEfficient<TextView>(R.id.txt_total).setText(earningItem.total_fare)
        findViewByIdEfficient<TextView>(R.id.txt_earning).setText(earningItem.earning)

        if (earningItem.toll_charges.contentEquals("$0.00"))
            findViewByIdEfficient<TextView>(R.id.toll_layout).visibility = View.GONE
        else
            findViewByIdEfficient<TextView>(R.id.toll_layout).visibility = View.VISIBLE

        findViewByIdEfficient<TextView>(R.id.txt_toll_amount).setText(earningItem.toll_charges)


        findViewByIdEfficient<LinearLayout>(R.id.ll).visibility = View.GONE
        findViewByIdEfficient<ToggleButton>(R.id.btn_arrow).setOnCheckedChangeListener(null)
        findViewByIdEfficient<ToggleButton>(R.id.btn_arrow).isChecked = false

        findViewByIdEfficient<ToggleButton>(R.id.btn_arrow).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                findViewByIdEfficient<LinearLayout>(R.id.ll).visibility = View.VISIBLE
            else findViewByIdEfficient<LinearLayout>(R.id.ll).visibility = View.GONE
        }
    }


    private fun isYesturday(date: Date?): Boolean {
        val yesturday = Calendar.getInstance()
        yesturday.timeInMillis = yesturday.timeInMillis - (1000 * 60 * 60 * 24)
        return DateTimeHelper.isSameDay(date, yesturday.time)
    }

}