package com.rsmnm.ViewHolders

import android.content.Context
import android.view.View
import android.widget.TextView
import com.rsmnm.Models.TripItem
import com.rsmnm.Models.WeeklyReportItem
import com.rsmnm.R
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder

/**
 * Created by saqib on 9/11/2018.
 */
class SummaryEarningsHolder(itemView: View?) : EfficientViewHolder<WeeklyReportItem>(itemView) {
    override fun updateView(context: Context, `object`: WeeklyReportItem) {

        findViewByIdEfficient<TextView>(R.id.txt_week).setText(`object`.week)
        findViewByIdEfficient<TextView>(R.id.txt_total_fare).setText(`object`.total_fare)
        findViewByIdEfficient<TextView>(R.id.txt_commision).setText(`object`.total_commission)
        findViewByIdEfficient<TextView>(R.id.txt_grand_total).setText(`object`.grand_total)
    }
}