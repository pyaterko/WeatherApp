package com.example.sampleweatherapp.model.repositories

import android.annotation.SuppressLint
import android.util.Log
import com.example.sampleweatherapp.model.api.ApiProvider
import com.example.sampleweatherapp.model.api.models.WeatherData
import com.example.sampleweatherapp.model.database.entities.WeatherDataDbModel
import com.example.sampleweatherapp.untils.getCityName
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class MainRepository(val api: ApiProvider) : BaseRepository<MainRepository.ServerResponse>() {

    private val gson = Gson()
    private val dbAccess = database.weatherDao()
    private val lang = when (Locale.getDefault().displayLanguage) {
        "русский" -> "ru"
        else -> "en"
    }

    @SuppressLint("CheckResult")
    fun reloadData(lat: String, lon: String) {
        Observable.zip(
            api.provideWeatherApi().getWeather(lat, lon, lang = lang),
            api.provideGeoCodingApi().detCity(lat, lon)
        ) { weatherData, geoLoc ->

            ServerResponse(geoLoc[0].getCityName(), weatherData)
        }
            .subscribeOn(Schedulers.io())
            .doOnNext {
                dbAccess.add(
                    WeatherDataDbModel(
                        name = it.cityName,
                        data = gson.toJson(it.weatherData)
                    )
                )
            }
            .onErrorResumeNext {
                Log.d("onErrorResumeNext", it.message.toString())
                Observable.just(
                    ServerResponse(
                        cityName = dbAccess.getData().name,
                        weatherData = gson.fromJson(
                            dbAccess.getData().data,
                            WeatherData::class.java
                        ),
                        error = it
                    )
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dataEmitter.onNext(it)
            },
                {
                    Log.d("MainRepository", it.toString())
                })
    }


    data class ServerResponse(
        val cityName: String?,
        val weatherData: WeatherData,
        val error: Throwable? = null,
    )
}