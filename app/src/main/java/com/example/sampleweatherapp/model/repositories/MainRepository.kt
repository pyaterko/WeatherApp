package com.example.sampleweatherapp.model.repositories

import android.annotation.SuppressLint
import android.util.Log
import com.example.sampleweatherapp.model.api.ApiProvider
import com.example.sampleweatherapp.model.entities.WeatherData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainRepository(api: ApiProvider) : BaseRepository<MainRepository.ServerResponse>(api) {
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
                //todo
            }
            //    .onErrorResumeNext {  }
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
    ) {

    }
}