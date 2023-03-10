package com.example.sampleweatherapp

import android.app.Application
import com.example.sampleweatherapp.model.database.WeatherDatabase
import com.example.sampleweatherapp.untils.APP_SETTINGS
import com.example.sampleweatherapp.untils.SettingsHolder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application()
{
    override fun onCreate() {
        super.onCreate()
       val pref = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE)
        SettingsHolder.onCreate(pref)
    }
}