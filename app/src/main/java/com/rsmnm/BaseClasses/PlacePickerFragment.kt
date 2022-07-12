package com.rsmnm.BaseClasses

import android.app.Activity
import android.content.Intent
import com.google.android.gms.location.places.ui.PlacePicker
import com.rsmnm.Interfaces.LocationPickedInterface
import com.rsmnm.Models.LocationItem

/**
 * Created by saqib on 9/12/2018.
 */
abstract class PlacePickerFragment : BaseFragment() {
    lateinit private var locationPickedInterface: LocationPickedInterface
    var PLACE_PICKER_REQUEST = 1532;

    fun pickPlace(locationPickedInterface: LocationPickedInterface) {
        this.locationPickedInterface = locationPickedInterface
        var builder = PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(context, data)
                var locationItem = LocationItem(place.name.toString(), place.address.toString(), place.latLng.latitude.toString(), place.latLng.longitude.toString())
                locationPickedInterface.onLocationSelected(locationItem)
            }
        }
    }
}