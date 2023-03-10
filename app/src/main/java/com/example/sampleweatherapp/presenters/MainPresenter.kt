package com.example.sampleweatherapp.presenters

import android.annotation.SuppressLint
import com.example.sampleweatherapp.model.repositories.MainRepository
import com.example.sampleweatherapp.views.MainView
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val response: MainRepository,
) : BasePresenter<MainView>() {


    @SuppressLint("CheckResult")
    override fun enable() {
        response.dataEmitter
            .doAfterNext { viewState.setLoading(false) }
            .subscribe { responce ->
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
