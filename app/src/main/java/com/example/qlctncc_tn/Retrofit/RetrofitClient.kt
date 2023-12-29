package com.example.qlctncc_tn.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.242.4.254:8080/api/")
//        .baseUrl("http://192.168.10.130:8080/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val firebasefcm: Retrofit = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/fcm/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
    val fcm: ApiService = firebasefcm.create(ApiService::class.java)
}