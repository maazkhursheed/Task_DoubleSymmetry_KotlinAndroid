package com.rsmnm.Fragments.driver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.view.View
import android.widget.RelativeLayout
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ncorti.slidetoact.SlideToActView
import com.rsmnm.Activities.GeofenceTransitionsService
import com.rsmnm.BaseClasses.PlacePickerFragment
import com.rsmnm.Dialogs.DestinationSearchDialog
import com.rsmnm.Dialogs.PassengerInfoDialog
import com.rsmnm.Interfaces.LocationPickedInterface
import com.rsmnm.Interfaces.WorkCompletedInterface
import com.rsmnm.Models.LocationItem
import com.rsmnm.Models.Resource
import com.rsmnm.Models.TripItem
import com.rsmnm.Networking.WebResponse
import com.rsmnm.R
import com.rsmnm.Utils.*
import com.rsmnm.Utils.permissionutils.PermissionUtils
import com.rsmnm.ViewModels.DriverViewModel
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.fragment_driver_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Created by saqib on 9/25/2018.
 */
class HomeFragment : PlacePickerFragment(), OnMapReadyCallback, Observer<Resource<WebResponse<TripItem>>> {

    val BTN_ARRIVED = "Arrived"
    val BTN_START = "Start"
    val BTN_END_RIDE = "End Ride"

    lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(fragmentActivity, GeofenceTransitionsService::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getService(fragmentActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private lateinit var driverViewModel: DriverViewModel
    private lateinit var cancelListener: View.OnClickListener
    var googleMap: GoogleMap? = null
    override fun getLayout(): Int = R.layout.fragment_driver_home
    var isCameraMoved = false
    var driverMarker: Marker? = null
    var tripItem: TripItem? = null
    lateinit var titleBar: TitleBar
    private lateinit var runnable: Runnable

    var status: TripItem.TripStatus? = null

    override fun getTitleBar(titleBar: TitleBar) {
        this.titleBar = titleBar
        titleBar.resetTitleBar().setTitle("Updating Status").enableRightButton(R.drawable.actionbar_search, View.OnClickListener {
            actionSearch()
        })
    }

    override fun activityCreated(savedInstanceState: Bundle?) {
        driverViewModel = ViewModelProviders.of(fragmentActivity).get(DriverViewModel::class.java)
        geofencingClient = LocationServices.getGeofencingClient(fragmentActivity)
        checkCurrentRide()
        driverViewModel.getProfile().observe(this, Observer { if (it?.status == Resource.Status.success && it.data?.body != null) setUserItem(it.data.body!!) })
    }

    private fun checkCurrentRide() {

        driverViewModel.getActiveRide().observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    tripItem = response.data?.body
                    if (tripItem != null) {
                        setTripItem(tripItem)
                        setMode(tripItem!!)
                    }
                }
                else -> {
                    setIdle()
                    hideLoader()
                }
            }
        })
    }

    override fun inits() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    fun setMode(tripItem: TripItem) {

        Log("Mode", tripItem.ride_status_text?.name)
        if (status != tripItem.ride_status_text) {
            when (tripItem.ride_status_text) {
                TripItem.TripStatus.active -> setActive()
                TripItem.TripStatus.arrived -> {
                    PreferencesManager.putBoolean(AppConstants.IS_TOLL_REQUIRED, false)
                    setArrived()
                }
                TripItem.TripStatus.started -> {
                    if (tripItem.tolls != null) {
                        addGeofenceLocations(tripItem.tolls)
                    }
                    setStarted()
                }
                TripItem.TripStatus.ended -> {
                    setEnded()
                    removedGeofenceLocations()
                }
            }
            status = tripItem.ride_status_text
        }
    }

    private fun removedGeofenceLocations() {
        geofencingClient?.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener {
                android.util.Log.e("Geofence", "Locations are removed successfullly!")
            }
            addOnFailureListener {
                // Failed to add geofences
                // ...
                makeSnackbar("Error in removed locations")
                it.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofenceLocations(tolls: java.util.ArrayList<LocationItem>?) {
        var geofences = ArrayList<Geofence>()
        var a: String = "150"
        tolls!!.forEach { locationItem ->

            var requestId = "".plus(locationItem.latitude).plus("_").plus(locationItem.longitude)
            geofences.add(Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(requestId)


                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            locationItem.latitude.toDouble(),
                            locationItem.longitude.toDouble(),
                            a.toFloat()
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build())
        }

        geofencingClient?.addGeofences(buildGeofencingRequest(geofences), geofencePendingIntent)?.run {
            addOnSuccessListener {
                android.util.Log.e("Geofence", "Locations are geofenced successfullly!")
            }
            addOnFailureListener {
                makeSnackbar("Error in geofenced locations")
                it.printStackTrace()
            }
        }
    }


    private fun buildGeofencingRequest(geofences: ArrayList<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofences)
        }.build()
    }

    //    [24.87979624895579178200932801701128482818603515625 lat, 67.064481675624847412109375 lng]
    private fun buildGeofence(toll: LocationItem): Geofence {
        android.util.Log.e("tolls:", String.format("[%s lat, %s lng]", toll.latitude, toll.longitude))
        val latitude = toll.latitude.toDouble()
        val longitude = toll.longitude.toDouble()
        val radius = 1

        return Geofence.Builder()
                .setRequestId(toll.latitude.plus("_").plus(toll.longitude))
                .setCircularRegion(
                        latitude,
                        longitude,
                        radius.toFloat()
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
    }

    private fun setIdle() {
        setTripItem(null)
        clearMap()
        layout_ride_status.visibility = View.GONE
        layout_wait_time.visibility = View.GONE
//        titleBar.disableRightButton()
        titleBar.enableStatusSwitch()
        slider.visibility = View.GONE
    }

    private fun setActive() {
        btn_search.visibility = View.VISIBLE
        layout_ride_status.visibility = View.VISIBLE
        txt_ride_status.text = "Picking up passenger"


        titleBar.hideStatusSwitch()
        titleBar.enableRightButton("Cancel", cancelListener)
        clearMap()
        drawPickupLocation()
        slider.resetSlider()
        slider.visibility = View.VISIBLE
        layout_wait_time.visibility = View.GONE
        (slider.layoutParams as RelativeLayout.LayoutParams).setMargins(0, 0, 0, StaticMethods.convertDpToPixel(70.0f, context))
        slider.text = BTN_ARRIVED
        slider.outerColor = ContextCompat.getColor(fragmentActivity, R.color.colorPrimary)
    }

    private fun setArrived() {
        btn_search.visibility = View.GONE
        layout_ride_status.visibility = View.VISIBLE
        txt_ride_status.text = "Waiting for passenger"

        titleBar.hideStatusSwitch()
        titleBar.disableRightButton()
        clearMap()
        slider.resetSlider()
        slider.visibility = View.VISIBLE
        (slider.layoutParams as RelativeLayout.LayoutParams).setMargins(0, 0, 0, StaticMethods.convertDpToPixel(120.0f, context))
        startTimer()
        slider.text = BTN_START
        slider.outerColor = ContextCompat.getColor(fragmentActivity, R.color.colorPrimary)
    }

    private fun setStarted() {
        btn_search.visibility = View.GONE
        layout_ride_status.visibility = View.GONE
        txt_ride_status.text = "On Route"

        titleBar.hideStatusSwitch()
        titleBar.disableRightButton()
        clearMap()
        drawDropoffRoute()
        slider.resetSlider()
        slider.visibility = View.VISIBLE
        layout_wait_time.visibility = View.GONE
        (slider.layoutParams as RelativeLayout.LayoutParams).setMargins(0, 0, 0, StaticMethods.convertDpToPixel(70.0f, context))
        slider.text = BTN_END_RIDE
        slider.outerColor = ContextCompat.getColor(fragmentActivity, R.color.red)
    }

    private fun setEnded() {
        setTripItem(null)
        layout_ride_status.visibility = View.GONE
        slider.visibility = View.GONE
        setIdle()
        fragmentActivity.replaceFragmentWithBackstack(ReceiptFragment.newInstance(tripItem!!))
    }

    override fun setEvents() {
        btn_search.setOnClickListener {
            actionSearch()
        }

        slider.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                when (slider.text) {
                    BTN_ARRIVED -> {
                        driverViewModel.rideArrived(tripItem!!.ride_id).observe(this@HomeFragment, this@HomeFragment)
                    }
                    BTN_START -> driverViewModel.rideStarted(tripItem!!.ride_id).observe(this@HomeFragment, this@HomeFragment)
                    BTN_END_RIDE -> driverViewModel.rideCompleted(tripItem!!.ride_id).observe(this@HomeFragment, this@HomeFragment)
                }
            }
        }

        btn_ride_detail.setOnClickListener {
            if (tripItem != null) {
                PassengerInfoDialog.newInstance(tripItem?.passenger!!).show(childFragmentManager, "passenger_info")
            }
        }

        cancelListener = View.OnClickListener {
            if (tripItem != null)
                driverViewModel.cancelTrip(tripItem?.ride_id!!).observe(this, Observer {
                    when (it?.status) {
                        Resource.Status.loading -> showLoader()
                        Resource.Status.success -> {
                            hideLoader()
                            setIdle()
                            checkCurrentRide()
                        }
                        else -> {

                        }
                    }
                })
        }
    }

    private fun actionSearch() {
        DestinationSearchDialog.newInstance(WorkCompletedInterface {
            pickPlace(object : LocationPickedInterface {
                override fun onLocationSelected(location: LocationItem) {
                    driverViewModel.setPreferredLocation(location).observe(this@HomeFragment, Observer { response ->
                        when (response?.status) {
                            Resource.Status.loading -> showLoader()
                            else -> {
                                hideLoader()
                                makeSnackbar(response?.data)
                            }
                        }
                    })
                }
            })
        }).show(childFragmentManager, "new_location")
    }


    private fun startTimer() {
        if (tripItem == null)
            return

        layout_wait_time.visibility = View.VISIBLE

        var currDiff: Long? = null
        if (tripItem?.arrive_time == null)
            currDiff = 0.toLong()
        else
            currDiff = System.currentTimeMillis().minus(tripItem?.arrive_time?.times(1000)!!)

        txt_timer.tag = currDiff

        runnable = Runnable {
            if (isAdded) {
                var tag = txt_timer.getTag()
                var time: Long
                if (tag != null)
                    time = tag as Long
                else
                    time = 0.toLong()

                time += 1000
                txt_timer.setText(DateTimeHelper.getMinutesSeconds(time))
                txt_timer.setTag(time)
                if (time >= (1000 * 60 * 5))
                    titleBar.enableRightButton("Cancel", cancelListener)
                handler.postDelayed(runnable, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)
    }

    override fun onDestroy() {
        try {
            handler.removeCallbacks(runnable)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }

    // Ride Change Responses
    override fun onChanged(response: Resource<WebResponse<TripItem>>?) {
        when (response?.status) {
            Resource.Status.loading -> showLoader()
            Resource.Status.success -> {
                hideLoader()
                tripItem = response.data?.body
                if (tripItem != null) {
                    setTripItem(tripItem)
                    setMode(tripItem!!)
                }
            }
            else -> {
                slider.resetSlider()
                makeSnackbar(response?.data)
                hideLoader()
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {

        if (checkSelfPermission(context!!, PermissionUtils.Manifest_ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            return

        googleMap = map
        googleMap!!.getUiSettings().isMapToolbarEnabled = false
        googleMap!!.getUiSettings().isZoomControlsEnabled = false
        googleMap?.setMyLocationEnabled(true);
        googleMap!!.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap?.setPadding(0, btn_search.measuredHeight + 50, 0, 0)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.995580, -87.685750), 3.0f));

        AppStore.getInstance().locationLiveData.observe(this, Observer { location ->
            if (location != null && googleMap != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                if (driverMarker == null)
                    driverMarker = googleMap?.addMarker(MarkerOptions().title("You").position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_driver_home)))
                else
                    driverMarker?.position = latLng

                if (!isCameraMoved) {
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, AppConstants.DEFAULT_ZOOM))
                    isCameraMoved = true
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        isCameraMoved = false
    }

    private fun clearMap() {
        googleMap?.clear()
        if (driverMarker != null)
            driverMarker = googleMap?.addMarker(MarkerOptions().position(driverMarker!!.position).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_driver_home)))
    }

    private fun drawPickupLocation() {

        GlobalScope.launch(Dispatchers.Main) {
            while (driverMarker == null)
                delay(500)

            var pickup = LatLng(tripItem!!.pickup_latitude.toDouble(), tripItem!!.pickup_longitude.toDouble())
            googleMap?.addMarker(MarkerOptions().position(pickup).title("Pickup").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)))
            GoogleDirection.withServerKey(getResources().getString(R.string.google_unrestricted_key))
                    .from(driverMarker?.position)
                    .to(pickup)
                    .alternativeRoute(false)
                    .transportMode(TransportMode.DRIVING)
                    .execute(object : DirectionCallback {
                        override fun onDirectionSuccess(direction: Direction?, rawBody: String?) {
                            if (direction?.isOK!!) {
                                googleMap?.addPolyline(DirectionConverter.createPolyline(getContext(), direction.routeList[0].getAllPoints(), 5, ContextCompat.getColor(fragmentActivity, R.color.colorAccent)))
                            }
                        }

                        override fun onDirectionFailure(t: Throwable?) {
                        }
                    })
        }
    }

    private fun drawDropoffRoute() {

        tripItem!!.apply {
            if (dropoffs == null || dropoffs.isEmpty())
                return

            GlobalScope.launch(Dispatchers.Main) {
                val pickup = LatLng(pickup_latitude.toDouble(), pickup_longitude.toDouble())
                val destination = dropoffs.get(dropoffs.size - 1).getAsLatLng()
                val waypoints = ArrayList(dropoffs).apply { removeAt((size - 1)) }.getLatLngList()

                googleMap?.addMarker(MarkerOptions().position(pickup).title("Pickup").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)))
                googleMap?.addMarker(MarkerOptions().position(destination).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red)))
                waypoints.forEach { latLng: LatLng -> googleMap?.addMarker(MarkerOptions().position(latLng).title("stopover").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red))) }

                GoogleDirection.withServerKey(getResources().getString(R.string.google_unrestricted_key))
                        .from(pickup)
                        .and(waypoints)
                        .to(destination)
                        .alternativeRoute(false)
                        .transportMode(TransportMode.DRIVING)
                        .execute(object : DirectionCallback {
                            override fun onDirectionSuccess(direction: Direction?, rawBody: String?) {
                                if (direction?.isOK!!) {
                                    googleMap?.addPolyline(DirectionConverter.createPolyline(getContext(), direction.routeList[0].getAllPoints(), 5, ContextCompat.getColor(fragmentActivity, R.color.colorAccent)))
                                }
                            }

                            override fun onDirectionFailure(t: Throwable?) {
                            }
                        })
            }
        }
    }
}