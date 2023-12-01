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

    @GET("partners/{partnerID}")
    fun getPartnerByID(@Path("partnerID") partnerID: Int): Call<Partner>

    @GET("users/{managerID}")
    fun getUserDetailByID(@Path("managerID") managerID: Int): Call<UserDetail>

    @GET("tasks")
    fun getListTaskbyBusinessTripID(@Query("businessTripID") businessTripID: Int): Call<List<Task>>
    @PUT("tasks/{taskId}")
    fun putTask(@Path("taskId") taskId: Int,
                @Body updatedTask: Task,
                @Header("Authorization") token:String): Call<Task>
}