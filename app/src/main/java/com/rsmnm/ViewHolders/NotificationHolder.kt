package com.rsmnm.ViewHolders

import android.content.Context
import android.view.View
import android.widget.TextView
import com.rsmnm.Models.NotifObject
import com.rsmnm.R
import com.rsmnm.Utils.DateTimeHelper
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder
import java.util.*

class NotificationHolder(itemView: View?) : EfficientViewHolder<NotifObject>(itemView) {

    override fun updateView(context: Context?, item: NotifObject?) {

        findViewByIdEfficient<TextView>(R.id.txt_message).text = item?.getMessage()
//        findViewByIdEfficient<TextView>(R.id.txt_time).text = DateTimeHelper.getDateTimeToShow(item?.getDatetime())

        val date = item?.getDatetime()
        date?.let {
            if (DateTimeHelper.isSameDay(it))
                findViewByIdEfficient<TextView>(R.id.txt_time).setText("Today " + DateTimeHelper.getTimeToShow(it))
            else if (isYesturday(it))
                findViewByIdEfficient<TextView>(R.id.txt_time).setText("Yesturday " + DateTimeHelper.getTimeToShow(it))
            else
                findViewByIdEfficient<TextView>(R.id.txt_time).setText(DateTimeHelper.getDateTimeToShow(it))
        }
    }

    private fun isYesturday(date: Date?): Boolean {
        val yesturday = Calendar.getInstance()
        yesturday.timeInMillis = yesturday.timeInMillis - (1000 * 60 * 60 * 24)
        return DateTimeHelper.isSameDay(date, yesturday.time)
    }
}
