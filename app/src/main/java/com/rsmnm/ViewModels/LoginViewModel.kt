package com.rsmnm.ViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.support.v4.app.FragmentActivity
import com.google.gson.JsonObject
import com.rsmnm.BaseClasses.FragmentHandlingActivity
import com.rsmnm.BuildConfig
import com.rsmnm.Models.*
import com.rsmnm.Networking.WebResponse
import com.rsmnm.Networking.WebServiceFactory
import com.rsmnm.Utils.*
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LoginViewModel(app: Application) : AndroidViewModel(app) {


    fun isPhoneValid(context: Context?, countryCodeStr: String, phoneStr: String): Boolean {
        var number: Phonenumber.PhoneNumber
        try {
            number = Phonenumber.PhoneNumber()
            number.countryCode = countryCodeStr.toInt()
            number.nationalNumber = phoneStr.replace("[^0-9]", "").toLong()
            return PhoneNumberUtil.createInstance(context).isValidNumber(number)
        } catch (e: Exception) {
            e.printStackTrace()
            return false;
        }

    }

    fun phoneValidation(phoneStr: String): LiveData<Resource<WebResponse<JsonObject>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<JsonObject>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("phone", phoneStr)
        map.put("user_type", if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) "normal" else "driver")
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().phoneValidation(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<JsonObject>> {
            override fun onResponse(call: Call<WebResponse<JsonObject>>, response: Response<WebResponse<JsonObject>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.error_code.equals("action_signup"))
                        liveData.postValue(Resource.response(Resource.Status.action_signup, response.body()!!))
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

    fun phonePinVerification(phoneStr: String, code: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("phone", phoneStr)
        map.put("code", code)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().phonePinVerification(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
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

    fun validateDriverPreRegisteration(state: String, city: String, invitercode: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("state", state)
        map.put("city", city)
        map.put("user_type", UserItem.UserType.driver.name)
        map.put("inviter_code", invitercode)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().validateDriverPreRegisteration(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
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


    fun resendCode(phoneStr: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("phone", phoneStr)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().resendCode(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
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

    fun forgotPassword(phoneStr: String): LiveData<Resource<WebResponse<JsonObject>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<JsonObject>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("phone", phoneStr)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().forgotPassword(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<JsonObject>> {
            override fun onResponse(call: Call<WebResponse<JsonObject>>, response: Response<WebResponse<JsonObject>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
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


    fun resetPassword(phoneStr: String, password: String, code: String): LiveData<Resource<WebResponse<Any?>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<Any?>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("phone", phoneStr)
        map.put("password", password)
        map.put("code", code)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().resetPassword(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<Any?>> {
            override fun onResponse(call: Call<WebResponse<Any?>>, response: Response<WebResponse<Any?>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
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


    fun register(map: TreeMap<String, String>, imageFile: File?): LiveData<Resource<WebResponse<UserItem>>> {

        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        var requestMap = map.convertStringMapToRequest()

        var liveData: MediatorLiveData<Resource<WebResponse<UserItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        WebServiceFactory.getInstance().register(EncryptionHelper.calculateHmac(map), requestMap, imageFile?.getRequestBody("profile_picture")).enqueue(object : Callback<WebResponse<UserItem>> {
            override fun onResponse(call: Call<WebResponse<UserItem>>, response: Response<WebResponse<UserItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
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

    fun registerDriver(map: TreeMap<String, String>, imageFile: File?, license: File?, insurance: File?, registration: File?, studentID: File?, car_pics: ArrayList<File>): LiveData<Resource<WebResponse<UserItem>>> {

        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        var requestMap = map.convertStringMapToRequest()
        var liveData: MediatorLiveData<Resource<WebResponse<UserItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())

        WebServiceFactory.getInstance().registerDriver(
                EncryptionHelper.calculateHmac(map),
                requestMap,
                imageFile?.getRequestBody("profile_picture"),
                StaticMethods.getMultiPartBody("license_pic", license),
                StaticMethods.getMultiPartBodyList("car_pic", car_pics),
                StaticMethods.getMultiPartBody("insurance_pic", insurance),
                StaticMethods.getMultiPartBody("registration_pic", registration),
                StaticMethods.getMultiPartBody("student_id", studentID)
        ).enqueue(object : Callback<WebResponse<UserItem>> {
            override fun onResponse(call: Call<WebResponse<UserItem>>, response: Response<WebResponse<UserItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
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


    fun login(phone: String, pass: String): LiveData<Resource<WebResponse<UserItem>>> {

        var map = TreeMap<String, String>()
        map.put("phone", phone)
        map.put("password", pass)
        map.put("device_type", "android")
        map.put("user_type", if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) "normal" else "driver")
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        var liveData: MediatorLiveData<Resource<WebResponse<UserItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        WebServiceFactory.getInstance().login(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<UserItem>> {
            override fun onResponse(call: Call<WebResponse<UserItem>>, response: Response<WebResponse<UserItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
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


    fun fbLogin(fbToken: String): LiveData<Resource<WebResponse<UserItem>>> {

        var map = TreeMap<String, String>()
        map.put("facebook_token", fbToken)
        map.put("device_type", "android")
        map.put("user_type", if (BuildConfig.FLAVOR.equals(AppConstants.FLAVOUR_PASSENGER)) "normal" else "driver")
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        var liveData: MediatorLiveData<Resource<WebResponse<UserItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        WebServiceFactory.getInstance().fbLogin(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<UserItem>> {
            override fun onResponse(call: Call<WebResponse<UserItem>>, response: Response<WebResponse<UserItem>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else if (response.body()!!.error_code.equals("action_signup"))
                        liveData.postValue(Resource.response(Resource.Status.action_signup, response.body()!!))
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

    var stateData: MediatorLiveData<Resource<WebResponse<ArrayList<StateItem>>>> = MediatorLiveData()

    fun getStates(): MediatorLiveData<Resource<WebResponse<ArrayList<StateItem>>>> {
        WebServiceFactory.getInstance().getStates().enqueue(object : Callback<WebResponse<ArrayList<StateItem>>> {
            override fun onResponse(call: Call<WebResponse<ArrayList<StateItem>>>, response: Response<WebResponse<ArrayList<StateItem>>>) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()!!.isSuccess())
                        stateData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                    else
                        stateData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                } else
                    stateData.postValue(Resource.response(Resource.Status.connection_error, null))
            }

            override fun onFailure(call: Call<WebResponse<ArrayList<StateItem>>>, t: Throwable) {
                stateData.postValue(Resource.response(Resource.Status.connection_error, null))
            }
        })
        return stateData
    }


    fun pickState(context: FragmentHandlingActivity): LiveData<Resource<StateItem>> {

        val data = MutableLiveData<Resource<StateItem>>()
        if (stateData.value != null && stateData.value?.status == Resource.Status.success) {
            DialogHelper.showLocationPickerDialog(context, stateData.value?.data?.body) { item: BaseLocationItem? -> data.postValue(Resource.response(Resource.Status.success, item as StateItem)) }
        } else {
            getStates().observe(context, android.arch.lifecycle.Observer { response ->
                when (response?.status) {
                    Resource.Status.success -> DialogHelper.showLocationPickerDialog(context, stateData.value?.data?.body) { item: BaseLocationItem? -> data.postValue(Resource.response(Resource.Status.success, item as StateItem)) }
                    else -> data.postValue(Resource.response(Resource.Status.connection_error, null))
                }
            })
            data.postValue(Resource.loading())
        }
        return data
    }

    var citiesMap = HashMap<String, ArrayList<CityItem>>()

    fun getCities(stateId: String): MediatorLiveData<Resource<WebResponse<ArrayList<CityItem>>>> {

        var cityData: MediatorLiveData<Resource<WebResponse<ArrayList<CityItem>>>> = MediatorLiveData()
        if (!citiesMap.containsKey(stateId))
            WebServiceFactory.getInstance().getCities(stateId).enqueue(object : Callback<WebResponse<ArrayList<CityItem>>> {
                override fun onResponse(call: Call<WebResponse<ArrayList<CityItem>>>, response: Response<WebResponse<ArrayList<CityItem>>>) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body()!!.isSuccess()) {
                            cityData.postValue(Resource.response(Resource.Status.success, response.body()!!))
                            citiesMap.put(stateId, response.body()?.body!!)
                        } else
                            cityData.postValue(Resource.response(Resource.Status.error, response.body()!!))
                    } else
                        cityData.postValue(Resource.response(Resource.Status.connection_error, null))
                }

                override fun onFailure(call: Call<WebResponse<ArrayList<CityItem>>>, t: Throwable) {
                    cityData.postValue(Resource.response(Resource.Status.connection_error, null))
                }
            })
        return cityData
    }

    fun pickCity(context: FragmentHandlingActivity, stateId: String): LiveData<Resource<CityItem>> {
        val data = MutableLiveData<Resource<CityItem>>()

        if (citiesMap.containsKey(stateId))
            DialogHelper.showLocationPickerDialog(context, citiesMap[stateId]) { item: BaseLocationItem? -> data.postValue(Resource.response(Resource.Status.success, item as CityItem)) }
        else {
            getCities(stateId).observe(context, android.arch.lifecycle.Observer { response ->
                when (response?.status) {
                    Resource.Status.success -> DialogHelper.showLocationPickerDialog(context, response.data?.body) { item: BaseLocationItem? -> data.postValue(Resource.response(Resource.Status.success, item as CityItem)) }
                    else -> data.postValue(Resource.response(Resource.Status.connection_error, null))
                }
            })
            data.postValue(Resource.loading())
        }
        return data
    }
}