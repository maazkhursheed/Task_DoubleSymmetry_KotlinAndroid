package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Interfaces.LogoutInterface
import com.rsmnm.Models.Resource
import com.rsmnm.R
import com.rsmnm.ViewModels.PassengerViewModel
import com.rsmnm.Views.TitleBar
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.fragment_logout.*

/**
 * Created by saqib on 9/11/2018.
 */
class DeleteFragment : BaseFragment() {

    override fun getLayout(): Int = R.layout.fragment_delete

    lateinit var viewMode: PassengerViewModel

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.resetTitleBar().enableBack()
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {

        viewMode = ViewModelProviders.of(fragmentActivity).get(PassengerViewModel::class.java)

        BounceView.addAnimTo(btn_submit)
        BounceView.addAnimTo(btn_cancel)
    }

    override fun setEvents() {
        btn_cancel.setOnClickListener { fragmentActivity.actionBack() }
        btn_submit.setOnClickListener {

            viewMode.deleteAccount().observe(this, Observer {

                when (it?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        (context as LogoutInterface).actionLogout()
                    }
                    else -> {
                        makeSnackbar(it?.data)
                    }
                }
            })
        }
    }
}