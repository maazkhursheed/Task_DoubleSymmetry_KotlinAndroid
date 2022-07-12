package com.rsmnm.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.rsmnm.Fragments.driver.UploadDocumentFragment;
import com.rsmnm.Models.Document;
import com.rsmnm.Models.Resource;
import com.rsmnm.Models.UserItem;
import com.rsmnm.Networking.WebResponse;
import com.rsmnm.Networking.WebServiceFactory;
import com.rsmnm.Utils.AppStore;
import com.rsmnm.Utils.EncryptionHelper;
import com.rsmnm.Utils.ExtensionUtilsKt;
import com.rsmnm.Utils.StaticMethods;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class DocumentViewModel extends AndroidViewModel {

    public DocumentViewModel(@NonNull Application application) {
        super(application);
    }

    public enum DocumentType {
        CAR_LICENSE, CAR_INSPECTION, CAR_INSURANCE , CAR_REGISTRATION , STUDENT_ID
    }

    public MediatorLiveData<Resource<WebResponse<UserItem>>> getAllDocuments() {
        final MediatorLiveData<Resource<WebResponse<UserItem>>> liveData = new MediatorLiveData();
        Map<String, String> map = new HashMap<>();

        map.put("_token", ExtensionUtilsKt.getUserItem().token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        WebServiceFactory.getInstance().getAllDocuments(EncryptionHelper.calculateHmac(map), map).enqueue(new Callback<WebResponse<UserItem>>() {

            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                    liveData.postValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                    AppStore.getInstance().sessionExpireObservable.postValue(true);
                    else
                    liveData.postValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                t.printStackTrace();
                liveData.postValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return liveData;
    }

    public MediatorLiveData<Resource<WebResponse<UserItem>>> uploadDocument(DocumentViewModel.DocumentType documentType, File documentToUpload) {
        final MediatorLiveData<Resource<WebResponse<UserItem>>> liveData = new MediatorLiveData();

        Map<String, String> map = new HashMap<>();
        map.put("_token", ExtensionUtilsKt.getUserItem().token);
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        String param = "";
        switch (documentType) {

            case CAR_INSPECTION:
                param = "inspection_pic";
                break;

            case CAR_INSURANCE:
                param = "insurance_pic";
                break;

            case CAR_LICENSE:
                param = "license_pic";
                break;

            case CAR_REGISTRATION:
                param = "vehicle_registration_pic";
                break;
            case STUDENT_ID:
                param = "student_id";
                break;

        }

        WebServiceFactory.getInstance().uploadDocument(
                EncryptionHelper.calculateHmac(map),
                ExtensionUtilsKt.convertStringMapToRequest(map),
                StaticMethods.getMultiPartBody(param, documentToUpload)

        ).enqueue(new Callback<WebResponse<UserItem>>() {
            @Override
            public void onResponse(Call<WebResponse<UserItem>> call, Response<WebResponse<UserItem>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess())
                        liveData.postValue(Resource.response(Resource.Status.success, response.body()));
                    else if (response.body().isExpired())
                        AppStore.getInstance().sessionExpireObservable.postValue(true);
                    else
                        liveData.postValue(Resource.response(Resource.Status.error, response.body()));
                } else
                    liveData.postValue(Resource.response(Resource.Status.connection_error, null));
            }

            @Override
            public void onFailure(Call<WebResponse<UserItem>> call, Throwable t) {
                liveData.postValue(Resource.response(Resource.Status.connection_error, null));
            }
        });
        return liveData;
    }
}