package com.rsmnm.Fragments.driver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.BaseClasses.BaseStatesFragment
import com.rsmnm.Models.EarningSummaryItem
import com.rsmnm.Models.Resource
import com.rsmnm.Models.TripItem
import com.rsmnm.Models.WeeklyReportItem
import com.rsmnm.R
import com.rsmnm.Utils.initVerticalRecycler
import com.rsmnm.ViewHolders.DailyEarningsHolder
import com.rsmnm.ViewHolders.SummaryEarningsHolder
import com.rsmnm.ViewModels.DriverViewModel
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_summary_earnings.*
import java.util.ArrayList

/**
 * Created by saqib on 9/11/2018.
 */
class SummaryEarningsFragment : BaseStatesFragment() {


    var mAdp: EfficientRecyclerAdapter<WeeklyReportItem>? = null
    var mList = ArrayList<WeeklyReportItem>()
    var summaryData: EarningSummaryItem? = null
    lateinit var driverViewModel: DriverViewModel


    override fun getStubLayout(): Int = R.layout.fragment_summary_earnings


    override fun getTitleBar(titleBar: TitleBar?) {
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        getData()
    }

    override fun inits() {
        driverViewModel = ViewModelProviders.of(fragmentActivity).get(DriverViewModel::class.java)
        initVerticalRecycler(recyclerview, false)
        mAdp = EfficientRecyclerAdapter(R.layout.item_summary_earnings, SummaryEarningsHolder::class.java, mList)
        recyclerview.setAdapter(mAdp)
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            mList.clear()
            getData()
        }
    }

    private fun getData() {
        driverViewModel.earningSummary().observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> setContentType(ContentType.loading)
                Resource.Status.success -> {
                    setContentType(ContentType.content)
                    hideLoader()
                    summaryData = response.data?.body
                    txt_info.text = summaryData?.required_hours
                    txt_recruits.text = summaryData?.total_recruits
                    txt_recruits_direct.text = summaryData?.direct_recruits
                    mList.addAll(summaryData?.weeks_data!!)
                    mAdp?.notifyDataSetChanged()
                }
                else -> setContentType(ContentType.error)
            }
        })
    }

    override fun setEvents() {

    }

    override fun onRetryClicked() {
        mList.clear()
        getData()
    }

}