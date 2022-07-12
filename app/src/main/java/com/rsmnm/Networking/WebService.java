package com.rsmnm.Networking;


import com.google.gson.JsonObject;
import com.rsmnm.BuildConfig;
import com.rsmnm.Models.CardItem;
import com.rsmnm.Models.CityItem;
import com.rsmnm.Models.Document;
import com.rsmnm.Models.StateItem;
import com.rsmnm.Models.TripListingItem;
import com.rsmnm.Models.EarningSummaryItem;
import com.rsmnm.Models.NotifObject;
import com.rsmnm.Models.TripItem;
import com.rsmnm.Models.UserItem;
import com.rsmnm.Models.VehicleTypeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface WebService {

    @FormUrlEncoded
    @POST("authenticate")
    Call<WebResponse<JsonObject>> phoneValidation(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("phone-verification")
    Call<WebResponse> phonePinVerification(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("validate-registration")
    Call<WebResponse> validateDriverPreRegisteration(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("forgot-password")
    Call<WebResponse<JsonObject>> forgotPassword(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("resend-code")
    Call<WebResponse> resendCode(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("reset-password")
    Call<WebResponse> resetPassword(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @Multipart
    @POST("register")
    Call<WebResponse<UserItem>> register(
            @Header("signature") String signature,
            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part image);

    @Multipart
    @POST("register")
    Call<WebResponse<UserItem>> registerDriver(
            @Header("signature") String signature,
            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part profile_pic,
            @Part MultipartBody.Part license_pic,
            @Part List<MultipartBody.Part> car_pic,
            @Part MultipartBody.Part insurance_pic,
            @Part MultipartBody.Part inspection_pic,
            @Part MultipartBody.Part student_id);

    @FormUrlEncoded
    @POST("login")
    Call<WebResponse<UserItem>> login(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("fb-login")
    Call<WebResponse<UserItem>> fbLogin(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("get/current-status")
    Call<WebResponse<TripItem>> getActiveRide(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("available/car-types")
    Call<WebResponse<ArrayList<VehicleTypeItem>>> getVehicleTypes(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("request/ride")
    Call<WebResponse<TripItem>> requestRide(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update/location")
    Call<WebResponse> updateLocation(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(BuildConfig.FLAVOR + "/cancel/ride")
    Call<WebResponse> cancelRide(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update/status")
    Call<WebResponse> updateStatus(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("mark/arrive")
    Call<WebResponse<TripItem>> rideArrived(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("start/ride")
    Call<WebResponse<TripItem>> rideStarted(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("end/ride")
    Call<WebResponse<TripItem>> rideCompleted(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ride/detail")
    Call<WebResponse<TripItem>> getRideDetails(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST(BuildConfig.FLAVOR + "/rate/ride")
    Call<WebResponse> rateRide(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @Multipart
    @POST("account/update")
    Call<WebResponse<UserItem>> updateProfile(
            @Header("signature") String signature,
            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part profile_pic);

    @FormUrlEncoded
    @POST("driver/earnings/daily")
    Call<WebResponse<ArrayList<TripListingItem>>> earningsDaily(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("driver/earnings/summary")
    Call<WebResponse<EarningSummaryItem>> earningsSummary(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("account/list/notifications")
    Call<WebResponse<ArrayList<NotifObject>>> getNotifications(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("driver/save/destination")
    Call<WebResponse<JsonObject>> setPreferredLocation(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("account/me")
    Call<WebResponse<UserItem>> getProfile(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("passenger/add/credit-card")
    Call<WebResponse<CardItem>> addCard(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("passenger/get/credit-card")
    Call<WebResponse<ArrayList<CardItem>>> getCard(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("passenger/remove/credit-card")
    Call<WebResponse<CardItem>> removeCard(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("charge/ride/payment")
    Call<WebResponse> completePayment(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("passenger/{path}/trips")
    Call<WebResponse<ArrayList<TripListingItem>>> passengerTripListing(
            @Path("path") String path,
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("account/delete")
    Call<WebResponse> accountDelete(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("account/me")
    Call<WebResponse<UserItem>> getUserProfile(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("logout")
    Call<WebResponse> logout(
            @Header("signature") String signature,
            @FieldMap Map<String, String> map);

    @GET("list/states")
    Call<WebResponse<ArrayList<StateItem>>> getStates();

    @GET("list/cities/{state_id}")
    Call<WebResponse<ArrayList<CityItem>>> getCities(@Path("state_id") String state_id);

    @FormUrlEncoded
    @POST("driver/documents")
    Call<WebResponse<UserItem>> getAllDocuments(@Header("signature") String signature, @FieldMap Map<String, String> map);

    @Multipart
    @POST("driver/documents/update")
    Call<WebResponse<UserItem>> uploadDocument(
            @Header("signature") String signature,
            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part document);//@PartMap Map<String, MultipartBody.Part> documents
}