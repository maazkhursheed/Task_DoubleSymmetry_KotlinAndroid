package com.rsmnm.Fragments.driver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.rsmnm.BaseClasses.BaseStatesFragment
import com.rsmnm.BuildConfig
import com.rsmnm.Models.Resource
import com.rsmnm.Models.TripItem
import com.rsmnm.Networking.WebResponse
import com.rsmnm.R
import com.rsmnm.Utils.AppConstants
import com.rsmnm.Utils.DateTimeHelper
import com.rsmnm.ViewModels.DriverViewModel
import com.rsmnm.Views.TitleBar
import com.squareup.picasso.Picasso
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.fragment_receipt.*
import java.util.*

/**
 * Created by saqib on 10/1/2018.
 */
class ReceiptFragment : BaseStatesFragment(), Observer<Resource<WebResponse<TripItem>>> {

    override fun getStubLayout(): Int = R.layout.fragment_receipt

    var tripItem: TripItem? = null
    var tripId: String? = null
    lateinit var viewModel: DriverViewModel

    companion object {
        fun newInstance(item: TripItem) = ReceiptFragment().apply {
            arguments = Bundle(2).apply {
                tripItem = item
            }
        }

        fun newInstance(id: String) = ReceiptFragment().apply {
            arguments = Bundle(2).apply {
                tripId = id
            }
        }
    }

    override fun getTitleBar(titleBar: TitleBar?) {

    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        if (tripItem != null)
            showReceiptData()
        else {
            viewModel.getRideDetails(tripId!!).observe(this, this)
        }
    }

    override fun onChanged(response: Resource<WebResponse<TripItem>>?) {
        when (response?.status) {
            Resource.Status.loading -> setContentType(ContentType.loading)
            Resource.Status.success -> {
                tripItem = response.data?.body
                showReceiptData()
            }
            else -> {
                setContentType(ContentType.error)
            }
        }
    }


    override fun inits() {
        viewModel = ViewModelProviders.of(fragmentActivity).get(DriverViewModel::class.java)
    }

    private fun showReceiptData() {
        setContentType(ContentType.content)
        tripItem?.apply {
            if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) {
                txt_name.text = driver.fullName
                if (!driver.profile_picture.isNullOrEmpty())
                    Picasso.get().load(driver.profile_picture).fit().centerCrop().into(img_user)
            } else {
                txt_name.text = passenger.fullName
                if (!passenger.profile_picture.isNullOrEmpty())
                    Picasso.get().load(passenger.profile_picture).fit().centerCrop().into(img_user)
            }

            txt_date.text = DateTimeHelper.getDateToShow(Date((invoice.updated_ts + "000").toLong()))
            txt_time.text = DateTimeHelper.getTimeToShow(Date((invoice.updated_ts + "000").toLong()))
//            txt_time.text = DateTimeHelper.getTimeToShow(nonUtc)

            txt_amount.text = invoice.getFare()
            txt_distance.text = String.format("%.2f Miles", invoice.travelled.toDouble())
            txt_duration.text = invoice.getDuration()
            txt_base_fare.text = invoice.getBase_fare()
            txt_wait_time.text = invoice.getWaitTime()
        }
    }

    override fun setEvents() {
        btn_toggle.setOnCheckedChangeListener(
                { buttonView, isChecked ->
                    if (isChecked)
                        ll.visibility = View.VISIBLE
                    else ll.visibility = View.GONE
                }
        )

        BounceView.addAnimTo(btn_complete)

        btn_complete.setOnClickListener {
            viewModel.ratePassenger(tripItem!!.ride_id, ratingbar.rating.toString(), field_review.text.toString()).observe(this, android.arch.lifecycle.Observer { response ->
                when (response?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        makeSnackbar(response.data)
                        fragmentActivity.actionBack()
                    }
                    else -> {
                        hideLoader()
                        makeSnackbar(response?.data)
                    }
                }
            })
        }
    }

    override fun onRetryClicked() {
        viewModel.getRideDetails(tripId!!).observe(this, this)
    }
}