package com.rsmnm.ViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.rsmnm.Models.*
import com.rsmnm.Networking.WebResponse
import com.rsmnm.Networking.WebServiceFactory
import com.rsmnm.Utils.AppStore
import com.rsmnm.Utils.EncryptionHelper
import com.rsmnm.Utils.convertStringMapToRequest
import com.rsmnm.Utils.getUserItem
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PassengerViewModel(app: Application) : AndroidViewModel(app) {

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

    fun getVehicleTypes(state: String, city: String): LiveData<Resource<WebResponse<ArrayList<VehicleTypeItem>>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<ArrayList<VehicleTypeItem>>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("state", state)
        map.put("city", city)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

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


    fun requestRide(selectedVehicle: VehicleTypeItem, tripItem: TripItem): LiveData<Resource<WebResponse<TripItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<TripItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())

        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("pickup_title", tripItem.pickup_title)
        map.put("pickup_latitude", tripItem.pickup_latitude)
        map.put("pickup_longitude", tripItem.pickup_longitude)
        map.put("car_type_id", selectedVehicle.id)
        map.put("city", tripItem.pickup_city)
        map.put("state", tripItem.pickup_state)
        map.put("dropoffs", Gson().toJson(tripItem.dropoffs))
        map.put("is_blind", "".plus(tripItem.is_blind))

        if (tripItem.scheduled_at != null)
            map.put("scheduled_at", tripItem.scheduled_at.toString())
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))


        WebServiceFactory.getInstance().requestRide(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<TripItem>> {
            override fun onResponse(call: Call<WebResponse<TripItem>>, response: Response<WebResponse<TripItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.error_code.equals("card_not_found"))
                        liveData.postValue(Resource.response(Resource.Status.action_card_not_added, response.body()!!))
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

    fun cancelTrip(trip_id: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", trip_id)
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


    fun completePayment(ride_id: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", ride_id)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().completePayment(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
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

    fun deleteAccount(): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().accountDelete(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
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

    fun ratePassenger(rideId: String, rating: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("ride_id", rideId)
        map.put("rating", rating)
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

    fun updateProfile(map: TreeMap<String, String>, profile_pic: MultipartBody.Part?): LiveData<Resource<WebResponse<UserItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<UserItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())

        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().updateProfile(EncryptionHelper.calculateHmac(map), map.convertStringMapToRequest(), profile_pic).enqueue(object : Callback<WebResponse<UserItem>> {
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

    val cardLiveData: MediatorLiveData<Resource<WebResponse<ArrayList<CardItem>>>> = MediatorLiveData()

    fun getCard(): LiveData<Resource<WebResponse<ArrayList<CardItem>>>> {

        if (cardLiveData.value == null)
            cardLiveData.postValue(Resource.loading())


        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().getCard(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<ArrayList<CardItem>>> {
            override fun onResponse(call: Call<WebResponse<ArrayList<CardItem>>>, response: Response<WebResponse<ArrayList<CardItem>>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        cardLiveData.value = Resource.response(Resource.Status.success, response.body()!!)
                    else if (response.body()!!.isExpired)
                        AppStore.getInstance().sessionExpireObservable.postValue(true)
                    else
                        cardLiveData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    cardLiveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<ArrayList<CardItem>>>, t: Throwable) {
                cardLiveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return cardLiveData
    }

    fun addCard(card: CardItem): LiveData<Resource<WebResponse<CardItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<CardItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("card_token", card.card_token)
        map.put("last_digits", card.last_digits)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().addCard(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<CardItem>> {
            override fun onResponse(call: Call<WebResponse<CardItem>>, response: Response<WebResponse<CardItem>>) {
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

            override fun onFailure(call: Call<WebResponse<CardItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun removeCard(card: CardItem): LiveData<Resource<WebResponse<CardItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<CardItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("card_id", card.id)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().removeCard(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<CardItem>> {
            override fun onResponse(call: Call<WebResponse<CardItem>>, response: Response<WebResponse<CardItem>>) {
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

            override fun onFailure(call: Call<WebResponse<CardItem>>, t: Throwable) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return liveData
    }

    fun passengerTripListing(page: Int = 0, path: String = "past"): LiveData<Resource<WebResponse<ArrayList<TripListingItem>>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<ArrayList<TripListingItem>>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", getUserItem()!!.token)
        map.put("page", page.toString())
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))



        WebServiceFactory.getInstance().passengerTripListing(path, EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<ArrayList<TripListingItem>>> {
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
}