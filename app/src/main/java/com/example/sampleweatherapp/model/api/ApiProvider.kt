package com.example.sampleweatherapp.model.api

import android.os.Build
import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Inject


class ApiProvider @Inject constructor() {
    private val openWeatherMap: Retrofit by lazy { initApi() }

    private fun initApi() = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .client(client)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val client = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(10))
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .writeTimeout(Duration.ofSeconds(10))
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    fun provideWeatherApi() = openWeatherMap.create(WeatherApi::class.java)
    fun provideGeoCodingApi() = openWeatherMap.create(GeoCodingApi::class.java)
}