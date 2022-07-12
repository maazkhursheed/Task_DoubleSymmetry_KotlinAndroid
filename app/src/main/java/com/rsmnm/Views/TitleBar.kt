package com.rsmnm.Views

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.RelativeLayout
import com.rsmnm.R
import kotlinx.android.synthetic.main.activity_driver.view.*
import kotlinx.android.synthetic.main.view_titlebar.view.*


/**
 * Created by rohail on 20-Oct-17.
 */

class TitleBar : RelativeLayout {

    lateinit var mContext: Context
    private var iface: CompoundButton.OnCheckedChangeListener? = null


    private var drawerLayout: DrawerLayout? = null

    constructor(context: Context) : super(context) {
        this.mContext = context
        initUi()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initUi()
    }

    private fun initUi() {
        View.inflate(getContext(), R.layout.view_titlebar, this)
    }

    fun setTitle(title: String): TitleBar {
        txt_title.text = title
        return this
    }

    fun enableMenu(): TitleBar {
        btn_back.visibility = View.GONE
        btn_menu.visibility = View.VISIBLE
        if (drawerLayout != null)
            drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        return this
    }

    fun enableBack(): TitleBar {
        btn_menu.visibility = View.GONE
        btn_back.visibility = View.VISIBLE
        if (drawerLayout != null)
            drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        return this
    }


    fun enableRightButton(@DrawableRes drawableRes: Int, listener: View.OnClickListener): TitleBar {
        btn_right.visibility = View.VISIBLE
        btn_right.setImageResource(drawableRes)
        btn_right.setOnClickListener(listener)
        return this
    }

    fun enableRightButton(str: String, listener: View.OnClickListener): TitleBar {
        btn_right_text.visibility = View.VISIBLE
        btn_right_text.text = str
        btn_right_text.setOnClickListener(listener)
        return this
    }


    fun disableRightButton(): TitleBar {
        btn_right_text.visibility = GONE
        btn_right.visibility = GONE
        return this
    }

    fun resetTitleBar(): TitleBar {
        switch_status.visibility = View.GONE
        btn_right.visibility = View.GONE
        btn_right_text.visibility = View.GONE
        txt_title.text = ""
        btn_right.setOnClickListener(null)
        return this
    }

    fun setMenuListener(iface: View.OnClickListener) {
        btn_menu.setOnClickListener(iface)
    }

    fun setBackListener(iface: View.OnClickListener) {
        btn_back.setOnClickListener(iface)
    }

    fun setDrawer(drawerLayout: DrawerLayout) {
        this.drawerLayout = drawerLayout
    }

    fun enableStatusSwitch(paramIface: CompoundButton.OnCheckedChangeListener? = null) {
        switch_status.visibility = View.VISIBLE
        txt_title.setText(if (switch_status.isChecked) "Online" else "Offline")
        if (paramIface != null)
            iface = paramIface
        switch_status.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.e("Status switch", isChecked.toString())
            iface?.onCheckedChanged(buttonView, isChecked)
        }
    }

    fun hideStatusSwitch() {
        switch_status.visibility = View.GONE
    }

    fun updateStatusTitle(checked: Boolean) {
        setTitle(if (checked) "Online" else "Offline")
    }

    fun disableBack(): TitleBar {
        btn_back.visibility = View.GONE
        return this
    }

}
