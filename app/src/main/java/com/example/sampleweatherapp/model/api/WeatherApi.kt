package com.example.sampleweatherapp.model.api

import com.example.sampleweatherapp.BuildConfig
import com.example.sampleweatherapp.model.api.models.WeatherData
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/onecall?")
    fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = BuildConfig.WEATHER_API_KEY,
        @Query("lang") lang: String = "ru",
        @Query("units") units: String = "standard",
    ): Observable<WeatherData>
}