package com.example.viewmodeldemo.network

import com.example.viewmodeldemo.utils.BaseUrlPath.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiServices {

    fun getRetrofit(): Retrofit {
        val loggin = HttpLoggingInterceptor()
        loggin.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(loggin)

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient.build()).build()

    }

}