package com.example.sampleweatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.sampleweatherapp.model.database.WeatherDatabase
import com.example.sampleweatherapp.untils.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideDataBase(
        @ApplicationContext context: Context,
    ) =
        Room.databaseBuilder(context, WeatherDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideWeatherDao(database: WeatherDatabase) = database.weatherDao()

    @Singleton
    @Provides
    fun provideCodeDao(database: WeatherDatabase) = database.geoCodeDao()

}
