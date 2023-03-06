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
        SettingsHolder.onCreate( getSharedPreferences(APP_SETTINGS, MODE_PRIVATE))
        database = WeatherDatabase.getInstance(this)
    }
}