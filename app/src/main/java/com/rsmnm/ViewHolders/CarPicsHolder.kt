package com.rsmnm.ViewHolders

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.rsmnm.BaseClasses.FragmentHandlingActivity
import com.rsmnm.Fragments.driver.RegisterDriverFragment2
import com.rsmnm.Models.CarTypeItem
import com.rsmnm.R
import com.skocken.efficientadapter.lib.viewholder.EfficientViewHolder
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import android.view.View.OnLongClickListener
import android.widget.Toast


/**
 * Created by saqib on 9/11/2018.
 */
class CarPicsHolder(itemView: View?) : EfficientViewHolder<File>(itemView) {
    override fun updateView(context: Context, `object`: File) {
        val img = findViewByIdEfficient<CircleImageView>(R.id.img_car)
        if (`object` != null)
            Picasso.get().load(`object`).fit().centerCrop().into(img)
        else {

        }
    }
}