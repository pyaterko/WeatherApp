package com.example.sampleweatherapp.presenters

import android.annotation.SuppressLint
import android.util.Log
import com.example.sampleweatherapp.model.api.ApiProvider
import com.example.sampleweatherapp.model.repositories.MainRepository
import com.example.sampleweatherapp.views.MainView

class MainPresenter : BasePresenter<MainView>() {

    private val response = MainRepository(ApiProvider())

    @SuppressLint("CheckResult")
    override fun enable() {
        response.dataEmitter.subscribe { responce ->
            viewState.displayLocation(responce.cityName)
            viewState.displayCurrentData(responce.weatherData)
            viewState.displayHourlyData(responce.weatherData.hourly)
            viewState.displayWeeklyData(responce.weatherData.daily)
            responce.error?.let { viewState.displayError(it) }

        }

    }

    fun refresh(lat: String, lon: String) {
        viewState.setLoading(true)
        response.reloadData(lat, lon)
    }

}
