package com.example.qlctncc_tn.Retrofit

import com.example.qlctncc_tn.Model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("auth/signin")
    fun postLogin(@Body user: UserLogin): Call<User>

    @POST("auth/signup")
    fun postSignUp(@Body user: UserSignup): Call<User>

    @POST("auth/changePassByEmail")
    fun postChangePassByEmail(@Body password: ChangePassword?): Call<ResponseBody?>?

    @GET("businessTrips")
    fun getListBusinessTripbyUserID(@Query("userID") userID: Int): Call<List<BusinessTrip>>

    @GET("businessTrips")
    fun getListBusinessAll(): Call<List<BusinessTrip>>

    @GET("partners/{partnerID}")
    fun getPartnerByID(@Path("partnerID") partnerID: Int): Call<Partner>

    @GET("users/{managerID}")
    fun getUserDetailByID(@Path("managerID") managerID: Int): Call<UserDetail>

    @GET("tasks")
    fun getListTaskbyBusinessTripID(@Query("businessTripID") businessTripID: Int): Call<List<Task>>

    @PUT("tasks/{taskId}")
    fun putTask(
        @Path("taskId") taskId: Int,
        @Body updatedTask: Task,
        @Header("Authorization") token: String
    ): Call<Task>

    @GET("rates")
    fun getListRatebyBusinessTripID(@Query("businessTripID") businessTripID: Int): Call<List<Rate>>

    @GET("rates")
    fun getListRatebyTaskID(@Query("taskID") taskID: Int): Call<List<Rate>>

    @POST("rates")
    fun postRate(@Body rate: Rate, @Header("Authorization") token: String): Call<Rate>

    @PUT("rates/{rateID}")
    fun putRate(
        @Path("rateID") rateID: Int,
        @Body updatedRate: Rate,
        @Header("Authorization") token: String
    ): Call<Rate>

    @GET("reports")
    fun getListReportbyBusinessTripID(@Query("businessTripID") businessTripID: Int): Call<List<Report>>

    @GET("reports")
    fun getListReportbyTaskID(@Query("taskID") taskID: Int): Call<List<Report>>

    @GET("users")
    fun getAllUSer(): Call<List<UserDetail>>

    @PUT("users/{userId}")
    fun putUserDetail(
        @Path("userId") userId: Int,
        @Body userDetail: UserDetail,
        @Header("Authorization") token: String
    ): Call<UserDetail>

    @POST("reports")
    fun postReport(@Body report: Report, @Header("Authorization") token: String): Call<Report>

    @POST("images")
    fun postImage(@Body image: Image, @Header("Authorization") token: String): Call<Image>

    @POST("send")
    fun senNotification(@Body notificationFCM: NotificationFCM, @Header("Authorization") token: String
                        , @Header("Content-Type") contextType: String): Call<NotificationFCM>
}