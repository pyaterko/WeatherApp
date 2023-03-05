package com.example.sampleweatherapp.model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.example.sampleweatherapp.model.api.models.geocod.GeoCodeItem
import com.example.sampleweatherapp.model.api.models.geocod.LocalNames

@Entity(tableName = "geocode_table", primaryKeys = ["lat", "lon"])
data class GeoCodeDbModel(
    @ColumnInfo(name = "name") val name: String,
    @Embedded val local_names: LocalNames?,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "state") val state: String?,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean = false,
) {
    fun fromItem() = GeoCodeItem(
        name = name,
        local_names = local_names,
        lat = lat,
        lon = lon,
        country = country,
        state = state,
        isFavorite = isFavorite
    )

    companion object {
        fun GeoCodeItem.toDbModelItem() =
            GeoCodeDbModel(
                name = this.name,
                local_names = this.local_names,
                lat = this.lat,
                lon = this.lon,
                country = this.country,
                state = this.state,
                isFavorite = this.isFavorite
            )
    }
}

