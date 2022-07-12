package com.rsmnm.Activities

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging
import com.rsmnm.Adapters.ViewPagerAdapter
import com.rsmnm.BaseClasses.LocationSettingsActivity
import com.rsmnm.Fragments.driver.DocumentFragment
import com.rsmnm.Fragments.driver.EarningsFragment
import com.rsmnm.Fragments.driver.HomeFragment
import com.rsmnm.Fragments.driver.RecruitFragment
import com.rsmnm.Fragments.passenger.SettingsFragment
import com.rsmnm.Interfaces.LogoutInterface
import com.rsmnm.Interfaces.WorkCheckInterface
import com.rsmnm.Models.NotifObject
import com.rsmnm.Models.Resource
import com.rsmnm.Networking.WebServiceFactory
import com.rsmnm.R
import com.rsmnm.Services.ForegroundLocationService
import com.rsmnm.Utils.*
import com.rsmnm.ViewModels.DriverViewModel
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.activity_driver.*
import kotlinx.android.synthetic.main.view_titlebar.*


/**
 * Created by saqib on 10/1/2018.
 */
class DriverActivity : LocationSettingsActivity(), CompoundButton.OnCheckedChangeListener, WorkCheckInterface, LogoutInterface {

    lateinit var adapter: ViewPagerAdapter
    lateinit var driverViewModel: DriverViewModel;
    lateinit var viewTitlebar: TitleBar

    override fun showLoader() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        progressBar.visibility = View.GONE
    }

    override fun getFrameLayoutId(): Int = R.id.frame_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        driverViewModel = ViewModelProviders.of(this).get(DriverViewModel::class.java)

        Log(getUserItem()?.user_id, getUserItem()?.token)
        viewTitlebar = titlebar

        inits()
        initTabs()
        setEvents()

        AppStore.getInstance().notificationLiveData.observe(this, Observer { notifObject ->
            notificationReceived(notifObject, true)
        })
        AppStore.getInstance().sessionExpireObservable.observe(this, Observer { expired ->
            if (expired != null && expired)
                actionLogout()
        })
        AppStore.getInstance().validDocumentsObservable.observe(this, Observer { expired ->
            //            if (expired != null && expired)
            showDialog("Please upload Vehicle Inspection form from Edit Profile")
//            actionMoveToDocument()
        })
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(mContext, R.style.ListDialog).setMessage(message).setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            actionMoveToDocument()
        }).show()
    }


    private fun notificationReceived(notifObject: NotifObject?, fromPushNotif: Boolean) {

        if (fromPushNotif && notifObject != null)
            AppStore.getInstance().notificationLiveData.postValue(null)

        when (notifObject?.data_click_action) {

            NotifObject.NotificationAction.new_ride -> startHomeFragment()
            NotifObject.NotificationAction.ride_canceled -> {
                DialogHelper.showAlertDialog(mContext, "Ride canceled by the passenger")
                if (currentFragment is HomeFragment)
                    replaceFragment(HomeFragment())
            }
            else -> {

            }
        }
    }

    private fun startHomeFragment() {
        if (tabLayout.selectedTabPosition != 0) tabLayout.getTabAt(0)?.select() else replaceFragment(HomeFragment())
    }

    override fun onResume() {
        super.onResume()
        checkLocationEnabled(this, false)
    }

    //    Location Allowed
    override fun onCompleted() {
        layout_location_disabled.visibility = View.GONE
        if (currentFragment == null)
            replaceFragment(HomeFragment())
    }

    // Location Disabled
    override fun onFailure() {
        layout_location_disabled.visibility = View.VISIBLE
    }

    private fun setEvents() {
        layout_location_disabled.setOnClickListener { Log("LocationDisabledView", "Clicked") }
        btn_retry.setOnClickListener { view -> checkLocationEnabled(this, true) }
    }


    // Driver Online Status Listener
    override fun onCheckedChanged(button: CompoundButton?, status: Boolean) {

        Log("CheckChanged", "Activity")
        driverViewModel.updateStatus(status).observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {

                    titlebar.updateStatusTitle(status)
                    startService<ForegroundLocationService>(if (status) ForegroundLocationService.START else ForegroundLocationService.STOP)

                    if (status)
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    else
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                    setUserItem(getUserItem()?.apply { is_online = if (status) 1 else 0 })
                    hideLoader()
                }
                else -> {
                    hideLoader()

                    if (switch_status.isChecked) {
                        switch_status.setOnCheckedChangeListener(null)
                        switch_status.isChecked = false
                        switch_status.setOnCheckedChangeListener(this)
                        setUserItem(getUserItem()?.apply { is_online = 0 })

                        driverViewModel.updateStatus(false).observe(this, Observer {  })
                        if (response != null) {
                            if (!response?.data!!.areValidDocuments()) {
                                AppStore.getInstance().validDocumentsObservable.postValue(true)
                            }
                        }
                    }

                    var msg: String = response?.data.toString()
                    if (!response?.data!!.areValidDocuments()) {
                        msg = "Please validate your documents!"
                    }
                    makeSnackbar(msg)
                }
            }
        })
    }

    private fun inits() {

        titlebar.resetTitleBar().enableStatusSwitch(this)
        titlebar.setBackListener(View.OnClickListener { actionBack() })
        getUserLiveData().observe(this, Observer { userItem ->
            switch_status.isChecked = if (userItem?.is_online == 1) true else false
        })
    }

    fun initTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.home_tabs_icon).setText("Home"))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.earnings_tabs_icon).setText("Earnings"))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.recruit_tabs_icon).setText("Recruit"))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.account_tabs_icon).setText("Account"))
        tabLayout.getTabAt(0)?.icon?.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
                when (tab?.position) {
                    0 -> replaceFragment(HomeFragment())
                    1 -> replaceFragment(EarningsFragment())
                    2 -> replaceFragment(RecruitFragment())
                    3 -> replaceFragment(SettingsFragment())
                }
            }
        })
    }


    private fun actionMoveToDocument() {
        replaceFragmentWithBackstack(DocumentFragment())
    }

    override fun actionLogout() {
        try {
            startService<ForegroundLocationService>(ForegroundLocationService.STOP)
            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/android1")
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.format("/topics/user_%s", getUserItem()?.user_id))
            AppStore.getInstance().sessionExpireObservable.postValue(null)
            driverViewModel.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setUserItem(null)
        startActivity<AccountActivity>()
        finish()
    }
}