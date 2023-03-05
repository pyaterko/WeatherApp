package com.example.sampleweatherapp.model.api.models.geocod

data class GeoCodeItem(
    val country: String,
    val lat: Double,
    val local_names: LocalNames?,
    val lon: Double,
    val name: String,
    val state: String?,
    var isFavorite: Boolean = false,
)