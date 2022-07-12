package com.rsmnm.ViewHolders

import android.content.Context
import android.view.View
import android.widget.TextView
import com.rsmnm.Models.LocationItem
import com.rsmnm.Models.TripItem
import com.rsmnm.R
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder

/**
 * Created by saqib on 9/11/2018.
 */
class SaveLocationHolder(itemView: View?) : EfficientViewHolder<LocationItem>(itemView) {
    override fun updateView(context: Context, `object`: LocationItem) {
        findViewByIdEfficient<TextView>(R.id.txt_name).setText(`object`.name)
        findViewByIdEfficient<TextView>(R.id.txt_address).setText(`object`.address)
    }
}