package com.rsmnm.ViewHolders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rsmnm.R

/**
 * Created by saqib on 9/11/2018.
 */
class HomeVehicleTypeHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    val parent = itemView?.findViewById<LinearLayout>(R.id.parent)
    val text = itemView?.findViewById<TextView>(R.id.txt_car_type)
    val img = itemView?.findViewById<ImageView>(R.id.img_car)
    val seperator = itemView?.findViewById<View>(R.id.seperator)
}