package com.example.sampleweatherapp

import android.app.Application
import com.example.sampleweatherapp.model.database.WeatherDatabase

class App : Application() {
 companion object{
     lateinit var database: WeatherDatabase
 }

    override fun onCreate() {
        super.onCreate()
        database = WeatherDatabase.getInstance(this)
    }
}