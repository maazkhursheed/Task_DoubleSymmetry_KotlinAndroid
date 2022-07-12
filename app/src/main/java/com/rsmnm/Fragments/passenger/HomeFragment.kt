package com.rsmnm.Fragments.passenger

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewTreeObserver
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import com.rsmnm.Activities.PassengerActivity
import com.rsmnm.Adapters.HomeVehicleTypesAdapter
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Dialogs.RideLaterDialog
import com.rsmnm.Enums.HomeCurrentStatus
import com.rsmnm.Fragments.PaymentFragment
import com.rsmnm.Interfaces.DateTimeSelectedInterface
import com.rsmnm.Interfaces.VehicleSelectedInterface
import com.rsmnm.Interfaces.WorkCheckInterface
import com.rsmnm.Models.LocationItem
import com.rsmnm.Models.Resource
import com.rsmnm.Models.TripItem
import com.rsmnm.Models.VehicleTypeItem
import com.rsmnm.R
import com.rsmnm.Services.LocationTracker
import com.rsmnm.Utils.*
import com.rsmnm.ViewModels.PassengerViewModel
import com.rsmnm.Views.TitleBar
import com.squareup.picasso.Picasso
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.fragment_user_home.*
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoQuery
import org.imperiumlabs.geofirestore.GeoQueryEventListener

class HomeFragment : BaseFragment(), OnMapReadyCallback {

    var googleMap: GoogleMap? = null
    var status: HomeCurrentStatus = HomeCurrentStatus.idle_location_disabled
    lateinit var passengerViewModel: PassengerViewModel
    private var pickLocation: Location? = null
    private lateinit var titlebar: TitleBar
    private var selectedVehicleType: VehicleTypeItem? = null
    var tripItem: TripItem? = null
    var selfLocation: LatLng? = null
    var driversMarkersMap = HashMap<String, Marker>()
    var rideDriverMarker: Marker? = null

    private lateinit var driverCollectionRef: CollectionReference
    private lateinit var geoFirestore: GeoFirestore

    override fun getLayout() = R.layout.fragment_user_home


    override fun getTitleBar(titleBar: TitleBar?) {
        titlebar = titleBar!!
        titleBar.resetTitleBar().enableMenu()
    }

    override fun activityCreated(savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        checkCurrentRide()
    }

