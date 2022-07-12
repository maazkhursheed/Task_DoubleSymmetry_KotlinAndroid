package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView

import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Models.NotifObject
import com.rsmnm.Models.Resource
import com.rsmnm.Networking.WebResponse
import com.rsmnm.R
import com.rsmnm.Utils.EndlessRecyclerViewScrollListener
import com.rsmnm.Utils.initVertical
import com.rsmnm.ViewHolders.NotificationHolder
import com.rsmnm.ViewModels.DriverViewModel
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationsFragment : BaseFragment(), Observer<Resource<WebResponse<ArrayList<NotifObject>>>> {


    lateinit var mAdp: EfficientRecyclerAdapter<NotifObject>
    var mList = ArrayList<NotifObject>()
    private lateinit var driverViewModel: DriverViewModel

    override fun getLayout(): Int = R.layout.fragment_notification

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.resetTitleBar().enableBack().setTitle("Notifications")
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        driverViewModel.getNotifications().observe(this, this)
    }

    override fun inits() {

        driverViewModel = ViewModelProviders.of(fragmentActivity).get(DriverViewModel::class.java)
        val layoutmanager = recyclerview.initVertical(context!!, true)
        mAdp = EfficientRecyclerAdapter(R.layout.item_notification, NotificationHolder::class.java, mList)
        recyclerview.adapter = mAdp

        recyclerview.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutmanager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                driverViewModel.getNotifications(page).observe(this@NotificationsFragment, this@NotificationsFragment)
            }
        })
    }

    override fun setEvents() {

    }

    override fun onChanged(response: Resource<WebResponse<ArrayList<NotifObject>>>?) {

        when (response?.status) {
            Resource.Status.loading -> showLoader()
            Resource.Status.success -> {
                hideLoader()
                mList.addAll(response?.data?.body!!)
                mAdp.notifyDataSetChanged()
            }
            else -> {
                hideLoader()
            }
        }
    }
}
