package com.rsmnm.Fragments.passenger

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearSnapHelper
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Models.CarTypeItem
import com.rsmnm.Models.Resource
import com.rsmnm.Models.VehicleTypeItem
import com.rsmnm.R
import com.rsmnm.Utils.AppStore
import com.rsmnm.Utils.PreferencesManager
import com.rsmnm.Utils.initVerticalRecycler
import com.rsmnm.ViewHolders.CarTypeHolder
import com.rsmnm.ViewModels.DriverViewModel
import com.rsmnm.ViewModels.PassengerViewModel
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_car_type.*
import java.util.ArrayList

/**
 * Created by saqib on 9/11/2018.
 */
class CarTypeFragment : BaseFragment() {

    lateinit var driverViewModel: DriverViewModel
    private var mAdp: EfficientRecyclerAdapter<VehicleTypeItem>? = null
    private var mList = ArrayList<VehicleTypeItem>()

    override fun getLayout(): Int = R.layout.fragment_car_type

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.enableBack().setTitle("")
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        driverViewModel = ViewModelProviders.of(fragmentActivity).get(DriverViewModel::class.java)
        initVerticalRecycler(divider = false)

        mAdp = EfficientRecyclerAdapter(R.layout.item_car_type, CarTypeHolder::class.java, mList)
        recyclerview.setAdapter(mAdp)
        LinearSnapHelper().attachToRecyclerView(recyclerview)
        refreshLayout.setOnRefreshListener({ getCarTypes() })

        getCarTypes()
    }

    override fun setEvents() {
    }


    private fun getCarTypes() {
        val state = PreferencesManager.getString("state", "Illinois")
        val city = PreferencesManager.getString("city", "Chicago")
        driverViewModel.getCarType(state, city).observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    refreshLayout.isRefreshing = false
                    hideLoader()
                    mList.clear()
                    mList.addAll(response.data?.body!!)
                    mAdp?.notifyDataSetChanged()
                }
                else -> {
                    refreshLayout.isRefreshing = false
                    hideLoader()
                    makeSnackbar(response?.data)
                }
            }
        })
    }

}