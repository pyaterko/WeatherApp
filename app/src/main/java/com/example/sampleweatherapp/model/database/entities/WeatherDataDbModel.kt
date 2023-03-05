package com.example.sampleweatherapp.model.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

private const val ITEM_DB_ID = 1

@Entity(tableName = "weather_data_table")
data class WeatherDataDbModel(
    @PrimaryKey
    val id: Int = ITEM_DB_ID,
    val name: String?,
    val data: String,
)