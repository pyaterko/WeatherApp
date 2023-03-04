package com.example.sampleweatherapp.model.repositories

import android.annotation.SuppressLint
import android.util.Log
import com.example.sampleweatherapp.model.api.ApiProvider
import com.example.sampleweatherapp.model.api.models.WeatherData
import com.example.sampleweatherapp.model.database.entities.WeatherDataDbModel
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class  MainRepository(api: ApiProvider) : BaseRepository<MainRepository.ServerResponse>(api) {

    private val gson = Gson()
    private val dbAccess = database.dictionaryDao()

    @SuppressLint("CheckResult")
    fun reloadData(lat: String, lon: String) {
        Observable.zip(
            api.provideWeatherApi().detWeather(lat, lon),
            api.provideGeoCodingApi().detCity(lat, lon)
        ) { weatherData, geoLoc ->
            ServerResponse(geoLoc.name, weatherData)
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

    class ServerResponse(
        val cityName: String,
        val weatherData: WeatherData,
        val error: Throwable? = null,
    )
}