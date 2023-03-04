package com.example.sampleweatherapp.model.database

import androidx.room.*
import com.example.sampleweatherapp.model.database.entities.WeatherDataDbModel

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_data_table WHERE id=1")
    fun getData():WeatherDataDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(weatherDataDbModel: WeatherDataDbModel)

    @Delete
    fun delete(weatherDataDbModel: WeatherDataDbModel)
}