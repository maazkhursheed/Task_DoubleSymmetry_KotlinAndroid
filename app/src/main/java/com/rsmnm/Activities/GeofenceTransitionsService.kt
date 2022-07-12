package com.rsmnm.Activities

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.rsmnm.Models.LocationItem
import com.rsmnm.Utils.AppConstants
import com.rsmnm.Utils.PreferencesManager

class GeofenceTransitionsService : IntentService("GeofenceTransitionsService") {
    private val TAG: String? = "GeofenceTransitionsService"


    @SuppressLint("LongLogTag")
    override fun onHandleIntent(intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = "Error occurred"
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                    triggeringGeofences
            )
            Log.i(TAG, "geo-fetch".plus(geofenceTransitionDetails!!.latitude).plus("_").plus(geofenceTransitionDetails!!.longitude))

            if (geofenceTransitionDetails != null) {
                PreferencesManager.putBoolean(AppConstants.IS_TOLL_REQUIRED, true)
            }

        } else {
            // Log the error.
            Log.e(TAG, "geofence_transition_invalid_type")
        }
    }

    @SuppressLint("LongLogTag")
    private fun getGeofenceTransitionDetails(triggeringGeofences: List<Geofence>): LocationItem? {
        try {
            Log.e(TAG, triggeringGeofences[0].requestId)
            if (triggeringGeofences[0].requestId.contains("_")) {
                val data = triggeringGeofences[0].requestId.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val locationItem = LocationItem()
                locationItem.latitude = data[0]
                locationItem.longitude = data[1]
                return locationItem
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}