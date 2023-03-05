package com.example.sampleweatherapp.model.api

import com.example.sampleweatherapp.BuildConfig
import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

private const val QUANTITY_LIMIT = "10"

interface GeoCodingApi {
    @GET("geo/1.0/direct?")
    fun detCityByName(
        @Query("q") name: String,
        @Query("limit") limit: String = QUANTITY_LIMIT,
        @Query("appid") appid: String = BuildConfig.WEATHER_API_KEY,
    ): Observable<List<GeoCodeItem>>

    @GET("geo/1.0/reverse?")
    fun detCity(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("limit") limit: String = QUANTITY_LIMIT,
        @Query("appid") appid: String = BuildConfig.WEATHER_API_KEY,
    ): Observable<List<GeoCodeItem>>
}