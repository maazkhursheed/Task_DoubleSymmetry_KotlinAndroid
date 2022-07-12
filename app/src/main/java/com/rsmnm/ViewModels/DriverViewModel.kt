package com.rsmnm.ViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.google.gson.JsonObject
import com.rsmnm.BuildConfig
import com.rsmnm.Models.*
import com.rsmnm.Networking.WebResponse
import com.rsmnm.Networking.WebServiceFactory
import com.rsmnm.Utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class DriverViewModel(app: Application) : AndroidViewModel(app) {

    fun getCarType(state: String, city: String): LiveData<Resource<WebResponse<ArrayList<VehicleTypeItem>>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<ArrayList<VehicleTypeItem>>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("state", state)
        map.put("city", city)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 9))

        WebServiceFactory.getInstance().getVehicleTypes(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<ArrayList<VehicleTypeItem>>> {
            override fun onResponse(call: Call<WebResponse<ArrayList<VehicleTypeItem>>>, response: Response<WebResponse<ArrayList<VehicleTypeItem>>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<ArrayList<VehicleTypeItem>>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }


    fun updateLocation(lat: String, long: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("latitude", lat)
        map.put("longitude", long)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().updateLocation(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<Any?>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }


        })
        return liveData
    }

    fun cancelTrip(ride_id: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", ride_id)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().cancelRide(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<Any?>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

        })
        return liveData
    }


    fun updateStatus(status: Boolean): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("status", if (status) "1" else "0")
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().updateStatus(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body()!!.areValidDocuments()) {
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                    } else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<Any?>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }


    fun getActiveRide(): LiveData<Resource<WebResponse<TripItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<TripItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().getActiveRide(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<TripItem>> {
            override fun onResponse(call: Call<WebResponse<TripItem>>, response: Response<WebResponse<TripItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<TripItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun rideArrived(rideId: String): LiveData<Resource<WebResponse<TripItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<TripItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", rideId)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().rideArrived(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<TripItem>> {
            override fun onResponse(call: Call<WebResponse<TripItem>>, response: Response<WebResponse<TripItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<TripItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun rideStarted(rideId: String): LiveData<Resource<WebResponse<TripItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<TripItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", rideId)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().rideStarted(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<TripItem>> {
            override fun onResponse(call: Call<WebResponse<TripItem>>, response: Response<WebResponse<TripItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<TripItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun rideCompleted(rideId: String): LiveData<Resource<WebResponse<TripItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<TripItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", rideId)
        var distanceMeters = PreferencesManager.getString(AppConstants.KEY_DISTANCE).asDouble() / 1609.344
        map.put("travelled", distanceMeters.toString())
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))
        map.put("toll_charges", PreferencesManager.getBoolean(AppConstants.IS_TOLL_REQUIRED).toString())

        WebServiceFactory.getInstance().rideCompleted(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<TripItem>> {
            override fun onResponse(call: Call<WebResponse<TripItem>>, response: Response<WebResponse<TripItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<TripItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun getRideDetails(rideId: String): LiveData<Resource<WebResponse<TripItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<TripItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", rideId)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().getRideDetails(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<TripItem>> {
            override fun onResponse(call: Call<WebResponse<TripItem>>, response: Response<WebResponse<TripItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<TripItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun ratePassenger(rideId: String, rating: String, feedback: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", rideId)
        map.put("rating", rating)
        map.put("feedback", feedback)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().rateRide(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<Any?>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }


    fun earningsDaily(page: Int = 0): LiveData<Resource<WebResponse<ArrayList<TripListingItem>>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<ArrayList<TripListingItem>>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("page", page.toString())
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().earningsDaily(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<ArrayList<TripListingItem>>> {
            override fun onResponse(call: Call<WebResponse<ArrayList<TripListingItem>>>, response: Response<WebResponse<ArrayList<TripListingItem>>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<ArrayList<TripListingItem>>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun earningSummary(): LiveData<Resource<WebResponse<EarningSummaryItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<EarningSummaryItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("year", Calendar.getInstance().get(Calendar.YEAR).toString())
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().earningsSummary(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<EarningSummaryItem>> {
            override fun onResponse(call: Call<WebResponse<EarningSummaryItem>>, response: Response<WebResponse<EarningSummaryItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<EarningSummaryItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }


    fun getNotifications(page: Int = 0): LiveData<Resource<WebResponse<ArrayList<NotifObject>>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<ArrayList<NotifObject>>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("page", page.toString())
        map.put("user_type", if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) "normal" else "driver")
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().getNotifications(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<ArrayList<NotifObject>>> {
            override fun onResponse(call: Call<WebResponse<ArrayList<NotifObject>>>, response: Response<WebResponse<ArrayList<NotifObject>>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<ArrayList<NotifObject>>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }


    fun setPreferredLocation(place: LocationItem): LiveData<Resource<WebResponse<JsonObject>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<JsonObject>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("title", place.name.toString())
        map.put("lat", place.latitude)
        map.put("long", place.longitude)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().setPreferredLocation(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<JsonObject>> {
            override fun onResponse(call: Call<WebResponse<JsonObject>>, response: Response<WebResponse<JsonObject>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<JsonObject>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }


    fun getProfile(): LiveData<Resource<WebResponse<UserItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<UserItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().getProfile(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<UserItem>> {
            override fun onResponse(call: Call<WebResponse<UserItem>>, response: Response<WebResponse<UserItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<UserItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }


    fun logout(): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().logout(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<Any?>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

        })
        return liveData
    }


}