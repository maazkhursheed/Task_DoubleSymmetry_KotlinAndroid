package com.rsmnm.Fragments.driver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.rsmnm.BaseClasses.BaseStatesFragment
import com.rsmnm.Models.TripListingItem
import com.rsmnm.Models.Resource
import com.rsmnm.Networking.WebResponse
import com.rsmnm.R
import com.rsmnm.Utils.EndlessRecyclerViewScrollListener
import com.rsmnm.Utils.initVerticalRecycler
import com.rsmnm.ViewHolders.DailyEarningsHolder
import com.rsmnm.ViewModels.DriverViewModel
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_daily_earnings.*

/**
 * Created by saqib on 9/11/2018.
 */
class DailyEarningsFragment : BaseStatesFragment(), Observer<Resource<WebResponse<ArrayList<TripListingItem>>>> {

    override fun getStubLayout(): Int = R.layout.fragment_daily_earnings

    private var mAdp: EfficientRecyclerAdapter<TripListingItem>? = null
    private var mList = ArrayList<TripListingItem>()
    private lateinit var driverViewModel: DriverViewModel


    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        getData(1)
    }

    private fun getData(page: Int) {
        driverViewModel.earningsDaily(page).observe(this, this)
    }

    override fun inits() {

        driverViewModel = ViewModelProviders.of(fragmentActivity).get(DriverViewModel::class.java)
        val layoutmanager = initVerticalRecycler(recyclerview, false)

        mAdp = EfficientRecyclerAdapter(R.layout.item_trip_listing_driver, DailyEarningsHolder::class.java, mList)
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
            Resource.Status.loading -> if (mList.isEmpty()) setContentType(ContentType.loading)
            Resource.Status.success -> {
                setContentType(ContentType.content)
                hideLoader()
                mList.addAll(response.data?.body!!)
                mAdp?.notifyDataSetChanged()
            }
            else -> {
                if (mList.isEmpty()) setContentType(ContentType.error)
                hideLoader()
                makeSnackbar(response?.data)
            }
        }
    }
}