package com.rsmnm.Utils

import android.app.*
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.akexorcist.googledirection.model.Leg
import com.akexorcist.googledirection.model.Route
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
import com.rsmnm.Activities.DriverActivity
import com.rsmnm.Activities.PassengerActivity
import com.rsmnm.BuildConfig
import com.rsmnm.Interfaces.AuthenticationStatus
import com.rsmnm.Interfaces.DateTimeSelectedInterface
import com.rsmnm.Interfaces.NumberSelected
import com.rsmnm.Interfaces.WorkCompletedInterface
import com.rsmnm.Models.LocationItem
import com.rsmnm.Models.TripItem
import com.rsmnm.Models.UserItem
import com.rsmnm.R
import com.shawnlin.numberpicker.NumberPicker
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

var userData = MutableLiveData<UserItem?>()
var prefTripItem: TripItem? = null


fun AuthenticateUser(token: String?, mContext: Activity, authentication: AuthenticationStatus) {
    var auth = FirebaseAuth.getInstance()
    token?.let {
        auth.signInWithCustomToken(it)
                .addOnCompleteListener(mContext) { task ->
                    //     if (task.isSuccessful) {
                    authentication.GetAuthResult(task.isSuccessful)
//                    } else {
//                    }
                }
    }
}



fun getUserLiveData(): MutableLiveData<UserItem?> {
    if (userData.value == null) {
        val user: UserItem? = PreferencesManager.getObject(AppConstants.KEY_USER, UserItem::class.java)
        userData.value = user;
    }
    return userData
}

fun getUserItem(): UserItem? = getUserLiveData().value

fun setUserItem(userItem: UserItem?) {
    PreferencesManager.putObject(AppConstants.KEY_USER, userItem)

    if (userItem != null && userData != null)
        userData.value = userItem
}

fun postUserItem(userItem: UserItem?) {
    PreferencesManager.putObject(AppConstants.KEY_USER, userItem)
    if (userItem != null && userData != null)
        userData.postValue(userItem)
}

fun setTripItem(tripItem: TripItem?) {
    prefTripItem = tripItem
    if (tripItem == null) {
        PreferencesManager.putString(AppConstants.KEY_DISTANCE, "0")
        PreferencesManager.putString("last_lat", "")
        PreferencesManager.putString("last_long", "")
    }
    PreferencesManager.putObject("pref_trip_item", prefTripItem)
}

fun getTripItem(): TripItem? {
    if (prefTripItem == null) {
        prefTripItem = PreferencesManager.getObject("pref_trip_item", TripItem::class.java)
    }
    return prefTripItem
}

inline fun <reified T : Activity> Context?.startActivity() = this?.startActivity(Intent(this, T::class.java))

inline fun <reified T : Service> Context?.startService() = this?.startActivity(Intent(this, T::class.java))

inline fun <reified T : Service> Context?.startService(action: String) = this?.startService(Intent(this, T::class.java).apply { setAction(action) })

