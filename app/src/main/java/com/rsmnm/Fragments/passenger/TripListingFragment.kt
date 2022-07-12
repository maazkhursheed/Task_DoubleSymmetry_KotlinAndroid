package com.rsmnm.Fragments.passenger

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.rsmnm.R
import com.rsmnm.Utils.initVerticalRecycler
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_past_trip.*
import java.util.ArrayList
import android.support.v7.widget.RecyclerView
import com.rsmnm.BaseClasses.BaseStatesFragment
import com.rsmnm.Models.TripListingItem
import com.rsmnm.Models.Resource
import com.rsmnm.Networking.WebResponse
import com.rsmnm.Utils.AppStore
import com.rsmnm.Utils.EndlessRecyclerViewScrollListener
import com.rsmnm.ViewHolders.PastTripsHolder
import com.rsmnm.ViewHolders.UpcommingTripsHolder
import com.rsmnm.ViewModels.PassengerViewModel


/**
 * Created by saqib on 9/11/2018.
 */
class TripListingFragment : BaseStatesFragment(), Observer<Resource<WebResponse<ArrayList<TripListingItem>>>> {

    override fun getStubLayout(): Int = R.layout.fragment_daily_earnings

    private var mAdp: EfficientRecyclerAdapter<TripListingItem>? = null
    private var mList = ArrayList<TripListingItem>()
    private lateinit var passengerViewModel: PassengerViewModel

    lateinit var path: String


    companion object {
        fun newInstance(str: String) = TripListingFragment().apply {
            arguments = Bundle(2).apply {
                path = str
            }
        }
    }


    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        getData(1)

        AppStore.getInstance().myTripsLiveData.observe(this, Observer { pair ->
            if (pair == null)
                return@Observer

            if (pair.first.equals("pay"))
                completePayment(pair.second)
            else
                cancelTrip(pair.second)

            AppStore.getInstance().myTripsLiveData.postValue(null)

        })
    }

    private fun getData(page: Int) {
        passengerViewModel.passengerTripListing(page, path).observe(this, this)
    }

    override fun inits() {

        passengerViewModel = ViewModelProviders.of(fragmentActivity).get(PassengerViewModel::class.java)
        val layoutmanager = initVerticalRecycler(recyclerview, false)

        mAdp = EfficientRecyclerAdapter(R.layout.item_trip_listing, if (path.equals("past")) PastTripsHolder::class.java else UpcommingTripsHolder::class.java, mList)
        recyclerview.setAdapter(mAdp)
        refreshLayout.setOnRefreshListener({
            mList.clear()
            getData(1)
            refreshLayout.isRefreshing = false
        })

        recyclerview.addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutmanager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (totalItemsCount > 10)
                    getData(page)
            }
        })
    }

    override fun setEvents() {

    }

    override fun onRetryClicked() {
        mList.clear()
        getData(1)
    }

    override fun onChanged(response: Resource<WebResponse<ArrayList<TripListingItem>>>?) {
        when (response?.status) {
            Resource.Status.loading -> if (mList.isEmpty()) setContentType(BaseStatesFragment.ContentType.loading)
            Resource.Status.success -> {
                setContentType(BaseStatesFragment.ContentType.content)
                hideLoader()
                mList.addAll(response.data?.body!!)
                mAdp?.notifyDataSetChanged()
            }
            else -> {
                if (mList.isEmpty()) setContentType(BaseStatesFragment.ContentType.error)
                hideLoader()
                makeSnackbar(response?.data)
            }
        }
    }

    fun completePayment(tripListingItem: TripListingItem?) {

        passengerViewModel.completePayment(tripListingItem?.ride_id!!).observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    makeSnackbar(response?.data)
//                    mList.set(mList.indexOf(tripListingItem), tripListingItem)
                    tripListingItem.is_paid = 1
                    mAdp?.notifyDataSetChanged()
                }
                else -> {
                    hideLoader()
                    makeSnackbar(response?.data)
                }
            }
        })

    }

    fun cancelTrip(tripListingItem: TripListingItem?) {

        passengerViewModel.cancelTrip(tripListingItem?.ride_id!!).observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    mList.remove(tripListingItem)
                    mAdp?.notifyDataSetChanged()
                }
                else -> {
                    hideLoader()
                    makeSnackbar(response?.data)
                }
            }
        })

    }

}