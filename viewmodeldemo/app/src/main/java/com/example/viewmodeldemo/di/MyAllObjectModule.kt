package com.example.viewmodeldemo.di

import android.app.Application
import android.content.Context
import com.example.viewmodeldemo.network.ApiInterface
import com.example.viewmodeldemo.preferense.Preferences
import com.example.viewmodeldemo.utils.BaseUrlPath
import com.example.viewmodeldemo.utils.Method
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MyAllObjectModule {

    @Provides
    @Singleton
    fun provideMethod(preferences: Preferences): Method {
        return Method(preferences)

    }

    @Provides
    @Singleton
    fun provideApiServices(): Retrofit {
        val loggin = HttpLoggingInterceptor()
        loggin.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(loggin)

        return Retrofit.Builder().baseUrl(BaseUrlPath.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient.build()).build()

    }

    @Provides
    @Singleton
    fun providePreferences(context: Context): Preferences {
        return Preferences(context)

    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }


}