fun Fragment.initVerticalRecycler(recyclerView: RecyclerView? = activity?.findViewById<RecyclerView>(R.id.recyclerview), divider: Boolean = true, isVertical: Boolean = true): LinearLayoutManager {
    lateinit var linearLayoutManager: LinearLayoutManager
    if (isVertical)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    else linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    recyclerView?.setLayoutManager(linearLayoutManager)
    recyclerView?.setItemAnimator(DefaultItemAnimator())
    if (divider)
        recyclerView?.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    return linearLayoutManager
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun String.isBelowMinLength(): Boolean {
    return length < AppConstants.FIELD_MINIMUM_LENGTH
}

fun String.isValidEmail(): Boolean {
    return StaticMethods.isValidEmail(this)
}

fun String.getRequestBody(): RequestBody? {
    try {
        if (TextUtils.isEmpty(this))
            return null
        try {
            return RequestBody.create(MediaType.parse("multipart/form-data"), this)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        return null
    }

}

fun FragmentActivity.showNumberPickerDialog(iface: NumberSelected, minValue: Int = 1900): Dialog {
    val cal = Calendar.getInstance()
    val li = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val dialogView = li.inflate(R.layout.dialog_yearpicker, null, false)
    val numberPicker = dialogView.findViewById<NumberPicker>(R.id.number_picker)
    numberPicker.setMinValue(minValue)
    val builder = AlertDialog.Builder(this, R.style.ListDialog)
    builder.setView(dialogView).setCancelable(true).setNegativeButton("Cancel", null).setPositiveButton("Done", { dialogInterface, i -> iface.numberSelected(numberPicker.getValue()) })

    return builder.show()
}

fun FragmentActivity.showRemoveImageDialog(iface: WorkCompletedInterface): Dialog {

    val builder = AlertDialog.Builder(this, R.style.ListDialog)
            .setTitle("Remove Picture").setMessage("Are you sure you want to remove picture?").setCancelable(true).setNegativeButton("Cancel", null).setPositiveButton("Done", { dialogInterface, i -> iface.onCompleted() })

    return builder.show()
}


fun File.getRequestBody(paramName: String): MultipartBody.Part? {
    var paramName = paramName
    try {
        if (this == null)
            return null
        if (paramName == null)
            paramName = "image"
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), this)
        return MultipartBody.Part.createFormData(paramName, this.name, requestFile)
    } catch (e: Throwable) {
        return null
    }
}

fun Map<String, String>.convertStringMapToRequest(): TreeMap<String, RequestBody> {
    var requestMap = TreeMap<String, RequestBody>()
    forEach { (key, value) ->
        try {
            requestMap.put(key, value.getRequestBody()!!)
        } catch (e: Exception) {
        }
    }
    return requestMap
}

fun RecyclerView.initVertical(context: Context, divider: Boolean = false): LinearLayoutManager? {
    val mlayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    itemAnimator = DefaultItemAnimator()
    layoutManager = mlayoutManager
    if (divider)
        addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    return mlayoutManager
}


fun RecyclerView.initHorizontal(context: Context, divider: Boolean = false) {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    itemAnimator = DefaultItemAnimator()
    if (divider)
        addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL))
}

fun ArrayList<LocationItem>.getLatLngList(): ArrayList<LatLng> {
    var list = ArrayList<LatLng>()
    forEach { locationItem: LocationItem ->
        try {
            list.add(LatLng(locationItem.latitude.toDouble(), locationItem.longitude.toDouble()))
        } catch (e: Exception) {
        }
    }
    return list
}

fun Route.getAllPoints(): ArrayList<LatLng> {
    var list = ArrayList<LatLng>()
    legList.forEach { leg: Leg? ->
        list.addAll(leg!!.directionPoint)
    }
    return list
}

fun AppCompatActivity.createContactIntent(phone: String?) {
    if (phone == null)
        return
    val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null));
    val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
    AlertDialog.Builder(this)
            .setMessage("Please choose the method of contact")
            .setPositiveButton("SMS", { dialog, which -> startActivity(smsIntent) })
            .setNegativeButton("Call", { dialog, which -> startActivity(callIntent) })
            .show()
}

fun AppCompatActivity.createCallIntent(phone: String?) {
    if (phone == null)
        return
    try {
        val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(callIntent)
    } catch (e: Exception) {
    }
}

fun LocationItem.getAsLatLng() = LatLng(latitude.toDouble(), longitude.toDouble())

fun String.asDouble(): Double {
    if (isNullOrEmpty())
        return 0.0
    else
        return toDouble()
}

