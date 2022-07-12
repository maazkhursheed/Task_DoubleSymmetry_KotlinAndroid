package com.rsmnm.ViewHolders

import android.content.Context
import android.view.View
import android.widget.TextView
import com.rsmnm.Models.CarTypeItem
import com.rsmnm.Models.VehicleTypeItem
import com.rsmnm.R
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder

/**
 * Created by saqib on 9/11/2018.
 */
class CarTypeHolder(itemView: View?) : EfficientViewHolder<VehicleTypeItem>(itemView) {
    override fun updateView(context: Context, `object`: VehicleTypeItem) {
        findViewByIdEfficient<TextView>(R.id.txt_car_type).setText(`object`.title)
        findViewByIdEfficient<TextView>(R.id.txt_seats).setText("(Seats " + `object`.seats + ")")
        findViewByIdEfficient<TextView>(R.id.txt_base_fare).setText("$ " + `object`.base_fare)
        findViewByIdEfficient<TextView>(R.id.txt_per_minute).setText("$ " + `object`.per_minute)
        findViewByIdEfficient<TextView>(R.id.txt_per_miles).setText("$ " + `object`.per_mile)
        findViewByIdEfficient<TextView>(R.id.txt_cancellation_fee).setText("$ " + `object`.cancellation_fee)
        findViewByIdEfficient<TextView>(R.id.txt_minimum_fare).setText("$ " + `object`.minimum_fare)
    }
}