    fun checkCurrentRide() {

        passengerViewModel.getActiveRide().observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    tripItem = response.data?.body
                    if (tripItem != null) {
                        AppStore.getInstance().tripRequestItem = null
                        setMode(HomeCurrentStatus.in_ride)
                    }
                }
                else -> {
                    hideLoader()
                    if (status == HomeCurrentStatus.in_ride)
                        initUserState()
                }
            }
        })
    }


    override fun inits() {
        driverCollectionRef = FirebaseFirestore.getInstance().collection("drivers")
        geoFirestore = GeoFirestore(driverCollectionRef)

        passengerViewModel = ViewModelProviders.of(fragmentActivity).get(PassengerViewModel::class.java)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        BounceView.addAnimTo(btn_contact)
        BounceView.addAnimTo(btn_cancel)
    }

    override fun setEvents() {

        BounceView.addAnimTo(btn_confirm)

        val goPickDropListner: View.OnClickListener = View.OnClickListener {
            AppStore.getInstance().tripRequestItem = null
            fragmentActivity.replaceFragmentWithBackstack(SelectPickupDropFragment.newInstance(pickLocation))
        }

        btn_pickup.setOnClickListener(goPickDropListner)
        layout_idle_location_enabled.setOnClickListener(goPickDropListner)

        val rideLaterListener: View.OnClickListener = View.OnClickListener {
            RideLaterDialog.newInstance(DateTimeSelectedInterface {
                AppStore.getInstance().tripRequestItem = null
                fragmentActivity.replaceFragmentWithBackstack(SelectPickupDropFragment.newInstance(pickLocation, it.timeInMillis))
            }).show(childFragmentManager, "RideLater")
        }

        btn_ridelater.setOnClickListener(rideLaterListener)
        btn_ridelater2.setOnClickListener(rideLaterListener)

        btn_confirm.setOnClickListener { view ->
            AppStore.getInstance().tripRequestItem.is_blind = if (blind_passenger.isChecked) 1 else 0
            if (selectedVehicleType == null)
                makeSnackbar("Please Select a Vehicle type")
            else
                passengerViewModel.requestRide(selectedVehicleType!!, AppStore.getInstance().tripRequestItem).observe(this, Observer { response ->
                    when (response?.status) {
                        Resource.Status.loading -> showRideLoader()
                        Resource.Status.success -> {
                            tripItem = response.data?.body
                            AppStore.getInstance().tripRequestItem = null
                            if (tripItem != null && !tripItem?.ride_id.isNullOrEmpty())
                                setMode(HomeCurrentStatus.in_ride)
                            else
                                initUserState()
                            hideRideLoader()
                        }
                        Resource.Status.action_card_not_added -> {
                            fragmentActivity.replaceFragmentWithBackstack(PaymentFragment.newInstance(true))
                            makeSnackbar(response.data)
                        }
                        else -> {
                            hideRideLoader()
                            makeSnackbar(response?.data)
                        }
                    }
                })
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map!!
        googleMap!!.uiSettings.isMapToolbarEnabled = false

        initUserState()
    }

    private fun initUserState() {
        if (AppStore.getInstance().tripRequestItem != null && AppStore.getInstance().tripRequestItem.isPickupCompleted)
            setMode(HomeCurrentStatus.searching_ride)
        else {
            startLocationWork(false)
        }
    }

    fun setMode(currStatus: HomeCurrentStatus) {
        if (!isAdded)
            return

        this.status = currStatus
        Log("Mode", status.name)
        when (status) {
            HomeCurrentStatus.idle_location_disabled -> showLocationDisabledUI()
            HomeCurrentStatus.idle_location_finding -> showLocationEnabledUI()
            HomeCurrentStatus.idle_location_found -> showLocationEnabledUI()
            HomeCurrentStatus.searching_ride -> showSearchingUI()
            HomeCurrentStatus.in_ride -> showInRideUI()
        }
    }

    fun clearSearch() {
        titlebar.enableMenu()
        googleMap?.clear()
        AppStore.getInstance().tripRequestItem = null
        layout_vehicle_type.visibility = View.GONE
        initUserState()
    }

    private fun showPickDrop(tripRequestItem: TripItem, onlyMarker: Boolean = false) {

        val pickUp = LatLng(tripRequestItem.pickup_latitude.toDouble(), tripRequestItem.pickup_longitude.toDouble())
        var dropoffList = ArrayList(tripRequestItem.dropoffs)
        googleMap?.addMarker(MarkerOptions().position(pickUp).title("pickup").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)))

        if (dropoffList.isEmpty() && !onlyMarker) {
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(pickUp, AppConstants.DEFAULT_ZOOM))
        } else {
            if (!onlyMarker) {
                try {
                    googleMap?.animateCamera(StaticMethods.getLatLngBounds(pickUp, dropoffList).getCameraUpdate())
                } catch (e: Throwable) {
                    e.printStackTrace()
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(StaticMethods.getLatLngBounds(pickUp, dropoffList), 30))
                }
            }

            dropoffList.forEach { locationItem: LocationItem? ->
                googleMap?.addMarker(MarkerOptions().position(LatLng(locationItem?.latitude?.toDouble()!!, locationItem.longitude?.toDouble()!!)).title("dropoff").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red)))
            }

            val destination = LatLng(dropoffList[dropoffList.size - 1].latitude.toDouble(), dropoffList[dropoffList.size - 1].longitude.toDouble())
            dropoffList.removeAt(dropoffList.size - 1)

            if (!onlyMarker)
                GoogleDirection.withServerKey(resources.getString(R.string.google_unrestricted_key))
                        .from(pickUp)
                        .and(dropoffList.getLatLngList())
                        .to(destination)
                        .alternativeRoute(false)
                        .transportMode(TransportMode.DRIVING)
                        .execute(object : DirectionCallback {
                            override fun onDirectionSuccess(direction: Direction?, rawBody: String?) {
                                if (direction?.isOK!!) {
                                    var polygon = DirectionConverter.createPolyline(context,
                                            direction.routeList[0].getAllPoints(),
                                            5,
                                            ContextCompat.getColor(fragmentActivity, R.color.colorAccent));

                                    googleMap?.addPolyline(polygon);

//                                    var latLng = LatLng(dropoffList[0].latitude.toDouble(), dropoffList[0].latitude.toDouble())
//                                    googleMap?.moveCamera (CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                                }
                            }

                            override fun onDirectionFailure(t: Throwable?) {

                            }
                        })
        }
    }

    private fun showLocationDisabledUI() {
        titlebar.disableRightButton()
        titlebar.enableMenu()
        googleMap?.clear()
        googleMap?.setPadding(0, 0, 0, 0)
        layout_in_ride.visibility = View.GONE
        layout_vehicle_type.visibility = View.GONE
        layout_idle_location_disabled.visibility = View.VISIBLE
        layout_idle_location_enabled.visibility = View.GONE
        btn_enable_location.setOnClickListener { view ->
            startLocationWork(true)
        }
    }


    private fun showLocationEnabledUI() {
        titlebar.enableMenu()
        titlebar.disableRightButton()
        googleMap?.clear()
        googleMap?.setPadding(0, 0, 0, 0)
        layout_in_ride.visibility = View.GONE
        layout_vehicle_type.visibility = View.GONE
        layout_idle_location_disabled.visibility = View.GONE
        layout_idle_location_enabled.visibility = View.VISIBLE
        if (status == HomeCurrentStatus.idle_location_found) {
            implementNearbyListener(selfLocation)
        }
    }

    private fun showSearchingUI() {
        titlebar.disableRightButton()
        titlebar.enableBack()
        layout_idle_location_enabled.visibility = View.GONE
        layout_vehicle_type.visibility = View.VISIBLE
        showPickDrop(AppStore.getInstance().tripRequestItem!!)
        implementNearbyListener(LatLng(AppStore.getInstance().tripRequestItem.pickup_latitude.toDouble(), AppStore.getInstance().tripRequestItem.pickup_longitude.toDouble()))


        passengerViewModel.getVehicleTypes(AppStore.getInstance().tripRequestItem.pickup_state, AppStore.getInstance().tripRequestItem.pickup_city).observe(this, Observer { response ->
            when (response?.status) {
                Resource.Status.loading -> showLoader()
                Resource.Status.success -> {
                    hideLoader()
                    layout_vehicle_type.visibility = View.VISIBLE
                    recycler_vehicletypes.visibility = View.VISIBLE
                    progress_vehicletype.visibility = View.GONE
                    var vehicelTypeAdp = HomeVehicleTypesAdapter(context, response.data?.body, VehicleSelectedInterface { item ->
                        txt_vehicle_capacity.text = "1-" + item.seats
                        selectedVehicleType = item
                    })
                    recycler_vehicletypes.initHorizontal(fragmentActivity, false)
                    recycler_vehicletypes.adapter = vehicelTypeAdp
                }
                else -> {
                    hideLoader()
                    makeSnackbar(response?.data)
                }
            }
        })
    }

    private fun showInRideUI() {
        layout_vehicle_type.visibility = View.GONE
        layout_idle_location_enabled.visibility = View.GONE
        layout_in_ride.visibility = View.VISIBLE
        titlebar.enableMenu()
        nearbyQuery?.removeAllListeners()
        clearMapExceptDriver()
        trackDriver(tripItem?.driver?.user_id)
        showPickDrop(tripItem!!)

        driver_name.text = tripItem?.driver?.fullName

        if (tripItem?.driver?.rating != null) {
            var rating = tripItem?.driver?.rating?.toDouble()
            if (rating!! > 4.5) {
                driver_rating.text = tripItem?.driver?.rating
            } else {
                driver_rating.text = "(pending)"
            }
        } else {
            driver_rating.text = "(pending)"
        }


        vehicle_make_model.text = """${tripItem?.driver?.make} (${tripItem?.driver?.model})"""
        vehicle_type.text = tripItem?.driver?.car?.title
        vehicle_number.text = tripItem?.driver?.license_no
        if (!tripItem?.driver?.profile_picture.isNullOrEmpty())
            Picasso.get().load(tripItem?.driver?.profile_picture).fit().centerCrop().into(driver_img)

        layout_in_ride.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                try {
                    googleMap?.setPadding(50, 0, 0, layout_in_ride.measuredHeight + 50)
                    layout_in_ride.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        btn_cancel.setOnClickListener {
            if (tripItem?.ride_status_text == TripItem.TripStatus.started) {
                makeSnackbar("Ride cannot be canceled at this stage")
                return@setOnClickListener
            }
            passengerViewModel.cancelTrip(tripItem!!.ride_id).observe(this, Observer { response ->
                when (response?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        googleMap?.clear()
                        hideLoader()
                        initUserState()
                    }
                    else -> {
                        hideLoader()
                        makeSnackbar(response?.data)
                    }
                }
            })
        }

        btn_contact.setOnClickListener {
            fragmentActivity.createContactIntent(tripItem?.driver?.phone)
        }
    }

    private fun clearMapExceptDriver() {
        googleMap?.clear()
        if (rideDriverMarker != null)
            rideDriverMarker = googleMap?.addMarker(MarkerOptions().position(rideDriverMarker!!.position).title("You").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_driver_home)))

    }

    private fun trackDriver(driver_id: String?) {
        if (driver_id == null)
            return
        driverCollectionRef.document(driver_id).addSnapshotListener { document: DocumentSnapshot?, exception: FirebaseFirestoreException? ->
            if (exception == null && document != null && document.data?.get("l") != null) {
                try {
                    val location = (document.data?.get("l") as ArrayList<Double>)
                    val possition = LatLng(location[0], location[1])
                    if (rideDriverMarker == null)
                        rideDriverMarker = googleMap?.addMarker(MarkerOptions().position(possition).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_driver_home)))
                    else
                        rideDriverMarker?.position = possition
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rideDriverMarker = null
    }

    private var nearbyQuery: GeoQuery? = null

    private fun implementNearbyListener(selfLocation: LatLng?) {
        if (selfLocation == null)
            return

        if (nearbyQuery != null)
            nearbyQuery?.removeAllListeners()

        nearbyQuery = geoFirestore.queryAtLocation(GeoPoint(selfLocation.latitude, selfLocation.longitude), 1.0)
        nearbyQuery?.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onGeoQueryReady() {
            }

            override fun onKeyEntered(docId: String?, point: GeoPoint?) {
                Log("entered", docId)
                if (point == null || docId == null)
                    return
                driversMarkersMap.put(docId, googleMap?.addMarker(MarkerOptions().position(LatLng(point.latitude, point.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_driver_home)))!!)
            }

            override fun onKeyMoved(docId: String?, point: GeoPoint?) {
                Log("moved", docId)
                if (point == null || docId == null)
                    return
                driversMarkersMap.get(docId)?.position = LatLng(point.latitude, point.longitude)
            }

            override fun onKeyExited(docId: String?) {
                Log("exit", docId)
                driversMarkersMap.remove(docId)
            }

            override fun onGeoQueryError(exception: java.lang.Exception?) {
                exception?.printStackTrace()
            }
        })

    }

    private fun startTrackingDriver() {

    }

    @SuppressLint("MissingPermission")
    private fun startLocationWork(openResolver: Boolean) {
        // Check Location Permission & Settings then observe location update
        (fragmentActivity as PassengerActivity).checkLocationEnabled(object : WorkCheckInterface {
            override fun onCompleted() {
//                googleMap.isMyLocationEnabled = true
                googleMap?.isMyLocationEnabled = true
                googleMap!!.uiSettings.isMyLocationButtonEnabled = false

                setMode(HomeCurrentStatus.idle_location_finding)
                val tracker = LocationTracker(fragmentActivity, true)
                tracker.locationData.observe(fragmentActivity, Observer { location ->
                    if (location != null) {
                        pickLocation = location
                        selfLocation = LatLng(location.latitude, location.longitude)
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(selfLocation, AppConstants.DEFAULT_ZOOM))
                        if (status != HomeCurrentStatus.in_ride)
                            setMode(HomeCurrentStatus.idle_location_found)
                    }
                })
                fragmentActivity.lifecycle.addObserver(tracker)
            }

            override fun onFailure() {
                setMode(HomeCurrentStatus.idle_location_disabled)
            }
        }, openResolver)
    }


    var animset: AnimatorSet? = null

    private fun showRideLoader() {
        loader.visibility = View.VISIBLE
        ripple_background.startRippleAnimation()

        if (animset == null) {
            val anim1 = ObjectAnimator.ofFloat(loader_circle, "rotation", -120f, 120f)
            anim1.duration = 2000
            val anim2 = ObjectAnimator.ofFloat(loader_circle, "rotation", 120f, -120f)
            anim2.duration = 2000

            animset = AnimatorSet()
            animset?.playSequentially(anim1, anim2)
            animset?.start()
            animset?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    try {
                        animset?.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }

            })
        }
    }

    private fun hideRideLoader() {
        try {
            loader.visibility = View.GONE
            ripple_background.stopRippleAnimation()
            animset?.removeAllListeners()
            animset?.cancel()
            animset = null
        } catch (e: Exception) {
        }
    }


}