fun showDatePopup(context: Context, iFace: DateTimeSelectedInterface? = null, allowPastDates: Boolean = false, textView: TextView? = null) {

    val c = Calendar.getInstance()
    val mYear = c.get(Calendar.YEAR)
    val mMonth = c.get(Calendar.MONTH)
    val mDay = c.get(Calendar.DAY_OF_MONTH)

    // Launch Date Picker Dialog
    val dpd = DatePickerDialog(context,
            { view, year, monthOfYear, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, monthOfYear, dayOfMonth)
                textView?.text = DateTimeHelper.getFormattedDate(cal.time)
                iFace?.onDateTimeSelected(cal)
            }, mYear, mMonth, mDay)

    if (!allowPastDates)
        dpd.datePicker.minDate = c.timeInMillis

    dpd.show()
}


fun convertTimeToDate(time: Long): Date {
    var stamp = Timestamp(time)
    return Date(stamp.getTime())
}

fun showTimePopup(context: Context, iFace: DateTimeSelectedInterface? = null, allowPastTimes: Boolean = false, textView: TextView? = null) {

    val c = Calendar.getInstance()
    val Hour = c.get(Calendar.HOUR_OF_DAY)
    val min = c.get(Calendar.MINUTE)

    val tpd = TimePickerDialog(context,
            { timePicker, hour, mins ->
                val cal = Calendar.getInstance()
                if (!allowPastTimes)
                    cal.timeInMillis = Calendar.getInstance().timeInMillis
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, mins)
                textView?.setText(DateTimeHelper.getFormattedDate(cal.time, "hh:mm"))
                iFace?.onDateTimeSelected(cal)
            }, Hour, min, true)
    tpd.show()
}

fun <T> String.fromJson(type: Class<T>): T? {
    return Gson().fromJson(this, type)
}

fun <T> Any.toJson(): String {
    return Gson().toJson(this)
}

fun LatLngBounds.getCameraUpdate(): CameraUpdate = CameraUpdateFactory.newLatLngBounds(reduceBounds(), 0)

fun CameraUpdateFactory.newLatLngBoundsReduced(bounds: LatLngBounds): CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds.reduceBounds(), 0)

fun LatLngBounds.reduceBounds(): LatLngBounds {

    val builder = LatLngBounds.Builder()
    builder.include(northeast)
    builder.include(southwest)

    val loc1 = Location("1")
    loc1.latitude = northeast.latitude
    loc1.longitude = northeast.longitude
    val loc2 = Location("2")
    loc2.latitude = southwest.latitude
    loc2.longitude = southwest.longitude

    var extra_distance = loc1.distanceTo(loc2)

    Log.e("distance", (extra_distance / 1000).toString())

    val northEast = move(northeast, extra_distance.toDouble(), extra_distance.toDouble())
    val southWest = move(southwest, extra_distance.toDouble().times(-1), -extra_distance.toDouble().times(-1))
    builder.include(northEast)
    builder.include(southWest)

    return toBounds(center, (extra_distance + 1000).toDouble())
//    return builder.build()
}

fun toBounds(center: LatLng, radiusInMeters: Double): LatLngBounds {
    val distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0)
    val southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0)
    val northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0)
    return LatLngBounds(southwestCorner, northeastCorner)
}

private val EARTHRADIUS = 6366198

private fun move(startLL: LatLng, toNorth: Double, toEast: Double): LatLng {
    val lonDiff = meterToLongitude(toEast, startLL.latitude)
    val latDiff = meterToLatitude(toNorth)
    return LatLng(startLL.latitude + latDiff, startLL.longitude + lonDiff)
}

private fun meterToLongitude(meterToEast: Double, latitude: Double): Double {
    val latArc = Math.toRadians(latitude)
    val radius = Math.cos(latArc) * EARTHRADIUS
    val rad = meterToEast / radius
    return Math.toDegrees(rad)
}

private fun meterToLatitude(meterToNorth: Double): Double {
    val rad = meterToNorth / EARTHRADIUS
    return Math.toDegrees(rad)
}

fun View.toggleVisibility() {
    visibility = if (isShown) android.view.View.GONE else android.view.View.VISIBLE
}

