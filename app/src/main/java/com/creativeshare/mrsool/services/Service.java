package com.creativeshare.mrsool.services;

import com.creativeshare.mrsool.models.AppDataModel;
import com.creativeshare.mrsool.models.NearDelegateDataModel;
import com.creativeshare.mrsool.models.NearbyStoreDataModel;
import com.creativeshare.mrsool.models.PlaceDetailsModel;
import com.creativeshare.mrsool.models.PlaceGeocodeData;
import com.creativeshare.mrsool.models.PlaceMapDetailsData;
import com.creativeshare.mrsool.models.SearchDataModel;
import com.creativeshare.mrsool.models.SliderModel;
import com.creativeshare.mrsool.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/nearbysearch/json")
    Call<NearbyStoreDataModel> getNearbyStores(@Query(value = "location") String location,
                                               @Query(value = "radius") int radius,
                                               @Query(value = "type") String type,
                                               @Query(value = "key") String key
    );

    @GET("place/findplacefromtext/json")
    Call<SearchDataModel> getNearbyStoresWithKeyword(@Query(value = "locationbias") String location,
                                                     @Query(value = "inputtype") String inputtype,
                                                     @Query(value = "input") String input,
                                                     @Query(value = "fields") String fields,
                                                     @Query(value = "language") String language,
                                                     @Query(value = "key") String key
    );

    @GET("place/details/json")
    Call<PlaceDetailsModel> getPlaceDetails(@Query(value = "placeid") String placeid,
                                            @Query(value = "fields") String fields,
                                            @Query(value = "language") String language,
                                            @Query(value = "key") String key
    );


    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);

    @FormUrlEncoded
    @POST("/Api/signup")
    Call<UserModel> signUpWithoutImage(@Field("user_email") String user_email,
                                       @Field("user_phone") String user_phone,
                                       @Field("user_full_name") String user_full_name,
                                       @Field("user_gender") String user_gender,
                                       @Field("user_country") String user_country,
                                       @Field("user_age") long user_age

    );


    @Multipart
    @POST("/Api/signup")
    Call<UserModel> signUpWithImage(@Part("user_email") RequestBody user_email,
                                    @Part("user_phone") RequestBody user_phone,
                                    @Part("user_full_name") RequestBody user_full_name,
                                    @Part("user_gender") RequestBody user_gender,
                                    @Part("user_country") RequestBody user_country,
                                    @Part("user_age") RequestBody user_age,
                                    @Part  MultipartBody.Part image
                                    );


    @FormUrlEncoded
    @POST("/Api/login")
    Call<UserModel> signIn(@Field("user_phone") String user_phone);

    @GET("/Api/appDetails")
    Call<AppDataModel> getAppData(@Query("type") int  type);

    @FormUrlEncoded
    @POST("/Api/updateLocation")
    Call<ResponseBody> updateLocation(@Field("user_id") String user_id,
                                      @Field("user_google_lat") double user_google_lat,
                                      @Field("user_google_long") double user_google_long
                                      );

    @FormUrlEncoded
    @POST("/Api/updateToken")
    Call<ResponseBody> updateToken(@Field("user_id") String user_id,
                                      @Field("user_token_id") String user_token_id

    );

    @GET("/Api/slider")
    Call<SliderModel> getAds();

    @GET("/Api/driverList")
    Call<NearDelegateDataModel> getDelegate(@Query("mylat") double lat,@Query("mylong") double lng,@Query("page") int page_index);

    @FormUrlEncoded
    @POST("/Api/logout")
    Call<ResponseBody> logOut(@Field("user_id") String user_id);
}

