package com.rsmnm.ViewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.Networking.WebResponse
import com.rsmnm.Networking.WebServiceFactory
import com.rsmnm.Utils.AppStore
import com.rsmnm.Utils.EncryptionHelper
import com.rsmnm.Utils.getUserItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AccountsViewModel(app: Application) : AndroidViewModel(app) {


    fun getUserProfile(token: String): LiveData<Resource<WebResponse<UserItem>>> {
        var liveData: MediatorLiveData<Resource<WebResponse<UserItem>>> = MediatorLiveData()
        liveData.postValue(Resource.loading())
        var map: TreeMap<String, String> = TreeMap()
        map.put("_token", token)
        map.put("timestamp", System.currentTimeMillis().toString().substring(0, 10))

        WebServiceFactory.getInstance().getUserProfile(EncryptionHelper.calculateHmac(map), map).enqueue(object : Callback<WebResponse<UserItem>> {
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
