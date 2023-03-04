package com.example.sampleweatherapp.model.api

import com.example.sampleweatherapp.BuildConfig
import com.example.sampleweatherapp.model.api.models.geocod.GeoCode
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingApi {
    @GET("data/2.5/weather?")
    fun detCity(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = BuildConfig.GEOCODE_API_KEY,
        @Query("lang") lang: String = "ru",
    ): Observable<GeoCode>
}