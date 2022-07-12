package com.rsmnm.Fragments.passenger

import android.location.Location
import android.os.Bundle
import com.rsmnm.Adapters.DropoffItemsAdapter
import com.rsmnm.BaseClasses.PlacePickerFragment
import com.rsmnm.Interfaces.LocationClickedInterface
import com.rsmnm.Interfaces.LocationPickedInterface
import com.rsmnm.Models.LocationItem
import com.rsmnm.R
import com.rsmnm.Utils.AppStore
import com.rsmnm.Utils.initVertical
import com.rsmnm.ViewHolders.SaveLocationHolder
import com.rsmnm.Views.TitleBar
import com.skocken.efficientadapter.lib.adapter.EfficientRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_selectpickdrop.*
import android.location.Geocoder
import android.view.View
import com.rsmnm.Models.TripItem
import com.rsmnm.Utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*


class SelectPickupDropFragment : PlacePickerFragment() {

    lateinit var dropOffAdp: DropoffItemsAdapter
    lateinit var savedLocationAdp: EfficientRecyclerAdapter<LocationItem>

    var pickUp: Location? = null

    companion object {
        fun newInstance(loc: Location?, rideTime: Long? = null) = SelectPickupDropFragment().apply {
            arguments = Bundle(2).apply {
                AppStore.getInstance().tripRequestItem = TripItem()
                AppStore.getInstance().tripRequestItem.scheduled_at = rideTime
                pickUp = loc
            }
        }
    }

    override fun getLayout() = R.layout.fragment_selectpickdrop

    override fun getTitleBar(titleBar: TitleBar?) {
        titleBar!!.resetTitleBar().enableBack().enableRightButton("Done", View.OnClickListener {
            if (AppStore.getInstance().tripRequestItem.isPickDataValid()) {
                AppStore.getInstance().tripRequestItem.isPickupCompleted = true
                AppStore.getInstance().tripRequestItem.dropoffs = dropOffAdp.getDropOffs()
                fragmentActivity.actionBack()
            } else {
                makeSnackbar("Please select valid pickup location before proceeding")
            }
        })
    }

    override fun activityCreated(savedInstanceState: Bundle?) {

    }

    private fun geoCodePickup(latitude: Double?, longitude: Double?, address: String?) {
        if (latitude == null || longitude == null)
            return
        val geocoder = Geocoder(context, Locale.ENGLISH)

        GlobalScope.launch(Dispatchers.Main) {
            showLoader()
            GlobalScope.async(Dispatchers.IO) {
                try {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (addresses.size > 0) {
                        val fetchedAddress = addresses[0]

                        var addressline = StringBuilder()
                        for (n in 0..fetchedAddress.getMaxAddressLineIndex()) {
                            addressline.append(fetchedAddress.getAddressLine(n)).append(" ")
                        }
                        if (address.isNullOrEmpty())
                            AppStore.getInstance().tripRequestItem.pickup_title = addressline.toString()
                        else
                            AppStore.getInstance().tripRequestItem.pickup_title = address
                        AppStore.getInstance().tripRequestItem.pickup_city = fetchedAddress.locality
                        AppStore.getInstance().tripRequestItem.pickup_state = fetchedAddress.adminArea
                        Log(fetchedAddress.adminArea, fetchedAddress.locality)

                        PreferencesManager.putString("state", fetchedAddress.adminArea)
                        PreferencesManager.putString("city", fetchedAddress.locality)
                    } else {
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.await()

            hideLoader()
            if (isAdded)
                txt_pickup.text = AppStore.getInstance().tripRequestItem.pickup_title

        }
    }


    override fun inits() {

        if (pickUp != null) {
            AppStore.getInstance().tripRequestItem.pickup_latitude = pickUp?.latitude.toString()
            AppStore.getInstance().tripRequestItem.pickup_longitude = pickUp?.longitude.toString()
            geoCodePickup(pickUp?.latitude, pickUp?.longitude, null)
        }

        recyclerview_dropoffs.initVertical(context!!)
        recyclerview_savedlocations.initVertical(context!!)

        dropOffAdp = DropoffItemsAdapter(context, LocationClickedInterface { location ->
            showLoader()
            pickPlace(object : LocationPickedInterface {
                override fun onLocationSelected(pickedLocation: LocationItem) {
                    hideLoader()
                    location.copy(pickedLocation)
                    dropOffAdp.notifyDataSetChanged()
                }
            })
        })
        recyclerview_dropoffs.adapter = dropOffAdp
        savedLocationAdp = EfficientRecyclerAdapter(R.layout.item_save_location, SaveLocationHolder::class.java, roomDb.locationDoa().all)
        recyclerview_savedlocations.setAdapter(savedLocationAdp)
    }

    override fun setEvents() {

        txt_pickup.setOnClickListener { view ->
            showLoader()
            pickPlace(object : LocationPickedInterface {
                override fun onLocationSelected(location: LocationItem) {
                    hideLoader()
                    AppStore.getInstance().tripRequestItem.pickup_latitude = location.latitude
                    AppStore.getInstance().tripRequestItem.pickup_longitude = location.longitude
                    geoCodePickup(location.latitude.toDouble(), location.longitude.toDouble(), location.address)
                }
            })
        }

        savedLocationAdp.setOnItemClickListener { adapter, view, `object`, position ->
            dropOffAdp.newLocationSelected(`object`)
        }
    }
}