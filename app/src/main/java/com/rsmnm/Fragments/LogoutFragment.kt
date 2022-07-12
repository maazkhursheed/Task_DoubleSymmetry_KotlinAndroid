package com.rsmnm.Fragments

import android.os.Bundle
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Interfaces.LogoutInterface
import com.rsmnm.R
import com.rsmnm.Views.TitleBar
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.fragment_logout.*

/**
 * Created by saqib on 9/11/2018.
 */
class LogoutFragment : BaseFragment() {

    override fun getLayout(): Int = R.layout.fragment_logout

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.resetTitleBar().enableBack()
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        BounceView.addAnimTo(btn_submit)
        BounceView.addAnimTo(btn_cancel)
    }

    override fun setEvents() {
        btn_cancel.setOnClickListener { fragmentActivity.actionBack() }
        btn_submit.setOnClickListener { (context as LogoutInterface).actionLogout() }
    }
}