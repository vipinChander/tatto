package com.example.tattomobile.RemoteApi;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RestApiService {

    @GET("user/mobile/exist/")
    Call<JsonObject> isMobileNumberExist(@Query("mobile_number") String mobile_number);
//    @POST("user/mobile/exist/")
//    Call<ResponseBody> isMobileNumberExist(@Body RequestBody userId);

    @POST
    Call<JsonObject> isMobileNumberExist2(@Url String url);

    @POST
    Call<JsonObject> checkLoginUser(@Url String url);

    @GET("services/get/list")
    Call<JsonObject> fetchServiceData(@Header("authorization") String auth);

    @GET("banner/get/list")
    Call<JsonObject> fetchBannerData(@Header("authorization") String auth);

    @GET("products/get/list/pagination")
    Call<JsonObject> fetchPaginationData(@Header("authorization") String auth);

    @GET("gallery/get/pagination")
    Call<JsonObject> fetchTestimonialsData(@Header("authorization") String auth);
    @GET("gallery/get/list")
    Call<JsonObject> fetchourwork(@Header("authorization") String auth);

    @GET("video/get/pagination")
    Call<JsonObject> fetchourworkData(@Header("authorization") String auth);

    @GET("video/get/list")
    Call<JsonObject> fetchourworkDatalist(@Header("authorization") String auth);
    @GET
    Call<JsonObject> fetchTimesData(@Url String url,@Header("authorization") String auth);

    @GET
    Call<JsonObject> isProductsByID(@Url String url,@Header("authorization") String auth);
    @GET
    Call<JsonObject> fetchBookingData(@Url String url,@Header("authorization") String auth);

    @GET
    Call<JsonObject> fetchProfileData(@Url String url,@Header("authorization") String auth);

    @GET("notifications/get/list")
    Call<JsonObject> fetchNotificationData(@Header("authorization") String auth);
    @POST
    Call<JsonObject> sendUserFeedback(@Url String url,@Header("authorization") String auth);

    @POST
    Call<JsonObject> bookedSlot(@Url String url,@Header("authorization") String auth);
    @PUT
    Call<JsonObject>  updateBookingSlot(@Url String url,@Header("authorization") String auth,@Header("booking") String bookingid);

    @POST
    Call<JsonObject>checkotp(@Url String url);
    @POST
    Call<JsonObject>passwordUpdated(@Url String url);

    @GET
    Call<JsonObject> getUpdateData(@Url String url,@Header("authorization") String auth);
}
