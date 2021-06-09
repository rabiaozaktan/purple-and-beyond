package com.purple.social.api.retrofit;

import com.purple.social.api.models.ApiResponse;
import com.purple.social.model.News;
import com.purple.social.model.Weather;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by zigo on 09.05.2020.
 */

public interface Api {

    @GET("news/getNews")
    Call<ApiResponse<List<News>>> getNews(@Header("content-type") String contentType, @Header("authorization") String apikey, @Query("country") String country, @Query("tag") String tag);

    @GET("weather/getWeather")
    Call<ApiResponse<List<Weather>>> getWeather(@Header("content-type") String contentType, @Header("authorization") String apikey, @Query("data.lang") String lang, @Query("data.city") String city);

//    @POST("register")//https://api.collectapi.com/news/getNews?country=tr&tag=general
//    Call<ApiResponse<Void>> register(@Body Map<String, Object> map);
//
//    @POST("forgotPassword")
//    Call<ApiResponse<Void>> forgotPassword(@Body Map<String, Object> map);
//
//    /**
//     * Users işlemleri
//     */
//    @GET("api/users")
//    Call<ApiResponse<User>> getCurrentUser(@Header("x-access-token") String token);
//
//    @GET("api/users/find")
//    Call<ApiResponse<List<User>>> findUser(@Header("x-access-token") String token, @Query("q") String q, @Query("page") int page, @Query("per_page") int per_page);
//
//    @PATCH("api/users/{id}")
//    Call<ApiResponse<User>> getUserById(@Header("x-access-token") String token, @Path("id") String userId);
//
//    @PUT("api/users")
//    Call<ApiResponse<User>> updateUser(@Header("x-access-token") String token, @Body Map<String, Object> map);
//
//    @PUT("api/users/fmc-token")
//    Call<ApiResponse<User>> fmcTokenUpdate(@Header("x-access-token") String token, @Body Map<String, Object> map);
//
//    /**
//     * Follow işlemleri
//     */
//
//    @POST("api/follow/{id}")
//    Call<ApiResponse<Follow>> followUser(@Header("x-access-token") String token, @Path("id") String userId);
//
//    @PATCH("api/follow/countById/{id}")
//    Call<ApiResponse<FollowCount>> getUserFollowCountById(@Header("x-access-token") String token, @Path("id") String userId);
//
//    /**
//     * Message işlemleri
//     */
//
//    @GET("api/message")
//    Call<ApiResponse<Pagination<Message>>> getMessageList(@Header("x-access-token") String token, @Query("roomId") String roomId, @Query("page") int page);
//
//    /**
//     * Users işlemleri
//     */
//
//    @Multipart
//    @POST("api/files")
//    Call<ApiResponse<List<FileModel>>> upload(@Header("x-access-token") String token, @Part MultipartBody.Part file);
//
//    @Multipart
//    @POST("api/files")
//    Call<ApiResponse<List<FileModel>>> uploadMulti(@Header("x-access-token") String token, @Part List<MultipartBody.Part> file);
}

