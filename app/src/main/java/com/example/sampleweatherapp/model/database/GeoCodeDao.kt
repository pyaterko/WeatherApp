package com.example.sampleweatherapp.model.database

import androidx.room.*
import com.example.sampleweatherapp.model.database.entities.GeoCodeDbModel
import com.example.sampleweatherapp.model.database.entities.WeatherDataDbModel

@Dao
interface GeoCodeDao {
    @Query("SELECT * FROM geocode_table")
    fun getData():List<GeoCodeDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(geoCodeDbModel: GeoCodeDbModel)

    @Delete
    fun delete(geoCodeDbModel: GeoCodeDbModel)
}