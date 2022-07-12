package com.rsmnm.Fragments.passenger

import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.rsmnm.BaseClasses.PlacePickerFragment
import com.rsmnm.Interfaces.LocationPickedInterface
import com.rsmnm.Models.LocationItem
import com.rsmnm.R
import com.rsmnm.Utils.initVerticalRecycler
import com.rsmnm.ViewHolders.SaveLocationHolder
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_save_location.*
import java.util.*

/**
 * Created by saqib on 9/11/2018.
 */
class SaveLocationFragment : PlacePickerFragment(), LocationPickedInterface {

    lateinit var mAdp: EfficientRecyclerAdapter<LocationItem>
    private var mList = ArrayList<LocationItem>()

    override fun getLayout(): Int = R.layout.fragment_save_location

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.setTitle("Save Location").enableBack()
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
    }

    override fun inits() {
        initVerticalRecycler(divider = false)

        mList.addAll(roomDb.locationDoa().all)
        mAdp = EfficientRecyclerAdapter(R.layout.item_save_location, SaveLocationHolder::class.java, mList)
        recyclerview.setAdapter(mAdp)

        refreshLayout.setOnRefreshListener({ refreshLayout.isRefreshing = false })
        btn_add.setOnClickListener({ pickPlace(this) })

        mAdp.setOnItemLongClickListener { adapter, view, `object`, position ->
            AlertDialog.Builder(context!!).setMessage("Do you want to remove this saved location?").setPositiveButton("Yes") { dialogInterface, i ->
                mList.remove(`object`)
                roomDb.locationDoa().delete(`object`)
                mAdp.notifyDataSetChanged()
            }.setNegativeButton("Cancel", null).show()

        }
    }

    override fun setEvents() {
    }

    override fun onLocationSelected(location: LocationItem) {
        mList.add(location)
        mAdp.notifyDataSetChanged()
        roomDb.locationDoa().insert(location)
    }

}