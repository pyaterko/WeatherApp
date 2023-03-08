package com.example.sampleweatherapp

import android.app.Application
import com.example.sampleweatherapp.model.database.WeatherDatabase
import com.example.sampleweatherapp.untils.APP_SETTINGS
import com.example.sampleweatherapp.untils.SettingsHolder

class App : Application() {
    companion object {
        lateinit var database: WeatherDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = WeatherDatabase.getInstance(this)
        val pref = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE)
        SettingsHolder.onCreate(pref)
    }
}