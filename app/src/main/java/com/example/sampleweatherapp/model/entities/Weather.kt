package com.example.sampleweatherapp.model.entities

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)