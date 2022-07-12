package com.rsmnm.Fragments.passenger

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.BuildConfig
import com.rsmnm.Fragments.DeleteFragment
import com.rsmnm.Fragments.LogoutFragment
import com.rsmnm.Fragments.NotificationsFragment
import com.rsmnm.Fragments.WebviewContentFragment
import com.rsmnm.Fragments.driver.DocumentFragment
import com.rsmnm.R
import com.rsmnm.Utils.*
import com.rsmnm.Views.TitleBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 */
class SettingsFragment : BaseFragment() {

    var isSubMenuVisibile = false

    override fun getLayout(): Int = R.layout.fragment_settings

    override fun getTitleBar(titleBar: TitleBar) {

        titleBar.resetTitleBar().setTitle(BuildConfig.FLAVOR.run { if (equals(AppConstants.FLAVOUR_PASSENGER)) "Settings" else "Account" }).disableBack()
        if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER))
            titleBar.enableBack()
    }

    override fun activityCreated(savedInstanceState: Bundle?) {

        if (isSubMenuVisibile) {
            layout_service_menu.visibility = View.VISIBLE
            icon_service_menu.setImageResource(R.drawable.arrow_down)
        } else {
            layout_service_menu.visibility = View.GONE
            icon_service_menu.setImageResource(R.drawable.arrow_right)
        }
    }

    override fun inits() {
        val notifCount = PreferencesManager.getInt("notif")
        if (notifCount == 0)
            txt_notif_count.visibility = View.GONE
        else {
            txt_notif_count.visibility = View.VISIBLE
            txt_notif_count.setText(notifCount.toString())
        }

        if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_DRIVER)) {
            seperator_saveloc.visibility = View.GONE
            btn_save_location.visibility = View.GONE
        }

        getUserLiveData().observe(this, Observer { userItem ->
            userItem?.apply {
                txt_name.setText(fullName)
                txt_email.setText(email)
                txt_phone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
                txt_phone.setText(phone)
                if (!profile_picture.isNullOrEmpty())
                    Picasso.get().load(profile_picture).fit().centerCrop().into(img_user)
            }
        })
    }

    override fun setEvents() {
        btn_profile.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(ProfileFragment()) }
        btn_notifications.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(NotificationsFragment()) }
        btn_save_location.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(SaveLocationFragment()) }
        btn_support.setOnRippleCompleteListener { fragmentActivity.createCallIntent("8326541897") }
        btn_car_type.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(CarTypeFragment()) }
        btn_logout.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(LogoutFragment()) }
        btn_delete.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(DeleteFragment()) }
        btn_documents.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(DocumentFragment()) }

        btn_service_menu.setOnClickListener {
            if (isSubMenuVisibile) {
                isSubMenuVisibile = false
                layout_service_menu.visibility = View.GONE
                icon_service_menu.setImageResource(R.drawable.arrow_right)
            } else {
                isSubMenuVisibile = true
                layout_service_menu.visibility = View.VISIBLE
                icon_service_menu.setImageResource(R.drawable.arrow_down)
            }
        }

        btn_terms.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(WebviewContentFragment.newInstance("Terms and Conditions", "file:///android_asset/terms.html")) }
        btn_privacypolicy.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(WebviewContentFragment.newInstance("Privacy Policy", "file:///android_asset/privacy_policy.html")) }
        btn_service_agreement.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(WebviewContentFragment.newInstance("Service Agreement", "file:///android_asset/service_agreement.html")) }
        btn_insurance.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(WebviewContentFragment.newInstance("Insurance", "file:///android_asset/terms.html")) }
        btn_software.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(WebviewContentFragment.newInstance("Software", "file:///android_asset/software.html")) }
        btn_payout_process.setOnRippleCompleteListener { fragmentActivity.replaceFragmentWithBackstack(WebviewContentFragment.newInstance("Payout Process", "file:///android_asset/payout_process.html")) }
    }
}