package com.example.sampleweatherapp.model.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sampleweatherapp.model.database.entities.WeatherDataDbModel

@Database(
    entities = [WeatherDataDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun dictionaryDao(): WeatherDao

    companion object {

        private const val DB_NAME = "weather.db"
        private var INSTANCE: WeatherDatabase? = null
        private val LOCK = Any()

        fun getInstance(application: Application): WeatherDatabase {
            INSTANCE?.let { trackDataBase ->
                return trackDataBase
            }
            synchronized(LOCK) {
                INSTANCE?.let { trackDataBase ->
                    return trackDataBase
                }
            }
            val dataBase = Room.databaseBuilder(
                application,
                WeatherDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = dataBase
            return dataBase
        }
    }

}