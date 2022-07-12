package com.rsmnm.Services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.gms.location.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.rsmnm.Networking.WebResponse
import com.rsmnm.Networking.WebServiceFactory
import com.rsmnm.R
import com.rsmnm.Utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.imperiumlabs.geofirestore.GeoFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 */

class ForegroundLocationService : Service() {

    internal var context: Context = this
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationCallback: LocationCallback? = null
    var lastUpdateTime: Long = 0L
    val CHANNEL_ID = "location_tracking"

    private lateinit var fireStoreRef: CollectionReference
    private lateinit var geoFirestore: GeoFirestore
    companion object {
        val START = "stop_foreground"
        val STOP = "start_foreground"
        var IS_SERVICE_RUNNING = false
        val FOREGROUND_NOTIF_ID = 181
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        StaticMethods.initPreferences(context)
        fireStoreRef = FirebaseFirestore.getInstance().collection("drivers")
        geoFirestore = GeoFirestore(fireStoreRef)

        try {
            if (intent?.action == START) {
                startForeground()
                startLocationWork()
                IS_SERVICE_RUNNING = true
                Log.v("Foreground Service", "Started")
            } else {
                stopForeground(true)
                stopLocationWork()
                IS_SERVICE_RUNNING = false
                Log.v("Foreground Service", "Stoped")
                fireStoreRef.document(getUserItem()!!.user_id).delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        return if (getUserItem() != null && getUserItem()?.is_online == 1)
        return if (getUserItem() != null)
            Service.START_STICKY
        else
            Service.START_NOT_STICKY
    }

    private fun startLocationWork() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }


        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                for (location in locationResult!!.locations) {
                    Log.v("Foreground Service", location.toString())

                    AppStore.getInstance().locationLiveData.postValue(location)
                    addDistanceToTrip(location)
                    updateOnFirestore(location)
                    updateOnServer(location)
                    geoCodeStateCity(location)
                }
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.smallestDisplacement = 0f
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback!!, null)
    }

    fun registerChanelID() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun geoCodeStateCity(location: Location) {

        if (PreferencesManager.getString("state").isEmpty() || PreferencesManager.getString("city").isEmpty()) {

            location.apply {
                if (latitude == null || longitude == null)
                    return
                val geocoder = Geocoder(context, Locale.ENGLISH)

                GlobalScope.launch(Dispatchers.Main) {
                    //                    async {
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (addresses.size > 0) {
                            val fetchedAddress = addresses[0]
                            PreferencesManager.putString("state", fetchedAddress.adminArea)
                            PreferencesManager.putString("city", fetchedAddress.locality)
                        } else {
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
//                    }.await()
                }
            }
        }
    }

    private fun addDistanceToTrip(location: Location) {
        if (getTripItem() != null) {
            var distance = PreferencesManager.getString(AppConstants.KEY_DISTANCE).asDouble()
            if (!PreferencesManager.getString("last_lat").isNullOrEmpty() && !PreferencesManager.getString("last_long").isNullOrEmpty()) {
                val lastLoc = Location("lastLoc").apply {
                    latitude = PreferencesManager.getString("last_lat").toDouble()
                    longitude = PreferencesManager.getString("last_long").toDouble()
                }
                val diff = location.distanceTo(lastLoc)
                distance = distance.plus(diff)
                PreferencesManager.putString(AppConstants.KEY_DISTANCE, distance.toString())
                Log.e("TripDistance", distance.toString())
            }
            PreferencesManager.putString("last_lat", location.latitude.toString())
            PreferencesManager.putString("last_long", location.longitude.toString())
        }
    }

    private fun stopLocationWork() {
        try {
            stopSelf()
            Log.v("LocaitonUpdate", "Closed")
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateOnServer(location: Location) {
        if (System.currentTimeMillis() - lastUpdateTime > AppConstants.INTERVAL_BETWEEN_LOCATION_UPDATES) {
            var map: TreeMap<String, String> = TreeMap()
            map.put("_token", getUserItem()!!.token)
            map.put("latitude", location.latitude.toString())
            map.put("longitude", location.longitude.toString())
            map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

            WebServiceFactory.getInstance().updateLocation(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any>> {
                override fun onResponse(call: Call<WebResponse<Any>>, response: Response<WebResponse<Any>>) {
                    lastUpdateTime = System.currentTimeMillis()
                }

                override fun onFailure(call: Call<WebResponse<Any>>, t: Throwable) {
                }
            })
        }
    }

    private fun updateOnFirestore(location: Location) {
        geoFirestore.setLocation(getUserItem()?.user_id, GeoPoint(location.latitude, location.longitude))
    }

    fun startForeground() {
        if (getUserItem() == null) {
            return
        }

        registerChanelID()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.logo_white_login)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        builder.setContentTitle(getString(R.string.app_name))
        builder.setContentText("Your Location is being tracked..")
        startForeground(FOREGROUND_NOTIF_ID, builder.build())
    }
}
