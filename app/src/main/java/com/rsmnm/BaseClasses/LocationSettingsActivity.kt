package com.rsmnm.BaseClasses

import android.content.Intent
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.rsmnm.Interfaces.WorkCheckInterface
import com.rsmnm.Utils.permissionutils.PermissionResult
import com.rsmnm.Utils.permissionutils.PermissionUtils

abstract class LocationSettingsActivity : FragmentHandlingActivity() {

    private val REQUEST_CHECK_SETTINGS = 823
    private var locationCheckInterface: WorkCheckInterface? = null

    fun checkLocationEnabled(iface: WorkCheckInterface?, openRessolver: Boolean) {
        locationCheckInterface = iface
        askCompactPermission(PermissionUtils.Manifest_ACCESS_FINE_LOCATION, object : PermissionResult {
            override fun permissionDenied() {
                locationCheckInterface?.onFailure()
            }

            override fun permissionForeverDenied() {
                locationCheckInterface?.onFailure()
            }

            override fun permissionGranted() {
                val builder = LocationSettingsRequest.Builder()
                val mLocationRequest = LocationRequest()
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                builder.addLocationRequest(mLocationRequest)
                val client = LocationServices.getSettingsClient(mContext)

                val task = client.checkLocationSettings(builder.build())
                task.addOnSuccessListener(mContext) { locationSettingsResponse -> locationCheckInterface?.onCompleted() }
                task.addOnFailureListener(mContext) { e ->
                    if (e is ResolvableApiException) {
                        try {
                            val resolvable = e
                            if (openRessolver)
                                resolvable.startResolutionForResult(this@LocationSettingsActivity, REQUEST_CHECK_SETTINGS)
                            else
                                locationCheckInterface?.onFailure()
                        } catch (sendEx: Exception) {
                            sendEx.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                if (resultCode == RESULT_OK)
                    locationCheckInterface?.onCompleted()
                else
                    locationCheckInterface?.onFailure()
            }
        } catch (e: Exception) {
        }
    }
}