package com.example.sampleweatherapp.model.api.models.geocod

data class GeoCodeItem(
    val name: String,
    val local_names: LocalNames?,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?,
    var isFavorite: Boolean = false,
)