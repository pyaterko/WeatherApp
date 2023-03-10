package com.example.sampleweatherapp.views

import com.example.sampleweatherapp.model.api.models.Daily
import com.example.sampleweatherapp.model.api.models.Hourly
import com.example.sampleweatherapp.model.api.models.WeatherData
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface MainView : MvpView {
    @AddToEndSingle
    fun displayLocation(value: String?)

    @AddToEndSingle
    fun displayCurrentData(data: WeatherData)

    @AddToEndSingle
    fun displayHourlyData(data:List<Hourly> )

    @AddToEndSingle
    fun displayWeeklyData(data: List<Daily>)

    @AddToEndSingle
    fun displayError(error: Throwable)

    @AddToEndSingle
    fun setLoading(flag: Boolean)

}
