package com.example.sampleweatherapp.model.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sampleweatherapp.model.database.entities.GeoCodeDbModel
import com.example.sampleweatherapp.model.database.entities.WeatherDataDbModel

@Database(
    entities = [WeatherDataDbModel::class, GeoCodeDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun geoCodeDao(): GeoCodeDao

}