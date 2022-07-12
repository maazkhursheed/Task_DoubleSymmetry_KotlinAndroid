package com.rsmnm.Activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.google.firebase.messaging.FirebaseMessaging
import com.rsmnm.BaseClasses.LocationSettingsActivity
import com.rsmnm.Enums.HomeCurrentStatus
import com.rsmnm.Fragments.PaymentFragment
import com.rsmnm.Fragments.driver.ReceiptFragment
import com.rsmnm.Fragments.passenger.HomeFragment
import com.rsmnm.Fragments.passenger.SettingsFragment
import com.rsmnm.Fragments.passenger.MyTripsFragment
import com.rsmnm.Interfaces.LogoutInterface
import com.rsmnm.Models.NotifObject
import com.rsmnm.Models.Resource
import com.rsmnm.R
import com.rsmnm.Utils.*
import com.rsmnm.ViewModels.PassengerViewModel
import com.rsmnm.Views.TitleBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_drawer.*
import kotlinx.android.synthetic.main.view_titlebar.*

class PassengerActivity : LocationSettingsActivity(), View.OnClickListener, LogoutInterface {

    override fun getFrameLayoutId(): Int = R.id.frame_main
    lateinit var viewTitlebar: TitleBar
    lateinit var passengerViewModel: PassengerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        passengerViewModel = ViewModelProviders.of(this).get(PassengerViewModel::class.java)
        Log(getUserItem()?.user_id, getUserItem()?.token)

        passengerViewModel.getProfile().observe(this, Observer { if (it?.status == Resource.Status.success && it.data?.body != null) setUserItem(it.data?.body!!) })

        viewTitlebar = titlebar
        titlebar.setDrawer(layout_drawer)

        setEvents()
        setDrawerInfo()

        replaceFragment(HomeFragment())

        if (intent.hasExtra(AppConstants.KEY_NOTIF_DATA)) {
            val notif = intent.extras?.getString(AppConstants.KEY_NOTIF_DATA)?.fromJson(NotifObject::class.java)
            notificationReceived(notif, false)
        }

        AppStore.getInstance().sessionExpireObservable.observe(this, Observer { expired ->
            if (expired != null && expired)
                actionLogout()
        })

        AppStore.getInstance().notificationLiveData.observe(this, Observer { notifObject ->
            notificationReceived(notifObject, true)
        })
    }

    private fun setDrawerInfo() {

        getUserLiveData().observe(this, Observer { user ->
            drawer_user_name.text = user?.fullName
            drawer_user_email.text = user?.email
            drawer_user_rating.text = user?.rating
            if (!user?.profile_picture.isNullOrEmpty())
                Picasso.get().load(user?.profile_picture).fit().centerCrop().into(drawer_user_img)
        })
    }


    override fun actionLogout() {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/android1")
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.format("/topics/user_%s", getUserItem()?.user_id))
            AppStore.getInstance().sessionExpireObservable.postValue(null)
            passengerViewModel.logout()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setUserItem(null)
        startActivity<AccountActivity>()
        finish()
    }

    private fun setEvents() {
        btn_menu.setOnClickListener { layout_drawer.openDrawer(Gravity.LEFT) }
        titlebar.setBackListener(View.OnClickListener {
            actionBack()
        })
        menu_trips.setOnClickListener(this)
        menu_payments.setOnClickListener(this)
        menu_settings.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        layout_drawer.closeDrawer(Gravity.LEFT)
        handler.postDelayed({
            when (view?.id) {
                R.id.menu_trips -> replaceFragmentWithBackstack(MyTripsFragment(), false, false)
                R.id.menu_payments -> replaceFragmentWithBackstack(PaymentFragment(), false, false)
                R.id.menu_settings -> replaceFragmentWithBackstack(SettingsFragment(), false, false)
            }
        }, 300)
    }

    override fun actionBack(): Boolean {
        if (currentFragment is HomeFragment) {
            if ((currentFragment as HomeFragment).status == HomeCurrentStatus.searching_ride) {
                (currentFragment as HomeFragment).clearSearch()
                return true
            }
        }
        return super.actionBack()
    }

    override fun showLoader() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        progressBar.visibility = View.GONE
    }

    private fun notificationReceived(notifObject: NotifObject?, fromPushNotif: Boolean) {

        if (fromPushNotif && notifObject != null)
            AppStore.getInstance().notificationLiveData.postValue(null)

        when (notifObject?.data_click_action) {

            NotifObject.NotificationAction.new_ride -> if (fromPushNotif) updateHome()
            NotifObject.NotificationAction.ride_canceled -> {
                DialogHelper.showAlertDialog(mContext, "Ride canceled by the driver")
                if (currentFragment is HomeFragment)
                    replaceFragment(HomeFragment())
            }
            NotifObject.NotificationAction.ride_ended -> {
                replaceFragmentWithBackstack(ReceiptFragment.newInstance(notifObject.ride_id))
            }
            NotifObject.NotificationAction.future_ride_assigned -> {
                if (fromPushNotif)
                    replaceFragment(HomeFragment())
            }
            else -> {
                if (fromPushNotif)
                    updateHome()
            }
        }
    }

    private fun updateHome() {
        if (currentFragment is HomeFragment)
            (currentFragment as HomeFragment).checkCurrentRide()
    }

}